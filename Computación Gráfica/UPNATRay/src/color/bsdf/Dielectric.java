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
public final class Dielectric extends BTDF {

  public Dielectric (final ReflectanceRGB dielectric,
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

    final float coso = toOutside ? n.dot(wo) : -n.dot(wo);
    return dielectric.transmissiveFilter(toOutside, cosi, coso, inputRadiance);
    
  }

  @Override
  protected RadianceRGB reflectiveFilter (final boolean toOutside,
                                          final RadianceRGB inputRadiance,         
                                          final Vector3f wi,
                                          final Vector3f wo,                                     
                                          final Vector3f n,
                                          final float cosi) {
    
    final float coso = toOutside ? n.dot(wo) : -n.dot(wo);
    return dielectric.reflectiveFilter(toOutside, cosi, coso, inputRadiance);

  }
  
}
