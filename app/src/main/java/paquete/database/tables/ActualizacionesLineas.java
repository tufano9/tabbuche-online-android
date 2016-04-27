package paquete.database.tables;

/**
 * Desarrollado por Gerson el 6/4/2016.
 * <p/>
 * Clase que contiene los campos necesarios para la tabla "Actualizaciones_Lineas".
 * <p/>
 * Esta tabla se encarga de almacenar las fechas de ultimas actualizaciones de cada linea, para
 * posteriormente poder comparar estas fechas con las fechas almacenadas en la BD. Local y
 * de esta forma poder conocer si alguna linea fue modificada para proceder a su actualizacion).
 *
 * @author Gerson Figuera.C
 */
public class ActualizacionesLineas
{
    public static final String TABLA_ACTUALIZACIONES_LINEAS     = "actualizaciones_lineas";
    public static final String CN_ID_ACTUALIZACION_LINEAS       = "_id";
    public static final String CN_ID_LINEA_ACTUALIZACION_LINEAS = "id_linea";
    public static final String CN_FECHA_ACTUALIZACION_LINEAS    = "fecha_ultima_actualizacion";
}