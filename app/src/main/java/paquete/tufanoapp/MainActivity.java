package paquete.tufanoapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoButton;
import com.devspark.robototextview.widget.RobotoTextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

import paquete.Droidlogin.library.Httppostaux;
import paquete.actualizaciones_app.actualizar_DatosApp;
import paquete.database.DBAdapter;
import paquete.dialogos.DialogoReestablecer_Pass;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.notifications.alarmNotification;
import paquete.push_notifications.QuickstartPreferences;
import paquete.push_notifications.RegistrationIntentService;

public class MainActivity extends Activity
{
    private EditText campo_usuario_login, campo_contrasena_login;
    private ProgressDialog pDialog;
    private Httppostaux post;
    private Context contexto;
    private DBAdapter manager;
    private String cedula, usuario, codigo_usuario, nombre, apellido, telefono, email, lineas_desh,
            contrasena, modelos_desh, productos_desh, fecha, estado;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contexto = getApplicationContext();
        manager = new DBAdapter(contexto);
        post = new Httppostaux();

        noInitialFocus();
        initTextView();
        initNotifications();
        initButtons();
        initBroadcastReceiver();
        registerWithGCM();
    }

    /**
     * Inicializa el broadcasReceiver, para verificar los tokens del GCM
     */
    private void initBroadcastReceiver()
    {
        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

                if (sentToken)
                {
                    Log.i(TAG, "Token exitoso..");
                }
                else
                {
                    Log.i(TAG, "Token false..");
                }
            }
        };
    }

    /**
     * Registra la app en el Google Cloud Messaging
     */
    private void registerWithGCM()
    {
        if (checkPlayServices())
        {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * Inicializa el sistema de notificaciones.
     */
    private void initNotifications()
    {
        int pedidos_locales = Funciones.verificar_Notificacion(contexto);

        if (pedidos_locales > 0)
            alarmNotification.notificaciones_persistentes(contexto);

        // Verificar constantemente actualizaciones..
        alarmNotification.notificaciones_persistentes_actualizarApp(contexto);
    }

    /**
     * Inicializa los botones.
     */
    private void initButtons()
    {
        RobotoButton btn_login = (RobotoButton) findViewById(R.id.btn_login);
        RobotoButton btn_limpiar = (RobotoButton) findViewById(R.id.btn_limpiar);
        campo_usuario_login = (EditText) findViewById(R.id.campo_usuario_login);
        campo_contrasena_login = (EditText) findViewById(R.id.campo_contrasena_login);
        //campo_usuario_login.setError("ERROR!!! XD");

        //contexto.deleteDatabase(DBHelper.DB_NAME); // Limpiar la BD
        //Funciones.setLoginCredentials("20179843", "Elg58led91", campo_usuario_login, campo_contrasena_login);
        //Funciones.setLoginCredentials("20123456", "123456", campo_usuario_login, campo_contrasena_login);

        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Validar los datos
                String user = campo_usuario_login.getText().toString();
                contrasena = campo_contrasena_login.getText().toString();
                cedula = user;

                if (user.isEmpty() || contrasena.isEmpty())
                    Toast.makeText(contexto, "Debes llenar todos los campos!", Toast.LENGTH_SHORT).show();
                else if (Funciones.isOnline(contexto))
                    new asynclogin().execute(user, contrasena);
                else
                    new asynclogin_local().execute(user, contrasena);
            }
        });

        btn_limpiar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                campo_usuario_login.setText("");
                campo_contrasena_login.setText("");
            }
        });
    }

    /**
     * Inicializa los TextView.
     */
    private void initTextView()
    {
        RobotoTextView res_pass = (RobotoTextView) findViewById(R.id.tvRecuperarPass);
        res_pass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogoPass();
            }
        });
    }

    /**
     * Evita que la aplicacion haga un focus inicial en algun campo al apenas abrir el activity.
     */
    private void noInitialFocus()
    {
        LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout_MainActivity);
        layout.requestFocus();
    }

    /**
     * Realiza un login local en segundo plano. (Para cuando no se disponga de conexion a internet.
     */
    class asynclogin_local extends AsyncTask<String, String, String>
    {
        String password;

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Por favor espere...");
            pDialog.setMessage("Verificando datos de forma local...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            usuario = params[0];
            password = params[1];

            try
            {
                //enviamos y recibimos y analizamos los datos en segundo plano.
                if (loginstatus_local(usuario, password))
                {
                    return "ok"; //login valido
                }

                else
                {
                    Log.d("Loginstatuslocal", "err");
                    return "err"; //login invalido
                }
            }

            catch (RuntimeException e)
            {
                Log.d("Loginstatuslocal", "err2: " + e);
                return "err2";
            }
        }

        /*
        Una vez terminado doInBackground segun lo que halla ocurrido
        pasamos a la sig. activity
        o mostramos error
        */
        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();

            if (result.equals("ok"))
            {
                entrar_Home();
            }

            else
            {
                Toast.makeText(contexto, "Usuario o contraseña erronea!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Realiza el login segun la BD local.
     * @param ci Cedula del usuario que intenta acceder.
     * @param clave Password de accedo asociado a la cedula ingresada.
     * @return True si el login fue exitoso, False en caso contrario.
     */
    private boolean loginstatus_local(String ci, String clave)
    {
        Log.d("Valida Password", "loginstatus_local");
        boolean bandera = false;

        Cursor cursor2 = manager.buscarSalt_Usuario(ci);

        for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
        {
            Log.d("buscarSalt", "ENCONTRE");
        }
        cursor2.close();


        Cursor cursor = manager.buscarUsuario(ci);
        String pass = "";

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            try
            {
                pass = Funciones.decrypt(Constantes.KEY, Funciones.encrypt(Constantes.KEY, clave));

                //Log.d("Valida Password","Password BD descrypt:"+pass);
                //Log.d("Valida Password","Password Usuario descrypt:"+clave);
                //pass2 = decrypt(KEY,pass);
            }
            catch (GeneralSecurityException e)
            {
                e.printStackTrace();
            }

            //Log.d("Comparando BD:",pass+" con Usuario:"+clave);

            if (pass.equals(clave))
            {
                Log.d("Valida Password", "Usuario y contraseña correctos!");
                codigo_usuario = cursor.getString(1);
                nombre = cursor.getString(2);
                apellido = cursor.getString(3);
                telefono = cursor.getString(5);
                email = cursor.getString(6);
                lineas_desh = cursor.getString(8);
                modelos_desh = cursor.getString(9);
                productos_desh = cursor.getString(10);
                bandera = true;
            }
            else
            {
                Log.d("Valida Password", "Usuario y contraseña INCORRECTOS!");
            }
        }
        cursor.close();

        return bandera;
    }

    /**
     * Realiza un login en segundo plano utilizando la BD externa (Web).
     */
    class asynclogin extends AsyncTask<String, String, String>
    {
        String password;

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Por favor espere...");
            pDialog.setMessage("Verificando datos...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            usuario = params[0];
            password = params[1];

            try
            {
                if (login(usuario, password))
                    return "ok"; //login valido
                else
                    return "err"; //login invalido
            }
            catch (RuntimeException e)
            {
                return "RuntimeException: " + e;
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
                return "NameNotFoundException: " + e;
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();

            if (result.equals("ok"))
            {
                // Ahora debo verificar si debo actualizar la aplicacion..
                pDialog = new ProgressDialog(MainActivity.this);
                new verificar_actualizaciones().execute();
            }
            else
            {
                Toast.makeText(contexto, "Usuario o contraseña erronea!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Realiza la descarga de informacion del usuario que acaba de acceder.
     * @param username Usuario.
     * @param clave Password.
     * @return True si el usuario esta activo y puede acceder al sistema con los datos que fueron
     * proporcionados.
     * @throws PackageManager.NameNotFoundException
     */
    private boolean login(String username, String clave) throws PackageManager.NameNotFoundException
    {
        boolean bandera = false;
        post = new Httppostaux();

        ArrayList<NameValuePair> listaParametros = new ArrayList<>();
        listaParametros.add(new BasicNameValuePair("cedula", username));
        listaParametros.add(new BasicNameValuePair("password", clave));
        listaParametros.add(new BasicNameValuePair("getAppVersion", Funciones.getAppVersion(contexto)));

        String URL_connect = Constantes.IP_Server + "login_android.php";
        JSONArray jdata = post.getserverdata(listaParametros, URL_connect);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);

                int logstatus = json_data.getInt("logstatus");
                String rol = json_data.getString("rol");

                codigo_usuario = json_data.getString("codigo_usuario");
                nombre = json_data.getString("nombre");
                apellido = json_data.getString("apellido");
                telefono = json_data.getString("telefono");
                email = json_data.getString("email");
                lineas_desh = json_data.getString("lineas_deshabilitadas");
                modelos_desh = json_data.getString("modelos_deshabilitados");
                productos_desh = json_data.getString("productos_deshabilitados");
                fecha = json_data.getString("fecha");
                estado = json_data.getString("estado");

                if (logstatus == 1 && !rol.equals("Administrador"))
                    bandera = true;
            }
            catch (JSONException e)
            {
                Log.e("JSONException", "" + e);
                //e.printStackTrace();
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
     * Clase para la verificacion en segundo plano de las posibles actualizaciones del sistema.
     */
    class verificar_actualizaciones extends AsyncTask<String, String, Boolean>
    {
        final int[] actualizar = new int[Constantes.NUMERO_ACTUALIZACIONES];

        @Override
        protected void onPreExecute()
        {
            pDialog.setTitle("Verificando estado de Actualizacion...");
            pDialog.setMessage("Espere un momento..");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected Boolean doInBackground(String... params)
        {
            boolean res = false;

            String fecha_linea = ""; // linea
            String fecha_modelo = ""; // modelo
            String fecha_producto = ""; // producto
            String fecha_usuario = ""; // usuario
            String fecha_material = ""; // material
            String fecha_color = ""; // color
            String fecha_bulto = ""; // bulto
            String fecha_cliente = ""; // cliente
            String fecha_pedido = ""; // pedido
            String fecha_funciones_moviles = "";

            for (int i = 0; i < actualizar.length; i++)
                actualizar[i] = 0;

            Cursor cursor = manager.cargarCursorActualizacionesGenerales();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                fecha_linea = cursor.getString(0);
                fecha_modelo = cursor.getString(1);
                fecha_producto = cursor.getString(2);
                fecha_material = cursor.getString(3);
                fecha_color = cursor.getString(4);
                fecha_bulto = cursor.getString(5);
            }

            cursor = manager.cargarCursorActualizacionesClientes_ID(codigo_usuario);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                Log.i("fecha_local_cliente", "(" + cursor.getString(2) + ")");
                fecha_cliente = cursor.getString(2);
            }

            cursor = manager.cargarCursorActualizacionesPedidos_ID(codigo_usuario);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                fecha_pedido = cursor.getString(2);
            }

            cursor = manager.cargarCursorActualizacionesFuncionesMoviles_ID(codigo_usuario);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                fecha_funciones_moviles = cursor.getString(2);
            }

            cursor = manager.buscarUsuario(cedula);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                fecha_usuario = cursor.getString(13);
            }

            cursor.close();

            String URL_actualizaciones = Constantes.IP_Server + "obtener_actualizaciones_new.php";
            ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
            postparameters2send.add(new BasicNameValuePair("id_usuario", codigo_usuario));
            JSONArray jdata = post.getserverdata(postparameters2send, URL_actualizaciones);

            String lin = "";
            String mod = "";
            String pro = "";
            String usuario_actualizacion = "";
            String materiales_actualizacion = "";
            String colores_actualizacion = "";
            String bultos_actualizacion = "";
            String clientes_actualizacion = "";
            String pedidos_actualizacion = "";
            String funciones_moviles_actualizacion = "";

            if (jdata != null && jdata.length() > 0)
            {
                try
                {
                    for (int z = 0; z < jdata.length(); z++)
                    {
                        JSONObject jsonObject = jdata.getJSONObject(z);

                        lin = jsonObject.getString("linea");
                        mod = jsonObject.getString("modelo");
                        pro = jsonObject.getString("producto");
                        usuario_actualizacion = jsonObject.getString("usuario");
                        materiales_actualizacion = jsonObject.getString("materiales");
                        colores_actualizacion = jsonObject.getString("colores");
                        bultos_actualizacion = jsonObject.getString("bultos");
                        clientes_actualizacion = jsonObject.getString("clientes");
                        pedidos_actualizacion = jsonObject.getString("pedidos");
                        funciones_moviles_actualizacion = jsonObject.getString("funciones");
                        Log.i("fecha_externa", "(" + clientes_actualizacion + ")");
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
            }

            if (fecha_linea.equals(lin) && fecha_modelo.equals(mod) && fecha_producto.equals(pro)
                    && fecha_usuario.equals(usuario_actualizacion) && fecha_material.equals(materiales_actualizacion)
                    && fecha_color.equals(colores_actualizacion) && fecha_bulto.equals(bultos_actualizacion)
                    && fecha_cliente.equals(clientes_actualizacion) && fecha_pedido.equals(pedidos_actualizacion)
                    && fecha_funciones_moviles.equals(funciones_moviles_actualizacion))
            {
                // No actualizare
                res = true;
            }
            else
            {
                if (!fecha_linea.equals(lin))
                {
                    Log.d("MainActitivy", "Actualizare linea");
                    actualizar[0] = 1;
                }
                if (!fecha_modelo.equals(mod))
                {
                    Log.d("MainActitivy", "Actualizare modelo");
                    actualizar[1] = 1;
                }
                if (!fecha_producto.equals(pro))
                {
                    Log.d("MainActitivy", "Actualizare producto");
                    actualizar[2] = 1;
                }
                if (!fecha_usuario.equals(usuario_actualizacion))
                {
                    Log.d("MainActitivy", "Actualizare usuario");
                    actualizar[3] = 1;
                }
                if (!fecha_material.equals(materiales_actualizacion))
                {
                    Log.d("MainActitivy", "Actualizare materiales");
                    actualizar[4] = 1;
                }
                if (!fecha_color.equals(colores_actualizacion))
                {
                    Log.d("MainActitivy", "Actualizare colores");
                    actualizar[5] = 1;
                }
                if (!fecha_bulto.equals(bultos_actualizacion))
                {
                    Log.d("MainActitivy", "Actualizare bultos");
                    actualizar[6] = 1;
                }
                if (!fecha_cliente.equals(clientes_actualizacion))
                {
                    Log.d("MainActitivy", "Actualizare clientes");
                    actualizar[7] = 1;
                }
                if (!fecha_pedido.equals(pedidos_actualizacion))
                {
                    Log.d("MainActitivy", "Actualizare pedidos");
                    actualizar[8] = 1;
                    actualizar[9] = 1;
                }
                if (!fecha_funciones_moviles.equals(funciones_moviles_actualizacion))
                {
                    Log.d("MainActitivy", "Actualizare funcionalidades moviles");
                    actualizar[10] = 1;
                }

                // Aca debere actualizar solamente lo que no esta, por ejemplo solo las lineas..
                manager.actualizar_actualizacionesGenerales(lin, mod, pro, materiales_actualizacion, colores_actualizacion, bultos_actualizacion);
                manager.actualizar_actualizacionesPedido(codigo_usuario, pedidos_actualizacion);
                manager.actualizar_actualizacionesCliente(codigo_usuario, clientes_actualizacion);
                manager.actualizar_actualizacionesFunciones(codigo_usuario, funciones_moviles_actualizacion);
            }
            return res;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            pDialog.dismiss();
            if (result)
            {
                Log.d("Verificar Actualizacion", "NO HAY QUE ACTUALIZAR");
                entrar_Home();
            }
            else
            {
                Log.d("Verificar Actualizacion", "HAY QUE ACTUALIZAR");
                new actualizar_DatosApp(MainActivity.this, codigo_usuario, actualizar, contrasena, usuario, nombre, apellido, telefono, email, lineas_desh, modelos_desh, productos_desh, fecha, estado);
            }
        }
    }

    /**
     * Funcion por la cual se accedera a la aplicacion.
     */
    private void entrar_Home()
    {
        String pass = null;
        try
        {
            pass = Arrays.toString(Funciones.encrypt(Constantes.KEY, contrasena));
            //Log.d("Encriptando","A "+contrasena+" ->> "+pass);
            //Log.d("Desencriptando","A "+contrasena+" ->> "+decrypt(KEY,encrypt(KEY,contrasena)));
        }
        catch (GeneralSecurityException e)
        {
            e.printStackTrace();
        }
        manager.insertar_usuario(codigo_usuario, usuario, nombre, apellido, telefono, email, lineas_desh, modelos_desh, productos_desh, pass, Constantes.KEY, fecha, estado);
        Cursor cursor = manager.buscarUsuario(usuario);

        String id_vendedor = "";
        String nombre_vendedor = "";
        String apellido_vendedor = "";
        String telefono_vendedor = "";
        String email_vendedor = "";

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            id_vendedor = cursor.getString(1);
            nombre_vendedor = cursor.getString(2);
            apellido_vendedor = cursor.getString(3);
            telefono_vendedor = cursor.getString(5);
            email_vendedor = cursor.getString(6);
        }
        cursor.close();

        Intent c = new Intent(MainActivity.this, Home.class);
        c.putExtra("cedula", usuario);
        c.putExtra("id_vendedor", id_vendedor);
        c.putExtra("nombre_vendedor", nombre_vendedor);
        c.putExtra("apellido_vendedor", apellido_vendedor);
        c.putExtra("telefono_vendedor", telefono_vendedor);
        c.putExtra("email_vendedor", email_vendedor);
        startActivity(c);
    }

    private void dialogoPass()
    {
        DialogFragment newFragment = new DialogoReestablecer_Pass();
        newFragment.show(getFragmentManager(), "ReestablecerPass");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (apiAvailability.isUserResolvableError(resultCode))
            {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            }
            else
            {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}