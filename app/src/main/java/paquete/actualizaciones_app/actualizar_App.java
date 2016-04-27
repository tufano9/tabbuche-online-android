package paquete.actualizaciones_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import paquete.Droidlogin.library.Httppostaux;
import paquete.download_from_url.descargarArchivo;
import paquete.global.Constantes;
import paquete.comparing_versions.DefaultArtifactVersion;
import paquete.global.Funciones;
import paquete.tufanoapp.R;

/**
 * Desarrollado por Gerson el 21/10/2015.
 *
 * Clase encargada de verificar actualizaciones de la app, descargarlas e instalarlas.
 */
public class actualizar_App
{
    private final Context contexto;
    public static ProgressDialog pDialog;
    private final Httppostaux post = new Httppostaux();
    private String version_web = "";
    private String URL;
    private String version_actual = "";
    private boolean CANCELADO;

    /**
     * Constructor de la clase
     * @param contexto Contexto de la aplicacion.
     * @param version_actual Version actual de la app, por ej 2.1.25
     */
    public actualizar_App(Context contexto, String version_actual)
    {
        Log.d("actualizarApp", "FragmentOpciones");
        this.contexto = contexto;
        this.version_actual =version_actual;
        new buscarVersionWeb().execute();
    }

    /**
     * Clase de consulta en segundo plano la ultima version disponible de la aplicacion, si hay una
     * version mas nueva, avisa al usuario y lo invita a descargarla.
     */
    private class buscarVersionWeb extends AsyncTask< String, String, String >
    {
        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Consultando datos...");
            pDialog.setMessage("Por favor, espere ...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                if (consultarVersionWeb()!=null )
                    return "ok";
                else
                    return "err";
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
                return ""+e;
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            if (result.equals("ok"))
            {
                if (newer_version_available(version_actual, version_web))
                {
                    // Hay que actualizar..
                    Log.d("onPostExecute", "Debo actualizar a la version: " + version_web);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(contexto);

                    dialog.setTitle( contexto.getResources().getString(R.string.actualizar_app_dialogo5) + " (Version "+version_web+")");
                    dialog.setMessage(R.string.actualizar_app_dialogo4);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("Si", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (Funciones.isOnline(contexto))
                            {
                                // Si el dispositivo tiene internet, descarga.
                                new descargarApk().execute();
                            }
                            else
                                Toast.makeText(contexto, Constantes.NO_INTERNET, Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener()
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
                    // Ya esta actualizado
                    Log.d("Datos", "Ya estas actualizado!");

                    AlertDialog.Builder dialog = new AlertDialog.Builder(contexto);

                    dialog.setMessage(R.string.actualizar_app_dialogo3);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
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
                Toast.makeText(contexto, "Ha ocurrido un error al consultar los datos.." + result, Toast.LENGTH_LONG).show();
                Log.d("ERROR",result);
            }
        }
    }

    /**
     * Consulta la version mas actualizada de la aplicacion en la web.
     * @return La ultima version estable de la aplicacion cargada en la web.
     */
    private String consultarVersionWeb()
    {
        Log.d("Opciones", "consultarVersionWeb");

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        String URL = Constantes.IP_Server + "consultar_ult_version.php";
        JSONArray jdata = post.getserverdata(postparameters2send, URL);

        if (jdata!=null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                this.version_web = json_data.getString("version");
                this.URL = json_data.getString("url");
                return this.version_web;
            }
            catch (JSONException e)
            {
                Log.e("Error consultarVers",""+e);
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
     * Clase para descargar en 2do plano la apk de la aplicacion actualizada desde la web.
     */
    private class descargarApk extends AsyncTask< String, String, String >
    {
        @Override
        protected void onPreExecute()
        {
            CANCELADO = false;
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Descargando actualizacion...");
            pDialog.setMessage("Por favor, espere ...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    descargarArchivo.cancelar();
                    Log.w("DownloadFromUrl", "Descarga cancelada!");
                    cancel(true);
                }
            });
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                if (downloadApplication())
                    return "ok";
                else
                    return "err";
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
                return ""+e;
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            if (result.equals("ok"))
            {
                instalarAplicacion();
            }
            else
            {
                if(CANCELADO)
                    Toast.makeText(contexto, "La descarga fue cancelada", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(contexto, "Ha ocurrido un error al descargar la aplicacion.." + result, Toast.LENGTH_LONG).show();
                Log.d("ERROR",result);
            }
        }

        @Override
        protected void onCancelled()
        {
            super.onCancelled();
            Log.w("DownloadFromUrl", "Descarga cancelada!!");
            pDialog = new ProgressDialog(contexto);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setMax(100);
            pDialog.setProgress(0);

            if(CANCELADO)
                Toast.makeText(contexto, "La descarga fue cancelada", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Descarga un archivo desde el internet, en este caso la aplicacion que se va a instalar.
     * @return True si la aplicacion/archivo fue descargado exitosamente, False en caso contrario.
     */
    private boolean downloadApplication()
    {
        //Descargar el archivo..
        ByteArrayBuffer baf = descargarArchivo.DownloadFromUrl(URL, 2);

        if (baf!=null) // Si es null, significa que fue cancelado..
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
     * Used for comparing different versions of software
     * @param local_version_string the version name of the app installed on the system
     * @param online_version_string the version name of the app released on the Google Play
     * @return true if a the online_version_string is greater than the local_version_string
     */
    private static boolean newer_version_available(String local_version_string, String online_version_string)
    {
        DefaultArtifactVersion local_version_mvn = new DefaultArtifactVersion(local_version_string);
        DefaultArtifactVersion online_version_mvn = new DefaultArtifactVersion(online_version_string);
        return local_version_mvn.compareTo(online_version_mvn) == -1 && !local_version_string.equals("");
    }

    /**
     * Instala la aplicacion una vez fue descargada.
     */
    private void instalarAplicacion()
    {
        Log.d("instalarAplicacion","Instalando paquete (No funciona)   : "+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + Constantes.APP_FILE_NAME);
        Log.d("instalarAplicacion","Instalando paquete (Funciona)      : "+Environment.getExternalStorageDirectory() + Constantes.APP_FILE_LOCATION);

        File file = new File(Environment.getExternalStorageDirectory() + Constantes.APP_FILE_LOCATION);
        //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + Constantes.APP_FILE_NAME);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        contexto.startActivity(intent);
    }
}