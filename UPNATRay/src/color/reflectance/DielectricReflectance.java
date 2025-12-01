package color.reflectance;
/**
 *
 * @author MAZ
 */
import static java.lang.Math.fma;
import static java.lang.Math.max;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;
//
import static primitives.ExtendedOperators.sop;
import color.RadianceRGB;
//
public class DielectricReflectance extends FresnelianReflectance {
  
  private final float eta_int;
  private final float eta_ext;
  private final float rho;
  private final float inv_rho;
  private final float critical_outside;
  private final float critical_inside;
  
  public DielectricReflectance (final ReflectanceRGB rfc,
                                final float eta_int,
                                final float eta_ext) {
    super(rfc);
    this.eta_int = eta_int;
    this.eta_ext = eta_ext;
    this.rho = eta_ext / eta_int;
    this.inv_rho = eta_int / eta_ext;
    this.critical_inside  = (float) sqrt(-fma(rho, rho, -1));
    this.critical_outside = (float) sqrt(-fma(inv_rho, inv_rho, -1));    
  }

  public RadianceRGB reflectiveFilter (final boolean toOutside,
                                       final float cosi, final float coso,
                                       final RadianceRGB radiance) {

    // El fenómeno de reflexión interna total se ha considerado en la clase BTDF.
    final float T = getUnpolarizedTransmitanceFactor(toOutside, cosi, coso);        
    return filter(1 - T, radiance); 
    
  }

  public RadianceRGB transmissiveFilter (final boolean toOutside,
                                         final float cosi, final float coso,
                                         final RadianceRGB radiance) {
    
    //Model3D.testCalls--;    
    
    // El fenómeno de reflexión interna total se ha considerado en la clase BTDF.
    final float T = getUnpolarizedTransmitanceFactor(toOutside, cosi, coso);
    return filter(T, radiance);   
    
  }

  // Unpolarized light transmittance
  private float getUnpolarizedTransmitanceFactor (final boolean toOutside,
                                                  final float _cosi, final float _cost) {
    
    // La simetría de las expresiones hace innecesario considerar el sentido
    // de transmisión (de dentro hacia afuera, o viceversa). En trasmisión hacia
    // el exterior simplemente cambia la nomenclatura: la expresión de Ts sirve
    // como Tp, y la expresión de Tp sirve como Ts. 

    final float cosi = max(0, _cosi);
    final float cost = max(0, _cost);
    
    final float eta_i = toOutside ? eta_int : eta_ext;
    final float eta_t = toOutside ? eta_ext : eta_int;
    
    final float a = eta_i * cosi;
    final float b = eta_t * cost;
    final float ts = 1 / (a + b);
    final float Ts = ts * ts;
    
    final float c = sop(eta_t, cosi, eta_i, cost);
    final float tp = 1 / c;
    final float Tp = tp * tp;
    
    final float T = 2 * a * b * (Ts + Tp);
    
    //System.out.printf("a = %3.2f b = %3.2f c = %3.2f Ts = %3.2f Tp = %3.2f T = %3.2f\n", a, b, c, Ts, Tp, 2 * a * b * (Ts + Tp));
//    if (signum(T - 1) < 0)
//      System.out.printf("T = %9.8f\n", T);
    
    return T;
    
  }  

  @Override
  public boolean isDielectric () {
    return true;
  }

  @Override
  public boolean isConductive () {
    return false;
  }
  
  public float eta_int () { return eta_int; }
  public float eta_ext () { return eta_ext; }  
  public float rho (final boolean toOutside) { return toOutside ? rho : inv_rho; }
  public float critical (final boolean toOutside) {
    if (signum(rho(toOutside) - 1) > 0)
      return toOutside ? critical_outside : critical_inside;
    else // rho <= 1
      return 0;
  }
  
}