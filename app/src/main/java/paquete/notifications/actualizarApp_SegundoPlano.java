package paquete.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.util.ByteArrayBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.global.download_from_url.descargarArchivo;
import paquete.tufanoapp.R;

/**
 * Desarrollado por Gerson el 21/10/2015.
 */
public class actualizarApp_SegundoPlano extends BroadcastReceiver
{
    public static  NotificationCompat.Builder builder;
    public static  NotificationManager        nm;
    private static boolean                    CANCELADO;

    /**
     * Descarga la aplicacion a traves de un URL proporcionado.
     *
     * @param URL URL donde se encuentra la app.
     * @return True si el proceso de descarga fue exitoso, False en caso contrario.
     */
    private static boolean downloadApplication(String URL)
    {
        //Descargar el archivo..
        ByteArrayBuffer baf = descargarArchivo.DownloadFromUrl(URL, 1);

        if (baf != null) // Si es null, significa que fue cancelado..
        {
            //Guardarlo en la carpeta downloads del dispositivo..
            try
            {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Constantes.APP_FILE_NAME);

                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(baf.toByteArray());
                outputStream.close();
                return true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            // La descarga fue cancelada por el usuario
            CANCELADO = true;
        }

        return false;
    }

    /**
     * Instala la aplicacion descargada.
     *
     * @param contexto Contexto de la aplicacion.
     */
    private static void instalarAplicacion(Context contexto)
    {
        /* Instalar aplicacion */
        Log.d("instalarAplicacion", "Instalando paquete (No funciona)   : " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + Constantes.APP_FILE_NAME);
        Log.d("instalarAplicacion", "Instalando paquete (Funciona)      : " + Environment.getExternalStorageDirectory() + Constantes.APP_FILE_LOCATION);
        File file = new File(Environment.getExternalStorageDirectory() + Constantes.APP_FILE_LOCATION);
        //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + Constantes.APP_FILE_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Se usa cuando inicias un activity desde fuera de un contexto de activity
        contexto.startActivity(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("onReceive", "actualizarApp_SegundoPlano");

        String newer_version = intent.getStringExtra("newer_version");
        String url           = intent.getStringExtra("url");

        String contenido = context.getResources().getString(R.string.contenido_notificacion_descargarApp);
        String titulo    = context.getResources().getString(R.string.titulo_notificacion_descargarApp);
        titulo = titulo.replace("%", newer_version);

        builder = new NotificationCompat.Builder(context);
        Intent intento = new Intent(context, actualizarApp_SegundoPlano.class);
        intento.setAction(Constantes.ACTUALIZAR_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constantes.REQUESTCODE2, intento, 0);

        builder.setContentIntent(pendingIntent);
        builder.setSound(null);

        // Sets the ticker text
        builder.setTicker(context.getResources().getString(R.string.titulo_notificacion_descargarApp).replace("%", newer_version));

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(titulo);
        builder.setContentText(contenido);

        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))

        String action = intent.getAction();

        Log.v("onReceive", "OpenNotification : " + action);

        if (Constantes.ACTUALIZAR_ACTION.equals(action))
        {
            Log.v("shuffTest", "Downloading");

            /*if (pedidos_locales>0)
            {*/
            if (Funciones.isOnline(context))
                new descargarApk(context, url).execute();
            else
                Toast.makeText(context, Constantes.NO_INTERNET, Toast.LENGTH_SHORT).show();
            /*}
            else
                nm.cancel(Constantes.NOTIFICACIONID);*/
        }
        else //if(NO_ACTION.equals(action))
        {
            Log.v("shuffTest", "Alarma (" + action + ")");
        }

    }

    /**
     * Descarga el APK en segundo plano.
     */
    static class descargarApk extends AsyncTask<Void, Integer, Integer>
    {
        final Context contexto;
        final String  url;
        String error;
        //final File file;

        public descargarApk(Context context, String url)
        {
            contexto = context;
            this.url = url;
            //this.file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Constantes.APP_FILE_NAME);
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            CANCELADO = false;
            builder.setContentTitle("Descargando actualizacion");
            builder.setContentText("Por favor espere o presione aqui para cancelar");
            builder.setSound(null);
            builder.setProgress(100, 0, true);

            builder.setAutoCancel(true);
            Intent close_intent = new Intent(contexto, CloseNotification.class);
            close_intent.putExtra("notificationID", Constantes.NOTIFICACIONID2);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, Constantes.REQUESTCODE2, close_intent, 0);
            builder.setContentIntent(pendingIntent);

            nm.notify(Constantes.NOTIFICACIONID2, builder.build());
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            try
            {
                if (downloadApplication(url))
                    return 1;
                else
                    return 0;
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
                error = String.valueOf(e);
                return 0;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            // Update progress
            builder.setProgress(100, values[0], true);
            nm.notify(Constantes.NOTIFICACIONID2, builder.build());
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if (result == 1)
            {
                nm.notify(Constantes.NOTIFICACIONID2, builder.build());

                builder.setContentText("Aplicacion descargada..");
                // Removes the progress bar
                builder.setProgress(0, 0, false);

                builder.setAutoCancel(true);

                Intent intento = new Intent(contexto, CloseNotification.class);
                intento.putExtra("notificationID", Constantes.NOTIFICACIONID2);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, Constantes.REQUESTCODE2, intento, 0);
                builder.setContentIntent(pendingIntent);

                nm.notify(Constantes.NOTIFICACIONID2, builder.build());

                instalarAplicacion(contexto);
            }
            else
            {
                if (CANCELADO)
                    Toast.makeText(contexto, "La descarga fue cancelada", Toast.LENGTH_LONG).show();
                else
                {
                    Toast.makeText(contexto, "Ha ocurrido un error al descargar la aplicacion.." + result, Toast.LENGTH_LONG).show();
                    Log.e("ERROR", "Ha ocurrido un error: " + error);
                }
            }
        }
    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    /*private static class DownloadTask extends AsyncTask<String, Integer, String>
    {
        final private Context context;
        private PowerManager.WakeLock mWakeLock;
        final private File file;

        public DownloadTask(Context context, File file)
        {
            this.context = context;
            this.file = file;
        }

        @Override
        protected String doInBackground(String... sUrl)
        {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String respuesta ;

            try
            {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                Log.i("DownloadTask","fileLength: "+ Funciones.readableFileSize(fileLength));

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1)
                {
                    // allow canceling with back button
                    if (isCancelled())
                    {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                    {
                        //publishProgress((int) (total * 100 / fileLength));
                        // Update progress
                        builder.setProgress(100, (int) (total * 100 / fileLength), false);
                        nm.notify(Constantes.NOTIFICACIONID2, builder.build());
                    }
                    output.write(data, 0, count);
                }
            }
            catch (Exception e)
            {
                return e.toString();
            }
            finally
            {
                try
                {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                }
                catch (IOException ignored)
                {
                }

                if (connection != null)
                    connection.disconnect();
                respuesta = "ok";
            }

            return respuesta;
        }

        @Override
        protected void onPostExecute(String s)
        {
            Log.d("onPost",s);
        }
    }*/
}