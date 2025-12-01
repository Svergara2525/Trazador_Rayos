package object3d.model;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.abs;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
//
import static primitives.ExtendedOperators.sop;
import static raytracer.Hit.NOHIT;
import object3d.boundingvolume.AABB;
import raytracer.Hit;
import raytracer.Ray;
//
public final class Torus extends ProceduralGeometry3D {
  
  static private final Point3f O = new Point3f(0, 0, 0);

  private final float L;
  private final float r;
  private final float inv_r;

  public Torus (final float L, final float r) {

    if (signum(r) <= 0)
      throw new IllegalArgumentException("Valor de radio r ilegal: " + r);
    
    if (signum(r) < 0)
      throw new IllegalArgumentException("Valor de radio L ilegal: " + L);    

    this.L = L;
    this.r = r;
    this.inv_r = 1 / r;
    
    super.boundingBox = new AABB(-(L + r), +(L + r),
                                 -r, +r,
                                 -(L + r), +(L + r));    

  }
  
  @Override
  protected float SDF (final Point3f P) {

    // TODO
    
    return Float.POSITIVE_INFINITY;

  }  

  @Override
  public Hit intersect (final Ray ray, final float tmin, final float tmax) {
    
    if (boundingBox.intersect(ray, tmin, tmax)) {

      final float d = this.rayMarching(ray);
      final float a = abs(d);

      if ((signum(tmin - a) <= 0) && (signum(a - tmax) <= 0)) {
        
        // P es un punto en la superficie del elipsoide canónico
        final Point3f P = ray.pointAtParameter(a);
        
        // TODO: normal respecto al sistema canónico
        final float x = 0;
        final float y = 0;
        final float z = 0;
        final Vector3f normal = new Vector3f(x, y, z);
        normal.normalize();
        
        return new Hit(a, P, normal);

      }
    
    }

    return NOHIT;

  }
  
  @Override
  protected float distanceUpperBound (final Point3f P) {
    return O.distance(P) + L + r;
  }
  
}