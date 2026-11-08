package com.tenisdle.ui.controller;

import com.tenisdle.model.Intento;
import com.tenisdle.model.Tenista;
import com.tenisdle.service.JuegoService;
import com.tenisdle.ui.nucleo.ContextoApp;
import com.tenisdle.ui.nucleo.Controlador;
import com.tenisdle.ui.nucleo.Navegador;
import com.tenisdle.ui.componente.FilaIntento;
import com.tenisdle.ui.util.Alertas;
import com.tenisdle.ui.util.FiltroTenistas;
import com.tenisdle.ui.util.Tareas;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import java.util.Comparator;

/**
 * Pantalla de partida: buscador de tenistas, contador de intentos
 * y una fila de pistas por cada intento. Toda la logica del juego
 * (elegir secreto, comparar, guardar) la hace JuegoService.
 */
public class JuegoController implements Controlador {

    @FXML
    private Label lblIntentos;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ListView<Tenista> lstSugerencias;
    @FXML
    private Button btnIntentar;
    @FXML
    private VBox filasIntentos;

    private JuegoService juego;
    private Navegador navegador;
    private FilteredList<Tenista> sugerencias;

    @Override
    public void init(ContextoApp contexto, Navegador navegador) {
        this.juego = contexto.getJuegoService();
        this.navegador = navegador;
        configurarBuscador(contexto);
        nuevaPartida();
    }

    private void configurarBuscador(ContextoApp contexto) {
        // FilteredList es una "vista" de la lista original: al cambiar el predicado
        // la ListView se actualiza sola, sin tener que rellenarla a mano
        var ordenados = contexto.getTenistas().stream()
                .sorted(Comparator.comparing(Tenista::getRanking))
                .toList();
        sugerencias = new FilteredList<>(FXCollections.observableArrayList(ordenados), t -> false);
        lstSugerencias.setItems(sugerencias);

        // Cada vez que cambia el texto se cambia el filtro y se marca la primera sugerencia
        txtBuscar.textProperty().addListener((obs, viejo, nuevo) -> {
            sugerencias.setPredicate(FiltroTenistas.porNombre(nuevo));
            lstSugerencias.getSelectionModel().selectFirst();
        });

        // Enter en el buscador o doble clic en una sugerencia = intentar
        txtBuscar.setOnAction(e -> onIntentar());
        lstSugerencias.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                onIntentar();
            }
        });
    }

    private void nuevaPartida() {
        setBuscadorActivo(false);

        // nuevaPartida() lee los tenistas de MySQL, asi que va en otro hilo
        Task<Void> tarea = new Task<>() {
            @Override
            protected Void call() {
                juego.nuevaPartida();
                return null;
            }
        };
        tarea.setOnSucceeded(e -> {
            filasIntentos.getChildren().clear();
            txtBuscar.clear();
            actualizarContador();
            setBuscadorActivo(true);
            txtBuscar.requestFocus();
        });
        tarea.setOnFailed(e -> Alertas.error("No se puede empezar la partida",
                Alertas.mensajeDe(tarea.getException())));
        Tareas.ejecutar(tarea);
    }

    @FXML
    private void onIntentar() {
        if (juego.isTerminada()) {
            return;
        }
        Tenista elegido = lstSugerencias.getSelectionModel().getSelectedItem();
        if (elegido == null && !sugerencias.isEmpty()) {
            elegido = sugerencias.get(0);
        }
        if (elegido == null) {
            Alertas.info("Elige un tenista", "Escribe un nombre y elige un tenista de la lista.");
            return;
        }

        Tenista tenista = elegido; // las lambdas necesitan una variable final
        setBuscadorActivo(false);

        // intentar() guarda la partida en MySQL cuando termina, por eso va en otro hilo
        Task<Intento> tarea = new Task<>() {
            @Override
            protected Intento call() {
                return juego.intentar(tenista);
            }
        };
        tarea.setOnSucceeded(e -> {
            // El ultimo intento se pone arriba del todo
            filasIntentos.getChildren().add(0, new FilaIntento(tarea.getValue()));
            txtBuscar.clear();
            actualizarContador();
            if (juego.isTerminada()) {
                navegador.mostrarEnJugar("FinView.fxml");
            } else {
                setBuscadorActivo(true);
                txtBuscar.requestFocus();
            }
        });
        tarea.setOnFailed(e -> {
            Throwable error = tarea.getException();
            // IllegalArgumentException = tenista repetido, no gasta intento
            if (error instanceof IllegalArgumentException) {
                Alertas.info("Tenista repetido", error.getMessage());
            } else {
                Alertas.error("Error en el intento", Alertas.mensajeDe(error));
            }
            setBuscadorActivo(true);
        });
        Tareas.ejecutar(tarea);
    }

    private void actualizarContador() {
        lblIntentos.setText("Intentos restantes: " + juego.getIntentosRestantes() + "/" + JuegoService.MAX_INTENTOS);
    }

    private void setBuscadorActivo(boolean activo) {
        txtBuscar.setDisable(!activo);
        lstSugerencias.setDisable(!activo);
        btnIntentar.setDisable(!activo);
    }
}
