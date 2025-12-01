package color.engine;
/**
 *
 * @author MAZ
 */
import color.RadianceRGB;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.signum;
import java.awt.Color;
import java.util.Collection;
import java.util.Map;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
import org.jogamp.vecmath.TexCoord2f;
//
import static color.RadianceRGB.NORADIANCE;
import color.BSDF;
import scene.VModel;
import light.Light;
import object3d.Object3D;
import object3d.group3d.Group3D;
import raytracer.Hit;
import raytracer.Ray;
import scene.Scene;
//
public final class ColorEngine {

  private final boolean illumination;
  private final boolean falseLighting;
  private final int maxRecursionDepth;

  //
  private Map<Object3D, VModel> vmap;
  private Group3D sceneObjects;
  private Collection<Light> lights;

  private ColorEngine (final boolean illumination,
                       final boolean falseLighting,
                       final int maxRecursionDepth) {
    if (illumination && (maxRecursionDepth < 0))
      throw new IllegalArgumentException("Nivel máximo de recursión no puede ser negativo");
    this.illumination = illumination;
    this.falseLighting = falseLighting;
    this.maxRecursionDepth = maxRecursionDepth;
  }

  public ColorEngine (final int maxRecursionDepth) {
    this(true, false, maxRecursionDepth);
  }

  public ColorEngine (final boolean falseLighting) {
    this(false, falseLighting, 0);
  }

  public Color getColor (final Hit hit) {
    final VModel vmodel = hit.getVisualModel();
    final TexCoord2f uv = hit.getTextureCoordenates();
    if (uv != null) {
      return vmodel.getColor(uv);
    } else
      return vmodel.getColor();
  }
  
  public Color getColor (final Hit hit, final Vector3f v) {

    // Calcula el ángulo de vista respecto a la dirección del vector director del rayo.
    final Vector3f n = hit.getNormal();

    final float brightnessFactor;
    if (falseLighting) {
        final float x = abs(n.dot(v));
        final float nv = x;//(2 - x) * x;
        brightnessFactor = max(min(nv, 1.0f), 0.0f);
    } else
      brightnessFactor = 1.0f;

    // Obtiene el color base del objeto
    final Color color = getColor(hit);
    // Muestra el color más brillante cuanto menor sea el ángulo de vista.
    final float[] colorComponents = new float[3];
    color.getColorComponents(colorComponents);
    return new Color(brightnessFactor * colorComponents[0],
                     brightnessFactor * colorComponents[1],
                     brightnessFactor * colorComponents[2]);

  }

  /**
   *
   * @param viewVector
   * @param scene
   * @param lights
   * @param hit informacion del punto de interseccion
   * @param ray rayo de trazado inicial
   * @return
   */
  public Color getColor (final Vector3f viewVector,
                         final Scene scene,
                         final Collection<Light> lights,
                         final Hit hit,
                         final Ray ray) {

    final Vector3f v = ray.getDirection();

    if (!illumination)
      // Obtiene el color del objeto; calcula un falso sombreado en función
      // de la dirección de incidencia y de la normal en el punto de intersección.
      return getColor(hit, v);
    else {
      // Computación del valor de color para el pixel a partir
      // de la radiancia que recibe el punto R de partida del rayo
      // desde la dirección v; se aplica el convenio de representar
      // la dirección de incidencia en sentido opuesto al que llevan
      // los fotones.
      this.vmap = scene.getVMap();
      this.sceneObjects = scene.getObjects();
      this.lights = lights;
      // La radiancia incidente sobre R desde la dirección v corresponde a radiancia
      // saliente desde el punto de intersección P en dirección -v. Esa radiancia
      // saliente depende de la radiancia recibida en el punto P y de las propiedades
      // del material asignado a la superficie en que se encuentra P.
      // - Se lanza el proceso recursivo para computar el espectro de radiancia
      //   saliente desde el hit en dirección -v.
      // - La opuesta a la dirección v de incidencia se emplea como dirección
      //   de radiancia saliente desde el hit.
      final RadianceRGB incidentRadiance = getOutgoingRadiance(v, hit, 0);
      // El vector de vista es ortogonal al plano de proyección; se aplica factor
      // de Lambert para obtener radiancia incidente medida sobre el plano
      // de incidencia (se recibe medida ortogonalmente a la dirección de propagación).
      final RadianceRGB effectiveRadiance = incidentRadiance.scale(viewVector.dot(v));
      // - A partir del espectro de radiancia incidente computado se obtiene
      //   la representación CIE-XYZ que corresponde a la sensación color que
      //   ese espectro produce en la retina humana.
      // - Finalmente, esa representación de color se traduce a coordenadas
      //   en el espacio de dispositivo sRGB D65.
      return effectiveRadiance.getColor();
    }

  }

  /**
   *
   * Devuelve el valor de radiancia saliente en dirección -wi desde el hit.
   *
   * @param hit informacion  (punto, normal, material) asociada al punto de intersección
   * @param wi opuesta a dirección de la radiancia saliente desde el hit
   * @param depth nivel de recursion
   * @return radiancia saliente en dirección -wi desde el hit
   */
  private RadianceRGB getOutgoingRadiance (final Vector3f wi,
                                           final Hit hit,
                                           final int depth) {

    if (depth <= maxRecursionDepth) {

      // El vector wi indica la dirección de radiancia incidente (conforme
      // al convenio de presentar sentido contrario al que llevan los fotones
      // incidentes) desde el punto de vista del receptor.
      //
      // Considerando un esquema de reflexión/transmisión basado en el punto
      // contenido en el hit, la dirección de salida wo de ese esquema es
      // la dirección opuesta de wi.
      final Vector3f wo = new Vector3f(wi);
      wo.negate();

      // Se considera el punto de partida del rayo con el que se ha obtenido el hit:
      // - Si ese punto está fuera del objeto tridimensional, eso indica que
      //   la radiancia saliente a computar se dirige al exterior del objeto
      //   tridimensional que corresponde al hit.
      // - Si ese punto está dentro del objeto tridimensional, eso indica que
      //   la radiancia saliente a computar se dirige al interior del objeto
      //   tridimensional que corresponde al hit.
      //
      // Por convenio, los algoritmos de intersección deben devolver una normal
      // hacia el exterior del objeto.
      //
      // Ese convenio permite determinar si la radiancia a computar va hacia
      // el exterior o hacia el interior del objeto: el coseno del ángulo que
      // presentan la dirección de salida y la dirección normal es positivo
      // si y solo si la radiancia de salida va hacia el exterior del objeto.
      final Vector3f n = hit.getNormal();
      final float coso = n.dot(wo);
      return (signum(coso) > 0) ? radianceFrom(true, hit, wo, depth) :
             (signum(coso) < 0) ? radianceFrom(false, hit, wo, depth) :
             NORADIANCE;

    } else
      return NORADIANCE;

  }
  
  private RadianceRGB radianceFrom (final boolean toOutside,
                                    final Hit hit,
                                    final Vector3f wo,
                                    final int depth) {
    
    // toOutside = true  implica computar la radiancia saliente hacia el medio exterior.
    // toOutside = false implica computar la radiancia saliente hacia el medio interior.

    // Se calcula y acumula radiancia recibida directamente desde fuentes luminosas.
    // Se calcula y acumula radiancia reflejada indirectamente desde otras superficies.
    // Si el medio es dieléctrico, se calcula y acumula radiancia incidente desde el otro medio.
    final RadianceRGB outgoingRadiance = new RadianceRGB();

    // Función de reflectancia/transmitancia asociada al punto de intersección.
    final Object3D object = hit.getModel3D();
    final BSDF bsdf = vmap.get(object).getBSDF();

    // Radiancia de fuentes sobre hit que es reflejada/transmitida en dirección wo.
    outgoingRadiance.add(getRadianceFromLights(hit, wo, toOutside));

    // Punto y normal; junto con wo son los elementos fijos
    // a partir de los que componer el esquema de la ley de Snell.
    final Point3f P = hit.getPoint();
    final Vector3f n = hit.getNormal();

    // Cómputo de radiancia incidente sobre P reflejada desde otras superficies
    // Todo material tiene capacidad reflexiva, así que este es un caso fijo.
    {

      // TODO

    }

    if (bsdf.isTransmissive()) {

      // TODO

    }

    return outgoingRadiance;

  }  

  private RadianceRGB getRadianceFromLights (final Hit hit,
                                             final Vector3f wo,
                                             final boolean toOutside) {

    // Radiancia de fuentes sobre hit que es reflejada/transmitida en dirección wo.
    // Argumento toOutside indica si la radiancia saliente va hacia
    // el medio exterior (true) o hacia medio el interior (false) del objeto.

    final Object3D object = hit.getModel3D();
    final BSDF bsdf = vmap.get(object).getBSDF();

    final Point3f P = hit.getPoint();
    final Vector3f n = hit.getNormal();
    final float won = signum(wo.dot(n));

    final RadianceRGB outgoingRadiance = new RadianceRGB();

    lights.forEach((var light) -> {

      final Vector3f wi = light.getIncidenceDirection(P);
      final float win = signum(wi.dot(n));

      if (signum(win - won) == 0) { // Reflexión: ambas direcciones en el mismo medio.

        final RadianceRGB inputRadiance = light.getRadianceAt(hit, sceneObjects);
        if (inputRadiance != NORADIANCE) {

          final RadianceRGB reflectedRadiance
            = bsdf.reflectiveFilter(toOutside, inputRadiance, wi, wo, n);
          outgoingRadiance.add(reflectedRadiance);

        }

      } else { // Transmisión: direcciones en diferentes medios.

        if (bsdf.isTransmissive()) {
          final RadianceRGB inputRadiance = light.getRadianceAt(hit, sceneObjects);
          if (inputRadiance != NORADIANCE) {
            
            final RadianceRGB transmittedRadiance
              = bsdf.transmissiveFilter(toOutside, inputRadiance, wi, wo, n);
            outgoingRadiance.add(transmittedRadiance);
            
          }
        }

      }

    });

    return outgoingRadiance;

  }

}