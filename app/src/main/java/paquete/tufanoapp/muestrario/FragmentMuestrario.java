package paquete.tufanoapp.muestrario;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.devspark.robototextview.util.RobotoTextViewUtils;
import com.devspark.robototextview.util.RobotoTypefaceManager;
import com.devspark.robototextview.widget.RobotoTextView;

import java.io.File;
import java.util.ArrayList;

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
    private static final String TAG = "FragmentMuestrario";
    public static String destacado_filtrado; // 1 new - 0 old - 2 all
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
        destacado_filtrado = "2";

        getExtrasVar();
        initRadioButton();
        seleccionarProductoPorDefecto();

        return rootView;
    }

    private void selectDefaultRB()
    {
        // Selecciona por defecto el rb_new (Solo en el primer inicio)..
        switch (destacado_filtrado)
        {
            case "0":
            {
                Log.i(TAG, "SetDefaultChecked rb_old");
                ((RadioButton) rootView.findViewById(R.id.rb_old)).setChecked(true);
            }
            break;
            case "1":
            {
                Log.i(TAG, "SetDefaultChecked rb_new");
                ((RadioButton) rootView.findViewById(R.id.rb_new)).setChecked(true);
            }
            break;
            case "2":
            {
                Log.i(TAG, "SetDefaultChecked rb_all");
                ((RadioButton) rootView.findViewById(R.id.rb_all)).setChecked(true);
            }
            break;

        }
    }

    private void initRadioButton()
    {
        selectDefaultRB();
        final RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.radio_group_producto);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                //boolean checked = ((RadioButton) view).isChecked();
                switch (rg.getCheckedRadioButtonId())
                {
                    case R.id.rb_old:
                        Log.i(TAG, "rb_old");
                        destacado_filtrado = "0";
                        break;
                    case R.id.rb_new:
                        Log.i(TAG, "rb_new");
                        destacado_filtrado = "1";
                        break;
                    case R.id.rb_all:
                        Log.i(TAG, "rb_all");
                        destacado_filtrado = "2";
                        break;
                }

                new reCargarDatos().execute();
            }
        });
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
        if (bundle.getString("selected_cb") != null)
        {
            Log.i(TAG, "getExtrasVar - destacado_filtrado: " + destacado_filtrado);
            destacado_filtrado = bundle.getString("selected_cb");
        }
    }

    @Override
    public void onDestroy()
    {
        System.gc();
        super.onDestroy();
    }

    private ArrayList<String> obtenerLineasValidas(String[] lineas)
    {
        ArrayList<String> lineas_permitidas = new ArrayList<>();
        // Verificando si la linea posee modelos disponibles..
        for (String linea : lineas)
        {
            String[] datos_linea  = linea.split("&");
            String   id_linea     = datos_linea[0];
            String   nombre_linea = datos_linea[1];

            String res2 = (manager.cargarCursorModelos_Lineas(id_linea, ci));

            if (res2.equals(""))
                Log.d(TAG, "La linea " + nombre_linea + " no tiene modelos..");
            else
            {
                lineas_permitidas.add(id_linea);
                Log.i(TAG, "Permitiendo la linea: " + nombre_linea + " de id: " + id_linea);


                /*
                String[]          datos_modelo       = res2.split("\\|");
                ArrayList<String> modelos_permitidos = obtenerModelosValidos_Linea(datos_modelo, id_linea);

                for (int i = 0; i < modelos_permitidos.size(); i++)
                    Log.d(TAG, "Comparando el id_linea: " + id_linea + " (TABLA LINEAS), con el id_linea: " + modelos_permitidos.get(i)+ "(TABLA MODELOS)");

                if (modelos_permitidos.contains(id_linea))
                {
                    Log.d(TAG, "La linea " + nombre_linea + "  ( id:" + id_linea + ") tiene modelos validos..");
                    lineas_permitidas.add(id_linea);
                }
                else
                {
                    Log.d(TAG, "La linea " + nombre_linea + " no tiene modelos (con filtros)..");
                }*/
            }
        }
        return lineas_permitidas;
    }

    private ArrayList<String> obtenerModelosValidos_Linea(String[] modelos, String id_linea_buscador)
    {
        ArrayList<String> permitidos = new ArrayList<>();
        // Verificando si el modelo posee productos disponibles..
        for (String modelo : modelos)
        {
            String[] c = modelo.split("&");
            String res2 = manager.cargarCursorProductos_Modelos_Lineas(id_linea_buscador,
                    c[0], ci, destacado_filtrado);

            if (res2.equals(""))
                Log.d(TAG, "El modelo no tiene productos.. (obtenerModelosValidos_Linea)");
            else
            {
                String[] datos_modelo = res2.split("\\|");

                for (int i = 0; i < datos_modelo.length; i++)
                {
                    String[] d = datos_modelo[i].split("&");
                    permitidos.add(d[1]);
                    Log.d(TAG, "Permitiendo a " + d[1]);
                }
            }
        }
        return permitidos;
    }

    private ArrayList<String> obtenerModelosValidos(String[] modelos, String id_linea)
    {
        ArrayList<String> permitidos = new ArrayList<>();
        // Verificando si el modelo posee productos disponibles..
        for (String modelo : modelos)
        {
            String[]                     c               = modelo.split("&");
            String                       id_modelo       = c[0];
            ArrayList<ArrayList<String>> datos_productos = manager.obtenerProductos_Filtrado(ci, destacado_filtrado);

            for (int i = 0; i < datos_productos.size(); i++)
            {
                Log.d(TAG, "ID_PRODUCTO: " + datos_productos.get(i).get(0) + " ID_MODELO: "
                        + datos_productos.get(i).get(1) + " ID_LINEA: " + datos_productos.get(i).get(2));

                if (datos_productos.get(i).get(1).equals(id_modelo))
                {
                    //Log.d(TAG, "El modelo si tiene productos..");
                    permitidos.add(id_modelo);
                }
                else
                {
                    Log.d(TAG, "El modelo no tiene productos..");
                }

            }

            /*if (datos_productos.equals("")) Log.d(TAG, "El modelo no tiene productos..");
            else
            {
                permitidos.add(id_modelo);
            }*/
        }
        return permitidos;
    }

    private void loadData()
    {
        Log.i(TAG, "Inicializando tabla.. Ordenando por: Destacado: " + destacado_filtrado);

        //if (id_modelo_seleccionado != null)
        //{
        Log.i(TAG, "Reloading..");
        final Thread hilo = new Thread()
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
                            HorizontalListView listview = (HorizontalListView) rootView.findViewById(R.id.listview);
                            listview.setVisibility(View.INVISIBLE);
                            listview.setAdapter(new HAdapter());

                            HorizontalListView listview2 = (HorizontalListView) rootView.findViewById(R.id.listview2);
                            listview2.setVisibility(View.INVISIBLE);
                            listview2.setAdapter(new HAdapter2(id_linea_seleccionado));

                            HorizontalListView listview3 = (HorizontalListView) rootView.findViewById(R.id.listview3);
                            listview3.setVisibility(View.INVISIBLE);
                            listview3.setAdapter(new HAdapter3(id_modelo_seleccionado, id_linea_seleccionado));
                        }
                    });
                }
            }
        };
        hilo.start();
        //}
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
                imageAlphaClass.ocultar_modelos();
                RecyclingImageView bt = (RecyclingImageView) v;
                bt.setAlpha(1f);

                id_linea_seleccionado = valor2[0];
                id_modelo_seleccionado = null;

                HorizontalListView listview2 = (HorizontalListView) rootView.findViewById(R.id.listview2);
                listview2.setAdapter(new HAdapter2(id_linea_seleccionado));

                HorizontalListView listview3 = (HorizontalListView) rootView.findViewById(R.id.listview3);
                listview3.setVisibility(View.INVISIBLE);
            }
        };

        public HAdapter()
        {
            super();
            Log.i(TAG, "Constructor del HAdapter");
            String res = (manager.cargarCursorLineas(ci));

            if (!res.equals(""))
            {
                String[]          lineas     = res.split("\\|");
                ArrayList<String> permitidos = obtenerLineasValidas(lineas);

                if (permitidos.size() > 0)
                {
                    HorizontalListView listview = (HorizontalListView) rootView.findViewById(R.id.listview);
                    listview.setVisibility(View.VISIBLE);
                }

                ids_lineas = new String[permitidos.size()];
                nombres_lineas = new String[permitidos.size()];

                for (int i = 0; i < lineas.length; i++)
                {
                    String[] c = lineas[i].split("&");

                    if (permitidos.contains(c[0]))
                    {
                        ids_lineas[i] = c[0];
                        nombres_lineas[i] = c[1];
                    }
                }
            }
            else
            {
                Toast.makeText(contexto, "No hay lineas a mostrar", Toast.LENGTH_LONG).show();
                Log.d(TAG, "HAdapter: No hay lineas a mostrar.");
            }
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

            RecyclingImageView imagen = (RecyclingImageView) convertView.findViewById(R.id.image);
            File               file   = new File(contexto.getFilesDir(), nombres_lineas[position] + ".jpg");

            imagen.setOnClickListener(mOnButtonClicked);

            if (file.exists())
            {
                //Log.d(TAG, "Encontrada");
                imagen.setImageBitmap(Funciones.decodeSampledBitmapFromResource(file, 130, 130));
                imagenAlpha.agregar_linea(imagen);
                if (!id_linea_seleccionado.equals(ids_lineas[position]))
                    imagen.setAlpha(Constantes.IMAGE_ALPHA);
            }

            imagen.setTag(ids_lineas[position] + "&" + nombres_lineas[position]);

            return convertView;
        }
    }

    //obtenerProductos_Filtrado()
    //obtenerModelos_Validos()
    //obtenerLineas_Validas()

    /**
     * Inicializa la 2da lista horizontal de los modelos
     */
    private class HAdapter2 extends BaseAdapter
    {

        private final String id_linea_buscador;
        final View.OnClickListener mOnButtonClicked = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // Al hacer click en algun modelo, se abren los productos asociados
                Object r        = v.getTag();
                String valor2[] = String.valueOf(r).split("&");

                imageAlphaClass.ocultar_modelos();
                RecyclingImageView bt = (RecyclingImageView) v;
                bt.setAlpha(1f);

                id_modelo_seleccionado = valor2[0];

                HorizontalListView listview3 = (HorizontalListView) rootView.findViewById(R.id.listview3);
                listview3.setVisibility(View.VISIBLE);
                listview3.setAdapter(new HAdapter3(id_modelo_seleccionado, id_linea_buscador));
            }
        };
        String[] valores_modelo_id     = new String[0];
        String[] valores_modelo_nombre = new String[0];
        String[] valores_modelo_img    = new String[0];

        public HAdapter2(String s)
        {
            super();
            id_linea_buscador = s;

            if (s != null)
            {
                Log.i(TAG, "Constructor del HAdapter2");
                String res = (manager.cargarCursorModelos_Lineas(id_linea_buscador, ci));

                if (!res.equals(""))
                {
                    String[]          modelos    = res.split("\\|");
                    ArrayList<String> permitidos = obtenerModelosValidos(modelos, id_linea_buscador);

                    if (permitidos.size() > 0)
                    {
                        HorizontalListView listview = (HorizontalListView) rootView.findViewById(R.id.listview2);
                        listview.setVisibility(View.VISIBLE);
                    }

                    valores_modelo_id = new String[permitidos.size()];
                    valores_modelo_nombre = new String[permitidos.size()];
                    valores_modelo_img = new String[permitidos.size()];

                    for (int i = 0; i < modelos.length; i++)
                    {
                        String[] datos_modelos = modelos[i].split("&");
                        if (permitidos.contains(datos_modelos[0]))
                        {
                            valores_modelo_id[i] = datos_modelos[0];
                            valores_modelo_nombre[i] = datos_modelos[1];
                            valores_modelo_img[i] = datos_modelos[2];
                        }
                    }
                }
                else
                {
                    Toast.makeText(contexto, "No hay modelos a mostrar", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "HAdapter2: No hay modelos a mostrar.");
                }
            }
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
            //Log.d(TAG, "HAdapter2: getView");
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
            //Log.d(TAG, valores_modelo_nombre[position] + ".jpg");

            if (file.exists())
            {
                //Log.d(TAG, "Encontrada");
                imagen.setImageBitmap(Funciones.decodeSampledBitmapFromResource(file, 130, 130));
                imagenAlpha.agregar_modelo(imagen);
                //if (!id_modelo_seleccionado.equals(valores_modelo_id[position]))
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
                Log.d(TAG, "Producto presionado.. Voy a FragmentGaleria");
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

            if (id_modelo != null && id_linea != null)
            {
                Log.i(TAG, "Constructor del HAdapter3");

                HorizontalListView listview = (HorizontalListView) rootView.findViewById(R.id.listview3);
                listview.setVisibility(View.VISIBLE);

                Log.i(TAG, "HAdapter3: linea: " + id_linea + ", modelo: " + id_modelo);

                String res = manager.cargarCursorProductos_Modelos_Lineas(id_linea_buscador,
                        id_modelo_buscador, ci, destacado_filtrado);
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
                    Log.d(TAG, "HAdapter3: No hay productos a mostrar.");
                }
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

    /**
     * Clase para la carga en 2do plano de los datos de la tabla (Con Filtros)
     */
    private class reCargarDatos extends AsyncTask<String, String, String>
    {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute()
        {
            Log.d(TAG, "Recargando informacion");
            pDialog = new ProgressDialog(contexto);
            pDialog.setTitle("Por favor espere...");
            pDialog.setMessage("Recargando informacion...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            loadData();
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            Log.d(TAG, "Dismiss Dialog");
        }
    }
}