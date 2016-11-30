package com.enginizer.service;

import com.enginizer.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xPku on 11/29/16.
 */
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repo;

    public void save(Customer customer) {
        repo.save(customer);
    }

    public List<Customer> findByNameStartsWithIgnoreCase(String name) {
        return repo.findByNameStartsWithIgnoreCase(name);
    }

    public List<Customer> findAll() {
        return repo.findAll();
    }

    public void delete(Customer customer) {
        repo.delete(customer);
    }

    public Customer findOne(Long id) {
        return repo.findOne(id);
    }
}
