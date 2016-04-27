package paquete.tufanoapp.cliente;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class FragmentCliente_Agregar extends Fragment
{
    private static Context contexto;
    //Fragment fragment = null;
    private        boolean cliente_existe;
    private        Spinner sp_rif, sp_estados;
    private EditText edit_razonSocial, rif1, rif2, direccion, telefono, email;
    private ProgressDialog pDialog;
    private Httppostaux    post;
    private String         id_vendedor, id_cliente, fecha_ingreso;
    private DBAdapter manager;
    private boolean desde_pedidos = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_cliente_registrar, container, false);
        FragmentCliente.TAG_CLIENTE = "fragment_cliente_agregar";

        contexto = getActivity();
        manager = new DBAdapter(contexto);
        post = new Httppostaux();

        getExtrasVar();
        initComponents(rootView);
        initSpinners();
        initButtons(rootView);

        return rootView;
    }

    /**
     * Inicializa los botones.
     *
     * @param rootView RottView
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
                direccion.setText("");
                telefono.setText("");
                email.setText("");
            }
        });

        Button btn_agregar = (Button) rootView.findViewById(R.id.button2);

        btn_agregar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (campos_llenos())
                {
                    if (validar_campos())
                    {
                        final boolean       permitido = Funciones.FuncionalidadPermitida("Clientes", 2, id_vendedor, manager);
                        AlertDialog.Builder dialog    = new AlertDialog.Builder(contexto);

                        dialog.setMessage(R.string.confirmacion_agregar_cliente);
                        dialog.setCancelable(false);

                        dialog.setPositiveButton("Agregar", new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                String razon_social      = edit_razonSocial.getText().toString();
                                String rif               = sp_rif.getSelectedItem().toString() + "" + rif1.getText().toString() + "-" + rif2.getText().toString();
                                String direccion_cliente = direccion.getText().toString();
                                String telefono_cliente  = telefono.getText().toString();
                                String estado_cliente    = sp_estados.getSelectedItem().toString();
                                String email_cliente     = email.getText().toString();

                                if (Funciones.isOnline(contexto))
                                    if (permitido)
                                        new async_add().execute(razon_social, rif, direccion_cliente, telefono_cliente, estado_cliente, email_cliente);
                                    else
                                        Toast.makeText(contexto, Constantes.NO_PERMITIDO, Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(contexto, Constantes.NO_INTERNET, Toast.LENGTH_LONG).show();
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
     * Inicializa los spinners.
     */
    private void initSpinners()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(contexto, R.array.rif_lista, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_rif.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                contexto, R.array.estados_lista, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_estados.setAdapter(adapter2);
    }

    /**
     * Inicializa los componentes dl activity.
     *
     * @param rootView RootView
     */
    private void initComponents(View rootView)
    {
        edit_razonSocial = (EditText) rootView.findViewById(R.id.editText_razonSocial);
        rif1 = (EditText) rootView.findViewById(R.id.editText_Rif1);
        rif2 = (EditText) rootView.findViewById(R.id.editText_Rif2);
        direccion = (EditText) rootView.findViewById(R.id.editText_direccion);
        telefono = (EditText) rootView.findViewById(R.id.editText_telefono);
        email = (EditText) rootView.findViewById(R.id.editText_email);
        sp_rif = (Spinner) rootView.findViewById(R.id.spinner);
        sp_estados = (Spinner) rootView.findViewById(R.id.spinner2);
    }

    /**
     * Obtiene las variables que fueron pasadas como parametros desde otra activity.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        id_vendedor = bundle.getString("id_usuario");
        desde_pedidos = bundle.getBoolean("desdePedidos");
    }

    /**
     * Valida los campos ingresados por el usuario al momento de agregar cliente.
     *
     * @return True si los campos son validos, False en caso contrario.
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
        if (telefono.getText().toString().length() != 11)
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
     * Verifica que todos los campos esten llenos
     *
     * @return True si los campos estan llenos, False en caso contrario.
     */
    private boolean campos_llenos()
    {
        boolean res;
        String  razon_social      = edit_razonSocial.getText().toString();
        String  rifA              = rif1.getText().toString();
        String  rifB              = rif2.getText().toString();
        String  direccion_cliente = direccion.getText().toString();
        String  telefono_cliente  = telefono.getText().toString();
        String  estado_cliente    = sp_estados.getSelectedItem().toString();
        String  email_cliente     = email.getText().toString();

        res = !(razon_social.isEmpty() || rifA.isEmpty() || rifB.isEmpty() || direccion_cliente.isEmpty() || telefono_cliente.isEmpty() || estado_cliente.isEmpty() || email_cliente.isEmpty());

        return res;
    }

    /**
     * Funcion encargada de agregar un cliente en la BD.
     *
     * @param razon_social      Razon social del cliente.
     * @param rif               Rif del cliente.
     * @param direccion_cliente Direccion del cliente.
     * @param telefono_cliente  Telefono del cliente.
     * @param estado_cliente    Estado del cliente.
     * @param email_cliente     Email del cliente.
     * @return True si el cliente fue agregado exitosamente
     */
    private boolean agregar_cliente(String razon_social, String rif, String direccion_cliente, String telefono_cliente, String estado_cliente, String email_cliente)
    {
        boolean bandera = false;
        post = new Httppostaux();

        ArrayList<NameValuePair> listaParametros = new ArrayList<>();
        listaParametros.add(new BasicNameValuePair("id_vendedor", id_vendedor));
        listaParametros.add(new BasicNameValuePair("razon_social", razon_social));
        listaParametros.add(new BasicNameValuePair("rif", rif));
        listaParametros.add(new BasicNameValuePair("direccion_cliente", direccion_cliente));
        listaParametros.add(new BasicNameValuePair("telefono_cliente", telefono_cliente));
        listaParametros.add(new BasicNameValuePair("estado_cliente", estado_cliente));
        listaParametros.add(new BasicNameValuePair("email_cliente", email_cliente));

        String    URL_connect = Constantes.IP_Server + "agregar_cliente.php";
        JSONArray jdata       = post.getserverdata(listaParametros, URL_connect);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);

                int logstatus = json_data.getInt("salida");
                id_cliente = json_data.getString("id_cliente");
                fecha_ingreso = json_data.getString("fecha_ingreso");

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
     * Clase para agregar en segundo plano un cliente en la BD.
     */
    class async_add extends AsyncTask<String, String, String>
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
            pDialog.setMessage("Agregando Cliente...");
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
                if (agregar_cliente(razon_social, rif, direccion_cliente, telefono_cliente, estado_cliente, email_cliente))
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
                Toast.makeText(contexto, "Cliente agregado con exito!", Toast.LENGTH_LONG).show();

                manager.insertar_cliente(id_cliente, id_vendedor, razon_social, rif, direccion_cliente, telefono_cliente, estado_cliente, email_cliente, fecha_ingreso);

                if (desde_pedidos)
                {
                    Log.d("Cliente agregado", "desde_pedidos");

                    Fragment fragment = new FragmentPedido_Realizar();
                    Bundle   args     = new Bundle();
                    args.putString("id_usuario", id_vendedor);
                    args.putString("id_cliente", id_cliente);
                    fragment.setArguments(args);

                    FragmentCliente.TAG_CLIENTE = "fragment_pedido_agregar_fin";

                    Funciones.cambiarTitulo("Muestrario", getActivity());

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
                }
                else
                {
                    Log.d("Cliente agregado", "normal");
                    Fragment fragment = new FragmentCliente();
                    Bundle   args     = new Bundle();
                    args.putString("id_usuario", id_vendedor);
                    fragment.setArguments(args);

                    FragmentCliente.TAG_CLIENTE = "fragment_cliente_agregar_fin";

                    Funciones.cambiarTitulo("Muestrario", getActivity());

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
                }
            }

            else
            {
                if (cliente_existe)
                    Toast.makeText(contexto, "Ya existe un cliente con el mismo RIF / Razon Social!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(contexto, "Ha ocurrido un error agregando al cliente!", Toast.LENGTH_LONG).show();
            }

        }
    }

}