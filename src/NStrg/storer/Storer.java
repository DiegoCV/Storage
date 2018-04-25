/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NStrg.storer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Fredy Arciniegas
 */
public class Storer {

    /*
    inyectar sqls genéricos a lo loco    
     */
    public static ResultSet exceuteUpdate(String sql, Connection cn) throws SQLException {
        try {
            PreparedStatement consulta = cn.prepareStatement(sql);
            consulta.executeUpdate();
            ResultSet rs = consulta.getGeneratedKeys();
            consulta.close();
            return rs;
        } catch (SQLException ex) {
            throw new SQLException(ex.getMessage()
                    + "\nHubo un problema al ejecutar su actualización."
                    + "\nSeguramente es un error tonto que no debió pasar, como su código o su vida."
                    + "\n" + sql);
        }
    }

    public static ResultSet exceuteQuery(String sql, Connection cn) throws SQLException {
        try {
            PreparedStatement consulta = cn.prepareStatement(sql);
            ResultSet rs = consulta.executeQuery();
            consulta.close();
            return rs;
        } catch (SQLException ex) {
            throw new SQLException(ex.getMessage()
                    + "\nHubo un problema al buscar en el inventario."
                    + "\nEs interesante saber que si hubiera contratado buenos developers, esto no pasaría."
                    + "\n" + sql);
        }
    }
}
