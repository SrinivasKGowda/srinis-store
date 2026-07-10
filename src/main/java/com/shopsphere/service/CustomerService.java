package com.shopsphere.service;

import com.shopsphere.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    Customer create(Customer customer);

    Customer getById(Long id);

    Page<Customer> getAll(Pageable pageable);

    Customer update(Long id, Customer customer);

    void delete(Long id);
}
