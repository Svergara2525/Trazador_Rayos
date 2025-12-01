package light;
/**
 * Representa una fuente de luz direccional de sección circular.
 *
 * @author MAZ
 */
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
//
import color.RadianceRGB;
import object3d.group3d.Group3D;
import raytracer.Hit;
import raytracer.Ray;
//
public class Directional extends Light {
  
  /**
   * Vector de orientación de la fuente
   */
  private final Vector3f direction;  

  /**
   * Exponente de atenuación
   */
  private final float attenuationExponent;

  /**
   * Radio (al cuadrado) de la sección circular
   */
  private final float squareRadius;
  
  /**
   * Radiosidad en cada punto de superficie luminosa
   */
  private final RadianceRGB puntualRadiosity;  

  /**
   * Constructor.
   *
   * @param position Ubicación del centro de la fuente
   * @param lookAt Punto de referencia hacia donde apunta la fuente
   * @param spectrum Espectro de radiación en canales RGB
   * @param power Potencia (intensidad) de emisión
   * @param radius Radio de la sección circular
   * @param attenuationExponent Exponente de atenuación para crear zona de penumbra
   */
    public Directional (
          final Point3f position,
          final Point3f lookAt,
          final float radius,          
          final SpectrumRGB spectrum,          
          final float power,
          final float attenuationExponent) {
    super(position, spectrum, power);
     // La dirección se almacena como se necesita para:
     // * generar el rayo de sombra,
     // * responder a la pregunta de dirección de incidencia.
    this.direction = new Vector3f();
    this.direction.sub(position, lookAt);
    this.direction.normalize();         
    this.squareRadius = radius * radius;
    final float r = (float) (power / (Math.PI * squareRadius));
    this.puntualRadiosity = spectrum.distribute(r);
    this.attenuationExponent = attenuationExponent;
  }  
 
  public Directional (
          final Point3f position,
          final Point3f lookAt,
          final float radius,          
          final SpectrumRGB spectrum,          
          final float power) {
    this(position, lookAt, radius, spectrum, power, 0.0f);
  }
  
  @Override
  public RadianceRGB getRadianceAt (final Hit hit, final Group3D scene) {
    
    // TODO

    return RadianceRGB.NORADIANCE;
  }
  
  @Override
  public Vector3f getIncidenceDirection (final Point3f P) { return direction; }  
  
}