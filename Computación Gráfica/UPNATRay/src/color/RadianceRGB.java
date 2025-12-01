 package color;
/**
 * Clase para manejar y operar con valores de radiancia/irradiancia
 * en los tres canales en los que irradian nuestras fuentes luminosas.
 *
 * @author MAZ
 */
import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_ProfileRGB;
import org.jogamp.vecmath.Matrix3f;
//
import static primitives.ExtendedOperators.sop; 
//
public final class RadianceRGB {
  
  // Objeto estatico -introducido por razones de eficiencia-
  // a devolver cuando la radiancia es cero.
  public static final RadianceRGB NORADIANCE = new RadianceRGB();
  
  // Umbral de ineficiencia.
  static private final float NEGLIGIBLE_RADIANCE = 1E-6f;

  // Espacio de color de dispositivo de Java: sRGB D50
  private static final ColorSpace PCS = new ICC_ColorSpace(ICC_ProfileRGB.getInstance(ColorSpace.CS_sRGB));
  
  //
  // Matrices para conversión a luminancia y color
  //
  private static final float LIGHT_EFFICIENCY = 683.002f; // lúmenes por vatio en 555 nm

  // Valores de las funciones para igualar colores r(), g() y b() del espacio CIE-RGB
  // en las longitudes de onda en las que irradian las fuentes luminosas aplicadas.
  // Para las longitudes de onda 546.1 y 435.8 se emplean los valores más cercanos
  // que se pueden encontrar en las tablas.
  private static final float[] CIE_RGB_MATCHING_VALUES = new float[] {
  // Valores en:  700.0 nm (700.0 nm) 545.0 nm (546.1 nm)   435.0 nm (435.8 nm)
                  +0.0041f,           -0.0061f,             +0.0004f, // Funcion r()
                  +0.0000f,           +0.2149f,             -0.0002f, // Funcion g()
                  +0.0000f,           +0.0002f,             +0.2901f  // Funcion b()
          // Valores interpolados
//                  +0.0041f,           +0.000258f,             -0.000080f, // Funcion r()
//                  +0.0000f,           +0.214218f,             +0.000072f, // Funcion g()
//                  +0.0000f,           +0.000024f,             +0.293752f  // Funcion b()
  };

  // Elementos de la matriz de paso de CIE-RGB a CIE-XYZ
  private static final float[] CIE_RGB_TO_XYZ_ELEMENTS = new float[] {
    +0.4887180f, +0.3106803f, +0.2006017f, // X row
    +0.1762044f, +0.8129847f, +0.0108109f, // Y row
    +0.0000000f, +0.0102048f, +0.9897952f  // Z row
  };
  
  private static final float[] CIE_XYZ_TO_XYZ_D50_ELEMENTS = new float[] {  
    +0.9642200f, +0.0000000f,  +0.0000000f,
    +0.0000000f, +1.0000000f,  +0.0000000f,
    +0.0000000f, +0.0000000f,  +0.8252100f
  };  
  
  private static final Matrix3f TO_LUMINANCE = new Matrix3f(CIE_RGB_MATCHING_VALUES);
  private static final Matrix3f RGB_TO_XYZ   = new Matrix3f(CIE_RGB_TO_XYZ_ELEMENTS);
  private static final Matrix3f XYZ_TO_XYZ_D50 = new Matrix3f(CIE_XYZ_TO_XYZ_D50_ELEMENTS);  

  private static final Matrix3f RADIANCE_TO_XYZ = fromRadianceToXYZ();  
  private static final Matrix3f RADIANCE_TO_XYZD50 = fromRadianceToXYZD50();

  private static Matrix3f fromRadianceToXYZ () {
    final Matrix3f m = new Matrix3f(RGB_TO_XYZ);   
    m.mul(TO_LUMINANCE);
    m.mul(LIGHT_EFFICIENCY);
    //System.out.println(m);
    //System.out.println(new RadianceRGB(137.39f, 1.21f, 1.07f).getColor(m));
    return m;
  }
  
  private static Matrix3f fromRadianceToXYZD50 () {
    final Matrix3f m = new Matrix3f(XYZ_TO_XYZ_D50);
    m.mul(TO_LUMINANCE);
    m.mul(LIGHT_EFFICIENCY);
    return m;
  }

  /**
   * Radiancia/irradiancia en banda roja (700.0 nm)
   */
  public float r;

  /**
   * Radiancia/irradiancia en banda verde (546.1 nm)
   */
  public float g;

  /**
   * Radiancia/irradiancia en banda azul (435.8 nm)
   */
  public float b;

  /**
   * Constructor por defecto.
   */
  public RadianceRGB () {
    this(0.0f, 0.0f, 0.0f);
  }

  /**
   * Clona un valor de radiancia/irradiancia.
   *
   * @param rgb: valores de radiancia a duplicar
   */
  public RadianceRGB (final RadianceRGB rgb) {
    this(rgb.r, rgb.g, rgb.b);
  }
  
  /**
   * Copia un valor de radiancia/irradiancia.
   *
   * @param f: factor de multiplicación
   * @param rgb: valores de radiancia a duplicar
   */
  public RadianceRGB (final float f, final RadianceRGB rgb) {
    this(f * rgb.r, f * rgb.g, f * rgb.b);
  }  

  /**
   * Constructor explícito.
   *
   * @param r
   * @param g
   * @param b
   */
  public RadianceRGB (final float r, final float g, final float b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  /**
   * Copia los valores de rdc a este objeto.
   *
   * @param rdc
   * @return this
   */
  public RadianceRGB set (final RadianceRGB rdc) {
    this.r = rdc.r;
    this.g = rdc.g;
    this.b = rdc.b;
    return this;
  }

  /**
   * Establece los valores dados (r, g, b) como valores de radiancia de este objeto.
   *
   * @param r
   * @param g
   * @param b
   * @return this
   */
  public RadianceRGB set (final float r, final float g, final float b) {
    this.r = r;
    this.g = g;
    this.b = b;
    return this;
  }

  /**
   * Escala cada componente de este objeto por el factor de escala proporcionado.
   *
   * @param k The scale value to use.
   * @return this
   */
  public RadianceRGB scale (final float k) {
    if (Math.signum(k) < 0)
      throw new IllegalArgumentException("Constante de escalado negativa: " + k);
//    if (Math.signum(k - 1.0) > 0)
//      throw new IllegalArgumentException("Constante de escalado no conservativa (mayor que 1)");
    this.r *= k;
    this.g *= k;
    this.b *= k;
    return this;
  }

  /**
   * Acumula valores de radiancia/irradiancia.
   *
   * @param rdc
   * @return this
   */
  public RadianceRGB add (final RadianceRGB rdc) {
    this.r += rdc.r;
    this.g += rdc.g;
    this.b += rdc.b;
    return this;
  }
  
  /**
   * Multiplica los valores de radiancia por el escalar y acumula. 
   *
   * @param k
   * @param rdc
   * @return this
   */
  public RadianceRGB add (final float k, final RadianceRGB rdc) {
    this.r += k * rdc.r;
    this.g += k * rdc.g;
    this.b += k * rdc.b;
    return this;
  }
  
  /**
   * Acumula valores de radiancia/irradiancia.
   *
   * @param rdc
   * @return this
   */
  public RadianceRGB sub (final RadianceRGB rdc) {
    return new RadianceRGB(this.r - rdc.r, this.g - rdc.g, this.b - rdc.b);
  }  
  
  public boolean isNegligible () {
    return (Math.signum(r + g + b - NEGLIGIBLE_RADIANCE) <= 0);
  }
  
  /**
   * @return @see Object#toString()
   */
  @Override
  public String toString() {
    return "radiancia: {" + this.r + ", " + this.g + ", " + this.b + "}";
  }
  
  public Color getColor () {

    return getColor(RADIANCE_TO_XYZD50);

  }
  
  private Color getColor (final Matrix3f toXYZ) {

    final float X = Math.max(0.0f, //Math.min(1.0f,
            Math.fma(toXYZ.m00, r, sop(toXYZ.m01, g, toXYZ.m02, b)));
    final float Y = Math.max(0.0f, //Math.min(1.0f,
            Math.fma(toXYZ.m10, r, sop(toXYZ.m11, g, toXYZ.m12, b)));
    final float Z = Math.max(0.0f, //Math.min(1.0f,
            Math.fma(toXYZ.m20, r, sop(toXYZ.m21, g, toXYZ.m22, b)));
    
    //System.out.println(X + " " + Y + " " + Z);
    final float[] rgb = PCS.fromCIEXYZ(new float[] { X, Y, Z });

    return new Color(rgb[0], rgb[1], rgb[2]);

  }   
  
}