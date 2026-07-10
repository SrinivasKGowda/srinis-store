package com.shopsphere.repository;

import com.shopsphere.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("select oi.product from OrderItem oi group by oi.product order by sum(oi.quantity) desc")
    List<Product> findTopSellingProducts(Pageable pageable);

    @Query("select p from Product p where p.inventory.quantity < :threshold")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    @Query(value = "select * from product p where p.price between :minPrice and :maxPrice", nativeQuery = true)
    List<Product> findByPriceRangeNative(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
}
