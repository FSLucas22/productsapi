package com.api.products.controllers;

import com.api.products.dtos.ProductRecordDto;
import com.api.products.models.ProductModel;
import com.api.products.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {
    private static final String URL = "/products";
    @Autowired
    ProductRepository repository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(URL)
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto dto) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(dto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(productModel));
    }

    @PreAuthorize("permitAll()")
    @GetMapping(URL)
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        var products = repository.findAll();
        products.forEach(
                u -> {
                    var id = u.getIdProduct();
                    u.add(linkTo(methodOn(ProductController.class).getProductById(id)).withSelfRel());
                });
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(URL + "/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable(value = "id") UUID id ) {
        var product = repository.findById(id);
        if (product.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        return ResponseEntity.status(HttpStatus.OK).body(
                product.get().add(linkTo(methodOn(ProductController.class).getAllProducts())
                        .withRel("Products List")));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(URL + "/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
                                                @RequestBody @Valid ProductRecordDto dto) {
        var product = repository.findById(id);
        if (product.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        var updatingProduct = product.get();
        updatingProduct.setName(dto.name());
        BeanUtils.copyProperties(dto, updatingProduct);
        return ResponseEntity.status(HttpStatus.OK).body(repository.save(updatingProduct));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(URL + "/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(value = "id") UUID id) {
        var product = repository.findById(id);
        if (product.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        repository.delete(product.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
    }
}
