package scene;
/**
 * Colección de objetos que forman la escena
 * 
 * @author MAZ
 */
import java.util.LinkedHashMap;
import java.util.Map;
//
import static raytracer.Hit.NOHIT;
import raytracer.Hit;
import raytracer.Ray;
import object3d.Intersectable;
import object3d.Object3D;
import object3d.group3d.Group3D;
//
public class Scene implements Intersectable {
  
  private final Map<Object3D, VModel> vmap;

  public Scene () {
    this.vmap = new LinkedHashMap<>();
  }

  public Scene addObject (final Object3D model3d, final VModel vmodel) {
    vmap.put(model3d, vmodel);  
    return this;
  }

  @Override
  public Hit intersect (final Ray ray, final float tmin, final float tmax) {

    Hit closestHit = NOHIT;

    for (final Object3D object: vmap.keySet()) {

      final Hit lastHit = object.intersect(ray, tmin, tmax);

      if (lastHit.isCloserThan(closestHit)) {
        lastHit.setModel3D(object);
        lastHit.setVisualModel(vmap.get(object));
        closestHit = lastHit;
      }

    }

    return closestHit;

  }
  
  public Map<Object3D, VModel> getVMap () {
    return vmap;
  }
  
  public Group3D getObjects () {
    return new Group3D(vmap.keySet());
  }  

  @Override
  public boolean intersectAny (final Ray ray, float tmin, float tmax) {
    
    for (final Object3D objeto: vmap.keySet()) {
      
      if (objeto.intersectAny(ray, tmin, tmax))
        return true;

    }
    
    return false;
    
  }

}