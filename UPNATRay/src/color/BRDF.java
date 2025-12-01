package color;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.min;
import org.jogamp.vecmath.Vector3f;
//
import static color.RadianceRGB.NORADIANCE;
import color.densityfunction.NormalDistributionFunction;
import color.reflectance.ConductiveReflectance;
import color.reflectance.LambertianReflectance;
import color.reflectance.ReflectanceRGB;
//
public abstract class BRDF extends BSDF {
  
  protected final ConductiveReflectance specular;
  protected final LambertianReflectance diffuse;
  
  protected BRDF (final ReflectanceRGB diffuse,
                  final ReflectanceRGB specular,
                  final NormalDistributionFunction ndf) {
    super(ndf);
    if (((specular == null) || !specular.isOperative()) &&
        ((diffuse == null) || !diffuse.isOperative())) 
      throw new IllegalArgumentException("Combinación de filtros inoperativa");
    if ((diffuse != null) && (specular != null) &&
        !ReflectanceRGB.combinationIsConservative(diffuse, specular))
      throw new IllegalArgumentException("Combinación de filtros no conservativa");    
    this.specular = ((specular != null) && specular.isOperative()) ?
            new ConductiveReflectance(specular) : null;
    this.diffuse = ((diffuse != null) && diffuse.isOperative()) ?
            new LambertianReflectance(diffuse) : null;
  }
  
  protected BRDF (final ReflectanceRGB diffuse) {
    this(diffuse, null, null);
  }
  
  protected BRDF (final ReflectanceRGB specular,
                  final NormalDistributionFunction ndf) {
    this(null, specular, ndf);
  }
  
  @Override
  public final RadianceRGB reflectiveFilter (final boolean toOutside,
                                             final RadianceRGB inputRadiance,
                                             final Vector3f wi,
                                             final Vector3f wo,
                                             final Vector3f n) {  

    if ((inputRadiance == NORADIANCE) || inputRadiance.isNegligible())
      return NORADIANCE;
    
    final Vector3f _n = toOutside ? n : new Vector3f(-n.x, -n.y, -n.z); 
    final float cosi = min(1.0f, _n.dot(wi));
    // La radiancia de salida es nula siempre que la radiancia incidente es nula
    // o llega por debajo del plano tangente (por debajo de la línea del horizonte,
    // desde la semiesfera de direcciones negativa), entonces la radiancia reflejada
    // es nula.
    if (Math.signum(cosi) > 0) {
      final RadianceRGB outputRadiance = new RadianceRGB();
      if (isDiffuse())
        outputRadiance.add(diffuseFilter(inputRadiance, wi, wo, _n, cosi));
      if (isSpecular())
        outputRadiance.add(reflectiveFilter(inputRadiance, wi, wo, _n, cosi));     
      return outputRadiance;
    } else
      return NORADIANCE;
  }
  
  @Override
  public RadianceRGB transmissiveFilter (final boolean toOutside,
                                         final RadianceRGB inputRadiance,         
                                         final Vector3f wi,                                     
                                         final Vector3f wo,                                      
                                         final Vector3f n) {
    return NORADIANCE;
  }  

  abstract protected RadianceRGB diffuseFilter (final RadianceRGB inputRadiance,
                                                final Vector3f wi,
                                                final Vector3f wo,
                                                final Vector3f n,
                                                final float cosi);

  abstract protected RadianceRGB reflectiveFilter (final RadianceRGB inputRadiance,
                                                   final Vector3f wi,
                                                   final Vector3f wo,
                                                   final Vector3f n,
                                                   final float cosi);
  
  private boolean isSpecular () {
    return (specular != null);
  }

  private boolean isDiffuse () {
    return (diffuse != null);
  }
  
  @Override
  public final boolean isTransmissive () {
    return false;
  }
  
  @Override
  public Vector3f getSpecularRefractionDirection (final boolean toOutside,
                                                  final Vector3f wo,
                                                  final Vector3f n) {
    throw new IllegalArgumentException("Operación sin sentido: el filtro no es transmisivo");
  }

}