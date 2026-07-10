package com.shopsphere.service;

import com.shopsphere.entity.Customer;
import com.shopsphere.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer getById(Long id) {
        return findOrThrow(id);
    }

    @Override
    public Page<Customer> getAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public Customer update(Long id, Customer customer) {
        Customer existing = findOrThrow(id);
        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        existing.setPhone(customer.getPhone());
        existing.setAddress(customer.getAddress());
        return customerRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Customer existing = findOrThrow(id);
        customerRepository.delete(existing);
    }

    private Customer findOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id " + id));
    }
}
