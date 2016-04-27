package paquete.tufanoapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import paquete.database.DBAdapter;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.recycle_bitmap.RecyclingImageView;

public class FragmentGaleria extends Fragment
{
    private static final Bitmap[] imageIDs = new Bitmap[Constantes.NUM_IMG];
    public static int posicion = 0;
    private static RecyclingImageView imageView;
    //public static Gallery gallery;
    private static FrameLayout view;
    private final int DIMENSION_IMG = 150;
    private View rootView;
    private String[] ids, mini_producto_nombre_real;
    private int pos_lista;
    private ListView lista;
    private DBAdapter manager;
    private CheckBox check;
    private File archivo;
    private String id_usuario, codigo_fabricante, nombre_producto, id_actual, precio, color, pares;
    private String idbulto = null, idcolor = null, numeracion = "", rangos = "";
    private boolean permitido;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_galeria, container, false);
        final Context contexto = getActivity();
        manager = new DBAdapter(contexto);

        permitido = Funciones.FuncionalidadPermitida("Muestrario", 1, id_usuario, manager);

        getExtrasVar();
        cargarInformacion();
        initCheckBox();
        defaultCheckBoxState();
        initTextView();
        buscarImagenes();
        initButtons(contexto);
        initImageView();
        createGalleryItems(contexto);
        mainImageListener();
        ArrayList<lista_entrada_galeria> datos = createObjectsGallery();
        initListView(contexto, datos);

        return rootView;
    }

    /**
     * Inicializa el imageView
     */
    private void initImageView()
    {
        imageView = (RecyclingImageView) rootView.findViewById(R.id.imagen_principal);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
    }

    /**
     * Crea los objetos que iran dentro de la galeria, para luego asignarlos.
     *
     * @return Una lista con los elementos de la galeria.
     */
    private ArrayList<lista_entrada_galeria> createObjectsGallery()
    {
        ArrayList<lista_entrada_galeria> datos = new ArrayList<>();

        String[] mini_producto_nombre = new String[ids.length - 1];
        mini_producto_nombre_real = new String[ids.length - 1];
        String[] ids_mini = new String[ids.length - 1];
        int w = 0;

        for (String id : ids)
        //for (int i=0; i<ids.length;i++)
        {
            if (!id.equals(id_actual))
            {
                Cursor cursor9 = manager.buscarProducto(id); // retornara el nombre del producto

                for (cursor9.moveToFirst(); !cursor9.isAfterLast(); cursor9.moveToNext())
                {
                    mini_producto_nombre[w] = cursor9.getString(0);
                    mini_producto_nombre_real[w] = cursor9.getString(1);
                    ids_mini[w] = cursor9.getString(2);
                    w++;
                }

                cursor9.close();
            }
        }
        for (int i = 0; i < ids.length - 1; i++)
        {
            datos.add(new lista_entrada_galeria(mini_producto_nombre[i], ids_mini[i]));
            //Log.e("DATOS.ADD","Producto nombre: "+ mini_producto_nombre[i]+" ID: "+ ids_mini[i]);
        }
        return datos;
    }

    /**
     * Crea el listener de la imagen principal para permitir ampliar la imagen.
     */
    private void mainImageListener()
    {
        final Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                synchronized (this)
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            imageView.setImageBitmap(imageIDs[0]);
                            view = (FrameLayout) rootView.findViewById(R.id.FrameLayout_1);
                            view.setOnTouchListener(new PanAndZoomListener(view, imageView, PanAndZoomListener.Anchor.TOPLEFT));
                        }
                    });

                }
            }
        };
        thread.start();
    }

    /**
     * Crea los items que iran en la galeria a mostrar.
     *
     * @param contexto Contexto de la actividad.
     */
    private void createGalleryItems(final Context contexto)
    {
        LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        childParam1.weight = 1f;
        childParam1.setMargins(10, 5, 10, 5);

        final Gallery_Selected_Item items = new Gallery_Selected_Item(contexto);

        for (final Bitmap imageID : imageIDs)
        {
            //LinearLayout contenedor = new LinearLayout(contexto);
            final RecyclingImageView imagen_gallery = new RecyclingImageView(contexto);
            //imagen_gallery.setLayoutParams(layout_img);
            imagen_gallery.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imagen_gallery.setImageBitmap(imageID);
            //imagen_gallery.setBackground(getResources().getDrawable(R.drawable.gallery_border));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                imagen_gallery.setBackground(getResources().getDrawable(R.drawable.gallery_border, contexto.getTheme()));
            }
            else
            {
                imagen_gallery.setBackground(ContextCompat.getDrawable(contexto, R.drawable.gallery_border));
            }

            imagen_gallery.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    imageView.setImageBitmap(imageID);
                    items.estado_normal();
                    //imagen_gallery.setBackground(getResources().getDrawable(R.drawable.gallery_border_selected));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        imagen_gallery.setBackground(getResources().getDrawable(R.drawable.gallery_border_selected, contexto.getTheme()));
                    }
                    else
                    {
                        imagen_gallery.setBackground(ContextCompat.getDrawable(contexto, R.drawable.gallery_border_selected));
                    }

                    FrameLayout vista = (FrameLayout) rootView.findViewById(R.id.FrameLayout_1);
                    vista.setOnTouchListener(new PanAndZoomListener(vista, imageView, PanAndZoomListener.Anchor.TOPLEFT));
                }
            });

            imagen_gallery.setLayoutParams(childParam1);
            items.agregar(imagen_gallery);
            //myGallery.addView(imagen_gallery);
        }

        items.activar(0);
    }

    /**
     * Busca las imagenes del producto para su uso posterior.
     */
    private void buscarImagenes()
    {
        for (int i = 0; i < Constantes.NUM_IMG; i++)
        {
            int j = i + 1;
            File file = new File(getActivity().getFilesDir(), nombre_producto + "_0" + j + ".jpg");
            Log.d("FragmentGaleria", "Buscando imagen " + nombre_producto + "_0" + j + ".jpg");

            if (file.exists())
            {
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageIDs[i] = myBitmap;
                archivo = file;
                Log.d("FragmentGaleria", "Imagen encontrada..");
            }
        }
    }

    /**
     * Consulta en BD si el producto actual ya habia sido seleccionado, para de esta forma, colocar
     * de forma predeterminada el checkBox activado o desactivado.
     */
    private void defaultCheckBoxState()
    {
        // Si el producto ya esta en la BD local de pedidos, colocare el check como presionado..

        Cursor cursor2 = manager.cargarCursorProductosPedidos();
        for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
        {
            if (cursor2.getString(5).equals(id_actual))
            {
                check.setChecked(true);
                break;
            }
        }

        cursor2.close();
    }

    /**
     * Carga de la BD informacion basica del producto.
     */
    private void cargarInformacion()
    {
        Cursor cursor = manager.buscar_ID_Bulto_Material_Producto(id_actual);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            idbulto = cursor.getString(0);
            //idmaterial = cursor.getString(1);
            idcolor = cursor.getString(2);
        }

        cursor = manager.buscarBulto(idbulto);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            pares = cursor.getString(5);
            numeracion = cursor.getString(4);
            rangos = cursor.getString(3);
        }

        cursor = manager.buscarColor(idcolor);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            color = cursor.getString(1);
        }

        cursor.close();
    }

    /**
     * Inicializa el checkBox
     */
    private void initCheckBox()
    {
        check = (CheckBox) rootView.findViewById(R.id.checkBox_galeria);
        check.setEnabled(permitido);

        final String finalPares = pares;
        final String finalNumeracion = numeracion;
        check.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (check.isChecked())
                    manager.insertar_producto_pedido(codigo_fabricante, precio, finalPares, finalNumeracion, id_actual, nombre_producto);
                else
                    manager.eliminar_producto_pedido(id_actual);
            }
        });
    }

    /**
     * Inicializa el textView
     */
    private void initTextView()
    {
        RobotoTextView tv1 = (RobotoTextView) rootView.findViewById(R.id.galeria_producto_nombre);
        tv1.setText(codigo_fabricante);

        RobotoTextView tv2 = (RobotoTextView) rootView.findViewById(R.id.galeria_producto_color);
        tv2.setText(color);

        RobotoTextView tv3 = (RobotoTextView) rootView.findViewById(R.id.galeria_producto_pares);
        tv3.setText(pares);
    }

    /**
     * Inicializa el listView.
     *
     * @param contexto Contexto de la aplicacion.
     * @param datos    Datos para llenar el listView.
     */
    private void initListView(Context contexto, ArrayList<lista_entrada_galeria> datos)
    {
        lista = (ListView) rootView.findViewById(R.id.ListView_listado);
        lista.setAdapter(new Lista_adaptador(contexto, R.layout.entrada_galeria, datos)
        {
            @Override
            public void onEntrada(Object entrada, final View view, final int posi)
            {
                if (entrada != null)
                {
                    RobotoTextView texto_inferior_entrada = (RobotoTextView) view.findViewById(R.id.textView_inferior);
                    if (texto_inferior_entrada != null)
                        texto_inferior_entrada.setText(((lista_entrada_galeria) entrada).get_textoDebajo());

                    final RecyclingImageView imagen_entrada = (RecyclingImageView) view.findViewById(R.id.imageView_imagen);
                    if (imagen_entrada != null)
                    {
                        final Thread thread2 = new Thread()
                        {
                            @Override
                            public void run()
                            {
                                synchronized (this)
                                {
                                    getActivity().runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            if (posi < mini_producto_nombre_real.length)
                                            {
                                                File file = new File(getActivity().getFilesDir(), mini_producto_nombre_real[posi] + "_01.jpg");

                                                if (file.exists())
                                                {
                                                    imagen_entrada.setImageBitmap(Funciones.decodeSampledBitmapFromResource(file, DIMENSION_IMG, DIMENSION_IMG));
                                                    //imagen_entrada.setImageBitmap(imagenes_mini[pos[0]]);
                                                    //Log.d("LISTO", "IMAGEN CARGADA: " + mini_producto_nombre_real[posi] + "_01.jpg con pos="+posi);
                                                }
                                            }
                                        }
                                    });

                                }
                            }


                        };
                        thread2.start();
                    }
                }
            }
        });

        lista.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                lista.setSelection(pos_lista);
            }
        }, 100L);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> pariente, View view, int position, long id)
            {
                lista_entrada_galeria elegido = (lista_entrada_galeria) pariente.getItemAtPosition(position);

                // A partir del nombre del producto (UNIQUE) buscare su id_usuario y su precio..
                Log.d("onItemClick", "codigo_fabricante: " + elegido.get_textoDebajo() + " id_usuario:" + elegido.get_idProducto());
                Cursor cursor = manager.buscarProductoByName(elegido.get_idProducto());
                String precio_seleccionado = null, cod = null;

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                {
                    precio_seleccionado = cursor.getString(0);
                    cod = cursor.getString(1);
                }

                //manager.cerrar();
                cursor.close();

                // Crea un fragmento y le pasa como argumento el artículo seleccionado
                FragmentGaleria newFragment = new FragmentGaleria();
                Bundle args = new Bundle();
                args.putString("codigo_fabricante", elegido.get_textoDebajo());
                args.putString("nombre_producto", cod);
                args.putStringArray("ids_productos_asociados", ids);
                args.putString("id_actual", elegido.get_idProducto());
                args.putString("precio", precio_seleccionado);
                args.putInt("pos_lista", position);
                args.putString("id", id_usuario);
                newFragment.setArguments(args);
                posicion = 0;

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                //getFragmentManager().beginTransaction().replace(getId(),newFragment).add(getId(), newFragment).addToBackStack(null).commit();

                transaction.replace(getId(), newFragment);
                transaction.addToBackStack("fragment_galeria");

                // Commit the transaction
                transaction.commit();
            }
        });
    }

    /**
     * Obtiene las variables adicionales que fueron pasadas como parametros desde otra actividad.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        codigo_fabricante = bundle.getString("codigo_fabricante");
        nombre_producto = bundle.getString("nombre_producto");
        id_actual = bundle.getString("id_actual");
        precio = bundle.getString("precio");
        ids = bundle.getStringArray("ids_productos_asociados");
        id_usuario = bundle.getString("id");
        pos_lista = bundle.getInt("pos_lista");
        Log.d("codigo_fabricante", codigo_fabricante + ", nombre_producto: " + nombre_producto + ", id_actual: " + id_actual + ", precio: " + precio);

    }

    /**
     * Inicializa los botones de la actividad
     *
     * @param contexto Contexto de la actividad.
     */
    private void initButtons(final Context contexto)
    {
        Button btn_whatsapp = (Button) rootView.findViewById(R.id.btn_whatsapp);

        final String finalColor = color;
        final String finalNumeracion1 = rangos.replace(",", "-");
        btn_whatsapp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                //share directly to WhatsApp and bypass the system picker
                sendIntent.setPackage("com.whatsapp");

                try
                {
                    if (archivo.exists())
                    {
                        Log.d("FragmentGaleria", "Compartiendo imagen " + archivo.getAbsolutePath());

                        Bitmap b = imageIDs[0];
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(contexto.getContentResolver(),
                                b, "Descripcion", null);
                        Uri imageUri2 = Uri.parse(path);

                        String texto = "Producto: " + codigo_fabricante + ", Color: " + finalColor
                                + ", Numeracion: " + finalNumeracion1;

                        sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri2);
                        sendIntent.setType("image/*");
                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(sendIntent);
                        //startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.enviar_hacia)));
                    }
                    else
                    {
                        Toast.makeText(contexto, "Ha ocurrido un error compartiendo la imagen.", Toast.LENGTH_LONG).show();
                    }
                }
                catch (android.content.ActivityNotFoundException ex)
                {
                    Toast.makeText(contexto, "Whatsapp no esta instalado.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestroy()
    {
        System.gc();
        super.onDestroy();
    }
}