package paquete.tufanoapp.pedido;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.robototextview.util.RobotoTextViewUtils;
import com.devspark.robototextview.util.RobotoTypefaceManager;
import com.devspark.robototextview.widget.RobotoButton;
import com.devspark.robototextview.widget.RobotoTextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import paquete.database.DBAdapter;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.global.library.Httppostaux;
import paquete.recycle_bitmap.RecyclingImageView;
import paquete.tufanoapp.R;
import paquete.tufanoapp.sortingHeader;

/**
 * Desarrollado por Gerson el 14/4/2015.
 */
public class FragmentPedido_Consultar extends Fragment
{
    private static DBAdapter      manager;
    private static ProgressDialog pDialog;
    private        String         id_usuario;
    private        TableLayout    tabla, cabecera;
    private TableRow.LayoutParams layoutFila, layout_codigo, layout_cliente, layout_fecha;
    private TableRow.LayoutParams layout_monto, layout_estatus, layout_detalles;
    private Resources rs;
    private Context   contexto;
    private View      rootView;
    private Spinner   sp_clientes, sp_estatus;
    private sortingHeader sort_header;
    private String[]      ids_clientes, razon_social_clientes;

    /**
     * Funcion encargada de enviar los pedidos hechos localmente hacia la BD externa.
     *
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private static boolean enviarPedidoLocales()
    {
        Httppostaux post                = new Httppostaux();
        String      URL_realizar_pedido = Constantes.IP_Server + "realizar_pedido.php";
        int         logstatus, y;

        Cursor            cursor          = manager.cargarCursorPedidos_Locales();
        ArrayList<String> id_pedido_local = new ArrayList<>();
        ArrayList<String> id_vendedor     = new ArrayList<>();
        ArrayList<String> id_cliente      = new ArrayList<>();
        ArrayList<String> monto_total     = new ArrayList<>();
        ArrayList<String> observaciones   = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            //Log.d("PEDIDO ID: "+cursor.getString(0),"id_vendedor: "+cursor.getString(2)+" , id_cliente: "+cursor.getString(3)+" , monto: "+cursor.getString(5)+" , observaciones: "+cursor.getString(7));
            id_pedido_local.add(cursor.getString(0));
            id_vendedor.add(cursor.getString(2));
            id_cliente.add(cursor.getString(3));
            monto_total.add(cursor.getString(5));
            observaciones.add(cursor.getString(7));
        }

        cursor.close();

        for (int i = 0; i < id_pedido_local.size(); i++)
        {
            y = 0;
            String id_producto = "", cantidad_pares = "", cantidad_bultos = "", precio_unitario = "", subtotal = "", numeracion = "";
            Cursor cursor2     = manager.cargarCursorPedidos_Locales_Detalles_ID(id_pedido_local.get(i));

            for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                Log.d("PEDIDO ID: " + cursor2.getString(1), "id_producto: " + cursor2.getString(2) + " , cantidad_pares: " + cursor2.getString(3) + " , cantidad_bultos: " + cursor2.getString(4) + " , numeracion: " + cursor2.getString(5) + " , precio_unitario: " + cursor2.getString(6) + " , subtotal: " + cursor2.getString(7));

                if (y == 0)
                {
                    id_producto = cursor2.getString(2);
                    cantidad_pares = cursor2.getString(3);
                    cantidad_bultos = cursor2.getString(4);
                    numeracion = cursor2.getString(5);
                    precio_unitario = cursor2.getString(6);
                    subtotal = cursor2.getString(7);
                    //subtotal = String.valueOf( Double.parseDouble(cantidad_pares) * Double.parseDouble(cantidad_bultos) * Double.parseDouble(precio_unitario) );
                }
                else
                {
                    id_producto = id_producto + "#" + cursor2.getString(2);
                    cantidad_pares = cantidad_pares + "#" + cursor2.getString(3);
                    cantidad_bultos = cantidad_bultos + "#" + cursor2.getString(4);
                    numeracion = numeracion + "#" + cursor2.getString(5);
                    precio_unitario = precio_unitario + "#" + cursor2.getString(6);
                    subtotal = subtotal + "#" + cursor2.getString(7);
                    //subtotal =  subtotal + "#" + String.valueOf( Double.parseDouble(cursor.getString(4)) * Double.parseDouble(cursor.getString(6)) * Double.parseDouble(cursor.getString(2)) );
                }
                y++;
            }

            cursor2.close();

            // ENVIAR
            ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
            postparameters2send.add(new BasicNameValuePair("id_vendedor", id_vendedor.get(i)));
            postparameters2send.add(new BasicNameValuePair("id_cliente", id_cliente.get(i)));
            postparameters2send.add(new BasicNameValuePair("id_productos", id_producto));
            postparameters2send.add(new BasicNameValuePair("monto", monto_total.get(i)));
            postparameters2send.add(new BasicNameValuePair("observaciones", observaciones.get(i)));
            postparameters2send.add(new BasicNameValuePair("cantidad_pares", cantidad_pares));
            postparameters2send.add(new BasicNameValuePair("cantidad_bultos", cantidad_bultos));
            postparameters2send.add(new BasicNameValuePair("precio_unitario", precio_unitario));
            postparameters2send.add(new BasicNameValuePair("subtotal", subtotal));
            postparameters2send.add(new BasicNameValuePair("numeraciones", numeracion));

            JSONArray jdata = post.getserverdata(postparameters2send, URL_realizar_pedido);

            if (jdata != null && jdata.length() > 0)
            {
                JSONObject json_data;
                try
                {
                    json_data = jdata.getJSONObject(0);
                    logstatus = json_data.getInt("logstatus");
                    String id_pedido     = json_data.getString("id_pedido");
                    String fecha_ingreso = json_data.getString("fecha_ingreso");
                    String codigo_pedido = json_data.getString("codigo_pedido");

                    if (logstatus == 1)
                    {
                        // Operacion exitosa
                        manager.insertar_pedido(id_pedido, id_vendedor.get(i), id_cliente.get(i), fecha_ingreso, Double.parseDouble(monto_total.get(i)), "En espera de aprobacion", observaciones.get(i), codigo_pedido);
                        manager.insertar_pedido_detalles(id_pedido, id_producto, cantidad_pares, cantidad_bultos, numeracion, precio_unitario, subtotal);
                        manager.actualizar_actualizacionesPedido(id_vendedor.get(i), fecha_ingreso);
                        manager.eliminar_pedidos_locales_ID(id_pedido_local.get(i));
                        pDialog.incrementProgressBy(1);
                        Log.d("PEDIDO", "ENVIADO");
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.e("JSON  ", "ERROR");
                return false;
            }
        }

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_pedido_consultar, container, false);
        contexto = getActivity();
        manager = new DBAdapter(contexto);
        sort_header = new sortingHeader();
        rs = this.getResources();

        getExtrasVar();
        initComponents();
        initButtons();
        cargarClientesxVendedor();
        initSpinners();

        new cargar_data().execute();

        return rootView;
    }

    /**
     * Carga los clientes que tiene cada vendedor (El que esta definido por el ID result
     */
    private void cargarClientesxVendedor()
    {
        Cursor cursor = manager.cargarCursorClientes_Vendedor(id_usuario);
        razon_social_clientes = new String[cursor.getCount() + 1];
        ids_clientes = new String[cursor.getCount() + 1];
        razon_social_clientes[0] = "Todos";
        ids_clientes[0] = "0";
        int x = 1;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            razon_social_clientes[x] = cursor.getString(0);
            ids_clientes[x] = cursor.getString(1);
            x++;
        }
        cursor.close();
    }

    /**
     * Inicializar los Spinners.
     */
    private void initSpinners()
    {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(contexto, android.R.layout.simple_spinner_item, razon_social_clientes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_clientes.setAdapter(dataAdapter);
        sp_clientes.setSelection(0, false);

        sp_clientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //Log.d("onItemClick sp_clientes", "Cliente : " + parent.getItemAtPosition(position) + ", Id: " + ids_clientes[position]);
                tabla.removeAllViews();
                agregarFilasTabla(ids_clientes[position], String.valueOf(sp_estatus.getSelectedItem()), "ASC", "fecha");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(contexto, R.array.estatus_lista, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_estatus.setAdapter(adapter);
        sp_estatus.setSelection(0, false);

        sp_estatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //Log.d("onItemClick sp_estatus", "Estatus: " + parent.getItemAtPosition(position));
                tabla.removeAllViews();
                agregarFilasTabla(ids_clientes[sp_clientes.getSelectedItemPosition()], String.valueOf(parent.getItemAtPosition(position)), "DESC", "fecha");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    /**
     * Inicializar los botones
     */
    private void initButtons()
    {
        RobotoButton btn = (RobotoButton) rootView.findViewById(R.id.btn_enviar_pendientes);

        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Cursor cursor  = manager.cargarCursorPedidos_Locales();
                int    pedidos = cursor.getCount();
                cursor.close();

                if (pedidos > 0)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(contexto);

                    dialog.setMessage(R.string.confirmacion_enviar_pedido)
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_menu_send)
                            .setTitle(R.string.pedido_btn_5);

                    dialog.setPositiveButton("Enviar pedidos", new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (Funciones.isOnline(contexto))
                                new enviarPedido().execute();
                            else
                                Toast.makeText(contexto, Constantes.NO_INTERNET, Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
                else
                    Toast.makeText(contexto, "No hay pedidos por enviar!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Inicializa los componentes basicos de la clase.
     */
    private void initComponents()
    {
        tabla = (TableLayout) rootView.findViewById(R.id.tableLayout_Contenido);
        cabecera = (TableLayout) rootView.findViewById(R.id.tableLayout_Cabecera);

        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        layout_codigo = new TableRow.LayoutParams(150, TableRow.LayoutParams.MATCH_PARENT);
        layout_cliente = new TableRow.LayoutParams(300, TableRow.LayoutParams.MATCH_PARENT);
        layout_fecha = new TableRow.LayoutParams(180, TableRow.LayoutParams.MATCH_PARENT);
        layout_monto = new TableRow.LayoutParams(130, TableRow.LayoutParams.MATCH_PARENT);
        layout_estatus = new TableRow.LayoutParams(130, TableRow.LayoutParams.MATCH_PARENT);
        layout_detalles = new TableRow.LayoutParams(130, TableRow.LayoutParams.MATCH_PARENT);

        sp_clientes = (Spinner) rootView.findViewById(R.id.sp_clientes);
        sp_estatus = (Spinner) rootView.findViewById(R.id.sp_estatus);
    }

    /**
     * Obtiene las variables que fueron pasadas como parametros a traves de otra actividad.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        id_usuario = bundle.getString("id_usuario");
    }

    /**
     * Agrega la cabecera en la tabla de datos.
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
                        @Override
                        public void run()
                        {
                            TableRow       fila        = new TableRow(contexto);
                            RobotoTextView txtCodigo   = new RobotoTextView(contexto);
                            RobotoTextView txtCliente  = new RobotoTextView(contexto);
                            RobotoTextView txtFecha    = new RobotoTextView(contexto);
                            RobotoTextView txtMonto    = new RobotoTextView(contexto);
                            RobotoTextView txtestatus  = new RobotoTextView(contexto);
                            RobotoTextView txtDetalles = new RobotoTextView(contexto);

                            fila.setLayoutParams(layoutFila);

                            Typeface typeface = RobotoTypefaceManager.obtainTypeface(
                                    contexto,
                                    RobotoTypefaceManager.FontFamily.ROBOTO,
                                    RobotoTypefaceManager.TextWeight.BOLD,
                                    RobotoTypefaceManager.TextStyle.NORMAL);

                            RobotoTextViewUtils.setTypeface(txtCodigo, typeface);
                            RobotoTextViewUtils.setTypeface(txtCliente, typeface);
                            RobotoTextViewUtils.setTypeface(txtFecha, typeface);
                            RobotoTextViewUtils.setTypeface(txtMonto, typeface);
                            RobotoTextViewUtils.setTypeface(txtestatus, typeface);
                            RobotoTextViewUtils.setTypeface(txtDetalles, typeface);

                            txtCodigo.setText(rs.getString(R.string.codigo_pedido));
                            txtCodigo.setGravity(Gravity.CENTER_HORIZONTAL);
                            txtCodigo.setTextAppearance(contexto, R.style.etiqueta);
                            //txtCodigo.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtCodigo.setLayoutParams(new TableRow.LayoutParams(130, TableRow.LayoutParams.MATCH_PARENT));

                            final RecyclingImageView imageView1 = new RecyclingImageView(contexto);

                            imageView1.setImageResource(R.drawable.sort_both);
                            imageView1.setLayoutParams(new TableRow.LayoutParams(20, TableRow.LayoutParams.MATCH_PARENT));
                            imageView1.setBackgroundColor(Color.TRANSPARENT);
                            imageView1.setTag("ASC");
                            sort_header.agregar(imageView1);

                            LinearLayout horizontal_layout = new LinearLayout(contexto);
                            horizontal_layout.setOrientation(LinearLayout.HORIZONTAL);
                            horizontal_layout.addView(txtCodigo);
                            horizontal_layout.addView(imageView1);
                            horizontal_layout.setLayoutParams(layout_codigo);
                            horizontal_layout.setBackgroundResource(R.drawable.tabla_celda_cabecera);

                            horizontal_layout.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    if (imageView1.getTag() == "ASC")
                                    {
                                        sort_header.normalHeader();
                                        imageView1.setImageResource(R.drawable.sort_desc);
                                        imageView1.setTag("DESC");
                                        ordenar_Tabla("DESC", "codigo_pedido");
                                        //Ordenare esta columna de forma descendente..
                                    }
                                    else
                                    {
                                        sort_header.normalHeader();
                                        imageView1.setImageResource(R.drawable.sort_asc);
                                        imageView1.setTag("ASC");
                                        ordenar_Tabla("ASC", "codigo_pedido");
                                        //Ordenare esta columna de forma ascendente..
                                    }
                                }
                            });

                            txtCliente.setText(rs.getString(R.string.cliente));
                            txtCliente.setGravity(Gravity.CENTER_HORIZONTAL);
                            txtCliente.setTextAppearance(contexto, R.style.etiqueta);
                            //txtCliente.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtCliente.setLayoutParams(new TableRow.LayoutParams(280, TableRow.LayoutParams.MATCH_PARENT));

                            final RecyclingImageView imageView2 = new RecyclingImageView(contexto);

                            imageView2.setImageResource(R.drawable.sort_both);
                            imageView2.setLayoutParams(new TableRow.LayoutParams(20, TableRow.LayoutParams.MATCH_PARENT));
                            imageView2.setBackgroundColor(Color.TRANSPARENT);
                            imageView2.setTag("ASC");
                            sort_header.agregar(imageView2);

                            LinearLayout horizontal_layout2 = new LinearLayout(contexto);
                            horizontal_layout2.setOrientation(LinearLayout.HORIZONTAL);
                            horizontal_layout2.addView(txtCliente);
                            horizontal_layout2.addView(imageView2);
                            horizontal_layout2.setLayoutParams(layout_cliente);
                            horizontal_layout2.setBackgroundResource(R.drawable.tabla_celda_cabecera);

                            horizontal_layout2.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    if (imageView2.getTag() == "ASC")
                                    {
                                        sort_header.normalHeader();
                                        imageView2.setImageResource(R.drawable.sort_desc);
                                        imageView2.setTag("DESC");
                                        ordenar_Tabla("DESC", "id_cliente");
                                        //Ordenare esta columna de forma descendente..
                                    }
                                    else
                                    {
                                        sort_header.normalHeader();
                                        imageView2.setImageResource(R.drawable.sort_asc);
                                        imageView2.setTag("ASC");
                                        ordenar_Tabla("ASC", "id_cliente");
                                        //Ordenare esta columna de forma ascendente..
                                    }
                                }
                            });

                            txtFecha.setText(rs.getString(R.string.fecha));
                            txtFecha.setGravity(Gravity.CENTER_HORIZONTAL);
                            txtFecha.setTextAppearance(contexto, R.style.etiqueta);
                            //txtFecha.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtFecha.setLayoutParams(new TableRow.LayoutParams(160, TableRow.LayoutParams.MATCH_PARENT));

                            final RecyclingImageView imageView3 = new RecyclingImageView(contexto);

                            imageView3.setImageResource(R.drawable.sort_desc);
                            imageView3.setLayoutParams(new TableRow.LayoutParams(20, TableRow.LayoutParams.MATCH_PARENT));
                            imageView3.setBackgroundColor(Color.TRANSPARENT);
                            imageView3.setTag("DESC");
                            sort_header.agregar(imageView3);

                            LinearLayout horizontal_layout3 = new LinearLayout(contexto);
                            horizontal_layout3.setOrientation(LinearLayout.HORIZONTAL);
                            horizontal_layout3.addView(txtFecha);
                            horizontal_layout3.addView(imageView3);
                            horizontal_layout3.setLayoutParams(layout_fecha);
                            horizontal_layout3.setBackgroundResource(R.drawable.tabla_celda_cabecera);

                            horizontal_layout3.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    if (imageView3.getTag() == "ASC")
                                    {
                                        sort_header.normalHeader();
                                        imageView3.setImageResource(R.drawable.sort_desc);
                                        imageView3.setTag("DESC");
                                        ordenar_Tabla("DESC", "fecha");
                                        //Ordenare esta columna de forma descendente..
                                    }
                                    else
                                    {
                                        sort_header.normalHeader();
                                        imageView3.setImageResource(R.drawable.sort_asc);
                                        imageView3.setTag("ASC");
                                        ordenar_Tabla("ASC", "fecha");
                                        //Ordenare esta columna de forma ascendente..
                                    }
                                }
                            });

                            txtMonto.setText(rs.getString(R.string.monto));
                            txtMonto.setGravity(Gravity.CENTER_HORIZONTAL);
                            txtMonto.setTextAppearance(contexto, R.style.etiqueta);
                            //txtMonto.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtMonto.setLayoutParams(new TableRow.LayoutParams(110, TableRow.LayoutParams.MATCH_PARENT));

                            final RecyclingImageView imageView4 = new RecyclingImageView(contexto);

                            imageView4.setImageResource(R.drawable.sort_both);
                            imageView4.setLayoutParams(new TableRow.LayoutParams(20, TableRow.LayoutParams.MATCH_PARENT));
                            imageView4.setBackgroundColor(Color.TRANSPARENT);
                            imageView4.setTag("ASC");
                            sort_header.agregar(imageView4);

                            LinearLayout horizontal_layout4 = new LinearLayout(contexto);
                            horizontal_layout4.setOrientation(LinearLayout.HORIZONTAL);
                            horizontal_layout4.addView(txtMonto);
                            horizontal_layout4.addView(imageView4);
                            horizontal_layout4.setLayoutParams(layout_monto);
                            horizontal_layout4.setBackgroundResource(R.drawable.tabla_celda_cabecera);

                            horizontal_layout4.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    if (imageView4.getTag() == "ASC")
                                    {
                                        sort_header.normalHeader();
                                        imageView4.setImageResource(R.drawable.sort_desc);
                                        imageView4.setTag("DESC");
                                        ordenar_Tabla("DESC", "monto");
                                        //Ordenare esta columna de forma descendente..
                                    }
                                    else
                                    {
                                        sort_header.normalHeader();
                                        imageView4.setImageResource(R.drawable.sort_asc);
                                        imageView4.setTag("ASC");
                                        ordenar_Tabla("ASC", "monto");
                                        //Ordenare esta columna de forma ascendente..
                                    }
                                }
                            });

                            txtestatus.setText(rs.getString(R.string.estatus));
                            txtestatus.setGravity(Gravity.CENTER_HORIZONTAL);
                            txtestatus.setTextAppearance(contexto, R.style.etiqueta);
                            //txtestatus.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtestatus.setLayoutParams(new TableRow.LayoutParams(110, TableRow.LayoutParams.MATCH_PARENT));

                            final RecyclingImageView imageView5 = new RecyclingImageView(contexto);

                            imageView5.setImageResource(R.drawable.sort_both);
                            imageView5.setLayoutParams(new TableRow.LayoutParams(20, TableRow.LayoutParams.MATCH_PARENT));
                            imageView5.setBackgroundColor(Color.TRANSPARENT);
                            imageView5.setTag("ASC");
                            sort_header.agregar(imageView5);

                            LinearLayout horizontal_layout5 = new LinearLayout(contexto);
                            horizontal_layout5.setOrientation(LinearLayout.HORIZONTAL);
                            horizontal_layout5.addView(txtestatus);
                            horizontal_layout5.addView(imageView5);
                            horizontal_layout5.setLayoutParams(layout_estatus);
                            horizontal_layout5.setBackgroundResource(R.drawable.tabla_celda_cabecera);

                            horizontal_layout5.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    if (imageView5.getTag() == "ASC")
                                    {
                                        sort_header.normalHeader();
                                        imageView5.setImageResource(R.drawable.sort_desc);
                                        imageView5.setTag("DESC");
                                        ordenar_Tabla("DESC", "estatus");
                                        //Ordenare esta columna de forma descendente..
                                    }
                                    else
                                    {
                                        sort_header.normalHeader();
                                        imageView5.setImageResource(R.drawable.sort_asc);
                                        imageView5.setTag("ASC");
                                        ordenar_Tabla("ASC", "estatus");
                                        //Ordenare esta columna de forma ascendente..
                                    }
                                }
                            });

                            txtDetalles.setText(rs.getString(R.string.detalles));
                            txtDetalles.setGravity(Gravity.CENTER_HORIZONTAL);
                            txtDetalles.setTextAppearance(contexto, R.style.etiqueta);
                            txtDetalles.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtDetalles.setLayoutParams(layout_detalles);

                            fila.addView(horizontal_layout);
                            fila.addView(horizontal_layout2);
                            fila.addView(horizontal_layout3);
                            fila.addView(horizontal_layout4);
                            fila.addView(horizontal_layout5);
                            fila.addView(txtDetalles);
                            cabecera.addView(fila);
                        }
                    });

                }
            }
        };
        hilo_base.start();


    }

    /**
     * Ordena la tabla segun los parametros indicados.
     *
     * @param modo    La forma en la cual se ordenara la tabla (ASC o DESC)
     * @param columna La columna a la cual se le aplicara el orden..
     */
    private void ordenar_Tabla(String modo, String columna)
    {
        Log.i("ordenar_Tabla", "Ordenando " + columna + " de forma " + modo);
        tabla.removeAllViews();
        agregarFilasTabla(ids_clientes[sp_clientes.getSelectedItemPosition()], sp_estatus.getSelectedItem().toString(), modo, columna);
    }

    /**
     * Agrega la informacion en la tabla.
     *
     * @param buscar_cliente_id ID del cliente.
     * @param buscar_estatus    Estatus.
     * @param modo              Modo de ordenacion.
     * @param columna           Columna a ordenar.
     */
    private void agregarFilasTabla(String buscar_cliente_id, String buscar_estatus, String modo, String columna)
    {
        Log.d("agregarFilasTabla", "Cliente: " + buscar_cliente_id + ", estatus: " + buscar_estatus + ", modo: " + modo + ", columna: " + columna);
        int    y        = 0, locales, externos, totales;
        Cursor cursor0  = manager.cargarCursorPedidosByID_Cliente(id_usuario, buscar_cliente_id, buscar_estatus, modo, columna);
        Cursor cursor00 = manager.cargarCursorPedidos_Locales_ByID_Cliente(id_usuario, buscar_cliente_id, buscar_estatus, modo, columna);
        locales = cursor00.getCount();
        externos = cursor0.getCount();
        totales = locales + externos;

        final String[] ids_pedidos  = new String[totales];
        final String[] ids_vendedor = new String[totales];
        final String[] ids_cliente  = new String[totales];
        final String[] fecha        = new String[totales];
        final String[] monto        = new String[totales];
        final String[] estatus      = new String[totales];
        final String[] codigos      = new String[totales];
        //final String[] observaciones = new String[ totales ];
        Log.d("Totales", "Pedidos_Locales: " + cursor00.getCount() + ", Pedidos_Externos: " + cursor0.getCount());

        if (totales == 0)
        {
            // No hay pedidos con dichos filtros.. (Mostrar mensaje)

            final TextView txt = new TextView(contexto);
            txt.setText(R.string.sinPedidosEncontrados);
            txt.setGravity(Gravity.CENTER);
            //txt.setTextAppearance(contexto, R.style.texto_galeria_1);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            {
                //noinspection deprecation
                txt.setTextAppearance(contexto, R.style.texto_galeria_1);
            }
            else
            {
                txt.setTextAppearance(R.style.texto_galeria_1);
            }

            final Thread hilo1 = new Thread()
            {
                @Override
                public void run()
                {
                    synchronized (this)
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                txt.setLayoutParams(layout);
                                //LinearLayout linear = (LinearLayout) rootView.findViewById(R.id.tableLayout_Cabecera);
                                //linear.removeAllViews();

                                LinearLayout linear = (LinearLayout) rootView.findViewById(R.id.tableLayout_Contenido);
                                linear.removeAllViews();
                                linear.addView(txt, layout);
                            }
                        });
                    }
                }
            };
            hilo1.start();

        }
        else
        {
            for (cursor00.moveToFirst(); !cursor00.isAfterLast(); cursor00.moveToNext())
            {
                ids_pedidos[y] = cursor00.getString(8);
                ids_cliente[y] = cursor00.getString(2);
                ids_vendedor[y] = cursor00.getString(1);

                Date date = null;

                try
                {
                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(cursor00.getString(3));
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }

                String newstring = new SimpleDateFormat("dd-MM-yyyy h:mm a", Locale.US).format(date);
                fecha[y] = newstring;

                DecimalFormat df2    = new DecimalFormat("###,###.##");
                String        output = df2.format(Double.parseDouble(cursor00.getString(4)));

                monto[y] = output;
                estatus[y] = cursor00.getString(5);
                codigos[y] = cursor00.getString(7);
                y++;
            }

            cursor00.close();

            for (cursor0.moveToFirst(); !cursor0.isAfterLast(); cursor0.moveToNext())
            {
                //Log.i("Encontrado", cursor0.getString(0) + "  " + cursor0.getString(1) + "  " + cursor0.getString(2) + "  " + cursor0.getString(3) + "  " + cursor0.getString(4) + "  " + cursor0.getString(5) + "  " + cursor0.getString(6));
                //Log.d("IDS", cursor0.getString(8));
                ids_pedidos[y] = cursor0.getString(0);
                //ids_pedidos[y] = cursor0.getString(8);
                //ids_vendedor[y] = cursor0.getString(1);
                ids_cliente[y] = cursor0.getString(2);
                ids_vendedor[y] = cursor0.getString(1);

                Date date = null;
                try
                {
                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(cursor0.getString(3));
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
                String newstring = new SimpleDateFormat("dd-MM-yyyy h:mm a", Locale.US).format(date);
                fecha[y] = newstring;

                DecimalFormat df2    = new DecimalFormat("###,###.##");
                String        output = df2.format(Double.parseDouble(cursor0.getString(4)));

                //DecimalFormat myFormatter = new DecimalFormat("###,###.###");
                //String output = myFormatter.format(cursor0.getString(4));

                monto[y] = output;
                estatus[y] = cursor0.getString(5);
                codigos[y] = cursor0.getString(7);
                y++;
            }

            cursor0.close();

            for (int i = 0; i < ids_pedidos.length; i++)
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
                                @Override
                                public void run()
                                {
                                    Button   btnDetalles = new Button(contexto);
                                    TableRow fila        = new TableRow(contexto);
                                    fila.setLayoutParams(layoutFila);

                                    RobotoTextView txtCodigo  = new RobotoTextView(contexto);
                                    RobotoTextView txtCliente = new RobotoTextView(contexto);
                                    RobotoTextView txtFecha   = new RobotoTextView(contexto);
                                    RobotoTextView txtMonto   = new RobotoTextView(contexto);
                                    RobotoTextView txtEstatus = new RobotoTextView(contexto);

                                    Typeface typeface = RobotoTypefaceManager.obtainTypeface(
                                            contexto,
                                            RobotoTypefaceManager.FontFamily.ROBOTO,
                                            RobotoTypefaceManager.TextWeight.NORMAL,
                                            RobotoTypefaceManager.TextStyle.NORMAL);

                                    RobotoTextViewUtils.setTypeface(txtCodigo, typeface);
                                    RobotoTextViewUtils.setTypeface(txtCliente, typeface);
                                    RobotoTextViewUtils.setTypeface(txtFecha, typeface);
                                    RobotoTextViewUtils.setTypeface(txtMonto, typeface);
                                    RobotoTextViewUtils.setTypeface(txtEstatus, typeface);
                                    fila.setLayoutParams(layoutFila);

                                /*
                                    Columna 0: Codigo Pedido
                                 */

                                    txtCodigo.setText(codigos[finalI]);
                                    txtCodigo.setGravity(Gravity.CENTER);
                                    //txtCodigo.setTextAppearance(contexto, R.style.etiqueta);
                                    txtCodigo.setBackgroundColor(Color.WHITE);
                                    txtCodigo.setLayoutParams(layout_codigo);
                                    txtCodigo.setBackgroundResource(R.drawable.tabla_celda);


                                /*
                                    Columna 1: Cliente
                                 */

                                    String nombre_cliente = "";
                                    Cursor cursor0        = manager.buscarClienteByID(ids_cliente[finalI]);

                                    for (cursor0.moveToFirst(); !cursor0.isAfterLast(); cursor0.moveToNext())
                                    {
                                        nombre_cliente = cursor0.getString(0);
                                    }
                                    cursor0.close();

                                    txtCliente.setText(nombre_cliente);
                                    txtCliente.setGravity(Gravity.CENTER);
                                    //txtCliente.setTextAppearance(contexto, R.style.etiqueta);
                                    txtCliente.setBackgroundColor(Color.WHITE);
                                    txtCliente.setLayoutParams(layout_cliente);
                                    txtCliente.setBackgroundResource(R.drawable.tabla_celda);

                               /*
                                    Columna 2: Fecha
                                */

                                    txtFecha.setText(fecha[finalI]);
                                    txtFecha.setLayoutParams(layout_fecha);
                                    txtFecha.setGravity(Gravity.CENTER);
                                    txtFecha.setBackgroundResource(R.drawable.tabla_celda);

                                /*
                                    Columna 3: Monto
                                */

                                    String mon = monto[finalI] + " " + Constantes.MONEDA;
                                    txtMonto.setText(mon);
                                    txtMonto.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                                    //txtMonto.setTextAppearance(contexto, R.style.etiqueta);
                                    txtMonto.setBackgroundResource(R.drawable.tabla_celda);
                                    txtMonto.setLayoutParams(layout_monto);

                                /*
                                    Columna 4: Estatus
                                */

                                    txtEstatus.setText(estatus[finalI]);
                                    txtEstatus.setGravity(Gravity.CENTER);
                                    txtEstatus.setBackgroundResource(R.drawable.tabla_celda);
                                    txtEstatus.setLayoutParams(layout_estatus);

                                /*
                                    Columna 5: Detalles
                                */
                                    btnDetalles.setText(R.string.verDetalles);
                                    btnDetalles.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                                    {
                                        //noinspection deprecation
                                        btnDetalles.setTextAppearance(contexto, R.style.etiqueta_link);
                                    }
                                    else
                                    {
                                        btnDetalles.setTextAppearance(R.style.etiqueta_link);
                                    }

                                    //btnDetalles.setTextAppearance(contexto, R.style.etiqueta_link);
                                    btnDetalles.setBackgroundResource(R.drawable.tabla_celda); //tabla_celda
                                    btnDetalles.setLayoutParams(layout_detalles);

                                    btnDetalles.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {

                                            Log.d("ID CLICK", ids_pedidos[finalI]);

                                            String   TAG      = "fragment_pedido_seleccionar_cliente";
                                            Fragment fragment = new FragmentPedido_Pre_Editar();

                                            Bundle bundle = getArguments();
                                            String ci     = bundle.getString("cedula");

                                            Bundle args = new Bundle();
                                            args.putString("id_pedido", ids_pedidos[finalI]);
                                            args.putString("estatus", estatus[finalI]);
                                            args.putString("cedula", ci);
                                            args.putString("id_vendedor", ids_vendedor[finalI]);
                                            fragment.setArguments(args);

                                            FragmentManager fragmentManager = getFragmentManager();
                                            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();
                                        }
                                    });

                                    fila.addView(txtCodigo);
                                    fila.addView(txtCliente);
                                    fila.addView(txtFecha);
                                    fila.addView(txtMonto);
                                    fila.addView(txtEstatus);
                                    fila.addView(btnDetalles);

                                    tabla.addView(fila);
                                }
                            });

                        }
                    }
                };
                hilo_base.start();
            }
        }
    }

    /**
     * Carga la data del pedido en segundo plano.
     */
    private class cargar_data extends AsyncTask<String, Void, String>
    {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Cargando pedidos realizados...");
            pDialog.setMessage("Por favor, espere...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls)
        {
            //DBAdapter manager0 = new DBAdapter(contexto);
            Cursor  cursor0    = manager.cargarCursorPedidosByID(id_usuario);
            Cursor  cursor00   = manager.cargarCursorPedidos_LocalesByID(id_usuario);
            boolean encontrado = cursor0.getCount() > 0 || cursor00.getCount() > 0;

            //manager0.cerrar();
            cursor0.close();
            cursor00.close();

            if (encontrado)
            {
                agregarCabecera();
                agregarFilasTabla("0", "Todos", "ASC", "fecha");
            }
            else
            {

                final Thread hilo1 = new Thread()
                {
                    @Override
                    public void run()
                    {
                        synchronized (this)
                        {
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    final TextView txt = new TextView(contexto);
                                    txt.setText(R.string.sinPedidos);
                                    txt.setGravity(Gravity.CENTER);

                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                                    {
                                        //noinspection deprecation
                                        txt.setTextAppearance(contexto, R.style.texto_galeria_1);
                                    }
                                    else
                                    {
                                        txt.setTextAppearance(R.style.texto_galeria_1);
                                    }

                                    //txt.setTextAppearance(contexto, R.style.texto_galeria_1);
                                    LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                    txt.setLayoutParams(layout);
                                    LinearLayout linear = (LinearLayout) rootView.findViewById(R.id.tab1);
                                    linear.removeAllViews();
                                    linear.addView(txt, layout);
                                }
                            });

                        }
                    }
                };
                hilo1.start();
            }

            return "OK";
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
        }
    }

    /**
     * Envia los pedidos locales en segundo plano.
     */
    private class enviarPedido extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute()
        {
            Cursor cursor = manager.cargarCursorPedidos_Locales();
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Enviando Pedidos...");
            pDialog.setMessage("Por favor espere...");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setProgress(0);
            pDialog.setMax(cursor.getCount());
            pDialog.show();
            cursor.close();
        }

        @Override
        protected String doInBackground(String... urls)
        {
            if (enviarPedidoLocales())
                return "OK";
            else
                return "ERROR";
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            if (result.equals("OK"))
            {
                getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Toast.makeText(contexto, "Pedidos enviados exitosamente!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(contexto, "Ha ocurrido un error enviando los pedidos!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}