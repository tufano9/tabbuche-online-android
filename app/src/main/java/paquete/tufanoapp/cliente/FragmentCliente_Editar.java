package paquete.tufanoapp.cliente;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import paquete.database.DBAdapter;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.global.library.Httppostaux;
import paquete.tufanoapp.R;
import paquete.tufanoapp.pedido.FragmentPedido_Realizar;

public class FragmentCliente_Editar extends Fragment
{
    private static Context contexto;
    private        boolean cliente_existe;
    private        Spinner sp_rif, sp_estados;
    private EditText edit_razonSocial, rif1, rif2, edit_direccion, edit_telefono, email;
    private ProgressDialog pDialog;
    private String         id_vendedor, id_cliente;
    private DBAdapter manager;
    private String    fecha_actualizacion;
    private boolean desdePedidos = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_cliente_editar, container, false);

        contexto = getActivity();
        manager = new DBAdapter(contexto);
        FragmentCliente.TAG_CLIENTE = "fragment_cliente_editar";

        getExtrasVar();
        initComponents(rootView);
        initSpinners(rootView);
        initButtons(rootView);
        loadValues();

        return rootView;
    }

    /**
     * Carga los valores por defecto dentro de los campos.
     */
    private void loadValues()
    {
        Cursor cursor = manager.buscarClienteByID(id_cliente);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            edit_razonSocial.setText(cursor.getString(0));
            sp_rif.setSelection(buscar_rif_posicion(cursor.getString(1).substring(0, 1)));
            rif1.setText(cursor.getString(1).substring(1, cursor.getString(1).length() - 2));
            rif2.setText(cursor.getString(1).substring(cursor.getString(1).length() - 1));
            edit_direccion.setText(cursor.getString(2));
            edit_telefono.setText(cursor.getString(3));
            sp_estados.setSelection(Funciones.buscar_estado_posicion(cursor.getString(4), contexto));
            email.setText(cursor.getString(5));
        }
        cursor.close();
    }

    /**
     * Inicializa los botones.
     *
     * @param rootView RootView
     */
    private void initButtons(View rootView)
    {
        Button btn_limpiar = (Button) rootView.findViewById(R.id.button);

        btn_limpiar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                edit_razonSocial.setText("");
                rif1.setText("");
                rif2.setText("");
                edit_direccion.setText("");
                edit_telefono.setText("");
                email.setText("");
            }
        });

        Button btn_agregar = (Button) rootView.findViewById(R.id.button2);
        btn_agregar.setText(R.string.boton_editar_cliente);

        btn_agregar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (campos_llenos())
                {
                    if (validar_campos())
                    {
                        final boolean       permitido = Funciones.FuncionalidadPermitida("Clientes", 1, id_vendedor, manager);
                        AlertDialog.Builder dialog    = new AlertDialog.Builder(contexto);

                        dialog.setMessage(R.string.dialogo_editar_cliente);
                        dialog.setCancelable(false);

                        dialog.setPositiveButton("Editar", new DialogInterface.OnClickListener()
                        {


                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                if (Funciones.isOnline(contexto))
                                {
                                    if (permitido)
                                    {
                                        String razon_social      = edit_razonSocial.getText().toString();
                                        String rif               = sp_rif.getSelectedItem().toString() + "" + rif1.getText().toString() + "-" + rif2.getText().toString();
                                        String direccion_cliente = edit_direccion.getText().toString();
                                        String telefono_cliente  = edit_telefono.getText().toString();
                                        String estado_cliente    = sp_estados.getSelectedItem().toString();
                                        String email_cliente     = email.getText().toString();

                                        new async_edit().execute(razon_social, rif, direccion_cliente, telefono_cliente, estado_cliente, email_cliente);
                                    }
                                    else
                                        Toast.makeText(contexto, Constantes.NO_PERMITIDO, Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(contexto, Constantes.NO_INTERNET, Toast.LENGTH_SHORT).show();
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
                }
                else
                {
                    Toast.makeText(contexto, "Por favor, ingrese todos los datos!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Inicializa los spinners
     *
     * @param rootView
     */
    private void initSpinners(View rootView)
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(contexto, R.array.rif_lista, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_rif.setAdapter(adapter);

        sp_estados = (Spinner) rootView.findViewById(R.id.spinner2);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                contexto, R.array.estados_lista, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_estados.setAdapter(adapter2);
    }

    /**
     * Inicializa los componentes primarios.
     *
     * @param rootView RootView.
     */
    private void initComponents(View rootView)
    {
        edit_razonSocial = (EditText) rootView.findViewById(R.id.editText_razonSocial);
        rif1 = (EditText) rootView.findViewById(R.id.editText_Rif1);
        rif2 = (EditText) rootView.findViewById(R.id.editText_Rif2);
        edit_direccion = (EditText) rootView.findViewById(R.id.editText_direccion);
        edit_telefono = (EditText) rootView.findViewById(R.id.editText_telefono);
        email = (EditText) rootView.findViewById(R.id.editText_email);
        sp_rif = (Spinner) rootView.findViewById(R.id.spinner);
    }

    /**
     * Obtiene las variables adicionales que fueron pasadas como parametros desde otra actividad.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        id_cliente = bundle.getString("id_cliente");
        id_vendedor = bundle.getString("id_usuario");
        desdePedidos = bundle.getBoolean("desdePedidos");
    }

    /**
     * Busca la posicion de un tipo de rif dentro de una lista ya definida.
     *
     * @param rif Rif a consultar la posicion.
     * @return La posicion del rif consultado. -1 Si no fue encontrado.
     */
    private int buscar_rif_posicion(String rif)
    {
        String[] lista_rif = getResources().getStringArray(R.array.rif_lista);

        for (int i = 0; i < lista_rif.length; i++)
        {
            if (lista_rif[i].equals(rif))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Valida los campos antes de editar un cliente.
     *
     * @return True si los campos estan correctos, False en caso contrario.
     */
    private boolean validar_campos()
    {
        boolean res = true;
        String  msj = "";

        if (rif1.getText().toString().length() < 7)
        {
            msj = "\nPor favor, ingrese un RIF valido (Minimo 7 Numeros)!";
            res = false;

        }
        if (edit_telefono.getText().toString().length() != 11)
        {
            msj = msj + "\nPor favor, ingrese un Telefono valido (11 Numeros)!";
            res = false;
        }
        if (Funciones.isValidEmail(email.getText().toString()))
        {
            msj = msj + "\nPor favor, ingrese un email valido!";
            res = false;
        }

        if (!res) Toast.makeText(contexto, msj + "\n", Toast.LENGTH_LONG).show();

        return res;
    }

    /**
     * Verifica si todos los campos estan llenos.
     *
     * @return True si todos estan llenos, False en caso contrario.
     */
    private boolean campos_llenos()
    {
        boolean res;
        String  razon_social      = edit_razonSocial.getText().toString();
        String  rifA              = rif1.getText().toString();
        String  rifB              = rif2.getText().toString();
        String  direccion_cliente = edit_direccion.getText().toString();
        String  telefono_cliente  = edit_telefono.getText().toString();
        String  estado_cliente    = sp_estados.getSelectedItem().toString();
        String  email_cliente     = email.getText().toString();

        res = !(razon_social.isEmpty() || rifA.isEmpty() || rifB.isEmpty() || direccion_cliente.isEmpty() || telefono_cliente.isEmpty() || estado_cliente.isEmpty() || email_cliente.isEmpty());

        return res;
    }

    /**
     * Funcion la para la edicion de un cliente, dado sus datos.
     *
     * @param razon_social      Razon social del cliente.
     * @param rif               Rif del cliente.
     * @param direccion_cliente Direccion del cliente.
     * @param telefono_cliente  Telefono del cliente.
     * @param estado_cliente    Estado del cliente.
     * @param email_cliente     Email del cliente.
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private boolean editar_cliente(String razon_social, String rif, String direccion_cliente, String telefono_cliente, String estado_cliente, String email_cliente)
    {
        boolean     bandera = false;
        Httppostaux post    = new Httppostaux();

        ArrayList<NameValuePair> listaParametros = new ArrayList<>();
        listaParametros.add(new BasicNameValuePair("id_cliente", id_cliente));
        listaParametros.add(new BasicNameValuePair("id_vendedor", id_vendedor));
        listaParametros.add(new BasicNameValuePair("razon_social", razon_social));
        listaParametros.add(new BasicNameValuePair("rif", rif));
        listaParametros.add(new BasicNameValuePair("direccion_cliente", direccion_cliente));
        listaParametros.add(new BasicNameValuePair("telefono_cliente", telefono_cliente));
        listaParametros.add(new BasicNameValuePair("estado_cliente", estado_cliente));
        listaParametros.add(new BasicNameValuePair("email_cliente", email_cliente));

        String    URL_connect = Constantes.IP_Server + "editar_cliente.php";
        JSONArray jdata       = post.getserverdata(listaParametros, URL_connect);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);

                int logstatus = json_data.getInt("salida");
                fecha_actualizacion = json_data.getString("fecha");

                if (logstatus == 1)
                {
                    bandera = true;
                    cliente_existe = false;
                }

                if (logstatus == 2)
                {
                    cliente_existe = true;
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

        return bandera;
    }

    /**
     * Clase para la edicion en segundo plano de un cliente en la BD.
     */
    class async_edit extends AsyncTask<String, String, String>
    {
        String razon_social;
        String rif;
        String direccion_cliente;
        String telefono_cliente;
        String estado_cliente;
        String email_cliente;

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Por favor espere...");
            pDialog.setMessage("Editando Cliente...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            //obtnemos usr y pass
            razon_social = params[0];
            rif = params[1];
            direccion_cliente = params[2];
            telefono_cliente = params[3];
            estado_cliente = params[4];
            email_cliente = params[5];

            //Log.d("Valores Cliente",razon_social+" "+ rif+" "+direccion_cliente+" "+telefono_cliente+" "+estado_cliente+" "+email_cliente);

            try
            {
                //enviamos y recibimos y analizamos los datos en segundo plano.
                if (editar_cliente(razon_social, rif, direccion_cliente, telefono_cliente, estado_cliente, email_cliente))
                {
                    return "ok"; //login valido
                }

                else
                {
                    return "err"; //login invalido
                }
            }

            catch (RuntimeException e)
            {
                return "err2";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();

            if (result.equals("ok"))
            {
                Toast.makeText(contexto, "Cliente editado con exito!", Toast.LENGTH_LONG).show();

                //Editar cliente en BD Local
                manager.actualizar_cliente(id_cliente, razon_social, rif, direccion_cliente, telefono_cliente, estado_cliente, email_cliente);
                manager.actualizar_actualizacionesCliente(id_vendedor, fecha_actualizacion);

                if (desdePedidos)
                {
                    Log.d("Cliente editado", "desde_pedidos");

                    Fragment fragment = new FragmentPedido_Realizar();
                    Bundle   args     = new Bundle();
                    args.putString("id_usuario", id_vendedor);
                    args.putString("id_cliente", id_cliente);
                    fragment.setArguments(args);

                    FragmentCliente.TAG_CLIENTE = "fragment_cliente_editar_fin";

                    Funciones.cambiarTitulo("Muestrario", getActivity());

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
                }
                else
                {
                    Log.d("Cliente editado", "normal");

                    Fragment fragment = new FragmentCliente();
                    Bundle   args     = new Bundle();
                    args.putString("id_usuario", id_vendedor);
                    fragment.setArguments(args);

                    Funciones.cambiarTitulo("Muestrario", getActivity());

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();

                    FragmentCliente.TAG_CLIENTE = "fragment_cliente_editar_fin";
                }
            }

            else
            {
                if (cliente_existe)
                    Toast.makeText(contexto, "Ya existe un cliente con el mismo RIF / Razon Social!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(contexto, "Ha ocurrido un error editando al cliente!", Toast.LENGTH_LONG).show();
            }

        }
    }
}