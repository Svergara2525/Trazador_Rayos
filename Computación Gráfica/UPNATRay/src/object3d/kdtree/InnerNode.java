package object3d.kdtree;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.signum;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;
//
import object3d.kdtree.KdTree.AXIS;
import raytracer.Hit;
import raytracer.Ray;
//
final class InnerNode extends Node {

  private final Node anterior, posterior;
  private final float split;
  private final AXIS axis;

  InnerNode (final AXIS axis, final float splitValue,
             final Node posterior, final Node anterior) {
    this.split = splitValue;
    this.axis = axis;    
    this.anterior = anterior;
    this.posterior = posterior;
  }

  @Override
  Hit intersect (final Ray ray, final float tin, final float tout) {

    if (Math.signum(tin - tout) > 0) {
      throw new IllegalArgumentException(tin + " (tin) > " + tout + " (tout)");
    }

    final Point3f  R = ray.getStartingPoint();
    final Vector3f v = ray.getDirection();

    final float num;
    final float vc;
    switch (axis) {
      case X:
        num = split - R.x;
        vc = v.x;
        break;

      case Z:
        num = split - R.z;
        vc = v.z;
        break;

      case Y:
      default:
        num = split - R.y;
        vc = v.y;
    }

    if (signum(vc) != 0) { // Rayo no paralelo al plano de split

      // Indica si trayectoria del rayo va de nodo izquierdo a nodo derecho
      final boolean leftToRight = signum(vc) > 0;
      final Node near = leftToRight ? posterior : anterior;
      final Node far  = leftToRight ? anterior : posterior;

      final float tSplit = num / vc;
      if (signum(tSplit - tin) < 0) {
        return far.intersect(ray, tin, tout);
      } else if (signum(tout - tSplit) < 0) {
        return near.intersect(ray, tin, tout);
      } else { // if ((signum(tin - tSplit) <= 0) && (signum(tSplit - tout) <= 0)) {
        // En método de intersección de Leaf se hace clamp con los limites recibidos.
        // En consecuencia, si la intersección no es vacía, está dentro
        // de los límites establecidos aquí.
        final Hit hitn = near.intersect(ray, tin, tSplit);
        return hitn.hits() ? hitn : far.intersect(ray, tSplit, tout);
//        final Hit hitn = near.intersect(fromOutside, ray, tin, tSplit);
//        final Hit hitf = far.intersect(fromOutside, ray, tSplit, tout);
//        return (hitn.isCloserThan(hitf)) ? hitn : hitf;
      }

    } else { // Rayo paralelo al plano de split
      return ((signum(num) >= 0) ? posterior : anterior).intersect(ray, tin, tout);
    }

  }
  
  @Override
  boolean intersectAny (final Ray ray, final float tin, final float tout) {

    if (Math.signum(tin - tout) > 0) {
      throw new IllegalArgumentException(tin + " (tin) > " + tout + " (tout)");
    }

    final Point3f R = ray.getStartingPoint();
    final Vector3f v = ray.getDirection();

    final float num;
    final float vc;
    switch (axis) {
      case X:
        num = split - R.x;
        vc = v.x;
        break;

      case Z:
        num = split - R.z;
        vc = v.z;
        break;

      case Y:
      default:
        num = split - R.y;
        vc = v.y;
    }

    if (signum(vc) != 0) { // Rayo no paralelo al plano de split

      // Indica si trayectoria del rayo va de nodo izquierdo a nodo derecho
      final boolean leftToRight = signum(vc) > 0;
      final Node near = leftToRight ? posterior : anterior;
      final Node far  = leftToRight ? anterior : posterior;

      final float tSplit = num / vc;
      if (signum(tSplit - tin) < 0) {
        return far.intersectAny(ray, tin, tout);
      } else if (signum(tout - tSplit) < 0) {
        return near.intersectAny(ray, tin, tout);
      } else { // if ((signum(tin - tSplit) <= 0) && (signum(tSplit - tout) <= 0)) {
        // En método de intersección de Leaf se hace clamp con los limites recibidos.
        // En consecuencia, si la intersección no es vacía, está dentro
        // de los límites establecidos aquí.
        return near.intersectAny(ray, tin, tSplit) ? true : far.intersectAny(ray, tSplit, tout);
      }

    } else { // Rayo paralelo al plano de split
      return ((signum(num) >= 0) ? posterior : anterior).intersectAny(ray, tin, tout);
    }

  }  

  @Override
  boolean isInside (final Point3f P) {
    final boolean inSplit = signum(((axis == AXIS.X) ? P.x :
                                   ((axis == AXIS.Y) ? P.y : P.z)) - split) == 0;
    return posterior.isInside(P) || anterior.isInside(P) || inSplit;
  }

  @Override
  boolean isOutside (final Point3f P) {
    return posterior.isOutside(P) && anterior.isOutside(P);
  }

}
