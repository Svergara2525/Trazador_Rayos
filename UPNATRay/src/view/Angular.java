package view;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.signum;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import org.jogamp.vecmath.Point3f;
//
import raytracer.RayGenerator;
import raytracer.Ray;
//
public class Angular extends Projection {

  public Angular (final float omega) {
    super((float) (2.0 * sin(toRadians(omega * 0.5f))), 1);
  }
  
  @Override
  public RayGenerator getRayGenerator (final Camera c, final int W, final int H) {
    return new AngularRayGenerator(c, W, H);
  }

  static private final class AngularRayGenerator extends RayGenerator {

    private final float r2;  // Cuadrado del radio de la imagen 
    private final float cos; // Coseno de omega / 2
    private final Point3f R;

    private AngularRayGenerator (final Camera c, final int W, final int H) {
      super(c, W, H);
      this.r2 = h * h * 0.25f;
      this.cos = (float) sqrt(1.0 - r2);
      this.R = new Point3f(0.0f, 0.0f, cos);      
      camera.toSceneCoordenates(R);
    }

    @Override
    public Ray getRay (final int m, final int n) {
        return null;

    }

  }

}