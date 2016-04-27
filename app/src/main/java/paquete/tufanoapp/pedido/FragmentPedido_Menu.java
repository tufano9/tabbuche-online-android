package paquete.tufanoapp.pedido;

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
import paquete.tufanoapp.Home;
import paquete.tufanoapp.R;

/**
 * Desarrollado por Gerson el 14/4/2015.
 */
public class FragmentPedido_Menu extends Fragment
{
    private static final String TAG = "fragment_pedido_vacio";
    private static Context contexto;
    private Fragment fragment = null;
    private String    id_usuario;
    private DBAdapter manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_pedido_menu, container, false);

        Bundle bundle = getArguments();
        id_usuario = bundle.getString("id_usuario");
        contexto = getActivity();
        manager = new DBAdapter(contexto);

        Home.TAG = "fragment_muestrario";

        initImagesViews(rootView);

        return rootView;
    }

    /**
     * Inicializa los imagesViews
     *
     * @param rootView RootView
     */
    private void initImagesViews(View rootView)
    {
        RecyclingImageView imagen_agregar   = (RecyclingImageView) rootView.findViewById(R.id.imageView_Realizar);
        RecyclingImageView imagen_consultar = (RecyclingImageView) rootView.findViewById(R.id.imageView_Consultar);

        imagen_agregar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final boolean permitido = Funciones.FuncionalidadPermitida("Pedidos", 2, id_usuario, manager);
                if (permitido)
                {
                    fragment = new FragmentPedido_Muestrario();
                    Bundle args = new Bundle();
                    args.putString("id_usuario", id_usuario);
                    fragment.setArguments(args);

                    Home.TAG = "fragment_pedido_agregar";

                    //TAG_CLIENTE = "fragment_cliente_agregar";

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();

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
                final boolean permitido = Funciones.FuncionalidadPermitida("Pedidos", 0, id_usuario, manager);
                if (permitido)
                {
                    fragment = new FragmentPedido_Consultar();
                    Bundle bundle = getArguments();
                    String ci     = bundle.getString("cedula");
                    Bundle args   = new Bundle();
                    args.putString("id_usuario", id_usuario);
                    args.putString("cedula", ci);
                    fragment.setArguments(args);

                    Home.TAG = "fragment_pedido_consultar";

                    //TAG_CLIENTE = "fragment_cliente_consultar";

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();
                }
                else
                    Toast.makeText(contexto, Constantes.NO_PERMITIDO, Toast.LENGTH_SHORT).show();
            }
        });
    }
}