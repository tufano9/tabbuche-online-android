package paquete.global;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import paquete.database.DBAdapter;
import paquete.global.comparing_versions.DefaultArtifactVersion;
import paquete.global.library.Httppostaux;
import paquete.notifications.MyBroadcastReceiver;
import paquete.notifications.actualizarApp_SegundoPlano;
import paquete.tufanoapp.Home;
import paquete.tufanoapp.R;

/**
 * Desarrollado por Gerson el 11/5/2015.
 * <p/>
 * Clase que engloba las funciones que se utilizan muy a menudo por distintas clases a lo largo
 * del proyecto.
 */
@SuppressWarnings("SameParameterValue")
public class Funciones
{
    /**
     * Verifica si el email introducido es valido.
     *
     * @param email Email a validar.
     * @return True si el e-mail es valido en el formato 'name@company.com'
     */
    public static boolean isValidEmail(CharSequence email)
    {
        return TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Verifica si el dispositivo esta con alguna conexion a internet disponible.
     *
     * @param contexto Contexto de la actividad.
     * @return True si el dispositivo posee una conexion activa a internet.
     */
    public static boolean isOnline(Context contexto)
    {
        //if (networkInfo != null && networkInfo.isConnected())

        ConnectivityManager cm      = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Comprime una imagen con las dimensiones indicadas por los parametros.
     *
     * @param file      Archivo a comprimir.
     * @param reqWidth  Ancho requerido.
     * @param reqHeight Alto requerido.
     * @return Imagen escalada a las dimensiones requeridas.
     */
    public static Bitmap decodeSampledBitmapFromResource(File file, int reqWidth, int reqHeight)
    {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        //return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height       = options.outHeight;
        final int width        = options.outWidth;
        int       inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            final int halfHeight = height / 2;
            final int halfWidth  = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth)
            {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Obtiene la version actual de la aplicacion, definida en el manifest como 'versionName'
     *
     * @param contexto Contexto de la activity
     * @return Version de la app en formato similar a '2.3.20'
     * @throws PackageManager.NameNotFoundException
     */
    public static String getAppVersion(Context contexto) throws PackageManager.NameNotFoundException
    {
        PackageManager manager = contexto.getPackageManager();
        PackageInfo    info    = manager.getPackageInfo(contexto.getPackageName(), 0);
        return info.versionName;
    }

    /**
     * Encripta un texto proporcionado segun un KEY indicado.
     *
     * @param key  KEY con el cual se encriptara, se aconseja que sea generado de forma aleatoria y
     *             guardado para su uso posterior al momento de Des-Encriptar.
     * @param text Texto a encriptar.
     * @return Texto encriptado.
     * @throws GeneralSecurityException
     */
    public static byte[] encrypt(String key, String text) throws GeneralSecurityException
    {

        SecretKey secret_key = new SecretKeySpec(key.getBytes(), Constantes.ALGORITM);

        Cipher cipher = Cipher.getInstance(Constantes.ALGORITM);
        cipher.init(Cipher.ENCRYPT_MODE, secret_key);

        return cipher.doFinal(text.getBytes());
    }

    /**
     * Des-Encripta un texto proporcionado segun un KEY indicado.
     *
     * @param key           KEY con el cual el texto fue encriptado, para poder des-encriptarlo nuevamente.
     * @param encryptedText Texto encriptado.
     * @return Texto sin encriptacion.
     * @throws GeneralSecurityException
     */
    public static String decrypt(String key, byte[] encryptedText) throws GeneralSecurityException
    {

        SecretKey secret_key = new SecretKeySpec(key.getBytes(), Constantes.ALGORITM);

        Cipher cipher = Cipher.getInstance(Constantes.ALGORITM);
        cipher.init(Cipher.DECRYPT_MODE, secret_key);

        byte[] decrypted = cipher.doFinal(encryptedText);

        return new String(decrypted);
    }

    /**
     * Se crea la notificacion de acuerdo al numero de pedidos que esperan a ser enviados.
     * @param n : Numero de pedidos en espera
     */
    /*public static void createNotification(int n, Context contexto)
    {
        Log.i("Create", "notification");
        String contenido;
        if(n==1)                contenido = Constantes.MENSAJE_NOTIFICACION_SINGULAR;
        else                    contenido = Constantes.MENSAJE_NOTIFICACION;

        String titulo = Constantes.TITULO_NOTIFICACION;

        contenido = contenido.replace("%",""+n);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(contexto)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(titulo)
                        .setContentText(contenido);

        // Creates an explicit intent for an Activity in your app (Onclick Event)
        Intent resultIntent = new Intent(contexto, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        mBuilder.setAutoCancel(true);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(Constantes.NOTIFICACIONID, mBuilder.build());
    }*/

    /*public static void notificacion(Context contexto)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(contexto)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(contexto, Pedidos_Locales_Notification.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(Pedidos_Locales_Notification.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 12345;
        mNotificationManager.notify(mId, mBuilder.build());
    }*/

    /**
     * Crea una notificacion con el numero de pedidos pendientes por enviar (Pedidos Locales)
     *
     * @param contexto            Contexto de la actividad.
     * @param num_pedidos_locales Numero de pedidos pendientes.
     */
    public static void createNotification(Context contexto, int num_pedidos_locales)
    {
        Log.i("Create", "notification");
        String contenido = contexto.getResources().getString(R.string.contenido_notificacion);

        String titulo = contexto.getResources().getString(R.string.titulo_notificacion);
        titulo = titulo.replace("%", "" + num_pedidos_locales);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto);

        Intent intento = new Intent(contexto, MyBroadcastReceiver.class);
        intento.setAction(Constantes.ENVIAR_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, Constantes.REQUESTCODE1, intento, 0);

        builder.setContentIntent(pendingIntent);
        builder.setSound(null);
        //builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        // Sets the ticker text
        builder.setTicker(contexto.getResources().getString(R.string.titulo_notificacion));

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(titulo);
        builder.setContentText(contenido);

        // Build the notification
        Notification notification = builder.build();

        // Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(Constantes.NOTIFICACIONID, notification);
    }

    /**
     * Crea una notificacion para la descarga de la app.
     *
     * @param contexto    Contexto de la aplicacion.
     * @param new_version Version mas actualizada de la app (Extraida de la BD.Externa)
     * @param url         Url donde se encuentra la app.
     */
    private static void createNotificationDownloadApp(Context contexto, String new_version, String url)
    {
        Log.i("Create", "createNotificationDownloadApp");
        String contenido = contexto.getResources().getString(R.string.contenido_notificacion_descargarApp);

        String titulo = contexto.getResources().getString(R.string.titulo_notificacion_descargarApp);
        titulo = titulo.replace("%", new_version);

        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(contexto);

        Intent broadcastedIntent = new Intent(contexto, actualizarApp_SegundoPlano.class);
        broadcastedIntent.setAction(Constantes.ACTUALIZAR_ACTION);
        broadcastedIntent.putExtra("url", url);
        broadcastedIntent.putExtra("newer_version", new_version);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, Constantes.REQUESTCODE2, broadcastedIntent, 0);

        builder2.setContentIntent(pendingIntent);
        builder2.setSound(null);
        //builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        // Sets the ticker text
        builder2.setTicker(contexto.getResources().getString(R.string.titulo_notificacion_descargarApp).replace("%", new_version));

        // Sets the small icon for the ticker
        builder2.setSmallIcon(R.drawable.ic_notification);
        builder2.setContentTitle(titulo);
        builder2.setContentText(contenido);

        // Build the notification
        Notification notification = builder2.build();

        // Use the NotificationManager to show the notification
        NotificationManager nm2 = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        nm2.notify(Constantes.NOTIFICACIONID2, notification);
    }

    /**
     * Se comprueba si hay pedidos locales que necesitan ser enviados, para de esta forma
     * crear la notificacion o editarla si ya esta creada..
     *
     * @return true si hay pedidos que deben ser enviados, para crear la notificacion,
     * retorna false en caso contrario, de no haber pedidos locales..
     */
    public static int verificar_Notificacion(Context contexto)
    {
        DBAdapter manager        = new DBAdapter(contexto);
        Cursor    cursor         = manager.cargarCursorProductosPedidos_Locales();
        int       numero_pedidos = cursor.getCount();
        cursor.close();

        Log.i("verificar_Notificacion", "Hay " + numero_pedidos + " pedidos locales.");

        return numero_pedidos;
    }

    @SuppressWarnings("unused")
    public static void setLoginCredentials(String cedula, String pass, EditText campo_usuario_login, EditText campo_contrasena_login)
    {
        campo_usuario_login.setText(cedula);
        campo_contrasena_login.setText(pass);
    }

    /**
     * Incluye un formato predefinido al telefono ingresado.
     *
     * @param telefono Numero de telefono al cual se le incluira el formato (Guiones y Puntos), como
     *                 por ejemplo 04121234567
     * @return El telefono bajo un formato predefinido, como por ejemplo: 0412-123.4567
     */
    public static String formatoTelefono(String telefono)
    {
        return telefono.substring(0, 4) + "-" + telefono.substring(4, 7) + "." + telefono.substring(7);
    }

    /**
     * Elimina el formato del telefono (Guiones y puntos)
     *
     * @param telefono El telefono bajo un formato 0000-000.0000
     * @return El telefono sin el formato 12345677901
     */
    public static String quitarFormatoTelefono(String telefono)
    {
        return telefono.replace("-", "").replace(".", "");
    }

    /**
     * Cambia el titulo de la barra de navegacion superior y selecciona el item correspondiente
     * en el menu lateral.
     *
     * @param titulo   Titulo por el cual se va a cambiar.
     * @param activity Actividad a la cual se le cambiara el titulo.
     */
    public static void cambiarTitulo(String titulo, Activity activity)
    {
        Log.i("GLOBAL", "cambiarTitulo: " + titulo);
        activity.setTitle(titulo);
        Home.NavList.setItemChecked(2, true);
        Home.NavList.setSelection(2);
    }

    /**
     * Busca la posicion de un estado dentro de la lista predefinida de estados del sistema.
     *
     * @param estado   Estado a consultar la posicion dentro de la lista.
     * @param contexto Contexto de la actividad.
     * @return La posicion del estado. Retornara -1 de no haberse encontrado.
     */
    public static int buscar_estado_posicion(String estado, Context contexto)
    {
        String[] estados_lista = contexto.getResources().getStringArray(R.array.estados_lista);

        for (int i = 0; i < estados_lista.length; i++)
        {
            if (estados_lista[i].equals(estado))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Formatea un precio con separadores de unidades de miles (Puntos y comas)
     *
     * @param price Precio actual a formatear, por lo general seria algo como 12500.05
     * @return El precio ya formateado, como por ejemplo 12,500.05
     */
    public static String priceWithDecimal(Double price)
    {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        return formatter.format(price);
    }

    /**
     * Funcion utilizada para verificar si el usuario actual tiene permiso para ver la opcion
     * indicada en los parametros
     *
     * @param opcion La opcion a la cual se desea verificar su estado de habilitado. por ejemplo:
     *               homes, muestrario, pedidos, clientes, perfil, opciones, edo_cuenta. Notese que estos son
     *               campos de la base de datos de la tabla TABLA_FUNCIONES
     * @param num    El numero de la opcion correspondiente, por ejemplo 0, 1, 2 ,3 (Con respecto a la
     *               opcion señalada en el String anterior.
     * @param id     El id del usuario en cuestion.
     * @return True si la funcionalidad se permite, false de lo contrario.
     */
    public static boolean FuncionalidadPermitida(String opcion, int num, String id, DBAdapter manager)
    {
        Cursor cursor  = manager.buscarFuncionesUsuario(id, opcion);
        String funcion = "";

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            funcion = cursor.getString(0);
        }
        cursor.close();

        String[] f = funcion.split(",");

        for (int i = 0; i < f.length; i++)
        {
            if (i == num)
            {
                return f[i].equals("1");
            }

        }
        return false;
    }

    /**
     * Funcion utilizada para verificar si el usuario actual tiene permiso para ver la opcion
     * indicada en los parametros
     *
     * @param opcion La opcion a la cual se desea verificar su estado de habilitado. por ejemplo:
     *               homes, muestrario, pedidos, clientes, perfil, opciones, edo_cuenta. Notese que estos son
     *               campos de la base de datos de la tabla TABLA_FUNCIONES     *
     * @param id     El id del usuario en cuestion.
     * @return True si la funcionalidad se permite, es decir, todas las funcionalidad que lo contienen
     * estan deshabilitadas, false de lo contrario.
     */
    public static boolean Funcionalidad_Menu_Permitida(String opcion, String id, DBAdapter manager)
    {
        Cursor cursor  = manager.buscarFuncionesUsuario(id, opcion);
        String funcion = "";

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            funcion = cursor.getString(0);
        }
        cursor.close();

        String[] f         = funcion.split(",");
        int      contador  = 0;
        int      num_comas = numero_ocurrencias_caracter(funcion, ",");

        for (String aF : f)
        {
            if (aF.equals("0"))
                contador++;
        }

        return !(contador == (num_comas + 1));
    }

    /**
     * Calcula el numero de ocurrencias del caracter indicado, en la cadena introducida
     *
     * @param cadena   La cadena donde se buscaran el numero de ocurrencias de un caracter
     * @param caracter El caracter a buscar en la cadena señalada.
     * @return El numero de ocurrencias del caracter en la cadena.
     */
    private static int numero_ocurrencias_caracter(String cadena, String caracter)
    {
        return cadena.length() - cadena.replace(caracter, "").length();
    }

    /**
     * Funcion para la verificacion de las actualizaciones de la app (Segun la version)
     *
     * @param context Contexto de la actividad.
     */
    public static void verificar_ActualizacionesApp(Context context)
    {
        new buscarVersionWeb(context).execute();
    }

    /**
     * Consulta la version actual de la aplicacion en la web.
     *
     * @return Array con la version mas nueva en la web y la url donde se consigue dicha actualiz.
     */
    private static String[] consultarVersionWeb()
    {
        Log.d("Opciones", "consultarVersionWeb");
        Httppostaux post = new Httppostaux();

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        String                   URL                 = Constantes.IP_Server + "consultar_ult_version.php";
        JSONArray                jdata               = post.getserverdata(postparameters2send, URL);
        String                   version_web;

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                version_web = json_data.getString("version");
                URL = json_data.getString("url");

                return new String[]{version_web, URL};
            }
            catch (JSONException e)
            {
                Log.e("Error consultarVers", "" + e);
                e.printStackTrace();
            }
        }
        else
        {
            Log.e("JSON  ", "ERROR: NO DATA");
            return null;
        }
        return null;
    }

    /**
     * Used for comparing different versions of software
     *
     * @param local_version_string  the version name of the app installed on the system
     * @param online_version_string the version name of the app released on the Google Play
     * @return true if a the online_version_string is greater than the local_version_string
     */
    private static boolean newer_version_available(String local_version_string, String online_version_string)
    {
        DefaultArtifactVersion local_version_mvn  = new DefaultArtifactVersion(local_version_string);
        DefaultArtifactVersion online_version_mvn = new DefaultArtifactVersion(online_version_string);
        return local_version_mvn.compareTo(online_version_mvn) == -1 && !local_version_string.equals("");
    }

    /**
     * Formatea el tamaño de un archivo introducido a un formato legible y entendible por usuario.
     *
     * @param size Tamaño del archivo a formatear.
     * @return Tamaño del archivo formateado con la unidad correspondiente, por ej 534.1 MB
     */
    public static String readableFileSize(long size)
    {
        if (size <= 0) return "0";
        final String[] units       = new String[]{"B", "kB", "MB", "GB", "TB"};
        int            digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * Clase para la busqueda en segundo plano de la version mas actualizada de la aplicacion
     * hasta el momento.
     */
    static class buscarVersionWeb extends AsyncTask<String, String, String>
    {
        final Context contexto;
        String version_actual, version_web, url;

        public buscarVersionWeb(Context context)
        {
            contexto = context;
            try
            {
                version_actual = contexto.getPackageManager().getPackageInfo(contexto.getPackageName(), 0).versionName;
                Log.d("Datos", "Version actual del sistema: " + version_actual);
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                String[] response = consultarVersionWeb();
                if (response != null)
                {
                    version_web = response[0];
                    url = response[1];
                }
                if (version_web != null)
                    return "ok";
                else
                    return "err";
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
                return "" + e;
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (result.equals("ok"))
            {
                if (newer_version_available(version_actual, version_web))
                {
                    // Hay que actualizar..
                    Log.d("onPostExecute", "Hay que actualizar a la version " + version_web);
                    Funciones.createNotificationDownloadApp(contexto, version_web, url);
                }
                else
                {
                    // Ya esta actualizado
                    Log.d("onPostExecute", "Ya esta actualizado..");
                }
            }
            else
            {
                if (!result.equals("err"))
                    Toast.makeText(contexto, "Ha ocurrido un error al consultar los datos.." + result, Toast.LENGTH_LONG).show();
                Log.d("ERROR", result);
            }
        }
    }
}