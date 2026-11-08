package com.tenisdle.ui;

import com.tenisdle.model.Tenista;
import com.tenisdle.service.JuegoService;
import com.tenisdle.service.TenistaService;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos compartidos por todas las pantallas: los servicios (se crean una sola vez
 * al arrancar) y la lista de tenistas cargada en la pantalla de inicio.
 */
public class ContextoApp {

    private final TenistaService tenistaService;
    private final JuegoService juegoService;
    private List<Tenista> tenistas = new ArrayList<>();

    public ContextoApp(TenistaService tenistaService, JuegoService juegoService) {
        this.tenistaService = tenistaService;
        this.juegoService = juegoService;
    }

    public TenistaService getTenistaService() {
        return tenistaService;
    }

    public JuegoService getJuegoService() {
        return juegoService;
    }

    public List<Tenista> getTenistas() {
        return tenistas;
    }

    public void setTenistas(List<Tenista> tenistas) {
        this.tenistas = tenistas;
    }
}
