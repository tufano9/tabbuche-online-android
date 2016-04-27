package paquete.tufanoapp;

import android.util.Log;

import java.util.ArrayList;

import paquete.recycle_bitmap.RecyclingImageView;

/**
 * Desarrollado por Gerson el 19/6/2015.
 * Metodo de apoyo para ordenar tablas con datos (Tipo web)
 */
class sortingHeader
{
    private final ArrayList<RecyclingImageView> header;

    public sortingHeader()
    {
        header = new ArrayList<>();
    }

    public void agregar(RecyclingImageView img)
    {
        Log.i("sortingHeader", "agregar");
        header.add(img);
    }

    public void normalHeader()
    {
        Log.d("normalHeader","INIT");
        for (int i = 0; i < header.size(); i++)
        {
            header.get(i).setImageResource(R.drawable.sort_both);
            header.get(i).setTag("BOTH");
        }
    }
}