package paquete.tufanoapp;

public class lista_entrada_galeria
{
    //private final int idImagen;
    private final String textoDebajo, idProducto;

    public lista_entrada_galeria(String textoDebajo, String idProducto)
    {
        //this.idImagen = idImagen;
        this.textoDebajo = textoDebajo;
        this.idProducto = idProducto;
    }

    public String get_textoDebajo()
    {
        return textoDebajo;
    }

    public String get_idProducto()
    {
        return idProducto;
    }

    /*public int get_idImagen()
    {
        return idImagen;
    }*/
}
