package paquete.dialogos;

import android.util.Log;
import android.widget.CheckBox;

import java.util.ArrayList;

/**
 * Desarrollado por Gerson el 1/6/2015.
 *
 * Clase para el manejo de checkbox, similar a la clase checkBoxClass pero para otros usos.
 */
class checkBoxClass_Editar
{
    private static ArrayList<CheckBox> array_c;
    private static ArrayList<String> array_ids;

    /**
     * Constructor por defecto.
     */
    public checkBoxClass_Editar()
    {
        array_c = new ArrayList<>();
        array_ids = new ArrayList<>();
    }

    public void agregar(CheckBox c, String id)
    {
        Log.i("checkBoxClass","agregar (id:"+id+")");
        array_c.add(c);
        array_ids.add(id);
    }

    public static void seleccionarTodos()
    {
        Log.i("checkBoxClass","seleccionarTodos");
        for (int i = 0; i < array_c.size(); i++)
        {
            array_c.get(i).setChecked(true);
            DialogoEliminar_Productos_Editar.ids_eliminar.add(array_ids.get(i));
        }
        DialogoEliminar_Productos_Editar.num_checks = array_c.size();
    }

    public static void limpiarTodos()
    {
        Log.i("checkBoxClass","limpiarTodos");
        for (int i = 0; i < array_c.size(); i++)
        {
            array_c.get(i).setChecked(false);
        }
        DialogoEliminar_Productos_Editar.ids_eliminar.clear();
        DialogoEliminar_Productos_Editar.num_checks = 0;
    }
}