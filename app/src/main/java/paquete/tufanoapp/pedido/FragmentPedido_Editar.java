package paquete.tufanoapp.pedido;

import android.app.AlertDialog;
import android.app.DialogFragment;
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
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.devspark.robototextview.util.RobotoTextViewUtils;
import com.devspark.robototextview.util.RobotoTypefaceManager;
import com.devspark.robototextview.widget.RobotoTextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import paquete.database.DBAdapter;
import paquete.dialogos.DialogoAgregar_Productos_Editar;
import paquete.dialogos.DialogoEliminar_Productos_Editar;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.global.Funciones_Pedidos;
import paquete.global.library.Httppostaux;
import paquete.recycle_bitmap.RecyclingImageView;
import paquete.tufanoapp.R;
import paquete.tufanoapp.muestrario.FragmentMuestrario;

/**
 * Desarrollado por Gerson el 7/5/2015.
 */
public class FragmentPedido_Editar extends Fragment
{
    private static Context contexto;
    private final Httppostaux post = new Httppostaux();
    private View rootView;
    private String id_pedido, id_vendedor, estatus, total_costo = "0.00";
    private int            w;
    private Resources      rs;
    private RobotoTextView texto_total_pedido, total_bultos, total_pares;
    private ProgressDialog pDialog;
    private DBAdapter manager;

    private Typeface typeface, typeface_2;

    private TableLayout tabla_numeraciones, cabecera_numeraciones;
    private TableRow.LayoutParams layoutFila, layout_producto, layout_numeracion;
    private TableRow.LayoutParams layout_color, layout_paresxbulto, layout_cantidad;
    private TableRow.LayoutParams layout_preciounitario, layout_subtotal;

    // Variables Pedido P
    private TableLayout tabla_P, cabecera_P;
    private RobotoTextView total_bultos_P, totalpares_P, total_monto_P;

    // Variables Pedido M
    private TableLayout tabla_M, cabecera_M;
    private RobotoTextView total_bultos_M, totalpares_M, total_monto_M;

    // Variables Pedido G
    private TableLayout tabla_G, cabecera_G;
    private RobotoTextView total_bultos_G, totalpares_G, total_monto_G;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        onDestroy();

        rootView = inflater.inflate(R.layout.fragment_pedido_editar, container, false);
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
     * Inicializa el tab host superior.
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
        Button btn1 = (Button) rootView.findViewById(R.id.btn_pedido1);
        Button btn2 = (Button) rootView.findViewById(R.id.btn_pedido2);
        Button btn3 = (Button) rootView.findViewById(R.id.btn_pedido3);
        Button btn4 = (Button) rootView.findViewById(R.id.btn_pedido4);

        btn1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(contexto);

                dialog.setMessage(R.string.confirmacion_cancelar_pedido)
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                        .setTitle(R.string.pedido_btn_1);

                dialog.setPositiveButton("Cancelar pedido", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (estatus.equals(Constantes.PENDIENTE))
                        {
                            manager.eliminar_pedido_local(id_pedido); // Limpiar la BD
                            getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            Toast.makeText(contexto, "!Pedido cancelado exitosamente!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            new async_eliminar_pedido().execute(id_pedido);
                        }
                    }
                });

                dialog.setNegativeButton("Volver", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });

        Cursor cursor = manager.cargarCursorProductosPedidos_editar();
        w = 0;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            w++;
        }
        cursor.close();


        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (w > 0)
                {
                    DialogFragment newFragment = new DialogoEliminar_Productos_Editar();
                    Bundle         bundle      = getArguments();
                    String         ci          = bundle.getString("cedula");

                    Bundle args = new Bundle();
                    args.putString("id_pedido", id_pedido);
                    args.putString("cedula", ci);
                    args.putString("estatus", estatus);
                    newFragment.setArguments(args);
                    newFragment.show(getFragmentManager(), "Eliminar_Productos");
                }
                else
                {
                    Toast.makeText(contexto, "El pedido esta vacio!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (w > 0)
                {
                    final boolean       permitido = Funciones.FuncionalidadPermitida("Pedidos", 1, id_vendedor, manager);
                    AlertDialog.Builder dialog    = new AlertDialog.Builder(contexto);

                    dialog.setMessage(R.string.confirmacion_editar_pedido)
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                            .setTitle(R.string.pedido_btn_4);

                    dialog.setPositiveButton("Editar pedido", new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Log.d("estatus", estatus);
                            if (estatus.equals(Constantes.PENDIENTE))
                            {
                                // Estoy editando un pedido local, por lo cual no usara conexion
                                // a internet, solo editara localmente sin enviar correo..
                                if (permitido)
                                    new asynclogin_comprobarPedido().execute();
                                else
                                    Toast.makeText(contexto, Constantes.NO_PERMITIDO, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                if (!Funciones.isOnline(contexto))
                                    Toast.makeText(contexto, Constantes.NO_INTERNET, Toast.LENGTH_LONG).show();
                                else if (permitido)
                                    new asynclogin_comprobarPedido().execute();
                                else
                                    Toast.makeText(contexto, Constantes.NO_PERMITIDO, Toast.LENGTH_SHORT).show();
                            }

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
                {
                    Toast.makeText(contexto, "El pedido esta vacio!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogFragment newFragment = new DialogoAgregar_Productos_Editar();
                Bundle         bundle      = getArguments();
                String         ci          = bundle.getString("cedula");

                Bundle args = new Bundle();
                args.putString("id_pedido", id_pedido);
                args.putString("cedula", ci);
                args.putString("estatus", estatus);
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "Agregar_Productos");
            }
        });
    }

    /**
     * Inicializa los componentes de la clase.
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

        texto_total_pedido = (RobotoTextView) rootView.findViewById(R.id.texto_total_pedido);
    }

    /**
     * Obtiene las variables que fueron pasadas como parametros a traves de otra actividad.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        id_pedido = bundle.getString("id_pedido");
        estatus = bundle.getString("estatus");
        id_vendedor = bundle.getString("id_vendedor");
    }

    /**
     * Prepara la generacion de la tabla de datos.
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

        new cargar_data_pedido().execute();
    }

    /**
     * Agrega la cabecera a la tabla de datos.
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

                            RobotoTextView txtProducto       = new RobotoTextView(contexto);
                            RobotoTextView txtNumeracion     = new RobotoTextView(contexto);
                            RobotoTextView txtColor          = new RobotoTextView(contexto);
                            RobotoTextView txtparesxbulto    = new RobotoTextView(contexto);
                            RobotoTextView txtCantidad       = new RobotoTextView(contexto);
                            RobotoTextView txtPrecioUnitario = new RobotoTextView(contexto);
                            RobotoTextView txtSubtotal       = new RobotoTextView(contexto);

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
     * Genera el contenido de la tabla de datos.
     *
     * @param tipo P, M o G.
     */
    private void agregarFilasTabla(final String tipo)
    {
        int y = 0;

        Cursor cursor = manager.cargarCursorPedidos_Editar2(tipo);

        final String[] nombre_producto      = new String[cursor.getCount()];
        final String[] nombre_producto_real = new String[cursor.getCount()];
        //String[] tipo_producto = new String[cursor.getCount()];
        final String[] precio_producto     = new String[cursor.getCount()];
        final String[] numeracion_producto = new String[cursor.getCount()];
        final String[] pares_producto      = new String[cursor.getCount()];
        final String[] ids                 = new String[cursor.getCount()];
        final String[] pares_usuario       = new String[cursor.getCount()];
        int            bultos              = 0, pares_totales = 0;
        float          montos              = 0;

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

        cursor.close();

        final int   finalPares  = pares_totales;
        final float finalMontos = montos;
        final int   finalBultos = bultos;
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (tipo.equals("P"))
                {
                    total_bultos_P.setText(Funciones.priceWithDecimal((double) (finalBultos)));
                    totalpares_P.setText(Funciones.priceWithDecimal((double) (finalPares)));
                    String t = Funciones.priceWithDecimal((double) (finalMontos)) + " " + Constantes.MONEDA;
                    total_monto_P.setText(t);
                }
                if (tipo.equals("M"))
                {
                    total_bultos_M.setText(Funciones.priceWithDecimal((double) (finalBultos)));
                    totalpares_M.setText(Funciones.priceWithDecimal((double) (finalPares)));
                    String t = Funciones.priceWithDecimal((double) (finalMontos)) + " " + Constantes.MONEDA;
                    total_monto_M.setText(t);
                }
                if (tipo.equals("G"))
                {
                    total_bultos_G.setText(Funciones.priceWithDecimal((double) (finalBultos)));
                    totalpares_G.setText(Funciones.priceWithDecimal((double) (finalPares)));
                    String t = Funciones.priceWithDecimal((double) (finalMontos)) + " " + Constantes.MONEDA;
                    total_monto_G.setText(t);
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

                                RecyclingImageView imageView1 = new RecyclingImageView(contexto);
                                LinearLayout       vertical_layout, vertical_layout2;

                                RobotoTextView txtId          = new RobotoTextView(contexto);
                                RobotoTextView paresxbulto    = new RobotoTextView(contexto);
                                RobotoTextView preciounitario = new RobotoTextView(contexto);
                                RobotoTextView subtotal       = new RobotoTextView(contexto);
                                NumberPicker   cantidad_pares = new NumberPicker(contexto);

                                RobotoTextViewUtils.setTypeface(txtId, typeface);
                                RobotoTextViewUtils.setTypeface(paresxbulto, typeface);
                                RobotoTextViewUtils.setTypeface(preciounitario, typeface);
                                RobotoTextViewUtils.setTypeface(subtotal, typeface);



                                /*
                                    Columna 1: Productos
                                 */

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

                                cantidad_pares.setMinValue(1);
                                cantidad_pares.setMaxValue(99);
                                cantidad_pares.setValue(Integer.parseInt(pares_usuario[finalI]));
                                cantidad_pares.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                                cantidad_pares.setBackgroundResource(R.drawable.tabla_celda);
                                cantidad_pares.setLayoutParams(layout_cantidad);

                                /*
                                    Columna 5: Precio unitario
                                */
                                String p = precio_producto[finalI] + " " + Constantes.MONEDA;
                                preciounitario.setText(p);
                                preciounitario.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                                //preciounitario.setTextAppearance(contexto,R.style.etiqueta);
                                preciounitario.setBackgroundResource(R.drawable.tabla_celda);
                                preciounitario.setLayoutParams(layout_preciounitario);

                                /*
                                    Columna 6: Subtotal
                                */
                                //Log.d("SUBTOTAL",Double.parseDouble(String.valueOf(preciounitario.getText()).substring(0,String.valueOf(preciounitario.getText()).lastIndexOf("B")))+" * "+Double.parseDouble(String.valueOf(paresxbulto.getText())));
                                double subtotal_articulo = Integer.parseInt(String.valueOf(cantidad_pares.getValue())) * Double.parseDouble(String.valueOf(preciounitario.getText()).substring(0, String.valueOf(preciounitario.getText()).lastIndexOf("B"))) * Double.parseDouble(String.valueOf(paresxbulto.getText()));

                                String p2 = Funciones.priceWithDecimal(subtotal_articulo) + " " + Constantes.MONEDA;
                                subtotal.setText(p2);
                                subtotal.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                                //subtotal.setTextAppearance(contexto, R.style.etiqueta);
                                subtotal.setBackgroundResource(R.drawable.tabla_celda);
                                subtotal.setLayoutParams(layout_subtotal);

                                total_costo = String.valueOf(Double.parseDouble(total_costo) + Double.parseDouble(String.valueOf(subtotal_articulo)));

                                final RobotoTextView finalSubtotal       = subtotal;
                                final RobotoTextView finalPreciounitario = preciounitario;
                                final RobotoTextView finalparesxbulto    = paresxbulto;
                                cantidad_pares.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
                                {
                                    @Override
                                    public void onValueChange(NumberPicker picker, int oldVal, final int newVal)
                                    {

                                        Thread hilo_secundario = new Thread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                String precio_viejo = String.valueOf(finalPreciounitario.getText());
                                                String pares        = String.valueOf(finalparesxbulto.getText());

                                                double precio_nuevo = Integer.parseInt(pares) * newVal * Double.parseDouble(precio_viejo.substring(0, precio_viejo.lastIndexOf("B")));
                                                String p3           = Funciones.priceWithDecimal(precio_nuevo) + " " + Constantes.MONEDA;
                                                finalSubtotal.setText(p3);

                                                Double res  = 0.0;
                                                Double res2 = 0.0;

                                                int bultos_actualizados = 0, pares_actualizados = 0;
                                                int pares_t             = 0, bultos_t = 0;

                                                manager.actualizar_producto_pedido_cantidad_editar(ids[finalI], newVal);

                                                Cursor cursor  = manager.cargarCursorPedidos_Editar2(tipo);
                                                Cursor cursor2 = manager.cargarCursorPedidos_Editar();

                                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                                                {
                                                    //Log.i("OPERACION",Double.valueOf(cursor.getString(2))+" * "+Integer.parseInt(cursor.getString(6))+" * "+Integer.parseInt(cursor.getString(4)));
                                                    bultos_actualizados += Integer.parseInt(cursor.getString(6));
                                                    res2 += Double.valueOf(cursor.getString(2)) * Integer.parseInt(cursor.getString(6)) * Integer.parseInt(cursor.getString(4));
                                                    pares_actualizados += (Integer.parseInt(cursor.getString(6)) * Integer.parseInt(cursor.getString(4)));
                                                }
                                                for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
                                                {
                                                    res += Double.valueOf(cursor2.getString(0)) * Integer.parseInt(cursor2.getString(1)) * Integer.parseInt(cursor2.getString(2));
                                                    pares_t += cursor2.getInt(1) * cursor2.getInt(2);
                                                    bultos_t += cursor2.getInt(2);
                                                }
                                                cursor.close();
                                                cursor2.close();

                                                if (tipo.equals("P"))
                                                {
                                                    total_bultos_P.setText(Funciones.priceWithDecimal((double) (bultos_actualizados)));
                                                    totalpares_P.setText(Funciones.priceWithDecimal((double) (pares_actualizados)));
                                                    String prec = Funciones.priceWithDecimal(res2) + " " + Constantes.MONEDA;
                                                    total_monto_P.setText(prec);
                                                }
                                                if (tipo.equals("M"))
                                                {
                                                    total_bultos_M.setText(Funciones.priceWithDecimal((double) (bultos_actualizados)));
                                                    totalpares_M.setText(Funciones.priceWithDecimal((double) (pares_actualizados)));
                                                    String prec = Funciones.priceWithDecimal(res2) + " " + Constantes.MONEDA;
                                                    total_monto_M.setText(prec);
                                                }
                                                if (tipo.equals("G"))
                                                {
                                                    total_bultos_G.setText(Funciones.priceWithDecimal((double) (bultos_actualizados)));
                                                    totalpares_G.setText(Funciones.priceWithDecimal((double) (pares_actualizados)));
                                                    String prec = Funciones.priceWithDecimal(res2) + " " + Constantes.MONEDA;
                                                    total_monto_G.setText(prec);
                                                }

                                                total_pares.setText(Funciones.priceWithDecimal((double) (pares_t)));
                                                total_bultos.setText(Funciones.priceWithDecimal((double) (bultos_t)));
                                                texto_total_pedido.setText(Funciones.priceWithDecimal(res));
                                            }
                                        });

                                        hilo_secundario.run();
                                    }
                                });

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
     * Actualiza el pedido en la BD.
     *
     * @return True si el proceso fue exitoso, False en caso contrario.
     */
    private boolean actualizar_Pedido()
    {
        int logstatus = -1;

        String     id_vendedor = "", id_cliente = "", id_productos = "", precios = "", observaciones_texto, numeracion = "", cantidad_pares = "", cantidad_bultos = "", precio_unitario = "", subtotal = "";
        int        multiplicador;
        BigDecimal total_monto = new BigDecimal("0.00");

        observaciones_texto = "";//String.valueOf(observaciones.getText());

        double monto = 0;
        int    y     = 0;

        Cursor cursor = manager.cargarCursorProductosPedidos_editar();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            if (y == 0)
            {
                precios = cursor.getString(2);
                numeracion = cursor.getString(3);
                id_productos = cursor.getString(5);
                cantidad_pares = cursor.getString(4);
                cantidad_bultos = cursor.getString(6);
                precio_unitario = cursor.getString(2);
                subtotal = String.valueOf(Double.parseDouble(cantidad_pares) * Double.parseDouble(cantidad_bultos) * Double.parseDouble(precio_unitario));
            }
            else
            {
                precios = precios + "#" + cursor.getString(2);
                numeracion = numeracion + "#" + cursor.getString(3);
                id_productos = id_productos + "#" + cursor.getString(5);
                cantidad_pares = cantidad_pares + "#" + cursor.getString(4);
                cantidad_bultos = cantidad_bultos + "#" + cursor.getString(6);
                precio_unitario = precio_unitario + "#" + cursor.getString(2);
                subtotal = subtotal + "#" + String.valueOf(Double.parseDouble(cursor.getString(4)) * Double.parseDouble(cursor.getString(6)) * Double.parseDouble(cursor.getString(2)));

            }
            // cantidad de bultos que el usuario desea de ese producto
            multiplicador = cursor.getInt(6);

            BigDecimal bd1 = BigDecimal.valueOf((Double.parseDouble(cursor.getString(2))));
            BigDecimal bd2 = BigDecimal.valueOf(Double.parseDouble(String.valueOf(cursor.getString(4))));
            BigDecimal bd3 = BigDecimal.valueOf(multiplicador);

            total_monto = total_monto.add(total_monto).multiply(bd1).multiply(bd2).multiply(bd3);

            monto += (Double.parseDouble(cursor.getString(2)) * Integer.parseInt(cursor.getString(4)) * multiplicador);
            //Log.d("Precio Formateado", priceWithDecimal(monto) + " Precio sin formatear: " + monto + " Sumatoria: "+total_monto);

            //Log.d("SUMA MONTOS","Sumando: "+Double.parseDouble(cursor.getString(2))+" * "+Integer.parseInt(cursor.getString(4)) +" * "+ multiplicador);
            y++;
        }

        cursor.close();

        if (estatus.equals(Constantes.PENDIENTE))
        {
            Cursor cursor_cliente = manager.cargarCursorPedidos_Locales(id_pedido);
            for (cursor_cliente.moveToFirst(); !cursor_cliente.isAfterLast(); cursor_cliente.moveToNext())
            {
                id_cliente = cursor_cliente.getString(1);
                id_vendedor = cursor_cliente.getString(2);
                Log.i("actualizar_Pedido", "id_cliente: " + id_cliente + ", id_vendedor: " + id_vendedor);
            }

            // Solo actualizo localmente, sin enviar correo ni nada..
            Calendar c       = Calendar.getInstance();
            String   month   = String.format("%02d", c.get(Calendar.MONTH) + 1);
            String   day     = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
            String   year    = String.format("%02d", c.get(Calendar.YEAR));
            int      hora    = c.get(Calendar.HOUR_OF_DAY);
            String   minutes = String.format("%02d", c.get(Calendar.MINUTE));
            String   seconds = String.format("%02d", c.get(Calendar.SECOND));

            String fecha_ingreso = year + "-" + month + "-" + day + " " + hora + ":" + minutes + ":" + seconds;
            String codigo_pedido = "Sin Codigo";
            //String id_pedido = "999999999";

            Log.d("actualizar_Pedido_local", "Operacion exitosa");
            //DBAdapter manager2 = new DBAdapter(contexto);

            long id_pedido = manager.insertar_pedido_local(id_vendedor, id_cliente, fecha_ingreso, monto, Constantes.PENDIENTE, observaciones_texto, codigo_pedido);

            if (id_pedido != -1)
            {
                manager.insertar_pedido_local_detalles(String.valueOf(id_pedido), id_productos, cantidad_pares, cantidad_bultos, numeracion, precio_unitario, subtotal);
                manager.eliminar_pedido_local(this.id_pedido);
            }

            //manager2.cerrar();
            return true;
        }
        else
        {
            Cursor cursor_cliente = manager.cargarCursorPedidos(id_pedido);
            for (cursor_cliente.moveToFirst(); !cursor_cliente.isAfterLast(); cursor_cliente.moveToNext())
            {
                id_cliente = cursor_cliente.getString(1);
                id_vendedor = cursor_cliente.getString(2);
                Log.i("actualizar_Pedido", "id_cliente: " + id_cliente + ", id_vendedor: " + id_vendedor);
            }

            cursor_cliente.close();

            String                   URL_obtener_datos_usuario = Constantes.IP_Server + "actualizar_pedido.php";
            ArrayList<NameValuePair> postparameters2send       = new ArrayList<>();
            postparameters2send.add(new BasicNameValuePair("id_pedido_act", id_pedido));
            postparameters2send.add(new BasicNameValuePair("id_vendedor", id_vendedor));
            postparameters2send.add(new BasicNameValuePair("id_cliente", id_cliente));
            postparameters2send.add(new BasicNameValuePair("id_productos", id_productos));
            postparameters2send.add(new BasicNameValuePair("monto", String.valueOf(monto)));
            postparameters2send.add(new BasicNameValuePair("observaciones", observaciones_texto));
            postparameters2send.add(new BasicNameValuePair("cantidad_pares", cantidad_pares));
            postparameters2send.add(new BasicNameValuePair("cantidad_bultos", cantidad_bultos));
            postparameters2send.add(new BasicNameValuePair("precio_unitario", precio_unitario));
            postparameters2send.add(new BasicNameValuePair("subtotal", subtotal));
            postparameters2send.add(new BasicNameValuePair("numeraciones", numeracion));
            JSONArray jdata = post.getserverdata(postparameters2send, URL_obtener_datos_usuario);

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
                        Log.d("actualizar_Pedido_local", "Operacion exitosa");
                        //DBAdapter manager2 = new DBAdapter(contexto);
                        manager.insertar_pedido(id_pedido, id_vendedor, id_cliente, fecha_ingreso, monto, "En espera de aprobacion", observaciones_texto, codigo_pedido);
                        manager.insertar_pedido_detalles(id_pedido, id_productos, cantidad_pares, cantidad_bultos, numeracion, precio_unitario, subtotal);
                        manager.actualizar_actualizacionesPedido(id_vendedor, fecha_ingreso);
                        //manager.cerrar();
                    }
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                return logstatus != 0;
            }

            else
            {
                Log.e("JSON", "ERROR");
                return false;
            }
        }
    }

    /**
     * Cancela el pedido antiguo.
     *
     * @return True si el proceso fue exitoso, False en caso contrario.
     */
    private boolean cancelar_Pedido()
    {
        String                   URL_obtener_datos_usuario = Constantes.IP_Server + "cancelar_pedido.php";
        ArrayList<NameValuePair> postparameters2send       = new ArrayList<>();
        postparameters2send.add(new BasicNameValuePair("id_pedido", id_pedido));
        postparameters2send.add(new BasicNameValuePair("id_vendedor", id_vendedor));
        JSONArray jdata     = post.getserverdata(postparameters2send, URL_obtener_datos_usuario);
        int       logstatus = -1;

        //Log.d("cancelar_Pedido","id_vendedor: "+id_vendedor);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");
                String fecha = json_data.getString("fecha");

                if (logstatus == 1)
                {
                    // Operacion exitosa
                    Log.d("Pedido_WEB_CANCEL", "Operacion exitosa: " + fecha);
                    manager.actualizar_actualizacionesPedido(id_vendedor, fecha);
                }
                else
                {
                    Log.d("Pedido_WEB_CANCEL", "Operacion fallida");
                }
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return logstatus != 0;
        }
        else
        {
            Log.e("JSON", "ERROR");
            return false;
        }
    }

    /**
     * Valida el estatus del pedido.
     *
     * @param id_pedido ID del pedido.
     * @return True si el pedido puede ser editado, es decir, el pedido esta en espera de
     * aprobacion.
     */
    private boolean validarEstatusPedido(String id_pedido)
    {
        String estatus = "";

        Log.d("validarEstatusPedido", "INICIO");

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        postparameters2send.add(new BasicNameValuePair("id_pedido", id_pedido));
        String    URL_obtener_clientes = Constantes.IP_Server + "obtener_estatus_pedido.php";
        JSONArray jdata                = post.getserverdata(postparameters2send, URL_obtener_clientes);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                estatus = json_data.getString("estatus");

                /*for (int i = 0; i < jdata.length(); i++)
                {
                    JSONObject jsonObject = jdata.getJSONObject(i);
                    estatus = jsonObject.getString("estatus");
                }*/
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }

            Log.d("validarEstatusPedido", "FIN");
            return estatus.equalsIgnoreCase("En espera de aprobacion");
        }
        else
        {
            Log.e("JSON  ", "ERROR");
            return false;
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
            pDialog.setTitle("Cargando Pedido...");
            pDialog.setMessage("Cargando Datos...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls)
        {
            final Cursor cursor0            = manager.cargarCursorPedidos_Editar();
            final int    cantidad_productos = cursor0.getCount();
            String       talla_actual       = "P";

            Double precio_total_pedido = 0.0;

            for (cursor0.moveToFirst(); !cursor0.isAfterLast(); cursor0.moveToNext())
            {
                precio_total_pedido += Double.parseDouble(cursor0.getString(0)) * Integer.parseInt(cursor0.getString(1)) * Integer.parseInt(cursor0.getString(2));
            }

            final Double finalPrecio_total_pedido = precio_total_pedido;
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
                                texto_total_pedido.setText(Funciones.priceWithDecimal(finalPrecio_total_pedido));
                                cursor0.close();
                            }
                        });
                    }
                }
            };
            hilo_cantidad.start();

            /*
                Pedido P
            */

            Cursor cursor = manager.cargarCursorPedidos_Editar(talla_actual);

            boolean encontrado = false;

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                encontrado = true;
            }

            cursor.close();

            if (encontrado)
            {
                agregarCabecera(talla_actual);
                agregarFilasTabla(talla_actual);
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
                                    final RobotoTextView txt = new RobotoTextView(contexto);
                                    txt.setText(R.string.sinPedidosP);
                                    txt.setGravity(Gravity.CENTER);
                                    txt.setTextSize(20f);

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

            Cursor  cursor2     = manager.cargarCursorPedidos_Editar(talla_actual);
            boolean encontrado2 = false;

            for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                encontrado2 = true;
            }

            cursor2.close();

            if (encontrado2)
            {
                agregarCabecera(talla_actual);
                agregarFilasTabla(talla_actual);
            }
            else
            {

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
                                    txt.setText(R.string.sinPedidosM);
                                    txt.setGravity(Gravity.CENTER);
                                    txt.setTextSize(20f);

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

            Cursor  cursor3     = manager.cargarCursorPedidos_Editar(talla_actual);
            boolean encontrado3 = false;

            for (cursor3.moveToFirst(); !cursor3.isAfterLast(); cursor3.moveToNext())
            {
                encontrado3 = true;
            }

            cursor3.close();

            if (encontrado3)
            {
                agregarCabecera(talla_actual);
                agregarFilasTabla(talla_actual);
            }
            else
            {
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
                                    txt.setText(R.string.sinPedidosG);
                                    txt.setGravity(Gravity.CENTER);
                                    txt.setTextSize(20f);

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

    /**
     * Comprueba el estado del pedido en segundo plano.
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
                String  aprobado = "";
                boolean pedido_aprobado;

                //Comprobar localmente si el pedido fue aprobado..

                //DBAdapter manager = new DBAdapter(contexto);
                Cursor cursor = manager.cargarCursorPedidos(id_pedido);

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                {
                    aprobado = cursor.getString(0);
                }

                //manager.cerrar();
                cursor.close();

                pedido_aprobado = aprobado.equalsIgnoreCase("En espera de aprobacion");
                Log.d("Pedido Local", pedido_aprobado + "");

                if (estatus.equals(Constantes.PENDIENTE))
                {
                    return "Editable";
                }

                //Comprobar en internet si el pedido fue aprobado..
                //if (pedido_aprobado)
                //{
                if (!Funciones.isOnline(contexto))
                    return "No internet";
                else
                {
                    Log.d("Pedido Web", "Iniciando");
                    pedido_aprobado = validarEstatusPedido(id_pedido);
                }
                //}

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

                    new asynclogin_ActualizarPedido().execute();



                    /*String TAG = "fragment_pedido_editar";
                    Fragment fragment = new FragmentPedido_Editar();
                    Bundle args = new Bundle();
                    args.putString("id_pedido", id_pedido);
                    fragment.setArguments(args);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();*/

                    //Toast.makeText(contexto, "El pedido es editable!", Toast.LENGTH_LONG).show();
                    break;
                case "No internet":
                    Toast.makeText(contexto, "Debe estar conectado a internet!", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(contexto, "El pedido ya fue procesado, no puede editarse!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**
     * Actualiza el pedido en segundo plano.
     */
    private class asynclogin_ActualizarPedido extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Actualizando pedido...");
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
                //enviamos y recibimos y analizamos los datos en segundo plano.
                if (actualizar_Pedido())
                    return "ok";
                else
                    return "err";
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
                case "ok":
                    if (!estatus.equals(Constantes.PENDIENTE))
                    {
                        //Actualizar el pedido en BD Local..
                        //DBAdapter manager = new DBAdapter(contexto);
                        manager.actualizar_estado_pedido(id_pedido, "Anulado");
                        //manager.cerrar();
                    }
                    /*else
                    {
                        //Actualizar el pedido en BD Local..
                        DBAdapter manager = new DBAdapter(contexto);
                        manager.actualizar_estado_pedido_local(id_pedido,"Anulado");
                        manager.cerrar();
                    }*/

                    String TAG = "fragment_pedido_editar";
                    Fragment fragment = new FragmentMuestrario();
                    Bundle bundle = getArguments();
                    String ci = bundle.getString("cedula");

                    Bundle args = new Bundle();
                    args.putString("cedula", ci);
                    fragment.setArguments(args);

                    Funciones.cambiarTitulo("Muestrario", getActivity());

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();
                    Toast.makeText(contexto, "Pedido actualizado con exito!", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(contexto, "El pedido ya fue procesado, no puede editarse!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**
     * Elimina el pedido en segundo plano.
     */
    private class async_eliminar_pedido extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Cancelando pedido...");
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
                //enviamos y recibimos y analizamos los datos en segundo plano.
                if (cancelar_Pedido())
                    return "ok";
                else
                    return "err";
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
                case "ok":
                    manager.actualizar_estado_pedido(id_pedido, "Anulado");
                    //manager.eliminar_pedido(id_pedido); // Limpiar la BD
                    Funciones.cambiarTitulo("Muestrario", getActivity());
                    getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Toast.makeText(contexto, "!Pedido cancelado exitosamente!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Log.e("CANCELAR PEDIDO ERROR", result);
                    Toast.makeText(contexto, "Ha ocurrido un error cancelando el pedido!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}