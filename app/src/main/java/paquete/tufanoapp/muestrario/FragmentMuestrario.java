package paquete.tufanoapp.muestrario;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.devspark.robototextview.util.RobotoTextViewUtils;
import com.devspark.robototextview.util.RobotoTypefaceManager;
import com.devspark.robototextview.widget.RobotoTextView;

import java.io.File;

import paquete.database.DBAdapter;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.horizontal_list_view.HorizontalListView;
import paquete.recycle_bitmap.RecyclingImageView;
import paquete.tufanoapp.R;
import paquete.tufanoapp.galeria.FragmentGaleria;
import paquete.tufanoapp.imageAlphaClass;

public class FragmentMuestrario extends Fragment
{
    private String[] ids_lineas, nombres_lineas;
    private View   rootView;
    private String ci = null, id;
    private Context         contexto;
    private DBAdapter       manager;
    private imageAlphaClass imagenAlpha;
    private String id_linea_seleccionado = "", id_modelo_seleccionado = "";

    public FragmentMuestrario()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        onDestroy();

        rootView = inflater.inflate(R.layout.fragment_muestrario, container, false);
        imagenAlpha = new imageAlphaClass();
        contexto = getActivity();
        manager = new DBAdapter(contexto);

        getExtrasVar();
        seleccionarProductoPorDefecto();

        return rootView;
    }

    /**
     * De haber algun producto seleccionado previamente, este metodo lo selecciona automaticamente,
     * este caso ocurre cuando entramos a la galeria de algun producto y nos devolvemos al muestrario,
     * al devolvernos, este metodo volvera a seleccionar por defecto el producto anteriormente
     * indicado. En caso de que haya producto previamente seleccionado, solo se abre la primera
     * lista horizontal de lineas.
     */
    private void seleccionarProductoPorDefecto()
    {
        Cursor cursor0 = manager.cargarProductoSeleccionado();
        String data    = ""; //id linea y modelo

        for (cursor0.moveToFirst(); !cursor0.isAfterLast(); cursor0.moveToNext())
        {
            data = cursor0.getString(0);
        }

        cursor0.close();

        if (!data.equals(""))
        {
            String[] ids = data.split("&");
            String   res = (manager.cargarCursorLineas(ci));

            if (!res.equals(""))
            {
                String[] lineas = res.split("\\|");
                ids_lineas = new String[lineas.length];
                nombres_lineas = new String[lineas.length];

                for (int i = 0; i < lineas.length; i++)
                {
                    String[] c = lineas[i].split("&");
                    ids_lineas[i] = c[0];
                    nombres_lineas[i] = c[1];
                }
            }

            id_linea_seleccionado = ids[0];
            id_modelo_seleccionado = ids[1];

            HorizontalListView listview = (HorizontalListView) rootView.findViewById(R.id.listview);
            listview.setAdapter(new HAdapter());

            HorizontalListView listview2 = (HorizontalListView) rootView.findViewById(R.id.listview2);
            listview2.setAdapter(new HAdapter2(ids[0]));

            HorizontalListView listview3 = (HorizontalListView) rootView.findViewById(R.id.listview3);
            listview3.setVisibility(View.VISIBLE);
            listview3.setAdapter(new HAdapter3(ids[1], ids[0]));
        }
        else
        {
            //Log.d("RECUPERAR FRAGMENTS", "NO HAY DATA");

            //DBAdapter manager = new DBAdapter(contexto);
            String res = (manager.cargarCursorLineas(ci));
            if (!res.equals(""))
            {
                String[] lineas = res.split("\\|");
                ids_lineas = new String[lineas.length];
                nombres_lineas = new String[lineas.length];

                for (int i = 0; i < lineas.length; i++)
                {
                    String[] c = lineas[i].split("&");
                    ids_lineas[i] = c[0];
                    nombres_lineas[i] = c[1];
                }
            }
            //manager.cerrar();

            HorizontalListView listview = (HorizontalListView) rootView.findViewById(R.id.listview);
            listview.setAdapter(new HAdapter());
        }
    }

    /**
     * Obtiene las variables adicionales que fueron pasadas como parametros desde otra actividad.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        ci = bundle.getString("cedula");
        id = bundle.getString("id_usuario");
    }

    @Override
    public void onDestroy()
    {
        System.gc();
        super.onDestroy();
    }

    /**
     * Inicializa la 1era lista horizontal de las lineas
     */
    private class HAdapter extends BaseAdapter
    {

        private final View.OnClickListener mOnButtonClicked = new View.OnClickListener()
        {

            public void onClick(View v)
            {

                // Al hacer click en alguna linea, se abren los modelos correspondientes
                Object r        = v.getTag();
                String valor2[] = String.valueOf(r).split("&");

                imageAlphaClass.ocultar_lineas();
                RecyclingImageView bt = (RecyclingImageView) v;
                bt.setAlpha(1f);

                HorizontalListView listview2 = (HorizontalListView) rootView.findViewById(R.id.listview2);
                listview2.setAdapter(new HAdapter2(valor2[0]));

                HorizontalListView listview3 = (HorizontalListView) rootView.findViewById(R.id.listview3);
                listview3.setVisibility(View.INVISIBLE);
            }
        };

        public HAdapter()
        {
            super();
        }

        public int getCount()
        {
            return ids_lineas.length;
        }

        public Object getItem(int position)
        {
            return null;
        }

        public long getItemId(int position)
        {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent)
        {
            //View retval;

            if (convertView == null)
            {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, parent, false);
            }

            //retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);
            RobotoTextView title = (RobotoTextView) convertView.findViewById(R.id.title);
            Typeface typeface = RobotoTypefaceManager.obtainTypeface(
                    contexto,
                    RobotoTypefaceManager.FontFamily.ROBOTO,
                    RobotoTypefaceManager.TextWeight.MEDIUM,
                    RobotoTypefaceManager.TextStyle.NORMAL);
            RobotoTextViewUtils.setTypeface(title, typeface);
            title.setText(nombres_lineas[position]); // nombre de la linea

            /*TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(nombres_lineas[position]); // nombre de la linea
            title.setTypeface(null, Typeface.BOLD);
            title.setTextColor(Color.BLACK);*/

            RecyclingImageView imagen = (RecyclingImageView) convertView.findViewById(R.id.image);
            File               file   = new File(contexto.getFilesDir(), nombres_lineas[position] + ".jpg");

            imagen.setOnClickListener(mOnButtonClicked);

            if (file.exists())
            {
                //Log.d("Buscando imagen", "Encontrada");
                imagen.setImageBitmap(Funciones.decodeSampledBitmapFromResource(file, 130, 130));
                imagenAlpha.agregar_linea(imagen);
                if (!id_linea_seleccionado.equals(ids_lineas[position]))
                    imagen.setAlpha(Constantes.IMAGE_ALPHA);
            }

            imagen.setTag(ids_lineas[position] + "&" + nombres_lineas[position]);

            return convertView;
        }
    }

    /**
     * Inicializa la 2da lista horizontal de los modelos
     */
    private class HAdapter2 extends BaseAdapter
    {

        private final String id_linea_buscador;
        private final View.OnClickListener mOnButtonClicked = new View.OnClickListener()
        {
            public void onClick(View v)
            {

                // Al hacer click en algun modelo, se abren los productos asociados
                Object r        = v.getTag();
                String valor2[] = String.valueOf(r).split("&");

                imageAlphaClass.ocultar_modelos();
                RecyclingImageView bt = (RecyclingImageView) v;
                bt.setAlpha(1f);

                /*String lin2 = valor2[1];

                TextView tv = (TextView) rootView.findViewById(R.id.textView8);
                tv.setText(lin2);*/

                HorizontalListView listview3 = (HorizontalListView) rootView.findViewById(R.id.listview3);
                listview3.setVisibility(View.VISIBLE);
                listview3.setAdapter(new HAdapter3(valor2[0], id_linea_buscador));
            }
        };
        String[] valores_modelo_id     = new String[0];
        String[] valores_modelo_nombre = new String[0];
        String[] valores_modelo_img    = new String[0];

        public HAdapter2(String s)
        {
            super();
            Log.i("HAdapter2", s);
            id_linea_buscador = s;
            String res = (manager.cargarCursorModelos_Lineas(id_linea_buscador, ci));
            if (!res.equals(""))
            {
                String[] modelos = res.split("\\|");
                valores_modelo_id = new String[modelos.length];
                valores_modelo_nombre = new String[modelos.length];
                valores_modelo_img = new String[modelos.length];

                for (int i = 0; i < modelos.length; i++)
                {
                    String[] c = modelos[i].split("&");
                    valores_modelo_id[i] = c[0];
                    valores_modelo_nombre[i] = c[1];
                    valores_modelo_img[i] = c[2];
                }
            }
            else
            {
                Toast.makeText(contexto, "No hay modelos a mostrar", Toast.LENGTH_LONG).show();
                Log.d("HAdapter2", "No hay modelos a mostrar.");
            }
            //manager.cerrar();
        }

        public int getCount()
        {
            return valores_modelo_id.length;
        }

        public Object getItem(int position)
        {
            return null;
        }

        public long getItemId(int position)
        {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, parent, false);
            }

            //View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);

            RobotoTextView title = (RobotoTextView) convertView.findViewById(R.id.title);
            Typeface typeface = RobotoTypefaceManager.obtainTypeface(
                    contexto,
                    RobotoTypefaceManager.FontFamily.ROBOTO,
                    RobotoTypefaceManager.TextWeight.MEDIUM,
                    RobotoTypefaceManager.TextStyle.NORMAL);
            RobotoTextViewUtils.setTypeface(title, typeface);
            title.setText(valores_modelo_nombre[position]); // nombre del modelo

            /*TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(valores_modelo_nombre[position]);
            title.setTypeface(null, Typeface.BOLD);*/

            RecyclingImageView imagen = (RecyclingImageView) convertView.findViewById(R.id.image);

            File file = new File(contexto.getFilesDir(), valores_modelo_nombre[position] + ".jpg");
            imagen.setOnClickListener(mOnButtonClicked);
            //Log.d("Buscando imagen", valores_modelo_nombre[position] + ".jpg");

            if (file.exists())
            {
                //Log.d("Buscando imagen", "Encontrada");
                imagen.setImageBitmap(Funciones.decodeSampledBitmapFromResource(file, 130, 130));
                imagenAlpha.agregar_modelo(imagen);
                if (!id_modelo_seleccionado.equals(valores_modelo_id[position]))
                    imagen.setAlpha(Constantes.IMAGE_ALPHA);
            }

            imagen.setTag(valores_modelo_id[position] + "&" + valores_modelo_nombre[position]); // aca le pondre el id_modelo (variable global)

            return convertView;
        }

    }

    /**
     * Inicializa la 3era lista horizontal de los productos
     */
    private class HAdapter3 extends BaseAdapter
    {

        private final String id_linea_buscador;
        private final String id_modelo_buscador;
        String[] ids_producto    = new String[0];
        String[] codigo_producto = new String[0];
        String[] nombre_producto = new String[0];
        String[] color_producto  = new String[0];
        String[] precios         = new String[0];
        private final View.OnClickListener mOnButtonClicked = new View.OnClickListener()
        {

            public void onClick(View v)
            {

                // Al hacer click en algun producto, se abren la galeria asociados
                Object r = v.getTag();
                imageAlphaClass.ocultar_productos();
                RecyclingImageView bt = (RecyclingImageView) v;
                bt.setAlpha(1f);

                String valor2[] = String.valueOf(r).split("&");
                String pos      = valor2[2];

                manager.insertar_producto_seleccionado(id_linea_buscador, id_modelo_buscador);

                // Crea un fragmento y le pasa como argumento el artículo seleccionado
                FragmentGaleria newFragment = new FragmentGaleria();
                Bundle          args        = new Bundle();
                args.putString("codigo_fabricante", codigo_producto[Integer.parseInt(pos)]);
                args.putString("nombre_producto", nombre_producto[Integer.parseInt(pos)]);
                args.putStringArray("ids_productos_asociados", ids_producto);
                args.putString("id_actual", valor2[0]);
                args.putString("id", id);
                args.putString("precio", precios[Integer.parseInt(pos)]);
                newFragment.setArguments(args);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                //getFragmentManager().beginTransaction().replace(getId(),newFragment).add(getId(), newFragment).addToBackStack(null).commit();

                transaction.replace(getId(), newFragment);
                transaction.addToBackStack("old_fragment_muestrario");

                // Commit the transaction
                transaction.commit();
            }
        };

        public HAdapter3(String id_modelo, String id_linea)
        {
            super();
            id_linea_buscador = id_linea;
            id_modelo_buscador = id_modelo;

            Log.i("HAdapter3", "linea: " + id_linea + ", modelo: " + id_modelo);

            String res = manager.cargarCursorProductos_Modelos_Lineas(id_linea_buscador, id_modelo_buscador, ci);
            if (!res.equals(""))
            {
                String[] modelos = res.split("\\|");
                ids_producto = new String[modelos.length];
                codigo_producto = new String[modelos.length];
                nombre_producto = new String[modelos.length];
                color_producto = new String[modelos.length];
                precios = new String[modelos.length];

                for (int i = 0; i < modelos.length; i++)
                {
                    String[] c = modelos[i].split("&");
                    ids_producto[i] = c[0];
                    color_producto[i] = c[1];
                    precios[i] = c[2];
                    nombre_producto[i] = c[3];
                    codigo_producto[i] = c[4];
                }
            }
            else
            {
                Toast.makeText(contexto, "No hay productos a mostrar", Toast.LENGTH_LONG).show();
                Log.d("HAdapter3", "No hay productos a mostrar.");
            }
        }

        public int getCount()
        {
            return ids_producto.length;
        }

        public Object getItem(int position)
        {
            return null;
        }

        public long getItemId(int position)
        {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            //View retval;
            //if (convertView == null)
            //{
            //retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, parent, false);
            //}

            if (convertView == null)
            {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, parent, false);
            }
            //retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);

            RobotoTextView title = (RobotoTextView) convertView.findViewById(R.id.title);
            Typeface typeface = RobotoTypefaceManager.obtainTypeface(
                    contexto,
                    RobotoTypefaceManager.FontFamily.ROBOTO,
                    RobotoTypefaceManager.TextWeight.MEDIUM,
                    RobotoTypefaceManager.TextStyle.NORMAL);
            RobotoTextViewUtils.setTypeface(title, typeface);
            title.setText(color_producto[position]); // nombre del producto

            /*TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(codigo_producto[position]);
            title.setTypeface(null, Typeface.BOLD);*/

            RecyclingImageView imagen = (RecyclingImageView) convertView.findViewById(R.id.image);

            File file = new File(contexto.getFilesDir(), nombre_producto[position] + "_01.jpg");
            imagen.setOnClickListener(mOnButtonClicked);
            //Log.d("Buscando imagen", nombre_producto[position] + "_01.jpg");

            if (file.exists())
            {
                //Log.d("Buscando imagen", "Encontrada");
                imagen.setImageBitmap(Funciones.decodeSampledBitmapFromResource(file, Constantes.IMG_MUESTRARIO_DIMEN, Constantes.IMG_MUESTRARIO_DIMEN));
                imagenAlpha.agregar_producto(imagen);
                imagen.setAlpha(Constantes.IMAGE_ALPHA);
            }
            imagen.setTag(ids_producto[position] + "&" + codigo_producto[position] + "&" + position); // aca le pondre el id_linea (variable global)

            return convertView;
        }

    }
}