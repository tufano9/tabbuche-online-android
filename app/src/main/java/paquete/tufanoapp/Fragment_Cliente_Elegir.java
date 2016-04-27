package paquete.tufanoapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import paquete.database.DBAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_Cliente_Elegir.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Cliente_Elegir#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Cliente_Elegir extends Fragment
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //private String mParam1;
    //private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context contexto;
    private String id_usuario;
    private boolean cliente_seleccionado = false;
    private ListView lista;
    private ValueAdapter valueAdapter;
    private String id_cliente;
    private ArrayList<String> columnArray2;
    private String id_cliente_agregar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Cliente_Elegir.
     */
    @SuppressWarnings("unused")
    public static Fragment_Cliente_Elegir newInstance(String param1, String param2)
    {
        Fragment_Cliente_Elegir fragment = new Fragment_Cliente_Elegir();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment_Cliente_Elegir()
    {
        // Required empty public constructor
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null)
        {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment__cliente__elegir, container, false);

        contexto = getActivity();
        DBAdapter manager = new DBAdapter(contexto);

        getExtrasVar();
        initButtons(rootView);

        ArrayList<String> columnArray1 = new ArrayList<>(); // nombre_clientes
        Cursor cursor = manager.cargarCursorClientesByID(id_usuario);
        columnArray2 = new ArrayList<>(); // id_clientes

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            columnArray1.add(cursor.getString(0));
            columnArray2.add(cursor.getString(1));
        }
        cursor.close();

        String[] elemento = columnArray1.toArray(new String[columnArray1.size()]);

        initListView(rootView, columnArray1);
        initAutoComplete(rootView, elemento);

        if (id_cliente_agregar != null)
        {
            int posx = buscarClienteLista(id_cliente_agregar);
            Log.d("buscarClienteLista", "respuesta: " + posx);
            //lista.setSelection(posx);
            lista.setItemChecked(posx, true);
            lista.performItemClick(lista.getSelectedView(), posx, 0);
        }

        return rootView;
    }

    private void initListView(View rootView, ArrayList<String> columnArray1)
    {
        lista = (ListView) rootView.findViewById(R.id.listView_clientes);
        valueAdapter = new ValueAdapter(columnArray1, columnArray2, contexto);
        lista.setAdapter(valueAdapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                id_cliente = String.valueOf(valueAdapter.obtener_id_Cliente(position));
                Log.d("id_cliente", id_cliente);
                cliente_seleccionado = true;

                //lista.setItemChecked(position, true);
                //view.setSelected(true); // <== Will cause the highlight to remain
            }
        });
    }

    /**
     * Inicializa los ListView
     *
     * @param rootView rootView
     * @param elemento elementos del autocomplete
     */
    private void initAutoComplete(View rootView, String[] elemento)
    {
        AutoCompleteTextView busqueda = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView_buscarCliente);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(contexto, android.R.layout.simple_dropdown_item_1line, elemento);
        //adapter.remove(adapter.getItem(0));
        busqueda.setAdapter(adapter1);
        busqueda.addTextChangedListener(watch);
    }

    /**
     * Obtiene los parametros que fueron pasados desde otra activity
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        id_usuario = bundle.getString("id_usuario");
        id_cliente_agregar = bundle.getString("id_cliente");
    }

    /**
     * Inicializa los botones
     *
     * @param rootView rootView
     */
    private void initButtons(View rootView)
    {
        Button btn_agregar_cliente = (Button) rootView.findViewById(R.id.btn_agregar_cliente);
        btn_agregar_cliente.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String TAG = "fragment_pedido_agregar_cliente";
                Fragment fragment = new FragmentCliente_Agregar();

                Bundle args = new Bundle();
                args.putString("id_usuario", id_usuario);
                args.putBoolean("desdePedidos", true);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();

            }
        });

        Button btn_editar_cliente = (Button) rootView.findViewById(R.id.btn_editar_cliente);
        btn_editar_cliente.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (cliente_seleccionado || id_cliente_agregar != null)
                {
                    String TAG = "fragment_pedido_editar_cliente";
                    Fragment fragment = new FragmentCliente_Editar();

                    Bundle args = new Bundle();
                    args.putString("id_cliente", id_cliente_agregar != null ? id_cliente_agregar : id_cliente);
                    args.putString("id_usuario", id_usuario);
                    args.putBoolean("desdePedidos", true);
                    fragment.setArguments(args);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();
                }
                else
                {
                    Toast.makeText(contexto, "Debes seleccionar un cliente!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Para los filtros
    private final TextWatcher watch = new TextWatcher()
    {
        @Override
        public void afterTextChanged(Editable arg0)
        {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c)
        {
            valueAdapter.getFilter().filter(s);
        }
    };

    /**
     * Busca la posicion del cliente dentro de la lista.
     *
     * @param id_cliente ID del cliente a buscar.
     * @return Posicion del cliente dentro de la lista.
     */
    private int buscarClienteLista(String id_cliente)
    {
        Log.d("buscarClienteLista", "INICIO");
        for (int i = 0; i < lista.getCount(); i++)
            if (columnArray2.get(i).equals(id_cliente))
                return i;

        Log.d("buscarClienteLista", "FIN :(");
        return -1;
    }

    @SuppressWarnings("unused")
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity)
        {
            a = (Activity) context;

            try
            {
                mListener = (OnFragmentInteractionListener) a;
            }
            catch (ClassCastException e)
            {
                throw new ClassCastException(a.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
        else
        {
            try
            {
                mListener = (OnFragmentInteractionListener) context;
            }
            catch (ClassCastException e)
            {
                throw new ClassCastException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }
}