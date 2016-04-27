package paquete.database.tables;

/**
 * Desarrollado por Gerson el 6/4/2016.
 *
 * Clase que contiene los campos necesarios para la tabla "Actualizaciones_Pedidos".
 *
 * Esta tabla se encarga de almacenar las fechas de ultimas actualizaciones de cada pedido, para
 * posteriormente poder comparar estas fechas con las fechas almacenadas en la BD. Local y
 * de esta forma poder conocer si algun pedido fue modificado para proceder a su actualizacion).
 *
 * @author Gerson Figuera.C
 */
public class ActualizacionesPedidos
{
    public static final String TABLA_ACTUALIZACIONES_PEDIDOS = "actualizaciones_pedidos";
    public static final String CN_ID_ACTUALIZACION_PEDIDOS = "_id";
    public static final String CN_ID_PEDIDOS_ACTUALIZACION = "id_vendedor";
    public static final String CN_FECHA_ACTUALIZACION_PEDIDOS = "fecha_ultima_actualizacion";
}
