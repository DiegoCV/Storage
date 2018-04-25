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
public class Ubicacion {
    private String bodega;
    private int cantidad;
    private ArrayList<Attb> myAtributos;

    public Ubicacion() {        
        this.myAtributos=new ArrayList<>();
    }

    
    public String getBodega() {
        return bodega;
    }

    public void setBodega(String bodega) {
        this.bodega = bodega;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public ArrayList<Attb> getMyAtributos() {
        return myAtributos;
    }

    public void setMyAtributos(ArrayList<Attb> myAtributos) {
        this.myAtributos = myAtributos;
    }
        
}
