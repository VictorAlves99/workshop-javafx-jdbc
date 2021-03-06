package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Departamento;
import model.entities.Vendedor;
import model.exceptions.ValidationException;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class VendedorFormController implements Initializable {

	private Vendedor entity;

	private VendedorService service;

	private DepartamentoService depService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpDataNascimento;

	@FXML
	private TextField txtSalarioBase;

	@FXML
	private ComboBox<Departamento> comboBoxDepartamento;

	@FXML
	private Label labelErroNome;

	@FXML
	private Label labelErroEmail;

	@FXML
	private Label labelErroDataNascimento;

	@FXML
	private Label labelErroSalarioBase;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	private ObservableList<Departamento> obsList;

	public void setVendedor(Vendedor entity) {
		this.entity = entity;
	}

	public void setServices(VendedorService service, DepartamentoService depService) {
		this.service = service;
		this.depService = depService;
	}

	public void sobrescreverDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity nulo");
		}

		if (service == null) {
			throw new IllegalStateException("Service nulo");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListener();
			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar vendedor", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setMensagensDeErro(e.getErros());
		}
	}

	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldDouble(txtSalarioBase);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpDataNascimento, "dd/MM/yyyy");
		initializeComboBoxDepartamento();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity est� nulo");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtSalarioBase.setText(String.format("%.2f", entity.getSalarioBase()));
		if (entity.getDataNascimento() != null) {
			dpDataNascimento.setValue(LocalDateTime
					.ofInstant(entity.getDataNascimento().toInstant(), ZoneId.systemDefault()).toLocalDate());
		}

		if (entity.getDepartamento() == null) {
			comboBoxDepartamento.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartamento.setValue(entity.getDepartamento());
		}
	}

	public void carregarDadosAssociados() {
		if (depService == null) {
			throw new IllegalStateException("DepartamentoService nulo");
		}
		List<Departamento> lista = depService.findAll();
		obsList = FXCollections.observableArrayList(lista);
		comboBoxDepartamento.setItems(obsList);
	}

	private Vendedor getFormData() {
		Vendedor vend = new Vendedor();

		ValidationException exception = new ValidationException("Erro de valida��o");

		vend.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addErro("nome", "Campo n�o pode ser vazio");
		}
		vend.setNome(txtNome.getText());

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addErro("email", "Campo n�o pode ser vazio");
		}
		vend.setEmail(txtEmail.getText());

		if (dpDataNascimento.getValue() == null) {
			exception.addErro("dataNascimento", "Campo n�o pode ser vazio");
		} else {
			Instant instant = Instant.from(dpDataNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
			vend.setDataNascimento(Date.from(instant));
		}
		if (txtSalarioBase.getText() == null || txtSalarioBase.getText().trim().equals("")) {
			exception.addErro("salarioBase", "Campo n�o pode ser vazio");
		}
		vend.setSalarioBase(Utils.tryParseToDouble(txtSalarioBase.getText()));

		vend.setDepartamento(comboBoxDepartamento.getValue());
		
		if (exception.getErros().size() > 0) {
			throw exception;
		}

		return vend;
	}

	private void setMensagensDeErro(Map<String, String> erros) {
		Set<String> fields = erros.keySet();

		labelErroNome.setText(fields.contains("nome") ? erros.get("nome") : "");
		labelErroEmail.setText(fields.contains("email") ? erros.get("email") : "");
		labelErroSalarioBase.setText(fields.contains("salarioBase") ? erros.get("salarioBase") : "");
		labelErroDataNascimento.setText(fields.contains("dataNascimento") ? erros.get("dataNascimento") : "");

	}

	private void initializeComboBoxDepartamento() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
	}

}
