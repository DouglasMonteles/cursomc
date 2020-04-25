package com.douglas.cursomc.domain.enums;

public enum Perfil {

	ADMIN(1, "ROLE_ADMIN"),
	CLIENTE(2, "ROLE_CLIENTE");
	
	private Integer cod;
	private String descricao;
	
	private Perfil(int cod, String descricao) {
		this.setCod(cod);
	}

	public Integer getCod() {
		return cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public static Perfil toEnum(Integer cod) {
		if (cod == null) 
			return null;
		
		for (Perfil p : Perfil.values()) {
			if (cod.equals(p.getCod())) {
				return p;
			}
		}
		
		throw new IllegalArgumentException("Id inválido: " + cod);
	}
	
}
