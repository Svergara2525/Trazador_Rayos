package object3d;
/**
 *
 * @author MAZ
 */
import raytracer.Hit;
import raytracer.Ray;
//
public interface Intersectable {
  
  Hit intersect (final Ray ray, final float tmin, final float tmax);

  default Hit intersect (final Ray ray) {
    return intersect(ray, 0, Float.POSITIVE_INFINITY);
  }

  boolean intersectAny (final Ray ray, final float tmin, final float tmax);  
  
  default boolean intersectAny (final Ray ray) {
    return intersectAny(ray, 0, Float.POSITIVE_INFINITY);
  }
  
}
