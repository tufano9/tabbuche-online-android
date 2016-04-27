package paquete.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

import paquete.global.Constantes;


import static paquete.database.tables.ActualizacionesClientes.CN_FECHA_ACTUALIZACION_CLIENTES;
import static paquete.database.tables.ActualizacionesClientes.CN_ID_CLIENTES_ACTUALIZACION;
import static paquete.database.tables.ActualizacionesClientes.TABLA_ACTUALIZACIONES_CLIENTES;
import static paquete.database.tables.ActualizacionesFunciones.CN_FECHA_ACTUALIZACION_FUNCIONES;
import static paquete.database.tables.ActualizacionesFunciones.CN_ID_FUNCIONES_ACTUALIZACION;
import static paquete.database.tables.ActualizacionesFunciones.TABLA_ACTUALIZACIONES_FUNCIONES;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_BULTOS;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_COLORES;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_LINEAS;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_MATERIALES;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_MODELOS;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_PRODUCTOS;
import static paquete.database.tables.ActualizacionesGenerales.TABLA_ACTUALIZACIONES_GENERALES;
import static paquete.database.tables.ActualizacionesLineas.CN_FECHA_ACTUALIZACION_LINEAS;
import static paquete.database.tables.ActualizacionesLineas.CN_ID_ACTUALIZACION_LINEAS;
import static paquete.database.tables.ActualizacionesLineas.CN_ID_LINEA_ACTUALIZACION_LINEAS;
import static paquete.database.tables.ActualizacionesLineas.TABLA_ACTUALIZACIONES_LINEAS;
import static paquete.database.tables.ActualizacionesModelos.CN_FECHA_ACTUALIZACION_MODELOS;
import static paquete.database.tables.ActualizacionesModelos.CN_ID_ACTUALIZACION_MODELOS;
import static paquete.database.tables.ActualizacionesModelos.CN_ID_MODELO_ACTUALIZACION_MODELOS;
import static paquete.database.tables.ActualizacionesModelos.TABLA_ACTUALIZACIONES_MODELOS;
import static paquete.database.tables.ActualizacionesPedidos.CN_FECHA_ACTUALIZACION_PEDIDOS;
import static paquete.database.tables.ActualizacionesPedidos.CN_ID_PEDIDOS_ACTUALIZACION;
import static paquete.database.tables.ActualizacionesPedidos.TABLA_ACTUALIZACIONES_PEDIDOS;
import static paquete.database.tables.ActualizacionesProductos.CN_ACTUALIZACION_IMAGEN_NUMERO;
import static paquete.database.tables.ActualizacionesProductos.CN_FECHA_ACTUALIZACION_PRODUCTO;
import static paquete.database.tables.ActualizacionesProductos.CN_ID_ACTUALIZACION_PRODUCTO;
import static paquete.database.tables.ActualizacionesProductos.TABLA_ACTUALIZACIONES_PRODUCTOS;
import static paquete.database.tables.Bultos.CN_ID_BULTO;
import static paquete.database.tables.Bultos.CN_ID_BULTO_BASE;
import static paquete.database.tables.Bultos.CN_NOMBRE_BULTO;
import static paquete.database.tables.Bultos.CN_NUMERACION_BULTO;
import static paquete.database.tables.Bultos.CN_NUMERO_PARES_BULTO;
import static paquete.database.tables.Bultos.CN_RANGO_TALLAS_BULTO;
import static paquete.database.tables.Bultos.CN_TIPO_BULTO;
import static paquete.database.tables.Bultos.TABLA_BULTOS;
import static paquete.database.tables.Clientes.CN_ID_CLIENTES;
import static paquete.database.tables.Clientes.CN_ID_DIRECCION;
import static paquete.database.tables.Clientes.CN_ID_EMAIL;
import static paquete.database.tables.Clientes.CN_ID_ESTADO;
import static paquete.database.tables.Clientes.CN_ID_FECHA_INGRESO;
import static paquete.database.tables.Clientes.CN_ID_RAZON_SOCIAL;
import static paquete.database.tables.Clientes.CN_ID_RIF;
import static paquete.database.tables.Clientes.CN_ID_TABLA_CLIENTES;
import static paquete.database.tables.Clientes.CN_ID_TELEFONO;
import static paquete.database.tables.Clientes.CN_ID_VENDEDOR;
import static paquete.database.tables.Clientes.TABLA_CLIENTES;
import static paquete.database.tables.Colores.CN_ID_COLOR;
import static paquete.database.tables.Colores.CN_ID_COLOR_BASE;
import static paquete.database.tables.Colores.CN_NOMBRE_COLOR;
import static paquete.database.tables.Colores.TABLA_COLORES;
import static paquete.database.tables.Funciones.CN_CLIENTES;
import static paquete.database.tables.Funciones.CN_ESTADO_CUENTA;
import static paquete.database.tables.Funciones.CN_HOMES;
import static paquete.database.tables.Funciones.CN_ID_TABLA_FUNCIONES;
import static paquete.database.tables.Funciones.CN_ID_USUARIO_FUNCIONES;
import static paquete.database.tables.Funciones.CN_MUESTRARIO;
import static paquete.database.tables.Funciones.CN_OPCIONES;
import static paquete.database.tables.Funciones.CN_PEDIDOS;
import static paquete.database.tables.Funciones.CN_PERFIL;
import static paquete.database.tables.Funciones.TABLA_FUNCIONES;
import static paquete.database.tables.Lineas.CN_ID_LINEA;
import static paquete.database.tables.Lineas.CN_ID_LINEA_BASE;
import static paquete.database.tables.Lineas.CN_NOMBRE_LINEA;
import static paquete.database.tables.Lineas.CN_TALLA_LINEA;
import static paquete.database.tables.Lineas.TABLA_LINEAS;
import static paquete.database.tables.Materiales.CN_ID_COLOR_BASE_MATERIAL;
import static paquete.database.tables.Materiales.CN_ID_MATERIAL;
import static paquete.database.tables.Materiales.CN_ID_MATERIAL_BASE;
import static paquete.database.tables.Materiales.CN_ID_MATERIAL_COLOR;
import static paquete.database.tables.Materiales.CN_NOMBRE_MATERIAL;
import static paquete.database.tables.Materiales.CN_PREFIJO_MATERIAL;
import static paquete.database.tables.Materiales.TABLA_MATERIALES;
import static paquete.database.tables.Modelos.CN_ID_LINEA_MODELO;
import static paquete.database.tables.Modelos.CN_ID_MODELO;
import static paquete.database.tables.Modelos.CN_ID_MODELO_BASE;
import static paquete.database.tables.Modelos.CN_NOMBRE_MODELO;
import static paquete.database.tables.Modelos.TABLA_MODELOS;
import static paquete.database.tables.Pedidos.CN_CODIGO_PEDIDOS;
import static paquete.database.tables.Pedidos.CN_ESTATUS_PEDIDO;
import static paquete.database.tables.Pedidos.CN_FECHA_PEDIDOS;
import static paquete.database.tables.Pedidos.CN_ID_CLIENTE_PEDIDOS;
import static paquete.database.tables.Pedidos.CN_ID_PEDIDOS_BD;
import static paquete.database.tables.Pedidos.CN_ID_PEDIDOS_REAL;
import static paquete.database.tables.Pedidos.CN_ID_VENDEDOR_PEDIDOS;
import static paquete.database.tables.Pedidos.CN_MONTO_PEDIDOS;
import static paquete.database.tables.Pedidos.CN_OBSERVACIONES;
import static paquete.database.tables.Pedidos.TABLA_PEDIDOS;
import static paquete.database.tables.PedidosDetalles.CN_ID_PEDIDOS_DETALLES_BD;
import static paquete.database.tables.PedidosDetalles.CN_ID_PEDIDOS_DETALLES_BULTOS;
import static paquete.database.tables.PedidosDetalles.CN_ID_PEDIDOS_DETALLES_NUMERACION;
import static paquete.database.tables.PedidosDetalles.CN_ID_PEDIDOS_DETALLES_PARES;
import static paquete.database.tables.PedidosDetalles.CN_ID_PEDIDOS_DETALLES_PEDIDO;
import static paquete.database.tables.PedidosDetalles.CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO;
import static paquete.database.tables.PedidosDetalles.CN_ID_PEDIDOS_DETALLES_PRODUCTO;
import static paquete.database.tables.PedidosDetalles.CN_ID_PEDIDOS_DETALLES_SUBTOTAL;
import static paquete.database.tables.PedidosDetalles.CN_ID_PEDIDOS_DETALLES_TALLA;
import static paquete.database.tables.PedidosDetalles.TABLA_PEDIDOS_DETALLES;
import static paquete.database.tables.PedidosLocales.CN_CODIGO_PEDIDOS_LOCALES;
import static paquete.database.tables.PedidosLocales.CN_ESTATUS_PEDIDO_LOCALES;
import static paquete.database.tables.PedidosLocales.CN_FECHA_PEDIDOS_LOCALES;
import static paquete.database.tables.PedidosLocales.CN_ID_CLIENTE_PEDIDOS_LOCALES;
import static paquete.database.tables.PedidosLocales.CN_ID_PEDIDOS_LOCALES_BD;
import static paquete.database.tables.PedidosLocales.CN_ID_VENDEDOR_PEDIDOS_LOCALES;
import static paquete.database.tables.PedidosLocales.CN_MONTO_PEDIDOS_LOCALES;
import static paquete.database.tables.PedidosLocales.CN_OBSERVACIONES_LOCALES;
import static paquete.database.tables.PedidosLocales.TABLA_PEDIDOS_LOCALES;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_BULTOS;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_NUMERACION;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_PARES;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_PRECIO_UNITARIO;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_PRODUCTO;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_REAL;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_SUBTOTAL;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_TALLA;
import static paquete.database.tables.PedidosLocalesDetalles.TABLA_PEDIDOS_LOCALES_DETALLES;
import static paquete.database.tables.ProductoSeleccionado.CN_SELECCIONADO;
import static paquete.database.tables.ProductoSeleccionado.TABLA_PRODUCTO_SELECCIONADO;
import static paquete.database.tables.Productos.CN_CODIGO_FABRICANTE_PRODUCTO;
import static paquete.database.tables.Productos.CN_ID_PRODUCTO;
import static paquete.database.tables.Productos.CN_ID_PRODUCTO_BASE;
import static paquete.database.tables.Productos.CN_ID_PRODUCTO_BULTO;
import static paquete.database.tables.Productos.CN_ID_PRODUCTO_COLOR;
import static paquete.database.tables.Productos.CN_ID_PRODUCTO_LINEA;
import static paquete.database.tables.Productos.CN_ID_PRODUCTO_MATERIAL;
import static paquete.database.tables.Productos.CN_ID_PRODUCTO_MODELO;
import static paquete.database.tables.Productos.CN_NOMBRE_PRODUCTO;
import static paquete.database.tables.Productos.CN_PRECIO_PRODUCTO;
import static paquete.database.tables.Productos.CN_TALLA_PRODUCTO;
import static paquete.database.tables.Productos.TABLA_PRODUCTOS;
import static paquete.database.tables.ProductosPedidos.CN_BULTOS_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_ID_PRODUCTO_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_NOMBRE_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_NUMERACION_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_PARES_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_PRECIO_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_TIPO_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.TABLA_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidosEditar.CN_BULTOS_PRODUCTOS_PEDIDOS_EDITAR;
import static paquete.database.tables.ProductosPedidosEditar.CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS_EDITAR;
import static paquete.database.tables.ProductosPedidosEditar.CN_ID_PRODUCTO_PEDIDOS_EDITAR;
import static paquete.database.tables.ProductosPedidosEditar.CN_NOMBRE_PRODUCTOS_PEDIDOS_EDITAR;
import static paquete.database.tables.ProductosPedidosEditar.CN_NUMERACION_PRODUCTOS_PEDIDOS_EDITAR;
import static paquete.database.tables.ProductosPedidosEditar.CN_PARES_PRODUCTOS_PEDIDOS_EDITAR;
import static paquete.database.tables.ProductosPedidosEditar.CN_PRECIO_PRODUCTOS_PEDIDOS_EDITAR;
import static paquete.database.tables.ProductosPedidosEditar.CN_TIPO_PRODUCTOS_PEDIDOS_EDITAR;
import static paquete.database.tables.ProductosPedidosEditar.TABLA_PRODUCTOS_PEDIDOS_EDITAR;
import static paquete.database.tables.Usuarios.CN_APELLIDO;
import static paquete.database.tables.Usuarios.CN_CEDULA;
import static paquete.database.tables.Usuarios.CN_CODIGO_USUARIO;
import static paquete.database.tables.Usuarios.CN_EMAIL;
import static paquete.database.tables.Usuarios.CN_ESTADO;
import static paquete.database.tables.Usuarios.CN_ID_USUARIO;
import static paquete.database.tables.Usuarios.CN_LINEAS_DESHABILITADAS;
import static paquete.database.tables.Usuarios.CN_MODELOS_DESHABILITADOS;
import static paquete.database.tables.Usuarios.CN_NOMBRE;
import static paquete.database.tables.Usuarios.CN_PASSWORD;
import static paquete.database.tables.Usuarios.CN_PRODUCTOS_DESHABILITADOS;
import static paquete.database.tables.Usuarios.CN_TELEFONO;
import static paquete.database.tables.Usuarios.CN_USUARIO_FECHA_ACTUALIZACION;
import static paquete.database.tables.Usuarios.CN_USUARIO_SALT;
import static paquete.database.tables.Usuarios.TABLA_USUARIO;

@SuppressWarnings("SameParameterValue")
public class DBAdapter
{
    private final DBHelper BD;
    public static SQLiteDatabase db = null;

    /**
     * Constructor de laclase.
     * @param context Contexto de la activity.
     */
    public DBAdapter(Context context)
    {
        BD = DBHelper.getInstance(context);
        db = BD.getWritableDatabase();
    }

    public void cerrar()
    {
        db.close();
        BD.close();
    }

    private ContentValues generarContentValues_insertarUsuario(String codigo_usuario, String cedula, String nombre, String apellido, String telefono, String email, String lineas_desh, String modelos_desh, String productos_desh, String pass, String salt, String fecha, String estado)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_CODIGO_USUARIO,codigo_usuario);
        valores.put(CN_CEDULA,cedula);
        valores.put(CN_NOMBRE,nombre);
        valores.put(CN_APELLIDO,apellido);
        valores.put(CN_TELEFONO,telefono);
        valores.put(CN_EMAIL,email);
        valores.put(CN_LINEAS_DESHABILITADAS,lineas_desh);
        valores.put(CN_MODELOS_DESHABILITADOS,modelos_desh);
        valores.put(CN_PRODUCTOS_DESHABILITADOS,productos_desh);
        valores.put(CN_PASSWORD,pass);
        valores.put(CN_USUARIO_SALT,salt);
        valores.put(CN_USUARIO_FECHA_ACTUALIZACION,fecha);
        valores.put(CN_ESTADO,estado);

        return valores;
    }

    public void insertar_cliente(String id_cliente, String id_vendedor, String razon_social, String rif, String direccion, String telefono, String estado, String email, String fecha_ingreso)
    {
        Long res = db.insert(TABLA_CLIENTES, null, generarContentValues_insertarCliente(id_cliente, id_vendedor, razon_social, rif, direccion, telefono, estado, email, fecha_ingreso)); // Si devuelve -1 ha habido un problema..
        if (res==-1)
            Log.e("TABLA_CLIENTES","Ha ocurrido un error insertando el cliente (Campos Duplicados)");
        else
            actualizar_actualizacionesCliente(id_vendedor, fecha_ingreso);
    }

    public void insertar_clientes(JSONArray id_cliente, JSONArray id_vendedor, JSONArray razon_social, JSONArray rif, JSONArray direccion, JSONArray telefono, JSONArray estado, JSONArray email, JSONArray fecha_ingreso) throws JSONException
    {
        String sql = "INSERT INTO "+TABLA_CLIENTES +" ("+CN_ID_CLIENTES +", "+CN_ID_VENDEDOR +", "+CN_ID_RAZON_SOCIAL +", "+CN_ID_RIF +", "+CN_ID_DIRECCION  +", "+CN_ID_TELEFONO  +", "+CN_ID_ESTADO  +", "+CN_ID_EMAIL+", "+CN_ID_FECHA_INGRESO+") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.beginTransaction();
        Log.d("INSERTANDO CLIENTES", "BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < id_cliente.length(); i++)
        {
            stmt.bindString(1, id_cliente.getString(i));
            stmt.bindString(2, id_vendedor.getString(i));
            stmt.bindString(3, razon_social.getString(i));
            stmt.bindString(4, rif.getString(i));
            stmt.bindString(5, direccion.getString(i));
            stmt.bindString(6, telefono.getString(i));
            stmt.bindString(7, estado.getString(i));
            stmt.bindString(8, email.getString(i));
            stmt.bindString(9, fecha_ingreso.getString(i));
            stmt.executeInsert();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful(); // Si no le pongo esto hara un rollback..
        db.endTransaction();
        Log.d("INSERTANDO CLIENTES", "END");
    }

    private ContentValues generarContentValues_insertarCliente(String id_cliente, String id_vendedor, String razon_social, String rif, String direccion, String telefono, String estado, String email, String fecha_ingreso)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_ID_CLIENTES, id_cliente);
        valores.put(CN_ID_VENDEDOR, id_vendedor);
        valores.put(CN_ID_RAZON_SOCIAL,razon_social);
        valores.put(CN_ID_RIF,rif);
        valores.put(CN_ID_DIRECCION,direccion);
        valores.put(CN_ID_TELEFONO,telefono);
        valores.put(CN_ID_ESTADO, estado);
        valores.put(CN_ID_EMAIL, email);
        valores.put(CN_ID_FECHA_INGRESO, fecha_ingreso);
        return valores;
    }

    public void insertar_producto_pedido(String valoresProductoNombre, String precio, String pares, String numeracion, String id_producto, String nombre_real)
    {
        ContentValues valores = generarContentValues_insertarProducto_Pedido(valoresProductoNombre, precio, pares, numeracion, id_producto, nombre_real);
        Long res = db.insert(TABLA_PRODUCTOS_PEDIDOS, null, valores);
        // Si devuelve -1 ha habido un problema..
        if (res==-1)
        {
            Log.d("DBAdapter","Ha ocurrido un error insertando el producto al pedido (Campos Duplicados)");
        }
    }

    public void eliminar_producto_pedido(String id)
    {
        int res = db.delete(TABLA_PRODUCTOS_PEDIDOS, CN_ID_PRODUCTO_PEDIDOS + "=?", new String[]{id});
        Log.w("eliminar_producto_pedid", "Se elimino " + res + " producto del pedido.");
        //db.execSQL("DELETE FROM "+ TABLA_PRODUCTOS_PEDIDOS +" WHERE "+CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS+" = '"+nombre+"' ");
    }

    public void eliminar_producto_pedidoByID(String id)
    {
        int res = db.delete(TABLA_PRODUCTOS_PEDIDOS, CN_ID_PRODUCTO_PEDIDOS + "=?", new String[]{id});
        Log.w("eliminar_producto_pedid", "Se elimino " + res + " producto del pedido.");
    }

    private ContentValues generarContentValues_insertarProducto_Pedido(String valoresProductoNombre, String precio, String pares, String numeracion, String id, String nombre_real)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS,valoresProductoNombre);
        valores.put(CN_NOMBRE_PRODUCTOS_PEDIDOS, nombre_real);
        valores.put(CN_TIPO_PRODUCTOS_PEDIDOS, nombre_real.substring(nombre_real.length() - 1)); // P, M, G
        valores.put(CN_PRECIO_PRODUCTOS_PEDIDOS ,precio);
        valores.put(CN_NUMERACION_PRODUCTOS_PEDIDOS ,numeracion);
        valores.put(CN_PARES_PRODUCTOS_PEDIDOS, pares);
        valores.put(CN_ID_PRODUCTO_PEDIDOS, id);
        valores.put(CN_BULTOS_PRODUCTOS_PEDIDOS, 1);

        return valores;
    }

    public void insertar_usuario(String codigo_usuario, String cedula, String nombre, String apellido, String telefono, String email, String lineas_desh, String modelos_desh, String productos_desh, String pass, String salt, String fecha, String estado)
    {
        Cursor c = cargarCursorUsuario();
        ArrayList<String> columnArray1 = new ArrayList<>(); // codigo_usuario

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            columnArray1.add(c.getString(1)); // codigo_usuario
        String[] ids = columnArray1.toArray(new String[columnArray1.size()]);

        //nullColumnHack = indicar que parametros seran opcionales (CN_Phone es opcional) de lo contrario se pone null
        if(existe_dato(ids, codigo_usuario))
        {
            Log.i("Usuario insertado", "codigo_usuario: " + codigo_usuario + " cedula: " + cedula + " nombre: " + nombre + " apellido: " + apellido + " telefono: " + telefono + " email: " + email + " pass: " + pass + " salt: " + salt);
            db.insert(TABLA_USUARIO, null, generarContentValues_insertarUsuario(codigo_usuario,cedula,nombre,apellido,telefono,email,lineas_desh,modelos_desh,productos_desh,pass,salt,fecha,estado)); // Si devuelve -1 ha habido un problema..
        }
        else
        {
            Log.w("insertar_usuario", "El usuario ya existe, no se insertara en BD Local!");
        }
    }

    public void insertar_lineas(JSONArray ids_lineas, JSONArray nombres_lineas , JSONArray split, JSONArray tallas) throws JSONException
    {
        String sql  = "INSERT INTO "+TABLA_LINEAS+"("+CN_ID_LINEA+", "+CN_NOMBRE_LINEA+", "+CN_TALLA_LINEA+") VALUES (?, ?, ?)";
        String sql2 = "INSERT INTO "+TABLA_ACTUALIZACIONES_LINEAS+"("+CN_ID_LINEA_ACTUALIZACION_LINEAS+", "+CN_FECHA_ACTUALIZACION_LINEAS+") VALUES (?, ?)";
        db.beginTransaction();
        Log.d("INSERTANDO LINEAS","BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);
        SQLiteStatement stmt2 = db.compileStatement(sql2);

        for (int i = 0; i < ids_lineas.length(); i++)
        {
            stmt.bindString(1, ids_lineas.getString(i));
            stmt.bindString(2, nombres_lineas.getString(i));
            stmt.bindString(3, tallas.getString(i));
            stmt.executeInsert();
            stmt.clearBindings();

            Cursor verificador = cargarCursorActualizacionesLineas(ids_lineas.getString(i));
            int response = verificador.getCount();
            verificador.close();

            // Si hay registros con un id_producto = ids_productos[i], entonces ya existe y no debo agregarlo a la transaccion..
            if (response <= 0)
            {
                //Log.d("verificador", "Agregar a la transaccion. ID LINEA: " + ids_lineas.getString(i));
                stmt2.bindString(1, ids_lineas.getString(i));
                stmt2.bindString(2, split.getString(i));
                stmt2.executeInsert();
                stmt2.clearBindings();
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERTANDO LINEAS","END");
    }

    private boolean existe_dato(String[] ids_BD, String ids_insert)
    {
        // Verificare si dentro de mi array ids existe el id_lineas que ando buscando, en caso de existir retorno true
        boolean res = false;

        for (String id : ids_BD)
        {
            if (id.equals(ids_insert)) {
                res = true;
                break;
            }
        }
        return !res;
    }

    public void insertar_modelos(JSONArray ids_modelos, JSONArray ids_linea_modelo, JSONArray nombres_modelos , JSONArray split) throws JSONException
    {
        String sql  = "INSERT INTO "+TABLA_MODELOS +" ("+CN_ID_MODELO +", "+CN_ID_LINEA_MODELO +", "+CN_NOMBRE_MODELO+") VALUES (?, ?, ?)";
        String sql2 = "INSERT INTO "+TABLA_ACTUALIZACIONES_MODELOS  +" ("+CN_ID_MODELO_ACTUALIZACION_MODELOS  +", "+CN_FECHA_ACTUALIZACION_MODELOS  +") VALUES (?, ?)";
        db.beginTransaction();
        Log.d("INSERTANDO MODELOS","BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);
        SQLiteStatement stmt2 = db.compileStatement(sql2);

        for (int i = 0; i < ids_modelos.length(); i++)
        {
            stmt.bindString(1, ids_modelos.getString(i));
            stmt.bindString(2, ids_linea_modelo.getString(i));
            stmt.bindString(3, nombres_modelos.getString(i));
            stmt.executeInsert();
            stmt.clearBindings();

            Cursor verificador = cargarCursorActualizacionesModelos(ids_modelos.getString(i));
            int response = verificador.getCount();
            verificador.close();

            // Si hay registros con un id_producto = ids_productos[i], entonces ya existe y no debo agregarlo a la transaccion..
            if (response <= 0)
            {
                //Log.d("verificador", "Agregar a la transaccion. ID MODELO: " + ids_modelos.getString(i));
                stmt2.bindString(1, ids_modelos.getString(i));
                stmt2.bindString(2, split.getString(i));
                stmt2.executeInsert();
                stmt2.clearBindings();
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERTANDO MODELOS","END");
    }

    public void insertar_productos(JSONArray ids_productos, JSONArray ids_linea_producto, JSONArray ids_modelo_producto, JSONArray nombres_productos , JSONArray split, JSONArray precios_productos, JSONArray ids_bultos, JSONArray ids_materiales, JSONArray nombre_real, JSONArray id_color) throws JSONException
    {
        String sql  = "INSERT INTO "+TABLA_PRODUCTOS +" ("+CN_ID_PRODUCTO +", "+CN_ID_PRODUCTO_MODELO +", "+CN_ID_PRODUCTO_LINEA +", "+CN_ID_PRODUCTO_BULTO +", "+CN_ID_PRODUCTO_MATERIAL  +", "+ CN_CODIGO_FABRICANTE_PRODUCTO +", "+CN_PRECIO_PRODUCTO  +", "+CN_NOMBRE_PRODUCTO+", "+CN_TALLA_PRODUCTO+", "+CN_ID_PRODUCTO_COLOR+") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sql3 = "INSERT INTO "+TABLA_ACTUALIZACIONES_PRODUCTOS  +" ("+CN_ID_ACTUALIZACION_PRODUCTO  +", "+CN_FECHA_ACTUALIZACION_PRODUCTO  +", "+CN_ACTUALIZACION_IMAGEN_NUMERO  +") VALUES (?, ?, ?)";
        db.beginTransaction();

        Log.d("INSERTANDO PRODUCTOS", "BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);
        SQLiteStatement stmt3 = db.compileStatement(sql3);
        int acumulador =0;

        for (int i=0;i<ids_productos.length();i++)
        {
            stmt.bindString(1, ids_productos.getString(i));
            stmt.bindString(2, ids_modelo_producto.getString(i));
            stmt.bindString(3, ids_linea_producto.getString(i));
            stmt.bindString(4, ids_bultos.getString(i));
            stmt.bindString(5, ids_materiales.getString(i));
            stmt.bindString(6, nombres_productos.getString(i));
            stmt.bindString(7, precios_productos.getString(i));
            stmt.bindString(8, nombre_real.getString(i));
            stmt.bindString(9, nombre_real.getString(i).substring(nombre_real.getString(i).length() - 1));
            stmt.bindString(10, id_color.getString(i));
            stmt.executeInsert();
            stmt.clearBindings();

            for(int k = 0; k < Constantes.NUM_IMG; k++)
            {
                Cursor verificador = cargarCursorActualizacionesProductos(ids_productos.getString(i),k+1);
                int response = verificador.getCount();
                verificador.close();

                // Si hay registros con un id_producto = ids_productos.getString(i), entonces ya existe y no debo agregarlo a la transaccion..
                if (response <= 0) // if (response <= 0)
                {
                    stmt3.bindString(1, ids_productos.getString(i));
                    stmt3.bindString(2, split.getString(acumulador));
                    stmt3.bindString(3, String.valueOf(k + 1));
                    stmt3.executeInsert();
                    stmt3.clearBindings();
                    //Log.d("ACTUALIZACION PRODUCTO", "END");
                }

                acumulador++;
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERTANDO PRODUCTOS","END");
    }

    public void actualizar_actualizacionesGenerales(String lin, String mod, String pro, String materiales, String colores, String bultos)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_FECHA_ACTUALIZACION_GENERALES_LINEAS,lin);
        valores.put(CN_FECHA_ACTUALIZACION_GENERALES_MODELOS,mod);
        valores.put(CN_FECHA_ACTUALIZACION_GENERALES_PRODUCTOS,pro);
        valores.put(CN_FECHA_ACTUALIZACION_GENERALES_MATERIALES,materiales);
        valores.put(CN_FECHA_ACTUALIZACION_GENERALES_COLORES, colores);
        valores.put(CN_FECHA_ACTUALIZACION_GENERALES_BULTOS, bultos);

        db.update(TABLA_ACTUALIZACIONES_GENERALES, valores, null, null);
        //db.execSQL("UPDATE "+ TABLA_ACTUALIZACIONES_GENERALES +" SET "+CN_FECHA_ACTUALIZACION_GENERALES_LINEAS+"='"+lin+"', "+CN_FECHA_ACTUALIZACION_GENERALES_MODELOS+"='"+mod+"', "+CN_FECHA_ACTUALIZACION_GENERALES_PRODUCTOS+"='"+pro+"' , "+CN_FECHA_ACTUALIZACION_GENERALES_MATERIALES+"='"+materiales+"' , "+CN_FECHA_ACTUALIZACION_GENERALES_COLORES+"='"+colores+"' , "+CN_FECHA_ACTUALIZACION_GENERALES_BULTOS+"='"+bultos+"'");
    }

    public void actualizar_actualizacionesLineas(String id, String fecha)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_FECHA_ACTUALIZACION_LINEAS, fecha);
        String[] args = {id};

        db.update(TABLA_ACTUALIZACIONES_LINEAS, valores, CN_ID_LINEA_ACTUALIZACION_LINEAS + " =? ", args);
        //db.execSQL("UPDATE "+ TABLA_ACTUALIZACIONES_LINEAS +" SET "+CN_FECHA_ACTUALIZACION_LINEAS+"='"+fecha+"' WHERE "+CN_ID_LINEA_ACTUALIZACION_LINEAS+" = '"+id+"'");
    }

    public void actualizar_actualizacionesModelos(String id, String fecha)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_FECHA_ACTUALIZACION_MODELOS,fecha);
        String[] args = {id};

        db.update(TABLA_ACTUALIZACIONES_MODELOS, valores, CN_ID_MODELO_ACTUALIZACION_MODELOS+" =? ", args);
        //db.execSQL("UPDATE "+ TABLA_ACTUALIZACIONES_MODELOS +" SET "+CN_FECHA_ACTUALIZACION_MODELOS+"='"+fecha+"' WHERE "+CN_ID_MODELO_ACTUALIZACION_MODELOS+" = '"+id+"'");
    }

    public void actualizar_actualizacionesProductos(String id, String fecha, int n)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_FECHA_ACTUALIZACION_PRODUCTO, fecha);
        String[] args = {id, String.valueOf(n)};

        int r = db.update(TABLA_ACTUALIZACIONES_PRODUCTOS, valores, CN_ID_ACTUALIZACION_PRODUCTO+"=? AND "+CN_ACTUALIZACION_IMAGEN_NUMERO+" = ?",args);
        Log.d("Actualizando..", r + " columnas afectadas.");
        if (r==0)
        {
            ContentValues valores_new = new ContentValues();
            valores_new.put(CN_ID_ACTUALIZACION_PRODUCTO, id);
            valores_new.put(CN_FECHA_ACTUALIZACION_PRODUCTO, fecha);
            valores_new.put(CN_ACTUALIZACION_IMAGEN_NUMERO, n);

            long s = db.insert(TABLA_ACTUALIZACIONES_PRODUCTOS, null, valores_new);

            Log.d("Creando campo",s+" campo insertado.");
        }
    }

    public void actualizar_actualizacionesPedido(String id_vendedor, String fecha)
    {
        String[] args = {id_vendedor};
        Cursor c = db.query(TABLA_ACTUALIZACIONES_PEDIDOS, null, CN_ID_PEDIDOS_ACTUALIZACION + "=?", args, null, null, null);

        if(c.getCount()==0)
        {
            // Crear registro
            Log.d("Actualizaciones Pedido","Crear");
            ContentValues valores = new ContentValues();
            valores.put(CN_ID_PEDIDOS_ACTUALIZACION, id_vendedor);
            valores.put(CN_FECHA_ACTUALIZACION_PEDIDOS, fecha);
            db.insert(TABLA_ACTUALIZACIONES_PEDIDOS, null, valores);
        }
        else
        {
            // Solo actualizar
            Log.d("Actualizaciones Pedido", "Actualizar");
            ContentValues valores = new ContentValues();
            valores.put(CN_FECHA_ACTUALIZACION_PEDIDOS, fecha);
            db.update(TABLA_ACTUALIZACIONES_PEDIDOS, valores, CN_ID_PEDIDOS_ACTUALIZACION + "=?", args);
        }

        c.close();
    }

    public void actualizar_actualizacionesCliente(String id_vendedor, String fecha)
    {
        String[] args = {id_vendedor};
        Cursor c = db.query(TABLA_ACTUALIZACIONES_CLIENTES, null, CN_ID_CLIENTES_ACTUALIZACION + "=?", args, null, null, null);

        if(c.getCount()==0)
        {
            // Crear registro
            Log.d("Actualizaciones Cliente","Crear");
            ContentValues valores = new ContentValues();
            valores.put(CN_ID_CLIENTES_ACTUALIZACION, id_vendedor);
            valores.put(CN_FECHA_ACTUALIZACION_CLIENTES, fecha);
            db.insert(TABLA_ACTUALIZACIONES_CLIENTES, null, valores);
        }
        else
        {
            // Solo actualizar
            Log.d("Actualizaciones Cliente", "Actualizar");
            ContentValues valores = new ContentValues();
            valores.put(CN_FECHA_ACTUALIZACION_CLIENTES, fecha);
            db.update(TABLA_ACTUALIZACIONES_CLIENTES, valores, CN_ID_CLIENTES_ACTUALIZACION + "=?", args);
        }

        c.close();
    }

    public void actualizar_actualizacionesFunciones(String id_vendedor, String fecha)
    {
        String[] args = {id_vendedor};
        Cursor c = db.query(TABLA_ACTUALIZACIONES_FUNCIONES , null, CN_ID_FUNCIONES_ACTUALIZACION  + "=?", args, null, null, null);

        if(c.getCount()==0)
        {
            // Crear registro
            Log.d("Actualizacion Funciones","Crear");
            ContentValues valores = new ContentValues();
            valores.put(CN_ID_FUNCIONES_ACTUALIZACION , id_vendedor);
            valores.put(CN_FECHA_ACTUALIZACION_FUNCIONES , fecha);
            db.insert(TABLA_ACTUALIZACIONES_FUNCIONES, null, valores);
        }
        else
        {
            // Solo actualizar
            Log.d("Actualizacion Funciones", "Actualizar");
            ContentValues valores = new ContentValues();
            valores.put(CN_FECHA_ACTUALIZACION_FUNCIONES, fecha);
            db.update(TABLA_ACTUALIZACIONES_FUNCIONES, valores, CN_ID_FUNCIONES_ACTUALIZACION + "=?", args);
        }

        c.close();
    }

    public Cursor buscarUsuario(String cedula)
    {
        String[] columnas = new String[]{CN_ID_USUARIO, CN_CODIGO_USUARIO, CN_NOMBRE, CN_APELLIDO, CN_CEDULA, CN_TELEFONO, CN_EMAIL, CN_PASSWORD, CN_LINEAS_DESHABILITADAS, CN_MODELOS_DESHABILITADOS, CN_PRODUCTOS_DESHABILITADOS, CN_PASSWORD, CN_USUARIO_SALT, CN_USUARIO_FECHA_ACTUALIZACION, CN_ESTADO};
        String[] args = { cedula };
        return db.query(TABLA_USUARIO,columnas,CN_CEDULA+"=?",args,null,null,null);
    }

    public Cursor buscarFuncionesUsuario(String id, String opcion)
    {
        /*String[] columnas = new String[]{ CN_HOMES, CN_MUESTRARIO, CN_PEDIDOS, CN_CLIENTES,
                                          CN_PERFIL, CN_OPCIONES, CN_ESTADO_CUENTA };*/

        String[] columnas = new String[]{ opcion }; // homes, muestrario

        String[] args = { id };
        return db.query(TABLA_FUNCIONES,columnas,CN_ID_USUARIO_FUNCIONES+"=?",args,null,null,null);
    }

    @SuppressWarnings("WeakerAccess")
    public Cursor cargarCursorUsuario()
    {
        String[] columnas = new String[]{CN_ID_USUARIO, CN_CODIGO_USUARIO, CN_NOMBRE, CN_APELLIDO, CN_CEDULA, CN_TELEFONO, CN_EMAIL, CN_PASSWORD, CN_USUARIO_FECHA_ACTUALIZACION};
        return db.query(TABLA_USUARIO,columnas,null,null,null,null,null);
    }

    public String cargarCursorLineas(String cedula)
    {
        String[] columnas2 = new String[]{CN_LINEAS_DESHABILITADAS};
        String[] args = { cedula };
        Cursor cursor = db.query(TABLA_USUARIO,columnas2,CN_CEDULA+"=?",args,null,null,null);
        String[] lineas_deshabilitadas = new String[cursor.getCount()];

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            //Log.d("Productos_Modelos_Linea","("+cursor.getString(0)+")");
            lineas_deshabilitadas = (cursor.getString(0)).split(",");
        }

        //Log.d("Productos_Modelos_Linea","El usuario "+cedula+" tiene "+cursor.getCount()+" lineas deshabilitadas!");

        cursor.close();

        String[] columnas = new String[]{CN_ID_LINEA, CN_NOMBRE_LINEA};
        Cursor cursor2 = db.query(TABLA_LINEAS,columnas,null,null,null,null,CN_TALLA_LINEA+" DESC, "+CN_NOMBRE_LINEA+" ASC");
        String res = "";

        if(lineas_deshabilitadas.length==0)
        {
            for(cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                if (!res.equals(""))
                    res = res+"|";
                res = res + cursor2.getString(0)+"&"+cursor2.getString(1);
            }
        }
        else
        {
            for(cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                String cad="";
                int bandera = 0;
                for (String lineas_deshabilitada : lineas_deshabilitadas)
                {
                    if (!cursor2.getString(0).equals(lineas_deshabilitada))
                    {
                        cad = "&" + cursor2.getString(1);
                        bandera = 1;
                        //Log.d("CursorModelos_Lineas", "Linea " + cursor2.getString(1) + " no deshabilitada aun");
                    }
                    else
                    {
                        bandera = 0;
                        //Log.d("CursorModelos_Lineas", "Linea " + cursor2.getString(1) + " deshabilitada..");
                        break;
                    }
                }
                if (bandera==1)
                {
                    if (!res.equals(""))
                        res = res+"|";
                    res = res + cursor2.getString(0)+cad;
                }
            }
        }
        cursor2.close();

        return res;
    }

    public String cargarCursorModelos_Lineas(String id, String cedula)
    {
        String[] columnas2 = new String[]{CN_MODELOS_DESHABILITADOS};
        String[] args = { cedula };
        Cursor cursor = db.query(TABLA_USUARIO,columnas2,"cedula=?",args,null,null,null);
        String[] modelos_deshabilitados = new String[0];

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            modelos_deshabilitados = (cursor.getString(0)).split(",");
        }

        cursor.close();

        Log.d("CargarCursorLineas", "Hay " + cursor.getCount() + " modelos deshabilitado!");

        String[] columnas = new String[]{CN_ID_MODELO, CN_ID_LINEA_MODELO, CN_NOMBRE_MODELO};
        String[] args2 = { id };
        Cursor cursor2 = db.query(TABLA_MODELOS,columnas,"id_linea=?",args2,null,null,CN_NOMBRE_MODELO);

        String res = "";

        if(modelos_deshabilitados.length==0)
        {
            for(cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                if (!res.equals(""))
                    res = res+"|";

                res = res + cursor2.getString(0)+"&" + cursor2.getString(2) + "&"+cursor2.getString(3);
            }
        }
        else
        {
            for(cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                String cad="";
                int bandera = 0;
                for (String modelos_deshabilitado : modelos_deshabilitados)
                {
                    if (!cursor2.getString(0).equals(modelos_deshabilitado))
                    {
                        cad = "&" + cursor2.getString(2) + "&";
                        bandera = 1;
                    }
                    else
                    {
                        bandera = 0;
                        break;
                    }
                }

                if (bandera==1)
                {
                    if (!res.equals(""))
                        res = res+"|";
                    res = res + cursor2.getString(0)+cad+cursor2.getString(2);
                }
            }
        }

        cursor2.close();

        return res;
    }

    public String cargarCursorProductos_Modelos_Lineas(String id_linea, String id_modelo, String cedula)
    {
        String[] columnas2 = new String[]{CN_PRODUCTOS_DESHABILITADOS};
        String[] args = { cedula };
        Cursor cursor = db.query(TABLA_USUARIO,columnas2,"cedula=?",args,null,null,null);
        String[] productos_deshabilitados = new String[0];

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            Log.d("CursorModelos_Line","Deshabilite!");
            productos_deshabilitados = (cursor.getString(0)).split(",");
        }

        cursor.close();

        Log.d("cargarCursorProductos","Hay "+cursor.getCount()+" Productos deshabilitados!");

        String[] columnas = new String[]{CN_ID_PRODUCTO, CN_ID_PRODUCTO_MODELO, CN_ID_PRODUCTO_LINEA, CN_CODIGO_FABRICANTE_PRODUCTO, CN_PRECIO_PRODUCTO, CN_NOMBRE_PRODUCTO, CN_ID_PRODUCTO_COLOR};
        String[] args2 = { id_linea,id_modelo };
        Cursor cursor2 = db.query(TABLA_PRODUCTOS,columnas,"id_linea=? AND id_modelo=?",args2,null,null, CN_CODIGO_FABRICANTE_PRODUCTO);

        String res = "";

        if(productos_deshabilitados.length==0)
        {
            for(cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                if (!res.equals(""))
                    res = res+"|";

                Cursor cursore = buscarColor(cursor2.getString(6));
                String color = "null";

                for (cursore.moveToFirst(); !cursore.isAfterLast(); cursore.moveToNext())
                {
                    color = cursore.getString(1);
                }

                cursor.close();

                res = res + cursor2.getString(0) +"&" + color + "&" + cursor2.getString(4) + "&" + cursor2.getString(5)+"&"+cursor2.getString(3);
            }
        }
        else
        {
            for(cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                String cad="";
                int bandera = 0;

                //for (int w = 0; w < productos_deshabilitados.length; w++)
                for (String productos_deshabilitado : productos_deshabilitados)
                {
                    if (!cursor2.getString(0).equals(productos_deshabilitado))
                    {
                        // El producto no esta deshabilitado, lo copio normalmente..
                        Cursor cursore = buscarColor(cursor2.getString(6));
                        String color = "null";

                        for (cursore.moveToFirst(); !cursore.isAfterLast(); cursore.moveToNext())
                        {
                            color = cursore.getString(1);
                        }

                        cursor.close();
                        cad = "&" + color + "&";
                        bandera = 1;
                    }
                    else
                    {
                        // El producto fue deshabilitado, no lo copio..
                        bandera = 0;
                        break;
                    }
                }

                if (bandera==1)
                {
                    if (!res.equals(""))
                        res = res +"|";
                    res = res + cursor2.getString(0)+cad+cursor2.getString(4)+"&"+cursor2.getString(5)+"&"+cursor2.getString(3);
                }

            }
        }

        cursor2.close();

        return res;
    }

    public String cargarCursorProductos_Modelos_Lineas_DialogAgregar(String id_linea, String id_modelo, String cedula, String[] ids_ocultar)
    {
        String[] columnas2 = new String[]{CN_PRODUCTOS_DESHABILITADOS};
        String[] args = { cedula };
        Cursor cursor = db.query(TABLA_USUARIO,columnas2,"cedula=?",args,null,null,null);
        String[] productos_deshabilitados = new String[0];

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            Log.d("CursorModelos_Line","Deshabilite!");
            productos_deshabilitados = (cursor.getString(0)).split(",");
        }

        cursor.close();

        Log.d("CargarCursorLineas","Hay "+cursor.getCount()+" Productos deshabilitados!");

        String[] columnas = new String[]{CN_ID_PRODUCTO, CN_ID_PRODUCTO_MODELO, CN_ID_PRODUCTO_LINEA, CN_CODIGO_FABRICANTE_PRODUCTO, CN_PRECIO_PRODUCTO, CN_NOMBRE_PRODUCTO};
        String[] args2 = { id_linea, id_modelo};
        Cursor cursor2 = db.query(TABLA_PRODUCTOS,columnas,"id_linea=? AND id_modelo=?",args2,null,null, CN_CODIGO_FABRICANTE_PRODUCTO);

        String res = "";

        if(productos_deshabilitados.length==0)
        {
            for(cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                if( !Arrays.asList(ids_ocultar).contains(cursor2.getString(0)) )
                {
                    if (!res.equals(""))
                        res = res+"|";
                    res = res + cursor2.getString(0) +"&" + cursor2.getString(3) + "&" + cursor2.getString(4) + "&" + cursor2.getString(5);
                }
            }
        }
        else
        {
            for(cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
            {
                String cad="";
                int bandera = 0;

                //for (int w = 0; w < productos_deshabilitados.length; w++)
                for (String productos_deshabilitado : productos_deshabilitados)
                {
                    if( !Arrays.asList(ids_ocultar).contains(cursor2.getString(0)) )
                    {
                        if (!cursor2.getString(0).equals(productos_deshabilitado))
                        {
                            cad = "&" + cursor2.getString(3) + "&";
                            bandera = 1;
                        }
                        else
                        {
                            bandera = 0;
                            break;
                        }
                    }
                }

                if (bandera==1)
                {
                    if( !Arrays.asList(ids_ocultar).contains(cursor2.getString(0)) )
                    {
                        if (!res.equals(""))
                            res = res +"|";
                        res = res + cursor2.getString(0)+cad+cursor2.getString(4)+"&"+cursor2.getString(5);
                    }
                }
            }
        }

        cursor2.close();

        return res;
    }

    public Cursor cargarCursorActualizacionesGenerales()
    {
        String[] columnas = new String[]{CN_FECHA_ACTUALIZACION_GENERALES_LINEAS, CN_FECHA_ACTUALIZACION_GENERALES_MODELOS, CN_FECHA_ACTUALIZACION_GENERALES_PRODUCTOS, CN_FECHA_ACTUALIZACION_GENERALES_MATERIALES, CN_FECHA_ACTUALIZACION_GENERALES_COLORES, CN_FECHA_ACTUALIZACION_GENERALES_BULTOS};
        return db.query(TABLA_ACTUALIZACIONES_GENERALES,columnas,null,null,null,null,null);
    }

    private Cursor cargarCursorActualizacionesLineas(String id)
    {
        String[] columnas = new String[]{CN_ID_ACTUALIZACION_LINEAS, CN_ID_LINEA_ACTUALIZACION_LINEAS, CN_FECHA_ACTUALIZACION_LINEAS};
        String[] args = {id};
        return db.query(TABLA_ACTUALIZACIONES_LINEAS,columnas,"id_linea=?",args,null,null,null);
    }

    public Cursor cargarCursorActualizacionesLineas()
    {
        String[] columnas = new String[]{ CN_ID_LINEA_ACTUALIZACION_LINEAS, CN_FECHA_ACTUALIZACION_LINEAS};
        return db.query(TABLA_ACTUALIZACIONES_LINEAS,columnas,null,null,null,null,CN_ID_LINEA_ACTUALIZACION_LINEAS+" ASC");
    }

    private Cursor cargarCursorActualizacionesModelos(String ids_modelo)
    {
        String[] columnas = new String[]{CN_ID_ACTUALIZACION_MODELOS, CN_ID_MODELO_ACTUALIZACION_MODELOS, CN_FECHA_ACTUALIZACION_MODELOS};
        String[] args = {ids_modelo};
        return db.query(TABLA_ACTUALIZACIONES_MODELOS,columnas,"id_modelo=?",args,null,null,null);
    }

    public Cursor cargarCursorActualizacionesModelos()
    {
        String[] columnas = new String[]{ CN_ID_MODELO_ACTUALIZACION_MODELOS, CN_FECHA_ACTUALIZACION_MODELOS};
        return db.query(TABLA_ACTUALIZACIONES_MODELOS,columnas,null,null,null,null,CN_ID_ACTUALIZACION_MODELOS+" ASC");
    }

    private Cursor cargarCursorActualizacionesProductos(String ids_producto, int w)
    {
        String[] columnas = new String[]{CN_ID_ACTUALIZACION_PRODUCTO, CN_ACTUALIZACION_IMAGEN_NUMERO, CN_FECHA_ACTUALIZACION_PRODUCTO };
        String[] args = {ids_producto, String.valueOf(w)};
        return db.query(TABLA_ACTUALIZACIONES_PRODUCTOS,columnas,"id_producto=? AND imagen_numero=?",args,null,null,null);
    }

    public Cursor cargarCursorActualizacionesProductos()
    {
        String[] columnas = new String[]{CN_ID_ACTUALIZACION_PRODUCTO, CN_ACTUALIZACION_IMAGEN_NUMERO, CN_FECHA_ACTUALIZACION_PRODUCTO };
        return db.query(TABLA_ACTUALIZACIONES_PRODUCTOS,columnas,null,null,null,null,CN_ID_ACTUALIZACION_PRODUCTO+" ASC, "+CN_ACTUALIZACION_IMAGEN_NUMERO+" ASC"); //
        //        query(String table          , String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
    }

    public Cursor cargarCursorProductosPedidos()
    {
        //                                                   0                           1                           2                               3                            4                              5                          6   (BULTOS)
        String[] columnas = new String[]{CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS, CN_TIPO_PRODUCTOS_PEDIDOS ,CN_PRECIO_PRODUCTOS_PEDIDOS ,CN_NUMERACION_PRODUCTOS_PEDIDOS ,CN_PARES_PRODUCTOS_PEDIDOS, CN_ID_PRODUCTO_PEDIDOS, CN_BULTOS_PRODUCTOS_PEDIDOS, CN_NOMBRE_PRODUCTOS_PEDIDOS };
        return db.query(TABLA_PRODUCTOS_PEDIDOS,columnas,null,null,null,null,null);
    }

    public Cursor cargarCursorProductosPedidos_porTalla(String tipo)
    {
        String[] columnas = new String[]{CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS, CN_TIPO_PRODUCTOS_PEDIDOS ,CN_PRECIO_PRODUCTOS_PEDIDOS ,CN_NUMERACION_PRODUCTOS_PEDIDOS ,CN_PARES_PRODUCTOS_PEDIDOS, CN_ID_PRODUCTO_PEDIDOS, CN_BULTOS_PRODUCTOS_PEDIDOS, CN_NOMBRE_PRODUCTOS_PEDIDOS };
        String[] args = {tipo};
        return db.query(TABLA_PRODUCTOS_PEDIDOS,columnas,CN_TIPO_PRODUCTOS_PEDIDOS+"=?",args,null,null,null);
    }

    public Cursor cargarCursorProductosPedidos_porTalla()
    {
        String[] columnas = new String[]{CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS, CN_TIPO_PRODUCTOS_PEDIDOS ,CN_PRECIO_PRODUCTOS_PEDIDOS ,CN_NUMERACION_PRODUCTOS_PEDIDOS ,CN_PARES_PRODUCTOS_PEDIDOS, CN_ID_PRODUCTO_PEDIDOS, CN_BULTOS_PRODUCTOS_PEDIDOS, CN_NOMBRE_PRODUCTOS_PEDIDOS };
        return db.query(TABLA_PRODUCTOS_PEDIDOS,columnas,null,null,null,null,null);
    }

    public Cursor cargarCursorClientes_Vendedor(String id)
    {
        String[] columnas = new String[]{CN_ID_RAZON_SOCIAL, CN_ID_CLIENTES};
        String[] args = {id};
        return db.query(TABLA_CLIENTES,columnas,CN_ID_VENDEDOR+"=?",args,null,null,null);
    }

    public Cursor cargarCursorClientesByID(String id)
    {
        String[] args = {id};
        String[] columnas = new String[]{CN_ID_RAZON_SOCIAL,CN_ID_CLIENTES};
        return db.query(TABLA_CLIENTES,columnas,CN_ID_VENDEDOR+"=?",args,null,null,null);
    }

    public void insertar_bulto(JSONArray id, JSONArray nombre, JSONArray tipo, JSONArray rango,JSONArray numeracion, JSONArray pares) throws JSONException
    {
        String sql = "INSERT INTO "+TABLA_BULTOS +" ("+CN_ID_BULTO +", "+CN_NOMBRE_BULTO +", "+CN_TIPO_BULTO +", "+CN_RANGO_TALLAS_BULTO +", "+CN_NUMERACION_BULTO  +", "+CN_NUMERO_PARES_BULTO  +") VALUES (?, ?, ?, ?, ?, ?)";
        db.beginTransaction();
        Log.d("INSERTANDO BULTOS","BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < id.length(); i++)
        {
            stmt.bindString(1, id.getString(i));
            stmt.bindString(2, nombre.getString(i));
            stmt.bindString(3, tipo.getString(i));
            stmt.bindString(4, rango.getString(i));
            stmt.bindString(5, numeracion.getString(i));
            stmt.bindString(6, pares.getString(i));
            stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERTANDO BULTOS","END");
    }

    public void insertar_materiales(JSONArray id, JSONArray nombre, JSONArray id_color, JSONArray prefijo, JSONArray color_base) throws JSONException
    {
        String sql = "INSERT INTO "+TABLA_MATERIALES +" ("+CN_ID_MATERIAL +", "+CN_NOMBRE_MATERIAL +", "+CN_ID_MATERIAL_COLOR +", "+CN_PREFIJO_MATERIAL +", "+CN_ID_COLOR_BASE_MATERIAL  +") VALUES (?, ?, ?, ?, ?)";
        db.beginTransaction();
        Log.d("INSERTANDO MATERIALES", "BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < id.length(); i++)
        {
            stmt.bindString(1, id.getString(i));
            stmt.bindString(2, nombre.getString(i));
            stmt.bindString(3, id_color.getString(i));
            stmt.bindString(4, prefijo.getString(i));
            stmt.bindString(5, color_base.getString(i));
            stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERTANDO MATERIALES", "END");
    }

    public Cursor cargarProductoSeleccionado()
    {
        String[] columnas = new String[]{CN_SELECCIONADO};
        return db.query(TABLA_PRODUCTO_SELECCIONADO,columnas,null,null,null,null,null);
    }

    public void insertar_producto_seleccionado(String id_linea_buscador, String id_modelo_buscador)
    {
        db.insert(TABLA_PRODUCTO_SELECCIONADO, null, generarContentValues_insertarProducto_Seleccionado(id_linea_buscador, id_modelo_buscador)); // Si devuelve -1 ha habido un problema..
    }

    private ContentValues generarContentValues_insertarProducto_Seleccionado(String id_linea_buscador, String id_modelo_buscador) {

        ContentValues valores = new ContentValues();
        valores.put(CN_SELECCIONADO, id_linea_buscador + "&" + id_modelo_buscador);
        return valores;
    }

    public Cursor buscarProducto(String id)
    {
        String[] columnas = new String[]{CN_CODIGO_FABRICANTE_PRODUCTO, CN_NOMBRE_PRODUCTO, CN_ID_PRODUCTO};
        String[] args = {id};
        return db.query(TABLA_PRODUCTOS,columnas,CN_ID_PRODUCTO+"=?",args,null,null,null);
    }

    public Cursor buscarProductoByName(String textoDebajo)
    {
        String[] columnas = new String[]{CN_PRECIO_PRODUCTO, CN_NOMBRE_PRODUCTO};
        String[] args = {textoDebajo};
        return db.query(TABLA_PRODUCTOS,columnas, CN_ID_PRODUCTO +"=?",args,null,null,null);
    }

    public void insertar_colores(JSONArray ids, JSONArray colores) throws JSONException
    {
        String sql = "INSERT INTO "+TABLA_COLORES +" ("+CN_ID_COLOR +", "+CN_NOMBRE_COLOR +") VALUES (?, ?)";
        db.beginTransaction();
        Log.d("INSERTANDO COLORES","BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < ids.length(); i++)
        {
            stmt.bindString(1, ids.getString(i));
            stmt.bindString(2, colores.getString(i));
            stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERTANDO COLORES","END");
    }

    public Cursor buscar_ID_Bulto_Material_Producto(String id_actual)
    {
        String[] columnas = new String[]{CN_ID_PRODUCTO_BULTO,CN_ID_PRODUCTO_MATERIAL, CN_ID_PRODUCTO_COLOR};
        String[] args = {id_actual};
        return db.query(TABLA_PRODUCTOS,columnas,CN_ID_PRODUCTO+"=?",args,null,null,null);
    }

    public Cursor buscarBulto(String idbulto)
    {
        String[] columnas = new String[]{CN_ID_BULTO , CN_NOMBRE_BULTO, CN_TIPO_BULTO, CN_RANGO_TALLAS_BULTO, CN_NUMERACION_BULTO, CN_NUMERO_PARES_BULTO};
        String[] args = {idbulto};
        return db.query(TABLA_BULTOS,columnas,CN_ID_BULTO+"=?",args,null,null,null);
    }

    public Cursor buscarColor(String idcolor)
    {
        Log.d("buscarColor",""+idcolor);
        String[] columnas = new String[]{CN_ID_COLOR , CN_NOMBRE_COLOR};
        String[] args = {idcolor};
        return db.query(TABLA_COLORES ,columnas,CN_ID_COLOR +"=?",args,null,null,null);
    }

    /*public Cursor buscarID_Color(String idmaterial)
    {
        String[] columnas = new String[]{CN_ID_MATERIAL_COLOR};
        String[] args = {idmaterial};
        return db.query(TABLA_MATERIALES ,columnas,CN_ID_MATERIAL +"=?",args,null,null,null);
    }*/

    public Cursor buscarSalt_Usuario(String ci)
    {
        String[] columnas = new String[]{CN_USUARIO_SALT};
        String[] args = { ci };
        return db.query(TABLA_USUARIO,columnas,"cedula=?",args,null,null,null);
    }

    public void eliminar_producto_seleccionado()
    {
        db.delete(TABLA_PRODUCTO_SELECCIONADO, null, null);
    }

    public void eliminar_data(int i)
    {
        if (i==0) // lineas
        {
            if (debo_borrar_tabla(i))
            {
                Log.w("BORRE", "TABLA_LINEAS");
                db.delete(TABLA_LINEAS, null, null);
                //db.execSQL("DELETE FROM "+ TABLA_LINEAS);
                //db.execSQL("DELETE FROM "+ TABLA_ACTUALIZACIONES_LINEAS);
            }
        }
        if (i==1) // modelos
        {
            if (debo_borrar_tabla(i)) {
                Log.w("BORRE", "TABLA_MODELOS");
                db.delete(TABLA_MODELOS, null, null);
                //db.execSQL("DELETE FROM "+ TABLA_MODELOS);
                //db.execSQL("DELETE FROM "+ TABLA_ACTUALIZACIONES_MODELOS);
            }
        }
        if (i==2) // productos
        {
            if (debo_borrar_tabla(i)) {
                Log.w("BORRE", "TABLA_PRODUCTOS");
                //db.execSQL("DELETE FROM " + TABLA_PRODUCTOS);
                db.delete(TABLA_PRODUCTOS, null, null);
                //db.execSQL("DELETE FROM "+ TABLA_ACTUALIZACIONES_PRODUCTOS); // No debo borrar esta tabla..
            }
        }
        if (i==3) // usuario
        {
            if (debo_borrar_tabla(i))
            {
                Log.w("BORRE?", "TABLA_USUARIO");
                //db.execSQL("DELETE FROM " + TABLA_USUARIO);
                // NO DEBO BORRARLA PORQUE BORRA LOS DEMAS USUARIOS
                // Y COMO YA TENGO UNA VERIFICACION DE USUARIOS YA CREADOS NO HAY PROBLEMA
            }
        }
        else if (i==4)
        {
            if (debo_borrar_tabla(i))
            {
                Log.w("BORRE", "TABLA_MATERIALES");
                db.delete(TABLA_MATERIALES, null, null);
                //db.execSQL("DELETE FROM "+ TABLA_MATERIALES);
            }
        }
        else if (i==5)
        {
            if (debo_borrar_tabla(i)) {
                Log.w("BORRE", "TABLA_COLORES");
                db.delete(TABLA_COLORES, null, null);
                //db.execSQL("DELETE FROM "+ TABLA_COLORES); //
            }
        }
        else if (i==6)
        {
            if (debo_borrar_tabla(i)) {
                Log.w("BORRE", "TABLA_BULTOS");
                db.delete(TABLA_BULTOS, null, null);
                //db.execSQL("DELETE FROM "+ TABLA_BULTOS); //
            }
        }
        else if (i==7)
        {
            if (debo_borrar_tabla(i)) {
                Log.w("BORRE", "TABLA_CLIENTES");
                db.delete(TABLA_CLIENTES, null, null);
                //db.execSQL("DELETE FROM "+ TABLA_CLIENTES); //
            }
        }
        else if (i==8)
        {
            if (debo_borrar_tabla(i)) {
                Log.w("BORRE", "TABLA_PEDIDOS");
                db.delete(TABLA_PEDIDOS, null, null);
                //db.execSQL("DELETE FROM "+ TABLA_PEDIDOS ); //
            }
        }
        else if (i==9)
        {
            if (debo_borrar_tabla(i)) {
                Log.w("BORRE", "TABLA_PEDIDOS_DETALLES");
                db.delete(TABLA_PEDIDOS_DETALLES, null, null);
                //db.execSQL("DELETE FROM "+ TABLA_PEDIDOS_DETALLES );
            }
        }
        else if (i==10)
        {
            if (debo_borrar_tabla(i)) {
                Log.w("BORRE", "TABLA_FUNCIONES");
                db.delete(TABLA_FUNCIONES, null, null);
                //db.execSQL("DELETE FROM "+ TABLA_PEDIDOS_DETALLES );
            }
        }
    }

    private boolean debo_borrar_tabla(int i)
    {
        boolean res = false;
        String[] argumento = argumentos(i);
        String[] columnas = new String[]{argumento[0]};
        Cursor cursor = db.query(argumento[1],columnas,null,null,null,null,null);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            res = true;
        }

        cursor.close();
        return res;
    }

    private String[] argumentos(int i)
    {
        String[] res = new String[2];
        if (i==0)
        {
            res[0] = CN_ID_LINEA_BASE;
            res[1] = TABLA_LINEAS;
        }
        else if (i==1)
        {
            res[0] = CN_ID_MODELO_BASE;
            res[1] = TABLA_MODELOS;
        }
        else if (i==2)
        {
            res[0] = CN_ID_PRODUCTO_BASE;
            res[1] = TABLA_PRODUCTOS;
        }
        else if (i==3) // usuario
        {
            res[1] = TABLA_USUARIO ;
            res[0] = CN_ID_USUARIO ;
        }
        else if (i==4)
        {
            res[1] = TABLA_MATERIALES ;
            res[0] = CN_ID_MATERIAL_BASE ;
        }
        else if (i==5)
        {
            res[1] = TABLA_COLORES ;
            res[0] = CN_ID_COLOR_BASE ;
        }
        else if (i==6)
        {
            res[1] = TABLA_BULTOS ;
            res[0] = CN_ID_BULTO_BASE  ;
        }
        else if (i==7)
        {
            res[1] = TABLA_CLIENTES ;
            res[0] = CN_ID_TABLA_CLIENTES ;
        }
        else if (i==8)
        {
            res[1] = TABLA_PEDIDOS ;
            res[0] = CN_ID_PEDIDOS_BD  ;
        }
        else if (i==9)
        {
            res[1] = TABLA_PEDIDOS_DETALLES ;
            res[0] = CN_ID_PEDIDOS_DETALLES_BD  ;
        }
        else if (i==10)
        {
            res[1] = TABLA_FUNCIONES ;
            res[0] = CN_ID_TABLA_FUNCIONES  ;
        }

        return res;
    }

    public void eliminar_pedido() {
        Log.d("ELIMINAR PEDIDO","OK");
        db.delete(TABLA_PRODUCTOS_PEDIDOS, null, null);
        //db.execSQL("DELETE FROM "+ TABLA_PRODUCTOS_PEDIDOS);
    }

    public void eliminar_pedido_local(String id_pedido)
    {
        String[] args = {id_pedido};
        int res1 = db.delete(TABLA_PEDIDOS_LOCALES, CN_ID_PEDIDOS_LOCALES_BD +"=?", args);
        int res2 = db.delete(TABLA_PEDIDOS_LOCALES_DETALLES, CN_ID_PEDIDOS_LOCALES_DETALLES_REAL  +"=?", args);
        if ( res1 != 0 && res2 != 0 )
            Log.d("ELIMINAR PEDIDO LOCAL","OK");
    }

    public Cursor buscarClienteByID(String id_cliente)
    {
        String[] columnas = new String[]{CN_ID_RAZON_SOCIAL,CN_ID_RIF,CN_ID_DIRECCION,CN_ID_TELEFONO,CN_ID_ESTADO,CN_ID_EMAIL};
        String[] args = { id_cliente };
        return db.query(TABLA_CLIENTES,columnas,CN_ID_CLIENTES+"=?",args,null,null,null);
    }

    public void actualizar_cliente(String id_cliente, String razon_social, String rif, String direccion_cliente, String telefono_cliente, String estado_cliente, String email_cliente)
    {
        db.update(TABLA_CLIENTES, generarContentValues_actualizarCliente(razon_social, rif, direccion_cliente, telefono_cliente, estado_cliente, email_cliente), CN_ID_CLIENTES + "=?", new String[]{id_cliente});
    }

    private ContentValues generarContentValues_actualizarCliente(String razon_social, String rif, String direccion_cliente, String telefono_cliente, String estado_cliente, String email_cliente)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_ID_RAZON_SOCIAL ,razon_social);
        valores.put(CN_ID_RIF ,rif);
        valores.put(CN_ID_DIRECCION ,direccion_cliente);
        valores.put(CN_ID_TELEFONO ,telefono_cliente);
        valores.put(CN_ID_ESTADO, estado_cliente);
        valores.put(CN_ID_EMAIL, email_cliente);
        return valores;
    }

    public void actualizar_producto_pedido_cantidad(String id, int newVal)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_BULTOS_PRODUCTOS_PEDIDOS, newVal);
        int res = db.update(TABLA_PRODUCTOS_PEDIDOS, valores, CN_ID_PRODUCTO_PEDIDOS+"=?",new String[]{id});
        Log.d("", "Se actualizo el numero de pares de usuario a " + res + " productos. ID " + id + ", a " + newVal + " pares.");
    }

    public void insertar_pedidos(JSONArray id_pedido, JSONArray id_vendedor, JSONArray id_cliente, JSONArray fecha, JSONArray monto, JSONArray estatus, JSONArray observaciones, JSONArray codigos) throws JSONException
    {
        String sql = "INSERT INTO "+TABLA_PEDIDOS +" ("+CN_ID_PEDIDOS_REAL +", "+CN_ID_VENDEDOR_PEDIDOS +", "+CN_ID_CLIENTE_PEDIDOS +", "+CN_FECHA_PEDIDOS +", "+CN_MONTO_PEDIDOS  +", "+CN_ESTATUS_PEDIDO  +", "+CN_OBSERVACIONES+", "+CN_CODIGO_PEDIDOS+") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        db.beginTransaction();
        Log.d("INSERTANDO PEDIDOS","BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < id_pedido.length(); i++)
        {
            stmt.bindString(1, id_pedido.getString(i));
            stmt.bindString(2, id_vendedor.getString(i));
            stmt.bindString(3, id_cliente.getString(i));
            stmt.bindString(4, fecha.getString(i));
            stmt.bindString(5, monto.getString(i));
            stmt.bindString(6, estatus.getString(i));
            stmt.bindString(7, observaciones.getString(i));
            stmt.bindString(8, codigos.getString(i));
            stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERTANDO PEDIDOS","END");
    }

    public Cursor cargarCursorPedidosByID(String id_usuario)
    {
        String[] columnas = new String[]{CN_ID_PEDIDOS_REAL, CN_ID_VENDEDOR_PEDIDOS, CN_ID_CLIENTE_PEDIDOS, CN_FECHA_PEDIDOS, CN_MONTO_PEDIDOS, CN_ESTATUS_PEDIDO, CN_OBSERVACIONES, CN_CODIGO_PEDIDOS};
        String[] args = { id_usuario };
        return db.query(TABLA_PEDIDOS,columnas,CN_ID_VENDEDOR_PEDIDOS+"=?",args,null,null,CN_FECHA_PEDIDOS+" DESC"); // Ordenara por fecha
    }

    public Cursor cargarCursorPedidos_LocalesByID(String id_usuario)
    {
        String[] columnas = new String[]{CN_FECHA_PEDIDOS_LOCALES, CN_ID_VENDEDOR_PEDIDOS_LOCALES, CN_ID_CLIENTE_PEDIDOS_LOCALES, CN_FECHA_PEDIDOS_LOCALES, CN_MONTO_PEDIDOS_LOCALES, CN_ESTATUS_PEDIDO_LOCALES, CN_OBSERVACIONES_LOCALES, CN_CODIGO_PEDIDOS_LOCALES};
        String[] args = { id_usuario };
        return db.query(TABLA_PEDIDOS_LOCALES,columnas,CN_ID_VENDEDOR_PEDIDOS_LOCALES+"=?",args,null,null,CN_FECHA_PEDIDOS_LOCALES+" DESC"); // Ordenara por fecha
    }

    public Cursor cargarCursorPedidosByID_Cliente(String id_usuario, String id_cliente, String estatus, String modo, String columna)
    {
        //Log.d("pre", "id_usuario: " + id_usuario + ", id_cliente: " + id_cliente + ", estatus: " + estatus);
        String[] columnas = new String[]{CN_ID_PEDIDOS_REAL, CN_ID_VENDEDOR_PEDIDOS, CN_ID_CLIENTE_PEDIDOS, CN_FECHA_PEDIDOS, CN_MONTO_PEDIDOS, CN_ESTATUS_PEDIDO, CN_OBSERVACIONES, CN_CODIGO_PEDIDOS, CN_ID_PEDIDOS_BD};
        String selection = CN_ID_VENDEDOR_PEDIDOS + "=? ";
        ArrayList<String> argumentos = new ArrayList<>();
        argumentos.add(id_usuario);
        String orderby;

        if(!id_cliente.equalsIgnoreCase("0"))
        {
            selection += " AND " + CN_ID_CLIENTE_PEDIDOS + "=? ";
            argumentos.add(id_cliente);
        }
        if(!estatus.equalsIgnoreCase("Todos"))
        {
            selection += " AND " + CN_ESTATUS_PEDIDO + "=? ";
            argumentos.add(estatus);
        }

        if (columna.equals("monto"))
            orderby = "CAST("+columna.toLowerCase()+" AS DECIMAL(15,2)) " + modo;
        else
            orderby = columna.toLowerCase() + " " + modo;

        String[] args = argumentos.toArray(new String[argumentos.size()]); //CN_FECHA_PEDIDOS

        return db.query(TABLA_PEDIDOS, columnas, selection, args, null, null, orderby); // Ordenara por fecha
    }

    public Cursor cargarCursorPedidos_Locales_ByID_Cliente(String id_usuario, String id_cliente, String estatus, String modo, String columna)
    {
        String[] columnas = new String[]{CN_FECHA_PEDIDOS_LOCALES, CN_ID_VENDEDOR_PEDIDOS_LOCALES, CN_ID_CLIENTE_PEDIDOS_LOCALES, CN_FECHA_PEDIDOS_LOCALES, CN_MONTO_PEDIDOS_LOCALES, CN_ESTATUS_PEDIDO_LOCALES, CN_OBSERVACIONES_LOCALES, CN_CODIGO_PEDIDOS_LOCALES, CN_ID_PEDIDOS_LOCALES_BD};
        String selection = CN_ID_VENDEDOR_PEDIDOS_LOCALES + "=? ";
        ArrayList<String> argumentos = new ArrayList<>();
        argumentos.add(id_usuario);
        String orderby;

        if(!id_cliente.equalsIgnoreCase("0"))
        {
            selection += " AND " + CN_ID_CLIENTE_PEDIDOS_LOCALES + "=? ";
            argumentos.add(id_cliente);
        }
        if(!estatus.equalsIgnoreCase("Todos"))
        {
            selection += " AND " + CN_ESTATUS_PEDIDO_LOCALES + "=? ";
            argumentos.add(estatus);
        }

        String[] args = argumentos.toArray(new String[argumentos.size()]); // CN_FECHA_PEDIDOS_LOCALES

        if (columna.equals("monto"))
            orderby = "CAST("+columna.toLowerCase()+" AS DECIMAL(15,2)) " + modo;
        else
            orderby = columna.toLowerCase() + " " + modo;

        Log.d("Cargando_pedido_local", "id_usuario: " + id_usuario + ", id_cliente: " + id_cliente + ", estatus: " + estatus + ", ");

        return db.query(TABLA_PEDIDOS_LOCALES, columnas, selection, args, null, null, orderby); // Ordenara por fecha
    }

    public void insertar_pedido(String id_pedido, String id_vendedor, String id_cliente, String fecha_ingreso, double monto, String estatus_pedido, EditText observaciones, String codigo_pedido, Context contexto)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_ID_PEDIDOS_REAL,id_pedido);
        valores.put(CN_ID_VENDEDOR_PEDIDOS,id_vendedor);
        valores.put(CN_ID_CLIENTE_PEDIDOS, id_cliente);
        valores.put(CN_FECHA_PEDIDOS,fecha_ingreso);
        valores.put(CN_MONTO_PEDIDOS, monto);
        valores.put(CN_ESTATUS_PEDIDO, estatus_pedido);
        valores.put(CN_OBSERVACIONES, String.valueOf(observaciones));
        valores.put(CN_CODIGO_PEDIDOS, codigo_pedido);

        if (db.insert(TABLA_PEDIDOS, null, valores) == -1)
        {
            Log.e("insertar_pedido","Ocurrio un error insertando el pedido");
            Toast.makeText(contexto, "Ocurrio un error insertando el pedido", Toast.LENGTH_SHORT).show();
        }
    }

    public void insertar_pedido(String id_pedido, String id_vendedor, String id_cliente, String fecha_ingreso, double monto, String s, String observaciones, String codigo_pedido)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_ID_PEDIDOS_REAL,id_pedido);
        valores.put(CN_ID_VENDEDOR_PEDIDOS,id_vendedor);
        valores.put(CN_ID_CLIENTE_PEDIDOS,id_cliente);
        valores.put(CN_FECHA_PEDIDOS,fecha_ingreso);
        valores.put(CN_MONTO_PEDIDOS,monto);
        valores.put(CN_ESTATUS_PEDIDO, s);
        valores.put(CN_OBSERVACIONES, observaciones);
        valores.put(CN_CODIGO_PEDIDOS, codigo_pedido);

        if (db.insert(TABLA_PEDIDOS, null, valores) == -1)
            Log.e("insertar_pedido","Ocurrio un error insertando el pedido");
    }

    public long insertar_pedido_local(String id_vendedor, String id_cliente, String fecha_ingreso, double monto, String s, String observaciones, String codigo_pedido)
    {
        long res;
        ContentValues valores = new ContentValues();
        valores.put(CN_ID_VENDEDOR_PEDIDOS_LOCALES,id_vendedor);
        valores.put(CN_ID_CLIENTE_PEDIDOS_LOCALES,id_cliente);
        valores.put(CN_FECHA_PEDIDOS_LOCALES,fecha_ingreso);
        valores.put(CN_MONTO_PEDIDOS_LOCALES,monto);
        valores.put(CN_ESTATUS_PEDIDO_LOCALES, s);
        valores.put(CN_OBSERVACIONES_LOCALES, observaciones);
        valores.put(CN_CODIGO_PEDIDOS_LOCALES, codigo_pedido);

        res = db.insert(TABLA_PEDIDOS_LOCALES, null, valores);

        if (res == -1)
            Log.e("insertar_pedido_local","Ocurrio un error insertando el pedido");
        return res;
    }

    public void insertar_pedido_local_detalles(String id_pedido, String id_productos, String cantidad_pares, String cantidad_bultos, String numeracion, String precio_unitario, String subtotal)
    {
        //Log.d("CONTENIDO","id_pedido: "+id_pedido+", id_productos:"+id_productos+", cantidad_pares:"+cantidad_pares+", cantidad_bultos:"+cantidad_bultos+", numeracion:"+numeracion+", precio_unitario:"+precio_unitario+", subtotal:"+subtotal);
        String sql = "INSERT INTO "+TABLA_PEDIDOS_LOCALES_DETALLES  +" ( "+CN_ID_PEDIDOS_LOCALES_DETALLES_REAL+", "+CN_ID_PEDIDOS_LOCALES_DETALLES_PRODUCTO  +", "+CN_ID_PEDIDOS_LOCALES_DETALLES_PARES  +", "+CN_ID_PEDIDOS_LOCALES_DETALLES_BULTOS   +", "+CN_ID_PEDIDOS_LOCALES_DETALLES_NUMERACION   +", "+CN_ID_PEDIDOS_LOCALES_DETALLES_PRECIO_UNITARIO +", "+CN_ID_PEDIDOS_LOCALES_DETALLES_SUBTOTAL +", "+CN_ID_PEDIDOS_LOCALES_DETALLES_TALLA+") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";
        db.beginTransaction();
        Log.d("INSERTANDO", "PEDIDOS_LOCALES_DETALLES: BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        String[] idproducto = id_productos.split("#");
        String[] pares = cantidad_pares.split("#");
        String[] bultos = cantidad_bultos.split("#");
        String[] numeraciones = numeracion.split("#");
        String[] precios = precio_unitario.split("#");
        String[] subtotales = subtotal.split("#");
        String tallas = "";
        int x = 0;

        for (String anIdproducto : idproducto)
        {
            String[] columnas = new String[]{CN_NOMBRE_PRODUCTO};
            String[] args = {anIdproducto};
            Cursor cursor = db.query(TABLA_PRODUCTOS, columnas, CN_ID_PRODUCTO + "=?", args, null, null, null);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (x == 0)
                    tallas = (cursor.getString(0)).substring(cursor.getString(0).length() - 1);
                else
                    tallas += "#" + (cursor.getString(0)).substring(cursor.getString(0).length() - 1);
                x++;
            }
            cursor.close();
        }

        String[] talla = tallas.split("#");

        for (int i = 0; i < idproducto.length; i++)
        {
            stmt.bindString(1, id_pedido);
            stmt.bindString(2, idproducto[i]);
            stmt.bindString(3, pares[i]);
            stmt.bindString(4, bultos[i]);
            stmt.bindString(5, numeraciones[i]);
            stmt.bindString(6, precios[i]);
            stmt.bindString(7, subtotales[i]);
            stmt.bindString(8, talla[i]);
            stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERTANDO", "(" + idproducto.length + " productos agregados) PEDIDOS_LOCALES_DETALLES: END");
    }

    public void insertar_pedido_detalles(String id_pedido, String id_productos, String cantidad_pares, String cantidad_bultos, String numeracion, String precio_unitario, String subtotal)
    {
        Log.d("CONTENIDO","id_pedido: "+id_pedido+", id_productos:"+id_productos+", cantidad_pares:"+cantidad_pares+", cantidad_bultos:"+cantidad_bultos+", numeracion:"+numeracion+", precio_unitario:"+precio_unitario+", subtotal:"+subtotal);
        String sql = "INSERT INTO "+TABLA_PEDIDOS_DETALLES  +" ( "+CN_ID_PEDIDOS_DETALLES_PEDIDO  +", "+CN_ID_PEDIDOS_DETALLES_PRODUCTO  +", "+CN_ID_PEDIDOS_DETALLES_PARES  +", "+CN_ID_PEDIDOS_DETALLES_BULTOS   +", "+CN_ID_PEDIDOS_DETALLES_NUMERACION   +", "+CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO +", "+CN_ID_PEDIDOS_DETALLES_SUBTOTAL +", "+CN_ID_PEDIDOS_DETALLES_TALLA+") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";
        db.beginTransaction();
        Log.d("INSERT PEDIDOS_DETALLES", "BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        String[] idproducto = id_productos.split("#");
        String[] pares = cantidad_pares.split("#");
        String[] bultos = cantidad_bultos.split("#");
        String[] numeraciones = numeracion.split("#");
        String[] precios = precio_unitario.split("#");
        String[] subtotales = subtotal.split("#");
        String tallas = "";
        int x = 0;

        for (String anIdproducto : idproducto)
        {
            String[] columnas = new String[]{CN_NOMBRE_PRODUCTO};
            String[] args = {anIdproducto};
            Cursor cursor = db.query(TABLA_PRODUCTOS, columnas, CN_ID_PRODUCTO + "=?", args, null, null, null);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (x == 0)
                    tallas = (cursor.getString(0)).substring(cursor.getString(0).length() - 1);
                else
                    tallas += "#" + (cursor.getString(0)).substring(cursor.getString(0).length() - 1);
                x++;
            }
            cursor.close();
        }

        String[] talla = tallas.split("#");

        for (int i = 0; i < idproducto.length; i++)
        {
            stmt.bindString(1, id_pedido);
            stmt.bindString(2, idproducto[i]);
            stmt.bindString(3, pares[i]);
            stmt.bindString(4, bultos[i]);
            stmt.bindString(5, numeraciones[i]);
            stmt.bindString(6, precios[i]);
            stmt.bindString(7, subtotales[i]);
            stmt.bindString(8, talla[i]);
            //Log.d("CONTENIDO", "id_pedido: " + id_pedido + ", id_productos:" + idproducto[i] + ", cantidad_pares:" + pares[i] + ", cantidad_bultos:" + bultos[i] + ", numeracion:" + numeraciones[i] + ", precio_unitario:" + precios[i] + ", subtotal:" + subtotales[i]);
            stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERT PEDIDOS_DETALLES", "END");
    }

    public void insertar_pedido_detalles(JSONArray id_pedido, JSONArray id_productos, JSONArray cantidad_pares, JSONArray cantidad_bultos, JSONArray numeracion, JSONArray precio_unitario, JSONArray subtotal, JSONArray talla) throws JSONException {
        //Log.d("CONTENIDO","id_pedido: "+id_pedido+", id_productos:"+id_productos+", cantidad_pares:"+cantidad_pares+", cantidad_bultos:"+cantidad_bultos+", numeracion:"+numeracion+", precio_unitario:"+precio_unitario+", subtotal:"+subtotal);
        String sql = "INSERT INTO "+TABLA_PEDIDOS_DETALLES  +" ( "+CN_ID_PEDIDOS_DETALLES_PEDIDO  +", "+CN_ID_PEDIDOS_DETALLES_PRODUCTO  +", "+CN_ID_PEDIDOS_DETALLES_PARES  +", "+CN_ID_PEDIDOS_DETALLES_BULTOS   +", "+CN_ID_PEDIDOS_DETALLES_NUMERACION   +", "+CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO +", "+CN_ID_PEDIDOS_DETALLES_SUBTOTAL +", "+CN_ID_PEDIDOS_DETALLES_TALLA+") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";
        db.beginTransaction();
        Log.d("INSERT PEDIDOS_DETALLES", "BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < id_pedido.length(); i++)
        {
            stmt.bindString(1, id_pedido.getString(i));
            stmt.bindString(2, id_productos.getString(i));
            stmt.bindString(3, cantidad_pares.getString(i));
            stmt.bindString(4, cantidad_bultos.getString(i));
            stmt.bindString(5, numeracion.getString(i));
            stmt.bindString(6, precio_unitario.getString(i));
            stmt.bindString(7, subtotal.getString(i));
            stmt.bindString(8, talla.getString(i));
            //Log.d("CONTENIDO", "id_pedido: " + id_pedido + ", id_productos:" + idproducto[i] + ", cantidad_pares:" + pares[i] + ", cantidad_bultos:" + bultos[i] + ", numeracion:" + numeraciones[i] + ", precio_unitario:" + precios[i] + ", subtotal:" + subtotales[i]);
            stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERT PEDIDOS_DETALLES", "END");
    }

    public Cursor cargarCursorProductosPedidos_Detalles(String id_pedido)
    {
        String[] columnas = new String[]{CN_ID_PEDIDOS_DETALLES_PRODUCTO , CN_ID_PEDIDOS_DETALLES_PARES , CN_ID_PEDIDOS_DETALLES_BULTOS , CN_ID_PEDIDOS_DETALLES_NUMERACION , CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO , CN_ID_PEDIDOS_DETALLES_SUBTOTAL};
        String[] args = { id_pedido };
        return db.query(TABLA_PEDIDOS_DETALLES ,columnas,CN_ID_PEDIDOS_DETALLES_PEDIDO +"=?",args,null,null,null);
    }

    public Cursor cargarCursorProductosPedidos_Locales(String id_pedido)
    {
        String[] columnas = new String[]{CN_ID_PEDIDOS_LOCALES_DETALLES_PRODUCTO , CN_ID_PEDIDOS_LOCALES_DETALLES_PARES , CN_ID_PEDIDOS_LOCALES_DETALLES_BULTOS , CN_ID_PEDIDOS_LOCALES_DETALLES_NUMERACION ,CN_ID_PEDIDOS_LOCALES_DETALLES_PRECIO_UNITARIO ,CN_ID_PEDIDOS_LOCALES_DETALLES_SUBTOTAL };
        String[] args = { id_pedido };
        return db.query(TABLA_PEDIDOS_LOCALES_DETALLES ,columnas,CN_ID_PEDIDOS_LOCALES_DETALLES_REAL +"=?",args,null,null,null);
    }

    public Cursor cargarCursorProductosPedidosDetalles_porTalla(String p, String id_pedido)
    {
        String[] columnas = new String[]{CN_ID_PEDIDOS_DETALLES_PRODUCTO, CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO, CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO , CN_ID_PEDIDOS_DETALLES_NUMERACION , CN_ID_PEDIDOS_DETALLES_PARES , CN_ID_PEDIDOS_DETALLES_PRODUCTO, CN_ID_PEDIDOS_DETALLES_BULTOS};
        String[] args = { id_pedido, p };
        return db.query(TABLA_PEDIDOS_DETALLES  ,columnas,CN_ID_PEDIDOS_DETALLES_PEDIDO  +"=? AND "+CN_ID_PEDIDOS_DETALLES_TALLA+"=?",args,null,null,null);
    }

    public Cursor cargarCursorProductosPedidos_Locales()
    {
        //String[] columnas = new String[]{CN_ID_PEDIDOS_DETALLES_PRODUCTO, CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO, CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO , CN_ID_PEDIDOS_DETALLES_NUMERACION , CN_ID_PEDIDOS_DETALLES_PARES , CN_ID_PEDIDOS_DETALLES_PRODUCTO, CN_ID_PEDIDOS_DETALLES_BULTOS};
        return db.query(TABLA_PEDIDOS_LOCALES  ,null,null,null,null,null,null);
    }

    public Cursor cargarCursorProductosPedidos_Locales_Detalle()
    {
        //String[] columnas = new String[]{CN_ID_PEDIDOS_DETALLES_PRODUCTO, CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO, CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO , CN_ID_PEDIDOS_DETALLES_NUMERACION , CN_ID_PEDIDOS_DETALLES_PARES , CN_ID_PEDIDOS_DETALLES_PRODUCTO, CN_ID_PEDIDOS_DETALLES_BULTOS};
        return db.query(TABLA_PEDIDOS_LOCALES_DETALLES   ,null,null,null,null,null,null);
    }

    public Cursor cargarCursorProductosPedidos_Locales_Detalles_porTalla(String p, String id_pedido)
    {
        String[] columnas = new String[]{CN_ID_PEDIDOS_LOCALES_DETALLES_PRODUCTO, CN_ID_PEDIDOS_LOCALES_DETALLES_PRECIO_UNITARIO, CN_ID_PEDIDOS_LOCALES_DETALLES_PRECIO_UNITARIO , CN_ID_PEDIDOS_LOCALES_DETALLES_NUMERACION , CN_ID_PEDIDOS_LOCALES_DETALLES_PARES , CN_ID_PEDIDOS_LOCALES_DETALLES_PRODUCTO, CN_ID_PEDIDOS_LOCALES_DETALLES_BULTOS};
        String[] args = { id_pedido, p };
        return db.query(TABLA_PEDIDOS_LOCALES_DETALLES  ,columnas,CN_ID_PEDIDOS_LOCALES_DETALLES_REAL +"=? AND "+CN_ID_PEDIDOS_LOCALES_DETALLES_TALLA+"=?",args,null,null,null);
    }

    public Cursor cargarCursorPedidos(String id_pedido)
    {
        String[] columnas = new String[]{CN_ESTATUS_PEDIDO,CN_ID_CLIENTE_PEDIDOS,CN_ID_VENDEDOR_PEDIDOS};
        String[] args = { id_pedido };
        return db.query(TABLA_PEDIDOS,columnas,CN_ID_PEDIDOS_REAL+"=?",args,null,null,null);
    }

    public Cursor cargarCursorPedidos_Locales(String id_pedido)
    {
        String[] columnas = new String[]{CN_ESTATUS_PEDIDO_LOCALES ,CN_ID_CLIENTE_PEDIDOS_LOCALES,CN_ID_VENDEDOR_PEDIDOS_LOCALES};
        String[] args = { id_pedido };
        return db.query(TABLA_PEDIDOS_LOCALES,columnas,CN_ID_PEDIDOS_LOCALES_BD +"=?",args,null,null,null);
    }

    public Cursor cargarCursorPedidos_Editar(String talla)
    {
        String[] columnas = new String[]{CN_PRECIO_PRODUCTOS_PEDIDOS_EDITAR, CN_PARES_PRODUCTOS_PEDIDOS_EDITAR, CN_BULTOS_PRODUCTOS_PEDIDOS_EDITAR};
        String[] args = { talla };
        return db.query(TABLA_PRODUCTOS_PEDIDOS_EDITAR,columnas,CN_TIPO_PRODUCTOS_PEDIDOS_EDITAR+"=?",args,null,null,null);
    }

    public Cursor cargarCursorPedidos_Editar2(String talla)
    {
        String[] columnas = new String[]{ CN_ID_PRODUCTO_PEDIDOS_EDITAR , CN_TIPO_PRODUCTOS_PEDIDOS_EDITAR , CN_PRECIO_PRODUCTOS_PEDIDOS_EDITAR , CN_NUMERACION_PRODUCTOS_PEDIDOS_EDITAR ,CN_PARES_PRODUCTOS_PEDIDOS_EDITAR , CN_ID_PRODUCTO_PEDIDOS_EDITAR , CN_BULTOS_PRODUCTOS_PEDIDOS_EDITAR};
        String[] args = { talla };
        return db.query(TABLA_PRODUCTOS_PEDIDOS_EDITAR,columnas,CN_TIPO_PRODUCTOS_PEDIDOS_EDITAR+"=?",args,null,null,null);
    }

    public Cursor cargarCursorPedidos_Editar()
    {
        String[] columnas = new String[]{CN_PRECIO_PRODUCTOS_PEDIDOS_EDITAR, CN_PARES_PRODUCTOS_PEDIDOS_EDITAR, CN_BULTOS_PRODUCTOS_PEDIDOS_EDITAR, CN_ID_PRODUCTO_PEDIDOS_EDITAR};
        return db.query(TABLA_PRODUCTOS_PEDIDOS_EDITAR,columnas,null,null,null,null,null);
    }

    public void actualizar_producto_pedido_cantidad_editar(String id, int newVal)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_BULTOS_PRODUCTOS_PEDIDOS_EDITAR,newVal);
        int res = db.update(TABLA_PRODUCTOS_PEDIDOS_EDITAR, valores, CN_ID_PRODUCTO_PEDIDOS_EDITAR+"=?",new String[]{id});
        Log.d("", "Se actualizo el numero de pares de usuario a " + res + " productos. ID " + id + ", a " + newVal + " pares.");
    }

    public void insertar_producto_pedido_local_editar(String id_pedido)
    {
        String[] columnas = new String[]{CN_ID_PEDIDOS_LOCALES_DETALLES_PRECIO_UNITARIO , CN_ID_PEDIDOS_LOCALES_DETALLES_PARES , CN_ID_PEDIDOS_LOCALES_DETALLES_NUMERACION , CN_ID_PEDIDOS_LOCALES_DETALLES_PRODUCTO , CN_ID_PEDIDOS_LOCALES_DETALLES_BULTOS  };
        String[] args = { id_pedido };
        Cursor res = db.query(TABLA_PEDIDOS_LOCALES_DETALLES,columnas,CN_ID_PEDIDOS_LOCALES_DETALLES_REAL +"=?",args,null,null,null);

        String[] valoresProductoNombre = new String[res.getCount()];
        String[] precio = new String[res.getCount()];
        String[] pares = new String[res.getCount()];
        String[] numeracion = new String[res.getCount()];
        String[] id_producto = new String[res.getCount()];
        String[] nombre_real = new String[res.getCount()];
        String[] bultos = new String[res.getCount()];
        int z = 0;

        for(res.moveToFirst(); !res.isAfterLast(); res.moveToNext())
        {
            precio[z] = res.getString(0);
            pares[z] = res.getString(1);
            numeracion[z] = res.getString(2);
            id_producto[z] = res.getString(3);
            bultos[z] = res.getString(4);
            z++;
        }

        res.close();

        z = 0;

        for (String anId_producto : id_producto)
        {
            String[] columnas2 = new String[]{CN_CODIGO_FABRICANTE_PRODUCTO, CN_NOMBRE_PRODUCTO};
            String[] args2 = {anId_producto};
            Cursor res2 = db.query(TABLA_PRODUCTOS, columnas2, CN_ID_PRODUCTO + "=?", args2, null, null, null);

            for (res2.moveToFirst(); !res2.isAfterLast(); res2.moveToNext())
            {
                valoresProductoNombre[z] = res2.getString(0);
                nombre_real[z] = res2.getString(1);
            }

            z++;
            res2.close();
        }

        String sql = "INSERT INTO "+TABLA_PRODUCTOS_PEDIDOS_EDITAR +" ("+CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_NOMBRE_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_TIPO_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_PRECIO_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_NUMERACION_PRODUCTOS_PEDIDOS_EDITAR  +", "+CN_PARES_PRODUCTOS_PEDIDOS_EDITAR  +", "+CN_ID_PRODUCTO_PEDIDOS_EDITAR+", "+ CN_BULTOS_PRODUCTOS_PEDIDOS_EDITAR +") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        db.beginTransaction();
        Log.d("INSERTANDO PEDIDOS EDIT","BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < valoresProductoNombre.length; i++)
        {
            stmt.bindString(1, valoresProductoNombre[i]);
            stmt.bindString(2, nombre_real[i]);
            stmt.bindString(3, nombre_real[i].substring(nombre_real[i].length() - 1));
            stmt.bindString(4, precio[i]);
            stmt.bindString(5, numeracion[i]);
            stmt.bindString(6, pares[i]);
            stmt.bindString(7, id_producto[i]);
            stmt.bindString(8, bultos[i]);
            stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERTANDO PEDIDOS EDIT","END :"+valoresProductoNombre.length+" insertados");
    }

    public void insertar_producto_pedido_editar(String id_pedido)
    {
        String[] columnas = new String[]{CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO, CN_ID_PEDIDOS_DETALLES_PARES, CN_ID_PEDIDOS_DETALLES_NUMERACION, CN_ID_PEDIDOS_DETALLES_PRODUCTO, CN_ID_PEDIDOS_DETALLES_BULTOS };
        String[] args = { id_pedido };
        Cursor res = db.query(TABLA_PEDIDOS_DETALLES,columnas,CN_ID_PEDIDOS_DETALLES_PEDIDO+"=?",args,null,null,null);

        String[] valoresProductoNombre = new String[res.getCount()];
        String[] precio = new String[res.getCount()];
        String[] pares = new String[res.getCount()];
        String[] numeracion = new String[res.getCount()];
        String[] id_producto = new String[res.getCount()];
        String[] nombre_real = new String[res.getCount()];
        String[] bultos = new String[res.getCount()];
        int z = 0;

        for(res.moveToFirst(); !res.isAfterLast(); res.moveToNext())
        {
            precio[z] = res.getString(0);
            pares[z] = res.getString(1);
            numeracion[z] = res.getString(2);
            id_producto[z] = res.getString(3);
            bultos[z] = res.getString(4);
            z++;
        }

        res.close();

        z = 0;

        for (String anId_producto : id_producto)
        {
            String[] columnas2 = new String[]{CN_CODIGO_FABRICANTE_PRODUCTO, CN_NOMBRE_PRODUCTO};
            String[] args2 = {anId_producto};
            Cursor res2 = db.query(TABLA_PRODUCTOS, columnas2, CN_ID_PRODUCTO + "=?", args2, null, null, null);

            for (res2.moveToFirst(); !res2.isAfterLast(); res2.moveToNext())
            {
                valoresProductoNombre[z] = res2.getString(0);
                nombre_real[z] = res2.getString(1);
            }

            z++;
            res2.close();
        }

        String sql = "INSERT INTO "+TABLA_PRODUCTOS_PEDIDOS_EDITAR +" ("+CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_NOMBRE_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_TIPO_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_PRECIO_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_NUMERACION_PRODUCTOS_PEDIDOS_EDITAR  +", "+CN_PARES_PRODUCTOS_PEDIDOS_EDITAR  +", "+CN_ID_PRODUCTO_PEDIDOS_EDITAR+", "+ CN_BULTOS_PRODUCTOS_PEDIDOS_EDITAR +") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        db.beginTransaction();
        Log.d("INSERTANDO PEDIDOS EDIT","BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < valoresProductoNombre.length; i++)
        {
            stmt.bindString(1, valoresProductoNombre[i]);
            stmt.bindString(2, nombre_real[i]);
            stmt.bindString(3, nombre_real[i].substring(nombre_real[i].length() - 1));
            stmt.bindString(4, precio[i]);
            stmt.bindString(5, numeracion[i]);
            stmt.bindString(6, pares[i]);
            stmt.bindString(7, id_producto[i]);
            stmt.bindString(8, bultos[i]);
            stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERTANDO PEDIDOS EDIT","END:"+valoresProductoNombre.length+" insertados");
    }

    public void insertar_funciones(JSONArray id_usuario, JSONArray home, JSONArray muestrario, JSONArray pedidos, JSONArray clientes, JSONArray perfil, JSONArray opciones, JSONArray estado_cuenta)
    {
        String sql = "INSERT INTO "+TABLA_FUNCIONES  +" ( "+CN_ID_USUARIO_FUNCIONES  +", "+CN_HOMES  +", "+CN_MUESTRARIO  +", "+CN_PEDIDOS   +", "+CN_CLIENTES   +", "+CN_PERFIL +", "+CN_OPCIONES +", "+CN_ESTADO_CUENTA+") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";
        db.beginTransaction();
        Log.d("INSERT TABLA_FUNCIONES", "BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < id_usuario.length(); i++)
        {
            try
            {
                stmt.bindString(1, id_usuario.getString(i));
                stmt.bindString(2, home.getString(i));
                stmt.bindString(3, muestrario.getString(i));
                stmt.bindString(4, pedidos.getString(i));
                stmt.bindString(5, clientes.getString(i));
                stmt.bindString(6, perfil.getString(i));
                stmt.bindString(7, opciones.getString(i));
                stmt.bindString(8, estado_cuenta.getString(i));
                stmt.executeInsert();
                stmt.clearBindings();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERT TABLA_FUNCIONES", "END");
    }

    public void borrar_tabla_producto_pedido_editar()
    {
        db.delete(TABLA_PRODUCTOS_PEDIDOS_EDITAR, null, null);
    }

    public Cursor cargarCursorProductosPedidos_editar()
    {
        //                                                   0                           1                           2                               3                            4                              5                          6   (BULTOS)
        String[] columnas = new String[]{CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS_EDITAR, CN_TIPO_PRODUCTOS_PEDIDOS_EDITAR ,CN_PRECIO_PRODUCTOS_PEDIDOS_EDITAR ,CN_NUMERACION_PRODUCTOS_PEDIDOS_EDITAR ,CN_PARES_PRODUCTOS_PEDIDOS_EDITAR, CN_ID_PRODUCTO_PEDIDOS_EDITAR, CN_BULTOS_PRODUCTOS_PEDIDOS_EDITAR, CN_NOMBRE_PRODUCTOS_PEDIDOS_EDITAR };
        return db.query(TABLA_PRODUCTOS_PEDIDOS_EDITAR,columnas,null,null,null,null,null);
    }

    public void eliminar_producto_pedidoByID_editar(String id)
    {
        int res = db.delete(TABLA_PRODUCTOS_PEDIDOS_EDITAR, CN_ID_PRODUCTO_PEDIDOS_EDITAR + "=?", new String[]{id});
        Log.w("eliminar_producto_pedid", "Se elimino " + res + " producto del pedido.");
    }

    public void actualizar_estado_pedido(String id_pedido, String estatus)
    {
        ContentValues valores = new ContentValues();
        valores.put(CN_ESTATUS_PEDIDO, estatus);

        int res = db.update(TABLA_PEDIDOS, valores, CN_ID_PEDIDOS_REAL + "=?", new String[]{id_pedido});
        Log.d("", "Se actualizo el estado de " + res + " pedido con id: " + id_pedido + ", a: " + estatus);
    }

    public Cursor cargarCursorPedidos_Locales_ID(String id_pedido)
    {
        String[] columnas = new String[]{CN_FECHA_PEDIDOS_LOCALES, CN_ID_VENDEDOR_PEDIDOS_LOCALES, CN_ID_CLIENTE_PEDIDOS_LOCALES, CN_FECHA_PEDIDOS_LOCALES, CN_MONTO_PEDIDOS_LOCALES, CN_ESTATUS_PEDIDO_LOCALES, CN_OBSERVACIONES_LOCALES, CN_CODIGO_PEDIDOS_LOCALES};
        String[] args = { id_pedido };
        return db.query(TABLA_PEDIDOS_LOCALES,columnas,CN_ID_PEDIDOS_LOCALES_BD +"=?",args,null,null,null); // Ordenara por fecha
    }

    public Cursor cargarCursorPedidos_ID(String id_pedido)
    {
        String[] columnas = new String[]{CN_FECHA_PEDIDOS, CN_ID_VENDEDOR_PEDIDOS, CN_ID_CLIENTE_PEDIDOS, CN_FECHA_PEDIDOS, CN_MONTO_PEDIDOS, CN_ESTATUS_PEDIDO, CN_OBSERVACIONES, CN_CODIGO_PEDIDOS};
        String[] args = { id_pedido };
        return db.query(TABLA_PEDIDOS,columnas,CN_ID_PEDIDOS_REAL +"=?",args,null,null,null); // Ordenara por fecha
    }

    public void eliminar_pedidos_locales_ID(String id)
    {
        String[] args = {id};
        long res1 = db.delete(TABLA_PEDIDOS_LOCALES, CN_ID_PEDIDOS_LOCALES_BD+"=?", args);
        long res2 = db.delete(TABLA_PEDIDOS_LOCALES_DETALLES, CN_ID_PEDIDOS_LOCALES_DETALLES_REAL+"=?", args);

        if(res1!=0 || res2!=0)
            Log.d("ELIMINADAS",TABLA_PEDIDOS_LOCALES+" y "+TABLA_PEDIDOS_LOCALES_DETALLES);
    }

    public Cursor cargarCursorPedidos_Locales()
    {
        //String[] columnas = new String[]{CN_ESTATUS_PEDIDO_LOCALES ,CN_ID_CLIENTE_PEDIDOS_LOCALES,CN_ID_VENDEDOR_PEDIDOS_LOCALES};
        return db.query(TABLA_PEDIDOS_LOCALES,null,null,null,null,null,null);
    }

    public Cursor cargarCursorPedidos_Locales_Detalles_ID(String id)
    {
        String[] args = {id};
        return db.query(TABLA_PEDIDOS_LOCALES_DETALLES,null,CN_ID_PEDIDOS_LOCALES_DETALLES_REAL+"=?",args,null,null,null,null);
    }

    public Cursor cargarCursorActualizacionesPedidos_ID(String codigo_usuario)
    {
        String[] args = {codigo_usuario};
        return db.query(TABLA_ACTUALIZACIONES_PEDIDOS,null,CN_ID_PEDIDOS_ACTUALIZACION+"=?",args,null,null,null,null);
    }

    public Cursor cargarCursorActualizacionesFuncionesMoviles_ID(String id)
    {
        String[] args = {id};
        return db.query(TABLA_ACTUALIZACIONES_FUNCIONES,null,CN_ID_FUNCIONES_ACTUALIZACION+"=?",args,null,null,null,null);
    }

    public Cursor cargarCursorActualizacionesClientes_ID (String id)
    {
        String[] args = {id};
        return db.query(TABLA_ACTUALIZACIONES_CLIENTES,null,CN_ID_CLIENTES_ACTUALIZACION+"=?",args,null,null,null,null);
    }

    public void actualizar_usuario(String id, String nombre, String apellido, String estado, String email, String telefono, String fecha)
    {
        Log.d("actualizar_usuario", "INICIO");

        ContentValues valores = new ContentValues();
        valores.put(CN_NOMBRE, nombre);
        valores.put(CN_APELLIDO, apellido);
        valores.put(CN_ESTADO, estado);
        valores.put(CN_EMAIL, email);
        valores.put(CN_TELEFONO, telefono);
        valores.put(CN_USUARIO_FECHA_ACTUALIZACION, fecha);

        String[] args = {id};
        db.update(TABLA_USUARIO, valores, CN_CODIGO_USUARIO+"=?", args);
    }

    public void insertar_producto_pedido_editar_id(ArrayList<String> id_producto)
    {
        String[] columns = new String[]{CN_CODIGO_FABRICANTE_PRODUCTO, CN_PRECIO_PRODUCTO, CN_TALLA_PRODUCTO, CN_NOMBRE_PRODUCTO, CN_ID_PRODUCTO_BULTO};
        //String[] args = id_producto.toArray(new String[id_producto.size()]);
        String[] codigo_fabricante = new String[id_producto.size()];
        String[] precio = new String[id_producto.size()];
        String[] talla_producto = new String[id_producto.size()];
        String[] nombre_producto = new String[id_producto.size()];
        String[] id_bulto_producto = new String[id_producto.size()];
        String[] numeracion = new String[id_producto.size()];
        String[] pares = new String[id_producto.size()];
        Cursor respuesta = null;
        int y = 0;

        for (int i = 0; i < id_producto.size(); i++)
        {
            respuesta = db.query(TABLA_PRODUCTOS,columns,CN_ID_PRODUCTO+"=?",new String[] {id_producto.get(i)},null,null,null,null);

            for(respuesta.moveToFirst(); !respuesta.isAfterLast(); respuesta.moveToNext())
            {
                codigo_fabricante[y] = respuesta.getString(0);
                precio[y] = respuesta.getString(1);
                talla_producto[y] = respuesta.getString(2);
                nombre_producto[y] = respuesta.getString(3);
                id_bulto_producto[y] = respuesta.getString(4);
                y++;
            }
        }

        y = 0;

        for (String anId_bulto_producto : id_bulto_producto)
        {
            String[] columns2 = new String[]{CN_NUMERACION_BULTO, CN_NUMERO_PARES_BULTO};
            respuesta = db.query(TABLA_BULTOS, columns2, CN_ID_BULTO + "=?", new String[]{anId_bulto_producto}, null, null, null, null);

            for (respuesta.moveToFirst(); !respuesta.isAfterLast(); respuesta.moveToNext())
            {
                numeracion[y] = respuesta.getString(0);
                pares[y] = respuesta.getString(1);
                y++;
            }
        }

        if (respuesta != null)
            respuesta.close();

        String sql = "INSERT INTO "+TABLA_PRODUCTOS_PEDIDOS_EDITAR +" ("+CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_NOMBRE_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_TIPO_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_PRECIO_PRODUCTOS_PEDIDOS_EDITAR +", "+CN_NUMERACION_PRODUCTOS_PEDIDOS_EDITAR  +", "+CN_PARES_PRODUCTOS_PEDIDOS_EDITAR  +", "+CN_ID_PRODUCTO_PEDIDOS_EDITAR+", "+ CN_BULTOS_PRODUCTOS_PEDIDOS_EDITAR +") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        db.beginTransaction();
        Log.d("INSERTANDO PEDIDOS EDIT","BEGIN");
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < codigo_fabricante.length; i++)
        {
            stmt.bindString(1, codigo_fabricante[i]);
            stmt.bindString(2, nombre_producto[i]);
            stmt.bindString(3, talla_producto[i]);
            stmt.bindString(4, precio[i]);
            stmt.bindString(5, numeracion[i]);
            stmt.bindString(6, pares[i]);
            stmt.bindString(7, id_producto.get(i));
            stmt.bindString(8, "1"); // bultos[i]
            stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        Log.d("INSERTANDO PEDIDOS EDIT","END:"+codigo_fabricante.length+" insertados");
    }
}