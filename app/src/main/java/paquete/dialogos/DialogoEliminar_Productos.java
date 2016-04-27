package paquete.dialogos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import paquete.database.DBAdapter;
import paquete.global.Funciones;
import paquete.recycle_bitmap.RecyclingImageView;
import paquete.tufanoapp.FragmentPedido_Muestrario;
import paquete.tufanoapp.R;

/**
 * Desarrollado por Gerson el 14/4/2015.
 */
@SuppressWarnings("SameParameterValue")
public class DialogoEliminar_Productos extends DialogFragment
{
    private Context contexto;
    private TableLayout tabla, cabecera;
    private TableRow.LayoutParams layoutFila, layout_producto, layout_eliminar;
    private View rootView;
    public static int num_checks;
    private Resources rs;
    public static ArrayList<String> ids_eliminar;
    private String id_vendedor;
    private DBAdapter manager;
    private checkBoxClass clase_checks;

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        Log.v("onCreateDialog","INIT");

        num_checks=0;
        contexto = getActivity();
        manager = new DBAdapter(contexto);

        rootView = inflater.inflate(R.layout.dialogo_eliminar_productos, null);
        clase_checks = new checkBoxClass();

        getExtrasVar();
        Dialog dialog = initDialog();
        generarTabla();

        return dialog;
    }

    /**
     * Inicializa el dialogo
     * @return
     */
    private Dialog initDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView)
                .setPositiveButton(R.string.eliminar_productos, null)
                .setTitle(R.string.seleccione_productos)
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogoEliminar_Productos.this.getDialog().cancel();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();


        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                //Editable value = email_text.getText();
                if( num_checks>0 )
                {
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(contexto);

                    String mensaje = contexto.getResources().getString(R.string.confirmacion_eliminar_producto);
                    mensaje = mensaje.replace("%",""+num_checks);

                    dialogo.setMessage(mensaje)
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                            .setTitle(R.string.pedido_btn_2);

                    dialogo.setPositiveButton("Eliminar Productos", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogo2, int which)
                        {
                            for (int i = 0; i < ids_eliminar.size(); i++)
                            {
                                Log.i("Ids a eliminar",ids_eliminar.get(i));
                                //DBAdapter manager = new DBAdapter(contexto);
                                manager.eliminar_producto_pedidoByID(ids_eliminar.get(i));
                                //manager.cerrar();

                            }
                            Toast.makeText(getActivity(), ids_eliminar.size()+" Productos eliminados exitosamente!", Toast.LENGTH_LONG).show();

                            //dialogo.dismiss();
                            dialog.dismiss();

                            String TAG = "fragment_pedido";
                            Fragment fragment = new FragmentPedido_Muestrario();
                            Bundle args = new Bundle();
                            args.putString("id_usuario",id_vendedor);
                            fragment.setArguments(args);

                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();

                        }
                    });

                    dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });

                    dialogo.show();
                }
                else
                {
                    //Log.i("PRUEBA","Email: "+value);
                    Toast.makeText(getActivity(), "Por favor seleccione al menos un producto!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return dialog;
    }

    /**
     * Obtiene las variables que fueron pasados como parametros desde otro activity
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        id_vendedor = bundle.getString("id_vendedor");
    }

    /**
     * Funcion base que prepara la generacion de la tabla,
     */
    private void generarTabla()
    {
        rs = this.getResources();
        tabla = (TableLayout) rootView.findViewById(R.id.tableLayout_Contenido);
        cabecera = (TableLayout) rootView.findViewById(R.id.tableLayout_Cabecera);
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        layout_producto = new TableRow.LayoutParams(200,TableRow.LayoutParams.MATCH_PARENT);
        layout_eliminar = new TableRow.LayoutParams(65,TableRow.LayoutParams.MATCH_PARENT);

        new cargar_data_pedido().execute();

    }

    /**
     * Genera la cabecera de la tabla en un hilo.
     */
    private void agregarCabecera()
    {
        final Thread hilo_base = new Thread()
        {
            @Override
            public void run()
            {
                synchronized (this)
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void run()
                        {
                            TableRow fila;
                            TextView txtProducto;
                            LinearLayout vertical_layout2;
                            final CheckBox Eliminar;

                            fila = new TableRow(contexto);
                            fila.setLayoutParams(layoutFila);

                            txtProducto = new TextView(contexto);
                            Eliminar = new CheckBox(contexto);

                            txtProducto.setText(rs.getString(R.string.producto_pedido));
                            txtProducto.setGravity(Gravity.CENTER);

                            if (Build.VERSION.SDK_INT < 23)
                            {
                                txtProducto.setTextAppearance(contexto, R.style.etiqueta);
                            }
                            else
                            {
                                txtProducto.setTextAppearance(R.style.etiqueta);
                            }

                            //txtProducto.setTextAppearance(contexto, R.style.etiqueta);
                            txtProducto.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtProducto.setLayoutParams(layout_producto);

                            //Eliminar.setLayoutParams(layout_eliminar);
                            //Eliminar.setGravity(Gravity.CENTER);
                            //Eliminar.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            Eliminar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Eliminar.isChecked()) {
                                        checkBoxClass.seleccionarTodos();
                                    } else {
                                        checkBoxClass.limpiarTodos();
                                    }
                                }
                            });

                            vertical_layout2 = new LinearLayout(contexto);
                            vertical_layout2.setLayoutParams(layout_eliminar);
                            vertical_layout2.setGravity(Gravity.CENTER);
                            vertical_layout2.addView(Eliminar);
                            vertical_layout2.setBackgroundResource(R.drawable.tabla_celda_cabecera);

                            /*Eliminar.setText(rs.getString(R.string.eliminar_producto));
                            Eliminar.setGravity(Gravity.CENTER_HORIZONTAL);
                            Eliminar.setTextAppearance(contexto, R.style.etiqueta);
                            Eliminar.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            Eliminar.setLayoutParams(layout_eliminar);*/

                            fila.addView(txtProducto);
                            fila.addView(vertical_layout2);
                            cabecera.addView(fila);
                        }
                    });

                }
            }
        };
        hilo_base.start();
    }

    /**
     * Agrega el contenido de la tabla en un hilo.
     */
    private void agregarFilas()
    {
        int y = 0;

        //DBAdapter manager = new DBAdapter(contexto);
        Cursor cursor = manager.cargarCursorProductosPedidos();

        final String[] nombre_producto = new String[cursor.getCount()];
        final String[] nombre_producto_real = new String[cursor.getCount()];
        final String[] ids = new String[cursor.getCount()];
        ids_eliminar = new ArrayList<>(cursor.getCount());

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            nombre_producto[y] = cursor.getString(0);
            ids[y] = cursor.getString(5);

            Cursor cursor2 = manager.buscarProducto(ids[y]);
            for(cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                nombre_producto_real[y] = cursor2.getString(1);
            }

            y++;
        }

        //manager.cerrar();
        cursor.close();

        for(int i = 0;i< nombre_producto.length;i++)
        {
            final int finalI = i;
            final Thread hilo_base = new Thread()
            {
                @Override
                public void run()
                {
                    synchronized (this)
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @SuppressWarnings("deprecation")
                            @Override
                            public void run()
                            {
                                TableRow fila;
                                RecyclingImageView imageView1;
                                LinearLayout vertical_layout, vertical_layout2;
                                TextView txtId;
                                final CheckBox cb;

                                fila = new TableRow(contexto);
                                fila.setLayoutParams(layoutFila);

                                /*
                                    Columna 1: Productos
                                 */

                                imageView1 = new RecyclingImageView(contexto);


                                final File file = new File(getActivity().getFilesDir(), nombre_producto_real[finalI] + "_01.jpg");

                                if (file.exists())
                                {
                                    imageView1.setImageBitmap(Funciones.decodeSampledBitmapFromResource(file, 180, 150));
                                }

                                //imageView1.setImageResource(android.R.drawable.ic_menu_gallery);
                                //layout_producto = new TableRow.LayoutParams(180,150);
                                imageView1.setLayoutParams(new TableRow.LayoutParams(180, 150));
                                imageView1.setBackgroundColor(Color.WHITE);

                                txtId = new TextView(contexto);


                                txtId.setText(nombre_producto[finalI]);
                                txtId.setGravity(Gravity.CENTER_HORIZONTAL);

                                if (Build.VERSION.SDK_INT < 23)
                                {
                                    txtId.setTextAppearance(contexto, R.style.etiqueta);
                                }
                                else
                                {
                                    txtId.setTextAppearance(R.style.etiqueta);
                                }

                                //txtId.setTextAppearance(contexto, R.style.etiqueta);
                                txtId.setBackgroundColor(Color.WHITE);
                                //layout_producto = new TableRow.LayoutParams(180,30);
                                txtId.setLayoutParams(new TableRow.LayoutParams(180,30));

                                vertical_layout = new LinearLayout(contexto);
                                vertical_layout.setOrientation(LinearLayout.VERTICAL);
                                vertical_layout.addView(imageView1);
                                vertical_layout.addView(txtId);
                                vertical_layout.setGravity(Gravity.CENTER);
                                vertical_layout.setBackgroundResource(R.drawable.tabla_celda);

                               /*
                                    Columna 2: Eliminar
                                */

                                cb = new CheckBox(contexto);
                                clase_checks.agregar(cb, ids[finalI]);

                                //cb.setGravity(Gravity.CENTER);
                                //cb.setLayoutParams(layout_eliminar);
                                //cb.setBackgroundResource(R.drawable.tabla_celda);

                                cb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (cb.isChecked())
                                        {
                                            num_checks++;
                                            ids_eliminar.add(ids[finalI]);
                                        }
                                        else
                                        {
                                            num_checks--;
                                            ids_eliminar.remove(ids[finalI]);
                                        }
                                        Log.i("CheckBox onClick", "CLICK ID:" + ids[finalI]);
                                    }
                                });

                                vertical_layout2 = new LinearLayout(contexto);
                                vertical_layout2.setLayoutParams(layout_eliminar);
                                vertical_layout2.setGravity(Gravity.CENTER);
                                vertical_layout2.addView(cb);
                                vertical_layout2.setBackgroundResource(R.drawable.tabla_celda);

                                fila.addView(vertical_layout);
                                fila.addView(vertical_layout2);

                                tabla.addView(fila);
                            }
                        });

                    }
                }
            };
            hilo_base.start();
        }


    }

    /**
     * Carga la data del pedido en segundo plano.
     */
    private class cargar_data_pedido extends AsyncTask<String, Void, String>
    {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Cargando Productos...");
            pDialog.setMessage("Cargando Datos...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls)
        {
            agregarCabecera();
            agregarFilas();
            return "OK";
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
        }
    }
}