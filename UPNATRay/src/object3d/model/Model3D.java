package object3d.model;
/**
 *
 * @author MAZ
 */
import static object3d.boundingvolume.AABB.NOBOUNDINGBOX;
import object3d.Intersectable;
import object3d.boundingvolume.AABB;
//
public interface Model3D extends Intersectable {
   
  default AABB getBoundingBox () {
    return NOBOUNDINGBOX;
  }
  
}