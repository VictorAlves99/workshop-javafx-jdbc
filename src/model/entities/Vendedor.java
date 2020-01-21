package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Vendedor implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String nome;
	private String email;
	private Date dataNascimento;
	private double salarioBase;
	
	private Departamento departamento;
	
	public Vendedor() {
		
	}	

	public Vendedor(Integer id, String nome, String email, Date dataNascimento, double salarioBase, Departamento departamento) {
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.dataNascimento = dataNascimento;
		this.salarioBase = salarioBase;
		this.departamento = departamento;
	}

	public Vendedor(String nome, String email, Date dataNascimento, double salarioBase, Departamento departamento) {
		this.nome = nome;
		this.email = email;
		this.dataNascimento = dataNascimento;
		this.salarioBase = salarioBase;
		this.departamento = departamento;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public double getSalarioBase() {
		return salarioBase;
	}

	public void setSalarioBase(double salarioBase) {
		this.salarioBase = salarioBase;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vendedor other = (Vendedor) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Vendedor [id=" + id + ", nome=" + nome + ", email=" + email + ", dataNascimento=" + dataNascimento
				+ ", salarioBase=" + salarioBase + ", departamento=" + departamento + "]";
	}
	
	
}
