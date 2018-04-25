/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NStrg.dtos;

import java.util.ArrayList;

/**
 *
 * @author Fredy Arciniegas
 */
public class Producto {
  private String nombre;
  private String cod;
  private Imagen imagen;
  private ArrayList<Attb> myAtributos;
  private ArrayList<Ubicacion> myUbicaciones;

    public Producto() {
        this.myAtributos=new ArrayList<>();
        this.myUbicaciones=new ArrayList<>();
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public Imagen getImagen() {
        return imagen;
    }

    public void setImagen(Imagen imagen) {
        this.imagen = imagen;
    }

    public ArrayList<Attb> getMyAtributos() {
        return myAtributos;
    }

    public void setMyAtributos(ArrayList<Attb> myAtributos) {
        this.myAtributos = myAtributos;
    }

    public ArrayList<Ubicacion> getMyUbicaciones() {
        return myUbicaciones;
    }

    public void setMyUbicaciones(ArrayList<Ubicacion> myUbicaciones) {
        this.myUbicaciones = myUbicaciones;
    }
    
}
