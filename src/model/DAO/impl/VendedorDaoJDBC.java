package model.DAO.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.DAO.VendedorDAO;
import model.entities.Departamento;
import model.entities.Vendedor;

public class VendedorDaoJDBC implements VendedorDAO {

	private Connection conn;

	public VendedorDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Vendedor obj) {

		PreparedStatement st = null;
		try {

			st = conn.prepareStatement(
					"INSERT INTO vendedor(Nome, Email, DataNascimento, SalarioBase, DepartamentoId) VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getDataNascimento().getTime()));
			st.setDouble(4, obj.getSalarioBase());
			st.setInt(5, obj.getDepartamento().getId());

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
	public void update(Vendedor obj) {
		PreparedStatement st = null;
		try {

			st = conn.prepareStatement(
					"UPDATE vendedor SET Nome = ?, Email = ?, DataNascimento = ?, SalarioBase = ?, DepartamentoId = ? WHERE Id = ?");

			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getDataNascimento().getTime()));
			st.setDouble(4, obj.getSalarioBase());
			st.setInt(5, obj.getDepartamento().getId());
			st.setInt(6, obj.getId());

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

			st = conn.prepareStatement(
					"DELETE FROM vendedor WHERE Id = ?");

			st.setInt(1, id);

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Vendedor findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT vendedor.*, departamento.Nome as DepName " + "FROM vendedor INNER JOIN departamento "
					+ "ON vendedor.DepartamentoId = departamento.Id " + "WHERE vendedor.Id = ?";
			st = conn.prepareStatement(sql);

			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Departamento dep = instanciarDepartamento(rs);

				Vendedor vend = instanciarVendedor(rs, dep);

				return vend;
			}
			return null;

		} catch (SQLException e) {

			throw new DbException(e.getMessage());

		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}

	}

	private Vendedor instanciarVendedor(ResultSet rs, Departamento dep) throws SQLException {

		Vendedor vend = new Vendedor();
		vend.setId(rs.getInt("Id"));
		vend.setNome(rs.getString("Nome"));
		vend.setEmail(rs.getString("Email"));
		vend.setSalarioBase(rs.getDouble("SalarioBase"));
		vend.setDataNascimento(new java.util.Date(rs.getTimestamp("DataNascimento").getTime()));
		vend.setDepartamento(dep);
		return vend;

	}

	private Departamento instanciarDepartamento(ResultSet rs) throws SQLException {

		Departamento dep = new Departamento();
		dep.setId(rs.getInt("DepartamentoId"));
		dep.setNome(rs.getString("DepName"));
		return dep;

	}

	@Override
	public List<Vendedor> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT vendedor.*,departamento.Nome as DepName "
					+ "FROM vendedor INNER JOIN departamento " + "ON vendedor.DepartamentoId = departamento.Id ORDER BY Nome");

			rs = st.executeQuery();

			List<Vendedor> vendedores = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();

			while (rs.next()) {

				Departamento departamento = map.get(rs.getInt("DepartamentoId"));

				if (departamento == null) {
					departamento = instanciarDepartamento(rs);
					map.put(rs.getInt("DepartamentoId"), departamento);
				}

				Vendedor vend = instanciarVendedor(rs, departamento);
				vendedores.add(vend);
			}
			return vendedores;

		} catch (SQLException e) {

			throw new DbException(e.getMessage());

		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Vendedor> findByDepartamento(Departamento dep) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT vendedor.*,departamento.Nome as DepName " + "FROM vendedor INNER JOIN departamento "
							+ "ON vendedor.DepartamentoId = departamento.Id " + "WHERE DepartamentoId = ? " + "ORDER BY Nome");

			st.setInt(1, dep.getId());

			rs = st.executeQuery();

			List<Vendedor> vendedores = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();

			while (rs.next()) {

				Departamento departamento = map.get(rs.getInt("DepartamentoId"));

				if (departamento == null) {
					departamento = instanciarDepartamento(rs);
					map.put(rs.getInt("DepartamentoId"), departamento);
				}

				Vendedor vend = instanciarVendedor(rs, departamento);
				vendedores.add(vend);
			}
			return vendedores;

		} catch (SQLException e) {

			throw new DbException(e.getMessage());

		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

}
