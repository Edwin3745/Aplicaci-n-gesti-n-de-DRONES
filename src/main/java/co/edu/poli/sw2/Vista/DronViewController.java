package co.edu.poli.sw2.Vista;

import co.edu.poli.sw2.Controlador.DronControlador;
import co.edu.poli.sw2.Modelo.Dron;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class DronViewController {

    @FXML private TextField txtId;
    @FXML private TextField txtSerial;
    @FXML private TextField txtModelo;
    @FXML private TextField txtFabricante;
    @FXML private TextField txtPeso;

    @FXML private TableView<Dron> tablaDrones;
    @FXML private TableColumn<Dron, Integer> colId;
    @FXML private TableColumn<Dron, String> colSerial;
    @FXML private TableColumn<Dron, String> colModelo;
    @FXML private TableColumn<Dron, String> colFabricante;
    @FXML private TableColumn<Dron, Float> colPeso;

    private final DronControlador dronControlador = new DronControlador();
    private final ObservableList<Dron> listaDrones = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colFabricante.setCellValueFactory(new PropertyValueFactory<>("fabricante"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));

        tablaDrones.setItems(listaDrones);

        // Al seleccionar una fila, cargar sus datos en el formulario
        tablaDrones.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                cargarEnFormulario(seleccionado);
            }
        });
    }

    @FXML
    public void agregarDron() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String serial = txtSerial.getText().trim();
            String modelo = txtModelo.getText().trim();
            String fabricante = txtFabricante.getText().trim();
            float peso = Float.parseFloat(txtPeso.getText().trim());

            if (serial.isEmpty() || modelo.isEmpty() || fabricante.isEmpty()) {
                mostrarAlerta("Campos vacíos", "Serial, modelo y fabricante son obligatorios.");
                return;
            }

            if (dronControlador.buscarDron(id) != null) {
                mostrarAlerta("ID duplicado", "Ya existe un dron con ese ID.");
                return;
            }

            // dronControlador.registrarDron(id, serial, modelo, fabricante, peso);
                        // TODO PASO 5: el formulario debe incluir un ComboBox de tipo y los campos
            // de capacidadTanque / deteccionTermica, y pasarlos aquí.
            // controlador.registrarDron(tipo, id, serial, modelo, fabricante, peso,
            //                           capacidadTanque, deteccionTermica);
            actualizarTabla();
            limpiarCampos();
        } catch (NumberFormatException e) {
            mostrarAlerta("Datos inválidos", "El ID debe ser entero y el peso un número decimal.");
        }
    }

    @FXML
    public void actualizarDronSeleccionado() {
        Dron seleccionado = tablaDrones.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Sin selección", "Selecciona un dron de la tabla para actualizar.");
            return;
        }
        try {
            seleccionado.setSerial(txtSerial.getText().trim());
            seleccionado.setModelo(txtModelo.getText().trim());
            seleccionado.setFabricante(txtFabricante.getText().trim());
            seleccionado.setPeso(Float.parseFloat(txtPeso.getText().trim()));

            dronControlador.actualizarDron(seleccionado);
            actualizarTabla();
            limpiarCampos();
        } catch (NumberFormatException e) {
            mostrarAlerta("Datos inválidos", "El peso debe ser un número decimal.");
        }
    }

    @FXML
    public void eliminarDron() {
        Dron seleccionado = tablaDrones.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            dronControlador.eliminarDron(seleccionado.getId());
            actualizarTabla();
            limpiarCampos();
        } else {
            mostrarAlerta("Sin selección", "Selecciona un dron de la tabla para eliminar.");
        }
    }

    private void cargarEnFormulario(Dron dron) {
        txtId.setText(String.valueOf(dron.getId()));
        txtSerial.setText(dron.getSerial());
        txtModelo.setText(dron.getModelo());
        txtFabricante.setText(dron.getFabricante());
        txtPeso.setText(String.valueOf(dron.getPeso()));
        txtId.setDisable(true); // el ID no se edita una vez creado
    }

    private void actualizarTabla() {
        listaDrones.setAll(dronControlador.listarDrones());
    }

    private void limpiarCampos() {
        txtId.clear();
        txtSerial.clear();
        txtModelo.clear();
        txtFabricante.clear();
        txtPeso.clear();
        txtId.setDisable(false);
        tablaDrones.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
