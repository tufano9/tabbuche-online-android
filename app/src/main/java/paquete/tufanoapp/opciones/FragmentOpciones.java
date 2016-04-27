package paquete.tufanoapp.opciones;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoButton;

import paquete.actualizaciones_app.actualizar_App;
import paquete.actualizaciones_app.actualizar_DatosApp;
import paquete.database.DBAdapter;
import paquete.dialogos.DialogoAlerta;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.tufanoapp.Home;
import paquete.tufanoapp.R;

public class FragmentOpciones extends Fragment
{
    private static Context contexto = null;
    private static FragmentManager fragmentManager;
    private final int[]  actualizar                  = new int[Constantes.NUMERO_ACTUALIZACIONES];
    private       String usuario, id, version_actual = "";
    private DBAdapter manager;

    /**
     * Lanza un dialogo de notificacion de la actualizacion finalizada.
     */
    public static void actualizacion_terminada()
    {
        //FragmentManager fragmentManager = getFragmentManager();
        DialogoAlerta dialogo = new DialogoAlerta();
        //dialogo.show(fragmentManager, "tagAlerta");

        /*
            Aca tuve que evitar el uso de dialogo.show porque si el dispositivo
            entraba en estado de onSaveInstance (Hibernacion) Ocurria un error..
         */

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(dialogo, null);
        ft.commitAllowingStateLoss();

        DBAdapter manager = new DBAdapter(contexto);
        manager.eliminar_producto_seleccionado();
        manager.cerrar();
        Home.TAG = "fragment_muestrario";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        onDestroy();

        View rootView = inflater.inflate(R.layout.fragment_opciones, container, false);

        manager = new DBAdapter(contexto);
        contexto = getActivity();
        fragmentManager = getFragmentManager();

        for (int i = 0; i < actualizar.length; i++)
            actualizar[i] = 1;

        getExtrasVar();
        initButtons(rootView);

        return rootView;
    }

    /**
     * Inicializa los botones de la activity.
     *
     * @param rootView RootView
     */
    private void initButtons(View rootView)
    {
        RobotoButton btn_actualizar = (RobotoButton) rootView.findViewById(R.id.btn_actualizar);

        btn_actualizar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                final boolean       permitido = Funciones.FuncionalidadPermitida("Opciones", 0, id, manager);
                AlertDialog.Builder dialog    = new AlertDialog.Builder(contexto);

                dialog.setMessage(R.string.actualizar_app_dialogo);
                dialog.setCancelable(false);
                dialog.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (Funciones.isOnline(contexto))
                            if (permitido)
                                new actualizar_DatosApp(contexto, id, actualizar, usuario);
                            else
                                Toast.makeText(contexto, Constantes.NO_PERMITIDO, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(contexto, Constantes.NO_INTERNET, Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.setNegativeButton("No", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });

        RobotoButton btn_actualizar_app = (RobotoButton) rootView.findViewById(R.id.btn_actualizar_aplicacion);

        btn_actualizar_app.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(contexto);

                dialog.setMessage(R.string.actualizar_app_dialogo2);
                dialog.setCancelable(false);
                dialog.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (Funciones.isOnline(contexto))
                        {
                            //Buscar actualizaciones..
                            //version_actual = "";
                            try
                            {
                                version_actual = contexto.getPackageManager().getPackageInfo(contexto.getPackageName(), 0).versionName;
                                Log.d("Datos", "Version actual del sistema: " + version_actual);
                            }
                            catch (PackageManager.NameNotFoundException e)
                            {
                                e.printStackTrace();
                            }
                            finally
                            {
                                //Buscar la version online..
                                new actualizar_App(contexto, version_actual);
                            }
                        }
                        else
                            Toast.makeText(contexto, Constantes.NO_INTERNET, Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.setNegativeButton("No", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });
    }

    /**
     * Obtiene las variables adicionales que fueron pasadas como parametros desde otra actividad.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getArguments();
        usuario = bundle.getString("cedula");
        id = bundle.getString("id_usuario");
    }
}