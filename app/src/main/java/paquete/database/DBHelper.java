package paquete.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import static paquete.database.creates.CreateSentences.CREAR_TABLA_ACTUALIZACIONES_CLIENTES;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_ACTUALIZACIONES_FUNCIONES;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_ACTUALIZACIONES_GENERALES;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_ACTUALIZACIONES_LINEAS;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_ACTUALIZACIONES_MODELOS;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_ACTUALIZACIONES_PEDIDOS;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_ACTUALIZACIONES_PRODUCTOS;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_BULTOS;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_CLIENTES;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_COLORES;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_COLORES_BASE;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_FUNCIONES;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_LINEA;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_MATERIALES;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_MODELO;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_PEDIDOS;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_PEDIDOS_DETALLES;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_PEDIDOS_LOCALES;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_PEDIDOS_LOCALES_DETALLES;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_PRIMER_LOGIN;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_PRODUCTO;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_PRODUCTOS_PEDIDOS;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_PRODUCTOS_PEDIDOS_EDITAR;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_PRODUCTO_SELECCIONADO;
import static paquete.database.creates.CreateSentences.CREAR_TABLA_USUARIO;
import static paquete.database.inserts.DefaultInserts.INSERTAR_TABLA_ACTUALIZACIONES_GENERALES;
import static paquete.database.inserts.DefaultInserts.INSERTAR_TABLA_PRIMER_LOGIN;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME    = "tufano_BD.db";
    private final static int DB_VERSION = 16;
    private static final String TAG = "DBHelper";
    //Ruta por defecto de las bases de datos en el sistema Android
    //private static String DB_PATH =  "/data/data/paquete.tufanoapp/databases/";
    public static String DB_RUTA;
    private static DBHelper mInstance = null;
    private final Context        myContext;
    private       SQLiteDatabase myDataBase;

    /**
     * Constructor
     * Toma referencia hacia el contexto de la aplicación que lo invoca para poder acceder a los
     * 'assets' y 'resources' de la aplicación.
     * <p/>
     * Crea un objeto DBOpenHelper que nos permitirá controlar la apertura de la base de datos.
     * <p/>
     * Constructor should be private to prevent direct instantiation. Make call to static factory
     * method "getInstance()" instead.
     *
     * @param context Contexto de la activity
     */
    private DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        Log.w("DBHelper", "(" + context.getFilesDir().getPath() + ")  DB_RUTA:  (" + context.getDatabasePath(DB_NAME));
        DB_RUTA = context.getDatabasePath(DB_NAME).toString();
    }

    public static DBHelper getInstance(Context ctx)
    {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null)
        {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public static void backUpDB(Context contexto)
    {
        try
        {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            {
                Log.i(TAG, "backUpDB initiation..");

                File output_folder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
                {
                    output_folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/TabbucheMovilFiles/backup");
                }
                else
                {
                    output_folder = new File(Environment.getExternalStorageDirectory() + "/dcim/" + "TabbucheMovilFiles/backup");
                }
                if (!output_folder.exists())
                {
                    if (output_folder.mkdirs())
                        Log.i(TAG, "Carpeta \"" + output_folder.getPath() + "\" creada exitosamente");
                    else
                    {
                        Log.e(TAG, "La carpeta no pudo ser creada..");
                    }
                }

                File dbFile = new File(DB_RUTA);
                FileInputStream fis = new FileInputStream(dbFile);

                String outFileName = output_folder.getPath() + "/db_copy.db";
                //Log.i(TAG, "1) backUpDB will be done in the following the directory: " + outFileName);
                //Log.i(TAG, "2) backUpDB will be done in the following the directory: " + Environment.getExternalStorageDirectory() + "/db_copy.db");

                // Open the empty db as the output stream
                OutputStream output = new FileOutputStream(outFileName);

                // Transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;

                while ((length = fis.read(buffer)) > 0)
                {
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                fis.close();
                Toast.makeText(contexto, "Respaldo de la base de datos completado de manera exitosa!", Toast.LENGTH_LONG).show();
                Log.i(TAG, "backUpDB finished in following the directory: " + outFileName
                        + ", origin: " + DB_RUTA);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Crea una base de datos vacía en el sistema y la reescribe con nuestro fichero de base de datos.
     */
    private void createDataBase()
    {
        boolean dbExist = checkDataBase();
        Log.i("DBHelper", "createDataBase!");

        if (dbExist)
        {
            //la base de datos existe y no hacemos nada.
            Log.i("DBHelper", "dbExist!");
        }
        else
        {
            //Llamando a este método se crea la base de datos vacía en la ruta por defecto del sistema
            //de nuestra aplicación por lo que podremos sobreescribirla con nuestra base de datos.
            this.getReadableDatabase();
            Log.i("DBHelper", "!dbExist!");

            try
            {
                copyDataBase();
            }
            catch (IOException e)
            {
                throw new Error("Error copiando Base de Datos");
            }
        }
    }

    /**
     * Comprueba si la base de datos existe para evitar copiar siempre el fichero cada vez que se abra la aplicación.
     *
     * @return true si existe, false si no existe
     */
    private boolean checkDataBase()
    {
        Log.i("checkDataBase", "INTRO");
        SQLiteDatabase checkDB = null;

        try
        {
            //String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(DB_RUTA, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e)
        {
            //si llegamos aqui es porque la base de datos no existe todavía.
        }
        if (checkDB != null)
        {
            checkDB.close();
        }
        return checkDB != null;
    }

    /**
     * Copia nuestra base de datos desde la carpeta assets a la recién creada
     * base de datos en la carpeta de sistema, desde dónde podremos acceder a ella.
     * Esto se hace con bytestream.
     */
    private void copyDataBase() throws IOException
    {
        Log.i("copyDataBase", "INTRO");
        //Abrimos el fichero de base de datos como entrada
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        //Ruta a la base de datos vacía recién creada
        //String outFileName = DB_PATH + DB_NAME;

        //Abrimos la base de datos vacía como salida
        OutputStream myOutput = new FileOutputStream(DB_RUTA);

        //Transferimos los bytes desde el fichero de entrada al de salida
        byte[] buffer = new byte[1024];
        int    length;

        while ((length = myInput.read(buffer)) > 0)
        {
            myOutput.write(buffer, 0, length);
        }

        //Liberamos los streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @SuppressWarnings("unused")
    public void open()
    {
        //Abre la base de datos
        createDataBase();
        //String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(DB_RUTA, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close()
    {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Crear la base de datos
        //db.execSQL("PRAGMA encoding = \"UTF-8\"");
        db.execSQL(CREAR_TABLA_USUARIO);
        db.execSQL(CREAR_TABLA_LINEA);
        db.execSQL(CREAR_TABLA_MODELO);
        db.execSQL(CREAR_TABLA_PRODUCTO);
        //db.execSQL(CREAR_TABLA_IMAGENES_PRODUCTOS);
        db.execSQL(CREAR_TABLA_ACTUALIZACIONES_GENERALES);
        db.execSQL(INSERTAR_TABLA_ACTUALIZACIONES_GENERALES);
        db.execSQL(CREAR_TABLA_ACTUALIZACIONES_LINEAS);
        db.execSQL(CREAR_TABLA_ACTUALIZACIONES_MODELOS);
        db.execSQL(CREAR_TABLA_ACTUALIZACIONES_PRODUCTOS);
        db.execSQL(CREAR_TABLA_PRODUCTOS_PEDIDOS);
        db.execSQL(CREAR_TABLA_CLIENTES);
        db.execSQL(CREAR_TABLA_BULTOS);
        db.execSQL(CREAR_TABLA_MATERIALES);
        db.execSQL(CREAR_TABLA_COLORES);
        db.execSQL(CREAR_TABLA_PRODUCTO_SELECCIONADO);
        db.execSQL(CREAR_TABLA_PRIMER_LOGIN);
        db.execSQL(INSERTAR_TABLA_PRIMER_LOGIN);
        db.execSQL(CREAR_TABLA_PEDIDOS);
        db.execSQL(CREAR_TABLA_PEDIDOS_DETALLES);
        db.execSQL(CREAR_TABLA_PRODUCTOS_PEDIDOS_EDITAR);
        // DB Version 3 (Hacia Arriba)
        // DB Version 4 (Hacia Arriba): Añadido campo codigo_pedido en Tabla Pedido
        // DB Version 5 (Hacia Arriba): Cambiado campos de BD de TXT a INTEGER
        db.execSQL(CREAR_TABLA_PEDIDOS_LOCALES);
        db.execSQL(CREAR_TABLA_PEDIDOS_LOCALES_DETALLES);
        // DB Version 6 : Añadidas las tablas de pedidos locales
        // DB Version 7 : Modificadas las tablas de pedidos locales
        db.execSQL(CREAR_TABLA_ACTUALIZACIONES_PEDIDOS);
        // DB Version 8: Añadida la tabla actualizacions pedidos y editada la tabla actualizaciones generales
        db.execSQL(CREAR_TABLA_ACTUALIZACIONES_CLIENTES);
        // DB Version 9: Añadida la tabla actualizacions clientes y editada la tabla actualizaciones generales,
        // borrada TABLA IMAGENES PRODUCTOS,
        // DB Version 10: Agregado el campo CN_ESTADO en la tabla usuarios.

        //------------------------------------------------------------------------------------------
        // DB Version 11: Agregada la tabla Actualizaciones Funciones..
        db.execSQL(CREAR_TABLA_ACTUALIZACIONES_FUNCIONES);
        db.execSQL(CREAR_TABLA_FUNCIONES);
        db.execSQL(CREAR_TABLA_COLORES_BASE);

        //------------------------------------------------------------------------------------------
        // DB Version 12: Editados los nombres de las columnas de la tabla funciones, para hacerlas
        // coincidir con el array de strings de los nombres de los menus..

        //------------------------------------------------------------------------------------------
        // DB Version 13: Agregada la columna ID_COLOR en la tabla PRODUCTOS

        //------------------------------------------------------------------------------------------
        // DB Version 14: Editada la columna ID_COLOR en la tabla PRODUCTOS a STRING

        //------------------------------------------------------------------------------------------
        // DB Version 15: Agregada la columna CN_DESTACADO_PRODUCTO en la tabla PRODUCTOS

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Actualizar la base de datos (Tomando en cuenta la version)
        Log.i("DBHelper", "onUpgrade!");

        myContext.deleteDatabase(DB_NAME); // Limpiar la BD
        onCreate(DBAdapter.db);
    }
}