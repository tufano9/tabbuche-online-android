package paquete.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import paquete.database.DBAdapter;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.global.library.Httppostaux;
import paquete.tufanoapp.R;

/**
 * Desarrollado por Gerson el 27/5/2015.
 */
public class MyBroadcastReceiver extends BroadcastReceiver
{

    private static DBAdapter                  manager;
    private static NotificationCompat.Builder builder;
    private static NotificationManager        nm;
    private        Context                    contexto;
    private        boolean                    resultado, internet;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        contexto = context;
        /*Intent mainIntent = new Intent (context, SimpleTaskReminderActivity.class);
        mainIntent.putExtra("refresh_reminders", true);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity (mainIntent);*/

        String contenido = contexto.getResources().getString(R.string.contenido_notificacion);

        String titulo = contexto.getResources().getString(R.string.titulo_notificacion);
        titulo = titulo.replace("%", "" + Funciones.verificar_Notificacion(contexto));

        builder = new NotificationCompat.Builder(contexto);
        Intent intento = new Intent(contexto, MyBroadcastReceiver.class);
        intento.setAction(Constantes.ENVIAR_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, 234324243, intento, 0);

        builder.setContentIntent(pendingIntent);
        builder.setSound(null);
        //builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        // Sets the ticker text
        builder.setTicker(contexto.getResources().getString(R.string.titulo_notificacion));

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(titulo);
        builder.setContentText(contenido);

        nm = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);

        //if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))

        manager = new DBAdapter(contexto);
        String action = intent.getAction();

        Log.v("onReceive", "OpenNotification : " + action);

        if (Constantes.ENVIAR_ACTION.equals(action))
        {
            Log.v("shuffTest", "Downloader");
            int pedidos_locales = Funciones.verificar_Notificacion(contexto);

            if (pedidos_locales > 0)
                new Downloader().execute();
            else
                nm.cancel(Constantes.NOTIFICACIONID);
        }
        else //if(NO_ACTION.equals(action))
        {
            Log.v("shuffTest", "Alarma");
        }

    }

    /**
     * Envia los pedidos que esten hechos solo de forma local al servidor web.
     *
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private boolean enviarPedidoLocales()
    {
        if (Funciones.isOnline(contexto))
        {
            internet = true;
            Httppostaux post                = new Httppostaux();
            String      URL_realizar_pedido = Constantes.IP_Server + "realizar_pedido.php";
            int         logstatus, y;

            Cursor            cursor          = manager.cargarCursorPedidos_Locales();
            ArrayList<String> id_pedido_local = new ArrayList<>();
            ArrayList<String> id_vendedor     = new ArrayList<>();
            ArrayList<String> id_cliente      = new ArrayList<>();
            ArrayList<String> monto_total     = new ArrayList<>();
            ArrayList<String> observaciones   = new ArrayList<>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                //Log.d("PEDIDO ID: "+cursor.getString(0),"id_vendedor: "+cursor.getString(2)+" , id_cliente: "+cursor.getString(3)+" , monto: "+cursor.getString(5)+" , observaciones: "+cursor.getString(7));
                id_pedido_local.add(cursor.getString(0));
                id_vendedor.add(cursor.getString(2));
                id_cliente.add(cursor.getString(3));
                monto_total.add(cursor.getString(5));
                observaciones.add(cursor.getString(7));
            }

            cursor.close();

            builder.setProgress(id_pedido_local.size(), 0, false);

            for (int i = 0; i < id_pedido_local.size(); i++)
            {
                y = 0;
                String id_producto = "", cantidad_pares = "", cantidad_bultos = "", precio_unitario = "", subtotal = "", numeracion = "";
                Cursor cursor2     = manager.cargarCursorPedidos_Locales_Detalles_ID(id_pedido_local.get(i));

                for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
                {
                    Log.d("PEDIDO ID: " + cursor2.getString(1), "id_producto: " + cursor2.getString(2) + " , cantidad_pares: " + cursor2.getString(3) + " , cantidad_bultos: " + cursor2.getString(4) + " , numeracion: " + cursor2.getString(5) + " , precio_unitario: " + cursor2.getString(6) + " , subtotal: " + cursor2.getString(7));

                    if (y == 0)
                    {
                        id_producto = cursor2.getString(2);
                        cantidad_pares = cursor2.getString(3);
                        cantidad_bultos = cursor2.getString(4);
                        numeracion = cursor2.getString(5);
                        precio_unitario = cursor2.getString(6);
                        subtotal = cursor2.getString(7);
                        //subtotal = String.valueOf( Double.parseDouble(cantidad_pares) * Double.parseDouble(cantidad_bultos) * Double.parseDouble(precio_unitario) );
                    }
                    else
                    {
                        id_producto = id_producto + "#" + cursor2.getString(2);
                        cantidad_pares = cantidad_pares + "#" + cursor2.getString(3);
                        cantidad_bultos = cantidad_bultos + "#" + cursor2.getString(4);
                        numeracion = numeracion + "#" + cursor2.getString(5);
                        precio_unitario = precio_unitario + "#" + cursor2.getString(6);
                        subtotal = subtotal + "#" + cursor2.getString(7);
                        //subtotal =  subtotal + "#" + String.valueOf( Double.parseDouble(cursor.getString(4)) * Double.parseDouble(cursor.getString(6)) * Double.parseDouble(cursor.getString(2)) );
                    }
                    y++;
                }

                cursor2.close();

                // ENVIAR
                ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
                postparameters2send.add(new BasicNameValuePair("id_vendedor", id_vendedor.get(i)));
                postparameters2send.add(new BasicNameValuePair("id_cliente", id_cliente.get(i)));
                postparameters2send.add(new BasicNameValuePair("id_productos", id_producto));
                postparameters2send.add(new BasicNameValuePair("monto", monto_total.get(i)));
                postparameters2send.add(new BasicNameValuePair("observaciones", observaciones.get(i)));
                postparameters2send.add(new BasicNameValuePair("cantidad_pares", cantidad_pares));
                postparameters2send.add(new BasicNameValuePair("cantidad_bultos", cantidad_bultos));
                postparameters2send.add(new BasicNameValuePair("precio_unitario", precio_unitario));
                postparameters2send.add(new BasicNameValuePair("subtotal", subtotal));
                postparameters2send.add(new BasicNameValuePair("numeraciones", numeracion));

                JSONArray jdata = post.getserverdata(postparameters2send, URL_realizar_pedido);

                if (jdata != null && jdata.length() > 0)
                {
                    JSONObject json_data;
                    try
                    {
                        json_data = jdata.getJSONObject(0);
                        logstatus = json_data.getInt("logstatus");
                        String id_pedido     = json_data.getString("id_pedido");
                        String fecha_ingreso = json_data.getString("fecha_ingreso");
                        String codigo_pedido = json_data.getString("codigo_pedido");

                        if (logstatus == 1)
                        {
                            // Operacion exitosa
                            //DBAdapter manager2 = new DBAdapter(contexto);
                            manager.insertar_pedido(id_pedido, id_vendedor.get(i), id_cliente.get(i), fecha_ingreso, Double.parseDouble(monto_total.get(i)), "En espera de aprobacion", observaciones.get(i), codigo_pedido);
                            manager.insertar_pedido_detalles(id_pedido, id_producto, cantidad_pares, cantidad_bultos, numeracion, precio_unitario, subtotal);
                            manager.actualizar_actualizacionesPedido(id_vendedor.get(i), fecha_ingreso);
                            manager.eliminar_pedidos_locales_ID(id_pedido_local.get(i));
                            builder.setProgress(id_pedido_local.size(), i + 1, false);
                            nm.notify(Constantes.NOTIFICACIONID, builder.build());
                            //publishProgress(Math.min(i, 100));
                            Log.d("PEDIDO", "ENVIADO");
                        }
                        else
                        {
                            Log.d("PEDIDO", "ERROR ENVIANDO");
                            return false;
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
            }

            return true;
        }
        else
        {
            internet = false;
            return false;
        }
    }

    /**
     * Clase para el envio en segundo plano de los pedidos locales.
     */
    private class Downloader extends AsyncTask<Void, Integer, Integer>
    {
        @Override
        protected void onPreExecute()
        {
            // Displays the progress bar for the first time.
            super.onPreExecute();
            builder.setSound(null);
            builder.setProgress(100, 0, false);
            nm.notify(Constantes.NOTIFICACIONID, builder.build());
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            // Update progress
            builder.setProgress(100, values[0], false);
            nm.notify(Constantes.NOTIFICACIONID, builder.build());
            super.onProgressUpdate(values);
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            resultado = enviarPedidoLocales();

            /*for (int i = 0; i <= 100; i += 50)
            {
                // Sets the progress indicator completion percentage
                publishProgress(Math.min(i, 100));
                try
                {
                    // Sleep for 5 seconds
                    Thread.sleep(2 * 1000);
                }
                catch (InterruptedException e)
                {
                    Log.d("TAG", "sleep failure");
                }
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            super.onPostExecute(result);

            if (!resultado)
            {
                if (!internet)
                    builder.setContentText("Verifique su conexion a internet!");
                else
                    builder.setContentText("No se ha podido enviar los pedidos!");
                // Removes the progress bar
                builder.setProgress(0, 0, false);

                builder.setAutoCancel(true);
                Intent        intento       = new Intent(contexto, CloseNotification.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, 234324243, intento, 0);
                builder.setContentIntent(pendingIntent);

                nm.notify(Constantes.NOTIFICACIONID, builder.build());
                //Funciones.nm.cancel(Constantes.NOTIFICACIONID);
            }
            else
            {
                builder.setContentText("Pedidos enviados exitosamente!");
                // Removes the progress bar
                builder.setProgress(0, 0, false);

                builder.setAutoCancel(true);
                Intent intento = new Intent(contexto, CloseNotification.class);
                intento.putExtra("notificationID", Constantes.NOTIFICACIONID);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, 234324243, intento, 0);
                builder.setContentIntent(pendingIntent);

                nm.notify(Constantes.NOTIFICACIONID, builder.build());
                //Funciones.nm.cancel(Constantes.NOTIFICACIONID);
            }

        }
    }
}