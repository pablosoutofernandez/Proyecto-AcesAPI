package com.tenisdle.ui.componente;

import com.tenisdle.model.Intento;
import com.tenisdle.model.Pista;
import com.tenisdle.ui.util.EstiloPista;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Una fila de la rejilla de la partida: el nombre del tenista
 * y una casilla de color por cada pista.
 */
public class FilaIntento extends HBox {

    public FilaIntento(Intento intento) {
        getStyleClass().add("fila");

        Label nombre = new Label(intento.tenista().getNombre());
        nombre.getStyleClass().addAll("casilla", "casilla-nombre", intento.correcto() ? "acierto" : "fallo");
        getChildren().add(nombre);

        for (Pista pista : intento.pistas()) {
            Label casilla = new Label(EstiloPista.texto(pista));
            casilla.getStyleClass().addAll("casilla", EstiloPista.claseCss(pista.resultado()));
            getChildren().add(casilla);
        }
    }
}
