package com.tenisdle.ui.componente;

import com.tenisdle.model.Tenista;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Formulario para crear o modificar un tenista.
 * showAndWait() devuelve el Tenista con los datos escritos, o nada si se cancela.
 * La validacion de los datos la hace TenistaService al guardar.
 */
public class DialogoTenista extends Dialog<Tenista> {

    private final TextField txtId = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtPais = new TextField();
    private final DatePicker dpFecha = new DatePicker();
    private final ComboBox<String> cmbMano = new ComboBox<>();
    private final Spinner<Integer> spRanking = new Spinner<>(1, 10000, 1);
    private final Spinner<Integer> spPuntos = new Spinner<>(0, 100000, 0);

    /**
     * @param tenista null para crear uno nuevo, o el tenista a modificar
     */
    public DialogoTenista(Tenista tenista) {
        setTitle(tenista == null ? "Nuevo tenista" : "Modificar tenista");
        setHeaderText(tenista == null ? "Datos del nuevo tenista" : "Modificar " + tenista.getNombre());

        cmbMano.getItems().addAll(Tenista.DIESTRO, Tenista.ZURDO);
        txtPais.setPromptText("ESP, ITA...");
        spRanking.setEditable(true);
        spPuntos.setEditable(true);

        if (tenista != null) {
            rellenar(tenista);
            // El id es la clave en la BD, no se puede cambiar
            txtId.setDisable(true);
        }

        // Formulario en dos columnas: etiqueta | campo
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.addRow(0, new Label("Id"), txtId);
        grid.addRow(1, new Label("Nombre"), txtNombre);
        grid.addRow(2, new Label("Pais"), txtPais);
        grid.addRow(3, new Label("Fecha de nacimiento"), dpFecha);
        grid.addRow(4, new Label("Mano"), cmbMano);
        grid.addRow(5, new Label("Ranking"), spRanking);
        grid.addRow(6, new Label("Puntos"), spPuntos);
        getDialogPane().setContent(grid);

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Aceptar desactivado hasta elegir mano y fecha: el backend falla si son null
        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(
                cmbMano.valueProperty().isNull().or(dpFecha.valueProperty().isNull()));

        // Convierte el boton pulsado en el resultado de showAndWait()
        setResultConverter(boton -> boton == ButtonType.OK ? leerFormulario() : null);
    }

    private void rellenar(Tenista t) {
        txtId.setText(t.getId());
        txtNombre.setText(t.getNombre());
        txtPais.setText(t.getPais());
        dpFecha.setValue(t.getFechaNacimiento());
        cmbMano.setValue(t.getMano());
        spRanking.getValueFactory().setValue(t.getRanking());
        spPuntos.getValueFactory().setValue(t.getPuntos());
    }

    private Tenista leerFormulario() {
        return new Tenista(
                txtId.getText().trim(),
                txtNombre.getText().trim(),
                txtPais.getText().trim().toUpperCase(),
                dpFecha.getValue(),
                cmbMano.getValue(),
                leerNumero(spRanking),
                leerNumero(spPuntos));
    }

    // Si el usuario escribe en el Spinner sin pulsar Enter, el valor aun no se ha guardado:
    // se lee lo escrito y, si no es un numero, se usa el ultimo valor valido
    private static int leerNumero(Spinner<Integer> spinner) {
        try {
            return Integer.parseInt(spinner.getEditor().getText().trim());
        } catch (NumberFormatException e) {
            return spinner.getValue();
        }
    }
}
