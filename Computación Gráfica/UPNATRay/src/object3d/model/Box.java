package object3d.model;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.abs;
import static java.lang.Math.fma;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.signum;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
//
import static raytracer.Hit.NOHIT;
import object3d.boundingvolume.AABB;
import raytracer.Hit;
import raytracer.Ray;
//
public final class Box implements Model3D {

  private final float halfW;
  private final float halfH;
  private final float halfD;
  
  private final AABB boundingBox;

  public Box (final float w, final float h, final float d) {
    this.halfW = 0.5f * w;
    this.halfH = 0.5f * h;
    this.halfD = 0.5f * d;
    this.boundingBox = new AABB(-halfW, +halfW, -halfH, +halfH, -halfD, +halfD);
  }
  
  private boolean isOutside (final Point3f P) {
    // TODO
    return false;
  }

  private boolean isInside (final Point3f P) {
    // TODO
    return false;
  }  
  
  @Override
  public Hit intersect (final Ray ray, final float tmin, final float tmax) {
    final Point3f R = ray.getStartingPoint();
    return isOutside(R) ? intersect(true, ray, tmin, tmax) :
           isInside(R)  ? intersect(false, ray, tmin, tmax) :
           NOHIT;
  
  }  
  
  private Hit intersect (final boolean fromOutside, final Ray ray,
                         final float tmin, final float tmax) {
    
    final Point3f  R = ray.getStartingPoint();
    final Vector3f v = ray.getDirection();
       
    // TODO
    
    return NOHIT;
    
  }   
  
  @Override
  public boolean intersectAny (final Ray ray, final float tmin, final float tmax) {
    
    // TODO 
    
    return false;
    
  }
  
  
  @Override
  public AABB getBoundingBox () {
    return boundingBox;
  }  

}