package paquete.database.tables;

/**
 * Desarrollado por Gerson el 6/4/2016.
 *
 * Clase que contiene los campos necesarios para la tabla "Actualizaciones_Funciones".
 *
 * Esta tabla se encarga de almacenar las fechas de ultimas actualizaciones de cada funcion, para
 * posteriormente poder comparar estas fechas con las fechas almacenadas en la BD. Local y
 * de esta forma poder conocer si alguna funcion fue habilitada o deshabilitada (Aun no se cual pero
 * se que hay una que fue modificado para proceder a su actualizacion).
 *
 * @author Gerson Figuera.C
 */
public class ActualizacionesFunciones
{
    public static final String TABLA_ACTUALIZACIONES_FUNCIONES = "actualizaciones_funciones";
    public static final String CN_ID_ACTUALIZACION_FUNCIONES = "_id";
    public static final String CN_ID_FUNCIONES_ACTUALIZACION = "id_vendedor";
    public static final String CN_FECHA_ACTUALIZACION_FUNCIONES = "fecha_ultima_actualizacion";
}