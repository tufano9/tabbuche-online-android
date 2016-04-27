package paquete.tufanoapp;

import android.app.Activity;
import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import paquete.global.Funciones;

/**
 * Clase encargada de descargar de un URL un archivo.
 */
public class ImageManager extends Activity
{
    public static ByteArrayBuffer DownloadFromUrl(String imageURL, String fileName)
    {
        ByteArrayBuffer baf = new ByteArrayBuffer(50);
        try
        {
            URL url = new URL(imageURL); //you can write here any link

            long startTime = System.currentTimeMillis();
            Log.i("ImageManager", "Iniciando descarga");
            Log.i("ImageManager", "Url a descargar:" + url);
            Log.i("ImageManager", "Archivo a descargar:" + fileName);

            /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();

            /* Define InputStreams to read from the URLConnection. */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            int fileLength = ucon.getContentLength();
            Log.i("DownloadTask", "fileLength: " + Funciones.readableFileSize(fileLength));

            /* Read bytes to the Buffer until there is nothing more to read(-1). */
            int current;
            while ((current = bis.read()) != -1)
            {
                baf.append((byte) current);
            }

            Log.i("ImageManager", "Descarga terminada en " + ((System.currentTimeMillis() - startTime) / 1000) + " segundos");
        }

        catch (IOException e)
        {
            Log.e("ImageManager", "Error: " + e);
        }

        return baf;

    }
}