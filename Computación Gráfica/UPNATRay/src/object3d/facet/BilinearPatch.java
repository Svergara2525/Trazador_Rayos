package object3d.facet;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.fma;
import static java.lang.Math.signum;
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
import object3d.boundingvolume.AABB;
//
public final class BilinearPatch implements Facet {
  
  static private final BilinearFunctionsNewtonSolver SOLVER
    = new BilinearFunctionsNewtonSolver();

  private final Vertex3D A;
  private final Vertex3D B;
  private final Vertex3D C;
  private final Vertex3D D;

  private final Vector3f AB;
  private final Vector3f AD;
  private final Vector3f E;
  
  private final Vector3f g;

  private final boolean isRhomboid;
  private final boolean noTextured;

  private float area;

  private final float e2;  
  private final float e3;
  private final float e4;

  private final float f2;
  private final float f3;  
  private final float f4;
  
  private final AABB boundingBox;
  
  public BilinearPatch (final Vertex3D A,
                        final Vertex3D B,
                        final Vertex3D C,
                        final Vertex3D D) {
    
    this.A = A;
    this.B = B;
    this.C = C;
    this.D = D;
    this.AB = new Vector3f();
    this.AB.sub(B.getPoint(), A.getPoint());
    this.AD = new Vector3f();
    this.AD.sub(D.getPoint(), A.getPoint());
    
    final Vector3f _E = new Vector3f();
    _E.sub(C.getPoint(), D.getPoint());    
    _E.sub(AB);

    final float Elength = _E.length();
    this.isRhomboid = (signum(Elength) == 0);
    
    this.g = new Vector3f();
    if (isRhomboid) {
      this.g.cross(AB, AD);
      this.area = g.length();
      this.E = null;
      this.e4 = 0;     
      this.f4 = 0;       
      this.boundingBox = null;
    } else {
      final Vector3f n = new Vector3f();

      n.cross(AB, AD);
      g.add(n);
      
      final Vector3f DC = new Vector3f();
      DC.sub(C.getPoint(), D.getPoint()); // C - D     
      
      n.cross(DC, AD);
      g.add(n);

      final Vector3f AC = new Vector3f();
      AC.sub(C.getPoint(), A.getPoint()); // C - A

      n.cross(AB, AC);
      g.add(n);

      final Vector3f BD = new Vector3f();
      BD.sub(D.getPoint(), B.getPoint()); // D - B

      n.cross(DC, BD);
      g.add(n);

      g.normalize();

      this.area = 1;
      
      this.E = _E;
      this.e4 = E.dot(AB);      
      this.f4 = E.dot(AD); 
      
      final Collection<Point3f> facets = new ArrayList<>(4);
      facets.add(this.A.getPoint());
      facets.add(this.B.getPoint());
      facets.add(this.C.getPoint());
      facets.add(this.D.getPoint());
      this.boundingBox = new AABB(facets);
      
    }

    this.noTextured = (A.getTextureCoordenates() == null) ||
                      (B.getTextureCoordenates() == null) ||
                      (C.getTextureCoordenates() == null) ||
                      (D.getTextureCoordenates() == null);
    
    // Primera ecuación:
    this.e2 = AB.dot(AB);
    this.e3 = AD.dot(AB);
    // Segunda ecuación:
    this.f3 = AD.dot(AD);     
    this.f2 = isRhomboid ? 1 / dop(e2, f3, e3, e3) : e3; //AB.dot(AD); 

  }

  @Override
  public Hit intersect (final Ray ray, final float tin, final float tout) {
    return isRhomboid ? intersectWithRhomboid(ray, tin, tout)
//                      : intersectWithQuadratic(ray, tin, tout);
                      : boundingBox.intersect(ray, tin, tout) ?
                        intersectWithQuadratic(ray, tin, tout) : NOHIT;
  }
    
  private Hit intersectWithQuadratic (final Ray ray,
                                      final float tmin, final float tmax) {
      
    // TODO
    
    final Vector3f AR = new Vector3f();
    AR.sub(A.getPoint(),ray.getStartingPoint());
    
    float alpha1 = AR.dot(ray.getDirection());
    float alpha2 = AB.dot(ray.getDirection());
    float alpha3 = AD.dot(ray.getDirection());
    float alpha4 = E.dot(ray.getDirection());
    
    float e0 = ray.getDirection().dot(AB);
    float e1 = AR.dot(AB);
    float e2 = this.e2;
    float e3 = this.e3;
    float e4 = this.e4;
    
    float a1 = fma(e0, alpha1, -e1);
    float a2 = fma(e0, alpha2, -e2);
    float a3 = fma(e0, alpha3, -e3);
    float a4 = fma(e0, alpha4, -e4);
    
    float f0 = ray.getDirection().dot(AD);
    float f1 = AR.dot(AD);
    float f2 = this.f2;
    float f3 = this.f3;
    float f4 = this.f4;
    
    float b1 = fma(f0, alpha1, -f1);
    float b2 = fma(f0, alpha2, -f2);
    float b3 = fma(f0, alpha3, -f3);
    float b4 = fma(f0, alpha4, -f4);
    
    final float[] solucion = SOLVER.solve(a1, a2, a3, b4, b1, b2, b3, b4);
    
    if (solucion.length == 0) return NOHIT;
    
    float beta = solucion[0];
    float gamma = solucion[1];
    
    if(beta < 0 || beta > 1 || gamma < 0 || gamma > 1) return NOHIT;
    
    float alpha = alpha1 + alpha2*beta + alpha3*gamma + alpha4*beta*gamma;
    
    if (alpha < tmin || alpha > tmax || alpha < 0) return NOHIT;
    
    final Point3f p = new Point3f();
    p.scaleAdd(alpha, ray.getDirection(), ray.getStartingPoint());
    
    final Vector3f normal = getNormal(beta, gamma);
    
    final TexCoord2f uv = noTextured ? null : getTextureCoordenates(beta, gamma);
    
    return new Hit(alpha, p, normal, uv);

  }
  


  private Hit intersectWithRhomboid (final Ray ray,
                                     final float tmin, final float tmax) {

    // TODO
    final Vector3f v = ray.getDirection();

    float denom = v.dot(g);
    if(signum(denom) == 0) return NOHIT;

    final Vector3f AR = new Vector3f();
    AR.sub(A.getPoint(), ray.getStartingPoint());

    float t = AR.dot(g) / denom;
    if(t < tmin || t > tmax) return NOHIT;

    Point3f p = new Point3f(v);
    p.scale(t);
    p.add(ray.getStartingPoint());

    Vector3f PA = new Vector3f();
    PA.sub(p, A.getPoint());

    float DX = PA.dot(AB);
    float DY = PA.dot(AD);

    float det = dop(e2, f3, e3, e3);
    if(signum(det) == 0) return NOHIT;

    float beta  = (DX * f3 - DY * e3) / det;
    float gamma = (DY * e2 - DX * e3) / det;

    if(beta < 0 || beta > 1 || gamma < 0 || gamma > 1) return NOHIT;

    Vector3f n = new Vector3f(g);
    if(signum(area) < 0) n.negate();

    TexCoord2f uv = noTextured ? null : getTextureCoordenates(beta, gamma);

    return new Hit(t, p, n, uv);
    
  }
  
  private Vector3f getNormal (final float U, final float V) {
    // TODO
    return new Vector3f();
  }
  
  private TexCoord2f getTextureCoordenates (final float U, final float V) {
    final TexCoord2f uvA = A.getTextureCoordenates();
    final TexCoord2f uvB = B.getTextureCoordenates();
    final TexCoord2f uvC = C.getTextureCoordenates();
    final TexCoord2f uvD = D.getTextureCoordenates();
    final float ut0 = sop(1 - U, uvA.x, U, uvB.x);
    final float ut1 = sop(1 - U, uvD.x, U, uvC.x);
    final float ut  = sop(1 - V, ut0, V, ut1);
    final float vt0 = sop(1 - U, uvA.y, U, uvB.y);
    final float vt1 = sop(1 - U, uvD.y, U, uvC.y);
    final float vt  = sop(1 - V, vt0, V, vt1);
    return new TexCoord2f(ut, 1 - vt);
  }

  private void check () {

    final float L = 1E-4f;
    
    final boolean ab = signum(AB.length() - L) < 0;
    final boolean da = signum(AD.length() - L) < 0;

    final Vector3f CD = new Vector3f();
    CD.sub(D.getPoint(), C.getPoint());
    final boolean cd = signum(CD.length() - L) < 0;

    final Vector3f BC = new Vector3f();
    BC.sub(C.getPoint(), B.getPoint());
    final boolean bc = signum(BC.length() - L) < 0;

    if (ab || bc || cd || da)
      System.out.println(AB.length() + " " + BC.length() + " " + CD.length() + " " + AD.length());
    
  }
  
  @Override
  public boolean intersectAny (Ray ray, float tmin, float tmax) {
    
    // TODO
    
    return false;   
  }  

  @Override
  public boolean isXanterior (final float x) {
    return A.isXanterior(x) ||
           B.isXanterior(x) ||
           C.isXanterior(x) || 
           D.isXanterior(x);
  }

  @Override
  public boolean isYanterior (final float y) {
    return A.isYanterior(y) ||
           B.isYanterior(y) ||
           C.isYanterior(y) || 
           D.isYanterior(y);
  }

  @Override
  public boolean isZanterior (final float z) {
    return A.isZanterior(z) ||
           B.isZanterior(z) ||
           C.isZanterior(z) || 
           D.isZanterior(z);
  }

  @Override
  public boolean isXposterior (final float x) {
    return A.isXposterior(x) ||
           B.isXposterior(x) ||
           C.isXposterior(x) ||
           D.isXposterior(x);
  }

  @Override
  public boolean isYposterior (final float y) {
    return A.isYposterior(y) ||
           B.isYposterior(y) ||
           C.isYposterior(y) ||
           D.isYposterior(y);
  }

  @Override
  public boolean isZposterior (final float z) {
    return A.isZposterior(z) ||
           B.isZposterior(z) ||
           C.isZposterior(z) ||
           D.isZposterior(z);
  }

  @Override
  public Collection<Vertex3D> getVertices () {
    final List<Vertex3D> list = new ArrayList<>(4);
    list.add(A);
    list.add(B);
    list.add(C);
    list.add(D);
    return list;
  }

  @Override
  public void setFlat () { area = (signum(area) > 0) ? -area : area; }

}

