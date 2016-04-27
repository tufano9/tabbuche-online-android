package paquete.database.tables;

/**
 * Desarrollado por Gerson el 6/4/2016.
 * <p/>
 * Clase que contiene los campos necesarios para la tabla "Actualizaciones_Productos".
 * <p/>
 * Esta tabla se encarga de almacenar las fechas de ultimas actualizaciones de cada producto, para
 * posteriormente poder comparar estas fechas con las fechas almacenadas en la BD. Local y
 * de esta forma poder conocer si algun producto fue modificado para proceder a su actualizacion).
 *
 * @author Gerson Figuera.C
 */
public class ActualizacionesProductos
{
    public static final String TABLA_ACTUALIZACIONES_PRODUCTOS = "actualizaciones_productos";
    public static final String CN_ID_ACTUALIZACION             = "_id_tabla";
    public static final String CN_ID_ACTUALIZACION_PRODUCTO    = "id_producto";
    public static final String CN_FECHA_ACTUALIZACION_PRODUCTO = "fecha_ultima_actualizacion";
    public static final String CN_ACTUALIZACION_IMAGEN_NUMERO  = "imagen_numero";
}