package object3d.model;
/**
 *
 * @author MAZ
 */

import static java.lang.Math.abs;
import static java.lang.Math.signum;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
//
import static primitives.ExtendedOperators.opposite;
import static raytracer.Hit.NOHIT;
import raytracer.Hit;
import raytracer.Ray;
//
public final class Plane implements Model3D {
  
  private final Point3f Q;  
  private final Vector3f n;
  
  public Plane (final Point3f Q, final Vector3f n) {
    this.Q = new Point3f(Q);
    this.n = new Vector3f(n);
    this.n.normalize();   
  }
  
  public Plane (final Point3f A, final Point3f B, final Point3f C) {
    this.Q = new Point3f(A);
    final Vector3f AB = new Vector3f();
    AB.sub(B, A);
    final Vector3f AC = new Vector3f();
    AC.sub(C, A);
    this.n = new Vector3f();
    this.n.cross(AB, AC);
    this.n.normalize();
  }  

  @Override
  public Hit intersect (Ray ray, float tmin, float tmax) {
    
    // TODO

    return NOHIT;
    
  }

  @Override
  public boolean intersectAny (final Ray ray, final float tmin, final float tmax) {

    // TODO

    return false;
    
  }

}