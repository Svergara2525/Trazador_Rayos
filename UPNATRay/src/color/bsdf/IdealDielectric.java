package color.bsdf;
/**
 *
 * @author MAZ
 */
import org.jogamp.vecmath.Vector3f;
//
import color.BTDF;
import color.reflectance.ReflectanceRGB;
import color.RadianceRGB;
import static color.RadianceRGB.NORADIANCE;
//
public final class IdealDielectric extends BTDF {

  public IdealDielectric (final ReflectanceRGB dielectric,
                          final float eta_int, final float eta_ext) {
    super(dielectric, eta_int, eta_ext, null);
  }

  @Override
  protected RadianceRGB transmissiveFilter (final boolean toOutside,
                                            final RadianceRGB inputRadiance,         
                                            final Vector3f wi,                                         
                                            final Vector3f wo,                                      
                                            final Vector3f n,
                                            final float cosi) {
    // Solo refracta en la dirección de incidencia que la Ley de Snell
    // asocia a los vectores wo (dirección de salida) y n (normal).
    // Si la dirección de incidencia wi no está suficientemente
    // cerca de la dirección _wi, se devuelve radiancia nula.    
    final Vector3f _wi = super.getSpecularRefractionDirection(toOutside, wo, n);
    if (_wi.epsilonEquals(wi, 1E-5f)) { 
      final float coso = toOutside ? n.dot(wo) : -n.dot(wo);
//      final float rho = dielectric.rho(toOutside);
//      final float rho2 = rho * rho;
      return dielectric.transmissiveFilter(toOutside, cosi, coso, inputRadiance);//.scale(rho2);
    }
    return NORADIANCE;
    
  }

  @Override
  protected RadianceRGB reflectiveFilter (final boolean toOutside,
                                          final RadianceRGB inputRadiance,         
                                          final Vector3f wi,
                                          final Vector3f wo,                                     
                                          final Vector3f n,
                                          final float cosi) {
    
    // Solo refleja en la dirección de incidencia que la Ley de Snell
    // asocia a los vectores wo (dirección de salida) y n (normal).
    // Si la dirección de incidencia wi no está suficientemente
    // cerca de la dirección _wi, se devuelve radiancia nula.
    final Vector3f _wi = super.getSpecularReflectionDirection(wo, n);
    if (_wi.epsilonEquals(wi, 1E-5f)) {
      final float coso = toOutside ? n.dot(wo) : -n.dot(wo);
      return dielectric.reflectiveFilter(toOutside, cosi, coso, inputRadiance);
    }
    return NORADIANCE;

  }
  
}
