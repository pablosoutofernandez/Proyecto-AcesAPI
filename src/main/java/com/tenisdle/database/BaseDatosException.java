package com.tenisdle.database;

/**
 * Error de acceso a la base de datos.
 * Envuelve la SQLException para no tener que declararla en cada metodo,
 * la interfaz la puede capturar y mostrar el mensaje al usuario.
 */
public class BaseDatosException extends RuntimeException {

    public BaseDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
