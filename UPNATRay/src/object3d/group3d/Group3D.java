package object3d.group3d;
/**
 * Colección de objetos que se manejan de forma conjunta
 * 
 * @author MAZ
 */
import java.util.Collection;
//
import static raytracer.Hit.NOHIT;
import raytracer.Hit;
import raytracer.Ray;
import object3d.Object3D;
import object3d.Intersectable;
//
public class Group3D implements Intersectable {
  
  private final Collection<Object3D> objects;
  
  public Group3D (final Collection<Object3D> objects) {
    this.objects = objects;
  }

  @Override
  public Hit intersect (final Ray ray, final float tmin, final float tmax) {
    
    Hit closestHit = NOHIT;

    for (final Object3D objeto: objects) {
      
      final Hit lastHit = objeto.intersect(ray, tmin, tmax);

      if (lastHit.isCloserThan(closestHit)) {
        lastHit.setModel3D(objeto);
        closestHit = lastHit;
      }

    }

    return closestHit;
    
  }

  @Override
  public boolean intersectAny (final Ray ray, final float tmin, final float tmax) {

    return objects.stream().anyMatch(x -> x.intersectAny(ray, tmin, tmax));
    
  }

}