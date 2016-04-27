package paquete.database;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
/*
    CLASE AUXILIAR PARA EL ENVIO DE PETICIONES A NUESTRO SISTEMA Y MANEJO DE RESPUESTA.
*/

class Httppostaux
{
    private InputStream is     = null;
    private String      result = "";

    public JSONArray getserverdata(ArrayList<NameValuePair> parameters, String urlwebserver)
    {
        //conecta via http y envia un post.
        httppostconnect(parameters, urlwebserver);

        if (is != null)
        {
            //si obtuvo una respuesta
            getpostresponse();
            return getjsonarray();
        }
        else
        {
            Log.e("ERROR", "getserverdata: InputStream = null");
            return null;
        }
    }

    private void httppostconnect(ArrayList<NameValuePair> parametros, String urlwebserver)
    {
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost   httppost   = new HttpPost(urlwebserver);
            httppost.setEntity(new UrlEncodedFormEntity(parametros));
            //ejecuto peticion enviando datos por POST
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity   entity   = response.getEntity();
            is = entity.getContent();
            //EntityUtils.consume(entity);
        }
        catch (Exception e)
        {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
    }

    private void getpostresponse()
    {
        //Convierte respuesta a String
        try
        {
            String         ENCODING = "UTF-8"; //ISO-8859-1
            BufferedReader reader   = new BufferedReader(new InputStreamReader(is, ENCODING), 8);
            StringBuilder  sb       = new StringBuilder();
            String         line;

            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }

            is.close();
            result = sb.toString();
            Log.e("getpostresponse", " result= " + sb.toString());
        }
        catch (Exception e)
        {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
    }

    private JSONArray getjsonarray()
    {
        //parse json data
        try
        {
            return new JSONArray(result);
        }
        catch (JSONException e)
        {
            Log.e("log_tag", "Error parsing data " + e.toString());
            return null;
        }
    }
}