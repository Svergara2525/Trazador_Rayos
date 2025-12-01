package light;
/**
 * Representa una fuente de luz omnidireccional
 */
import static color.RadianceRGB.NORADIANCE;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
//
import color.RadianceRGB;
import object3d.group3d.Group3D;
import raytracer.Hit;
import raytracer.Ray;
//
public class Omnidirectional extends Light {
  
  /**
   * Intensidad radiante en cada dirección
   */
  private final RadianceRGB radiantIntensity;

  /**
   * Crea una nueva fuente de luz puntual.
   *
   * @param position Ubicación de la fuente de luz
   * @param spectrum Espectro de radiación en canales RGB
   * @param power Potencia (intensidad) de emisión
   */
  public Omnidirectional (
          final Point3f position,
          final SpectrumRGB spectrum,
          final float power) {
    super(position, spectrum, power);
    final float r = (float) (power / (4 * Math.PI));
    radiantIntensity = spectrum.distribute(r);
  }
  
  @Override
  public RadianceRGB getRadianceAt (final Hit hit, final Group3D scene) {

    // TODO

    return NORADIANCE;
    
  }

  @Override
  public String toString () {
    return "OmnidirectionalLight -> S = " + this.S;
  }
  
  @Override
  public Vector3f getIncidenceDirection (final Point3f P) {
    final Vector3f I = new Vector3f();
    I.sub(S, P); // S - P
    I.normalize();
    return I;
  }

}