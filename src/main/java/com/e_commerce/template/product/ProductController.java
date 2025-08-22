package com.e_commerce.template.product;

import com.e_commerce.template.product.dto.ProductCreateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> CreateProduct(@Valid @RequestBody ProductCreateRequest dto) {
        try {
            return ResponseEntity.ok(productService.CreateProduct(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
