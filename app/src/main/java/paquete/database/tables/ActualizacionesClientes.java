package paquete.database.tables;

/**
 * Desarrollado por Gerson el 6/4/2016.
 * <p/>
 * Clase que contiene los campos necesarios de la tabla "Actualizaciones_Clientes".
 * <p/>
 * Esta tabla se encarga de almacenar las fechas de ultimas actualizaciones de cada cliente, para
 * posteriormente poder comparar estas fechas con las fechas almacenadas en la BD. Local y
 * de esta forma poder conocer si algun cliente fue modificado (Aun no se cual pero se que
 * hay uno que fue modificado para proceder a su actualizacion).
 *
 * @author Gerson Figuera.C
 */
public class ActualizacionesClientes
{
    public static final String TABLA_ACTUALIZACIONES_CLIENTES  = "actualizaciones_clientes";
    public static final String CN_ID_ACTUALIZACION_CLIENTES    = "_id";
    public static final String CN_ID_CLIENTES_ACTUALIZACION    = "id_vendedor";
    public static final String CN_FECHA_ACTUALIZACION_CLIENTES = "fecha_ultima_actualizacion";
}