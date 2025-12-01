 package object3d.model;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.signum;
import java.util.Map;
import java.util.Set;
//
import static raytracer.Hit.NOHIT;
import object3d.boundingvolume.AABB;
import object3d.facet.Facet;
import object3d.facet.Vertex3D;
import object3d.kdtree.KdTree;
import raytracer.Hit;
import raytracer.Ray;
//
public class PolygonalMesh implements Model3D {
  
  private KdTree kdtree;  
  private final AABB boundingBox;
  private final Set<Facet> facets;
  private final Map<Vertex3D, Set<Facet>> vertexToFacetMap;
      
  public PolygonalMesh (final Set<Facet> facets,
                        final Map<Vertex3D, Set<Facet>> vertexToFacetMap) {

    this.facets = facets;
    this.vertexToFacetMap = vertexToFacetMap;
    this.boundingBox = new AABB(vertexToFacetMap.keySet());
    this.kdtree = null;
   
  }
  
  public void set (final KdTree kdtree) {
    this.kdtree = kdtree;
    kdtree.set(this);
  }

  @Override
  public Hit intersect (final Ray ray, final float tmin, final float tmax) {
    Hit closestHit = NOHIT;
    for (final Facet x: facets) {
      final Hit lastHit = x.intersect(ray, tmin, tmax);
      if (lastHit.isCloserThan(closestHit))
        closestHit = lastHit;
    }
    return closestHit;
//    final float[] hits = boundingBox.intersectComplete(ray);
//    if (hits.length == 2) {
//      final float _tmin = max(hits[0], tmin);
//      final float _tmax = min(hits[1], tmax);
//      if (signum(_tmax - _tmin) < 0)
//        return NOHIT;
//      final Hit hit = kdtree.intersect(ray, _tmin, _tmax);
//      return hit;
//    }
//    return NOHIT;
  }

  @Override
  public boolean intersectAny (final Ray ray, final float tmin, final float tmax) {
    final float[] hits = boundingBox.intersectComplete(ray);
    if (hits.length == 2) {
      final float _tmin = max(hits[0], tmin);
      final float _tmax = min(hits[1], tmax);
      return (signum(_tmin - _tmax) <= 0) ? kdtree.intersectAny(ray, _tmin, _tmax) : false;
    }
    return false;
  }
  
  public Set<Facet> getFacets () { return facets; }
  public Map<Vertex3D, Set<Facet>> getMap () { return vertexToFacetMap; }
  
  @Override
  public AABB getBoundingBox () {
    return boundingBox;
  } 
  
}