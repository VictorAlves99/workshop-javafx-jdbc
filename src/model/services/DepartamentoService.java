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
	
	public void saveOrUpdate(Departamento dep) {
		if(dep.getId() == null) {
			dao.insert(dep);
		}else {
			dao.update(dep);
		}
	}
	
	public void remove(Departamento dep) {
		dao.deleteById(dep.getId());
	}
}
