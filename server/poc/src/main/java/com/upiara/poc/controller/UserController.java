package com.upiara.poc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upiara.poc.exception.ResourceNotFoundException;
import com.upiara.poc.model.User;
import com.upiara.poc.repository.UserRepository;

@RestController
@RequestMapping("/api/v1")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private User getUser(Long userId) throws ResourceNotFoundException {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não existe para o id: "+userId));
	}
	
	@GetMapping("/users")
	public List <User> getAllUsers() {
		return userRepository.findAll();
	}
	
	@GetMapping("/users/{id}")
	public ResponseEntity <User> getUserById(@PathVariable(value = "id") Long userId)
	throws ResourceNotFoundException {
		User user = this.getUser(userId);
		return ResponseEntity.ok().body(user);
	}
	
	@PostMapping("/users")
	public User createUser(@RequestBody User user) {
		userRepository.save(user);
		return userRepository.findTopByOrderByIdDesc();
	}
	
	@PutMapping("/users/{id}")
	public User updateUser(@PathVariable(value="id") Long userId,
			@RequestBody User userDetail) throws ResourceNotFoundException {
		User user = this.getUser(userId);
		user.setName(userDetail.getName());
		user.setBirthday(userDetail.getBirthday());
		user.setLanguage_code(userDetail.getLanguage_code());
		userRepository.save(user);
		entityManager.detach(user); //não pegar do cache, campos calculados em view
		return this.getUser(userId);
	}
	
	@DeleteMapping("/users/{id}")
	public Map <String, Boolean> deleteUser(@PathVariable(value="id") Long userId)
		throws ResourceNotFoundException {
		User user = this.getUser(userId);
		userRepository.delete(user);
		Map<String,Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}
}
