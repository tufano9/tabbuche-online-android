package paquete.tufanoapp;

import java.util.ArrayList;

import paquete.global.Constantes;
import paquete.recycle_bitmap.RecyclingImageView;

/**
 * Desarrollado por Gerson el 4/6/2015.
 */
public class imageAlphaClass
{
    private static ArrayList<RecyclingImageView> array_lineas;
    private static ArrayList<RecyclingImageView> array_modelos;
    private static ArrayList<RecyclingImageView> array_productos;

    /**
     * Constructor por defecto de la clase.
     */
    public imageAlphaClass()
    {
        array_lineas = new ArrayList<>();
        array_modelos = new ArrayList<>();
        array_productos = new ArrayList<>();
    }

    /**
     * Agrega una linea a la clase.
     *
     * @param c Imagen de la linea.
     */
    public void agregar_linea(RecyclingImageView c)
    {
        array_lineas.add(c);
    }

    /**
     * Agrega un modelo a la clase.
     *
     * @param c Imagen del modelo.
     */
    public void agregar_modelo(RecyclingImageView c)
    {
        array_modelos.add(c);
    }

    /**
     * Agrega un producto a la clase.
     *
     * @param c Imagen del producto.
     */
    public void agregar_producto(RecyclingImageView c)
    {
        array_productos.add(c);
    }

    /**
     * Aplica un factor Alpha a todas las imagenes de las lineas que existan en esta clase. (Las
     * vuelve mas transparentes y claras)
     */
    public static void ocultar_lineas()
    {
        for (int i = 0; i < array_lineas.size(); i++)
            array_lineas.get(i).setAlpha(Constantes.IMAGE_ALPHA);
    }

    /**
     * Aplica un factor Alpha a todas las imagenes de los modelos que existan en esta clase. (Las
     * vuelve mas transparentes y claras)
     */
    public static void ocultar_modelos()
    {
        for (int i = 0; i < array_modelos.size(); i++)
            array_modelos.get(i).setAlpha(Constantes.IMAGE_ALPHA);
    }

    /**
     * Aplica un factor Alpha a todas las imagenes de los productos que existan en esta clase. (Las
     * vuelve mas transparentes y claras)
     */
    public static void ocultar_productos()
    {
        for (int i = 0; i < array_productos.size(); i++)
            array_productos.get(i).setAlpha(Constantes.IMAGE_ALPHA);
    }
}