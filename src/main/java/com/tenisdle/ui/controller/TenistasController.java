package com.tenisdle.ui.controller;

import com.tenisdle.model.Tenista;
import com.tenisdle.service.TenistaService;
import com.tenisdle.ui.ContextoApp;
import com.tenisdle.ui.Controlador;
import com.tenisdle.ui.Navegador;
import com.tenisdle.ui.util.Alertas;
import com.tenisdle.ui.util.Tareas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.function.Supplier;

/**
 * Pestana "Tenistas": consultas y gestion de tenistas con TenistaService.
 * La tabla siempre muestra el resultado de la ultima consulta.
 */
public class TenistasController implements Controlador {

    @FXML
    private HBox barra;
    @FXML
    private TableView<Tenista> tabla;
    @FXML
    private TableColumn<Tenista, String> colNombre;
    @FXML
    private TableColumn<Tenista, String> colPais;
    @FXML
    private TableColumn<Tenista, String> colEdad;
    @FXML
    private TableColumn<Tenista, String> colMano;
    @FXML
    private TableColumn<Tenista, String> colRanking;
    @FXML
    private TableColumn<Tenista, String> colPuntos;
    @FXML
    private Label lblResultados;

    private TenistaService servicio;

    @Override
    public void init(ContextoApp contexto, Navegador navegador) {
        this.servicio = contexto.getTenistaService();
        configurarTabla();
        mostrarResultado(servicio::listarTodos);
    }

    // Cada columna dice que texto saca de cada Tenista
    private void configurarTabla() {
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPais.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPais()));
        colEdad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getEdad())));
        colMano.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMano()));
        colRanking.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getRanking())));
        colPuntos.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPuntos())));
        tabla.setPlaceholder(new Label("Sin resultados"));
    }

    /**
     * Ejecuta una consulta de TenistaService en otro hilo y pone el resultado en la tabla.
     * Todos los botones de la pestana lo usan, por ejemplo:
     * mostrarResultado(() -> servicio.filtrarPorPais("ESP"))
     */
    private void mostrarResultado(Supplier<List<Tenista>> consulta) {
        barra.setDisable(true);
        Task<List<Tenista>> tarea = new Task<>() {
            @Override
            protected List<Tenista> call() {
                return consulta.get();
            }
        };
        tarea.setOnSucceeded(e -> {
            List<Tenista> resultado = tarea.getValue();
            tabla.setItems(FXCollections.observableArrayList(resultado));
            lblResultados.setText(resultado.size() + " resultados");
            barra.setDisable(false);
        });
        tarea.setOnFailed(e -> {
            Alertas.error("Error en la consulta", Alertas.mensajeDe(tarea.getException()));
            barra.setDisable(false);
        });
        Tareas.ejecutar(tarea);
    }

    @FXML
    private void onVerTodos() {
        mostrarResultado(servicio::listarTodos);
    }
}
