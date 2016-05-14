package paquete.global;

import java.io.File;

/**
 * Desarrollado por Gerson el 10/5/2015.
 */
public class Constantes
{
    public final static int NUMERO_ACTUALIZACIONES = 12;
    public final static int NOTIFICACIONID         = 100;
    public final static int NOTIFICACIONID2        = 200;

    public final static int REQUESTCODE1 = 234324243;
    public final static int REQUESTCODE2 = 239601672;

    public final static  float  IMAGE_ALPHA                        = 0.5f;
    public final static  int    NUM_IMG                            = 1;
    // Horas x Minutos x Segundos (Una vez al dia) . . 60000 = 1 minuto en miliseconds
    public final static  long   TIEMPO_NOTIFICACION_ACTUALIZAR_APP = 24 * 60 * 60000;
    // Key para las contraseñas (Puede ser generado aleatoriamente y guardando dicho valor en BD)
    public final static  String KEY                                = "2356a3a42ba5781f80a72dad3f90aeee8ba93c7637aaf218a8b8c18c";
    public final static  String ALGORITM                           = "Blowfish";
    public final static  String PENDIENTE                          = "Envio Pendiente";
    public final static  String IP_Server                          = "http://www.tabbuche.net/tufano/android/";
    public final static  String ENVIAR_ACTION                      = "Enviar";
    public final static  String ACTUALIZAR_ACTION                  = "Actualizar";
    public final static  String NO_INTERNET                        = "Debe estar conectado a internet!";
    public final static  String MONEDA                             = "Bs.F";
    public static final  String NO_PERMITIDO                       = "Funcionalidad no permitida.";
    public static final  String APP_FILE_NAME                      = "tufanoApp.apk";
    public final static  int    IMG_MUESTRARIO_DIMEN               = 130;
    private final static int    HORAS_NOTIFICACION                 = 5;
    // Cada 5 horas
    public final static  long   TIEMPO_NOTIFICACION                = HORAS_NOTIFICACION * 60 * 60000;
    private static final String APP_FILE_FOLDER                    = File.separator + "download" + File.separator;
    public static final  String APP_FILE_LOCATION                  = APP_FILE_FOLDER + APP_FILE_NAME;
}