package color.bsdf;
/**
 *
 * @author MAZ
 *
 **/
import static java.lang.Math.fma;
import static java.lang.Math.max;
import org.jogamp.vecmath.Vector3f;
//
import color.BRDF;
import color.reflectance.ReflectanceRGB;
import color.RadianceRGB;
//
public final class OrenNayar extends BRDF {

  private final float A;
  private final float B;

  public OrenNayar (final ReflectanceRGB diffuse,
                    final float _sigma) {
    super(diffuse);
    if ((Math.signum(_sigma) < 0) || (Math.signum(_sigma - 90.0f) > 0))
      throw new IllegalArgumentException("Valor sigma fuera de rango [0,90]");
    final float sigma = (float) Math.toRadians(_sigma);
    final float sigma2 = sigma * sigma;
    this.A = (1 - 0.50f * sigma2 / (sigma2 + (1.0f / 3)));
    this.B = (0.45f * sigma2 / (sigma2 + 0.09f));
  }

  @Override
  public RadianceRGB reflectiveFilter (final RadianceRGB inputRadiance,
                                       final Vector3f in,
                                       final Vector3f out,
                                       final Vector3f n,
                                       final float cosi) {
    return RadianceRGB.NORADIANCE;
  }
  
  @Override
  protected RadianceRGB diffuseFilter (final RadianceRGB inputRadiance,
                                       final Vector3f wi,
                                       final Vector3f wo,
                                       final Vector3f n,
                                       final float cosi) {

    final float uv = wi.dot(wo);
    final float nv = n.dot(wo);
    final float nu = cosi;

    final float cosPhi = -fma(nv, nu, -uv); // - nv * nu;

    float w = A;
    final float cosBeta = (float) max(nu, nv);
    final float cosPhicosBeta = cosPhi / cosBeta;
    w += B * ((Math.signum(cosPhicosBeta) > 0) ? cosPhicosBeta : 0);

    return diffuse.filter(cosi, inputRadiance).scale(w);

  }

}