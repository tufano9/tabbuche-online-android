package paquete.global.download_from_url;

import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import paquete.actualizaciones_app.actualizar_App;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.notifications.actualizarApp_SegundoPlano;

/**
 * Desarrollado por Gerson el 21/10/2015.
 */
public class descargarArchivo
{
    private static int fileLength, OPCION;
    private static long    total;
    private static boolean CANCELADO;

    /**
     * Descarga un archivo encontrado en una URL.
     *
     * @param URL La Url del archivo a descargar
     * @param opc Si es 1: Funcionara en base a la actualizacion en 2do plano, Si es 2: Funcionara
     *            en base a la actualizacion InApp
     * @return El ByteArrayBuffer del archivo descargado
     */
    public static ByteArrayBuffer DownloadFromUrl(String URL, int opc)
    {
        ByteArrayBuffer baf = new ByteArrayBuffer(50);
        OPCION = opc;
        CANCELADO = false;
        total = 0;
        try
        {
            URL url = new URL(URL); //you can write here any link

            long startTime = System.currentTimeMillis();
            Log.i("ImageManager", "Iniciando descarga..");
            Log.i("ImageManager", "Url a descargar:" + url);

            /* Open a connection to that URL. */
            URLConnection connection = url.openConnection();

            /* Define InputStreams to read from the URLConnection. */
            InputStream         is  = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            fileLength = connection.getContentLength();
            //long total = 0;
            byte data[] = new byte[4096];
            Log.i("DownloadTask", "fileLength: " + Funciones.readableFileSize(fileLength));

            /* Read bytes to the Buffer until there is nothing more to read(-1). */

            int current;
            while ((current = bis.read(data)) != -1)
            {
                if (CANCELADO)
                {
                    bis.close();
                    return null;
                }
                //baf.append((byte) current);
                baf.append(data, 0, current);
                total += current;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                {
                    // Update progress
                    actualizarProgreso();
                }
            }
            Log.i("ImageManager", "Descarga terminada en " + ((System.currentTimeMillis() - startTime) / 1000) + " segundos");
        }
        catch (IOException e)
        {
            Log.e("ImageManager", "Error: " + e);
        }

        return baf;
    }

    /**
     * Actualiza el proceso de la descarga mostrado en la barra de estado.
     */
    private static void actualizarProgreso()
    {
        int progreso = (int) (total * 100 / fileLength);

        switch (OPCION)
        {
            case 1:
            {
                actualizarApp_SegundoPlano.builder.setProgress(100, progreso, false);
                actualizarApp_SegundoPlano.nm.notify(Constantes.NOTIFICACIONID2, actualizarApp_SegundoPlano.builder.build());
            }
            break;
            case 2:
            {
                actualizar_App.pDialog.setProgress(progreso);
            }
            break;
        }
    }

    /**
     * Cancela la descarga del archivo.
     */
    public static void cancelar()
    {
        Log.w("DownloadFromUrl", "Descarga cancelada");
        CANCELADO = true;
    }
}