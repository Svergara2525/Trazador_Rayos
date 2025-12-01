package color;
/**
 *
 * @author MAZ
 */
import org.jogamp.vecmath.Vector3f;
//
import color.densityfunction.NormalDistributionFunction;
//
public abstract class BSDF {
  
  public static final float AIR_RI = 1.0002926f;   
  
  protected final NormalDistributionFunction ndf;
  
  protected BSDF (final NormalDistributionFunction ndf) {
    this.ndf = ndf;
  }

  // Método para calcular radiancia reflejada en materiales dieléctricos y conductivos.
  // El argumento toOutside indica si la reflexión ocurre en el exterior
  // del material (true), o si ocurre en el interior (false).
  public abstract RadianceRGB reflectiveFilter (final boolean toOutside,
                                                final RadianceRGB inputRadiance,
                                                final Vector3f wi,                                          
                                                final Vector3f wo,                                      
                                                final Vector3f n);
  
  // Método para calcular radiancia transmitida en materiales dieléctricos
  // El argumento toOutside indica si la radiancia se refracta al medio exterior
  // (true), o hacia el medio interior (false).
  public abstract RadianceRGB transmissiveFilter (final boolean toOutside,
                                                  final RadianceRGB inputRadiance,         
                                                  final Vector3f wi,                                     
                                                  final Vector3f wo,                                      
                                                  final Vector3f m);  
  
  public final Vector3f getSpecularReflectionDirection (final Vector3f wo,
                                                        final Vector3f n) {
    final Vector3f wi = new Vector3f();
    wi.scaleAdd(-2 * n.dot(wo), n, wo);
    wi.negate();
    return wi;
    
  }
  
  public abstract Vector3f getSpecularRefractionDirection (final boolean toOutside,
                                                           final Vector3f wo,
                                                           final Vector3f n);
  
  public abstract boolean isTransmissive ();  
  
}