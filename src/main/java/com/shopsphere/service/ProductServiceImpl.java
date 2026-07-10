package com.shopsphere.service;

import com.shopsphere.dto.ProductDto;
import com.shopsphere.entity.Category;
import com.shopsphere.entity.Inventory;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.Tag;
import com.shopsphere.repository.CategoryRepository;
import com.shopsphere.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EntityManager entityManager;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public ProductDto.Response create(ProductDto.Request request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Category not found with id " + request.getCategoryId()));

        Inventory inventory = Inventory.builder()
                .quantity(request.getInitialQuantity())
                .build();

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .category(category)
                .inventory(inventory)
                .tags(resolveTags(request.getTagIds()))
                .build();

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Override
    public ProductDto.Response getById(Long id) {
        Product product = findProductOrThrow(id);
        return toResponse(product);
    }

    @Override
    public Page<ProductDto.Response> getAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public Page<ProductDto.Response> search(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable).map(this::toResponse);
    }

    @Override
    @CacheEvict(value = "topSellingProducts", allEntries = true)
    public ProductDto.Response update(Long id, ProductDto.Request request) {
        Product product = findProductOrThrow(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Category not found with id " + request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setCategory(category);
        product.setTags(resolveTags(request.getTagIds()));
        if (product.getInventory() != null) {
            product.getInventory().setQuantity(request.getInitialQuantity());
        }

        return toResponse(productRepository.save(product));
    }

    @Override
    @CacheEvict(value = "topSellingProducts", allEntries = true)
    public ProductDto.Response patch(Long id, Map<String, Object> updates) {
        Product product = findProductOrThrow(id);

        if (updates.containsKey("name")) {
            product.setName((String) updates.get("name"));
        }
        if (updates.containsKey("description")) {
            product.setDescription((String) updates.get("description"));
        }
        if (updates.containsKey("price")) {
            product.setPrice(new java.math.BigDecimal(updates.get("price").toString()));
        }
        if (updates.containsKey("quantity") && product.getInventory() != null) {
            product.getInventory().setQuantity(Integer.parseInt(updates.get("quantity").toString()));
        }

        return toResponse(productRepository.save(product));
    }

    @Override
    @CacheEvict(value = "topSellingProducts", allEntries = true)
    public void delete(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
    }

    @Override
    @Cacheable(value = "topSellingProducts", key = "#limit")
    public List<ProductDto.Response> getTopSellingProducts(int limit) {
        return productRepository.findTopSellingProducts(PageRequest.of(0, limit))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto.Response> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String uploadImage(Long id, MultipartFile file) {
        Product product = findProductOrThrow(id);
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String extension = "";
            String originalName = file.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf('.'));
            }
            String fileName = "product-" + id + extension;
            Path targetPath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            product.setImagePath(fileName);
            productRepository.save(product);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file for product " + id, e);
        }
    }

    @Override
    public Resource downloadImage(Long id) {
        Product product = findProductOrThrow(id);
        if (product.getImagePath() == null) {
            throw new NoSuchElementException("No image found for product " + id);
        }
        try {
            Path filePath = Paths.get(uploadDir).resolve(product.getImagePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new NoSuchElementException("Image file not readable for product " + id);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to load image for product " + id, e);
        }
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id " + id));
    }

    private Set<Tag> resolveTags(Set<Long> tagIds) {
        if (tagIds == null) {
            return Set.of();
        }
        return tagIds.stream()
                .map(tagId -> entityManager.getReference(Tag.class, tagId))
                .collect(Collectors.toSet());
    }

    private ProductDto.Response toResponse(Product product) {
        return ProductDto.Response.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .imagePath(product.getImagePath())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .quantity(product.getInventory() != null ? product.getInventory().getQuantity() : null)
                .tags(product.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .build();
    }
}
