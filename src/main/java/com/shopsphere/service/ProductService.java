package com.shopsphere.service;

import com.shopsphere.dto.ProductDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductService {

    ProductDto.Response create(ProductDto.Request request);

    ProductDto.Response getById(Long id);

    Page<ProductDto.Response> getAll(Pageable pageable);

    Page<ProductDto.Response> search(String name, Pageable pageable);

    ProductDto.Response update(Long id, ProductDto.Request request);

    ProductDto.Response patch(Long id, Map<String, Object> updates);

    void delete(Long id);

    List<ProductDto.Response> getTopSellingProducts(int limit);

    List<ProductDto.Response> getLowStockProducts(int threshold);

    String uploadImage(Long id, MultipartFile file);

    Resource downloadImage(Long id);
}
