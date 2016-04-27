package paquete.dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Clase para la creacion de un dialogo informativo.
 */
public class DialogoAlerta extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("La actualizacion ha concluido.")
                .setTitle("Información")
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                });

        return builder.create();
    }
}