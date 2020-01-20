package model.DAO;

import db.DB;
import model.DAO.impl.DepartamentoDaoJDBC;
import model.DAO.impl.VendedorDaoJDBC;

public class DAOFactory {

	public static VendedorDAO criaVendedorDAO() {
		return new VendedorDaoJDBC(DB.getConnection());
	}
	
	public static DepartamentoDAO criaDepartamentoDAO() {
		return new DepartamentoDaoJDBC(DB.getConnection());
	}
	
}
