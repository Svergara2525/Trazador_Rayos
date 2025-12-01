package object3d;
/**
 *
 * @author MAZ
 */
import static raytracer.Hit.NOHIT;
import object3d.boundingvolume.AABB;
import raytracer.Hit;
import raytracer.Ray;
import object3d.model.Model3D;
//
public class Object3D implements Intersectable {

  private final Model3D model;
  private final AABB boundingBox;
  private final Transform transform;
  
  public Object3D (final Model3D shape,
                  final Transform transform) {
    this.model = shape;
    this.boundingBox = (AABB) transform.modelToWorld(shape.getBoundingBox());
    this.transform = new Transform(transform);
  }
  
  @Override
  public Hit intersect (final Ray ray, final float tmin, final float tmax) {

    if (boundingBox.intersect(ray, tmin, tmax)) {
      final Ray transformedRay = transform.worldToModel(ray);
      final Hit hit = model.intersect(transformedRay, tmin, tmax);
      if (hit.hits()) {        
        return transform.modelToWorld(hit, ray.getStartingPoint());
      }
    }
    
    return NOHIT;
    
  }

  @Override
  public boolean intersectAny (final Ray ray, final float tmin, final float tmax) {
    
    if (boundingBox.intersect(ray, tmin, tmax)) {
      final Ray transformedRay = transform.worldToModel(ray);
      // Es necesario ajustar los valores tmin y tmax
      // cuando la transformación incluya un cambio de escala.
      return model.intersectAny(transformedRay, tmin, tmax);
    }
    return false;
    
  }
  
}
