package paquete.database.creates;

import static paquete.database.tables.ActualizacionesClientes.CN_FECHA_ACTUALIZACION_CLIENTES;
import static paquete.database.tables.ActualizacionesClientes.CN_ID_ACTUALIZACION_CLIENTES;
import static paquete.database.tables.ActualizacionesClientes.CN_ID_CLIENTES_ACTUALIZACION;
import static paquete.database.tables.ActualizacionesClientes.TABLA_ACTUALIZACIONES_CLIENTES;
import static paquete.database.tables.ActualizacionesFunciones.CN_FECHA_ACTUALIZACION_FUNCIONES;
import static paquete.database.tables.ActualizacionesFunciones.CN_ID_ACTUALIZACION_FUNCIONES;
import static paquete.database.tables.ActualizacionesFunciones.CN_ID_FUNCIONES_ACTUALIZACION;
import static paquete.database.tables.ActualizacionesFunciones.TABLA_ACTUALIZACIONES_FUNCIONES;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_BULTOS;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_COLORES;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_COLORES_BASE;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_LINEAS;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_MATERIALES;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_MODELOS;
import static paquete.database.tables.ActualizacionesGenerales.CN_FECHA_ACTUALIZACION_GENERALES_PRODUCTOS;
import static paquete.database.tables.ActualizacionesGenerales.CN_ID_ACTUALIZACION_GENERALES;
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
import static paquete.database.tables.ActualizacionesPedidos.CN_ID_ACTUALIZACION_PEDIDOS;
import static paquete.database.tables.ActualizacionesPedidos.CN_ID_PEDIDOS_ACTUALIZACION;
import static paquete.database.tables.ActualizacionesPedidos.TABLA_ACTUALIZACIONES_PEDIDOS;
import static paquete.database.tables.ActualizacionesProductos.CN_ACTUALIZACION_IMAGEN_NUMERO;
import static paquete.database.tables.ActualizacionesProductos.CN_FECHA_ACTUALIZACION_PRODUCTO;
import static paquete.database.tables.ActualizacionesProductos.CN_ID_ACTUALIZACION;
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
import static paquete.database.tables.ColoresBase.ID_COLOR_BASE;
import static paquete.database.tables.ColoresBase.NOMBRE_COLOR_BASE;
import static paquete.database.tables.ColoresBase.TABLA_COLORES_BASE;
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
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_BD;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_BULTOS;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_NUMERACION;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_PARES;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_PRECIO_UNITARIO;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_PRODUCTO;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_REAL;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_SUBTOTAL;
import static paquete.database.tables.PedidosLocalesDetalles.CN_ID_PEDIDOS_LOCALES_DETALLES_TALLA;
import static paquete.database.tables.PedidosLocalesDetalles.TABLA_PEDIDOS_LOCALES_DETALLES;
import static paquete.database.tables.PrimerLogin.CN_ID_PRIMER_LOGIN;
import static paquete.database.tables.PrimerLogin.TABLA_PRIMER_LOGIN;
import static paquete.database.tables.ProductoSeleccionado.CN_ID_TABLA_PRODUCTO_SELECCIONADO;
import static paquete.database.tables.ProductoSeleccionado.CN_SELECCIONADO;
import static paquete.database.tables.ProductoSeleccionado.TABLA_PRODUCTO_SELECCIONADO;
import static paquete.database.tables.Productos.CN_CODIGO_FABRICANTE_PRODUCTO;
import static paquete.database.tables.Productos.CN_DESTACADO_PRODUCTO;
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
import static paquete.database.tables.Productos.ID_COLOR_BASE_PRODUCTO;
import static paquete.database.tables.Productos.TABLA_PRODUCTOS;
import static paquete.database.tables.ProductosPedidos.CN_BULTOS_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_ID_PRODUCTO_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_ID_TABLA_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_NOMBRE_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_NUMERACION_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_PARES_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_PRECIO_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.CN_TIPO_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidos.TABLA_PRODUCTOS_PEDIDOS;
import static paquete.database.tables.ProductosPedidosEditar.CN_BULTOS_PRODUCTOS_PEDIDOS_EDITAR;
import static paquete.database.tables.ProductosPedidosEditar.CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS_EDITAR;
import static paquete.database.tables.ProductosPedidosEditar.CN_ID_PRODUCTO_PEDIDOS_EDITAR;
import static paquete.database.tables.ProductosPedidosEditar.CN_ID_TABLA_PRODUCTOS_PEDIDOS_EDITAR;
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

/**
 * Desarrollado por Gerson el 6/4/2016.
 * <p/>
 * Clase que agrupa las sentencias con las cuales se crearan las tablas en la BD. Local
 *
 * @author Gerson Figuera.C
 */
public class CreateSentences
{
    public static final String CREAR_TABLA_PRODUCTO_SELECCIONADO = "CREATE TABLE " + TABLA_PRODUCTO_SELECCIONADO + " (" + CN_ID_TABLA_PRODUCTO_SELECCIONADO + " INTEGER PRIMARY KEY," + CN_SELECCIONADO + " TEXT NOT NULL);";
    public static final String CREAR_TABLA_BULTOS = "CREATE TABLE " + TABLA_BULTOS + " (" + CN_ID_BULTO_BASE + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_BULTO + " INTEGER NOT NULL," + CN_NOMBRE_BULTO + " TEXT NOT NULL," + CN_TIPO_BULTO + " TEXT NOT NULL," + CN_RANGO_TALLAS_BULTO + " TEXT NOT NULL," + CN_NUMERACION_BULTO + " TEXT NOT NULL," + CN_NUMERO_PARES_BULTO + " INTEGER NOT NULL);";
    public static final String CREAR_TABLA_MATERIALES = "CREATE TABLE " + TABLA_MATERIALES + " (" + CN_ID_MATERIAL_BASE + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_MATERIAL + " INTEGER NOT NULL," + CN_NOMBRE_MATERIAL + " TEXT NOT NULL," + CN_ID_MATERIAL_COLOR + " INTEGER NOT NULL," + CN_PREFIJO_MATERIAL + " TEXT NOT NULL," + CN_ID_COLOR_BASE_MATERIAL + " INTEGER NOT NULL);";
    public static final String CREAR_TABLA_COLORES = "CREATE TABLE " + TABLA_COLORES + " (" + CN_ID_COLOR_BASE + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_COLOR + " INTEGER NOT NULL," + CN_NOMBRE_COLOR + " TEXT NOT NULL);";
    public static final String CREAR_TABLA_PEDIDOS = "CREATE TABLE " + TABLA_PEDIDOS + " (" + CN_ID_PEDIDOS_BD + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CN_ID_PEDIDOS_REAL + " INTEGER NOT NULL ," + CN_CODIGO_PEDIDOS + " TEXT NOT NULL, " + CN_ID_VENDEDOR_PEDIDOS + " INTEGER NOT NULL , " + CN_ID_CLIENTE_PEDIDOS + " INTEGER NOT NULL  , " + CN_FECHA_PEDIDOS + " TEXT NOT NULL , " + CN_MONTO_PEDIDOS + " TEXT NOT NULL , " + CN_ESTATUS_PEDIDO + " TEXT NOT NULL , " + CN_OBSERVACIONES + " TEXT NOT NULL); ";
    public static final String CREAR_TABLA_PEDIDOS_DETALLES = "CREATE TABLE " + TABLA_PEDIDOS_DETALLES + " (" + CN_ID_PEDIDOS_DETALLES_BD + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CN_ID_PEDIDOS_DETALLES_PEDIDO + " INTEGER NOT NULL , " + CN_ID_PEDIDOS_DETALLES_PRODUCTO + " INTEGER NOT NULL  , " + CN_ID_PEDIDOS_DETALLES_PARES + " INTEGER NOT NULL , " + CN_ID_PEDIDOS_DETALLES_BULTOS + " INTEGER NOT NULL , " + CN_ID_PEDIDOS_DETALLES_NUMERACION + " TEXT NOT NULL , " + CN_ID_PEDIDOS_DETALLES_PRECIO_UNITARIO + " TEXT NOT NULL," + CN_ID_PEDIDOS_DETALLES_SUBTOTAL + " TEXT NOT NULL," + CN_ID_PEDIDOS_DETALLES_TALLA + " TEXT NOT NULL); ";
    public static final String CREAR_TABLA_PEDIDOS_LOCALES = "CREATE TABLE " + TABLA_PEDIDOS_LOCALES + " (" + CN_ID_PEDIDOS_LOCALES_BD + " INTEGER PRIMARY KEY AUTOINCREMENT ," + CN_CODIGO_PEDIDOS_LOCALES + " TEXT NOT NULL, " + CN_ID_VENDEDOR_PEDIDOS_LOCALES + " INTEGER NOT NULL , " + CN_ID_CLIENTE_PEDIDOS_LOCALES + " INTEGER NOT NULL  , " + CN_FECHA_PEDIDOS_LOCALES + " TEXT NOT NULL , " + CN_MONTO_PEDIDOS_LOCALES + " TEXT NOT NULL , " + CN_ESTATUS_PEDIDO_LOCALES + " TEXT NOT NULL , " + CN_OBSERVACIONES_LOCALES + " TEXT NOT NULL); ";
    public static final String CREAR_TABLA_PEDIDOS_LOCALES_DETALLES = "CREATE TABLE " + TABLA_PEDIDOS_LOCALES_DETALLES + " (" + CN_ID_PEDIDOS_LOCALES_DETALLES_BD + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CN_ID_PEDIDOS_LOCALES_DETALLES_REAL + " INTEGER NOT NULL , " + CN_ID_PEDIDOS_LOCALES_DETALLES_PRODUCTO + " INTEGER NOT NULL  , " + CN_ID_PEDIDOS_LOCALES_DETALLES_PARES + " INTEGER NOT NULL , " + CN_ID_PEDIDOS_LOCALES_DETALLES_BULTOS + " INTEGER NOT NULL , " + CN_ID_PEDIDOS_LOCALES_DETALLES_NUMERACION + " TEXT NOT NULL , " + CN_ID_PEDIDOS_LOCALES_DETALLES_PRECIO_UNITARIO + " TEXT NOT NULL," + CN_ID_PEDIDOS_LOCALES_DETALLES_SUBTOTAL + " TEXT NOT NULL," + CN_ID_PEDIDOS_LOCALES_DETALLES_TALLA + " TEXT NOT NULL); ";
    public static final String CREAR_TABLA_CLIENTES = "CREATE TABLE " + TABLA_CLIENTES + " (" + CN_ID_TABLA_CLIENTES + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_CLIENTES + " INTEGER NOT NULL," + CN_ID_VENDEDOR + " INTEGER NOT NULL," + CN_ID_RAZON_SOCIAL + " TEXT UNIQUE NOT NULL," + CN_ID_RIF + " TEXT UNIQUE NOT NULL," + CN_ID_DIRECCION + " TEXT NOT NULL," + CN_ID_TELEFONO + " TEXT NOT NULL," + CN_ID_ESTADO + " TEXT NOT NULL," + CN_ID_EMAIL + " TEXT NOT NULL," + CN_ID_FECHA_INGRESO + " TEXT NOT NULL);";
    public static final String CREAR_TABLA_PRODUCTOS_PEDIDOS = "CREATE TABLE " + TABLA_PRODUCTOS_PEDIDOS + " (" + CN_ID_TABLA_PRODUCTOS_PEDIDOS + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_PRODUCTO_PEDIDOS + " INTEGER NOT NULL ," + CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS + " TEXT NOT NULL, " + CN_NOMBRE_PRODUCTOS_PEDIDOS + " TEXT NOT NULL, " + CN_TIPO_PRODUCTOS_PEDIDOS + " TEXT NOT NULL, " + CN_PRECIO_PRODUCTOS_PEDIDOS + " TEXT NOT NULL," + CN_NUMERACION_PRODUCTOS_PEDIDOS + " TEXT NOT NULL," + CN_PARES_PRODUCTOS_PEDIDOS + " TEXT NOT NULL," + CN_BULTOS_PRODUCTOS_PEDIDOS + " INTEGER NOT NULL );";
    public static final String CREAR_TABLA_PRODUCTOS_PEDIDOS_EDITAR = "CREATE TABLE " + TABLA_PRODUCTOS_PEDIDOS_EDITAR + " (" + CN_ID_TABLA_PRODUCTOS_PEDIDOS_EDITAR + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_PRODUCTO_PEDIDOS_EDITAR + " INTEGER NOT NULL ," + CN_COD_FABRICANTE_PRODUCTOS_PEDIDOS_EDITAR + " TEXT NOT NULL, " + CN_NOMBRE_PRODUCTOS_PEDIDOS_EDITAR + " TEXT NOT NULL, " + CN_TIPO_PRODUCTOS_PEDIDOS_EDITAR + " TEXT NOT NULL, " + CN_PRECIO_PRODUCTOS_PEDIDOS_EDITAR + " TEXT NOT NULL," + CN_NUMERACION_PRODUCTOS_PEDIDOS_EDITAR + " TEXT NOT NULL," + CN_PARES_PRODUCTOS_PEDIDOS_EDITAR + " TEXT NOT NULL," + CN_BULTOS_PRODUCTOS_PEDIDOS_EDITAR + " INTEGER NOT NULL );";
    public static final String CREAR_TABLA_USUARIO = "CREATE TABLE " + TABLA_USUARIO + " (" + CN_ID_USUARIO + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_CODIGO_USUARIO + " INTEGER UNIQUE NOT NULL," + CN_CEDULA + " TEXT NOT NULL," + CN_NOMBRE + " TEXT NOT NULL," + CN_APELLIDO + " TEXT NOT NULL," + CN_TELEFONO + " TEXT NOT NULL, " + CN_ESTADO + " TEXT NOT NULL, " + CN_EMAIL + " TEXT NOT NULL," + CN_LINEAS_DESHABILITADAS + " TEXT," + CN_MODELOS_DESHABILITADOS + " TEXT," + CN_PRODUCTOS_DESHABILITADOS + " TEXT," + CN_PASSWORD + " TEXT NOT NULL," + CN_USUARIO_SALT + " TEXT NOT NULL, " + CN_USUARIO_FECHA_ACTUALIZACION + " TEXT NOT NULL);";
    public static final String CREAR_TABLA_LINEA = "CREATE TABLE " + TABLA_LINEAS + " (" + CN_ID_LINEA_BASE + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_LINEA + " INTEGER UNIQUE NOT NULL," + CN_NOMBRE_LINEA + " TEXT NOT NULL, " + CN_TALLA_LINEA + " TEXT NOT NULL);";
    public static final String CREAR_TABLA_MODELO = "CREATE TABLE " + TABLA_MODELOS + " (" + CN_ID_MODELO_BASE + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_MODELO + " INTEGER UNIQUE NOT NULL," + CN_ID_LINEA_MODELO + " TEXT NOT NULL," + CN_NOMBRE_MODELO + " TEXT NOT NULL);";
    public static final String CREAR_TABLA_PRODUCTO = "CREATE TABLE " + TABLA_PRODUCTOS + " (" + CN_ID_PRODUCTO_BASE + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_PRODUCTO + " INTEGER UNIQUE NOT NULL," + CN_ID_PRODUCTO_MODELO + " TEXT NOT NULL," + CN_ID_PRODUCTO_LINEA + " TEXT NOT NULL," + CN_ID_PRODUCTO_BULTO + " TEXT NOT NULL," + CN_ID_PRODUCTO_MATERIAL + " TEXT NOT NULL, " + CN_ID_PRODUCTO_COLOR + " TEXT NOT NULL, " + ID_COLOR_BASE_PRODUCTO + " TEXT NOT NULL, " + CN_CODIGO_FABRICANTE_PRODUCTO + " TEXT NOT NULL," + CN_PRECIO_PRODUCTO + " TEXT NOT NULL," + CN_NOMBRE_PRODUCTO + " TEXT NOT NULL," + CN_TALLA_PRODUCTO + " TEXT NOT NULL," + CN_DESTACADO_PRODUCTO + " INTEGER NOT NULL DEFAULT 0);";
    public static final String CREAR_TABLA_FUNCIONES = "CREATE TABLE " + TABLA_FUNCIONES + " (" + CN_ID_TABLA_FUNCIONES + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CN_ID_USUARIO_FUNCIONES + " INTEGER UNIQUE NOT NULL, " + CN_HOMES + " TEXT NOT NULL, " + CN_MUESTRARIO + " TEXT NOT NULL, " + CN_PEDIDOS + " TEXT NOT NULL, " + CN_CLIENTES + " TEXT NOT NULL, " + CN_PERFIL + " TEXT NOT NULL, " + CN_OPCIONES + " TEXT NOT NULL, " + CN_ESTADO_CUENTA + " TEXT NOT NULL);";
    public static final String CREAR_TABLA_ACTUALIZACIONES_GENERALES = "CREATE TABLE " + TABLA_ACTUALIZACIONES_GENERALES + " (" + CN_ID_ACTUALIZACION_GENERALES + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_FECHA_ACTUALIZACION_GENERALES_LINEAS + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20'," + CN_FECHA_ACTUALIZACION_GENERALES_MODELOS + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20'," + CN_FECHA_ACTUALIZACION_GENERALES_PRODUCTOS + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20'," + CN_FECHA_ACTUALIZACION_GENERALES_MATERIALES + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20' ," + CN_FECHA_ACTUALIZACION_GENERALES_COLORES + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20' ," + CN_FECHA_ACTUALIZACION_GENERALES_BULTOS + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20' ," + CN_FECHA_ACTUALIZACION_GENERALES_COLORES_BASE + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20');";
    public static final String CREAR_TABLA_ACTUALIZACIONES_LINEAS = "CREATE TABLE " + TABLA_ACTUALIZACIONES_LINEAS + " (" + CN_ID_ACTUALIZACION_LINEAS + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_LINEA_ACTUALIZACION_LINEAS + " INTEGER NOT NULL UNIQUE," + CN_FECHA_ACTUALIZACION_LINEAS + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20');";
    public static final String CREAR_TABLA_ACTUALIZACIONES_MODELOS = "CREATE TABLE " + TABLA_ACTUALIZACIONES_MODELOS + " (" + CN_ID_ACTUALIZACION_MODELOS + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_MODELO_ACTUALIZACION_MODELOS + " INTEGER NOT NULL UNIQUE," + CN_FECHA_ACTUALIZACION_MODELOS + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20');";
    public static final String CREAR_TABLA_ACTUALIZACIONES_PRODUCTOS = "CREATE TABLE " + TABLA_ACTUALIZACIONES_PRODUCTOS + " (" + CN_ID_ACTUALIZACION + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_ACTUALIZACION_PRODUCTO + " INTEGER NOT NULL," + CN_ACTUALIZACION_IMAGEN_NUMERO + " INTEGER NOT NULL," + CN_FECHA_ACTUALIZACION_PRODUCTO + " TEXT NOT NULL);";
    public static final String CREAR_TABLA_ACTUALIZACIONES_PEDIDOS = "CREATE TABLE " + TABLA_ACTUALIZACIONES_PEDIDOS + " (" + CN_ID_ACTUALIZACION_PEDIDOS + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_PEDIDOS_ACTUALIZACION + " INTEGER NOT NULL UNIQUE," + CN_FECHA_ACTUALIZACION_PEDIDOS + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20');";
    public static final String CREAR_TABLA_ACTUALIZACIONES_CLIENTES = "CREATE TABLE " + TABLA_ACTUALIZACIONES_CLIENTES + " (" + CN_ID_ACTUALIZACION_CLIENTES + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_CLIENTES_ACTUALIZACION + " INTEGER NOT NULL UNIQUE," + CN_FECHA_ACTUALIZACION_CLIENTES + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20');";
    public static final String CREAR_TABLA_ACTUALIZACIONES_FUNCIONES = "CREATE TABLE " + TABLA_ACTUALIZACIONES_FUNCIONES + " (" + CN_ID_ACTUALIZACION_FUNCIONES + " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_ID_FUNCIONES_ACTUALIZACION + " INTEGER NOT NULL UNIQUE," + CN_FECHA_ACTUALIZACION_FUNCIONES + " TEXT NOT NULL DEFAULT '2000-02-06 16:47:20');";
    public static final String CREAR_TABLA_PRIMER_LOGIN = "CREATE TABLE " + TABLA_PRIMER_LOGIN + " (" + CN_ID_PRIMER_LOGIN + " INTEGER PRIMARY KEY AUTOINCREMENT);";
    public static final String CREAR_TABLA_COLORES_BASE = "CREATE TABLE " + TABLA_COLORES_BASE + " (" + ID_COLOR_BASE + " INTEGER PRIMARY KEY AUTOINCREMENT," + NOMBRE_COLOR_BASE + " TEXT NOT NULL);";
}