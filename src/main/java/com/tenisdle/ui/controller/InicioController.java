package com.tenisdle.ui.controller;

import com.tenisdle.model.Tenista;
import com.tenisdle.service.CargaDatosService;
import com.tenisdle.ui.ContextoApp;
import com.tenisdle.ui.Controlador;
import com.tenisdle.ui.Navegador;
import com.tenisdle.ui.util.Alertas;
import com.tenisdle.ui.util.Tareas;
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
    private Button btnEstadisticas;
    @FXML
    private Button btnReintentar;

    private ContextoApp contexto;
    private Navegador navegador;

    @Override
    public void init(ContextoApp contexto, Navegador navegador) {
        this.contexto = contexto;
        this.navegador = navegador;

        // Si se vuelve al inicio despues de jugar, los tenistas ya estan cargados
        if (!contexto.getTenistas().isEmpty()) {
            lblEstado.setText(contexto.getTenistas().size() + " tenistas cargados");
            progreso.setVisible(false);
            btnReintentar.setVisible(false);
            btnJugar.setDisable(false);
            btnEstadisticas.setDisable(false);
        } else {
            cargarDatos();
        }
    }

    private void cargarDatos() {
        btnJugar.setDisable(true);
        btnEstadisticas.setDisable(true);
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
            btnEstadisticas.setDisable(false);
        });

        tarea.setOnFailed(e -> {
            lblEstado.textProperty().unbind();
            lblEstado.setText("No se han podido cargar los datos");
            progreso.setVisible(false);
            btnReintentar.setVisible(true);
            Alertas.error("Error al cargar los datos", Alertas.mensajeDe(tarea.getException()));
        });

        Tareas.ejecutar(tarea);
    }

    @FXML
    private void onJugar() {
        navegador.mostrar("JuegoView.fxml");
    }

    @FXML
    private void onEstadisticas() {
        navegador.mostrar("EstadisticasView.fxml");
    }

    @FXML
    private void onReintentar() {
        cargarDatos();
    }
}
