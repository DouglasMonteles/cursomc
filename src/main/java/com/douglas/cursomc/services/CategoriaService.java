package com.douglas.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.douglas.cursomc.domain.Categoria;
import com.douglas.cursomc.repositories.CategoriaRepository;

import com.douglas.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repository;
	
	public Categoria find(Integer id) {
		Optional<Categoria> obj = repository.findById(id);
		
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));

	}
	
	public Categoria insert(Categoria obj) {
		obj.setId(null);
		
		return repository.save(obj);
	}
	
	public Categoria update(Categoria obj) {
		find(obj.getId());
		
		return repository.save(obj);
	}
	
}
