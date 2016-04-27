package paquete.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static paquete.database.creates.CreateSentences.*;
import static paquete.database.inserts.DefaultInserts.*;

public class DBHelper extends SQLiteOpenHelper
{
    //Ruta por defecto de las bases de datos en el sistema Android
    //private static String DB_PATH =  "/data/data/paquete.tufanoapp/databases/";
    private static String DB_RUTA;
    private static final String DB_NAME = "tufano_BD.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private static DBHelper mInstance = null;
    private final static int DB_VERSION = 14;

    /**
     * Constructor
     * Toma referencia hacia el contexto de la aplicación que lo invoca para poder acceder a los
     * 'assets' y 'resources' de la aplicación.
     *
     * Crea un objeto DBOpenHelper que nos permitirá controlar la apertura de la base de datos.
     *
     * Constructor should be private to prevent direct instantiation. Make call to static factory
     * method "getInstance()" instead.
     *
     * @param context Contexto de la activity
     *
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

        //------------------------------------------------------------------------------------------
        // DB Version 12: Editados los nombres de las columnas de la tabla funciones, para hacerlas
        // coincidir con el array de strings de los nombres de los menus..

        //------------------------------------------------------------------------------------------
        // DB Version 13: Agregada la columna ID_COLOR en la tabla PRODUCTOS

        //------------------------------------------------------------------------------------------
        // DB Version 14: Editada la columna ID_COLOR en la tabla PRODUCTOS a STRING
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Actualizar la base de datos (Tomando en cuenta la version)
        Log.i("DBHelper", "onUpgrade!");

        myContext.deleteDatabase(DB_NAME); // Limpiar la BD
        onCreate(DBAdapter.db);
    }

    /**
     * Crea una base de datos vacía en el sistema y la reescribe con nuestro fichero de base de datos.
     */
    private void createDataBase() {
        boolean dbExist = checkDataBase();
        Log.i("DBHelper", "createDataBase!");

        if (dbExist) {
            //la base de datos existe y no hacemos nada.
            Log.i("DBHelper", "dbExist!");
        }
        else {
            //Llamando a este método se crea la base de datos vacía en la ruta por defecto del sistema
            //de nuestra aplicación por lo que podremos sobreescribirla con nuestra base de datos.
            this.getReadableDatabase();
            Log.i("DBHelper", "!dbExist!");

            try {
                copyDataBase();
            }
            catch (IOException e) {
                throw new Error("Error copiando Base de Datos");
            }
        }
    }

    /**
     * Comprueba si la base de datos existe para evitar copiar siempre el fichero cada vez que se abra la aplicación.
     *
     * @return true si existe, false si no existe
     */
    private boolean checkDataBase() {
        Log.i("checkDataBase", "INTRO");
        SQLiteDatabase checkDB = null;

        try {
            //String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(DB_RUTA, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e) {
            //si llegamos aqui es porque la base de datos no existe todavía.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    /**
     * Copia nuestra base de datos desde la carpeta assets a la recién creada
     * base de datos en la carpeta de sistema, desde dónde podremos acceder a ella.
     * Esto se hace con bytestream.
     */
    private void copyDataBase() throws IOException {
        Log.i("copyDataBase", "INTRO");
        //Abrimos el fichero de base de datos como entrada
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        //Ruta a la base de datos vacía recién creada
        //String outFileName = DB_PATH + DB_NAME;

        //Abrimos la base de datos vacía como salida
        OutputStream myOutput = new FileOutputStream(DB_RUTA);

        //Transferimos los bytes desde el fichero de entrada al de salida
        byte[] buffer = new byte[1024];
        int length;

        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Liberamos los streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @SuppressWarnings("unused")
    public void open() {
        //Abre la base de datos
        createDataBase();
        //String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(DB_RUTA, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }
}