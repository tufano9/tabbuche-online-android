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

import paquete.Droidlogin.library.Httppostaux;
import paquete.database.DBAdapter;
import paquete.global.Constantes;
import paquete.global.Funciones;

/**
 * Desarrollado por Gerson el 16/6/2015.
 */
public class FragmentPerfil extends Fragment
{
    private EditText nombre, apellido, telefono, email;
    private Spinner estado;
    private Context contexto;
    private ProgressDialog pDialog;
    private DBAdapter manager;
    private String codigo_usuario, fecha;
    private boolean datos_invalidos, telefono_invalido = false, email_invalido = false;
    private String cedula, id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        onDestroy();
        final View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);
        contexto = getActivity();
        manager = new DBAdapter(contexto);

        getExtrasVar();
        initComponents(rootView);
        initSpinners();
        FillValues();

        initButtons(rootView);

        return rootView;
    }

    /**
     * Llena los campos con informacion de la BD.
     */
    private void FillValues()
    {
        Cursor cursor = manager.buscarUsuario(cedula);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            codigo_usuario = cursor.getString(1);
            nombre.setText(cursor.getString(2));
            apellido.setText(cursor.getString(3));
            telefono.setText(Funciones.quitarFormatoTelefono(cursor.getString(5)));
            email.setText(cursor.getString(6));
            estado.setSelection(Funciones.buscar_estado_posicion(cursor.getString(14), contexto));
        }
    }

    /**
     * Inicializa el spinner de estado.
     */
    private void initSpinners()
    {
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                contexto, R.array.estados_lista, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estado.setAdapter(adapter2);
    }

    /**
     * Inicializa los botones.
     *
     * @param rootView RootView
     */
    private void initButtons(View rootView)
    {
        Button editar_usuario = (Button) rootView.findViewById(R.id.btn_editar_usuario);

        editar_usuario.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (campos_llenos())
                {
                    if (validar_campos())
                    {
                        final boolean permitido = Funciones.FuncionalidadPermitida("Perfil", 1, id, manager);

                        AlertDialog.Builder dialog = new AlertDialog.Builder(contexto);

                        dialog.setMessage(R.string.confirmacion_editar_usuario);
                        dialog.setCancelable(false);

                        dialog.setPositiveButton("Editar", new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                String nombres = nombre.getText().toString();
                                String apellidos = apellido.getText().toString();
                                String telefonos = telefono.getText().toString();
                                String estados = estado.getSelectedItem().toString();
                                String emails = email.getText().toString();

                                if (Funciones.isOnline(contexto))
                                    if (permitido)
                                        new modificar_usuario().execute(nombres, apellidos, telefonos, estados, emails);
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
     * Obtiene las variables adicionales que fueron pasadas como parametros desde otra actividad.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        cedula = bundle.getString("cedula");
        id = bundle.getString("id_usuario");
    }

    /**
     * Inicializa los componentes.
     *
     * @param rootView RootView
     */
    private void initComponents(View rootView)
    {
        nombre = (EditText) rootView.findViewById(R.id.nombre_perfil);
        apellido = (EditText) rootView.findViewById(R.id.apellido_perfil);
        telefono = (EditText) rootView.findViewById(R.id.telefono_perfil);
        email = (EditText) rootView.findViewById(R.id.email_perfil);
        estado = (Spinner) rootView.findViewById(R.id.estado);
    }

    /**
     * Verifica que los campos esten llenos.
     *
     * @return True si los campos estan llenos, False en caso contrario.
     */
    private boolean campos_llenos()
    {
        String nombre = this.nombre.getText().toString();
        String apellido = this.apellido.getText().toString();
        String telefono = this.telefono.getText().toString();
        String estado = this.estado.getSelectedItem().toString();
        String email = this.email.getText().toString();

        return !(nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || estado.isEmpty() || email.isEmpty());
    }

    /**
     * Valida los campos introducidos por el usuario.
     *
     * @return True si los campos estan correctos, False en caso contrario.
     */
    private boolean validar_campos()
    {
        boolean res = true;
        String msj = "";
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
     * Clase para la modificacion en segundo plano del perfil de usuario actualmente en sistema.
     */
    class modificar_usuario extends AsyncTask<String, String, String>
    {
        String nombre;
        String apellido;
        String telefono_cliente;
        String estado_cliente;
        String email_cliente;

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Por favor espere...");
            pDialog.setMessage("Editando datos...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            //obtnemos usr y pass
            nombre = params[0];
            apellido = params[1];
            telefono_cliente = params[2];
            estado_cliente = params[3];
            email_cliente = params[4];

            try
            {
                if (editar_datos(nombre, apellido, telefono_cliente, estado_cliente, email_cliente))
                {
                    return "ok";
                }

                else
                {
                    return "err";
                }
            }
            catch (RuntimeException e)
            {
                Log.e("Error editar_datos", "" + e);
                return "err2";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();

            if (result.equals("ok"))
            {
                Toast.makeText(contexto, "Datos editados exitosamente!", Toast.LENGTH_LONG).show();

                manager.actualizar_usuario(codigo_usuario, nombre, apellido, estado_cliente, email_cliente, telefono_cliente, fecha);

                Log.d("Usuario editado", "FIN");
                Fragment fragment = new FragmentMuestrario();
                Bundle args = new Bundle();
                args.putString("cedula", cedula);
                fragment.setArguments(args);

                Funciones.cambiarTitulo("Muestrario", getActivity());

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
            }
            else
            {
                if (datos_invalidos && telefono_invalido)
                    Toast.makeText(contexto, "Ya existe un usuario con el mismo telefono!", Toast.LENGTH_LONG).show();
                else if (datos_invalidos && email_invalido)
                    Toast.makeText(contexto, "Ya existe un usuario con el mismo email!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(contexto, "Ha ocurrido un error agregando al cliente!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Funcion para editar los datos del perfil de usuario.
     *
     * @param nombre   Nombre del usuario.
     * @param apellido Apellido del usuario.
     * @param telefono Telefono del usuario.
     * @param estado   Estado del usuario.
     * @param email    Email del usuario.
     * @return True si la modificacion fue exitosa, False en caso contrario.
     */
    private boolean editar_datos(String nombre, String apellido, String telefono, String estado, String email)
    {
        boolean bandera = false;
        Httppostaux post = new Httppostaux();
        telefono_invalido = false;
        email_invalido = false;

        ArrayList<NameValuePair> listaParametros = new ArrayList<>();
        listaParametros.add(new BasicNameValuePair("id_usuario", codigo_usuario));
        listaParametros.add(new BasicNameValuePair("nombre", nombre));
        listaParametros.add(new BasicNameValuePair("apellido", apellido));
        listaParametros.add(new BasicNameValuePair("telefono", Funciones.formatoTelefono(telefono)));
        listaParametros.add(new BasicNameValuePair("estado", estado));
        listaParametros.add(new BasicNameValuePair("email", email));

        String URL_connect = Constantes.IP_Server + "editar_usuario.php";
        JSONArray jdata = post.getserverdata(listaParametros, URL_connect);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);

                int logstatus = json_data.getInt("salida");
                fecha = json_data.getString("fecha");

                if (logstatus == 1)
                {
                    Log.w("datos_invalidos", "TODO BIEN");
                    bandera = true; // TODOOO bien
                    datos_invalidos = false;
                }
                else if (logstatus == 2)
                {
                    Log.w("datos_invalidos", "telefono invalido");
                    datos_invalidos = true; // telefono no valido
                    telefono_invalido = true;
                }
                else if (logstatus == 3)
                {
                    Log.w("datos_invalidos", "email invalido");
                    datos_invalidos = true; // email no valido
                    email_invalido = true;
                }
                else
                {
                    Log.w("datos_invalidos", "TODO MAL");
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
}