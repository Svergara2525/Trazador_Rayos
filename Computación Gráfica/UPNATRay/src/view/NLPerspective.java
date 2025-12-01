package view;
/**
 *
 * @author MAZ
 */
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
//
import raytracer.RayGenerator;
import raytracer.Ray;
import static primitives.ExtendedOperators.sop;
//
public class NLPerspective extends Projection {
  
  private final float f;
  
  public NLPerspective (final float fov) {
    this(fov, 1.0f);
  }  

  public NLPerspective (final float fov, final float f) {
    super(2 * (float) Math.tan(Math.toRadians(0.25f * fov)), f);
    this.f = f;
  }

  @Override
  public RayGenerator getRayGenerator (final Camera c, final int W, final int H) {
    return new NLPerspectiveRayGenerator(c, W, H);
  }

  private final class NLPerspectiveRayGenerator extends RayGenerator {
    
    private final Point3f R;
    private final float h2;

    private NLPerspectiveRayGenerator (final Camera camera,
                                       final int W,
                                       final int H) {
      super(camera, W, H);
      this.R = camera.getPosition();
      this.h2 = 0.25f * h * h;
    }

    @Override
    public Ray getRay (final int m, final int n) {

      return null;

    }

  }

}