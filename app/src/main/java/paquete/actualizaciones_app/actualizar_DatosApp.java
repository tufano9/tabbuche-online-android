package paquete.actualizaciones_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

import paquete.database.DBAdapter;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.global.library.Httppostaux;
import paquete.tufanoapp.Home;
import paquete.tufanoapp.ImageManager;
import paquete.tufanoapp.opciones.FragmentOpciones;

/**
 * Clase encargada de Verificar actualizaciones en los datos de la aplicacion, como por ejemplo
 * nuevas lineas agregadas, nuevos modelos, productos, etc.
 */
public class actualizar_DatosApp
{
    private final int MODE_PRIVATE = 0;
    private final Context   contexto;
    private final DBAdapter manager;
    private final Httppostaux post = new Httppostaux();
    private final String usuario, codigo_usuario;
    private final ArrayList<String>  fecha_local_faltantes_lineas    = new ArrayList<>();
    private final ArrayList<String>  ids_faltantes_lineas            = new ArrayList<>();
    private final ArrayList<String>  nombre_faltantes_lineas         = new ArrayList<>();
    private final ArrayList<String>  links_faltantes_lineas          = new ArrayList<>();
    private final ArrayList<String>  fecha_local_faltantes_modelos   = new ArrayList<>();
    private final ArrayList<String>  ids_faltantes_modelos           = new ArrayList<>();
    private final ArrayList<String>  nombre_faltantes_modelos        = new ArrayList<>();
    private final ArrayList<String>  links_faltantes_modelos         = new ArrayList<>();
    private final ArrayList<String>  fecha_local_faltantes_productos = new ArrayList<>();
    private final ArrayList<String>  ids_faltantes_productos         = new ArrayList<>();
    private final ArrayList<String>  nombre_faltantes_productos      = new ArrayList<>();
    private final ArrayList<String>  links_faltantes_productos       = new ArrayList<>();
    private final ArrayList<Integer> numero_faltantes_productos      = new ArrayList<>();
    private ProgressDialog pDialog;
    private String nombre, apellido, telefono, email, fecha, estado, lineas_desh, modelos_desh,
            productos_desh, contrasena;
    private JSONArray ids_lineas, links_lineas, fecha_lineas, nombre_lineas;
    private JSONArray ids_modelos, nombre_modelos, links_modelos, fecha_modelos;
    private JSONArray links_productos, fecha_productos, nombre_productos;
    private JSONArray ids_tabla_completa;
    private int opc = 0;

    /**
     * Constructor de la clase. Este actualizar sera para el FragmentOpciones (El que el usuario
     * busca dentro de la app.
     */
    public actualizar_DatosApp(Context contexto, String codigo_usuario, int[] actualizar, String usuario)
    {
        Log.d("actualizarApp", "FragmentOpciones");
        this.contexto = contexto;
        this.codigo_usuario = codigo_usuario;
        this.usuario = usuario;
        manager = new DBAdapter(contexto);
        new asynclogin_actualizar_muestrario(actualizar).execute();
    }

    /**
     * Constructor de la clase. Este actualizar sera para el MainActivity (OPC = 1), es decir,
     * cuando ando logeando y la app detecta que hay actualizaciones de la informacion disponibles
     */
    public actualizar_DatosApp(Context contexto, String codigo_usuario, int[] actualizar, String contrasena, String usuario, String nombre, String apellido, String telefono, String email, String lineas_desh, String modelos_desh, String productos_desh, String fecha, String estado)
    {
        Log.d("actualizarApp", "MainActivity");
        this.contexto = contexto;
        this.codigo_usuario = codigo_usuario;
        this.contrasena = contrasena;
        this.usuario = usuario;
        this.opc = 1;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.lineas_desh = lineas_desh;
        this.modelos_desh = modelos_desh;
        this.productos_desh = productos_desh;
        this.fecha = fecha;
        this.estado = estado;
        manager = new DBAdapter(contexto);
        new asynclogin_actualizar_muestrario(actualizar).execute();
    }

    /**
     * Cuenta el numero de actualizaciones que deben realizarse.
     *
     * @param actualizacion Array de unos '1' y ceros '0' indicando que debe actualizarse y que no.
     * @return El numero de tablas a actualizar en la BD local.
     */
    private static int contarActualizaciones(int[] actualizacion)
    {
        int res = 0;
        for (int anActualizacion : actualizacion)
        {
            if (anActualizacion == 1)
                res++;
        }
        return res;
    }

    /**
     * Verifica el array de unos '1' y ceros '0' donde se indica que debe actualizarse, en caso
     * de haber un 1, se procede a la actualizacion.
     *
     * @param actualizacion Array donde se indique que debe actualizarse (con un 1) y que no debe
     *                      actualizarse (con un 0)
     * @return True si la operacion fue exitosa, False en caso contrario.
     * @throws JSONException En caso de encontrar algun error actualizando
     */
    private boolean obtener_datos(int[] actualizacion) throws JSONException
    {
        boolean res = false;
        for (int i = 0; i < actualizacion.length; i++)
        {
            if (actualizacion[i] == 1)
            {
                if (actualizar(i))
                    res = true;
                else
                {
                    res = false;
                    break;
                }
            }
        }
        return res;
    }

    /**
     * Actualiza el dato indicado bajo el parametro i.
     *
     * @param i Numero equivalente del dato que debe ser indentificado.
     * @return True si la actualizacion fue exitosa, False en caso contrario.
     * @throws JSONException En caso de ocurrir algun error actualizando.
     */
    private boolean actualizar(int i) throws JSONException
    {
        if (i == 0)
        {
            return obtener_lineas_muestrario(i);
        }
        else if (i == 1)
        {
            return obtener_modelos_muestrario(i);
        }
        else if (i == 2)
        {
            return obtener_productos_muestrario(i);
        }
        else if (i == 3)
        {
            return obtener_datos_usuario(i);
        }
        else if (i == 4)
        {
            return obtener_materiales_muestrario(i);
        }
        else if (i == 5)
        {
            return obtener_colores_muestrario(i);
        }
        else if (i == 6)
        {
            return obtener_bultos_muestrario(i);
        }
        else if (i == 7)
        {
            return obtener_clientes(i);
        }
        else if (i == 8)
        {
            return obtener_pedidos(i);
        }
        else if (i == 9)
        {
            return obtener_pedidos_detalles(i);
        }
        else if (i == 10)
        {
            return obtener_funciones_moviles(i);
        }
        return false;
    }

    /**
     * Descarga las lineas del muestrario desde la BD externa (web), hasta la BD local (android).
     *
     * @param num Numero entero que identifica la tabla a modificar, por ej 1 = modelos, 2 = productos.
     * @return True si la operacion fue exitosa, False en caso contrario.
     * @throws JSONException En caso de ocurrir algun error en la actualizacion.
     */
    private boolean obtener_lineas_muestrario(int num) throws JSONException
    {
        Log.d("ACTUALIZANDO", "Empezando a actualizar lineas..");
        Log.d("CEDULA", usuario);

        cambiar_mensaje_dialogo("Cargando lineas...");

        int logstatus = -1;

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        postparameters2send.add(new BasicNameValuePair("cedula", usuario));

        String URL_obtener_lineas = Constantes.IP_Server + "obtener_lineas_muestrario_new.php";

        JSONArray jdata = post.getserverdata(postparameters2send, URL_obtener_lineas);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");

                for (int i = 0; i < jdata.length(); i++)
                {
                    JSONObject jsonObject = jdata.getJSONObject(i);
                    JSONArray  ids        = jsonObject.getJSONArray("id_linea");
                    JSONArray  nombres    = jsonObject.getJSONArray("nombre_linea");
                    JSONArray  tallas     = jsonObject.getJSONArray("talla_linea");
                    JSONArray  links      = jsonObject.getJSONArray("img_linea");
                    JSONArray  fechas     = jsonObject.getJSONArray("fechas");

                    ids_lineas = ids;
                    links_lineas = links; // array de los links de las imagenes
                    fecha_lineas = fechas; // Fecha de ultima actualizacion de las lineas
                    nombre_lineas = nombres;

                    // Borra el contenido de la tabla indicada, esto con el objetivo de volver a
                    // descargar los datos nuevos.
                    manager.eliminar_data(num);
                    manager.insertar_lineas(ids_lineas, nombres, fechas, tallas);
                }
            }

            catch (JSONException e)
            {
                Log.e("Error lineas", "" + e);
                pDialog.dismiss();
                e.printStackTrace();
            }

            Cursor cursor_faltantes = manager.cargarCursorActualizacionesLineas();

            int contar = 0, descarga = 0;

            for (cursor_faltantes.moveToFirst(); !cursor_faltantes.isAfterLast(); cursor_faltantes.moveToNext())
            {
                File file = new File(contexto.getFilesDir(), nombre_lineas.getString(contar) + ".jpg");

                if (!file.exists() || !cursor_faltantes.getString(1).equals(fecha_lineas.getString(contar)))
                {
                    Log.d("Examinando", "linea: " + nombre_lineas.getString(contar) + ", id_externo: " + ids_lineas.getString(contar) + ", id_local: " + cursor_faltantes.getString(0) + ", link: " + links_lineas.getString(contar));
                    Log.d("Examinando_mas", "fecha_externo: " + fecha_lineas.getString(contar) + ", fecha_local: " + cursor_faltantes.getString(1));
                    Log.d("Descargar", "SI!!!! : existe? " + file.exists() + " : fechas? " + cursor_faltantes.getString(1).equals(fecha_lineas.getString(contar)));
                    fecha_local_faltantes_lineas.add(fecha_lineas.getString(contar));
                    ids_faltantes_lineas.add(ids_lineas.getString(contar));
                    links_faltantes_lineas.add(links_lineas.getString(contar));
                    nombre_faltantes_lineas.add(nombre_lineas.getString(contar));
                    Log.w("Descargar Modelo", "Añadido a la lista");
                    descarga++;
                }
                contar++;
            }

            cursor_faltantes.close();
            pDialog.incrementProgressBy(1);

            Log.w("FIN", "Lineas a descargar: " + descarga);
            Log.d("ACTUALIZANDO", "Terminando a actualizar lineas..");

            return logstatus != 0;
        }

        else
        {
            Log.e("JSON", "ERROR: NO DATA");
            Log.d("ACTUALIZANDO", "Terminando a actualizar lineas..");
            return false;
        }
    }

    /**
     * Descarga los modelos del muestrario desde la BD externa (web), hasta la BD local (android).
     *
     * @param num Numero entero que identifica la tabla a modificar, por ej 1 = modelos, 2 = productos.
     * @return True si la operacion fue exitosa, False en caso contrario.
     * @throws JSONException En caso de ocurrir algun error en la actualizacion.
     */
    private boolean obtener_modelos_muestrario(int num) throws JSONException
    {
        Log.d("ACTUALIZANDO", "Empezando a actualizar modelos..");
        cambiar_mensaje_dialogo("Cargando modelos...");
        //asynclogin_actualizar_muestrario.mensaje("Cargando modelos...");

        int                      logstatus           = -1;
        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        postparameters2send.add(new BasicNameValuePair("cedula", usuario));
        String    URL_obtener_modelos = Constantes.IP_Server + "obtener_modelos_muestrario_new.php";
        JSONArray jdata               = post.getserverdata(postparameters2send, URL_obtener_modelos);


        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");

                for (int i = 0; i < jdata.length(); i++)
                {
                    JSONObject jsonObject = jdata.getJSONObject(i);

                    JSONArray idModelo = jsonObject.getJSONArray("id_modelo");
                    JSONArray idLinea  = jsonObject.getJSONArray("id_linea");
                    JSONArray nombres  = jsonObject.getJSONArray("nombre_modelo");
                    JSONArray links    = jsonObject.getJSONArray("img_modelo");
                    JSONArray fechas   = jsonObject.getJSONArray("fechas");

                    ids_modelos = idModelo;
                    links_modelos = links;
                    fecha_modelos = fechas;
                    nombre_modelos = nombres;

                    manager.eliminar_data(num);
                    manager.insertar_modelos(ids_modelos, idLinea, nombres, fechas);
                }
            }

            catch (JSONException e)
            {
                Log.e("Error modelos", "" + e);
                pDialog.dismiss();
                e.printStackTrace();
            }

            Cursor cursor_faltantes = manager.cargarCursorActualizacionesModelos();

            int contar   = 0;
            int descarga = 0;

            for (cursor_faltantes.moveToFirst(); !cursor_faltantes.isAfterLast(); cursor_faltantes.moveToNext())
            {
                File file = new File(contexto.getFilesDir(), nombre_modelos.getString(contar) + ".jpg");

                if (!file.exists() || !cursor_faltantes.getString(1).equals(fecha_modelos.getString(contar)))
                {
                    Log.d("Examinando", "modelo: " + nombre_modelos.getString(contar) + ", id_externo: " + ids_modelos.getString(contar) + ", id_local: " + cursor_faltantes.getString(0) + ", link: " + links_modelos.getString(contar));
                    Log.d("Examinando_mas", "fecha_externo: " + fecha_modelos.getString(contar) + ", fecha_local: " + cursor_faltantes.getString(1));
                    Log.d("Descargar", "SI!!!! : existe? " + file.exists() + " : fechas? " + cursor_faltantes.getString(1).equals(fecha_modelos.getString(contar)));
                    fecha_local_faltantes_modelos.add(fecha_modelos.getString(contar));
                    ids_faltantes_modelos.add(ids_modelos.getString(contar));
                    links_faltantes_modelos.add(links_modelos.getString(contar));
                    nombre_faltantes_modelos.add(nombre_modelos.getString(contar));
                    Log.w("Descargar Modelo", "Añadido a la lista");
                    descarga++;
                }
                contar++;
            }

            cursor_faltantes.close();

            Log.w("FIN", "Modelos a descargar: " + descarga);
            Log.d("ACTUALIZANDO", "Terminando a actualizar modelos..");
            pDialog.incrementProgressBy(1);
            return logstatus != 0;
        }

        else
        {
            Log.d("ACTUALIZANDO", "Terminando a actualizar modelos..");
            Log.e("JSON  ", "ERROR: NO DATA");
            return false;
        }
    }

    /**
     * Descarga los productos del muestrario desde la BD externa (web), hasta la BD local (android).
     *
     * @param num_borrar Numero entero que identifica la tabla a modificar, por ej 1 = modelos, 2 = productos.
     * @return True si la operacion fue exitosa, False en caso contrario.
     * @throws JSONException En caso de ocurrir algun error en la actualizacion.
     */
    private boolean obtener_productos_muestrario(int num_borrar) throws JSONException
    {
        Log.d("ACTUALIZANDO", "Empezando a actualizar productos..");
        cambiar_mensaje_dialogo("Cargando productos...");

        int logstatus = -1;

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        postparameters2send.add(new BasicNameValuePair("cedula", usuario));
        String    URL_obtener_productos = Constantes.IP_Server + "obtener_productos_muestrario_new.php";
        JSONArray jdata                 = post.getserverdata(postparameters2send, URL_obtener_productos);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");

                for (int i = 0; i < jdata.length(); i++)
                {
                    JSONObject jsonObject = jdata.getJSONObject(i);

                    JSONArray idProducto        = jsonObject.getJSONArray("id_producto");
                    JSONArray idModelo          = jsonObject.getJSONArray("id_modelo");
                    JSONArray idLinea           = jsonObject.getJSONArray("id_linea");
                    JSONArray nombres           = jsonObject.getJSONArray("nombre_producto");
                    JSONArray nombre_real       = jsonObject.getJSONArray("nombre_real");
                    JSONArray links             = jsonObject.getJSONArray("img_producto");
                    JSONArray fechas            = jsonObject.getJSONArray("fechas");
                    JSONArray precios_productos = jsonObject.getJSONArray("precios");
                    JSONArray id_bultos         = jsonObject.getJSONArray("ids_bultos");
                    JSONArray id_materiales     = jsonObject.getJSONArray("ids_materiales");
                    JSONArray id_color          = jsonObject.getJSONArray("id_color");
                    ids_tabla_completa = jsonObject.getJSONArray("ids_tabla_completa");
                    links_productos = links;
                    fecha_productos = fechas;
                    nombre_productos = nombre_real;

                    manager.eliminar_data(num_borrar);
                    manager.insertar_productos(idProducto, idLinea, idModelo, nombres, fechas, precios_productos, id_bultos, id_materiales, nombre_real, id_color);
                }
            }

            catch (JSONException e)
            {
                Toast.makeText(contexto, "Error Productos: " + e, Toast.LENGTH_LONG).show();
                Log.e("Error Productos", "" + e);
                pDialog.dismiss();
                e.printStackTrace();
            }

            Cursor cursor_faltantes = manager.cargarCursorActualizacionesProductos();

            int contar   = 0;
            int num      = 1; // estaba en 0
            int k        = 1; // estaba en 0
            int descarga = 0;

            for (cursor_faltantes.moveToFirst(); !cursor_faltantes.isAfterLast(); cursor_faltantes.moveToNext())
            {

                /*num++;

                if (num == 6)
                {
                    num = 1;
                    k++;
                }*/

                File file = new File(contexto.getFilesDir(), nombre_productos.getString(contar) + "_0" + num + ".jpg");

                Log.w("Validando", nombre_productos.getString(contar) + "_0" + num + ".jpg existe? " + file.exists() + " : fechas? " + cursor_faltantes.getString(2).equals(fecha_productos.getString(contar)));

                if (!file.exists() || !cursor_faltantes.getString(2).equals(fecha_productos.getString(contar)))
                {
                    Log.d("Examinando", "producto: " + nombre_productos.getString(k) + "_0" + num + ".jpg, id_externo: " + ids_tabla_completa.getString(contar) + ", id_local: " + cursor_faltantes.getString(0) + ", num: " + num + "-" + cursor_faltantes.getString(1) + ", link: " + links_productos.getString(contar));
                    Log.d("Examinando_mas", "fecha_externo: " + fecha_productos.getString(contar) + ", fecha_local: " + cursor_faltantes.getString(2));
                    Log.d("Descargar", "SI!!!! : existe? " + file.exists() + " : fechas? " + cursor_faltantes.getString(2).equals(fecha_productos.getString(contar)));
                    fecha_local_faltantes_productos.add(fecha_productos.getString(contar));
                    ids_faltantes_productos.add(ids_tabla_completa.getString(contar));
                    links_faltantes_productos.add(links_productos.getString(contar));
                    numero_faltantes_productos.add(cursor_faltantes.getInt(1));
                    nombre_faltantes_productos.add(nombre_productos.getString(contar));
                    Log.w("Descargar", "Añadido a la lista");
                    descarga++;
                }
                contar++;
            }

            cursor_faltantes.close();

            Log.w("FIN", "Productos a descargar: " + descarga);
            Log.d("ACTUALIZANDO", "Terminando a actualizar productos..");

            pDialog.incrementProgressBy(1);
            return logstatus != 0;
        }
        else
        {
            Toast.makeText(contexto, "Error Productos: Terminando a actualizar productos", Toast.LENGTH_LONG).show();
            Log.d("ACTUALIZANDO", "Terminando a actualizar productos..");
            Log.e("JSON  ", "ERROR: NO DATA");
            return false;
        }
    }

    /**
     * Descarga los datos del usuario (nombre, apellido, etc) desde la BD externa (web), hasta la
     * BD local (android).
     *
     * @param num Numero entero que identifica la tabla a modificar, por ej 1 = modelos, 2 = productos.
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private boolean obtener_datos_usuario(int num)
    {
        int logstatus = -1;

        cambiar_mensaje_dialogo("Cargando datos...");
        //asynclogin_actualizar_muestrario.mensaje("Cargando datos...");

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        postparameters2send.add(new BasicNameValuePair("cedula", usuario));
        Log.d("Cedula", usuario);
        String    URL_obtener_datos_usuario = Constantes.IP_Server + "obtener_obtener_datos_usuario.php";
        JSONArray jdata                     = post.getserverdata(postparameters2send, URL_obtener_datos_usuario);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");

                for (int i = 0; i < jdata.length(); i++)
                {
                    JSONObject jsonObject               = jdata.getJSONObject(i);
                    String     nombre                   = jsonObject.getString("nombre");
                    String     apellido                 = jsonObject.getString("apellido");
                    String     email                    = jsonObject.getString("email");
                    String     telefono                 = jsonObject.getString("telefono");
                    String     lineas_deshabilitadas    = jsonObject.getString("lineas_deshabilitadas");
                    String     modelos_deshabilitados   = jsonObject.getString("modelos_deshabilitados");
                    String     productos_deshabilitados = jsonObject.getString("productos_deshabilitados");
                    String     pass                     = jsonObject.getString("pass");
                    String     salt                     = jsonObject.getString("salt"); // ESTO NO ES LA SALT REAL
                    String     fecha                    = jsonObject.getString("fecha");
                    String     estado                   = jsonObject.getString("estado");

                    manager.eliminar_data(num);
                    manager.insertar_usuario(codigo_usuario, usuario, nombre, apellido, telefono, email, lineas_deshabilitadas, modelos_deshabilitados, productos_deshabilitados, pass, salt, fecha, estado);
                }
            }

            catch (JSONException e)
            {
                Log.e("Error datos usuario", "" + e);
                pDialog.dismiss();
                e.printStackTrace();
            }

            pDialog.incrementProgressBy(1);

            return logstatus != 0;
        }

        else
        {
            Log.e("JSON  ", "ERROR: NO DATA");
            return false;
        }
    }

    /**
     * Descarga los materiales de los productos desde la BD externa (web), hasta la BD local (android).
     *
     * @param num Numero entero que identifica la tabla a modificar, por ej 1 = modelos, 2 = productos.
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private boolean obtener_materiales_muestrario(int num)
    {
        int logstatus = -1;
        cambiar_mensaje_dialogo("Cargando materiales...");
        //asynclogin_actualizar_muestrario.mensaje("Cargando materiales...");
        ArrayList<NameValuePair> postparameters2send    = new ArrayList<>();
        String                   URL_obtener_materiales = Constantes.IP_Server + "obtener_materiales_new.php";
        JSONArray                jdata                  = post.getserverdata(postparameters2send, URL_obtener_materiales);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");

                for (int i = 0; i < jdata.length(); i++)
                {
                    JSONObject jsonObject = jdata.getJSONObject(i);
                    JSONArray  r1         = jsonObject.getJSONArray("ids");
                    JSONArray  r2         = jsonObject.getJSONArray("nombres");
                    JSONArray  r3         = jsonObject.getJSONArray("ids_color");
                    JSONArray  r4         = jsonObject.getJSONArray("prefijos");
                    JSONArray  r5         = jsonObject.getJSONArray("ids_colores_bases");

                    manager.eliminar_data(num);
                    manager.insertar_materiales(r1, r2, r3, r4, r5);
                }
            }
            catch (JSONException e)
            {
                Log.e("Error materiales", "" + e);
                pDialog.dismiss();
                e.printStackTrace();
            }

            pDialog.incrementProgressBy(1);

            return logstatus != 0;
        }
        else
        {
            Log.e("JSON  ", "ERROR: NO DATA");
            return false;
        }
    }

    /**
     * Descarga los colores de los productos desde la BD externa (web), hasta la BD local (android).
     *
     * @param num Numero entero que identifica la tabla a modificar, por ej 1 = modelos, 2 = productos.
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private boolean obtener_colores_muestrario(int num)
    {
        int logstatus = -1;
        cambiar_mensaje_dialogo("Cargando colores...");
        Log.d("Actualizando", "COLORES");

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        String                   URL_obtener_colores = Constantes.IP_Server + "obtener_colores_new.php";
        JSONArray                jdata               = post.getserverdata(postparameters2send, URL_obtener_colores);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");

                for (int i = 0; i < jdata.length(); i++)
                {
                    JSONObject jsonObject = jdata.getJSONObject(i);
                    JSONArray  r1         = jsonObject.getJSONArray("ids");
                    JSONArray  r2         = jsonObject.getJSONArray("colores");

                    manager.eliminar_data(num);
                    manager.insertar_colores(r1, r2);
                }
            }
            catch (JSONException e)
            {
                Log.e("Error colores", "" + e);
                pDialog.dismiss();
                e.printStackTrace();
            }

            pDialog.incrementProgressBy(1);

            return logstatus != 0;
        }

        else
        {
            Log.e("JSON  ", "ERROR: NO DATA");
            return false;
        }
    }

    /**
     * Descarga los bultos de los productos desde la BD externa (web), hasta la BD local (android).
     *
     * @param num Numero entero que identifica la tabla a modificar, por ej 1 = modelos, 2 = productos.
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private boolean obtener_bultos_muestrario(int num)
    {
        int logstatus = -1;
        cambiar_mensaje_dialogo("Cargando bultos...");
        //asynclogin_actualizar_muestrario.mensaje("Cargando bultos...");

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        String                   URL_obtener_bultos  = Constantes.IP_Server + "obtener_bultos_new.php";
        JSONArray                jdata               = post.getserverdata(postparameters2send, URL_obtener_bultos);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");

                for (int i = 0; i < jdata.length(); i++)
                {
                    JSONObject jsonObject = jdata.getJSONObject(i);
                    JSONArray  r1         = jsonObject.getJSONArray("ids");
                    JSONArray  r2         = jsonObject.getJSONArray("nombres");
                    JSONArray  r3         = jsonObject.getJSONArray("tipos");
                    JSONArray  r4         = jsonObject.getJSONArray("rango_tallas");
                    JSONArray  r5         = jsonObject.getJSONArray("numeracion");
                    JSONArray  r6         = jsonObject.getJSONArray("numero_pares");

                    manager.eliminar_data(num);
                    manager.insertar_bulto(r1, r2, r3, r4, r5, r6);
                }
            }

            catch (JSONException e)
            {
                Log.e("Error bultos", "" + e);
                pDialog.dismiss();
                e.printStackTrace();
            }
            pDialog.incrementProgressBy(1);

            return logstatus != 0;
        }

        else
        {
            Log.e("JSON  ", "ERROR: NO DATA");
            return false;
        }
    }

    /**
     * Descarga los clientes de los usuarios desde la BD externa (web), hasta la BD local (android).
     *
     * @param num Numero entero que identifica la tabla a modificar, por ej 1 = modelos, 2 = productos.
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private boolean obtener_clientes(int num)
    {
        int logstatus = -1;
        cambiar_mensaje_dialogo("Cargando clientes...");

        Log.d("obtener_clientes", "INICIO");

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        postparameters2send.add(new BasicNameValuePair("id", codigo_usuario));
        String    URL_obtener_clientes = Constantes.IP_Server + "obtener_clientes.php";
        JSONArray jdata                = post.getserverdata(postparameters2send, URL_obtener_clientes);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");

                for (int i = 0; i < jdata.length(); i++)
                {
                    JSONObject jsonObject    = jdata.getJSONObject(i);
                    JSONArray  id_cliente    = jsonObject.getJSONArray("id_cliente");
                    JSONArray  id_vendedor   = jsonObject.getJSONArray("id_vendedor");
                    JSONArray  razon_social  = jsonObject.getJSONArray("razon_social");
                    JSONArray  rif           = jsonObject.getJSONArray("rif");
                    JSONArray  direccion     = jsonObject.getJSONArray("direccion");
                    JSONArray  telefono      = jsonObject.getJSONArray("telefono");
                    JSONArray  estado        = jsonObject.getJSONArray("estado");
                    JSONArray  email         = jsonObject.getJSONArray("email");
                    JSONArray  fecha_ingreso = jsonObject.getJSONArray("fecha_ingreso");

                    manager.eliminar_data(num);
                    manager.insertar_clientes(id_cliente, id_vendedor, razon_social, rif, direccion, telefono, estado, email, fecha_ingreso);
                }
            }
            catch (JSONException e)
            {
                Log.e("Error clientes", "" + e);
                pDialog.dismiss();
                e.printStackTrace();
            }

            pDialog.incrementProgressBy(1);
            Log.d("obtener_clientes", "FIN");
            return logstatus != 0;
        }
        else
        {
            Log.e("JSON  ", "ERROR: NO DATA");
            return false;
        }
    }

    /**
     * Descarga los pedidos de los usuarios desde la BD externa (web), hasta la BD local (android).
     *
     * @param num Numero entero que identifica la tabla a modificar, por ej 1 = modelos, 2 = productos.
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private boolean obtener_pedidos(int num)
    {
        int logstatus;

        Log.d("obtener_pedidos", "INICIO");
        cambiar_mensaje_dialogo("Cargando pedidos...");

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        Log.d("obtener_pedidos", "ID Usuario: " + codigo_usuario);
        postparameters2send.add(new BasicNameValuePair("id", codigo_usuario));
        String    URL_obtener_clientes = Constantes.IP_Server + "obtener_pedidos_new.php";
        JSONArray jdata                = post.getserverdata(postparameters2send, URL_obtener_clientes);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");

                if (logstatus == 1)
                {
                    for (int i = 0; i < jdata.length(); i++)
                    {
                        JSONObject jsonObject    = jdata.getJSONObject(i);
                        JSONArray  id_pedido     = jsonObject.getJSONArray("id_pedido");
                        JSONArray  codigo_pedido = jsonObject.getJSONArray("codigo_pedido");
                        JSONArray  id_vendedor   = jsonObject.getJSONArray("id_vendedor");
                        JSONArray  id_cliente    = jsonObject.getJSONArray("id_cliente");
                        JSONArray  fecha         = jsonObject.getJSONArray("fecha");
                        JSONArray  monto         = jsonObject.getJSONArray("monto");
                        JSONArray  estatus       = jsonObject.getJSONArray("estatus");
                        JSONArray  observaciones = jsonObject.getJSONArray("observaciones");

                        manager.eliminar_data(num);
                        manager.insertar_pedidos(id_pedido, id_vendedor, id_cliente, fecha, monto, estatus, observaciones, codigo_pedido);
                    }
                }
            }

            catch (JSONException e)
            {
                Log.e("Error pedidos", "" + e);
                pDialog.dismiss();
                e.printStackTrace();
            }

            pDialog.incrementProgressBy(1);
            Log.d("obtener_pedidos", "FIN");
            return true;//logstatus != 0
        }

        else
        {
            Log.e("JSON  ", "ERROR: NO DATA");
            return false;
        }
    }

    /**
     * Descarga los detalles de los pedidos desde la BD externa (web), hasta la BD local (android).
     *
     * @param num Numero entero que identifica la tabla a modificar, por ej 1 = modelos, 2 = productos.
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private boolean obtener_pedidos_detalles(int num)
    {
        int logstatus;

        Log.d("obtener_pedidos_detalle", "INICIO");
        cambiar_mensaje_dialogo("Cargando pedidos (detalles)...");

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        postparameters2send.add(new BasicNameValuePair("id", codigo_usuario));
        String    URL_obtener_pedidos_detalles = Constantes.IP_Server + "obtener_pedidos_detalles_new.php";
        JSONArray jdata                        = post.getserverdata(postparameters2send, URL_obtener_pedidos_detalles);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");

                if (logstatus == 1)
                {
                    for (int i = 0; i < jdata.length(); i++)
                    {
                        JSONObject jsonObject      = jdata.getJSONObject(i);
                        JSONArray  id_pedido       = jsonObject.getJSONArray("id_pedido");
                        JSONArray  id_producto     = jsonObject.getJSONArray("id_producto");
                        JSONArray  cantidad_pares  = jsonObject.getJSONArray("cantidad_pares");
                        JSONArray  cantidad_bultos = jsonObject.getJSONArray("cantidad_bultos");
                        JSONArray  numeracion      = jsonObject.getJSONArray("numeracion");
                        JSONArray  precio_unitario = jsonObject.getJSONArray("precio_unitario");
                        JSONArray  subtotal        = jsonObject.getJSONArray("subtotal");
                        JSONArray  talla           = jsonObject.getJSONArray("talla");

                        manager.eliminar_data(num);
                        manager.insertar_pedido_detalles(id_pedido, id_producto, cantidad_pares, cantidad_bultos, numeracion, precio_unitario, subtotal, talla);
                    }
                }
            }

            catch (JSONException e)
            {
                Log.e("Error pedidos_det", "" + e);
                pDialog.dismiss();
                e.printStackTrace();
            }

            pDialog.incrementProgressBy(1);
            Log.d("obtener_pedidos_detalle", "FIN");
            return true;
        }

        else
        {
            Log.e("JSON  ", "ERROR: NO DATA");
            return false;
        }
    }

    /**
     * Descarga las funcionalidades que los usuarios tienen permitido utilizar dentro de la app,
     * desde la BD externa (web), hasta la BD local (android).
     *
     * @param num Numero entero que identifica la tabla a modificar, por ej 1 = modelos, 2 = productos.
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private boolean obtener_funciones_moviles(int num)
    {
        int logstatus;

        Log.d("obtener_funcionesMovil", "INICIO");
        cambiar_mensaje_dialogo("Cargando Funcionalidades...");

        ArrayList<NameValuePair> postparameters2send = new ArrayList<>();
        Log.d("obtener_funcionesMovil", "ID Usuario: " + codigo_usuario);
        postparameters2send.add(new BasicNameValuePair("id", codigo_usuario));
        String    URL   = Constantes.IP_Server + "obtener_funciones_moviles.php";
        JSONArray jdata = post.getserverdata(postparameters2send, URL);

        if (jdata != null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");

                if (logstatus == 1)
                {
                    for (int i = 0; i < jdata.length(); i++)
                    {
                        JSONObject jsonObject    = jdata.getJSONObject(i);
                        JSONArray  home          = jsonObject.getJSONArray("home");
                        JSONArray  muestrario    = jsonObject.getJSONArray("muestrario");
                        JSONArray  pedidos       = jsonObject.getJSONArray("pedidos");
                        JSONArray  clientes      = jsonObject.getJSONArray("clientes");
                        JSONArray  perfil        = jsonObject.getJSONArray("perfil");
                        JSONArray  opciones      = jsonObject.getJSONArray("opciones");
                        JSONArray  estado_cuenta = jsonObject.getJSONArray("estado_cuenta");
                        //JSONArray fecha_ult_actualizacion = jsonObject.getJSONArray("fecha_ult_actualizacion");
                        JSONArray id_usuario = jsonObject.getJSONArray("id_usuario");

                        manager.eliminar_data(num);
                        manager.insertar_funciones(id_usuario, home, muestrario, pedidos, clientes, perfil, opciones, estado_cuenta);
                    }
                }
            }

            catch (JSONException e)
            {
                Log.e("Error pedidos", "" + e);
                pDialog.dismiss();
                e.printStackTrace();
            }

            pDialog.incrementProgressBy(1);
            Log.d("obtener_pedidos", "FIN");
            return true;//logstatus != 0
        }

        else
        {
            Log.e("JSON  ", "ERROR: NO DATA");
            return false;
        }
    }

    /**
     * Ingresa al Home de la aplicacion una vez la app fue actualizada.
     */
    private void entrar_Home()
    {
        String pass = null;
        try
        {
            pass = Arrays.toString(Funciones.encrypt(Constantes.KEY, contrasena));
            //Log.d("Encriptando","A "+contrasena+" ->> "+pass);
            //Log.d("Desencriptando","A "+contrasena+" ->> "+decrypt(KEY,encrypt(KEY,contrasena)));
        }
        catch (GeneralSecurityException e)
        {
            e.printStackTrace();
        }

        manager.insertar_usuario(codigo_usuario, usuario, nombre, apellido, telefono, email, lineas_desh, modelos_desh, productos_desh, pass, Constantes.KEY, fecha, estado);
        Cursor cursor = manager.buscarUsuario(usuario);

        String id_vendedor       = "";
        String nombre_vendedor   = "";
        String apellido_vendedor = "";
        String telefono_vendedor = "";
        String email_vendedor    = "";

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            id_vendedor = cursor.getString(1);
            nombre_vendedor = cursor.getString(2);
            apellido_vendedor = cursor.getString(3);
            telefono_vendedor = cursor.getString(5);
            email_vendedor = cursor.getString(6);
            //Log.d("CURSOR","ID: "+id_vendedor+" nombre: "+nombre_vendedor+" apellido: "+apellido_vendedor+" telefono: "+telefono_vendedor+" email: "+email_vendedor+" pass"+ cursor.getString(7)+" salt"+ cursor.getString(8) );
        }
        cursor.close();

        Intent c = new Intent(contexto, Home.class);
        c.putExtra("cedula", usuario);
        c.putExtra("id_vendedor", id_vendedor);
        c.putExtra("nombre_vendedor", nombre_vendedor);
        c.putExtra("apellido_vendedor", apellido_vendedor);
        c.putExtra("telefono_vendedor", telefono_vendedor);
        c.putExtra("email_vendedor", email_vendedor);
        contexto.startActivity(c);
    }

    /**
     * Realiza la descarga de las imagenes de las lineas
     *
     * @return "ok" si el proceso fue exitoso, un error en caso contrario.
     */
    private String realizar_descarga_lineas()
    {
        if (nombre_faltantes_lineas != null)
        {
            for (int i = 0; i < nombre_faltantes_lineas.size(); i++)
            {
                try
                {
                    ByteArrayBuffer baf = ImageManager.DownloadFromUrl(links_faltantes_lineas.get(i), "linea_" + nombre_faltantes_lineas.get(i));

                    try
                    {
                        FileOutputStream fos = contexto.openFileOutput(nombre_faltantes_lineas.get(i) + ".jpg", MODE_PRIVATE);
                        fos.write(baf.toByteArray());
                        fos.close();

                        manager.actualizar_actualizacionesLineas(ids_faltantes_lineas.get(i), fecha_local_faltantes_lineas.get(i));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                catch (RuntimeException e)
                {
                    return "realizar_descarga_lineas: " + e;
                }
                pDialog.incrementProgressBy(1);
            }
        }
        return "ok";
    }

    /**
     * Realiza la descarga de las imagenes de los modelos
     *
     * @return "ok" si el proceso fue exitoso, un error en caso contrario.
     */
    private String realizar_descarga_modelos()
    {
        if (nombre_faltantes_modelos != null)
        {
            for (int i = 0; i < nombre_faltantes_modelos.size(); i++)
            {
                try
                {
                    ByteArrayBuffer baf = ImageManager.DownloadFromUrl(links_faltantes_modelos.get(i), "modelo_" + nombre_faltantes_modelos.get(i));

                    try
                    {
                        FileOutputStream fos = contexto.openFileOutput(nombre_faltantes_modelos.get(i) + ".jpg", MODE_PRIVATE);
                        fos.write(baf.toByteArray());
                        fos.close();

                        manager.actualizar_actualizacionesModelos(ids_faltantes_modelos.get(i), fecha_local_faltantes_modelos.get(i));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                catch (RuntimeException e)
                {
                    return "realizar_descarga_modelos: " + e;
                }
                pDialog.incrementProgressBy(1);
            }
        }

        return "ok";
    }

    /**
     * Cambia el mensaje que esta en el dialogo de espera, por ejemplo: Si termino de descargar las
     * lineas y voy a seguir con los modelos, el mensaje cambiaria de "Descargando Lineas" a
     * "Descargando modelos".
     *
     * @param mensaje El mensaje con el cual se quiere cambiar.
     */
    private void cambiar_mensaje_dialogo(final String mensaje)
    {
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                synchronized (this)
                {
                    ((Activity) contexto).runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            pDialog.setMessage(mensaje);
                        }
                    });
                }
            }
        };
        thread.start();
    }

    /**
     * Clase para actualizar el muestrario de la aplicacion en segundo plano.
     */
    class asynclogin_actualizar_muestrario extends AsyncTask<String, String, String>
    {
        final int[] actualizacion;
        final int   contador_actualizacion;

        public asynclogin_actualizar_muestrario(int[] actualizar)
        {
            actualizacion = new int[actualizar.length];
            System.arraycopy(actualizar, 0, actualizacion, 0, actualizar.length); // Haciendo una copia local
            contador_actualizacion = contarActualizaciones(actualizacion);
            Log.d("contador_actualizacion", "" + contador_actualizacion);
        }

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Actualizando la aplicacion...");
            pDialog.setMessage("Cargando Datos...");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setProgress(0);
            pDialog.setMax(contador_actualizacion);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                //enviamos y recibimos y analizamos los datos en segundo plano.
                if (obtener_datos(actualizacion))
                    return "ok";
                else
                    return "err";
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
                return "" + e;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                return "" + e;
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (result.equals("ok"))
            {
                pDialog.dismiss();
                pDialog = new ProgressDialog(contexto);

                // Actualizo las imagenes de ser necesario.

                if (actualizacion[0] == 1 && actualizacion[1] == 1 && actualizacion[2] == 1)
                {
                    Log.d("Actualizacion", "Debo actualizar TODO (IMAGENES)");
                    new descargar_imagenes_lineas(0).execute();
                }

                else if (actualizacion[0] == 1 && actualizacion[1] == 1)
                {
                    Log.d("Actualizacion", "Debo actualizar las lineas y los Modelos (IMAGENES)");
                    new descargar_imagenes_lineas_modelos(0).execute();
                }

                else if (actualizacion[0] == 1 && actualizacion[2] == 1)
                {
                    Log.d("Actualizacion", "Debo actualizar las lineas y los Productos (IMAGENES)");
                    new descargar_imagenes_lineas_productos(0).execute();
                }

                else if (actualizacion[1] == 1 && actualizacion[2] == 1)
                {
                    Log.d("Actualizacion", "Debo actualizar los modelos y los Productos (IMAGENES)");
                    new descargar_imagenes_modelos(0).execute();
                }

                else if (actualizacion[0] == 1) //debo actualizar las imagenes de las lineas
                {
                    Log.d("Actualizacion", "Debo actualizar SOLO las lineas (IMAGENES)");
                    new descargar_imagenes_lineas_solo(0).execute();
                }

                else if (actualizacion[1] == 1) //debo actualizar las imagenes de los modelos
                {
                    Log.d("Actualizacion", "Debo actualizar SOLO los modelos (IMAGENES)");
                    new descargar_imagenes_modelos_solo(0).execute();
                }

                else if (actualizacion[2] == 1) //debo actualizar las imagenes de los productos
                {
                    Log.d("Actualizacion", "Debo actualizar SOLO los productos (IMAGENES)");
                    new descargar_imagenes_productos().execute();
                }
                else
                {
                    // No debo actualizar nada. Entro al Home directamente.
                    if (opc == 1) entrar_Home();
                }
            }
            else
            {
                pDialog.dismiss();
                Toast.makeText(contexto, "Ha ocurrido un error al consultar los datos.." + result, Toast.LENGTH_LONG).show();
                Log.d("ERROR", result);
            }
        }
    }

    /**
     * Clase para descargar en segundo plano las imagenes de las lineas faltantes o que necesitan
     * actualizarse.
     */
    @SuppressWarnings("SameParameterValue")
    class descargar_imagenes_lineas extends AsyncTask<String, String, String>
    {
        final int pos;

        public descargar_imagenes_lineas(int position)
        {
            pos = position;
        }

        @Override
        protected void onPreExecute()
        {
            pDialog.setTitle("Actualizando la aplicacion...");
            pDialog.setMessage("Descargando Imagenes de las Lineas..");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setProgress(pos);
            int numeros;

            if (nombre_faltantes_lineas == null)
                numeros = 0;
            else
                numeros = nombre_faltantes_lineas.size();

            pDialog.setMax(numeros);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            return realizar_descarga_lineas();
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            pDialog = new ProgressDialog(contexto);
            new descargar_imagenes_modelos(0).execute();
        }
    }

    /**
     * Clase para descargar en segundo plano SOLO las imagenes de las lineas faltantes o que
     * necesitan actualizarse.
     */
    @SuppressWarnings("SameParameterValue")
    class descargar_imagenes_lineas_solo extends AsyncTask<String, String, String>
    {
        final int pos;

        public descargar_imagenes_lineas_solo(int position)
        {
            pos = position;
        }

        @Override
        protected void onPreExecute()
        {
            pDialog.setTitle("Actualizando la aplicacion...");
            pDialog.setMessage("Descargando Imagenes de las Lineas..");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setProgress(pos);
            int numeros;

            if (nombre_faltantes_lineas == null)
                numeros = 0;
            else
                numeros = nombre_faltantes_lineas.size();

            pDialog.setMax(numeros);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            return realizar_descarga_lineas();
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            if (opc == 1)
                entrar_Home();
        }
    }

    /**
     * Clase para descargar en segundo plano las imagenes de las lineas y modelos faltantes
     * o que necesitan actualizarse.
     */
    @SuppressWarnings("SameParameterValue")
    class descargar_imagenes_lineas_modelos extends AsyncTask<String, String, String>
    {
        final int pos;

        public descargar_imagenes_lineas_modelos(int position)
        {
            pos = position;
        }

        @Override
        protected void onPreExecute()
        {
            pDialog.setTitle("Actualizando la aplicacion...");
            pDialog.setMessage("Descargando Imagenes de las Lineas..");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setProgress(pos);

            int numeros;

            if (nombre_faltantes_lineas == null)
                numeros = 0;
            else
                numeros = nombre_faltantes_lineas.size();

            pDialog.setMax(numeros);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            return realizar_descarga_lineas();
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            pDialog = new ProgressDialog(contexto);
            new descargar_imagenes_modelos_solo(0).execute();
        }
    }

    /**
     * Clase para descargar en segundo plano las imagenes de las lineas  y productos faltantes o que
     * necesitan actualizarse.
     */
    @SuppressWarnings("SameParameterValue")
    class descargar_imagenes_lineas_productos extends AsyncTask<String, String, String>
    {
        final int pos;

        public descargar_imagenes_lineas_productos(int position)
        {
            pos = position;
        }

        @Override
        protected void onPreExecute()
        {
            pDialog.setTitle("Actualizando la aplicacion...");
            pDialog.setMessage("Descargando Imagenes de las Lineas..");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setProgress(pos);

            int numeros;

            if (nombre_faltantes_lineas == null)
                numeros = 0;
            else
                numeros = nombre_faltantes_lineas.size();

            pDialog.setMax(numeros);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            return realizar_descarga_lineas();
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            pDialog = new ProgressDialog(contexto);
            new descargar_imagenes_productos().execute();
        }
    }

    /**
     * Clase para descargar en segundo plano las imagenes de los modelos faltantes o que necesitan
     * actualizarse.
     */
    @SuppressWarnings("SameParameterValue")
    class descargar_imagenes_modelos extends AsyncTask<String, String, String>
    {
        final int pos;

        public descargar_imagenes_modelos(int position)
        {
            pos = position;
        }

        @Override
        protected void onPreExecute()
        {
            pDialog.setTitle("Actualizando la aplicacion...");
            pDialog.setMessage("Descargando Imagenes de los Modelos..");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setProgress(pos);

            int numeros;

            if (nombre_faltantes_modelos == null)
                numeros = 0;
            else
                numeros = nombre_faltantes_modelos.size();

            pDialog.setMax(numeros);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            return realizar_descarga_modelos();
        }

        /*Una vez terminado doInBackground segun lo que halla ocurrido
        pasamos a la sig. activity
        o mostramos error*/
        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            pDialog = new ProgressDialog(contexto);
            pDialog.setProgress(0);
            new descargar_imagenes_productos().execute();
        }
    }

    /**
     * Clase para descargar en segundo plano solo las imagenes de los modelos faltantes o que
     * necesitan actualizarse.
     */
    @SuppressWarnings("SameParameterValue")
    class descargar_imagenes_modelos_solo extends AsyncTask<String, String, String>
    {
        final int pos;

        public descargar_imagenes_modelos_solo(int position)
        {
            pos = position;
        }

        @Override
        protected void onPreExecute()
        {
            pDialog.setTitle("Actualizando la aplicacion...");
            pDialog.setMessage("Descargando Imagenes de los Modelos..");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setProgress(pos);

            int numeros;

            if (nombre_faltantes_modelos == null)
                numeros = 0;
            else
                numeros = nombre_faltantes_modelos.size();

            pDialog.setMax(numeros);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            return realizar_descarga_modelos();
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            if (opc == 1)
                entrar_Home();
        }
    }

    /**
     * Clase para descargar en segundo plano las imagenes de los productos faltantes o que necesitan
     * actualizarse.
     */
    class descargar_imagenes_productos extends AsyncTask<String, String, String>
    {
        public descargar_imagenes_productos()
        {
        }

        @Override
        protected void onPreExecute()
        {
            pDialog.setTitle("Actualizando la aplicacion...");
            pDialog.setMessage("Descargando Imagenes de los Productos..");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            int numeros;

            if (nombre_faltantes_productos == null)
                numeros = 0;
            else
                numeros = nombre_faltantes_productos.size();

            pDialog.setMax(numeros);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String res = "ok";

            if (nombre_faltantes_productos != null)
            {
                //Log.d("Verificador 1","Iniciando");
                for (int i = 0; i < nombre_faltantes_productos.size(); i++)
                {
                    try
                    {
                        ByteArrayBuffer baf = ImageManager.DownloadFromUrl(links_faltantes_productos.get(i), "producto_" + nombre_faltantes_productos.get(i) + "_0" + numero_faltantes_productos.get(i));

                        try
                        {
                            int j;// = numero_faltantes_productos.get(i);
                            j = 1;
                            FileOutputStream fos = contexto.openFileOutput(nombre_faltantes_productos.get(i) + "_0" + j + ".jpg", MODE_PRIVATE);
                            fos.write(baf.toByteArray());
                            fos.close();

                            Log.e("Saving File", nombre_faltantes_productos.get(i) + "_0" + j + ".jpg");

                            Log.d("Actualizando fecha", "id: " + ids_faltantes_productos.get(i) + ", fecha externa: " + fecha_local_faltantes_productos.get(i) + ", imagen_numero: " + numero_faltantes_productos.get(i));
                            manager.actualizar_actualizacionesProductos(ids_faltantes_productos.get(i), fecha_local_faltantes_productos.get(i), numero_faltantes_productos.get(i));
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    catch (RuntimeException e)
                    {
                        Log.d("ERROR", "" + e);
                        return "err2";
                    }
                    pDialog.incrementProgressBy(1);
                }

                //Log.d("Verificador 1","Finalizado");
            }

            return res;
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            if (opc == 1)
                entrar_Home();
            else
                FragmentOpciones.actualizacion_terminada();
        }
    }
}