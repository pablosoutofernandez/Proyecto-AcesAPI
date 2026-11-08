package com.tenisdle.ui.controller;

import com.tenisdle.model.Partida;
import com.tenisdle.model.Tenista;
import com.tenisdle.service.JuegoService;
import com.tenisdle.ui.nucleo.ContextoApp;
import com.tenisdle.ui.nucleo.Controlador;
import com.tenisdle.ui.nucleo.Navegador;
import com.tenisdle.ui.util.Alertas;
import com.tenisdle.ui.util.Tareas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pantalla de estadisticas: numeros del historial y tabla con todas las partidas.
 * Los calculos los hace JuegoService; aqui solo se muestran.
 */
public class EstadisticasController implements Controlador {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private Label lblPartidas;
    @FXML
    private Label lblVictorias;
    @FXML
    private Label lblMedia;
    @FXML
    private TableView<Partida> tabla;
    @FXML
    private TableColumn<Partida, String> colFecha;
    @FXML
    private TableColumn<Partida, String> colTenista;
    @FXML
    private TableColumn<Partida, String> colIntentos;
    @FXML
    private TableColumn<Partida, String> colResultado;
    @FXML
    private Button btnBorrar;
    @FXML
    private Button btnVolver;

    private JuegoService juego;
    private Navegador navegador;
    // id del tenista -> nombre, para mostrar el nombre en la tabla (Partida solo guarda el id)
    private final Map<String, String> nombres = new HashMap<>();

    // Lo que devuelve el Task: las tres consultas juntas
    private record Datos(List<Partida> historial, double porcentaje, double media) {
    }

    @Override
    public void init(ContextoApp contexto, Navegador navegador) {
        this.juego = contexto.getJuegoService();
        this.navegador = navegador;
        for (Tenista t : contexto.getTenistas()) {
            nombres.put(t.getId(), t.getNombre());
        }
        configurarTabla();
        cargar();
    }

    // Cada columna dice que texto saca de cada Partida
    private void configurarTabla() {
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().format(FORMATO_FECHA)));
        colTenista.setCellValueFactory(c -> new SimpleStringProperty(
                nombres.getOrDefault(c.getValue().getTenistaId(), c.getValue().getTenistaId())));
        colIntentos.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIntentos())));
        colResultado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isGanada() ? "Ganada" : "Perdida"));
        tabla.setPlaceholder(new Label("Todavia no has jugado ninguna partida"));
    }

    private void cargar() {
        setBotonesActivos(false);

        // Las tres llamadas consultan MySQL, asi que van en otro hilo
        Task<Datos> tarea = new Task<>() {
            @Override
            protected Datos call() {
                return new Datos(juego.getHistorial(), juego.porcentajeVictorias(), juego.mediaIntentos());
            }
        };
        tarea.setOnSucceeded(e -> {
            Datos datos = tarea.getValue();
            lblPartidas.setText(String.valueOf(datos.historial().size()));
            lblVictorias.setText(String.format("%.1f%%", datos.porcentaje()));
            lblMedia.setText(String.format("%.2f", datos.media()));
            tabla.setItems(FXCollections.observableArrayList(datos.historial()));
            setBotonesActivos(true);
        });
        tarea.setOnFailed(e -> {
            Alertas.error("No se pueden cargar las estadisticas", Alertas.mensajeDe(tarea.getException()));
            btnVolver.setDisable(false);
        });
        Tareas.ejecutar(tarea);
    }

    @FXML
    private void onBorrar() {
        if (!Alertas.confirmar("Borrar historial", "Se van a borrar todas las partidas guardadas. ¿Seguro?")) {
            return;
        }
        setBotonesActivos(false);
        Task<Void> tarea = new Task<>() {
            @Override
            protected Void call() {
                juego.borrarHistorial();
                return null;
            }
        };
        tarea.setOnSucceeded(e -> cargar());
        tarea.setOnFailed(e -> {
            Alertas.error("No se puede borrar el historial", Alertas.mensajeDe(tarea.getException()));
            setBotonesActivos(true);
        });
        Tareas.ejecutar(tarea);
    }

    @FXML
    private void onVolver() {
        navegador.mostrar("InicioView.fxml");
    }

    private void setBotonesActivos(boolean activos) {
        btnBorrar.setDisable(!activos);
        btnVolver.setDisable(!activos);
    }
}
