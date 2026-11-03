package com.tenisdle.ui.controller;

import com.tenisdle.model.Tenista;
import com.tenisdle.service.CargaDatosService;
import com.tenisdle.ui.ContextoApp;
import com.tenisdle.ui.Controlador;
import com.tenisdle.ui.Navegador;
import com.tenisdle.ui.util.Alertas;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.util.List;

/**
 * Pantalla de inicio: carga los tenistas (de MySQL o de AcesAPI) en segundo plano
 * y cuando termina deja empezar la partida.
 */
public class InicioController implements Controlador {

    @FXML
    private ProgressIndicator progreso;
    @FXML
    private Label lblEstado;
    @FXML
    private Button btnJugar;
    @FXML
    private Button btnReintentar;

    private ContextoApp contexto;
    private Navegador navegador;

    @Override
    public void init(ContextoApp contexto, Navegador navegador) {
        this.contexto = contexto;
        this.navegador = navegador;
        cargarDatos();
    }

    private void cargarDatos() {
        btnJugar.setDisable(true);
        btnReintentar.setVisible(false);
        progreso.setVisible(true);

        // Task: trabajo que se ejecuta en otro hilo para no congelar la ventana
        Task<List<Tenista>> tarea = new Task<>() {
            @Override
            protected List<Tenista> call() throws Exception {
                updateMessage("Conectando con la base de datos...");
                // Se crea aqui dentro porque su constructor ya lee config.properties
                CargaDatosService carga = new CargaDatosService();
                if (carga.hayDatosEnBD()) {
                    updateMessage("Leyendo tenistas de la base de datos...");
                } else {
                    updateMessage("Descargando tenistas de AcesAPI (solo la primera vez)...");
                }
                return carga.cargar();
            }
        };

        // El texto de la etiqueta sigue a los updateMessage() del Task
        lblEstado.textProperty().bind(tarea.messageProperty());

        // setOnSucceeded y setOnFailed se ejecutan ya en el hilo de la interfaz,
        // por eso aqui se puede tocar la ventana sin Platform.runLater
        tarea.setOnSucceeded(e -> {
            List<Tenista> tenistas = tarea.getValue();
            contexto.setTenistas(tenistas);
            lblEstado.textProperty().unbind();
            lblEstado.setText(tenistas.size() + " tenistas cargados");
            progreso.setVisible(false);
            btnJugar.setDisable(false);
        });

        tarea.setOnFailed(e -> {
            lblEstado.textProperty().unbind();
            lblEstado.setText("No se han podido cargar los datos");
            progreso.setVisible(false);
            btnReintentar.setVisible(true);
            Alertas.error("Error al cargar los datos", Alertas.mensajeDe(tarea.getException()));
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true); // para que no impida cerrar la aplicacion
        hilo.start();
    }

    @FXML
    private void onJugar() {
        // TODO: navegador.mostrar("JuegoView.fxml") cuando exista la pantalla de partida
        Alertas.info("Pendiente", "La pantalla de partida todavia no esta hecha.");
    }

    @FXML
    private void onReintentar() {
        cargarDatos();
    }
}
