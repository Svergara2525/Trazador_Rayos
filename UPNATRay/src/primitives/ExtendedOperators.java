package primitives;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.fma;
import org.jogamp.vecmath.Vector3f;
//
//
public final class ExtendedOperators {
  
  static public Vector3f opposite (final Vector3f v) {
    return new Vector3f(-v.x, -v.y, -v.z);
  }
  
  static public float cdot (final Vector3f a, final Vector3f b) {
    return (float) (Math.max(0.0, Math.min(1.0, a.dot(b))));
  }
  
  static public float triple (final Vector3f c,
                              final Vector3f a,
                              final Vector3f b) {
    final float xa = a.x;
    final float ya = a.y;
    final float za = a.z;
    final float xb = b.x;
    final float yb = b.y;
    final float zb = b.z;
    final float x = dop(ya, zb, za, yb); // ya * zb - za * yb;
    final float y = dop(za, xb, xa, zb); // za * xb - xa * zb;
    final float z = dop(xa, yb, ya, xb); // xa * yb - ya * xb;     
    return fma(c.x, x, fma(c.y, y, c.z * z));    
  }
  
  static public double dop (final double a, final double b,
                            final double c, final double d) {
    final double cd = -c * d;
    final double err = fma(c, d, cd);
    final double r = fma(a, b, cd);
    return r + err;
  }  
  
  static public float dop (final float a, final float b,
                           final float c, final float d) {
    final float cd = c * d;
    final float err = fma(c, d, -cd);
    final float r = fma(a, b, -cd);
    return r + err;
  }

  static public float sop (final float a, final float b,
                           final float c, final float d) {
    final float cd = -c * d;
    final float err = fma(c, d, cd);
    final float r = fma(a, b, -cd);
    return r + err;
  }
  
  static public float clamp (final float x) {
    return (Math.signum(x - 1.0f) > 0) ? 1.0f : ((Math.signum(x) < 0) ? 0.0f: x);
  }  
  
}
