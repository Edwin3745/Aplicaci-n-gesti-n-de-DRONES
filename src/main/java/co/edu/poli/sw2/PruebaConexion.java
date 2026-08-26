package co.edu.poli.sw2;

import co.edu.poli.sw2.Config.ConexionBD;
import java.sql.Connection;

public class PruebaConexion {
    public static void main(String[] args) {
        try (Connection con = ConexionBD.obtenerConexion()) {
            System.out.println("✅ Conexión exitosa a PostgreSQL");
        } catch (Exception e) {
            System.out.println("❌ Error al conectar:");
            e.printStackTrace();
        }
    }
}