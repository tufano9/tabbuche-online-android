package paquete.notifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import paquete.global.Funciones;

/**
 * Desarrollado por Gerson el 1/6/2015.
 * <p/>
 * Clase encargada de verificar si la notificacion esta activa y de crearla
 */
public class MyAlarmBroadcastReceiver extends MyBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.v("onReceive", "MyAlarmBroadcastReceiver");

        if (context != null && intent != null)
        {
            int pedidos_locales = Funciones.verificar_Notificacion(context);
            if (pedidos_locales > 0) Funciones.createNotification(context, pedidos_locales);
        }

        /*if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
        {
            Log.i("BOOT_COMPLETED","BOOT_COMPLETED");
            int pedidos_locales = Funciones.verificar_Notificacion(context);
            if(pedidos_locales>0)   Funciones.createNotification(context,pedidos_locales);

            Intent pushIntent = new Intent(context, MainActivity.class);
            context.startService(pushIntent);
        }
        else
        {
            int pedidos_locales = Funciones.verificar_Notificacion(context);
            if(pedidos_locales>0)   Funciones.createNotification(context,pedidos_locales);
        }*/
    }
}
