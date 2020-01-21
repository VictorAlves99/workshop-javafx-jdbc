package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import model.entities.Vendedor;
import model.services.VendedorService;

public class ListagemVendedorController implements Initializable, DataChangeListener {

	private VendedorService service;

	@FXML
	private TableView<Vendedor> tableViewVendedors;

	@FXML
	private TableColumn<Vendedor, Integer> tableColumnId;

	@FXML
	private TableColumn<Vendedor, String> tableColumnNome;

	@FXML
	private TableColumn<Vendedor, String> tableColumnEmail;

	@FXML
	private TableColumn<Vendedor, Date> tableColumnDataNascimento;

	@FXML
	private TableColumn<Vendedor, Double> tableColumnSalarioBase;

	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnEDIT;

	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnREMOVE;

	@FXML
	private Button btNovo;

	private ObservableList<Vendedor> obsList;

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Vendedor dep = new Vendedor();
		createDialogForm(dep, "/gui/VendedorForm.fxml", Utils.currentStage(event));
	}

	public void setVendedorService(VendedorService vendedorService) {
		this.service = vendedorService;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
		Utils.formatTableColumnDate(tableColumnDataNascimento, "dd/MM/yyyy");
		tableColumnSalarioBase.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
		Utils.formatTableColumnDouble(tableColumnSalarioBase, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewVendedors.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service está nulo");
		}

		List<Vendedor> lista = service.findAll();
		obsList = FXCollections.observableArrayList(lista);
		tableViewVendedors.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Vendedor vend, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			VendedorFormController controller = loader.getController();
			controller.setVendedor(vend);
			controller.setVendedorService(new VendedorService());
			controller.sobrescreverDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Insira informações do Vendedor");
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Vendedor vend, boolean empty) {
				super.updateItem(vend, empty);
				if (vend == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(vend, "/gui/VendedorForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Vendedor vend, boolean empty) {
				super.updateItem(vend, empty);
				if (vend == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(vend));
			}
		});
	}

	protected void removeEntity(Vendedor vend) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Tem certeza de que quer deletar?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service nulo");
			}
			try {
				service.remove(vend);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro excluindo vendedor", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
