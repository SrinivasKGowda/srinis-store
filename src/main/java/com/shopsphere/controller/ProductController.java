package com.shopsphere.controller;

import com.shopsphere.dto.ProductDto;
import com.shopsphere.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management")
@Validated
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List all products with pagination and sorting")
    public Page<ProductDto.Response> getAll(Pageable pageable) {
        return productService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by id")
    public ProductDto.Response getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name")
    public Page<ProductDto.Response> search(@RequestParam String name, Pageable pageable) {
        return productService.search(name, pageable);
    }

    @GetMapping("/top-selling")
    @Operation(summary = "Get top selling products")
    public List<ProductDto.Response> getTopSelling(@RequestParam(defaultValue = "5") int limit) {
        return productService.getTopSellingProducts(limit);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get products below a stock threshold")
    public List<ProductDto.Response> getLowStock(@RequestParam(defaultValue = "10") int threshold) {
        return productService.getLowStockProducts(threshold);
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductDto.Response> create(@Valid @RequestBody ProductDto.Request request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a product")
    public ProductDto.Response update(@PathVariable Long id, @Valid @RequestBody ProductDto.Request request) {
        return productService.update(id, request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a product")
    public ProductDto.Response patch(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return productService.patch(id, updates);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/image")
    @Operation(summary = "Upload a product image")
    public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        String fileName = productService.uploadImage(id, file);
        return ResponseEntity.ok(fileName);
    }

    @GetMapping("/{id}/image")
    @Operation(summary = "Download a product image")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long id) {
        Resource resource = productService.downloadImage(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
