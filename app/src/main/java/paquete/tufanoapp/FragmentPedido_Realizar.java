package paquete.tufanoapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoTextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import paquete.Droidlogin.library.Httppostaux;
import paquete.database.DBAdapter;
import paquete.global.Constantes;
import paquete.global.Funciones;

/**
 * Desarrollado por Gerson el 11/4/2015.
 */
public class FragmentPedido_Realizar extends Fragment
{
    private static Context contexto;
    private RobotoTextView sp_rif, sp_estados, rif1, rif2, direccion, telefono, email;
    private EditText observaciones;
    private RobotoTextView edit_razonSocial;
    private ValueAdapter valueAdapter;
    private boolean cliente_seleccionado = false;
    private DBAdapter manager;
    private ListView lista;
    private ArrayList<String> columnArray1, columnArray2;
    private String id_cliente, id_usuario, id_cliente_agregar;
    private String[] elemento;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        onDestroy();
        final View rootView = inflater.inflate(R.layout.fragment_pedido_realizar, container, false);

        contexto = getActivity();
        manager = new DBAdapter(contexto);

        getExtrasVar();
        initComponents(rootView);
        initButtons(rootView);
        cargarDatosCliente();
        initListView(rootView);
        initAutoComplete(rootView);
        selectDefaultCliente();

        return rootView;
    }

    /**
     * Carga los datos del cliente a partir de un ID.
     */
    private void cargarDatosCliente()
    {
        Cursor cursor = manager.cargarCursorClientesByID(id_usuario);
        columnArray1 = new ArrayList<>(); // nombre_clientes
        columnArray2 = new ArrayList<>(); // id_clientes

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            columnArray1.add(cursor.getString(0));
            columnArray2.add(cursor.getString(1));
        }
        cursor.close();
        elemento = columnArray1.toArray(new String[columnArray1.size()]);
    }

    /**
     * Selecciona el cliente por defecto.
     */
    private void selectDefaultCliente()
    {
        if (id_cliente_agregar != null)
        {
            int posx = buscarClienteLista(id_cliente_agregar);
            Log.d("buscarClienteLista", "respuesta: " + posx);
            //lista.setSelection(posx);
            lista.setItemChecked(posx, true);
            lista.performItemClick(lista.getSelectedView(), posx, 0);
        }
    }

    /**
     * Inicializa los autoComplete
     *
     * @param rootView RootView
     */
    private void initAutoComplete(View rootView)
    {
        AutoCompleteTextView busqueda = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView_buscarCliente);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(contexto, android.R.layout.simple_dropdown_item_1line, elemento);
        //adapter.remove(adapter.getItem(0));
        busqueda.setAdapter(adapter1);
        busqueda.addTextChangedListener(watch);
    }

    /**
     * Inicializa los listView
     *
     * @param columnArray1 Datos para la lista.
     * @param rootView     RootView
     */
    private void initListView(final View rootView)
    {
        lista = (ListView) rootView.findViewById(R.id.listView_clientes);
        valueAdapter = new ValueAdapter(columnArray1, columnArray2, contexto);
        lista.setAdapter(valueAdapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                id_cliente = String.valueOf(valueAdapter.obtener_id_Cliente(position));
                Log.d("id_cliente", id_cliente);
                cliente_seleccionado = true;
                Cursor cursor = manager.buscarClienteByID(String.valueOf(valueAdapter.obtener_id_Cliente(position)));

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                {
                    edit_razonSocial.setText(cursor.getString(0));
                    sp_rif.setText(cursor.getString(1).substring(0, 1));
                    rif1.setText(cursor.getString(1).substring(1, cursor.getString(1).length() - 2));
                    rif2.setText(cursor.getString(1).substring(cursor.getString(1).length() - 1));
                    direccion.setText(cursor.getString(2));
                    telefono.setText(Funciones.formatoTelefono(cursor.getString(3)));
                    sp_estados.setText(cursor.getString(4));
                    email.setText(cursor.getString(5));
                }
                cursor.close();
                TextView guion1 = (TextView) rootView.findViewById(R.id.guion1);
                guion1.setVisibility(View.VISIBLE);
                TextView guion2 = (TextView) rootView.findViewById(R.id.guion2);
                guion2.setVisibility(View.VISIBLE);

                //lista.setItemChecked(position, true);
                //view.setSelected(true); // <== Will cause the highlight to remain
            }
        });
    }

    /**
     * Inicializa los botones.
     *
     * @param rootView RootView
     */
    private void initButtons(View rootView)
    {
        Button btn_realizar_pedido = (Button) rootView.findViewById(R.id.btn_realizar_pedido);

        btn_realizar_pedido.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (cliente_seleccionado)
                {
                    final boolean permitido = Funciones.FuncionalidadPermitida("Pedidos", 2, id_usuario, manager);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(contexto);

                    dialog.setMessage(R.string.confirmacion_realizar_pedido)
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_menu_send)
                            .setTitle(R.string.pedido_btn_3);

                    dialog.setPositiveButton("Realizar pedido", new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (Funciones.isOnline(contexto))
                                if (permitido)
                                    new realizarPedido().execute();
                                else
                                    Toast.makeText(contexto, Constantes.NO_PERMITIDO, Toast.LENGTH_SHORT).show();
                            else
                                new realizarPedido_Offline().execute();
                            //Toast.makeText(contexto,"Debe estar conectado a internet!",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(contexto, "Debes seleccionar un cliente!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btn_agregar_cliente = (Button) rootView.findViewById(R.id.btn_agregar_cliente);
        btn_agregar_cliente.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String TAG = "fragment_pedido_agregar_cliente";
                Fragment fragment = new FragmentCliente_Agregar();

                Bundle args = new Bundle();
                args.putString("id_usuario", id_usuario);
                args.putBoolean("desdePedidos", true);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();

            }
        });

        Button btn_editar_cliente = (Button) rootView.findViewById(R.id.btn_editar_cliente);
        btn_editar_cliente.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (cliente_seleccionado || id_cliente_agregar != null)
                {
                    String TAG = "fragment_pedido_editar_cliente";
                    Fragment fragment = new FragmentCliente_Editar();

                    Bundle args = new Bundle();
                    args.putString("id_cliente", id_cliente_agregar != null ? id_cliente_agregar : id_cliente);
                    args.putString("id_usuario", id_usuario);
                    args.putBoolean("desdePedidos", true);
                    fragment.setArguments(args);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();
                }
                else
                {
                    Toast.makeText(contexto, "Debes seleccionar un cliente!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Inicializa los componentes primarios.
     *
     * @param rootView RootView.
     */
    private void initComponents(View rootView)
    {
        edit_razonSocial = (RobotoTextView) rootView.findViewById(R.id.editText_razonSocial);
        rif1 = (RobotoTextView) rootView.findViewById(R.id.editText_Rif1);
        rif2 = (RobotoTextView) rootView.findViewById(R.id.editText_Rif2);
        direccion = (RobotoTextView) rootView.findViewById(R.id.editText_direccion);
        telefono = (RobotoTextView) rootView.findViewById(R.id.editText_telefono);
        email = (RobotoTextView) rootView.findViewById(R.id.editText_email);
        sp_rif = (RobotoTextView) rootView.findViewById(R.id.editText_Rif0);
        sp_estados = (RobotoTextView) rootView.findViewById(R.id.editTextEstado);
        observaciones = (EditText) rootView.findViewById(R.id.observaciones);
    }

    /**
     * Obtiene las variables adicionales que fueron pasadas como parametros desde otra actividad.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        id_usuario = bundle.getString("id_usuario");
        id_cliente_agregar = bundle.getString("id_cliente");
    }

    /**
     * Busca un cliente de la lista.
     *
     * @param id_cliente ID del cliente a buscar.
     * @return Posicion del cliente en la lista.
     */
    private int buscarClienteLista(String id_cliente)
    {
        Log.d("buscarClienteLista", "INICIO");
        for (int i = 0; i < lista.getCount(); i++)
            if (columnArray2.get(i).equals(id_cliente))
                return i;

        Log.d("buscarClienteLista", "FIN :(");
        return -1;
    }

    private final TextWatcher watch = new TextWatcher()
    {
        @Override
        public void afterTextChanged(Editable arg0)
        {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c)
        {
            valueAdapter.getFilter().filter(s);
        }
    };

    /**
     * Clase para realizar en segundo plano un pedido de forma online (web)
     */
    private class realizarPedido extends AsyncTask<String, Void, String>
    {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Realizando Pedido...");
            pDialog.setMessage("Por favor espere...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls)
        {
            if (enviarPedido())
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
                manager.eliminar_pedido(); // Limpiar la BD
                Funciones.cambiarTitulo("Pedidos", getActivity());

                String TAG = "fragment_pedido_consultar";
                Fragment fragment = new FragmentPedido_Consultar();
                Bundle args = new Bundle();
                args.putString("id_usuario", id_usuario);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();

                //getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Toast.makeText(contexto, "¡Pedido realizado exitosamente!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(contexto, "¡Ha ocurrido un error realizando el pedido!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Clase para realizar en segundo plano un pedido de forma offline (local).
     */
    private class realizarPedido_Offline extends AsyncTask<String, Void, String>
    {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Realizando Pedido (Solo Localmente)...");
            pDialog.setMessage("Por favor espere...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls)
        {
            if (enviarPedido_Offline())
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
                manager.eliminar_pedido(); // Limpiar la BD

                Funciones.cambiarTitulo("Pedidos", getActivity());

                String TAG = "fragment_pedido_consultar";
                Fragment fragment = new FragmentPedido_Consultar();
                Bundle args = new Bundle();
                args.putString("id_usuario", id_usuario);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();

                //getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Toast.makeText(contexto, "¡Pedido local realizado exitosamente!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(contexto, "¡Ha ocurrido un error realizando el pedido!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Envia el pedido a la BD externa (Web)
     *
     * @return True si el proceso fue exitoso, False en caso contrario.
     */
    private boolean enviarPedido()
    {
        Httppostaux post = new Httppostaux();
        String URL_realizar_pedido = Constantes.IP_Server + "realizar_pedido.php";
        int logstatus = -1;

        Bundle bundle = getArguments();
        String id_vendedor = bundle.getString("id_usuario");
        Cursor cursor = manager.cargarCursorProductosPedidos();
        String id_productos = "", precios = "", observaciones_texto, numeracion = "";
        String cantidad_pares = "", cantidad_bultos = "", precio_unitario = "", subtotal = "";
        int multiplicador;
        BigDecimal total_monto = new BigDecimal("0.00");

        observaciones_texto = String.valueOf(observaciones.getText());

        double monto = 0;
        int y = 0;

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
            Log.d("Precio Formateado", Funciones.priceWithDecimal(monto) + " Precio sin formatear: " + monto + " Sumatoria: " + total_monto);

            Log.d("SUMA MONTOS", "Sumando: " + Double.parseDouble(cursor.getString(2)) + " * " + Integer.parseInt(cursor.getString(4)) + " * " + multiplicador);
            y++;
        }
        cursor.close();

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
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

        JSONArray jdata = post.getserverdata(postparameters2send, URL_realizar_pedido);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");
                String id_pedido = json_data.getString("id_pedido");
                String fecha_ingreso = json_data.getString("fecha_ingreso");
                String codigo_pedido = json_data.getString("codigo_pedido");
                /*String error = json_data.getString("error");

                if(!error.equals(""))
                    Toast.makeText(contexto, "¡ERROR 1! "+error, Toast.LENGTH_SHORT).show();*/

                if (logstatus == 1)
                {
                    manager.insertar_pedido(id_pedido, id_vendedor, id_cliente, fecha_ingreso, monto, "En espera de aprobacion", observaciones, codigo_pedido, contexto);
                    manager.insertar_pedido_detalles(id_pedido, id_productos, cantidad_pares, cantidad_bultos, numeracion, precio_unitario, subtotal);
                    manager.actualizar_actualizacionesPedido(id_vendedor, fecha_ingreso);
                }
            }
            catch (JSONException e)
            {
                Toast.makeText(contexto, "¡ERROR 2! ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            return logstatus != 0;
        }
        else
        {
            Toast.makeText(contexto, "¡ERROR 3!", Toast.LENGTH_SHORT).show();
            Log.e("JSON  ", "ERROR");
            return false;
        }
    }

    /**
     * Realiza el pedido de forma Offline.
     *
     * @return True si el proceso fue exitoso, False en caso contrario.
     */
    private boolean enviarPedido_Offline()
    {
        Bundle bundle = getArguments();
        String id_vendedor = bundle.getString("id_usuario");
        String fecha_ingreso;

        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);

        // 2015-05-12 10:40:19

        String month = String.format("%02d", c.get(Calendar.MONTH) + 1);
        String day = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
        String year = String.format("%02d", c.get(Calendar.YEAR));
        String minutes = String.format("%02d", c.get(Calendar.MINUTE));
        String seconds = String.format("%02d", c.get(Calendar.SECOND));

        fecha_ingreso = year + "-" + month + "-" + day + " " + hora + ":" + minutes + ":" + seconds;
        Log.d("Calendar", "Fecha Actual: " + fecha_ingreso);
        String codigo_pedido = "Sin Codigo";

        Cursor cursor = manager.cargarCursorProductosPedidos();

        String id_productos = "", precios = "", observaciones_texto, numeracion = "";
        String cantidad_pares = "", cantidad_bultos = "", precio_unitario = "", subtotal = "";
        int multiplicador;
        BigDecimal total_monto = new BigDecimal("0.00");

        observaciones_texto = String.valueOf(observaciones.getText());

        double monto = 0;
        int y = 0;

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
            Log.d("Precio Formateado", Funciones.priceWithDecimal(monto) + " Precio sin formatear: " + monto + " Sumatoria: " + total_monto);
            Log.d("SUMA MONTOS", "Sumando: " + Double.parseDouble(cursor.getString(2)) + " * " + Integer.parseInt(cursor.getString(4)) + " * " + multiplicador);
            y++;
        }
        cursor.close();

        long id_pedido = manager.insertar_pedido_local(id_vendedor, id_cliente, fecha_ingreso, monto, Constantes.PENDIENTE, observaciones_texto, codigo_pedido);
        Log.d("id_pedido_nuevo", "" + id_pedido);

        if (id_pedido != -1)
        {
            manager.insertar_pedido_local_detalles(String.valueOf(id_pedido), id_productos, cantidad_pares, cantidad_bultos, numeracion, precio_unitario, subtotal);
            return true;
        }

        return false;
    }
}