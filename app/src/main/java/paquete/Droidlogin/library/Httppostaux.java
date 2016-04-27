package paquete.Droidlogin.library;

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
public class Httppostaux
{
	private InputStream is = null;
	private String result = "";

	public JSONArray getserverdata(ArrayList<NameValuePair> parameters, String urlwebserver )
	{
		//conecta via http y envia un post.
		httppostconnect(parameters,urlwebserver);

		if (is!=null)
		{
			//si obtuvo una respuesta
			getpostresponse();
			return getjsonarray();
		}
		else
		{
            Log.e("ERROR","getserverdata: InputStream = null");
			return null;
		}
	}

	/**
	 * Realiza la conexion a la url indicada usando un metodo post para enviar informacion a dicha
	 * url.
	 * @param parametros Parametros pasar como POST a la url.
	 * @param urlwebserver URL destino.
	 */
	private void httppostconnect(ArrayList<NameValuePair> parametros, String urlwebserver)
	{
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(urlwebserver);
			httppost.setEntity(new UrlEncodedFormEntity(parametros));

			//ejecuto peticion enviando datos por POST
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}
		catch(Exception e)
		{
			Log.e("log_tag", "Error in http connection "+e.toString());
		}
	}

    /**
     * Obtiene una respuesta por parte del server y lo convierte a un string.
     */
	private void getpostresponse()
	{
		//Convierte respuesta a String
		try
		{
            String ENCODING = "UTF-8"; //ISO-8859-1
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, ENCODING),8);
			StringBuilder sb = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null)
			{
				sb.append(line).append("\n");
			}

			is.close();
			result=sb.toString();
			Log.e("getpostresponse"," result= "+sb.toString());
		}
		catch(Exception e)
		{
			Log.e("Error", e.toString());
		}
	}

    /**
     * Retorna el jsonArray de la respuesta generada por el server.
     * @return JSONArray con la respuesta del server.
     */
	private JSONArray getjsonarray()
	{
		//parse json data
		try
		{
			return new JSONArray(result);
		}
		catch(JSONException e)
		{
			Log.e("log_tag", "Error parsing data "+e.toString());
			return null;
		}
	}
}