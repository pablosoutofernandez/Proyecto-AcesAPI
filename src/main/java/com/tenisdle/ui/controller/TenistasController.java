package com.tenisdle.ui.controller;

import com.tenisdle.model.Tenista;
import com.tenisdle.service.TenistaService;
import com.tenisdle.ui.ContextoApp;
import com.tenisdle.ui.Controlador;
import com.tenisdle.ui.Navegador;
import com.tenisdle.ui.componente.DialogoTenista;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    @FXML
    private VBox panel;
    @FXML
    private TextField txtId;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnBorrar;

    // Texto que muestra la tabla cuando no hay filas
    private final Label lblVacio = new Label("Sin resultados");

    private TenistaService servicio;
    private ContextoApp contexto;

    @Override
    public void init(ContextoApp contexto, Navegador navegador) {
        this.contexto = contexto;
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
        tabla.setPlaceholder(lblVacio);

        // Modificar y Borrar solo se pueden pulsar con una fila seleccionada.
        // Con bind, JavaFX los activa/desactiva solo cuando cambia la seleccion
        btnModificar.disableProperty().bind(tabla.getSelectionModel().selectedItemProperty().isNull());
        btnBorrar.disableProperty().bind(tabla.getSelectionModel().selectedItemProperty().isNull());
    }

    private void mostrarResultado(Supplier<List<Tenista>> consulta) {
        mostrarResultado(consulta, "Sin resultados");
    }

    /**
     * Ejecuta una consulta de TenistaService en otro hilo y pone el resultado en la tabla.
     * Todos los botones de la pestana lo usan, por ejemplo:
     * mostrarResultado(() -> servicio.filtrarPorPais("ESP"))
     *
     * @param textoSiVacio lo que se ve en la tabla si no hay resultados
     */
    private void mostrarResultado(Supplier<List<Tenista>> consulta, String textoSiVacio) {
        setControlesActivos(false);
        Task<List<Tenista>> tarea = new Task<>() {
            @Override
            protected List<Tenista> call() {
                return consulta.get();
            }
        };
        tarea.setOnSucceeded(e -> {
            List<Tenista> resultado = tarea.getValue();
            lblVacio.setText(textoSiVacio);
            tabla.setItems(FXCollections.observableArrayList(resultado));
            lblResultados.setText(resultado.size() + " resultados");
            setControlesActivos(true);
        });
        tarea.setOnFailed(e -> {
            Alertas.error("No se ha podido completar la operacion", Alertas.mensajeDe(tarea.getException()));
            setControlesActivos(true);
        });
        Tareas.ejecutar(tarea);
    }

    /**
     * Para crear, modificar y borrar: hace el cambio y despues vuelve a leer todos los
     * tenistas, que se muestran en la tabla y se guardan en ContextoApp
     * (Estadisticas usa esa lista para los nombres).
     */
    private void guardarCambio(Runnable cambio) {
        mostrarResultado(() -> {
            cambio.run();
            List<Tenista> todos = servicio.listarTodos();
            contexto.setTenistas(todos);
            return todos;
        });
    }

    private void setControlesActivos(boolean activos) {
        barra.setDisable(!activos);
        panel.setDisable(!activos);
    }

    @FXML
    private void onVerTodos() {
        mostrarResultado(servicio::listarTodos);
    }

    // ---------- CRUD ----------

    @FXML
    private void onLeer() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            Alertas.info("Buscar por id", "Escribe el id del tenista.");
            return;
        }
        // leer() devuelve null si no existe: la tabla queda vacia con el mensaje "No encontrado"
        mostrarResultado(() -> {
            Tenista t = servicio.leer(id);
            return t == null ? List.of() : List.of(t);
        }, "No encontrado ningun tenista con id " + id);
    }

    @FXML
    private void onNuevo() {
        // showAndWait() devuelve un Optional: solo tiene valor si se pulsa Aceptar
        new DialogoTenista(null).showAndWait()
                .ifPresent(t -> guardarCambio(() -> servicio.crear(t)));
    }

    @FXML
    private void onModificar() {
        Tenista seleccionado = tabla.getSelectionModel().getSelectedItem();
        new DialogoTenista(seleccionado).showAndWait()
                .ifPresent(t -> guardarCambio(() -> {
                    if (!servicio.modificar(t)) {
                        throw new IllegalArgumentException("El tenista " + t.getId() + " ya no existe");
                    }
                }));
    }

    @FXML
    private void onBorrar() {
        Tenista seleccionado = tabla.getSelectionModel().getSelectedItem();
        boolean confirmado = Alertas.confirmar("Borrar tenista",
                "Se va a borrar a " + seleccionado.getNombre()
                        + " y tambien sus partidas del historial. ¿Seguro?");
        if (confirmado) {
            guardarCambio(() -> servicio.borrar(seleccionado.getId()));
        }
    }
}
