package paquete.global.download_from_url;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;

@SuppressWarnings("unused")
class downloadBitmap
{
    /**
     * Descarga una imagen desde un URL proporcionado.
     *
     * @param url URL donde se encuentra la imagen.
     * @return Bitmap con la imagen descargada.
     */
    public static Bitmap descargarImagen(String url)
    {
        // initilize the default HTTP client object
        final DefaultHttpClient client = new DefaultHttpClient();

        //forming a HttoGet request
        final HttpGet getRequest = new HttpGet(url);
        try
        {
            HttpResponse response = client.execute(getRequest);

            //check 200 OK for success
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK)
            {
                Log.e("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                InputStream inputStream = null;
                try
                {
                    // getting contents from the stream
                    inputStream = entity.getContent();

                    // decoding stream data back into image Bitmap that android understands
                    return BitmapFactory.decodeStream(inputStream);
                }
                finally
                {
                    if (inputStream != null)
                        inputStream.close();

                    // Consuming an HttpEntity ensures that all the resources allocated to this
                    // entity are deallocated

                    // This is deprecated, so i must use the next line..
                    //entity.consumeContent();
                    EntityUtils.consume(entity);
                }
            }
        }
        catch (Exception e)
        {
            // You Could provide a more explicit error message for IOException
            getRequest.abort();
            Log.e("ImageDownloader", "Something went wrong while retrieving bitmap from " + url + e.toString());
        }
        return null;
    }
}