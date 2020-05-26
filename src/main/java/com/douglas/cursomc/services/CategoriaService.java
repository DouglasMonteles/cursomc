package com.douglas.cursomc.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.douglas.cursomc.domain.Categoria;
import com.douglas.cursomc.dto.CategoriaDTO;
import com.douglas.cursomc.repositories.CategoriaRepository;
import com.douglas.cursomc.services.exceptions.DataIntegrityException;
import com.douglas.cursomc.services.exceptions.FileException;
import com.douglas.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {
	
	public static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

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
		Categoria newObj = find(obj.getId());
		updateData(newObj, obj);
		
		return repository.save(newObj);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir uma categoria que possui produtos");
		}
	}
	
	public List<Categoria> findAll() {
		return repository.findAll();
	}
	
	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		return repository.findAll(pageRequest);
	}
	
	public Categoria fromDTO(CategoriaDTO objDto) {
		return new Categoria(objDto.getId(), objDto.getNome());
	}
	
	public void updateData(Categoria newObj, Categoria obj) {
		newObj.setNome(obj.getNome());
	}
	
	public void uploadCategoriaPicture(MultipartFile file, Integer id) {
		try {
			String extention = FilenameUtils.getExtension(file.getOriginalFilename());
			String pictureName = "cat" + id + "." + extention;
		
			new File(UPLOAD_DIRECTORY).mkdir();
			Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, pictureName);
			Files.write(fileNameAndPath, file.getBytes());
		} catch (Exception e) {
			throw new FileException("Erro no upload da imagem");
		}
	}
	
	public void showProfilePicture(String pictureUrl, HttpServletResponse response) {
		try {
			File picture = new File(UPLOAD_DIRECTORY + "/"+ pictureUrl);
			try (InputStream stream  = new FileInputStream(picture)) {
				response.setContentType("application/force-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + pictureUrl);
				IOUtils.copy(stream, response.getOutputStream());
				response.flushBuffer();
			}
		} catch (Exception e) {
			throw new FileException("Erro ao exibir imagem");
		}
	}
}
