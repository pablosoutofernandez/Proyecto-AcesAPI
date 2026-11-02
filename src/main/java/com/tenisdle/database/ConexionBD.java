package com.tenisdle.database;

import com.tenisdle.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexion con MySQL. La primera vez que se pide una conexion
 * se crean las tablas si no existen (la BD la crea el parametro
 * createDatabaseIfNotExist de la URL).
 */
public final class ConexionBD {

    private static final String CREAR_TENISTAS = """
            CREATE TABLE IF NOT EXISTS tenistas (
                id               VARCHAR(20)  PRIMARY KEY,
                nombre           VARCHAR(100) NOT NULL,
                pais             CHAR(3)      NOT NULL,
                fecha_nacimiento DATE,
                mano             VARCHAR(10),
                ranking          INT,
                puntos           INT
            )""";

    private static final String CREAR_PARTIDAS = """
            CREATE TABLE IF NOT EXISTS partidas (
                id         INT AUTO_INCREMENT PRIMARY KEY,
                tenista_id VARCHAR(20) NOT NULL,
                fecha      DATETIME    NOT NULL,
                intentos   INT         NOT NULL,
                ganada     BOOLEAN     NOT NULL,
                FOREIGN KEY (tenista_id) REFERENCES tenistas(id) ON DELETE CASCADE
            )""";

    private static boolean tablasCreadas = false;

    private ConexionBD() {
    }

    /**
     * Abre una conexion nueva. Hay que cerrarla con try-with-resources.
     */
    public static Connection getConexion() throws SQLException {
        AppConfig config = AppConfig.getInstance();
        Connection conexion = DriverManager.getConnection(
                config.getDbUrl(), config.getDbUser(), config.getDbPassword());
        if (!tablasCreadas) {
            crearTablas(conexion);
        }
        return conexion;
    }

    private static synchronized void crearTablas(Connection conexion) throws SQLException {
        if (tablasCreadas) {
            return;
        }
        try (Statement st = conexion.createStatement()) {
            st.executeUpdate(CREAR_TENISTAS);
            st.executeUpdate(CREAR_PARTIDAS);
        }
        tablasCreadas = true;
    }
}
