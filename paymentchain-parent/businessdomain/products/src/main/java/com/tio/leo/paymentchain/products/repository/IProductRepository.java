package com.tio.leo.paymentchain.products.repository;

import com.tio.leo.paymentchain.products.entitys.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author emedina
 */
public interface IProductRepository extends JpaRepository<Product, Long> {
}
