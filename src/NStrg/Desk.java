/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NStrg;

import NStrg.factory.Factory;
import NStrg.dtos.*;
import NStrg.storer.Storer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Fredy Arciniegas
 */
public class Desk {

    private static Connection con;

    public static void setConexion(Connection con) {
        Desk.con = con;
    }

    private static Producto getBasicProducto(Producto p) throws SQLException {
        String sql = "select p.cod,p.nombre from producto p "
                + "where p.cod=" + p.getCod();
        ResultSet rs = Storer.exceuteQuery(sql, con);

        p = Factory.getProducto();
        p.setCod(rs.getString("cod"));
        p.setNombre(rs.getString("nombre"));

        return p;
    }

    private static ArrayList<Producto> listBasicProductos() throws SQLException {
        String sql = "select cod from producto p";
        ResultSet rs = Storer.exceuteQuery(sql, con);

        ArrayList<Producto> productos = new ArrayList();
        while (rs.next()) {
            Producto p = Factory.getProducto(rs.getString("cod"), "");
            productos.add(p);
        }

        return productos;
    }

    /**
     * Busca los atributos propios de un producto a partir de su código.
     *
     * @param p Producto con código
     * @return Producto con código, nombre, imagen y atributos con sus valores
     * @throws SQLException
     */
    public static Producto getProducto(Producto p) throws SQLException {

        p = Desk.getBasicProducto(p);

        String sql = "select i.imagen from producto p "
                + "inner join imagen i on p.img=i.id "
                + "where p.cod=" + p.getCod();
        ResultSet rs = Storer.exceuteQuery(sql, con);

        p.setImagen(Factory.getImagen(rs.getString("imagen")));

        sql = "select a.atributo,v.valor from valp v "
                + "inner join attbp a on v.atributo=a.id "
                + "where v.obj=" + p.getCod();
        rs = Storer.exceuteQuery(sql, con);

        ArrayList<Attb> attbs = new ArrayList();
        while (rs.next()) {
            attbs.add(Factory.getAttb(rs.getString("atributo"), rs.getString("valor")));
        }
        p.setMyAtributos(attbs);
        return p;
    }

    /**
     * Lista los atributos propios de todos los productos de la base de datos.
     *
     * @return ArrayList de Productos con código, nombre, imagen y atributos con
     * sus valores
     * @throws SQLException
     */
    public static ArrayList<Producto> listProductos() throws SQLException {
        ArrayList<Producto> productos = new ArrayList();
        for (Producto p : Desk.listBasicProductos()) {
            productos.add(Desk.getProducto(p));
        }
        return productos;
    }

    /**
     * Lista las ubicaciones de un producto específico.
     *
     * @param p Producto con código.
     * @return ArrayList de Ubicaciones con idBodega y cantidad.
     * @throws SQLException
     */
    private static ArrayList<Ubicacion> listUbicaciones(Producto p) throws SQLException {
        String sql = "select b.nombre, u.cantidad from ubicacion u"
                + "inner join bodega b on u.idBodega=b.id "
                + "where u.codProducto=" + p.getCod();
        ResultSet rs = Storer.exceuteQuery(sql, con);
        ArrayList<Ubicacion> ubicaciones = new ArrayList();
        while (rs.next()) {
            Ubicacion u = Factory.getUbicacion(rs.getString("nombre"), rs.getInt("cantidad"));
            u.setMyAtributos(Desk.listAtributosU(Desk.getIdUbicacion(p.getCod(), u.getBodega())));
            ubicaciones.add(u);
        }
        return ubicaciones;
    }

    private static int getIdUbicacion(String codProducto, String bodega) throws SQLException {
        String sql = "select id from ubicacion u "
                + "inner join bodega b on u.idBodega=b.id "
                + "where b.nombre=´" + bodega + "´ AND codProducto=´" + codProducto + "´";
        ResultSet rs = Storer.exceuteQuery(sql, con);

        return rs.getInt("id");
    }

    private static ArrayList<Attb> listAtributosU(int id) throws SQLException {
        String sql = "select a.atributo,v.valor from valu v "
                + "inner join attbu a on v.atributo=a.id "
                + "where v.obj=" + id;
        ResultSet rs = Storer.exceuteQuery(sql, con);

        ArrayList<Attb> attbs = new ArrayList();
        while (rs.next()) {
            attbs.add(Factory.getAttb(rs.getString("atributo"), rs.getString("valor")));
        }
        return attbs;
    }

    /**
     * Lista las características de cada ubicación donde se encuentre un
     * producto determinado.
     *
     * @param p Producto con código
     * @return Un producto con código, nombre y un listado de
     * ubicaciones{bodega,cantidad,listado de atributos de ubicación}
     * @throws SQLException
     */
    public static Producto getProductoUbicacion(Producto p) throws SQLException {
        p = Desk.getBasicProducto(p);
        p.setMyUbicaciones(Desk.listUbicaciones(p));

        return p;
    }

    /**
     * Lista todos los productos y las caracterìsticas de la ubicaciòn de cada
     * uno.
     *
     * @return Listado de productos con código, nombre y un listado de
     * ubicaciones{bodega,cantidad,listado de atributos de ubicación}
     * @throws SQLException
     */
    public static ArrayList<Producto> listProductosUbicacion() throws SQLException {
        ArrayList<Producto> productos = new ArrayList();
        for (Producto p : Desk.listBasicProductos()) {
            productos.add(Desk.getProductoUbicacion(p));
        }
        return productos;
    }

    public static boolean insertProducto(Producto p) {
        /*
            (imagen,cod,nombre,attbp{attb,valor}[],ubicaciones{bodega,CANT,attbu{attb,valor}[]}[])
        
            idImg=insert into imagen (imagen) values (<p.getImagen().getImagen()>)
            insert into producto (cod,nombre,img) values (<p.getCod()>,<p.getNombre()>,idImg)
            fore(Attb a ->p.getMyAtributos()){
                insert into valp (attb,obj,valor) values (<a.getNombre()>,<p.getCod()>,<a.getValor()>)
            }            
            fore(Ubicacion u->p.getMyUbicaciones()){                
                idUbicacion=getIdUbicacion(p.getCod(),u.getBodega())
                if(idUbicacion!=¿try?¿null?¿-1?){
                    update ubicacion set cantidad=cantidad+<u.getCantidad()>
                }else{
                    idUbicacion=insert into ubicacion (idBodega,codProducto) values (<idBodega>,<p.getCod()>)                    
                }
                fore(Attb a ->u.getMyAtributos()){
                    insert into valu (attb,obj,valor) values (<a.getNombre()>,<idUbicacion>,<a.getValor()>)
                }
            }
         */
        return false;
    }

    public void updateCantidad(Producto p) {
        /*
        fore(Ubicacion u->p.getMyUbicaciones()){
            idBodega=select id from bodega where nombre=<u.getBodega>
            update ubicacion set cantidad=<u.getCantidad> where idBodega=<idBodega> AND codProducto=<p.getCod()>
        }
         */
    }

    public static void insertAtributo(Attb a, boolean forProducto) {
        /*
        String table=""
        if(forProducto){
        table=attbp
        }else{
        table=attbu
        }
        insert into +table+ (atributo) values (a.getNombre())
         */
    }

    public static void updateValorP(Producto p) {
        /*
        fore(Attb a=p.getMyAtributos()){
            idAttb=select id from attbp where atributo=<a.getNombre()>
            update valp set valor=a.getValor() where obj=<p.getCod()> and atributo=<idAttb>
        }
         */
    }

    public static void updateValorU(Producto p) {
        /*
        fore(Ubicacion u=p.getMyUbicaciones()){
            idUbicacion=getIdUbicacion(p.getCod(),u.getBodega())
            fore(Attb a=u.getMyAtributos()){
                update valu set valor=a.getValor() where obj=<idUbicacion> and atributo in (select id from attbu where atributo=<a.getNombre()>)
            }
        }
         */
    }

    public static void listAtributos(boolean forProducto) {
        /*
        if(forProducto){
        select atributo from attbp
        }else{
        select atributo from attbu        
        }
        Array<attb> arr
        a.setNombre()
        a.setValor(NULL)
        arr.add()
         */
    }

    public static void insertBodega(String nombre) {
        /*
        id=insert into bodega (nombre) values (nombre)
        return id? nadie lo va a usar en la aplicación
         */
    }

    public static void listBodegas() {
        /*
        select id,nombre from bodega
        new Bodega()
        .setId()?
        .setNombre()
         */
    }

    public static void deleteBodega(String nombre) {
        /*
        
        delete from bodega where nombre=<nombre>
        
        por cascada:
            bodega  ->ubicacion
                    ->ubicacion
                    ->valu        
         */
    }

    public static void deleteProducto(Producto p) {
        /*
        
        delete from producto where cod=<p.getCod()>
        
        por cascada:
            producto->imagen
                    ->valp
                    ->ubicacion
                    ->valu        
         */
    }

    public static void deleteUbicacion(Producto p) {
        /*
        fore(Ubicacion u=p.getMyUbicaciones()){
            idUbicacion=getIdUbicacion(p.getCod(),u.getBodega())
            delete from ubicacion where id=<idUbicacion>
        }        
        por cascada:
            ubicacion->valu
         */
    }

}
