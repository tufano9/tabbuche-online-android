package paquete.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import paquete.global.Funciones;

/**
 * Desarrollado por Gerson el 2/7/2015.
 * <p/>
 * Clase que se ejecutara al reiniciar el dispositivo.
 */
public class MyRebootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("MyRebootReceiver", "REBOOTING");
        int pedidos_locales = Funciones.verificar_Notificacion(context);
        if (pedidos_locales > 0) Funciones.createNotification(context, pedidos_locales);

        /*Intent serviceIntent = new Intent(context, MeCorpServiceClass.class);
        serviceIntent.putExtra("caller", "RebootReceiver");
        context.startService(serviceIntent);*/
    }
}