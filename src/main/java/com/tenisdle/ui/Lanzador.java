package com.tenisdle.ui;

import javafx.application.Application;

/**
 * Para ejecutar la GUI desde el IDE. Si se ejecuta directamente una clase que
 * extiende Application sin module-info, Java da el error
 * "JavaFX runtime components are missing"; arrancando desde aqui no pasa.
 */
public class Lanzador {

    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
