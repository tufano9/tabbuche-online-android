package paquete.database.inserts;

import static paquete.database.tables.ActualizacionesGenerales.TABLA_ACTUALIZACIONES_GENERALES;
import static paquete.database.tables.PrimerLogin.TABLA_PRIMER_LOGIN;

/**
 * Desarrollado por Gerson el 6/4/2016.
 *
 * Clase que agrupa las sentencias de insert por defecto para la BD. Local
 *
 * @author Gerson Figuera.C
 */
public class DefaultInserts
{
    public static final String INSERTAR_TABLA_ACTUALIZACIONES_GENERALES = "INSERT INTO "+TABLA_ACTUALIZACIONES_GENERALES+" VALUES (1,'2000-02-06 16:47:20','2000-02-06 16:47:20','2000-02-06 16:47:20','2000-02-06 16:47:20','2000-02-06 16:47:20','2000-02-06 16:47:20')";
    public static final String INSERTAR_TABLA_PRIMER_LOGIN = "INSERT INTO "+TABLA_PRIMER_LOGIN+" VALUES (0)";
}