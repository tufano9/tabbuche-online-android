package paquete.tufanoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import paquete.adapters.NavigationAdapter;
import paquete.database.DBAdapter;
import paquete.global.Funciones;
import paquete.tufanoapp.cliente.FragmentCliente;
import paquete.tufanoapp.muestrario.FragmentMuestrario;
import paquete.tufanoapp.opciones.FragmentOpciones;
import paquete.tufanoapp.pedido.FragmentPedido_Menu;
import paquete.tufanoapp.perfil.FragmentPerfil;

@SuppressWarnings("deprecation")
@SuppressLint({"InflateParams", "Recycle"})
public class Home extends Activity
{
    public static ListView              NavList;
    public static String                TAG;
    private       String[]              titulos;
    private       ActionBarDrawerToggle mDrawerToggle;
    private       DrawerLayout          NavDrawerLayout;
    private String ci = "";
    private String                id_vendedor;
    private CharSequence          mTitle;
    private ArrayList<Item_objct> navItms;
    private Context               contexto;
    private String                nombre_vendedor, apellido_vendedor, email_vendedor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTitle = getTitle();
        contexto = this;

        getExtrasVar();
        //Declaramos el header el caul sera el layout de header.xml
        View header = getLayoutInflater().inflate(R.layout.header, null);
        initTextView(header);
        initNavDrawer(header);

        //Cuando la aplicacion cargue por defecto mostrar la opcion Home
        MostrarFragment(titulos[1]);
    }

    /**
     * Inicializa el navigation drawer (Barra lateral desplegable)
     *
     * @param header La barra (NavDrawer).
     */
    private void initNavDrawer(View header)
    {
        //Drawer Layout
        NavDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // set a custom shadow that overlays the main content when the drawer opens
        NavDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        //Lista
        NavList = (ListView) findViewById(R.id.lista);

        //Establecemos header
        NavList.addHeaderView(header);
        //NavList.addFooterView(null);
        //Tomamos listado  de imgs desde drawable
        TypedArray navIcons = getResources().obtainTypedArray(R.array.navigation_iconos);
        //Tomamos listado  de titulos desde el string-array de los recursos @string/nav_options
        titulos = getResources().getStringArray(R.array.opciones_home);

        //Listado de titulos de barra de navegacion
        navItms = crearMenuOpciones(navIcons);

        //Declaramos y seteamos nuestrp adaptador al cual le pasamos el array con los titulos
        NavigationAdapter navAdapter = new NavigationAdapter(this, navItms);
        NavList.setAdapter(navAdapter);

        //Declaramos el mDrawerToggle y las imgs a utilizar
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                NavDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* Icono de navegacion*/
                R.string.app_name,  /* "open drawer" description */
                R.string.hello_world  /* "close drawer" description */
        )
        {

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view)
            {
                //Log.e("Cerrado completo", "!!");
                //setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView)
            {
                //Log.e("Apertura completa", "!!");
                //mTitle = getTitle();
                //setTitle("Menu"); //mDrawerTitle
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Establecemos que mDrawerToggle declarado anteriormente sea el DrawerListener
        NavDrawerLayout.setDrawerListener(mDrawerToggle);
        // Establecemos que el ActionBar muestre el Boton Home
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        //Establecemos la accion al clickear sobre cualquier item del menu.
        //De la misma forma que hariamos en una app comun con un listview.
        NavList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)
            {
                String opc = navItms.get(position - 1).getTitulo();
                System.out.println("Menu presionado: " + opc);
                MostrarFragment(opc);
            }
        });
    }

    /**
     * Inicializa los textView.
     *
     * @param header La barra (NavDrawer).
     */
    private void initTextView(View header)
    {
        TextView online_user = (TextView) header.findViewById(R.id.online_user);
        String   user_name   = String.format(getResources().getString(R.string.user_name), nombre_vendedor, apellido_vendedor);
        online_user.setText(user_name);

        TextView online_email = (TextView) header.findViewById(R.id.online_email);
        online_email.setText(email_vendedor);
    }

    /**
     * Obtiene las variables adicionales que fueron pasadas como parametros desde otra actividad.
     */
    private void getExtrasVar()
    {
        Bundle bundle = getIntent().getExtras();
        ci = bundle.getString("cedula");
        id_vendedor = bundle.getString("id_vendedor");
        nombre_vendedor = bundle.getString("nombre_vendedor");
        apellido_vendedor = bundle.getString("apellido_vendedor");
        email_vendedor = bundle.getString("email_vendedor");
    }

    /**
     * Crea el menu de opciones de la navigation drawer.
     *
     * @param navIcons Iconos de la Navigation Drawer.
     * @return Menu de opciones construido.
     */
    private ArrayList<Item_objct> crearMenuOpciones(TypedArray navIcons)
    {
        ArrayList<Item_objct> navItms = new ArrayList<>();

        for (int i = 0; i < titulos.length; i++)
        {
            // Verificara cada opcion, para saber si se tiene permitido entrar en ella o no..
            /*if (menuValido(titulos[i]))
            {
                Log.i("crearMenuOpciones", "Agregado el menu " + titulos[i]);
                navItms.add(new Item_objct(titulos[i], navIcons.getResourceId(i, -1)));
            }*/

            // SOLO HABILITA EL MUESTRARIO
            if (titulos[i].equals("Muestrario"))
            {
                Log.i("crearMenuOpciones", "Agregado el menu " + titulos[i]);
                navItms.add(new Item_objct(titulos[i], navIcons.getResourceId(i, -1)));
                break;
            }
        }

        return navItms;
    }

    /**
     * Verifica si la opcion indicada por el titulo esta habilitada para el usuario.
     *
     * @param titulo Titulo de la opcion que se desea verificar si existe.
     * @return True si la opcion esta habilitada.
     */
    private boolean menuValido(String titulo)
    {
        DBAdapter manager = new DBAdapter(contexto);

        return Funciones.Funcionalidad_Menu_Permitida(titulo, id_vendedor, manager);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        mTitle = title;
        if (getActionBar() != null)
            getActionBar().setTitle(mTitle);
    }

    /* Pasando la posicion de la opcion en el menu nos mostrara el Fragment correspondiente */
    private void MostrarFragment(String opcion)
    {
        // update the main content by replacing fragments
        Fragment fragment = null;

        switch (opcion)
        {
            //Iba a partir de 1 antes de la modificacion..
            /*case "Home":
                fragment = new FragmentHome();
	            break;*/
            case "Muestrario":
                fragment = new FragmentMuestrario();
                TAG = "fragment_muestrario";
                break;
            case "Pedidos": // Pedidos
                fragment = new FragmentPedido_Menu();
                TAG = "fragment_pedido";
                break;
            case "Clientes": // Clientes
                fragment = new FragmentCliente();
                TAG = "fragment_cliente";
                break;
	        /*case "E_Cuenta": // Estados de cuenta
	            fragment = new FragmentMuestrario();
	            break;*/
            case "Perfil": // Perfil
                fragment = new FragmentPerfil();
                break;
            case "Opciones": // Opciones
                fragment = new FragmentOpciones();
                TAG = "fragment_opciones";
                break;
            default:
                //si no esta la opcion mostrara un toast..
                //if (position!=0)
                Toast.makeText(getApplicationContext(), "Opcion " + opcion + " no disponible!", Toast.LENGTH_SHORT).show();
                //position=1;
                break;
        }

        //Validamos si el fragment no es nulo
        if (fragment != null)
        {
            Bundle args = new Bundle();
            args.putString("id_usuario", id_vendedor);
            args.putString("cedula", ci);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(TAG).commit();

            int position = buscarPosicionItemMenu(opcion);

            // Actualizamos el contenido segun la opcion elegida
            NavList.setItemChecked(position, true);
            NavList.setSelection(position);
            //Cambiamos el titulo
            setTitle(opcion); // antes iba titulos[position-1]
            //Cerramos el menu deslizable
            NavDrawerLayout.closeDrawer(NavList);
        }
    }

    /**
     * Busca la posicion del item en el menu.
     *
     * @param opcion Opcion a buscar.
     * @return Posicion del item encontrado. -1 Si no encontro el item.
     */
    private int buscarPosicionItemMenu(String opcion)
    {
        for (int i = 0; i < navItms.size(); i++)
        {
            if (opcion.equals(navItms.get(i).getTitulo()))
            {
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item))
        {
            Log.e("mDrawerToggle pushed", "x");
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (NavDrawerLayout.isDrawerOpen(GravityCompat.START)) // Si la barra lateral esta abierta, la cierra.
        {
            NavDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            // Catch back action and pops from backstack
            // (if you called previously to addToBackStack() in your transaction)
            if (getFragmentManager().getBackStackEntryCount() > 1)
            {
                Log.d("FRAGMENT HOME TAG", TAG);
                switch (TAG)
                {
                    case "fragment_cliente":
                        switch (FragmentCliente.TAG_CLIENTE)
                        {
                            case "fragment_cliente_agregar":
                                Log.d("FRAGMENT", FragmentCliente.TAG_CLIENTE);

                                AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                                dialog.setMessage(R.string.onbackPressed_AddCliente);
                                dialog.setCancelable(false);
                                dialog.setPositiveButton("Si", new DialogInterface.OnClickListener()
                                {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        getFragmentManager().popBackStack();
                                        FragmentCliente.TAG_CLIENTE = "fragment_cliente_vacio";
                                        setTitle(titulos[1]);
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
                                break;
                            /*case "fragment_cliente_editar":
                                Log.d("FRAGMENT", "fragment_cliente_editar");
                                getFragmentManager().popBackStack();
                                break;
                            case "fragment_cliente_editar_fin":
                                Log.d("FRAGMENT", "fragment_cliente_editar_fin");
                                getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                break;
                            case "fragment_cliente_agregar_fin":
                                Log.d("FRAGMENT", "fragment_cliente_agregar_fin");
                                getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                break;
                            case "fragment_cliente_consultar":
                                Log.d("FRAGMENT", FragmentCliente.TAG_CLIENTE);
                                getFragmentManager().popBackStack();
                                break;*/
                            default:
                                Log.d("FRAGMENT DEFAULT", FragmentCliente.TAG_CLIENTE);
                                getFragmentManager().popBackStack();
                                setTitle(titulos[1]);
                                break;
                        }
                        break;

                    case "fragment_muestrario":
                        Log.d("FRAGMENT", "fragment_muestrario");
                        getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        setTitle(titulos[1]);
                        break;

                    case "fragment_opciones":
                        Log.d("FRAGMENT", "fragment_opciones");
                        getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        //getFragmentManager().popBackStack();
                        setTitle(titulos[1]);
                        break;

                    case "fragment_pedido":
                        Log.d("FRAGMENT", "fragment_pedido");
                        getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        //getFragmentManager().popBackStack();
                        setTitle(titulos[1]);
                        break;

                    default:
                        Log.d("FRAGMENT default", TAG);
                        getFragmentManager().popBackStack();
                        //getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        setTitle(titulos[1]);
                        break;
                }
            }
            // Default action on back pressed
            else
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                dialog.setMessage(R.string.cerrar_sesion);
                dialog.setCancelable(false);
                dialog.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
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
        }
    }
}