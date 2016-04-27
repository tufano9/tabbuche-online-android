package paquete.database.tables;

/**
 * Desarrollado por Gerson el 6/4/2016.
 * <p/>
 * Clase que contiene los campos necesarios para la tabla "Actualizaciones_Generales".
 * <p/>
 * Esta tabla se encarga de almacenar las fechas de ultimas actualizaciones globales, para
 * posteriormente poder comparar estas fechas con las fechas almacenadas en la BD. Local y
 * de esta forma poder conocer si alguna tabla fue modificada (Aun no se cual pero se que
 * hay una que fue modificada para proceder a su actualizacion).
 *
 * @author Gerson Figuera.C
 */
public class ActualizacionesGenerales
{
    public static final String TABLA_ACTUALIZACIONES_GENERALES             = "actualizaciones_generales";
    public static final String CN_ID_ACTUALIZACION_GENERALES               = "_id";
    public static final String CN_FECHA_ACTUALIZACION_GENERALES_LINEAS     = "fecha_ultima_actualizacion_lineas";
    public static final String CN_FECHA_ACTUALIZACION_GENERALES_MODELOS    = "fecha_ultima_actualizacion_modelos";
    public static final String CN_FECHA_ACTUALIZACION_GENERALES_PRODUCTOS  = "fecha_ultima_actualizacion_productos";
    public static final String CN_FECHA_ACTUALIZACION_GENERALES_MATERIALES = "fecha_ultima_actualizacion_materiales";
    public static final String CN_FECHA_ACTUALIZACION_GENERALES_COLORES    = "fecha_ultima_actualizacion_colores";
    public static final String CN_FECHA_ACTUALIZACION_GENERALES_BULTOS     = "fecha_ultima_actualizacion_bultos";
}