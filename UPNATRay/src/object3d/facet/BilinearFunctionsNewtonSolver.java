package object3d.facet;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.abs;
import static java.lang.Math.fma;
import static java.lang.Math.signum;
//
import static primitives.ExtendedOperators.dop;
import static primitives.ExtendedOperators.sop;
//
final class BilinearFunctionsNewtonSolver {

  // Número máximo de iteraciones por defecto
  static private final int _N = 5;

  // Valor del parámetro para estimar la mejora en la aproximación computada
  static private final float RHO = (System.getProperty("RHO") != null) ?
    Float.parseFloat(System.getProperty("RHO")) : 1E-2f;

  private final float rho2;
  private final int N;

  BilinearFunctionsNewtonSolver () {
    this.rho2 = RHO * RHO;
    this.N = _N;
  }

  BilinearFunctionsNewtonSolver (final float rho, final int N) {
    this.rho2 = rho * rho;
    this.N = N;
  }

  float[] solve (final float a0, final float a1, final float a2, final float a3,
                 final float b0, final float b1, final float b2, final float b3) {
    // TODO
    float u = 0.5f;
    float v = 0.5f;

    for (int k = 0; k < N; k++){
        float F = eval(a0, a1, a2, a3, u, v);
        float G = eval(b0, b1, b2, b3, u, v);
        
        float dFu = fma(a3, v, a2);
        float dFv = fma(a3, u, a2);
        float dGu = fma(b3, v, b2);
        float dGv = fma(b3, u, b2);
        
        float detJ = dop(dFu, dGv, dFv, dGu);
        
        float x = dop(-F, dGv, dFv, -G)/detJ;
        float y = dop(dFu, -G, -F, dGu)/detJ;
        
        u = x + u;
        v = y + v;
        
        if (sop(x/u, x/u, y/v, y/v) <= rho2) {
            break;
        }   
    }
    
    float Ffinal = eval(a0, a1, a2, a3, u, v);
    float Gfinal = eval(b0, b1, b2, b3, u, v);
    
    final float limite = 0.5e-6f;
    
    if (abs(Ffinal) <= limite && abs(Gfinal) <= limite){
        if (u >= 0 && u <= 1 && v >= 0 && v <= 1){
            return new float[] { u, v }; 
        }
        return new float[0]; 
    }
    return new float[0]; 
  }

  float eval (final float a, final float b, final float c, final float d,
              final float u, final float v) {

    // Evalúa la expresión a + u * b + v * c + u * v * d
    // de la forma siguiente (u * d + c) * v + (u * b + a).
    final float t1 = fma(u, d, c);
    final float t2 = fma(u, b, a);
    return fma(t1, v, t2);

  }

}
