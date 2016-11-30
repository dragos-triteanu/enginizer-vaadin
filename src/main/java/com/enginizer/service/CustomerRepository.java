package com.enginizer.service;

import java.util.List;

import com.enginizer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	List<Customer> findByNameStartsWithIgnoreCase(String name);
}
