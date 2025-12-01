package object3d.boundingvolume;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.abs;
import static java.lang.Math.signum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
import org.jogamp.vecmath.Matrix4f;
//
import object3d.facet.Vertex3D;
import raytracer.Ray;
//
public class AABB implements BoundingVolume {

  static public final AABB NOBOUNDINGBOX = new NoBoundingBox();

  public final float Xmax;
  public final float Ymax;
  public final float Zmax;
  public final float Xmin;
  public final float Ymin;
  public final float Zmin;

  private final float halfLx;
  private final float halfLy;
  private final float halfLz;

  private final float Cx;
  private final float Cy;
  private final float Cz;
  
  private static Set<Point3f> getPoints (final Set<Vertex3D> vertices) {
    final Set<Point3f> points = new LinkedHashSet<>();
    for (final Vertex3D v: vertices)
      points.add(v.getPoint());
    return points;
  }

  public AABB (final float xmin, final float xmax,
               final float ymin, final float ymax,
               final float zmin, final float zmax) {

    Xmax = xmax;
    Xmin = xmin;
    Ymax = ymax;
    Ymin = ymin;
    Zmax = zmax;
    Zmin = zmin;

    halfLx = (Xmax - Xmin) * 0.5f;
    halfLy = (Ymax - Ymin) * 0.5f;
    halfLz = (Zmax - Zmin) * 0.5f;

    Cx = (Xmax + Xmin) * 0.5f;
    Cy = (Ymax + Ymin) * 0.5f;
    Cz = (Zmax + Zmin) * 0.5f;

  }
  
  public AABB (final Set<Vertex3D> vertices) {
    this(getPoints(vertices));
  }  

  public AABB (final Collection<Point3f> points) {

    float Xmax_ = Float.NEGATIVE_INFINITY;
    float Ymax_ = Float.NEGATIVE_INFINITY;
    float Zmax_ = Float.NEGATIVE_INFINITY;
    float Xmin_ = Float.POSITIVE_INFINITY;
    float Ymin_ = Float.POSITIVE_INFINITY;
    float Zmin_ = Float.POSITIVE_INFINITY;
    
    for (final Point3f P: points) {
      if (signum(P.x - Xmax_) > 0) {
        Xmax_ = P.x;
      }
      if (signum(P.x - Xmin_) < 0) {
        Xmin_ = P.x;
      }
      if (signum(P.y - Ymax_) > 0) {
        Ymax_ = P.y;
      }
      if (signum(P.y - Ymin_) < 0) {
        Ymin_ = P.y;
      }
      if (signum(P.z - Zmax_) > 0) {
        Zmax_ = P.z;
      }
      if (signum(P.z - Zmin_) < 0) {
        Zmin_ = P.z;
      }
    }
    Xmax = Xmax_;
    Xmin = Xmin_;
    Ymax = Ymax_;
    Ymin = Ymin_;
    Zmax = Zmax_;
    Zmin = Zmin_;

    halfLx = (Xmax - Xmin) * 0.5f;
    halfLy = (Ymax - Ymin) * 0.5f;
    halfLz = (Zmax - Zmin) * 0.5f;

    Cx = (Xmax + Xmin) * 0.5f;
    Cy = (Ymax + Ymin) * 0.5f;
    Cz = (Zmax + Zmin) * 0.5f;

  }
  
  @Override
  public boolean isInside (final Point3f P) {
    return ((signum(halfLx - abs(P.x - Cx)) > 0) &&
            (signum(halfLy - abs(P.y - Cy)) > 0) &&
            (signum(halfLz - abs(P.z - Cz)) > 0));
  }

  @Override
  public boolean isOutside (final Point3f P) {
    return ((signum(halfLx - abs(P.x - Cx)) < 0) ||
            (signum(halfLy - abs(P.y - Cy)) < 0) ||
            (signum(halfLz - abs(P.z - Cz)) < 0));
  }

  @Override
  public boolean intersect (final Ray ray, final float tmin, final float tmax) {

    final Point3f  R = ray.getStartingPoint();
    final Vector3f v = ray.getDirection();

    float tNear = tmin;
    float tFar  = tmax;
    
    // TODO

    return true;

  }
 
  @Override
  public float[] intersectComplete (final Ray ray, final float tmin, final float tmax) {
 
    final Point3f  R = ray.getStartingPoint();
    final Vector3f v = ray.getDirection();

    float tNear = tmin;
    float tFar  = tmax;

    // TODO

    return new float[0];

  }
  
  Collection<Point3f> getCorners () {

    // Vértices (esquinas) del ortoedro
    final Collection<Point3f> corners = new ArrayList<>();
    corners.add(new Point3f(Xmin, Ymin, Zmin));
    corners.add(new Point3f(Xmax, Ymin, Zmin));
    corners.add(new Point3f(Xmin, Ymax, Zmin));
    corners.add(new Point3f(Xmax, Ymax, Zmin));
    corners.add(new Point3f(Xmin, Ymin, Zmax));
    corners.add(new Point3f(Xmax, Ymin, Zmax));
    corners.add(new Point3f(Xmin, Ymax, Zmax));
    corners.add(new Point3f(Xmax, Ymax, Zmax));
    
    return corners;
    
  }

  @Override
  public AABB transformedBy (final Matrix4f M) {

    // Vértices (esquinas) del ortoedro
    final Collection<Point3f> corners = getCorners();

    // Transformación de vértices
    for (final Point3f P: corners)
      M.transform(P);

    // Nueva AABB
    return new AABB(corners);

  }

  @Override
  public String toString () {
    String s = "Xmin: " + Xmin + " Xmax: " + Xmax;
    s += " Ymin: " + Ymin + " Ymax: " + Ymax;
    s += " Zmin: " + Zmin + " Zmax: " + Zmax;
    return s;
  }

}
