package object3d.facet;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.fma;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.TexCoord2f;
import org.jogamp.vecmath.Vector3f;
//
import static raytracer.Hit.NOHIT;
import raytracer.Hit;
import raytracer.Ray;
import static primitives.ExtendedOperators.dop;
import static primitives.ExtendedOperators.sop;
//
public class Triangle implements Facet {
  
  private final Vertex3D A;
  private final Vertex3D B;
  private final Vertex3D C;
  private final Vector3f g; // Normal geométrica no normalizada
  private final Vector3f AB;
  private final Vector3f AC;
  
//  public final float u;
//  public final float w;
//  public final float v;
  public final float den; 
  
  private final boolean noTextured;  

  private float area;

  public Triangle (final Vertex3D A, final Vertex3D B, final Vertex3D C,
                   final boolean isFlat) throws IllegalArgumentException {

    this.A = A;
    this.B = B;
    this.C = C;
    this.AB = new Vector3f();
    this.AB.sub(B.getPoint(), A.getPoint()); // B - A
    this.AC = new Vector3f();
    this.AC.sub(C.getPoint(), A.getPoint()); // C - A
    this.g = new Vector3f();
    this.g.cross(AB, AC);
    
//    this.u = AB.dot(AB);
//    this.w = AC.dot(AC);
//    this.v = AB.dot(AC);
    final float u = AB.dot(AB);
    final float v = AB.dot(AC);    
    final float w = AC.dot(AC);
    this.den = 1 / dop(u, w, v, v);
    
    if (Float.isInfinite(this.den)) {
//      System.out.println(A.getPoint());
//      System.out.println(B.getPoint());
//      System.out.println(C.getPoint());
//      System.out.println(AB);
//      System.out.println(AC);
      throw new IllegalArgumentException("Triángulo degenerado");
    }
    
    this.noTextured = (A.getTextureCoordenates() == null) ||
                      (B.getTextureCoordenates() == null) ||
                      (C.getTextureCoordenates() == null);

    // Área: mitad del módulo de la norma geométrica.
    this.area = (float) (0.5 * sqrt(g.dot(g)));

  }
  
  public Triangle (final Point3f A, final Point3f B, final Point3f C) {
    this(new Vertex3D(A), new Vertex3D(B), new Vertex3D(C), true);
  }  

  public Triangle (final Vertex3D A, final Vertex3D B, final Vertex3D C) {
    this(A, B, C, false);
  }

  @Override
  public Hit intersect (final Ray ray, final float tmin, final float tmax) {
    
    final Point3f R = ray.getStartingPoint();    
    final Vector3f v = ray.getDirection();
    
    // TODO
    
    return NOHIT;
  }  

  
  private Vector3f smoothNormal (final float aa, final float bb, final float cc) {
    // TODO
    return new Vector3f(0, 0, 0);
  }
  
  private TexCoord2f getTextureCoordenates (final float aa, final float bb, final float cc) {
    final TexCoord2f uvA = A.getTextureCoordenates();
    final TexCoord2f uvB = B.getTextureCoordenates();
    final TexCoord2f uvC = C.getTextureCoordenates();
    final float ut = +fma(aa, uvA.x, sop(bb, uvB.x, cc, uvC.x));
    final float vt = -fma(aa, uvA.y, fma(bb, uvB.y, fma(cc, uvC.y, -1)));
    return new TexCoord2f(ut, vt);
  }
  
  @Override
  public boolean intersectAny (final Ray ray, final float tmin, final float tmax) {
    
    final Point3f R = ray.getStartingPoint();    
    final Vector3f v = ray.getDirection();
  
    // TODO
    
    return false;
   
  }   
 
  public final Vector3f getNormal () {
    return this.g;
  }

  public final Point3f getA () {
    return this.A.getPoint();
  }

  public final Point3f getB () {
    return this.B.getPoint();
  }

  public final Point3f getC () {
    return this.C.getPoint();
  }

  public float area () {
    return area;
  }

  @Override
  public boolean isXanterior (final float x) {
    return A.isXanterior(x) || B.isXanterior(x) || C.isXanterior(x);
  }

  @Override
  public boolean isYanterior (final float y) {
    return A.isYanterior(y) || B.isYanterior(y) || C.isYanterior(y);
  }

  @Override
  public boolean isZanterior (final float z) {
    return A.isZanterior(z) || B.isZanterior(z) || C.isZanterior(z);
  }

  @Override
  public boolean isXposterior (final float x) {
    return A.isXposterior(x) || B.isXposterior(x) || C.isXposterior(x);
  }

  @Override
  public boolean isYposterior (final float y) {
    return A.isYposterior(y) || B.isYposterior(y) || C.isYposterior(y);
  }

  @Override
  public boolean isZposterior (final float z) {
    return A.isZposterior(z) || B.isZposterior(z) || C.isZposterior(z);
  }

  @Override
  public Collection<Vertex3D> getVertices () {
    final List<Vertex3D> list = new ArrayList<>(3);
    list.add(A);
    list.add(B);
    list.add(C);
    return list;
  }

  @Override
  public void setFlat () { area = (signum(area) > 0) ? -area : area; }

}