/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NStrg.factory;

import NStrg.dtos.*;

/**
 *
 * @author Fredy Arciniegas
 */
public class Factory {

    public static Producto getProducto() {
        return new Producto();
    }

    public static Producto getProducto(String cod, String nombre) {
        Producto p = new Producto();
        p.setCod(cod);
        p.setNombre(nombre);
        return p;
    }

    public static Attb getAttb() {
        return new Attb();
    }

    public static Attb getAttb(String nombre, String valor) {
        Attb a = new Attb();
        a.setNombre(nombre);
        a.setValor(valor);
        return a;
    }

    public static Imagen getImagen() {
        return new Imagen();
    }

    public static Imagen getImagen(String imagen) {
        Imagen i = new Imagen();
        i.setImagen(imagen);
        return i;
    }
        
    public static Imagen getImagen(int id, String imagen) {
        Imagen i = new Imagen();
        i.setId(id);
        i.setImagen(imagen);
        return i;
    }

    public static Ubicacion getUbicacion() {
        return new Ubicacion();
    }

    public static Ubicacion getUbicacion(String bodega, int cantidad) {
        Ubicacion u = new Ubicacion();
        u.setBodega(bodega);
        u.setCantidad(cantidad);
        return u;
    }
}
