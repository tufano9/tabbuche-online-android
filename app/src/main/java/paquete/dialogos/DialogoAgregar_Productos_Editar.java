package paquete.dialogos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
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
import paquete.tufanoapp.FragmentPedido_Editar;
import paquete.tufanoapp.R;
import paquete.tufanoapp.imageAlphaClass;

/**
 * Desarrollado por Gerson el 18/6/2015.
 */
public class DialogoAgregar_Productos_Editar extends DialogFragment
{
    private Context contexto;
    private View rootView;
    private static int num_checks;
    private static ArrayList<String> ids_agregar;
    private String id_pedido, ci, estatus;
    private DBAdapter manager;
    private imageAlphaClass imagenAlpha;
    private String[] ids_lineas,nombres_lineas;

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        contexto = getActivity();
        manager = new DBAdapter(contexto);

        imagenAlpha = new imageAlphaClass();
        ids_agregar = new ArrayList<>();
        num_checks=0;
        rootView = inflater.inflate(R.layout.dialogo_agregar_productos, null);

        getExtrasVar();
        return initAlertDialog();
    }

    /**
     * Inicializa el dialogo de alerta
     * @return
     */
    private Dialog initAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView)
                .setPositiveButton(R.string.agregar_productos, null)
                .setTitle(R.string.seleccione_productos_agregar)
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogoAgregar_Productos_Editar.this.getDialog().cancel();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(800, WindowManager.LayoutParams.MATCH_PARENT); //Controlling width and height.

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

        HorizontalListView listview = (HorizontalListView) rootView.findViewById(R.id.listview);
        listview.setAdapter(new HAdapter());

        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (num_checks > 0)
                {
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(contexto);
                    String mensaje = contexto.getResources().getString(R.string.confirmacion_agregar_producto);
                    mensaje = mensaje.replace("%", "" + num_checks);

                    dialogo.setMessage(mensaje)
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                            .setTitle(R.string.pedido_btn_6);

                    dialogo.setPositiveButton("Agregar Productos", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogo2, int which)
                        {
                            manager.insertar_producto_pedido_editar_id(ids_agregar);

                            Toast.makeText(getActivity(), ids_agregar.size() + " Productos agregados exitosamente!", Toast.LENGTH_LONG).show();

                            dialog.dismiss();

                            String TAG = "fragment_pedido";
                            Fragment fragment = new FragmentPedido_Editar();
                            Bundle args = new Bundle();
                            args.putString("id_pedido", id_pedido);
                            args.putString("cedula", ci);
                            args.putString("estatus", estatus);
                            fragment.setArguments(args);

                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();

                        }
                    });

                    dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    dialogo.show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Por favor seleccione al menos un producto!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return dialog;
    }

    /**
     * Obtiene las variables que fueron pasadas como parametros desde otro activity
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        id_pedido = bundle.getString("id_pedido");
        ci = bundle.getString("cedula");
        estatus = bundle.getString("estatus");
    }

    /**
     * Inicializa la 1era lista horizontal de las lineas
     */
    private class  HAdapter extends BaseAdapter {

        public HAdapter()
        {
            super();
        }
        private final View.OnClickListener mOnButtonClicked = new View.OnClickListener() {

            public void onClick(View v) {

                Object r = v.getTag();
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


        public int getCount() {
            return ids_lineas.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            //View retval;

            if (convertView == null)
            {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, parent, false);
            }

            //retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);
            // Creo el titulo de la imagen
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

            // Creo la imagen de la linea
            RecyclingImageView imagen = (RecyclingImageView) convertView.findViewById(R.id.image);
            File file = new File(contexto.getFilesDir(), nombres_lineas[position]+".jpg");

            imagen.setOnClickListener(mOnButtonClicked);

            if(file.exists())
            {
                //Log.d("Buscando imagen", "Encontrada");
                imagen.setImageBitmap(Funciones.decodeSampledBitmapFromResource(file, 130, 130));
                imagenAlpha.agregar_linea(imagen);
                String id_linea_seleccionado = "";
                if( ! id_linea_seleccionado.equals(ids_lineas[position]) )
                    imagen.setAlpha(Constantes.IMAGE_ALPHA);
            }

            imagen.setTag(ids_lineas[position]+"&"+nombres_lineas[position]);

            return convertView;
        }
    }

    /**
     * Inicializa la 2da lista horizontal de los modelos
     */
    private class  HAdapter2 extends BaseAdapter {

        private final String id_linea_buscador;
        String[] valores_modelo_id = new String[0];
        String[] valores_modelo_nombre = new String[0];
        String[] valores_modelo_img = new String[0];

        public HAdapter2(String s)
        {
            super();
            Log.i("HAdapter2",s);
            id_linea_buscador = s;
            String res = (manager.cargarCursorModelos_Lineas(id_linea_buscador,ci));
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
            //manager.cerrar();
        }

        private final View.OnClickListener mOnButtonClicked = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Object r = v.getTag();
                String valor2[] = String.valueOf(r).split("&");

                imageAlphaClass.ocultar_modelos();
                RecyclingImageView bt = (RecyclingImageView) v;
                bt.setAlpha(1f);

                /*String lin2 = valor2[1];

                TextView tv = (TextView) rootView.findViewById(R.id.textView8);
                tv.setText(lin2);*/

                HorizontalListView listview3 = (HorizontalListView) rootView.findViewById(R.id.listview3);
                listview3.setVisibility(View.VISIBLE);
                listview3.setAdapter(new HAdapter3(valor2[0],id_linea_buscador));
            }
        };


        public int getCount() {
            return valores_modelo_id.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, parent, false);
            }

            //View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);

            //Crea el titulo de la imagen
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

            //Crea la imagen del modelo
            RecyclingImageView imagen = (RecyclingImageView) convertView.findViewById(R.id.image);

            File file = new File(contexto.getFilesDir(), valores_modelo_nombre[position]+".jpg");
            imagen.setOnClickListener(mOnButtonClicked);
            //Log.d("Buscando imagen", valores_modelo_nombre[position] + ".jpg");

            if(file.exists())
            {
                //Log.d("Buscando imagen", "Encontrada");
                imagen.setImageBitmap(Funciones.decodeSampledBitmapFromResource(file, 130, 130));
                imagenAlpha.agregar_modelo(imagen);
                String id_modelo_seleccionado = "";
                if( ! id_modelo_seleccionado.equals(valores_modelo_id[position]) )
                    imagen.setAlpha(Constantes.IMAGE_ALPHA);
            }

            imagen.setTag(valores_modelo_id[position]+"&"+valores_modelo_nombre[position]); // aca le pondre el id_modelo (variable global)

            return convertView;
        }
    }

    /**
     * Inicializa la 3era lista horizontal de los productos
     */
    private class  HAdapter3 extends BaseAdapter {

        private final String id_linea_buscador;
        private final String id_modelo_buscador;
        String[] valores_producto_id = new String[0];
        String[] valores_producto_nombre = new String[0];
        String[] valores_producto_nombre_real = new String[0];
        String[] precios = new String[0];

        public HAdapter3(String id_modelo,String id_linea)
        {
            super();
            id_linea_buscador = id_linea;
            id_modelo_buscador = id_modelo;

            Log.i("HAdapter3","linea: "+id_linea+", modelo: "+id_modelo);

            Cursor cur = manager.cargarCursorPedidos_Editar();
            String[] ids_ocultar = new String[cur.getCount()];
            int z = 0;

            for(cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext())
            {
                ids_ocultar[z] = cur.getString(3);
                Log.d("ID PRODUCTO","No mostrar: "+cur.getString(3));
                z++;
            }

            cur.close();

            String res = manager.cargarCursorProductos_Modelos_Lineas_DialogAgregar(id_linea_buscador, id_modelo_buscador, ci, ids_ocultar);
            if (!res.equals(""))
            {
                String[] modelos = res.split("\\|");
                valores_producto_id = new String[modelos.length];
                valores_producto_nombre = new String[modelos.length];
                valores_producto_nombre_real = new String[modelos.length];
                precios = new String[modelos.length];

                for (int i = 0; i < modelos.length; i++)
                {
                    String[] c = modelos[i].split("&");
                    valores_producto_id[i] = c[0];
                    valores_producto_nombre[i] = c[1];
                    precios[i] = c[2];
                    valores_producto_nombre_real[i] = c[3];
                }
            }
            else
            {
                Log.d("HAdapter3","No hay productos a mostrar.");
            }
        }

        private final View.OnClickListener mOnButtonClicked = new View.OnClickListener()
        {

            public void onClick(View v)
            {
                Object r = v.getTag();
                String valor2[] = String.valueOf(r).split("&");
                String pos = valor2[0];

                RecyclingImageView bt = (RecyclingImageView) v;
                if(bt.getAlpha()==1f)
                {
                    // Si el producto estaba seleccionado, lo presiono de nuevo y lo des-selecciono..
                    num_checks--;
                    bt.setAlpha(Constantes.IMAGE_ALPHA);
                    ids_agregar.remove(pos);
                    Toast.makeText(contexto,"Producto "+valor2[1]+" eliminado",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    num_checks++;
                    bt.setAlpha(1f);
                    ids_agregar.add(pos);
                    Toast.makeText(contexto,"Producto "+valor2[1]+" agregado",Toast.LENGTH_SHORT).show();
                }
            }
        };

        public int getCount() {
            return valores_producto_id.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, parent, false);
            }

            // Crea el titulo de la imagen
            RobotoTextView title = (RobotoTextView) convertView.findViewById(R.id.title);
            Typeface typeface = RobotoTypefaceManager.obtainTypeface(
                    contexto,
                    RobotoTypefaceManager.FontFamily.ROBOTO,
                    RobotoTypefaceManager.TextWeight.MEDIUM,
                    RobotoTypefaceManager.TextStyle.NORMAL);
            RobotoTextViewUtils.setTypeface(title, typeface);
            title.setText(valores_producto_nombre[position]);

            // Crea la imagen del producto
            RecyclingImageView imagen = (RecyclingImageView) convertView.findViewById(R.id.image);

            File file = new File(contexto.getFilesDir(), valores_producto_nombre_real[position]+"_01.jpg");
            imagen.setOnClickListener(mOnButtonClicked);

            if(file.exists())
            {
                imagen.setImageBitmap(Funciones.decodeSampledBitmapFromResource(file, 130, 130));
                imagenAlpha.agregar_producto(imagen);
                imagen.setAlpha(Constantes.IMAGE_ALPHA);
            }

            imagen.setTag(valores_producto_id[position]+"&"+valores_producto_nombre[position]+"&"+position); // aca le pondre el id_linea (variable global)

            return convertView;
        }

    }
}