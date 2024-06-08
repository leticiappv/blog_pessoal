package com.generation.blogpessoal.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional; 

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@BeforeAll
	void star() {
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "root", "root@root.com", "rootroot", ""));
	}
	
	@Test
	@DisplayName("Deve cadastrar um novo usuário")
	public void DeveCriarUmNovoUsuario() {
		HttpEntity<Usuario> corpoRequisicao= new HttpEntity<Usuario>(
				new Usuario(0L, "Thiago", "thiago@email.com", "12345678", "")
				);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange(
				"/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class
				);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Não deve permitir a duplicação do Usuário")
	public void naoDevePublicarUsuario() {
		
		usuarioService.cadastrarUsuario(
				new Usuario(0L, "Maria da Silva", "maria@email.com", "123456789", ""));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario (0L, "Maria da Silva", "maria@email.com", "123456789", ""));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(
				new Usuario(0L, "Ana Clara", "ana@email.com", "123456789", "" ));
		
		Usuario usuarioUptade = new Usuario (usuarioCadastrado.get().getId(),
				"Ana Clara Silva", "anasilva@email.com", "123456789", "");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUptade);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(
				new Usuario(0L, "Bianca Pires", "bianca@email.com", "123456789", "" ));
		
		usuarioService.cadastrarUsuario(
				new Usuario(0L, "Bruno Buzo", "bruno@email.com", "123456789", "" ));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		
	}
}
