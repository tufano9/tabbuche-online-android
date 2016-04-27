package paquete.tufanoapp;

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

import java.io.File;

import paquete.database.DBAdapter;
import paquete.dialogos.DialogoEliminar_Productos;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.global.Funciones_Pedidos;
import paquete.recycle_bitmap.RecyclingImageView;

public class FragmentPedido_Muestrario extends Fragment
{
    private static Context contexto;
    private View rootView;
    private String id_vendedor, total_costo = "0.00";
    private int w;
    private Resources rs;
    private RobotoTextView texto_total_pedido, total_bultos, total_pares;
    private DBAdapter manager;
    private Typeface typeface, typeface_2;

    private TableLayout tabla_numeraciones, cabecera_numeraciones;
    private TableRow.LayoutParams layoutFila, layout_producto, layout_numeracion;
    private TableRow.LayoutParams layout_color, layout_paresxbulto;
    private TableRow.LayoutParams layout_cantidad, layout_preciounitario, layout_subtotal;

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

        rootView = inflater.inflate(R.layout.fragment_pedido, container, false);
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
                        Log.i("AndroidTabs", "Pulsada pestaña: " + "pedido_p");
                        break;

                    case "pedido_m":
                        Log.i("AndroidTabs", "Pulsada pestaña: " + "pedido_m");
                        break;

                    case "pedido_g":
                        Log.i("AndroidTabs", "Pulsada pestaña: " + "pedido_g");
                        break;
                }
            }
        });
    }

    /**
     * Inicializa los botones
     */
    private void initButtons()
    {
        Cursor cursor = manager.cargarCursorProductosPedidos();
        w = 0;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            w++;
        }
        cursor.close();

        Button btn1 = (Button) rootView.findViewById(R.id.btn_pedido1);
        Button btn2 = (Button) rootView.findViewById(R.id.btn_pedido2);
        Button btn3 = (Button) rootView.findViewById(R.id.btn_pedido3);
        texto_total_pedido = (RobotoTextView) rootView.findViewById(R.id.texto_total_pedido);

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
                        //DBAdapter manager = new DBAdapter(contexto);
                        manager.eliminar_pedido(); // Limpiar la BD
                        //manager.cerrar();
                        getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        Toast.makeText(contexto, "¡Pedido cancelado exitosamente!", Toast.LENGTH_SHORT).show();
                        //new async_add().execute(razon_social, rif, direccion_cliente, telefono_cliente, estado_cliente, email_cliente);
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
        });

        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (w > 0)
                {
                    DialogFragment newFragment = new DialogoEliminar_Productos();

                    Bundle args = new Bundle();
                    args.putString("id_vendedor", id_vendedor);
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
                    String TAG = "fragment_pedido_seleccionar_cliente";
                    Fragment fragment = new FragmentPedido_Realizar();
                    Bundle args = new Bundle();
                    args.putString("id_usuario", id_vendedor);
                    args.putString("id_cliente", null);
                    fragment.setArguments(args);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();
                }
                else
                {
                    Toast.makeText(contexto, "El pedido esta vacio!", Toast.LENGTH_SHORT).show();
                }
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
        id_vendedor = bundle.getString("id_usuario");
    }

    /**
     * Genera la tabla de informacion.
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
     * Agrega la cabecera a la tabla.
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
                            txtProducto.setGravity(Gravity.CENTER);
                            //txtProducto.setTextAppearance(contexto, R.style.etiqueta);
                            txtProducto.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtProducto.setLayoutParams(layout_producto);

                            txtNumeracion.setText(rs.getString(R.string.numeracion));
                            txtNumeracion.setGravity(Gravity.CENTER);
                            //txtNumeracion.setTextAppearance(contexto, R.style.etiqueta);
                            txtNumeracion.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtNumeracion.setLayoutParams(layout_numeracion);

                            txtColor.setText(rs.getString(R.string.color_pedido));
                            txtColor.setGravity(Gravity.CENTER);
                            //txtColor.setTextAppearance(contexto, R.style.etiqueta);
                            txtColor.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtColor.setLayoutParams(layout_color);

                            txtparesxbulto.setText(rs.getString(R.string.pares_pedido));
                            txtparesxbulto.setGravity(Gravity.CENTER);
                            //txtparesxbulto.setTextAppearance(contexto, R.style.etiqueta);
                            txtparesxbulto.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtparesxbulto.setLayoutParams(layout_paresxbulto);

                            txtCantidad.setText(rs.getString(R.string.bultos_pedido));
                            txtCantidad.setGravity(Gravity.CENTER);
                            //txtCantidad.setTextAppearance(contexto, R.style.etiqueta);
                            txtCantidad.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtCantidad.setLayoutParams(layout_cantidad);

                            txtPrecioUnitario.setText(rs.getString(R.string.precio_unitario));
                            txtPrecioUnitario.setGravity(Gravity.CENTER);
                            //txtPrecioUnitario.setTextAppearance(contexto, R.style.etiqueta);
                            txtPrecioUnitario.setBackgroundResource(R.drawable.tabla_celda_cabecera);
                            txtPrecioUnitario.setLayoutParams(layout_preciounitario);

                            txtSubtotal.setText(rs.getString(R.string.precio_total));
                            txtSubtotal.setGravity(Gravity.CENTER);
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
     * @param tipo P, M o G.
     */
    private void agregarFilasTabla(final String tipo)
    {
        int y = 0;

        Cursor cursor = manager.cargarCursorProductosPedidos_porTalla(tipo);

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
            nombre_producto[y] = cursor.getString(0);
            //tipo_producto[y] = cursor.getString(1);
            precio_producto[y] = cursor.getString(2);
            numeracion_producto[y] = cursor.getString(3);
            pares_producto[y] = cursor.getString(4);
            ids[y] = cursor.getString(5);
            pares_usuario[y] = cursor.getString(6);
            bultos += Integer.parseInt(pares_usuario[y]);
            //pares += Integer.parseInt(pares_producto[y]);
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
                                NumberPicker cantidad_pares = new NumberPicker(contexto);

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
                                //txtId.setTextAppearance(contexto,R.style.etiqueta);
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
                                //cantidad_pares.setWrapSelectorWheel(false);

                                /*
                                    Columna 5: Precio unitario
                                */

                                String pUni = precio_producto[finalI] + " " + Constantes.MONEDA;
                                preciounitario.setText(pUni);
                                preciounitario.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                                //preciounitario.setTextAppearance(contexto,R.style.etiqueta);
                                preciounitario.setBackgroundResource(R.drawable.tabla_celda);
                                preciounitario.setLayoutParams(layout_preciounitario);

                                /*
                                    Columna 6: Subtotal
                                */
                                //Log.d("SUBTOTAL",Double.parseDouble(String.valueOf(preciounitario.getText()).substring(0,String.valueOf(preciounitario.getText()).lastIndexOf("B")))+" * "+Double.parseDouble(String.valueOf(paresxbulto.getText())));
                                double subtotal_articulo = cantidad_pares.getValue() * Double.parseDouble(String.valueOf(preciounitario.getText()).substring(0, String.valueOf(preciounitario.getText()).lastIndexOf("B"))) * Double.parseDouble(String.valueOf(paresxbulto.getText()));

                                String pp = Funciones.priceWithDecimal(subtotal_articulo) + " " + Constantes.MONEDA;
                                subtotal.setText(pp);
                                subtotal.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                                //subtotal.setTextAppearance(contexto,R.style.etiqueta);
                                subtotal.setBackgroundResource(R.drawable.tabla_celda);
                                subtotal.setLayoutParams(layout_subtotal);

                                total_costo = String.valueOf(Double.parseDouble(total_costo) + Double.parseDouble(String.valueOf(subtotal_articulo)));

                                final RobotoTextView finalSubtotal = subtotal;
                                final RobotoTextView finalPreciounitario = preciounitario;
                                final RobotoTextView finalparesxbulto = paresxbulto;
                                cantidad_pares.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
                                {
                                    @Override
                                    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
                                    {

                                        String precio_viejo = String.valueOf(finalPreciounitario.getText());
                                        String pares = String.valueOf(finalparesxbulto.getText());
                                        double precio_nuevo = Integer.parseInt(pares) * newVal * Double.parseDouble(precio_viejo.substring(0, precio_viejo.lastIndexOf("B")));
                                        Double res = 0.0;
                                        Double res2 = 0.0;
                                        int bultos_actualizados = 0, pares_actualizados = 0;
                                        int pares_t = 0, bultos_t = 0;

                                        String sub = Funciones.priceWithDecimal(precio_nuevo) + " " + Constantes.MONEDA;
                                        finalSubtotal.setText(sub);
                                        manager.actualizar_producto_pedido_cantidad(ids[finalI], newVal);

                                        Cursor cursor = manager.cargarCursorProductosPedidos_porTalla(tipo);
                                        Cursor cursor2 = manager.cargarCursorProductosPedidos_porTalla();

                                        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                                        {
                                            Log.i("OPERACION", Double.valueOf(cursor.getString(2)) + " * " + Integer.parseInt(cursor.getString(4)) + " * " + Integer.parseInt(cursor.getString(6)));
                                            res2 += Double.valueOf(cursor.getString(2)) * Integer.parseInt(cursor.getString(4)) * Integer.parseInt(cursor.getString(6));
                                            bultos_actualizados += Integer.parseInt(cursor.getString(6));
                                            pares_actualizados += (Integer.parseInt(cursor.getString(4)) * Integer.parseInt(cursor.getString(6)));
                                        }

                                        for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
                                        {
                                            res += Double.valueOf(cursor2.getString(2)) * Integer.parseInt(cursor2.getString(4)) * Integer.parseInt(cursor2.getString(6));
                                            pares_t += cursor2.getInt(4) * cursor2.getInt(6);
                                            bultos_t += cursor2.getInt(6);
                                        }

                                        cursor.close();
                                        cursor2.close();

                                        if (tipo.equals("P"))
                                        {
                                            total_bultos_P.setText(Funciones.priceWithDecimal((double) (bultos_actualizados)));
                                            totalpares_P.setText(Funciones.priceWithDecimal((double) (pares_actualizados)));
                                            String p = Funciones.priceWithDecimal(res2) + " " + Constantes.MONEDA;
                                            total_monto_P.setText(p);
                                        }
                                        if (tipo.equals("M"))
                                        {
                                            total_bultos_M.setText(Funciones.priceWithDecimal((double) (bultos_actualizados)));
                                            totalpares_M.setText(Funciones.priceWithDecimal((double) (pares_actualizados)));
                                            String p = Funciones.priceWithDecimal(res2) + " " + Constantes.MONEDA;
                                            total_monto_M.setText(p);
                                        }
                                        if (tipo.equals("G"))
                                        {
                                            total_bultos_G.setText(Funciones.priceWithDecimal((double) (bultos_actualizados)));
                                            totalpares_G.setText(Funciones.priceWithDecimal((double) (pares_actualizados)));
                                            String p = Funciones.priceWithDecimal(res2) + " " + Constantes.MONEDA;
                                            total_monto_G.setText(p);
                                        }

                                        total_pares.setText(Funciones.priceWithDecimal((double) (pares_t)));
                                        total_bultos.setText(Funciones.priceWithDecimal((double) (bultos_t)));
                                        texto_total_pedido.setText(Funciones.priceWithDecimal(res));
                                    }
                                });

                                fila.addView(vertical_layout);
                                fila.addView(vertical_layout2);
                                fila.addView(paresxbulto);
                                fila.addView(cantidad_pares);
                                fila.addView(preciounitario);
                                fila.addView(subtotal);

                                if (tipo.equals("P"))
                                    tabla_P.addView(fila);
                                if (tipo.equals("M"))
                                    tabla_M.addView(fila);
                                if (tipo.equals("G"))
                                    tabla_G.addView(fila);

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
            pDialog.setTitle("Cargando Pedido...");
            pDialog.setMessage("Cargando Datos...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls)
        {
            //DBAdapter manager0 = new DBAdapter(contexto);
            final Cursor cursor0 = manager.cargarCursorProductosPedidos();
            String talla_actual = "P";

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
                                int cantidad_productos = cursor0.getCount();
                                RobotoTextView texto = (RobotoTextView) rootView.findViewById(R.id.txt_cantidad_productos);
                                texto.setText(Funciones.priceWithDecimal((double) (cantidad_productos)));
                                RobotoTextViewUtils.setTypeface(texto, typeface);

                                int pares = 0, bultos = 0;
                                Double precio = 0.0;

                                for (cursor0.moveToFirst(); !cursor0.isAfterLast(); cursor0.moveToNext())
                                {
                                    pares += cursor0.getInt(4) * cursor0.getInt(6);
                                    bultos += cursor0.getInt(6);
                                    precio += cursor0.getDouble(2) * cursor0.getInt(4) * cursor0.getInt(6);
                                }

                                total_pares.setText(Funciones.priceWithDecimal((double) (pares)));
                                total_bultos.setText(Funciones.priceWithDecimal((double) (bultos)));
                                texto_total_pedido.setText(Funciones.priceWithDecimal(precio));
                                cursor0.close();

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

            Cursor cursor = manager.cargarCursorProductosPedidos_porTalla(talla_actual);
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

            Cursor cursor2 = manager.cargarCursorProductosPedidos_porTalla(talla_actual);
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

            Cursor cursor3 = manager.cargarCursorProductosPedidos_porTalla(talla_actual);
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
                                    txt.setTextSize(20f);
                                    txt.setGravity(Gravity.CENTER);
                                    RobotoTextViewUtils.setTypeface(txt, typeface);
                                    //txt.setTextAppearance(contexto, R.style.texto_galeria_1);

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