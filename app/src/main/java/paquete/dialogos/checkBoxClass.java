package paquete.dialogos;

import android.util.Log;
import android.widget.CheckBox;

import java.util.ArrayList;

/**
 * Desarrollado por Gerson el 1/6/2015.
 * <p/>
 * Clase de apoyo de checkBoxs multiples. Para seleccionar varios con presionar uno solo, o
 * des-seleccionar.
 */
class checkBoxClass
{
    private static ArrayList<CheckBox> array_c;
    private static ArrayList<String>   array_ids;

    /**
     * Constructor de la clase por defecto.
     */
    public checkBoxClass()
    {
        array_c = new ArrayList<>();
        array_ids = new ArrayList<>();
    }

    /**
     * Selecciona todos los checkbox
     */
    public static void seleccionarTodos()
    {
        Log.i("checkBoxClass", "seleccionarTodos");
        for (int i = 0; i < array_c.size(); i++)
        {
            array_c.get(i).setChecked(true);
            DialogoEliminar_Productos.ids_eliminar.add(array_ids.get(i));
        }
        DialogoEliminar_Productos.num_checks = array_c.size();
    }

    /**
     * Des-Selecciona los checkBoxs
     */
    public static void limpiarTodos()
    {
        Log.i("checkBoxClass", "limpiarTodos");
        for (int i = 0; i < array_c.size(); i++)
        {
            array_c.get(i).setChecked(false);
        }
        DialogoEliminar_Productos.ids_eliminar.clear();
        DialogoEliminar_Productos.num_checks = 0;
    }

    /**
     * Agrega el checkbox a la lista junto con un id para su identificacion.
     *
     * @param c  CheckBox a agregar.
     * @param id ID para identificar el checkbox.
     */
    public void agregar(CheckBox c, String id)
    {
        Log.i("checkBoxClass", "agregar (id:" + id + ")");
        array_c.add(c);
        array_ids.add(id);
    }
}