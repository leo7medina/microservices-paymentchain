package com.tio.leo.paymentchain.products.controller;

import com.tio.leo.paymentchain.products.entitys.Product;
import com.tio.leo.paymentchain.products.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author emedina
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private IProductRepository productRepository;

    @GetMapping("/all")
    public List<Product> list() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable long id) {
        return productRepository.findById(id).get();
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Product input) {
        return ResponseEntity.ok(productRepository.save(input));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable String id, @RequestBody Product input) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return null;
    }
}
