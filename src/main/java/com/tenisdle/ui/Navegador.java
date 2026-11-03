package com.tenisdle.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/**
 * Cambia de pantalla: carga el FXML de la vista, le pasa el contexto
 * a su controlador y lo pone en la ventana principal.
 */
public class Navegador {

    private static final String CARPETA_VISTAS = "/com/tenisdle/ui/view/";
    private static final String CSS = "/com/tenisdle/ui/css/estilos.css";

    private final Stage ventana;
    private final ContextoApp contexto;

    public Navegador(Stage ventana, ContextoApp contexto) {
        this.ventana = ventana;
        this.contexto = contexto;
    }

    /**
     * Muestra una vista, por ejemplo mostrar("InicioView.fxml").
     */
    public void mostrar(String fxml) {
        URL ruta = getClass().getResource(CARPETA_VISTAS + fxml);
        if (ruta == null) {
            throw new IllegalArgumentException("No existe la vista " + fxml);
        }
        try {
            FXMLLoader loader = new FXMLLoader(ruta);
            Parent raiz = loader.load();

            // El controlador lo crea JavaFX al cargar el FXML (atributo fx:controller)
            Controlador controlador = loader.getController();
            controlador.init(contexto, this);

            if (ventana.getScene() == null) {
                Scene escena = new Scene(raiz, 900, 600);
                escena.getStylesheets().add(getClass().getResource(CSS).toExternalForm());
                ventana.setScene(escena);
            } else {
                // Reutilizamos la escena para no perder el tamano de la ventana ni el CSS
                ventana.getScene().setRoot(raiz);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error cargando la vista " + fxml, e);
        }
    }
}
