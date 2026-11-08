package com.tenisdle.ui.nucleo;

/**
 * Lo implementan todos los controladores. El Navegador llama a init()
 * justo despues de cargar el FXML para pasarle los servicios.
 */
public interface Controlador {

    void init(ContextoApp contexto, Navegador navegador);
}
