package com.e_commerce.template.product;

import com.e_commerce.template.product.dto.ProductCreateRequest;
import com.e_commerce.template.product.model.Product;
import com.e_commerce.template.product.model.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public ResponseEntity<?> CreateProduct(@Valid @RequestBody ProductCreateRequest dto) {
        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        productRepository.save(product);
        return ResponseEntity.ok(product);
    }


}
