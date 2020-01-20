package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Departamento;
import model.services.DepartamentoService;

public class ListagemDepartamentoController implements Initializable, DataChangeListener {

	private DepartamentoService service;

	@FXML
	private TableView<Departamento> tableViewDepartamentos;

	@FXML
	private TableColumn<Departamento, Integer> tableColumnId;

	@FXML
	private TableColumn<Departamento, String> tableColumnNome;

	@FXML
	private TableColumn<Departamento, Departamento> tableColumnEDIT;

	@FXML
	private TableColumn<Departamento, Departamento> tableColumnREMOVE;

	@FXML
	private Button btNovo;

	private ObservableList<Departamento> obsList;

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Departamento dep = new Departamento();
		createDialogForm(dep, "/gui/DepartamentoForm.fxml", Utils.currentStage(event));
	}

	public void setDepartamentoService(DepartamentoService departamentoService) {
		this.service = departamentoService;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartamentos.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service está nulo");
		}

		List<Departamento> lista = service.findAll();
		obsList = FXCollections.observableArrayList(lista);
		tableViewDepartamentos.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Departamento dep, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartamentoFormController controller = loader.getController();
			controller.setDepartamento(dep);
			controller.setDepartamentoService(new DepartamentoService());
			controller.sobrescreverDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Insira informações do Departamento");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro carregando a View", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartamentoForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	protected void removeEntity(Departamento obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Tem certeza de que quer deletar?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service nulo");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro excluindo departamento", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
