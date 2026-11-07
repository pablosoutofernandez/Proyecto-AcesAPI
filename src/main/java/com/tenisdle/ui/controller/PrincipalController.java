package com.tenisdle.ui.controller;

import com.tenisdle.ui.ContextoApp;
import com.tenisdle.ui.Controlador;
import com.tenisdle.ui.Navegador;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

/**
 * Ventana con pestanas "Jugar" y "Tenistas".
 */
public class PrincipalController implements Controlador {

    @FXML
    private Tab tabJugar;
    @FXML
    private Tab tabTenistas;

    @Override
    public void init(ContextoApp contexto, Navegador navegador) {
        // A partir de aqui Partida, Fin y Estadisticas se muestran dentro de "Jugar"
        navegador.setPestanaJugar(tabJugar);

        // La pestana "Tenistas" se carga solo la primera vez que se abre,
        // asi no se consulta MySQL si nadie entra en ella
        tabTenistas.selectedProperty().addListener((obs, antes, seleccionada) -> {
            if (seleccionada && tabTenistas.getContent() == null) {
                tabTenistas.setContent(navegador.cargar("TenistasView.fxml"));
            }
        });
    }
}
