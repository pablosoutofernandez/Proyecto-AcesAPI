package com.tenisdle.ui.nucleo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/**
 * Cambia de pantalla: carga el FXML de la vista, le pasa el contexto
 * a su controlador y lo pone en la ventana o dentro de la pestana "Jugar".
 */
public class Navegador {

    private static final String CARPETA_VISTAS = "/com/tenisdle/ui/view/";
    private static final String CSS = "/com/tenisdle/ui/css/estilos.css";

    private final Stage ventana;
    private final ContextoApp contexto;
    private Tab pestanaJugar;

    public Navegador(Stage ventana, ContextoApp contexto) {
        this.ventana = ventana;
        this.contexto = contexto;
    }

    /**
     * Carga una vista y le pasa el contexto a su controlador, pero no la muestra.
     * Devuelve la raiz para ponerla donde haga falta (ventana o pestana).
     */
    public Parent cargar(String fxml) {
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
            return raiz;
        } catch (IOException e) {
            throw new UncheckedIOException("Error cargando la vista " + fxml, e);
        }
    }

    /**
     * Muestra una vista ocupando toda la ventana, por ejemplo mostrar("InicioView.fxml").
     */
    public void mostrar(String fxml) {
        Parent raiz = cargar(fxml);
        if (ventana.getScene() == null) {
            Scene escena = new Scene(raiz, 900, 600);
            escena.getStylesheets().add(getClass().getResource(CSS).toExternalForm());
            ventana.setScene(escena);
        } else {
            // Reutilizamos la escena para no perder el tamano de la ventana ni el CSS
            ventana.getScene().setRoot(raiz);
        }
    }

    /**
     * Lo llama PrincipalController para indicar en que pestana van Partida, Fin y Estadisticas.
     */
    public void setPestanaJugar(Tab pestana) {
        this.pestanaJugar = pestana;
    }

    /**
     * Cambia solo el contenido de la pestana "Jugar"; las pestanas siguen visibles.
     */
    public void mostrarEnJugar(String fxml) {
        if (pestanaJugar == null) {
            throw new IllegalStateException("Hay que abrir PrincipalView.fxml antes de usar mostrarEnJugar");
        }
        pestanaJugar.setContent(cargar(fxml));
        pestanaJugar.getTabPane().getSelectionModel().select(pestanaJugar);
    }

    /**
     * Abre la ventana con pestanas y pone la vista indicada dentro de "Jugar".
     */
    public void abrirPrincipal(String vistaJugar) {
        mostrar("PrincipalView.fxml");
        mostrarEnJugar(vistaJugar);
    }
}
