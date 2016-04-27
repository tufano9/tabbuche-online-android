package paquete.notifications;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import paquete.download_from_url.descargarArchivo;
import paquete.global.Constantes;

/**
 * Desarrollado por Gerson el 30/5/2015.
 *
 * Clase para cerrar una notificacion.
 */
public class CloseNotification extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.v("onReceive", "CloseNotification");
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id_notification = intent.getIntExtra("notificationID",0);
        nm.cancel(id_notification);
        if(id_notification== Constantes.NOTIFICACIONID2)
        {
            Log.v("onReceive", "CancelNotification2");
            descargarArchivo.cancelar();
        }
    }
}