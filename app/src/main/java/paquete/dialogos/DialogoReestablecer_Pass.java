package paquete.dialogos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import paquete.Droidlogin.library.Httppostaux;
import paquete.global.Constantes;
import paquete.global.Funciones;
import paquete.tufanoapp.R;

public class DialogoReestablecer_Pass extends DialogFragment
{
    private EditText email_text;
    private ProgressDialog pDialog;
    private Dialog dialogo;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.dialogo_contrasena, null);
        email_text = (EditText) v.findViewById(R.id.email_dialogo);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.btn_ok, null)
                .setTitle(R.string.titulo_restablecer)
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_email)
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        DialogoReestablecer_Pass.this.getDialog().cancel();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                Editable value = email_text.getText();
                if(Funciones.isValidEmail(value.toString()))
                {
                    Log.i("PRUEBA", "Email invalido " + value);
                    Toast.makeText(getActivity(), "Por favor ingrese un email valido!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Log.i("PRUEBA","Email: "+value);
                    if(Funciones.isOnline(getActivity()))
                    {
                        new enviarCorreo().execute(value.toString());
                        dialogo = dialog;
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Debe estar conectado a una red!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return dialog;
    }

    /**
     * Envia un email a la direccion indicada para re-establecer la cuenta y su password.
     * @param email Correo electronico donde la cuenta esta registrada.
     * @return True si la operacion fue exitosa, False en caso contrario.
     */
    private boolean enviarCorreo(String email)
    {
        boolean bandera = false;
        Httppostaux post = new Httppostaux();

        ArrayList<NameValuePair> listaParametros= new ArrayList<>();
        listaParametros.add(new BasicNameValuePair("email",email));

        String URL_connect = Constantes.IP_Server +"recuperar_pass.php";
        JSONArray jdata= post.getserverdata(listaParametros, URL_connect);

        if (jdata!=null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);

                String logstatus=json_data.getString("logstatus");

                switch (logstatus) {
                    case "1":
                        bandera = true;
                        //cliente_existe=false;
                        break;
                    case "2":
                        bandera = false;
                        //cliente_existe=true;
                        break;
                    default:
                        bandera = false;
                        // Email no existente
                        break;
                }
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Log.e("JSON  ", "ERROR");
            return false;
        }

        return bandera;
    }

    /**
     * Clase para el envio del correo de re-establecimiento de la cuenta en segundo plano.
     */
    class enviarCorreo extends AsyncTask< String, String, String >
    {
        String email;
        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Por favor espere...");
            pDialog.setMessage("Enviando email...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            //obtnemos usr y pass
            email=params[0];

            try
            {
                //enviamos y recibimos y analizamos los datos en segundo plano.
                if (enviarCorreo(email))
                {
                    return "ok"; //login valido
                }

                else
                {
                    return "err"; //login invalido
                }
            }

            catch (RuntimeException e)
            {
                return "err2";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();

            if (result.equals("ok"))
            {
                Toast.makeText(getActivity(), "Se le ha enviado un correo para reestablecer su contraseña!", Toast.LENGTH_LONG).show();
                dialogo.dismiss();
            }

            else
            {
                Toast.makeText(getActivity(), "El correo ingresado es erroneo!", Toast.LENGTH_LONG).show();
            }

        }
    }
}