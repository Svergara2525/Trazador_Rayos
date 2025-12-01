package object3d.boundingvolume;
/**
 *
 * @author MAZ
 */
import org.jogamp.vecmath.Matrix4f;
//
import object3d.Volumetric;
import raytracer.Ray;
//
public interface BoundingVolume extends Volumetric {
 
  /* 
    Devuelve true si el rayo atraviesa el volumen
    definido por la envoltura; false en caso contrario.
  */
  public boolean intersect (final Ray ray, final float tmin, final float tmax);

  /* 
    Devuelve un array con los valores del parámetro alfa de los puntos
    de intersección del rayo con las fronteras del volumen; los valores
    del array están ordenados en creciente y dentro del intervalo [tmin, tmax].
  */  
  public float[] intersectComplete (final Ray ray, final float tmin, final float tmax);
  
  /* 
    Devuelve un array con los valores del parámetro alfa de los puntos
    de intersección del rayo con las fronteras del volumen; los valores
    del array están ordenados en creciente.
  */
  default public float[] intersectComplete (final Ray ray) {
    return intersectComplete(ray, 0, Float.POSITIVE_INFINITY);
  }
  
  /* 
    Devuelve el volumen envolvente resulta de aplicar la transformación afín
    cuya matriz se proporciona como argumento.
  */
  public BoundingVolume transformedBy (final Matrix4f M);
  
}