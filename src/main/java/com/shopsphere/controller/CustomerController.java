package com.shopsphere.controller;

import com.shopsphere.entity.Customer;
import com.shopsphere.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer account management")
@Validated
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "List all customers with pagination")
    public Page<Customer> getAll(Pageable pageable) {
        return customerService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer by id")
    public Customer getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<Customer> create(@Valid @RequestBody CustomerRequest request) {
        Customer customer = Customer.builder()
                .name(request.name)
                .email(request.email)
                .phone(request.phone)
                .address(request.address)
                .build();
        return ResponseEntity.ok(customerService.create(customer));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a customer")
    public Customer update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        Customer customer = Customer.builder()
                .name(request.name)
                .email(request.email)
                .phone(request.phone)
                .address(request.address)
                .build();
        return customerService.update(id, customer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public static class CustomerRequest {

        @NotBlank
        public String name;

        @NotBlank
        @Email
        public String email;

        public String phone;

        public String address;
    }
}
