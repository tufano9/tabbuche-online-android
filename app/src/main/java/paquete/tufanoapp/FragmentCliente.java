package paquete.tufanoapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import paquete.database.DBAdapter;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.recycle_bitmap.RecyclingImageView;

public class FragmentCliente extends Fragment
{
    private Fragment fragment = null;
    private String id_usuario;
    public static String TAG_CLIENTE = "fragment_cliente_vacio";
    private static Context contexto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_cliente, container, false);
        contexto = getActivity();
        Home.TAG = "fragment_muestrario";
        getExtrasVar();
        initImagesViews(rootView);
        return rootView;
    }

    /**
     * Obtiene los parametros que fueron pasados por medio de otra activity.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        id_usuario = bundle.getString("id_usuario");
    }

    /**
     * Inicializa los imagesViews
     *
     * @param rootView RootView
     */
    private void initImagesViews(View rootView)
    {
        final DBAdapter manager = new DBAdapter(contexto);

        RecyclingImageView imagen_agregar = (RecyclingImageView) rootView.findViewById(R.id.imageView_Agregar);
        RecyclingImageView imagen_consultar = (RecyclingImageView) rootView.findViewById(R.id.imageView_Consultar);

        imagen_agregar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final boolean permitido = Funciones.FuncionalidadPermitida("Clientes", 2, id_usuario, manager);
                if (permitido)
                {
                    fragment = new FragmentCliente_Agregar();
                    Bundle args = new Bundle();
                    args.putString("id_usuario", id_usuario);
                    fragment.setArguments(args);

                    Home.TAG = "fragment_cliente_agregar";

                    //TAG_CLIENTE = "fragment_cliente_agregar";

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG_CLIENTE).commit();

                    //Cambiamos el titulo
                    //((Home) getActivity()).setActionBarTitle("Clientes / Agregar");
                }
                else
                    Toast.makeText(contexto, Constantes.NO_PERMITIDO, Toast.LENGTH_SHORT).show();
            }
        });

        imagen_consultar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final boolean permitido = Funciones.FuncionalidadPermitida("Clientes", 0, id_usuario, manager);
                if (permitido)
                {
                    fragment = new FragmentCliente_Seleccionar();
                    Bundle args = new Bundle();
                    args.putString("id_usuario", id_usuario);
                    fragment.setArguments(args);

                    Home.TAG = "fragment_cliente_consultar";

                    //TAG_CLIENTE = "fragment_cliente_consultar";

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG_CLIENTE).commit();
                }
                else
                    Toast.makeText(contexto, Constantes.NO_PERMITIDO, Toast.LENGTH_SHORT).show();
            }
        });
    }
}