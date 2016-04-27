package paquete.database.tables;

/**
 * Desarrollado por Gerson el 6/4/2016.
 *
 * Clase que contiene los campos necesarios para la tabla "Actualizaciones_Modelo".
 *
 * Esta tabla se encarga de almacenar las fechas de ultimas actualizaciones de cada modelo, para
 * posteriormente poder comparar estas fechas con las fechas almacenadas en la BD. Local y
 * de esta forma poder conocer si algun modelo fue modificado para proceder a su actualizacion).
 *
 * @author Gerson Figuera.C
 */
public class ActualizacionesModelos
{
    public static final String TABLA_ACTUALIZACIONES_MODELOS = "actualizaciones_modelos";
    public static final String CN_ID_ACTUALIZACION_MODELOS = "_id";
    public static final String CN_ID_MODELO_ACTUALIZACION_MODELOS = "id_modelo";
    public static final String CN_FECHA_ACTUALIZACION_MODELOS = "fecha_ultima_actualizacion";
}