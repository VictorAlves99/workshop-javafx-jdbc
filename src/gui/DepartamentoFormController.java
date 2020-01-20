package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Departamento;
import model.exceptions.ValidationException;
import model.services.DepartamentoService;

public class DepartamentoFormController implements Initializable {

	private Departamento entity;

	private DepartamentoService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>(); 

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private Label labelErroNome;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	public void setDepartamento(Departamento entity) {
		this.entity = entity;
	}

	public void setDepartamentoService(DepartamentoService service) {
		this.service = service;
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
			Alerts.showAlert("Erro ao salvar departamento", null, e.getMessage(), AlertType.ERROR);
		} catch(ValidationException e) {
			setMensagensDeErro(e.getErros());
		}
	}

	private void notifyDataChangeListener() {
		for(DataChangeListener listener : dataChangeListeners) {
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
		Constraints.setTextFieldMaxLength(txtNome, 30);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity está nulo");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
	}

	private Departamento getFormData() {
		Departamento dep = new Departamento();

		ValidationException exception = new ValidationException("Erro de validação");
		
		dep.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addErro("nome", "Campo não pode ser vazio");
		}
		
		dep.setNome(txtNome.getText());

		if(exception.getErros().size() > 0) {
			throw exception;
		}
		
		return dep;
	}
	
	private void setMensagensDeErro(Map<String,String> erros) {
		Set<String> fields = erros.keySet();
		
		if(fields.contains("nome")) {
			labelErroNome.setText(erros.get("nome"));
		}
	}
}
