package object3d.kdtree;
/**
 *
 * @author MAZ
 */
import org.jogamp.vecmath.Point3f;
import raytracer.Hit;
import raytracer.Ray;
//
import object3d.Volumetric;
//
abstract class Node {
  
  abstract Hit intersect (final Ray ray, final float tin, final float tout);
  abstract boolean intersectAny (final Ray ray, final float tin, final float tout);  
  
  abstract boolean isInside  (final Point3f P);
  abstract boolean isOutside (final Point3f P);
  
}