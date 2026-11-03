package com.tenisdle.ui.util;

import javafx.concurrent.Task;

/**
 * Lanza un Task de JavaFX en un hilo aparte para no congelar la ventana.
 */
public final class Tareas {

    private Tareas() {
    }

    public static void ejecutar(Task<?> tarea) {
        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true); // para que no impida cerrar la aplicacion
        hilo.start();
    }
}
