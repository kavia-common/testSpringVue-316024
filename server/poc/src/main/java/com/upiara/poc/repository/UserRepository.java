package com.upiara.poc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upiara.poc.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	User findTopByOrderByIdDesc();
}
