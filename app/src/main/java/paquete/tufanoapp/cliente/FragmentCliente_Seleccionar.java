package paquete.tufanoapp.cliente;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
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
import android.widget.ListView;

import java.util.ArrayList;

import paquete.adapters.ValueAdapter;
import paquete.database.DBAdapter;
import paquete.tufanoapp.R;

public class FragmentCliente_Seleccionar extends Fragment
{
    private static String   TAG_CLIENTE = "fragment_cliente_vacio";
    private        Fragment fragment    = null;
    private ValueAdapter valueAdapter;
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
    private String id_usuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_cliente_seleccionar, container, false);
        FragmentCliente.TAG_CLIENTE = "fragment_cliente_consultar";

        Context   contexto = getActivity();
        DBAdapter manager  = new DBAdapter(contexto);

        getExtrasVar();

        /*
            Obtiene de la BD los datos del cliente.
         */

        Cursor            cursor       = manager.cargarCursorClientesByID(id_usuario);
        ArrayList<String> columnArray1 = new ArrayList<>(); // nombre_clientes
        ArrayList<String> columnArray2 = new ArrayList<>(); // id_clientes

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            columnArray1.add(cursor.getString(0));
            columnArray2.add(cursor.getString(1));
        }
        cursor.close();

        String[] elemento = columnArray1.toArray(new String[columnArray1.size()]);

        /*
            Obtiene de la BD los datos del cliente.
         */

        initListView(rootView, columnArray1, columnArray2, contexto);
        initAutoComplete(rootView, contexto, elemento);

        return rootView;
    }

    /**
     * Inicializa los listViews
     *
     * @param rootView     RootView
     * @param columnArray1 Nombres de los clientes
     * @param columnArray2 Ids de los clientes
     * @param contexto     Contexto de la aplicacion.
     */
    private void initListView(View rootView, ArrayList<String> columnArray1, ArrayList<String> columnArray2, Context contexto)
    {
        ListView lista = (ListView) rootView.findViewById(R.id.listView_clientes);
        valueAdapter = new ValueAdapter(columnArray1, columnArray2, contexto);
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, elemento );
        lista.setAdapter(valueAdapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //Toast.makeText(contexto, "Cliente seleccionado: "+lista.getItemAtPosition(position)+" id_cliente:"+valueAdapter.obtener_id_Cliente(position), Toast.LENGTH_SHORT).show();

                fragment = new FragmentCliente_Editar();
                Bundle args = new Bundle();
                Log.d("position", "" + position);
                args.putString("id_cliente", String.valueOf(valueAdapter.obtener_id_Cliente(position)));
                args.putString("id_usuario", id_usuario);
                fragment.setArguments(args);

                TAG_CLIENTE = "fragment_cliente_editar";

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG_CLIENTE).commit();
            }
        });
    }

    /**
     * Inicializa los autoComplete
     *
     * @param rootView RootView
     * @param contexto Contexto
     * @param elemento Data para el autoComplete.
     */
    private void initAutoComplete(View rootView, Context contexto, String[] elemento)
    {
        AutoCompleteTextView busqueda = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView_buscarCliente);
        ArrayAdapter<String> adapter  = new ArrayAdapter<>(contexto, android.R.layout.simple_dropdown_item_1line, elemento);
        busqueda.setAdapter(adapter);
        busqueda.addTextChangedListener(watch);
    }

    /**
     * Obtiene las variables adicionales que fueron pasadas como parametros desde otra actividad.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        id_usuario = bundle.getString("id_usuario");
    }
}