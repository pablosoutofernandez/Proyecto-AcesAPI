package com.tenisdle.ui.controller;

import com.tenisdle.model.Intento;
import com.tenisdle.model.Tenista;
import com.tenisdle.service.JuegoService;
import com.tenisdle.ui.nucleo.ContextoApp;
import com.tenisdle.ui.nucleo.Controlador;
import com.tenisdle.ui.nucleo.Navegador;
import com.tenisdle.ui.componente.FilaIntento;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Pantalla de fin de partida. Los datos se leen de JuegoService,
 * que sigue guardando la partida que acaba de terminar.
 */
public class FinController implements Controlador {

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblSubtitulo;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblPais;
    @FXML
    private Label lblEdad;
    @FXML
    private Label lblMano;
    @FXML
    private Label lblRanking;
    @FXML
    private Label lblPuntos;
    @FXML
    private VBox filasIntentos;

    private Navegador navegador;

    @Override
    public void init(ContextoApp contexto, Navegador navegador) {
        this.navegador = navegador;
        JuegoService juego = contexto.getJuegoService();

        if (juego.isGanada()) {
            lblTitulo.setText("¡Has ganado!");
            lblTitulo.getStyleClass().add("victoria");
            lblSubtitulo.setText("Lo has adivinado en " + juego.getIntentos().size()
                    + " de " + JuegoService.MAX_INTENTOS + " intentos");
        } else {
            lblTitulo.setText("Has perdido");
            lblTitulo.getStyleClass().add("derrota");
            lblSubtitulo.setText("Has agotado los " + JuegoService.MAX_INTENTOS + " intentos");
        }

        mostrarSecreto(juego.getSecreto());

        // Resumen en orden: el primer intento arriba
        for (Intento intento : juego.getIntentos()) {
            filasIntentos.getChildren().add(new FilaIntento(intento));
        }
    }

    private void mostrarSecreto(Tenista t) {
        lblNombre.setText(t.getNombre());
        lblPais.setText(t.getPais());
        lblEdad.setText(t.getEdad() + " años");
        lblMano.setText(t.getMano());
        lblRanking.setText("#" + t.getRanking());
        lblPuntos.setText(String.valueOf(t.getPuntos()));
    }

    @FXML
    private void onJugarOtraVez() {
        // JuegoController.init() ya empieza una partida nueva
        navegador.mostrarEnJugar("JuegoView.fxml");
    }

    @FXML
    private void onEstadisticas() {
        navegador.mostrarEnJugar("EstadisticasView.fxml");
    }

    @FXML
    private void onVolverInicio() {
        navegador.mostrar("InicioView.fxml");
    }
}
