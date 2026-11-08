package com.tenisdle.ui.controller;

import com.tenisdle.model.Tenista;
import com.tenisdle.service.TenistaService;
import com.tenisdle.storage.ExportadorCsv;
import com.tenisdle.ui.nucleo.ContextoApp;
import com.tenisdle.ui.nucleo.Controlador;
import com.tenisdle.ui.nucleo.Navegador;
import com.tenisdle.ui.componente.DialogoTenista;
import com.tenisdle.ui.util.Alertas;
import com.tenisdle.ui.util.Campos;
import com.tenisdle.ui.util.Tareas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<String> cmbPais;
    @FXML
    private ComboBox<String> cmbMano;
    @FXML
    private Spinner<Integer> spTop;
    @FXML
    private ComboBox<String> cmbOrden;
    @FXML
    private Label lblEdadMedia;
    @FXML
    private Label lblNumPaises;
    @FXML
    private TableView<FilaPais> tablaPaises;
    @FXML
    private TableColumn<FilaPais, String> colPaisEst;
    @FXML
    private TableColumn<FilaPais, String> colNumTenistas;

    // Una fila de la tabla de paises
    private record FilaPais(String pais, long tenistas) {
    }

    // Lo que devuelve el Task de estadisticas: las tres consultas juntas
    private record DatosEstadisticas(List<String> paises, Map<String, Long> conteo, double edadMedia) {
    }

    // Opciones del desplegable "Ordenar por"
    private static final String ORDEN_RANKING = "Ranking";
    private static final String ORDEN_NOMBRE = "Nombre";
    private static final String ORDEN_EDAD = "Edad (mas joven primero)";
    private static final String ORDEN_PUNTOS = "Puntos (mas puntos primero)";

    // Texto que muestra la tabla cuando no hay filas
    private final Label lblVacio = new Label("Sin resultados");

    @FXML
    private Button btnExportar;

    // Exportador del backend (paquete storage)
    private final ExportadorCsv exportador = new ExportadorCsv();

    private TenistaService servicio;
    private ContextoApp contexto;
    // true mientras se recarga la lista de paises (ver cargarPaises)
    private boolean actualizandoPaises = false;

    @Override
    public void init(ContextoApp contexto, Navegador navegador) {
        this.contexto = contexto;
        this.servicio = contexto.getTenistaService();
        configurarTabla();
        configurarFiltros();
        configurarEstadisticas();
        mostrarResultado(servicio::listarTodos);
        cargarEstadisticas();
    }

    // ---------- Estadisticas ----------

    private void configurarEstadisticas() {
        colPaisEst.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().pais()));
        colNumTenistas.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().tenistas())));
        tablaPaises.setPlaceholder(new Label("Sin datos"));
    }

    /**
     * Pide al servicio la edad media, la lista de paises y el conteo por pais.
     * Se llama al abrir la pestana y despues de crear, modificar o borrar.
     */
    private void cargarEstadisticas() {
        Task<DatosEstadisticas> tarea = new Task<>() {
            @Override
            protected DatosEstadisticas call() {
                return new DatosEstadisticas(servicio.listarPaises(), servicio.contarPorPais(), servicio.edadMedia());
            }
        };
        tarea.setOnSucceeded(e -> {
            DatosEstadisticas datos = tarea.getValue();
            lblEdadMedia.setText(String.format("Edad media: %.1f años", datos.edadMedia()));
            lblNumPaises.setText("Paises: " + datos.paises().size());

            // Una fila por pais, en el orden de listarPaises(), con su numero de contarPorPais()
            List<FilaPais> filas = new ArrayList<>();
            for (String pais : datos.paises()) {
                filas.add(new FilaPais(pais, datos.conteo().getOrDefault(pais, 0L)));
            }
            tablaPaises.setItems(FXCollections.observableArrayList(filas));
        });
        tarea.setOnFailed(e -> Alertas.error("No se pueden cargar las estadisticas",
                Alertas.mensajeDe(tarea.getException())));
        Tareas.ejecutar(tarea);
    }

    private void configurarFiltros() {
        cmbMano.getItems().addAll(Tenista.DIESTRO, Tenista.ZURDO);
        spTop.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 10));
        cmbOrden.getItems().addAll(ORDEN_RANKING, ORDEN_NOMBRE, ORDEN_EDAD, ORDEN_PUNTOS);

        // La lista de paises sale de la BD; se vuelve a pedir cada vez que se abre
        // el desplegable por si se ha creado o borrado algun tenista
        cargarPaises();
        cmbPais.setOnShowing(e -> cargarPaises());
    }

    private void cargarPaises() {
        Task<List<String>> tarea = new Task<>() {
            @Override
            protected List<String> call() {
                return servicio.listarPaises();
            }
        };
        tarea.setOnSucceeded(e -> {
            // Cambiar la lista puede cambiar el valor elegido y lanzar onFiltrarPais;
            // con este aviso onFiltrarPais sabe que no lo ha elegido el usuario
            actualizandoPaises = true;
            String elegido = cmbPais.getValue();
            cmbPais.getItems().setAll(tarea.getValue());
            cmbPais.setValue(elegido);
            actualizandoPaises = false;
        });
        tarea.setOnFailed(e -> Alertas.error("No se pueden cargar los paises",
                Alertas.mensajeDe(tarea.getException())));
        Tareas.ejecutar(tarea);
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

        // Por defecto TableView ordena al pulsar en la cabecera de una columna. Se desactiva
        // porque las ordenaciones las hace TenistaService (y aqui las columnas son texto,
        // asi que ordenaria "10" antes que "2")
        for (TableColumn<Tenista, ?> columna : tabla.getColumns()) {
            columna.setSortable(false);
        }

        // Modificar y Borrar solo se pueden pulsar con una fila seleccionada.
        // Con bind, JavaFX los activa/desactiva solo cuando cambia la seleccion
        btnModificar.disableProperty().bind(tabla.getSelectionModel().selectedItemProperty().isNull());
        btnBorrar.disableProperty().bind(tabla.getSelectionModel().selectedItemProperty().isNull());
    }

    private void mostrarResultado(Supplier<List<Tenista>> consulta) {
        mostrarResultado(consulta, "Sin resultados", () -> { });
    }

    private void mostrarResultado(Supplier<List<Tenista>> consulta, String textoSiVacio) {
        mostrarResultado(consulta, textoSiVacio, () -> { });
    }

    /**
     * Ejecuta una consulta de TenistaService en otro hilo y pone el resultado en la tabla.
     * Todos los botones de la pestana lo usan, por ejemplo:
     * mostrarResultado(() -> servicio.filtrarPorPais("ESP"))
     *
     * @param textoSiVacio lo que se ve en la tabla si no hay resultados
     * @param alTerminar   algo que hacer despues de mostrar el resultado (si va bien)
     */
    private void mostrarResultado(Supplier<List<Tenista>> consulta, String textoSiVacio, Runnable alTerminar) {
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
            // No tiene sentido exportar una tabla vacia
            btnExportar.setDisable(resultado.isEmpty());
            setControlesActivos(true);
            alTerminar.run();
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
        }, "Sin resultados", this::cargarEstadisticas); // los datos han cambiado: se recalculan
    }

    private void setControlesActivos(boolean activos) {
        barra.setDisable(!activos);
        panel.setDisable(!activos);
    }

    @FXML
    private void onVerTodos() {
        limpiarFiltros();
        mostrarResultado(servicio::listarTodos);
    }

    // ---------- Exportar ----------

    /**
     * Guarda en un CSV las filas que se ven en la tabla (resultado de la ultima consulta)
     * usando ExportadorCsv del backend.
     */
    @FXML
    private void onExportarCsv() {
        // Copia de las filas: la tabla podria cambiar mientras se escribe el archivo
        List<Tenista> filas = new ArrayList<>(tabla.getItems());

        // Dialogo "Guardar como" del sistema operativo
        FileChooser selector = new FileChooser();
        selector.setTitle("Exportar tenistas a CSV");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        selector.setInitialFileName("tenistas.csv");
        File fichero = selector.showSaveDialog(tabla.getScene().getWindow());
        if (fichero == null) {
            return; // el usuario ha cancelado
        }

        // Escribir un archivo puede tardar, asi que va en otro hilo
        Task<String> tarea = new Task<>() {
            @Override
            protected String call() throws IOException {
                return exportador.exportar(filas, fichero.getAbsolutePath());
            }
        };
        tarea.setOnSucceeded(e -> Alertas.info("Exportacion completada",
                "Se han exportado " + filas.size() + " tenistas a\n" + tarea.getValue()));
        // Aqui el error es de escritura del archivo (sin permisos, archivo abierto en Excel...),
        // por eso se muestra su mensaje directamente
        tarea.setOnFailed(e -> Alertas.error("No se ha podido exportar",
                "No se ha podido escribir el archivo:\n" + tarea.getException().getMessage()));
        Tareas.ejecutar(tarea);
    }

    // ---------- Busquedas y filtros ----------

    @FXML
    private void onBuscarNombre() {
        String texto = txtNombre.getText().trim();
        if (texto.isEmpty()) {
            Alertas.info("Buscar por nombre", "Escribe parte del nombre del tenista.");
            return;
        }
        limpiarFiltros();
        mostrarResultado(() -> servicio.buscarPorNombre(texto),
                "Ningun tenista tiene \"" + texto + "\" en el nombre");
    }

    @FXML
    private void onFiltrarPais() {
        String pais = cmbPais.getValue();
        // Al vaciar el ComboBox (limpiarFiltros) o recargar los paises tambien salta este evento: se ignora
        if (pais == null || actualizandoPaises) {
            return;
        }
        cmbMano.setValue(null);
        cmbOrden.setValue(null);
        mostrarResultado(() -> servicio.filtrarPorPais(pais), "Ningun tenista de " + pais);
    }

    @FXML
    private void onFiltrarMano() {
        String mano = cmbMano.getValue();
        if (mano == null) {
            return;
        }
        cmbPais.setValue(null);
        cmbOrden.setValue(null);
        mostrarResultado(() -> servicio.filtrarPorMano(mano), "Ningun tenista " + mano.toLowerCase());
    }

    @FXML
    private void onTop() {
        int n = Campos.leerNumero(spTop);
        if (n < 1) {
            Alertas.info("Top", "El numero tiene que ser mayor que 0.");
            return;
        }
        limpiarFiltros();
        mostrarResultado(() -> servicio.top(n));
    }

    // ---------- Ordenaciones ----------

    @FXML
    private void onOrdenar() {
        String orden = cmbOrden.getValue();
        // Al vaciar el ComboBox desde el codigo tambien salta este evento: se ignora
        if (orden == null) {
            return;
        }
        cmbPais.setValue(null);
        cmbMano.setValue(null);

        // Cada opcion llama a su metodo del servicio (las ordenaciones las hace el backend)
        Supplier<List<Tenista>> consulta = switch (orden) {
            case ORDEN_NOMBRE -> servicio::ordenarPorNombre;
            case ORDEN_EDAD -> servicio::ordenarPorEdad;
            case ORDEN_PUNTOS -> servicio::ordenarPorPuntos;
            default -> servicio::ordenarPorRanking;
        };
        mostrarResultado(consulta);
    }

    // Los filtros y ordenaciones no se combinan: al usar uno se vacian los demas
    // para que se vea que la tabla solo muestra el resultado de la ultima consulta
    private void limpiarFiltros() {
        cmbPais.setValue(null);
        cmbMano.setValue(null);
        cmbOrden.setValue(null);
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
