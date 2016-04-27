package paquete.notifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import paquete.global.Funciones;

/**
 * Desarrollado por Gerson el 21/10/2015.
 *
 * Clase que se encargara de verificar las actualizaciones que tenga la app en segundo plano.
 */
public class verificarActualizacionesApp extends MyBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.v("onReceive", "verificarActualizacionesApp");
        if (context!=null && intent!=null)
        {
            Funciones.verificar_ActualizacionesApp(context);
        }
    }
}
