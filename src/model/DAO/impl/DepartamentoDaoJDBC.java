package model.DAO.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.DAO.DepartamentoDAO;
import model.entities.Departamento;

public class DepartamentoDaoJDBC implements DepartamentoDAO {

	private Connection conn;

	public DepartamentoDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Departamento obj) {

		PreparedStatement st = null;
		try {

			st = conn.prepareStatement("INSERT INTO departamento(Nome) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getNome());

			int linhasAfetadas = st.executeUpdate();

			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Erro inesperado! Nenhuma linha afetada.");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void update(Departamento obj) {
		PreparedStatement st = null;
		try {

			st = conn.prepareStatement("UPDATE departamento SET Nome = ? WHERE Id = ?");

			st.setString(1, obj.getNome());
			st.setInt(2, obj.getId());

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {

		PreparedStatement st = null;
		try {

			st = conn.prepareStatement("DELETE FROM departamento WHERE Id = ?");

			st.setInt(1, id);

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Departamento findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM departamento WHERE Id = ?";
			st = conn.prepareStatement(sql);

			st.setInt(1, id);
			rs = st.executeQuery();

			if (rs.next()) {
				Departamento dep = instanciarDepartamento(rs);

				return dep;
			}
			return null;

		} catch (SQLException e) {

			throw new DbException(e.getMessage());

		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}

	}

	@Override
	public List<Departamento> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM departamento ORDER BY Nome");

			rs = st.executeQuery();

			List<Departamento> departamentos = new ArrayList<>();

			while (rs.next()) {
				Departamento dep = instanciarDepartamento(rs);
				departamentos.add(dep);
			}
			return departamentos;

		} catch (SQLException e) {

			throw new DbException(e.getMessage());

		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	private Departamento instanciarDepartamento(ResultSet rs) throws SQLException {

		Departamento dep = new Departamento();
		dep.setId(rs.getInt("Id"));
		dep.setNome(rs.getString("Nome"));
		return dep;

	}

}
