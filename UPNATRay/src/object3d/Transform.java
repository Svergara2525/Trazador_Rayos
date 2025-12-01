package object3d;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.signum;
import org.jogamp.vecmath.Matrix4f;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Tuple3f;
import org.jogamp.vecmath.Vector3f;
//
import object3d.boundingvolume.BoundingVolume;
import raytracer.Hit;
import raytracer.Ray;
//
public class Transform {

  static public final Transform IDENTITY_TRANSFORM = new IdentityTransform();

  private final Matrix4f worldToModel; // Matriz de transformación inversa
  private final Matrix4f modelToWorld; // Matriz de transformación directa
  private final Matrix4f N;            // Matriz para ajuste de normales
  private final boolean scaled;        // Indica si incluye un cambio de escala.
  
  static public Matrix4f getAimingMatrix (final Vector3f i,
                                          final Vector3f j,
                                          final Vector3f k) {

    final Matrix4f A = new Matrix4f();
    A.setIdentity();
    
    // TODO
    A.setRow(0, i.x, i.y, i.z, 0);
    A.setRow(1, j.x, j.y, j.z, 0);
    A.setRow(2, k.x, k.y, k.z, 0);
    A.m33 = 1;

    return A;
    
  }  
  
  static public Matrix4f getRotationMatrix (final Vector3f axis,
                                            final float theta) {

    final Matrix4f R = new Matrix4f();
    R.setIdentity();
    
    // TODO, aperece en los apuntes, rotacion.arbitraria.ejercicio (solución punto 2)
    
    final float a = axis.x;
    final float b = axis.y;
    final float c = axis.z;
    final float t = (float) Math.cos(theta);
    final float s = (float) Math.sin(theta);
    
    R.setRow(0, a*a*(1 - t) + t, a*b*(1 - t) - c*s, a*c*(1 - t) + b*s, 0);
    R.setRow(1, a*b*(1 - t) + c*s, b*b*(1 - t) + t, b*c*(1 - t) - a*s, 0);
    R.setRow(2, a*c*(1 - t) - b*s, b*c*(1 - t) + a*s, c*c*(1 - t) + t, 0);
    return R;
    
  }
  
  static public Matrix4f getTranslationMatrix (final Vector3f d) {
    
    final Matrix4f T = new Matrix4f();
    T.setIdentity();
    
    // TODO
    T.m03 = d.x;
    T.m13 = d.y;
    T.m23 = d.z;
    return T;
    
  }
  
  static public Matrix4f getScaleMatrix (final Tuple3f s) {
    
    final Matrix4f S = new Matrix4f();
    S.setIdentity();
    
    //TODO
    S.m00 = s.x;
    S.m11 = s.y;
    S.m22 = s.z;
    return S; 
    
  }
  
  public Transform (final Matrix4f S,
                    final Matrix4f A,
                    final Matrix4f R,
                    final Matrix4f T,
                    final boolean scaled) {
      
    this.scaled = scaled;

    // TODO
    
    modelToWorld = new Matrix4f();
    modelToWorld.setIdentity();
    modelToWorld.mul(T);
    modelToWorld.mul(R);
    modelToWorld.mul(A);
    modelToWorld.mul(S);
    
    final Matrix4f invS = getScaleMatrix(new Vector3f(1 / S.m00, 1 / S.m11, 1 / S.m22));
    final Matrix4f invT = getScaleMatrix(new Vector3f(-T.m03, -T.m13, -T.m23));
    final Matrix4f invR = new Matrix4f(R);
    invR.transpose();
    final Matrix4f invA = new Matrix4f(A);
    invA.transpose();
    
    
    worldToModel = new Matrix4f();
    worldToModel.setIdentity();
    worldToModel.mul(invS);
    worldToModel.mul(invA);
    worldToModel.mul(invR);
    worldToModel.mul(invT);
    
    N = new Matrix4f(R);
    N.mul(invS);
  }
  
  public Transform (final Transform transform) {
    this(new Matrix4f(transform.modelToWorld),
         new Matrix4f(transform.worldToModel),
         new Matrix4f(transform.N),
         transform.scaled); 
  }
  
  public Transform (final Matrix4f modelToWorld,
                    final Matrix4f worldToModel,
                    final Matrix4f N,
                    final boolean scaled) {
    this.worldToModel = new Matrix4f(worldToModel);
    this.modelToWorld = new Matrix4f(modelToWorld);
    this.N = new Matrix4f(N);
    this.scaled = scaled;
  }  
    
  public void modelToWorld (final Point3f P) {
    modelToWorld.transform(P);
  }
  
  public void worldToModel (final Point3f P) {
    worldToModel.transform(P);
  }
  
  public void modelToWorld (final Vector3f w) {
    modelToWorld.transform(w);   
  }
  
  public void worldToModel (final Vector3f w) {
    worldToModel.transform(w);
  }
  
  public Ray worldToModel (final Ray ray) {
    final Point3f  R = new Point3f(ray.getStartingPoint());
    final Vector3f v = new Vector3f(ray.getDirection());
    worldToModel.transform(R);
    worldToModel.transform(v);
    return new Ray(R, v);
  }
  
  public Hit modelToWorld (final Hit hit, final Point3f R) {
    final Point3f P = hit.getPoint();
    modelToWorld.transform(P);
    N.transform(hit.getNormal());
    if (scaled) {
      final float a = P.distance(R);
      hit.setAlpha(a);
    }
    return hit;
  }
  
  public BoundingVolume modelToWorld (final BoundingVolume boundingVolume) {
    return boundingVolume.transformedBy(modelToWorld);
  }  
  
  public Object3D transform (final Object3D model) {
    return null;
  }
  
  public Transform compose (final Transform transform) {
    if (this != IDENTITY_TRANSFORM) {   
      worldToModel.mul(transform.worldToModel);
      final Matrix4f _modelToWorld = new Matrix4f(transform.modelToWorld);
      _modelToWorld.mul(modelToWorld);
      modelToWorld.set(_modelToWorld);
      final Matrix4f _N = new Matrix4f(transform.N);
      _N.mul(N);
      N.set(_N);  
      return this;
    } else {
      return (transform != IDENTITY_TRANSFORM) ? new Transform(transform) : this;
    }   
  }
  
  static private final class IdentityTransform extends Transform {
    
    static private final Matrix4f I = getIdentityMatrix();
    
    static private Matrix4f getIdentityMatrix () {
      final Matrix4f _I = new Matrix4f();
      _I.setIdentity();
      return _I;    
    }

    private IdentityTransform () {
      super(I, I, I, false);
    }
    
    @Override
    public void modelToWorld (final Point3f P) {}
    @Override
    public void worldToModel (final Point3f P) {}

    @Override
    public void modelToWorld (final Vector3f w) {}
    @Override
    public void worldToModel (final Vector3f w) {}
    
    @Override
    public Ray worldToModel (final Ray ray) {
      return ray;
    }
    
    @Override
    public Hit modelToWorld (final Hit hit, final Point3f R) {
      return hit;
    }    
  
  }
  
}

