package model.services;

import java.util.List;

import model.DAO.DAOFactory;
import model.DAO.DepartamentoDAO;
import model.entities.Departamento;

public class DepartamentoService {

	private DepartamentoDAO dao = DAOFactory.criaDepartamentoDAO();
	
	public List<Departamento> findAll(){
		return dao.findAll();
	}
	
}
