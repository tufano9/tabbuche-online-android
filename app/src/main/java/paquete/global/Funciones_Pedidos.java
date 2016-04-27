package paquete.global;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.devspark.robototextview.util.RobotoTextViewUtils;
import com.devspark.robototextview.widget.RobotoTextView;

import paquete.tufanoapp.R;

/**
 * Desarrollado por Gerson el 23/6/2015.
 */
public class Funciones_Pedidos
{
    /**
     * Genera una cabecera de una tabla con la numeracion.
     *
     * @param numeracion Numeracion para rellenar la tabla.
     * @param contexto   Contexto de la actividad.
     * @param typeface   Estilo de la Fuente.
     * @return Cabecera generada.
     */
    public static TableLayout agregarCabecera_numeracion(String numeracion, Context contexto, Typeface typeface)
    {
        TableRow       fila;
        RobotoTextView txtNombre;

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

        TableLayout cabecera_numeraciones = new TableLayout(contexto);
        cabecera_numeraciones.setLayoutParams(tableParams);

        TableRow.LayoutParams layout_numeracion_cont = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        layout_numeracion_cont.weight = 5f;
        TableRow.LayoutParams layoutFila_numeracion = new TableRow.LayoutParams(450, TableRow.LayoutParams.WRAP_CONTENT);
        fila = new TableRow(contexto);
        fila.setLayoutParams(layoutFila_numeracion);

        String[] valores = numeracion.split(",");
        for (String valore : valores)
        {
            String[] valor = valore.split(":");
            txtNombre = new RobotoTextView(contexto);
            txtNombre.setText(String.valueOf(valor[0]));
            txtNombre.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            RobotoTextViewUtils.setTypeface(txtNombre, typeface);
            txtNombre.setBackgroundResource(R.drawable.tabla_celda_cabecera);
            txtNombre.setLayoutParams(layout_numeracion_cont);

            fila.addView(txtNombre);
        }

        cabecera_numeraciones.addView(fila);
        return cabecera_numeraciones;
    }

    /**
     * Genera una tabla (Contenido) con la numeracion de algun producto.
     *
     * @param numeracion Numeracion para rellenar la tabla.
     * @param contexto   Contexto de la actividad.
     * @param typeface   Estilo de la Fuente.
     * @return Cabecera generada.
     */
    public static TableLayout agregarFilasTabla_numeracion(String numeracion, Context contexto, Typeface typeface)
    {
        TableRow       fila;
        RobotoTextView txtNombre;

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

        TableLayout tabla_numeraciones = new TableLayout(contexto);
        tabla_numeraciones.setLayoutParams(tableParams);

        TableRow.LayoutParams layout_numeracion_cont = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        layout_numeracion_cont.weight = 5f;
        TableRow.LayoutParams layoutFila_numeracion = new TableRow.LayoutParams(450, TableRow.LayoutParams.WRAP_CONTENT);
        fila = new TableRow(contexto);
        fila.setLayoutParams(layoutFila_numeracion);

        String[] valores = numeracion.split(",");

        for (String valore : valores)
        {
            String[] valor = valore.split(":");
            txtNombre = new RobotoTextView(contexto);
            txtNombre.setText(String.valueOf(valor[1]));
            txtNombre.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            RobotoTextViewUtils.setTypeface(txtNombre, typeface);
            txtNombre.setBackgroundResource(R.drawable.tabla_celda);
            txtNombre.setLayoutParams(layout_numeracion_cont);

            fila.addView(txtNombre);
        }
        tabla_numeraciones.addView(fila);
        return tabla_numeraciones;
    }
}