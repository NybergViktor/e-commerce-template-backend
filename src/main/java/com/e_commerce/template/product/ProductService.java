package com.e_commerce.template.product;

import com.e_commerce.template.product.dto.ProductCreateRequest;
import com.e_commerce.template.common.exception.ProductNotFoundException;
import com.e_commerce.template.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductCreateRequest> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductCreateRequest getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return convertToDto(product);
    }

    public ProductCreateRequest createProduct(ProductCreateRequest productDto) {
        Product product = convertToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    public ProductCreateRequest updateProduct(String id, ProductCreateRequest productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        existingProduct.setTitle(productDto.getTitle());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setPrice(productDto.getPrice());

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDto(updatedProduct);
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductCreateRequest convertToDto(Product product) {
        return ProductCreateRequest.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    private Product convertToEntity(ProductCreateRequest productDto) {
        return Product.builder()
                .id(productDto.getProductId())
                .title(productDto.getTitle())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .build();
    }
}