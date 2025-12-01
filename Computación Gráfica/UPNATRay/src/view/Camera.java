package view;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.fma;
import static primitives.ExtendedOperators.dop;
import org.jogamp.vecmath.Matrix4f;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
//
import raytracer.RayGenerator;
//
public class Camera {

  // ATRIBUTOS
  private final Point3f V;
  private final Vector3f view; // Vector LookAt
  private final Matrix4f camera2scene;
  private Projection optics;

  // CONSTRUCTOR
  public Camera (final Point3f V, final Point3f C, final Vector3f _up) {

    this.optics = null;
    this.V = new Point3f(V);

    final Vector3f up = new Vector3f(_up);
    up.normalize();
    view = new Vector3f();
    view.sub(C, V); // C - V
    view.normalize();

    final float s = up.dot(view); // Se guarda con signo cambiado.
    final float t = (float) (1.0 / Math.sqrt(-fma(s, s, -1)));

    camera2scene = new Matrix4f();
    
    //TODO: coeficientes de la matriz de vista
    
    final Vector3f w = view;
    
    final Vector3f v = new Vector3f();
    v.x = t * (up.x - s * w.x);
    v.y = t * (up.y - s * w.y);
    v.z = t * (up.z - s * w.z);
    
    final Vector3f u = new Vector3f();
    u.cross(up, w);
    
    camera2scene.m00 = -u.x * t;
    camera2scene.m10 = -u.y * t;
    camera2scene.m20 = -u.z * t;
    camera2scene.m30 = 0.0f;
    
    camera2scene.m01 = v.x;
    camera2scene.m11 = v.y;
    camera2scene.m21 = v.z;
    camera2scene.m31 = 0.0f;
    
    camera2scene.m02 = -w.x;
    camera2scene.m12 = -w.y;
    camera2scene.m22 = -w.z;
    camera2scene.m32 = 0.0f;
    
    camera2scene.m03 = V.x;
    camera2scene.m13 = V.y;
    camera2scene.m23 = V.z;
    camera2scene.m33 = 1.0f;
  }

  public Camera (final Camera c) {
    this.V = new Point3f(c.V);
    this.view = new Vector3f(c.view);
    this.camera2scene = new Matrix4f(c.camera2scene);
    this.optics = c.optics;
  }

  public final void toSceneCoordenates (final Vector3f v) {
    camera2scene.transform(v);
  }

  public final void toSceneCoordenates (final Point3f P) {
    camera2scene.transform(P);
  }

  public final Vector3f getLook () {
    return this.view;
  }

  public final Point3f getPosition () {
    return this.V;
  }

  public final void setProjection (final Projection p) {
    this.optics = p;
  }

  public final Projection getProjection () {
    return this.optics;
  }

  public final RayGenerator getRayGenerator (final int W, final int H) {
    return this.optics.getRayGenerator(this, W, H);
  }

}