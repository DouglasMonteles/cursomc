package com.douglas.cursomc.resources;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.douglas.cursomc.domain.Produto;
import com.douglas.cursomc.dto.ProdutoDTO;
import com.douglas.cursomc.resources.utils.URI;
import com.douglas.cursomc.services.ProdutoService;

@RestController
@RequestMapping(value = "/produtos")
public class ProdutoResource {

	@Autowired
	private ProdutoService service;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Produto> find(@PathVariable Integer id) {
		Produto obj = service.find(id);
		
		return ResponseEntity.ok().body(obj);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Page<ProdutoDTO>> findPage(
			@RequestParam(value = "nome", defaultValue = "") String nome,
			@RequestParam(value = "categorias", defaultValue = "") String categorias,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "listPerPage", defaultValue = "24") Integer linesPerPage,
			@RequestParam(value = "orderBy", defaultValue = "nome") String orderBy,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction
		) {
		String nomeDecoded = URI.decodeParam(nome);
		List<Integer> ids = URI.decodeIntList(categorias);
		Page<Produto> list = service.search(nomeDecoded, ids, page, linesPerPage, orderBy, direction);
		Page<ProdutoDTO> listDto = list.map((obj) -> new ProdutoDTO(obj));
		
		return ResponseEntity.ok().body(listDto);
	}
	
	@RequestMapping(value = "/picture/{id}", method = RequestMethod.POST)
	public ResponseEntity<Void> uploadProfilePicture(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
		service.uploadCategoriaPicture(file, id);
		return ResponseEntity.ok().build();
	}
	
	@RequestMapping(value = "/picture/show/{fileUrl}", method = RequestMethod.GET)
	public ResponseEntity<Void> showProfilePicture(@PathVariable("fileUrl") String fileUrl, HttpServletResponse response) {
		service.showProfilePicture(fileUrl, response);
		return ResponseEntity.ok().build();
	}
	
}
