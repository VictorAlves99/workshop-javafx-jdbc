package model.services;

import java.util.List;

import model.DAO.DAOFactory;
import model.DAO.VendedorDAO;
import model.entities.Vendedor;

public class VendedorService {

	private VendedorDAO dao = DAOFactory.criaVendedorDAO();
	
	public List<Vendedor> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Vendedor dep) {
		if(dep.getId() == null) {
			dao.insert(dep);
		}else {
			dao.update(dep);
		}
	}
	
	public void remove(Vendedor dep) {
		dao.deleteById(dep.getId());
	}
}
