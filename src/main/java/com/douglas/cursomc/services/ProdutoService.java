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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.douglas.cursomc.domain.Categoria;
import com.douglas.cursomc.domain.Produto;
import com.douglas.cursomc.repositories.CategoriaRepository;
import com.douglas.cursomc.repositories.ProdutoRepository;
import com.douglas.cursomc.services.exceptions.FileException;
import com.douglas.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ProdutoService {

	public static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	public Produto find(Integer id) {
		Optional<Produto> obj = produtoRepository.findById(id);
		
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado!" + id + ", Tipo: " + Produto.class.getName()
		));
	}
	
	public Page<Produto> search(String nome, List<Integer> ids, Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		List<Categoria> categorias = categoriaRepository.findAllById(ids);
		
		return produtoRepository.search(nome, categorias, pageRequest);
	}
	
	public void uploadCategoriaPicture(MultipartFile file, Integer id) {
		try {
			String extention = FilenameUtils.getExtension(file.getOriginalFilename());
			String pictureName = "prod" + id + "." + extention;
		
			new File(UPLOAD_DIRECTORY).mkdir();
			Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, pictureName);
			Files.write(fileNameAndPath, file.getBytes());
		} catch (Exception e) {
			throw new FileException("Erro no upload da imagem");
		}
	}
	
	public void showProfilePicture(String pictureUrl, HttpServletResponse response) {
		try {
			File picture = new File(UPLOAD_DIRECTORY + "/" + pictureUrl);
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
