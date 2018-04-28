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
 *
 * SPONSORED BY \(x.x )/ ANARCHY \( x.x)/ 
 * Así es, ahora metemos publicidad en otros proyectos
 */
public class Desk {

    private static Connection con;

    public static void setConexion(Connection con) {
        Desk.con = con;
    }

    private static Producto getBasicProducto(Producto p) throws SQLException, NullPointerException {
        try {
            String sql = "select p.cod,p.nombre from producto p "
                    + "where p.cod=´" + p.getCod() + "´";
            ResultSet rs = Storer.exceuteQuery(sql, con);

            p = Factory.getProducto();
            p.setCod(rs.getString("cod"));
            p.setNombre(rs.getString("nombre"));

            return p;
        } catch (NullPointerException ex) {
            throw Desk.totiarNullPointer();
        }
    }

    private static ArrayList<Producto> listBasicProductos() throws SQLException {
        String sql = "select cod from producto";
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
     * @throws NullPointerException
     */
    public static Producto getProducto(Producto p) throws SQLException, NullPointerException {
        try {
            p = Desk.getBasicProducto(p);

            String sql = "select imagen from imagen "
                    + "where codProducto=´" + p.getCod() + "´";
            ResultSet rs = Storer.exceuteQuery(sql, con);

            p.setImagen(Factory.getImagen(rs.getString("imagen")));

            sql = "select a.atributo,v.valor from valp v "
                    + "inner join attbp a on v.atributo=a.id "
                    + "where v.obj=´" + p.getCod() + "´";
            rs = Storer.exceuteQuery(sql, con);

            ArrayList<Attb> attbs = new ArrayList();
            while (rs.next()) {
                attbs.add(Factory.getAttb(rs.getString("atributo"), rs.getString("valor")));
            }
            p.setMyAtributos(attbs);
        } catch (NullPointerException ex) {
            throw Desk.totiarNullPointer();
        }
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
     * @throws NullPointerException
     */
    private static ArrayList<Ubicacion> listUbicaciones(Producto p) throws SQLException, NullPointerException {
        try {
            String sql = "select b.nombre, u.cantidad from ubicacion u"
                    + "inner join bodega b on u.idBodega=b.id "
                    + "where u.codProducto=´" + p.getCod() + "´";
            ResultSet rs = Storer.exceuteQuery(sql, con);
            ArrayList<Ubicacion> ubicaciones = new ArrayList();
            while (rs.next()) {
                Ubicacion u = Factory.getUbicacion(rs.getString("nombre"), rs.getInt("cantidad"));
                u.setMyAtributos(Desk.listAtributosU(Desk.getIdUbicacion(p.getCod(), u.getBodega())));
                ubicaciones.add(u);
            }
            return ubicaciones;
        } catch (NullPointerException ex) {
            throw Desk.totiarNullPointer();
        }
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
                + "where v.obj=´" + id + "´";
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
     * @throws NullPointerException
     */
    public static Producto getProductoUbicacion(Producto p) throws SQLException, NullPointerException {
        try {
            p = Desk.getBasicProducto(p);
            p.setMyUbicaciones(Desk.listUbicaciones(p));
        } catch (NullPointerException ex) {
            throw Desk.totiarNullPointer();
        }
        return p;

    }

    /**
     * Lista todos los productos y las caracterìsticas de la ubicaciòn de cada
     * uno.
     *
     * @return Listado de productos con código, nombre y un listado de
     * ubicaciones{bodega,cantidad,listado de atributos de ubicación}
     * @throws SQLException
     * @throws NullPointerException
     */
    public static ArrayList<Producto> listProductosUbicacion() throws SQLException, NullPointerException {
        ArrayList<Producto> productos = new ArrayList();
        for (Producto p : Desk.listBasicProductos()) {
            productos.add(Desk.getProductoUbicacion(p));
        }
        return productos;
    }

    /**
     * Lista todos los productos de una bodega, y las características de cada
     * uno.
     *
     * @param bodega Nombre de la bodega a consultar
     * @return Listado de productos con código, nombre y un listado de
     * ubicaciones{bodega,cantidad,listado de atributos de ubicación}
     * @throws SQLException
     * @throws NullPointerException
     */
    public static ArrayList<Producto> listProductosByBodega(String bodega) throws SQLException, NullPointerException {
        ArrayList<Producto> productos = new ArrayList();
        String sql = "select codProducto from ubicacion u "
                + "inner join bodega b on u.idBodega=b.id"
                + "where b.nombre=" + bodega;
        ResultSet rs = Storer.exceuteQuery(sql, con);
        while (rs.next()) {
            Producto p = Factory.getProducto(rs.getString("codProducto"), "");
            p = Desk.getProducto(p);
            p = Desk.getProductoUbicacion(p);
            productos.add(p);
        }
        return productos;
    }
    
    private static void insertImagen(Producto p) throws SQLException, NullPointerException {        
        try {
            String sql = "insert into imagen (codProducto,imagen) values (´" + p.getCod() + "´,´" + p.getImagen().getImagen() + "´)";
            Storer.exceuteUpdate(sql, con);
            sql = "insert into producto (cod,nombre) values (´" + p.getCod() + "´,´" + p.getNombre() + "´)";
            Storer.exceuteUpdate(sql, con);
        } catch (NullPointerException ex) {
            throw Desk.totiarNullPointer();
        }
        
    }

    /**
     * Inserta un producto con todas sus características
     *
     * @param p Producto con { imagen,código,nombre, listado de
     * attbp{attb,valor}, listado de ubicaciones{ bodega,CANTIDADES CALCULADAS,
     * listado de attbu{attb,valor} (si no se encuentra registrado) } }
     * @throws SQLException
     * @throws NullPointerException
     */
    public static void insertProducto(Producto p) throws SQLException, NullPointerException {
        Desk.BEGIN();
        try {
            Desk.insertImagen(p);
            String sql;
            for (Attb a : p.getMyAtributos()) {
                sql = "select id from attbp where atributo=´" + a.getNombre() + "´";
                ResultSet rs = Storer.exceuteQuery(sql, con);
                int idAttb = rs.getInt("id");
                sql = "insert into valp (attb,obj,valor) values (" + idAttb + ",´" + p.getCod() + "´,´" + a.getValor() + "´)";
                Storer.exceuteUpdate(sql, con);
            }
            for (Ubicacion u : p.getMyUbicaciones()) {
                int idUbicacion = Desk.getIdUbicacion(p.getCod(), u.getBodega());
                if (idUbicacion > 0) {
                    sql = "update ubicacion set cantidad=" + u.getCantidad();
                    Storer.exceuteUpdate(sql, con);
                } else {
                    sql = "select id from bodega where nombre=´" + u.getBodega() + "´";
                    ResultSet rs = Storer.exceuteQuery(sql, con);
                    int idBodega = rs.getInt(1);
                    sql = "insert into ubicacion (idBodega,codProducto) values (" + idBodega + "," + p.getCod() + ")";
                    rs = Storer.exceuteUpdate(sql, con);
                    idUbicacion = rs.getInt(1);
                    for (Attb a : u.getMyAtributos()) {
                        sql = "insert into valu (attb,obj,valor) values (" + a.getNombre() + "," + idUbicacion + "," + a.getValor() + ")";
                        Storer.exceuteUpdate(sql, con);
                    }
                }
            }
        } catch (NullPointerException ex) {
            throw Desk.totiarNullPointer();
        }
        Desk.COMMIT();
    }

    /**
     *
     * @param p Producto con código y una o más ubicaciones, cada una con el
     * nombre de la bodega y la nueva cantidad YA CALCULADA
     * @throws SQLException
     * @throws NullPointerException
     */
    public void updateCantidad(Producto p) throws SQLException, NullPointerException {
        Desk.BEGIN();
        try {
            for (Ubicacion u : p.getMyUbicaciones()) {
                int idUbicacion = Desk.getIdUbicacion(p.getCod(), u.getBodega());
                String sql = "update ubicacion set cantidad=" + u.getCantidad() + " "
                        + "where id=" + idUbicacion;
                Storer.exceuteUpdate(sql, con);
            }
        } catch (NullPointerException ex) {
            throw Desk.totiarNullPointer();
        }
        Desk.COMMIT();
    }

    /**
     * Registra un atributo (categoría) para producto o ubicación.
     *
     * @param a Atributo con nombre
     * @param forProducto Booleano que responde a ¿es para un producto?
     * @throws SQLException
     * @throws NullPointerException
     */
    public static void insertAtributo(Attb a, boolean forProducto) throws SQLException, NullPointerException {
        try {
            String table;
            if (forProducto) {
                table = "attbp";
            } else {
                table = "attbu";
            }
            String sql = "insert into " + table + " (atributo) values (" + a.getNombre() + ")";
            Storer.exceuteUpdate(sql, con);
        } catch (NullPointerException ex) {
            throw Desk.totiarNullPointer();
        }
    }

    private static int getIdAtributo(String nombre, boolean forProducto) throws SQLException {
        String tableName;
        if (forProducto) {
            tableName = "attbp";
        } else {
            tableName = "attbu";
        }
        String sql = "select id from " + tableName + " where atributo=" + nombre;
        ResultSet rs = Storer.exceuteQuery(sql, con);
        return rs.getInt("id");
    }

    /**
     *
     * @param p Producto con código y uno o más atributos{nombre, valor} cuyos
     * valores SERÁN CAMBIADOS. Si un valor no quiere ser cambiado, no lo meta
     * aquí.
     * @throws SQLException
     * @throws NullPointerException
     */
    public static void updateValorP(Producto p) throws SQLException, NullPointerException {
        Desk.BEGIN();
        try {
            for (Attb a : p.getMyAtributos()) {
                int idAttb = Desk.getIdAtributo(a.getNombre(), true);
                String sql = "update valp set valor=" + a.getValor() + " where obj=" + p.getCod() + " and atributo=" + idAttb;
                Storer.exceuteUpdate(sql, con);
            }
        } catch (NullPointerException ex) {
            throw Desk.totiarNullPointer();
        }
        Desk.COMMIT();
    }

    /**
     *
     * @param p Producto con código y una o más ubicaciones. Cada ubicación con
     * uno o más atributos{nombre, valor} cuyos valores SERÁN CAMBIADOS. Si un
     * valor no quiere ser cambiado, no lo meta aquí.
     * @throws SQLException
     * @throws NullPointerException
     */
    public static void updateValorU(Producto p) throws SQLException, NullPointerException {
        Desk.BEGIN();
        try {
            for (Ubicacion u : p.getMyUbicaciones()) {
                int idUbicacion = Desk.getIdUbicacion(p.getCod(), u.getBodega());
                for (Attb a : u.getMyAtributos()) {
                    int idAttb = Desk.getIdAtributo(a.getNombre(), false);
                    String sql = "update valu set valor=" + a.getValor() + " where obj=" + idUbicacion + " and atributo=" + idAttb;
                    Storer.exceuteUpdate(sql, con);
                }
            }
        } catch (NullPointerException ex) {
            throw Desk.totiarNullPointer();
        }
        Desk.COMMIT();
    }

    /**
     * Devuelve los nombres de los atributos registrados en la tabla
     * seleccionada.
     *
     * @param forProducto Booleano que responde a la pregunta ¿es para un
     * producto?
     * @return ArrayList<String> con los nombres de las bodegas.
     * @throws SQLException
     */
    public static ArrayList<String> listAtributos(boolean forProducto) throws SQLException {
        String tableName;
        if (forProducto) {
            tableName = "attbp";
        } else {
            tableName = "attbu";
        }
        String sql = "select atributo from " + tableName + "";
        ResultSet rs = Storer.exceuteQuery(sql, con);
        ArrayList<String> arr = new ArrayList();
        while (rs.next()) {
            arr.add(rs.getString("atributo"));
        }
        return arr;
    }

    /**
     * Registra una bodega en la base de datos.
     *
     * @param nombre Nombre de la bodega.
     * @throws SQLException
     */
    public static void insertBodega(String nombre) throws SQLException {
        String sql = "insert into bodega (nombre) values (´" + nombre + "´)";
        Storer.exceuteUpdate(sql, con);
    }

    /**
     * Devuelve los nombres de las bodegas registradas.
     *
     * @return ArrayList<String> con los nombres de las bodegas.
     * @throws SQLException
     */
    public static ArrayList<String> listBodegas() throws SQLException {
        String sql = "select nombre from bodega";
        ResultSet rs = Storer.exceuteQuery(sql, con);
        ArrayList<String> bodegas = new ArrayList<>();
        while (rs.next()) {
            bodegas.add(rs.getString("nombre"));
        }
        return bodegas;
    }

    /**
     * Borra una bodega y sus ubicaciones a partir de un nombre de bodega
     * @param nombre
     * @throws SQLException
     */
    public static void deleteBodega(String nombre) throws SQLException {
        String sql="Delete from bodega where nombre=´"+nombre+"´";
        Storer.exceuteUpdate(sql, con);
    }

    /**
     * Elimina un producto de la base de datos, así como sus características,imagen y ubicaciones
     * @param p Producto con código.
     * @throws SQLException
     * @throws NullPointerException
     */
    public static void deleteProducto(Producto p) throws SQLException,NullPointerException  {
        try{
        String sql="delete from producto where cod=´"+p.getCod()+"´";
        Storer.exceuteUpdate(sql, con);
        }catch(NullPointerException ex){
            throw Desk.totiarNullPointer();
        }
    }

    /**
     * Borra una o más ubicaciones, así como sus características, donde se encuentre un producto.
     * @param p Producto con ubicaciones que SE BORRARÁN.     
     * @throws SQLException
     * @throws NullPointerException
     */
    public static void deleteUbicacion(Producto p) throws SQLException,NullPointerException {
        try{
        for (Ubicacion u : p.getMyUbicaciones()) {
            int idUbicacion=Desk.getIdUbicacion(p.getCod(),u.getBodega());
            String sql="delete from ubicacion where id="+idUbicacion;
            Storer.exceuteUpdate(sql, con);            
        }            
        }catch(NullPointerException ex){
            throw Desk.totiarNullPointer();
        }
    }

    private static NullPointerException totiarNullPointer() {
        return new NullPointerException("Tarado >:c tanto me mato en escribir la documentación para que no la leas\n"
                + "ingresa los parámetros que son ( ¬.¬)");
    }

    private static void BEGIN() throws SQLException {
        Storer.exceuteUpdate("BEGIN", con);
    }

    private static void COMMIT() throws SQLException {
        Storer.exceuteUpdate("COMMIT", con);
    }
}
