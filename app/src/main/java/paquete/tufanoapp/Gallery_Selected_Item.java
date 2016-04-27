package paquete.tufanoapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

import paquete.recycle_bitmap.RecyclingImageView;

/**
 * Desarrollado por Gerson el 29/6/2015.
 */
@SuppressWarnings("SameParameterValue")
class Gallery_Selected_Item
{
    private static ArrayList<RecyclingImageView> array_c;
    private final Context contexto;

    /**
     * Constructor de la clase.
     *
     * @param contexto Contexto de la Aplicacion.
     */
    public Gallery_Selected_Item(Context contexto)
    {
        array_c = new ArrayList<>();
        this.contexto = contexto;
    }

    /**
     * Agrega un item a la galeria
     *
     * @param img Imagen a agregar.
     */
    public void agregar(RecyclingImageView img)
    {
        array_c.add(img);
    }

    public void activar(int n)
    {
        Drawable drawa = ContextCompat.getDrawable(contexto, R.drawable.gallery_border_selected);
        array_c.get(n).setBackground(drawa);
        //array_c.get(n).setBackground(contexto.getResources().getDrawable(R.drawable.gallery_border_selected));
    }

    public void estado_normal()
    {
        for (int i = 0; i < array_c.size(); i++)
        {
            Drawable drawa = ContextCompat.getDrawable(contexto, R.drawable.gallery_border);
            array_c.get(i).setBackground(drawa);
            //array_c.get(i).setBackground(contexto.getResources().getDrawable(R.drawable.gallery_border));
        }
    }
}
