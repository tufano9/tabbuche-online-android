package paquete.global;

import android.database.Cursor;

import paquete.database.DBAdapter;

/**
 * Desarrollado por Gerson el 5/5/2016.
 */
public class FuncionesTablas
{
    public static String obtenerTipoTalla(String id_tipo_talla, DBAdapter manager)
    {
        //Log.i(TAG, "Buscando talla con id: " + id_tipo_talla);
        String nombre_talla = null;
        Cursor cursor = manager.buscarTipoTalla_ID(id_tipo_talla);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            nombre_talla = cursor.getString(0);
        }
        cursor.close();
        return nombre_talla;
    }
}