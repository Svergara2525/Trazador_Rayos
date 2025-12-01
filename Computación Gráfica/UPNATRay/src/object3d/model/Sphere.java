package object3d.model;
//
import static java.lang.Math.fma;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
//
import static object3d.boundingvolume.AABB.NOBOUNDINGBOX;
import static raytracer.Hit.NOHIT;
import object3d.boundingvolume.AABB;
import raytracer.Hit;
import raytracer.Ray;
//
public class Sphere implements Model3D {
  
  static private final Point3f O = new Point3f(0, 0, 0);

  private final float r2;
  private final float inv_r;
 
  public Sphere (final float r) {
    this.r2 = r * r;
    this.inv_r = (float) (1.0 / r);  
  }

  @Override
  public Hit intersect (final Ray ray,
                        final float tmin, final float tmax) {
    final float ssd = Math.signum(signedSquaredDistance(ray.getStartingPoint()));
    return (signum(ssd) > 0) ? intersectFromOutside(ray, tmin, tmax) :
           (signum(ssd) < 0) ? intersectFromInside(ray, tmin, tmax) :
           NOHIT;
  }

  private Hit intersectFromOutside (final Ray ray, final float tmin, final float tmax) {
      final Point3f R = ray.getStartingPoint();
      final Vector3f RC = new Vector3f();
      RC.sub(O, R);
      final float c = RC.dot(RC) - r2;
      Vector3f v = ray.getDirection();
      final float b = RC.dot(v);
      if (signum(b) > 0){
          final float discr = b * b - c;
          if (signum(discr) > 0) {
              final float d = (float) Math.sqrt(discr);
              final float ap = b + d;
              final float am = c/ap;
              //
              final Point3f P = ray.pointAtParameter(am);
              final Vector3f n = new Vector3f(P);
              n.sub(O);
              n.scale(inv_r);
              System.out.println(am);
              return new Hit(am, P, n);
          }
      }
      return NOHIT;

  }

  private Hit intersectFromInside (final Ray ray, final float tmin, final float tmax) {
      final Point3f R = ray.getStartingPoint();
      final Vector3f RC = new Vector3f();
      Vector3f v = ray.getDirection();
      final float b = RC.dot(v);
      final float c = RC.dot(RC) - r2;
      final float discr = b * b - c;
      final float d = (float) Math.sqrt(discr);
      final float a = (signum(b) > 0) ? b + d : c / (b - d);
      final Point3f P = ray.pointAtParameter(a);
      final Vector3f n = new Vector3f();
      n.sub(P, O);
      n.scale(inv_r);
      return new Hit(a, P, n);    
  } 
  
  private float signedSquaredDistance (final Point3f P) {
    final float x = P.x; // - O.x;
    final float y = P.y; // - O.y;
    final float z = P.z; // - O.z;
    return fma(x, x, fma(y, y, fma(z, z, -r2)));
  }
  
  @Override
  public AABB getBoundingBox () {
    return NOBOUNDINGBOX; //new AABB(+r, -r, +r, -r, +r, -r);
  }

  @Override
  public boolean intersectAny (final Ray ray, final float tmin, final float tmax) {

    // TODO
    
    return false;
    
  }

}