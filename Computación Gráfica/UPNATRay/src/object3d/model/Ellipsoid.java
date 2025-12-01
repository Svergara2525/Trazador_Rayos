package object3d.model;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.abs;
import static java.lang.Math.fma;
import static java.lang.Math.max;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
//
import static primitives.ExtendedOperators.sop;
import object3d.boundingvolume.AABB;
import raytracer.Hit;
import raytracer.Ray;
//
public final class Ellipsoid extends ProceduralGeometry3D {
  
  static private final Point3f O = new Point3f(0, 0, 0);
  
  private final float a2;
  private final float b2;
  private final float c2;
  private final float ABC; 
  
  public Ellipsoid (final float a, final float b, final float c) {
    
    this.a2 = 1.0f / (a * a);
    this.b2 = 1.0f / (b * b);
    this.c2 = 1.0f / (c * c);
    this.ABC = max(a, max(b, c));
    super.boundingBox = new AABB(-a, +a, -b, +b, -c, +c);
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

    return Hit.NOHIT;
    
  }
  
  @Override
  protected float distanceUpperBound (final Point3f P) {
    return O.distance(P) + ABC;
  }
  
}