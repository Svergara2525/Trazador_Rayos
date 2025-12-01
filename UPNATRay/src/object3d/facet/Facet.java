package object3d.facet;
/**
 *
 * @author MAZ
 */
import java.util.Collection;
import object3d.Intersectable;
import org.jogamp.vecmath.Point3f;
//
import raytracer.Hit;
import raytracer.Ray;
//
public interface Facet extends Intersectable {

  @Override
  Hit intersect (final Ray ray, final float tmin, final float tmax);
  
  @Override
  boolean intersectAny (final Ray ray, final float tmin, final float tmax);

  public Collection<Vertex3D> getVertices ();
  
  public boolean isXanterior (final float x);
  public boolean isYanterior (final float y);
  public boolean isZanterior (final float z);
  public boolean isXposterior (final float x);
  public boolean isYposterior (final float y);
  public boolean isZposterior (final float z);
  
  public void setFlat ();

}
