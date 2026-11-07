package com.tenisdle.ui.util;

import com.tenisdle.database.BaseDatosException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;

/**
 * Dialogos para avisar al usuario. Hay que llamarlos desde el hilo de la interfaz.
 */
public final class Alertas {

    private Alertas() {
    }

    public static void error(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static void info(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    /**
     * Pregunta al usuario y devuelve true si pulsa Aceptar.
     */
    public static boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        // showAndWait() espera a que se cierre el dialogo y devuelve el boton pulsado
        return alerta.showAndWait().filter(boton -> boton == ButtonType.OK).isPresent();
    }

    /**
     * Convierte una excepcion del backend en un mensaje que entienda el usuario.
     */
    public static String mensajeDe(Throwable e) {
        if (e instanceof IllegalStateException && e.getMessage() != null
                && e.getMessage().contains("config.properties")) {
            return "No se encuentra el archivo config.properties.\n"
                    + "Copia config.properties.example como config.properties en la carpeta del proyecto y rellenalo.";
        }
        if (e instanceof BaseDatosException) {
            return "No se puede acceder a la base de datos MySQL.\n"
                    + "Comprueba que el servidor esta arrancado y que el usuario y la contrasena de config.properties son correctos.";
        }
        if (e instanceof IOException) {
            return "Error al descargar los datos de AcesAPI:\n" + e.getMessage();
        }
        return "Error inesperado: " + e.getMessage();
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
