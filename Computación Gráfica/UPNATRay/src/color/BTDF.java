package color;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.fma;
import static java.lang.Math.min;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;
import org.jogamp.vecmath.Vector3f;
//
import static color.RadianceRGB.NORADIANCE;
import color.densityfunction.NormalDistributionFunction;
import color.reflectance.DielectricReflectance;
import color.reflectance.ReflectanceRGB;
//
public abstract class BTDF extends BSDF {

  protected final DielectricReflectance dielectric;
  
  protected BTDF (final ReflectanceRGB dielectric,
                  final float eta_int, final float eta_ext,
                  final NormalDistributionFunction ndf) {
    super(ndf);
    this.dielectric = new DielectricReflectance(dielectric, eta_int, eta_ext);
  }
  
  @Override
  public final RadianceRGB reflectiveFilter (final boolean toOutside,
                                             final RadianceRGB inputRadiance,        
                                             final Vector3f wi,                                            
                                             final Vector3f wo,                                      
                                             final Vector3f n) {
    
    if ((inputRadiance == NORADIANCE) || inputRadiance.isNegligible())
      return NORADIANCE;
    // Si la radiancia incidente es nula o llega por debajo del plano tangente
    // (por debajo de la línea del horizonte, desde la semiesfera de direcciones
    // negativa), la radiancia de salida es nula.
    final float cosi = min(1.0f, toOutside ? n.dot(wi) : -n.dot(wi));
    if (Math.signum(cosi) <= 0)
      return NORADIANCE;
    
    // Cuando la luz se transmite de un medio más denso a un medio menos denso,
    // la identidad de Snell solo es válida para ángulos de incidencia por debajo
    // de un valor crítico. Por encima de ese ángulo crítico no hay transmisión,
    // solo hay reflexión interna.    
    final float rho = dielectric.rho(toOutside);
    if (signum(rho - 1) < 0) {
      final float critical = fma(rho, rho, -1);
      final float tir = fma(cosi, cosi, critical);
      if (signum(tir) <= 0) {
        // Ángulo theta_i es mayor que theta_c; solo hay reflexión interna total.
        // Eso implica que no es necesario computar el factor 1 - T.
        return dielectric.filter(cosi, inputRadiance);
      }
    }
    
    // Las pruebas anteriores aseguran:
    // * inputRadiance es un valor de radiancia significativo.
    // * wi indica una dirección dentro del hemisferio iluminante.
    // * No ocurre el fenómeno de reflexión interna total.
    // Una fracción de radiancia incidente se refleja y la otra fracción
    // se transmite; el método siguiente computa la radiancia reflejada.
    return reflectiveFilter(toOutside, inputRadiance, wi, wo, n, cosi);
    
  }
  
  @Override
  public final RadianceRGB transmissiveFilter (final boolean toOutside,
                                               final RadianceRGB inputRadiance,         
                                               final Vector3f wi,                                            
                                               final Vector3f wo,                                      
                                               final Vector3f n) {
    
    // Si la radiancia incidente es nula o despreciable, la radiancia devuelta
    // es nula.
    if ((inputRadiance == NORADIANCE) || inputRadiance.isNegligible())
      return NORADIANCE;
    
    // Si la radiancia incidente llega por encima del plano tangente (por encima
    // de la línea del horizonte, desde la semiesfera de direcciones positiva),
    // la radiancia devuelta es nula.
    final float cosi = min(1.0f, toOutside ? -n.dot(wi) : n.dot(wi));
    if (Math.signum(cosi) <= 0) 
       return NORADIANCE;
    
    // Cuando la luz se transmite de un medio más denso a un medio menos denso,
    // la identidad de Snell solo es válida para ángulos de incidencia por debajo
    // de un valor crítico. Por encima de ese ángulo crítico no hay transmisión,
    // solo hay reflexión interna.    
    final float rho = dielectric.rho(toOutside);
    if (signum(rho - 1) < 0) {
      final float critical = fma(rho, rho, -1);
      final float tir = fma(cosi, cosi, critical);
      if (signum(tir) <= 0) {
        // Ángulo theta_i es mayor que theta_c; solo hay reflexión interna total;
        // no hay radiancia transmitida.
        return NORADIANCE;
      }
    }

    // Las pruebas anteriores aseguran:
    // * inputRadiance es un valor de radiancia significativo.
    // * wi indica una dirección dentro del hemisferio iluminante.
    // * No ocurre el fenómeno de reflexión interna total.
    // Una fracción de radiancia incidente se refleja y la otra fracción
    // se transmite; el método siguiente computa la radiancia transmitida.    
    return transmissiveFilter(toOutside, inputRadiance, wi, wo, n, cosi);
    
  }
  
  abstract protected RadianceRGB reflectiveFilter (final boolean toOutside,
                                                   final RadianceRGB inputRadiance,
                                                   final Vector3f wi,
                                                   final Vector3f wo,
                                                   final Vector3f n,
                                                   final float cosi);
  
  abstract protected RadianceRGB transmissiveFilter (final boolean toOutside,
                                                    final RadianceRGB inputRadiance,
                                                    final Vector3f wi,
                                                    final Vector3f wo,
                                                    final Vector3f n,
                                                    final float cosi);  
  
  @Override
  public final boolean isTransmissive () {
    return true;
  }
  
  @Override
  public Vector3f getSpecularRefractionDirection (final boolean toOutside,
                                                  final Vector3f wo,
                                                  final Vector3f n) {
    
    // Convenio de operación: los métodos de intersección
    // devuelven una normal que apunta al exterior del modelo.

    final Vector3f wi = new Vector3f(0, 0, 0);

    // TODO
    
    return wi;

  }

}