package paquete.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Desarrollado por Gerson el 1/6/2015.
 */
public class alarmNotification
{
    /*
     * Metodo para crear una notificacion que se repetira cada cierto tiempo..
     *
     * @param hours Cada cuantas horas se repetira la notificacion
     * @param contexto Contexto de la aplicacion.
     */
    /*public static void createScheduledNotification(int hours, Context contexto)
    {
        Log.i("alarmNotification", "createScheduledNotification");
        // Get new calendar object and set the date to now
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Add defined amount of days to the date
        //calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.SECOND, hours);

        // Retrieve alarm manager from the system
        AlarmManager alarmManager = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
        // Every scheduled intent needs a different ID, else it is just executed once
        int id = (int) System.currentTimeMillis();

        // Prepare the intent which should be launched at the date
        Intent intent = new Intent(contexto, MyBroadcastReceiver.class);
        intent.setAction("ALARMA");

        // Prepare the pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Register the alert in the system. You have the option to define if the device has to wake up on the alert or not
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }*/

    /**
     * Inicializa una notificacion persistente con el tiempo
     *
     * @param contexto Contexto de la aplicacion.
     */
    public static void notificaciones_persistentes(Context contexto)
    {
        Log.i("alarmNotification", "notificaciones_persistentes");
        Intent        intent = new Intent(contexto, MyAlarmBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(contexto, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager  am     = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);

        //boolean alarmUp = (PendingIntent.getBroadcast(contexto, 0, intent, PendingIntent.FLAG_NO_CREATE) != null);

        /*if (alarmUp)
            Log.i("myTag", "Alarm is already active");
        else*/
        am.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(), paquete.global.Constantes.TIEMPO_NOTIFICACION, sender);
    }

    /**
     * Inicializa una notificacion persistente con el tiempo para la actualizacion de la app.
     *
     * @param contexto Contexto de la aplicacion.
     */
    public static void notificaciones_persistentes_actualizarApp(Context contexto)
    {
        Log.i("alarmNotification", "notificaciones_persistentes_actualizarApp");
        Intent        broadcastedIntent = new Intent(contexto, verificarActualizacionesApp.class);
        PendingIntent sender            = PendingIntent.getBroadcast(contexto, 0, broadcastedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager  am                = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);

        /*boolean alarmUp = (PendingIntent.getBroadcast(contexto, 0, intent, PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
            Log.i("myTag", "La alarma [actualizar_app] ya esta activa");
        else*/
        am.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(), paquete.global.Constantes.TIEMPO_NOTIFICACION_ACTUALIZAR_APP, sender);
    }
}