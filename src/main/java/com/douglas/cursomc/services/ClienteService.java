package com.douglas.cursomc.services;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.douglas.cursomc.domain.Cidade;
import com.douglas.cursomc.domain.Cliente;
import com.douglas.cursomc.domain.Endereco;
import com.douglas.cursomc.domain.enums.Perfil;
import com.douglas.cursomc.domain.enums.TipoCliente;
import com.douglas.cursomc.dto.ClienteDTO;
import com.douglas.cursomc.dto.ClienteNewDTO;
import com.douglas.cursomc.repositories.ClienteRepository;
import com.douglas.cursomc.repositories.EnderecoRepository;
import com.douglas.cursomc.security.UserSS;
import com.douglas.cursomc.services.exceptions.AuthorizationException;
import com.douglas.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	public static final String UPLOAD_DIRECTORY = System.getProperty("user.dir");

	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Value("${img.prefix.client.profile}")
	private String prefix;
	
	public Cliente find(Integer id) {
		UserSS user = UserService.authenticated();
		if (user==null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo" + Cliente.class.getName()
			)
		);
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repository.save(obj);
		
		enderecoRepository.saveAll(obj.getEnderecos()); 
		
		return obj;
	}
	
	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		
		return repository.save(newObj);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException("Não é possível excluir, pois há pedidos relacionados");
		}
	}
	
	public List<Cliente> findAll() {
		return repository.findAll();
	}
	
	public Cliente findByEmail(String email) {
		UserSS user = UserService.authenticated();
		if (user == null || !user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Cliente obj = repository.findByEmail(email);
		if (obj == null) {
			throw new ObjectNotFoundException("Objeto não encontrado! Id: " + user.getId()
											+ ", Tipo: " + Cliente.class.getName());
		}
		
		return obj;
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		return repository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null, null);
	}
	
	public Cliente fromDTO(ClienteNewDTO objNewDto) {
		Cliente cliente = new Cliente(null, objNewDto.getNome(), objNewDto.getEmail(), objNewDto.getCpfOuCnpj(), TipoCliente.toEnum(objNewDto.getTipo()), encoder.encode(objNewDto.getSenha()));
		Cidade cidade = new Cidade(objNewDto.getCidadeId(), null, null);
		Endereco endereco = new Endereco(null, objNewDto.getLogradouro(), objNewDto.getNumero(), objNewDto.getComplemento(), objNewDto.getBairro(), objNewDto.getCep(), cliente, cidade);
	
		cliente.getEnderecos().add(endereco);
		cliente.getTelefones().add(objNewDto.getTelefone1());
		
		if (objNewDto.getTelefone2() != null) {
			cliente.getTelefones().add(objNewDto.getTelefone2());
		}
		
		if (objNewDto.getTelefone3() != null) {
			cliente.getTelefones().add(objNewDto.getTelefone3());
		}
		
		return cliente;
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
	
	public void uploadProfilePicture(MultipartFile file) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		
		try {
			String extention = FilenameUtils.getExtension(file.getOriginalFilename());
			String pictureName = "/uploads/" + prefix + user.getId().toString() + "." + extention;
		
			Optional<Cliente> cli = repository.findById(user.getId());
			cli.get().setImageUrl(pictureName);
			repository.save(cli.get());
		
			new File(UPLOAD_DIRECTORY).mkdir();
			Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, pictureName);
			Files.write(fileNameAndPath, file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
