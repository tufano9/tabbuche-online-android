package paquete.tufanoapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.devspark.robototextview.util.RobotoTextViewUtils;
import com.devspark.robototextview.util.RobotoTypefaceManager;
import com.devspark.robototextview.widget.RobotoTextView;

import java.io.File;

import paquete.database.DBAdapter;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.global.Funciones_Pedidos;
import paquete.recycle_bitmap.RecyclingImageView;

/**
 * Desarrollado por Gerson el 6/5/2015.
 */
public class FragmentPedido_Pre_Editar extends Fragment
{
    private static Context contexto;
    private View rootView;
    private String id_pedido, id_vendedor;
    private String estatus;
    private Resources rs;
    private RobotoTextView texto_total_pedido;
    private String total_costo = "0.00";
    private DBAdapter manager;
    private Typeface typeface, typeface_2;
    private ProgressDialog pDialog;
    private RobotoTextView total_bultos, total_pares;

    private TableLayout tabla_numeraciones, cabecera_numeraciones;
    private TableRow.LayoutParams layoutFila, layout_producto, layout_numeracion;
    private TableRow.LayoutParams layout_color, layout_paresxbulto;
    private TableRow.LayoutParams layout_cantidad, layout_preciounitario, layout_subtotal;

    /* Variables Pedido P */
    private TableLayout tabla_P, cabecera_P;
    private RobotoTextView total_bultos_P, totalpares_P, total_monto_P;

    /* Variables Pedido M */
    private TableLayout tabla_M, cabecera_M;
    private RobotoTextView total_bultos_M, totalpares_M, total_monto_M;

    /*Variables Pedido G */
    private TableLayout tabla_G, cabecera_G;
    private RobotoTextView total_bultos_G, totalpares_G, total_monto_G;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        onDestroy();

        rootView = inflater.inflate(R.layout.fragment_pedido_pre_editar, container, false);
        contexto = getActivity();
        manager = new DBAdapter(contexto);

        getExtrasVar();
        initComponents();
        initButtons();
        initTabHost();

        final Thread hilo = new Thread()
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
                            generarTabla();
                        }
                    });

                }
            }
        };
        hilo.start();

        return rootView;
    }

    /**
     * Inicializa el tabHost superior.
     */
    private void initTabHost()
    {
        TabHost tabs = (TabHost) rootView.findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("pedido_p");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Pedidos P");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("pedido_m");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Pedidos M");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("pedido_g");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Pedidos G");
        tabs.addTab(spec);

        tabs.setCurrentTab(2);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            public void onTabChanged(String tabId)
            {
                switch (tabId)
                {
                    case "pedido_p":
                        Log.i("AndroidTabs", "Pulsada pestana: " + "pedido_p");
                        break;

                    case "pedido_m":
                        Log.i("AndroidTabs", "Pulsada pestana: " + "pedido_m");
                        break;

                    case "pedido_g":
                        Log.i("AndroidTabs", "Pulsada pestana: " + "pedido_g");
                        break;
                }
            }
        });
    }

    /**
     * Inicializa los botones.
     */
    private void initButtons()
    {
        Button btn2 = (Button) rootView.findViewById(R.id.btn_pedido2);
        texto_total_pedido = (RobotoTextView) rootView.findViewById(R.id.texto_total_pedido);

        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new asynclogin_comprobarPedido().execute();
            }
        });
    }

    /**
     * Inicializa los componentes.
     */
    private void initComponents()
    {
        total_bultos = (RobotoTextView) rootView.findViewById(R.id.txt_cantidad_bultos);
        total_pares = (RobotoTextView) rootView.findViewById(R.id.txt_cantidad_pares);

        total_bultos_G = (RobotoTextView) rootView.findViewById(R.id.total_bultos_G);
        totalpares_G = (RobotoTextView) rootView.findViewById(R.id.total_pares_G);
        total_monto_G = (RobotoTextView) rootView.findViewById(R.id.total_monto_G);

        total_bultos_M = (RobotoTextView) rootView.findViewById(R.id.total_bultos_M);
        totalpares_M = (RobotoTextView) rootView.findViewById(R.id.total_pares_M);
        total_monto_M = (RobotoTextView) rootView.findViewById(R.id.total_monto_M);

        total_bultos_P = (RobotoTextView) rootView.findViewById(R.id.total_bultos_P);
        totalpares_P = (RobotoTextView) rootView.findViewById(R.id.total_pares_P);
        total_monto_P = (RobotoTextView) rootView.findViewById(R.id.total_monto_P);

        typeface = RobotoTypefaceManager.obtainTypeface(
                contexto,
                RobotoTypefaceManager.FontFamily.ROBOTO,
                RobotoTypefaceManager.TextWeight.MEDIUM,
                RobotoTypefaceManager.TextStyle.NORMAL);

        typeface_2 = RobotoTypefaceManager.obtainTypeface(
                contexto,
                RobotoTypefaceManager.FontFamily.ROBOTO,
                RobotoTypefaceManager.TextWeight.BOLD,
                RobotoTypefaceManager.TextStyle.NORMAL);
    }

    /**
     * Obtiene las variables que fueron pasadas como parametros a traves de otra actividad.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        id_pedido = bundle.getString("id_pedido");
        id_vendedor = bundle.getString("id_vendedor");
        estatus = bundle.getString("estatus");
    }

    /**
     * Prepara la data para generar la tabla.
     */
    private void generarTabla()
    {
        rs = this.getResources();
        tabla_P = (TableLayout) rootView.findViewById(R.id.tabla1);
        cabecera_P = (TableLayout) rootView.findViewById(R.id.cabecera1);
        tabla_M = (TableLayout) rootView.findViewById(R.id.tabla2);
        cabecera_M = (TableLayout) rootView.findViewById(R.id.cabecera2);
        tabla_G = (TableLayout) rootView.findViewById(R.id.tabla3);
        cabecera_G = (TableLayout) rootView.findViewById(R.id.cabecera3);

        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 50);
        layout_producto = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT);
        layout_numeracion = new TableRow.LayoutParams(480, TableRow.LayoutParams.MATCH_PARENT);

        layout_color = new TableRow.LayoutParams(130, TableRow.LayoutParams.MATCH_PARENT);
        layout_paresxbulto = new TableRow.LayoutParams(130, TableRow.LayoutParams.MATCH_PARENT);
        layout_cantidad = new TableRow.LayoutParams(130, TableRow.LayoutParams.MATCH_PARENT);
        layout_preciounitario = new TableRow.LayoutParams(130, TableRow.LayoutParams.MATCH_PARENT);
        layout_subtotal = new TableRow.LayoutParams(130, TableRow.LayoutParams.MATCH_PARENT);

        new cargar_data_pedido().execute();
    }

    /**
     * Agrega la cabecera de la tabla
     *
     * @param tipo P, M o G
     */
    private void agregarCabecera(final String tipo)
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
                            TableRow fila = new TableRow(contexto);
                            fila.setLayoutParams(layoutFila);

                            RobotoTextView txtProducto = new RobotoTextView(contexto);
                            RobotoTextView txtNumeracion = new RobotoTextView(contexto);
                            RobotoTextView txtColor = new RobotoTextView(contexto);
                            RobotoTextView txtparesxbulto = new RobotoTextView(contexto);
                            RobotoTextView txtCantidad = new RobotoTextView(contexto);
                            RobotoTextView txtPrecioUnitario = new RobotoTextView(contexto);
                            RobotoTextView txtSubtotal = new RobotoTextView(contexto);

                            txtProducto.setTextSize(16f);
                            txtNumeracion.setTextSize(16f);
                            txtColor.setTextSize(16f);
                            txtparesxbulto.setTextSize(16f);
                            txtCantidad.setTextSize(16f);
                            txtPrecioUnitario.setTextSize(16f);
                            txtSubtotal.setTextSize(16f);

                            RobotoTextViewUtils.setTypeface(txtProducto, typeface_2);
                            RobotoTextViewUtils.setTypeface(txtNumeracion, typeface_2);
                            RobotoTextViewUtils.setTypeface(txtColor, typeface_2);
                            RobotoTextViewUtils.setTypeface(txtparesxbulto, typeface_2);
                            RobotoTextViewUtils.setTypeface(txtCantidad, typeface_2);
                            RobotoTextViewUtils.setTypeface(txtPrecioUnitario, typeface_2);
                            RobotoTextViewUtils.setTypeface(txtSubtotal, typeface_2);

                            txtProducto.setText(rs.getString(R.string.producto_pedido));
                            txtProducto.setGravity(Gravity.CENTER_HORIZONTAL);
                            //txtProducto.setTextAppearance(contexto, R.style.etiqueta);
                            txtProducto.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtProducto.setLayoutParams(layout_producto);

                            txtNumeracion.setText(rs.getString(R.string.numeracion));
                            txtNumeracion.setGravity(Gravity.CENTER_HORIZONTAL);
                            //txtNumeracion.setTextAppearance(contexto, R.style.etiqueta);
                            txtNumeracion.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtNumeracion.setLayoutParams(layout_numeracion);

                            txtColor.setText(rs.getString(R.string.color_pedido));
                            txtColor.setGravity(Gravity.CENTER_HORIZONTAL);
                            //txtColor.setTextAppearance(contexto, R.style.etiqueta);
                            txtColor.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtColor.setLayoutParams(layout_color);

                            txtparesxbulto.setText(rs.getString(R.string.pares_pedido));
                            txtparesxbulto.setGravity(Gravity.CENTER_HORIZONTAL);
                            //txtparesxbulto.setTextAppearance(contexto, R.style.etiqueta);
                            txtparesxbulto.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtparesxbulto.setLayoutParams(layout_paresxbulto);

                            txtCantidad.setText(rs.getString(R.string.bultos_pedido));
                            txtCantidad.setGravity(Gravity.CENTER_HORIZONTAL);
                            //txtCantidad.setTextAppearance(contexto, R.style.etiqueta);
                            txtCantidad.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtCantidad.setLayoutParams(layout_cantidad);

                            txtPrecioUnitario.setText(rs.getString(R.string.precio_unitario));
                            txtPrecioUnitario.setGravity(Gravity.CENTER_HORIZONTAL);
                            //txtPrecioUnitario.setTextAppearance(contexto, R.style.etiqueta);
                            txtPrecioUnitario.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtPrecioUnitario.setLayoutParams(layout_preciounitario);

                            txtSubtotal.setText(rs.getString(R.string.precio_total));
                            txtSubtotal.setGravity(Gravity.CENTER_HORIZONTAL);
                            //txtSubtotal.setTextAppearance(contexto, R.style.etiqueta);
                            txtSubtotal.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtSubtotal.setLayoutParams(layout_subtotal);

                            fila.addView(txtProducto);
                            fila.addView(txtNumeracion);
                            //fila.addView(txtColor);
                            fila.addView(txtparesxbulto);
                            fila.addView(txtCantidad);
                            fila.addView(txtPrecioUnitario);
                            fila.addView(txtSubtotal);

                            if (tipo.equals("P"))
                                cabecera_P.addView(fila);
                            if (tipo.equals("M"))
                                cabecera_M.addView(fila);
                            if (tipo.equals("G"))
                                cabecera_G.addView(fila);
                        }
                    });
                }
            }
        };
        hilo_base.start();
    }

    /**
     * Agrega el contenido a la tabla.
     *
     * @param tipo P, M o G
     */
    private void agregarFilasTabla(final String tipo)
    {
        int y = 0;

        //DBAdapter manager = new DBAdapter(contexto);
        Cursor cursor;
        if (estatus.equals(Constantes.PENDIENTE))
        {
            cursor = manager.cargarCursorProductosPedidos_Locales_Detalles_porTalla(tipo, id_pedido);
            Log.d("Envio Pendiente?", "SI: agregarFilasTabla " + id_pedido);
        }
        else
            cursor = manager.cargarCursorProductosPedidosDetalles_porTalla(tipo, id_pedido);

        final String[] nombre_producto = new String[cursor.getCount()];
        final String[] nombre_producto_real = new String[cursor.getCount()];
        //String[] tipo_producto = new String[cursor.getCount()];
        final String[] precio_producto = new String[cursor.getCount()];
        final String[] numeracion_producto = new String[cursor.getCount()];
        final String[] pares_producto = new String[cursor.getCount()];
        final String[] ids = new String[cursor.getCount()];
        final String[] pares_usuario = new String[cursor.getCount()];
        int bultos = 0, pares_totales = 0;
        float montos = 0;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            Cursor cursor3 = manager.buscarProducto(cursor.getString(0));

            for (cursor3.moveToFirst(); !cursor3.isAfterLast(); cursor3.moveToNext())
                nombre_producto[y] = cursor3.getString(0);
            cursor3.close();

            //nombre_producto[y] = cursor.getString(0);
            //tipo_producto[y] = cursor.getString(1);
            precio_producto[y] = cursor.getString(2);
            numeracion_producto[y] = cursor.getString(3);
            pares_producto[y] = cursor.getString(4);
            ids[y] = cursor.getString(5);
            pares_usuario[y] = cursor.getString(6);
            bultos += Integer.parseInt(pares_usuario[y]);
            montos += Float.parseFloat(precio_producto[y]) * Integer.parseInt(pares_producto[y]) * Integer.parseInt(pares_usuario[y]);
            pares_totales += Integer.parseInt(pares_producto[y]) * Integer.parseInt(pares_usuario[y]);

            Cursor cursor2 = manager.buscarProducto(ids[y]);
            for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                nombre_producto_real[y] = cursor2.getString(1);
            }

            y++;
        }

        //manager.cerrar();
        cursor.close();

        final int finalPares = pares_totales;
        final float finalMontos = montos;
        final int finalBultos = bultos;
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (tipo.equals("P"))
                {
                    total_bultos_P.setText(Funciones.priceWithDecimal((double) (finalBultos)));
                    totalpares_P.setText(Funciones.priceWithDecimal((double) (finalPares)));
                    String p = Funciones.priceWithDecimal((double) (finalMontos)) + " " + Constantes.MONEDA;
                    total_monto_P.setText(p);
                }
                if (tipo.equals("M"))
                {
                    total_bultos_M.setText(Funciones.priceWithDecimal((double) (finalBultos)));
                    totalpares_M.setText(Funciones.priceWithDecimal((double) (finalPares)));
                    String p = Funciones.priceWithDecimal((double) (finalMontos)) + " " + Constantes.MONEDA;
                    total_monto_M.setText(p);
                }
                if (tipo.equals("G"))
                {
                    total_bultos_G.setText(Funciones.priceWithDecimal((double) (finalBultos)));
                    totalpares_G.setText(Funciones.priceWithDecimal((double) (finalPares)));
                    String p = Funciones.priceWithDecimal((double) (finalMontos)) + " " + Constantes.MONEDA;
                    total_monto_G.setText(p);
                }
            }
        });

        for (int i = 0; i < nombre_producto.length; i++)
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
                                TableRow fila = new TableRow(contexto);
                                fila.setLayoutParams(layoutFila);

                                RecyclingImageView imageView1;
                                LinearLayout vertical_layout, vertical_layout2;

                                RobotoTextView txtId = new RobotoTextView(contexto);
                                RobotoTextView paresxbulto = new RobotoTextView(contexto);
                                RobotoTextView preciounitario = new RobotoTextView(contexto);
                                RobotoTextView subtotal = new RobotoTextView(contexto);
                                RobotoTextView cantidad_pares = new RobotoTextView(contexto);

                                RobotoTextViewUtils.setTypeface(txtId, typeface);
                                RobotoTextViewUtils.setTypeface(paresxbulto, typeface);
                                RobotoTextViewUtils.setTypeface(preciounitario, typeface);
                                RobotoTextViewUtils.setTypeface(subtotal, typeface);

                                /*
                                    Columna 1: Productos
                                 */

                                imageView1 = new RecyclingImageView(contexto);

                                final File file = new File(getActivity().getFilesDir(), nombre_producto_real[finalI] + "_01.jpg");

                                if (file.exists())
                                {
                                    imageView1.setImageBitmap(Funciones.decodeSampledBitmapFromResource(file, 180, 150));
                                }

                                imageView1.setLayoutParams(new TableRow.LayoutParams(180, 150));
                                imageView1.setBackgroundColor(Color.WHITE);

                                txtId.setText(nombre_producto[finalI]);
                                txtId.setGravity(Gravity.CENTER_HORIZONTAL);
                                //txtId.setTextAppearance(contexto, R.style.etiqueta);
                                txtId.setBackgroundColor(Color.WHITE);
                                txtId.setLayoutParams(new TableRow.LayoutParams(180, 30));

                                vertical_layout = new LinearLayout(contexto);
                                vertical_layout.setOrientation(LinearLayout.VERTICAL);
                                vertical_layout.addView(imageView1);
                                vertical_layout.addView(txtId);
                                vertical_layout.setBackgroundResource(R.drawable.tabla_celda);

                               /*
                                    Columna 2: Numeracion
                                */

                                cabecera_numeraciones = Funciones_Pedidos.agregarCabecera_numeracion(numeracion_producto[finalI], contexto, typeface);
                                tabla_numeraciones = Funciones_Pedidos.agregarFilasTabla_numeracion(numeracion_producto[finalI], contexto, typeface);

                                //generarTabla_numeracion(numeracion_producto[finalI]);

                                vertical_layout2 = new LinearLayout(contexto);
                                vertical_layout2.setOrientation(LinearLayout.VERTICAL);
                                vertical_layout2.setLayoutParams(layout_numeracion);
                                vertical_layout2.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                                vertical_layout2.addView(cabecera_numeraciones);
                                vertical_layout2.addView(tabla_numeraciones);
                                vertical_layout2.setBackgroundResource(R.drawable.tabla_celda);

                                /*
                                    Columna 3: Pares por bulto
                                */

                                paresxbulto.setText(pares_producto[finalI]);
                                paresxbulto.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                                //paresxbulto.setTextAppearance(contexto, R.style.etiqueta);
                                paresxbulto.setBackgroundResource(R.drawable.tabla_celda);
                                paresxbulto.setLayoutParams(layout_paresxbulto);

                                /*
                                    Columna 4: Cantidad
                                */

                                cantidad_pares.setText(pares_usuario[finalI]);
                                cantidad_pares.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                                cantidad_pares.setBackgroundResource(R.drawable.tabla_celda);
                                cantidad_pares.setLayoutParams(layout_cantidad);
                                //cantidad_pares.setWrapSelectorWheel(false);

                                /*
                                    Columna 5: Precio unitario
                                */

                                String mensaje = contexto.getResources().getString(R.string.precios_moneda);
                                mensaje = mensaje.replace("%", precio_producto[finalI]);
                                mensaje = mensaje.replace("$", Constantes.MONEDA);

                                preciounitario.setText(mensaje);
                                preciounitario.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                                //preciounitario.setTextAppearance(contexto,R.style.etiqueta);
                                preciounitario.setBackgroundResource(R.drawable.tabla_celda);
                                preciounitario.setLayoutParams(layout_preciounitario);

                                /*
                                    Columna 6: Subtotal
                                */
                                double subtotal_articulo = Integer.parseInt(String.valueOf(cantidad_pares.getText())) * Double.parseDouble(String.valueOf(preciounitario.getText()).substring(0, String.valueOf(preciounitario.getText()).lastIndexOf("B"))) * Double.parseDouble(String.valueOf(paresxbulto.getText()));

                                subtotal = new RobotoTextView(contexto);

                                String mensaje2 = contexto.getResources().getString(R.string.precios_moneda);
                                mensaje2 = mensaje2.replace("%", Funciones.priceWithDecimal(subtotal_articulo));
                                mensaje2 = mensaje2.replace("$", Constantes.MONEDA);

                                subtotal.setText(mensaje2);
                                subtotal.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                                //subtotal.setTextAppearance(contexto, R.style.etiqueta);
                                subtotal.setBackgroundResource(R.drawable.tabla_celda);
                                subtotal.setLayoutParams(layout_subtotal);

                                total_costo = String.valueOf(Double.parseDouble(total_costo) + Double.parseDouble(String.valueOf(subtotal_articulo)));

                                fila.addView(vertical_layout);
                                fila.addView(vertical_layout2);
                                fila.addView(paresxbulto);
                                fila.addView(cantidad_pares);
                                fila.addView(preciounitario);
                                fila.addView(subtotal);

                                if (tipo.equals("P"))
                                {
                                    tabla_P.addView(fila);
                                }
                                if (tipo.equals("M"))
                                {
                                    tabla_M.addView(fila);
                                }
                                if (tipo.equals("G"))
                                {
                                    tabla_G.addView(fila);
                                }
                            }
                        });

                    }
                }
            };
            hilo_base.start();
        }
    }

    /**
     * Comprueba en seegundo plano, el estado actual del pedido.
     */
    private class asynclogin_comprobarPedido extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Comprobando estado del pedido...");
            pDialog.setMessage("Por favor espere...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                String aprobado = "";
                boolean pedido_aprobado;

                //Comprobar localmente si el pedido fue aprobado..

                //DBAdapter manager = new DBAdapter(contexto);
                Cursor cursor;
                if (estatus.equals(Constantes.PENDIENTE))
                {
                    cursor = manager.cargarCursorPedidos_Locales(id_pedido);
                    Log.d("estatus", "PENDIENTE : LOCAL : ID " + id_pedido);
                }
                else
                    cursor = manager.cargarCursorPedidos(id_pedido);

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                {
                    aprobado = cursor.getString(0);
                }

                //manager.cerrar();
                cursor.close();

                pedido_aprobado = aprobado.equalsIgnoreCase("En espera de aprobacion") || aprobado.equalsIgnoreCase(Constantes.PENDIENTE);
                Log.d("Pedido", aprobado + " aprobado Localmente? " + pedido_aprobado);

                //Si no fue aprobado localmente, comprobar en internet si el pedido fue aprobado..
                /*if (pedido_aprobado)
                {
                    if (!Funciones.isOnline(contexto))
                        return "No internet";
                    else
                    {
                        Log.d("Pedido Web","Iniciando");
                        pedido_aprobado = validarEstatusPedido(id_pedido);
                    }
                }*/

                if (pedido_aprobado)
                    return "Editable";
                else
                    return "No Editable";
            }
            catch (RuntimeException e)
            {
                return "" + e;
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();

            switch (result)
            {
                case "Editable":
                    Bundle bundle = getArguments();
                    String ci = bundle.getString("cedula");

                    String TAG = "fragment_pedido_editar";
                    Fragment fragment = new FragmentPedido_Editar();
                    Bundle args = new Bundle();
                    args.putString("id_pedido", id_pedido);
                    args.putString("cedula", ci);
                    args.putString("id_vendedor", id_vendedor);
                    args.putString("estatus", estatus);
                    fragment.setArguments(args);

                    manager.borrar_tabla_producto_pedido_editar();
                    if (estatus.equals(Constantes.PENDIENTE))
                        manager.insertar_producto_pedido_local_editar(id_pedido);
                    else
                        manager.insertar_producto_pedido_editar(id_pedido);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();
                    break;
                case "No internet":
                    Toast.makeText(contexto, Constantes.NO_INTERNET, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(contexto, "El pedido ya fue procesado, no puede editarse!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**
     * Carga en segundo plano la data del pedido.
     */
    private class cargar_data_pedido extends AsyncTask<String, Void, String>
    {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Cargando Pedido...");
            pDialog.setMessage("Cargando Datos...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls)
        {
            //final DBAdapter manager0 = new DBAdapter(contexto);
            final Cursor cursor0, c;
            String precio_total_pedido = "";
            String talla_actual = "P";

            if (estatus.equals(Constantes.PENDIENTE))
            {
                cursor0 = manager.cargarCursorProductosPedidos_Locales(id_pedido);
                c = manager.cargarCursorPedidos_Locales_ID(id_pedido);
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
                {
                    precio_total_pedido = c.getString(4);
                }
                Log.d("Envio Pendiente?", "SI, ID: " + id_pedido);
            }
            else
            {
                cursor0 = manager.cargarCursorProductosPedidos_Detalles(id_pedido);
                c = manager.cargarCursorPedidos_ID(id_pedido);
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
                {
                    precio_total_pedido = c.getString(4);
                }
            }

            final String finalPrecio_total_pedido = precio_total_pedido;
            final int cantidad_productos = cursor0.getCount();
            final Thread hilo_cantidad = new Thread()
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

                                RobotoTextView texto = (RobotoTextView) rootView.findViewById(R.id.txt_cantidad_productos);
                                texto.setText(Funciones.priceWithDecimal((double) (cantidad_productos)));

                                int pares = 0, bultos = 0;

                                for (cursor0.moveToFirst(); !cursor0.isAfterLast(); cursor0.moveToNext())
                                {
                                    pares += cursor0.getInt(1) * cursor0.getInt(2);
                                    bultos += cursor0.getInt(2);
                                }

                                total_pares.setText(Funciones.priceWithDecimal((double) (pares)));
                                total_bultos.setText(Funciones.priceWithDecimal((double) (bultos)));
                                texto_total_pedido.setText(Funciones.priceWithDecimal(Double.parseDouble(finalPrecio_total_pedido)));
                                cursor0.close();
                            }
                        });
                    }
                }
            };
            hilo_cantidad.start();
            //manager0.cerrar();
            //cursor0.close();
            c.close();

            /*
                Pedido P
            */

            boolean encontrado;
            if (estatus.equals(Constantes.PENDIENTE))
            {
                //cursor = manager.cargarCursorProductosPedidos_Locales_Detalles_porTalla("P", id_pedido);
                encontrado = manager.cargarCursorProductosPedidos_Locales_Detalles_porTalla(talla_actual, id_pedido).getCount() > 0;
                Log.d("Envio P Pendiente?", "SI");
            }
            else
            {
                encontrado = manager.cargarCursorProductosPedidosDetalles_porTalla(talla_actual, id_pedido).getCount() > 0;
            }

            if (encontrado)
            {
                agregarCabecera(talla_actual);
                agregarFilasTabla(talla_actual);
            }
            else
            {
                final String finalTalla_actual = talla_actual;
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

                                    final RobotoTextView txt = new RobotoTextView(contexto);

                                    String mensaje = contexto.getResources().getString(R.string.pedido_sin_talla);
                                    mensaje = mensaje.replace("%", finalTalla_actual);

                                    txt.setText(mensaje);
                                    txt.setGravity(Gravity.CENTER);
                                    txt.setTextSize(20f);
                                    //txt.setTextAppearance(contexto, R.style.texto_galeria_1);
                                    RobotoTextViewUtils.setTypeface(txt, typeface);

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

            /*
                Pedido M
            */

            talla_actual = "M";

            Cursor cursor2;
            if (estatus.equals(Constantes.PENDIENTE))
            {
                cursor2 = manager.cargarCursorProductosPedidos_Locales_Detalles_porTalla(talla_actual, id_pedido);
                Log.d("Envio M Pendiente?", "SI");
            }
            else
                cursor2 = manager.cargarCursorProductosPedidosDetalles_porTalla(talla_actual, id_pedido);

            boolean encontrado2 = cursor2.getCount() > 0;
            cursor2.close();

            if (encontrado2)
            {
                agregarCabecera(talla_actual);
                agregarFilasTabla(talla_actual);
            }
            else
            {
                final String finalTalla_actual1 = talla_actual;
                final Thread hilo2 = new Thread()
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


                                    final RobotoTextView txt = new RobotoTextView(contexto);

                                    String mensaje = contexto.getResources().getString(R.string.pedido_sin_talla);
                                    mensaje = mensaje.replace("%", finalTalla_actual1);

                                    txt.setText(mensaje);
                                    txt.setGravity(Gravity.CENTER);
                                    txt.setTextSize(20f);
                                    //txt.setTextAppearance(contexto, R.style.texto_galeria_1);
                                    RobotoTextViewUtils.setTypeface(txt, typeface);

                                    LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                    txt.setLayoutParams(layout);
                                    LinearLayout linear = (LinearLayout) rootView.findViewById(R.id.tab2);
                                    linear.removeAllViews();
                                    linear.addView(txt, layout);
                                }
                            });

                        }
                    }
                };
                hilo2.start();
            }

            /*
                Pedido G
            */

            talla_actual = "G";
            Cursor cursor3;

            if (estatus.equals(Constantes.PENDIENTE))
            {
                cursor3 = manager.cargarCursorProductosPedidos_Locales_Detalles_porTalla(talla_actual, id_pedido);
                Log.d("Envio G Pendiente?", "SI");
            }
            else
                cursor3 = manager.cargarCursorProductosPedidosDetalles_porTalla(talla_actual, id_pedido);
            boolean encontrado3 = cursor3.getCount() > 0;

            cursor3.close();

            if (encontrado3)
            {
                agregarCabecera(talla_actual);
                agregarFilasTabla(talla_actual);
            }
            else
            {
                final String finalTalla_actual2 = talla_actual;
                final Thread hilo3 = new Thread()
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
                                    final RobotoTextView txt = new RobotoTextView(contexto);

                                    String mensaje = contexto.getResources().getString(R.string.pedido_sin_talla);
                                    mensaje = mensaje.replace("%", finalTalla_actual2);

                                    txt.setText(mensaje);
                                    txt.setGravity(Gravity.CENTER);
                                    txt.setTextSize(20f);
                                    //txt.setTextAppearance(contexto, R.style.texto_galeria_1);
                                    RobotoTextViewUtils.setTypeface(txt, typeface);

                                    LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                    txt.setLayoutParams(layout);
                                    LinearLayout linear = (LinearLayout) rootView.findViewById(R.id.tab3);
                                    linear.removeAllViews();
                                    linear.addView(txt, layout);
                                }
                            });
                        }
                    }
                };
                hilo3.start();
            }

            return "OK";
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
        }
    }
}