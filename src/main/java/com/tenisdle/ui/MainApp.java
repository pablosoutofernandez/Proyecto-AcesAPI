package com.tenisdle.ui;

import com.tenisdle.service.JuegoService;
import com.tenisdle.service.TenistaService;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto de entrada de la interfaz grafica.
 * Crea los servicios una sola vez y muestra la pantalla de inicio.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage ventana) {
        // Estos constructores no tocan la BD ni config.properties, asi que es seguro
        // crearlos aqui en el hilo de la interfaz
        ContextoApp contexto = new ContextoApp(new TenistaService(), new JuegoService());
        Navegador navegador = new Navegador(ventana, contexto);

        ventana.setTitle("Tenisdle");
        navegador.mostrar("InicioView.fxml");
        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
