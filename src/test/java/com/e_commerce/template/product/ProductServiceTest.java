package com.e_commerce.template.product;

import com.e_commerce.template.product.dto.ProductCreateRequest;
import com.e_commerce.template.common.exception.ProductNotFoundException;
import com.e_commerce.template.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductCreateRequest productDto;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id("1L")
                .title("Laptop")
                .description("A powerful laptop")
                .price(1200.00)
                .build();

        productDto = ProductCreateRequest.builder()
                .productId("1L")
                .title("Laptop")
                .description("A powerful laptop")
                .price(1200.00)
                .build();
    }

    @Test
    @DisplayName("Should return all products when products exist")
    void getAllProducts_shouldReturnAllProducts() {
        // Given
        when(productRepository.findAll()).thenReturn(List.of(product));

        // When
        List<ProductCreateRequest> products = productService.getAllProducts();

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals(productDto.getTitle(), products.get(0).getTitle());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void getAllProducts_shouldReturnEmptyList() {
        // Given
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<ProductCreateRequest> products = productService.getAllProducts();

        // Then
        assertNotNull(products);
        assertTrue(products.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return product when product ID exists")
    void getProductById_whenProductExists_shouldReturnProduct() {
        // Given
        when(productRepository.findById("1L")).thenReturn(Optional.of(product));

        // When
        ProductCreateRequest foundProduct = productService.getProductById("1L");

        // Then
        assertNotNull(foundProduct);
        assertEquals(productDto.getProductId(), foundProduct.getProductId());
        verify(productRepository, times(1)).findById("1L");
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product ID does not exist")
    void getProductById_whenProductDoesNotExist_shouldThrowException() {
        // Given
        when(productRepository.findById("1L")).thenReturn(Optional.empty());

        // When & Then
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById("1L");
        });

        assertEquals("Product not found with id: 1L", exception.getMessage());
        verify(productRepository, times(1)).findById("1L");
    }

    @Test
    @DisplayName("Should create and return a new product")
    void createProduct_shouldSaveAndReturnProduct() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When
        ProductCreateRequest createdProduct = productService.createProduct(productDto);

        // Then
        assertNotNull(createdProduct);
        assertEquals(productDto.getTitle(), createdProduct.getTitle());
        assertEquals("1L", createdProduct.getProductId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update and return product when product exists")
    void updateProduct_whenProductExists_shouldUpdateAndReturnProduct() {
        // Given
        ProductCreateRequest updatedDto = ProductCreateRequest.builder()
                .productId("1L")
                .title("Gaming Laptop")
                .description("An even more powerful laptop")
                .price(1500.00)
                .build();

        when(productRepository.findById("1L")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProductCreateRequest result = productService.updateProduct("1L", updatedDto);

        // Then
        assertNotNull(result);
        assertEquals(updatedDto.getTitle(), result.getTitle());
        assertEquals(updatedDto.getDescription(), result.getDescription());
        assertEquals(updatedDto.getPrice(), result.getPrice());
        verify(productRepository, times(1)).findById("1L");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating a non-existent product")
    void updateProduct_whenProductDoesNotExist_shouldThrowException() {
        // Given
        when(productRepository.findById("1L")).thenReturn(Optional.empty());

        // When & Then
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.updateProduct("1L", productDto));

        assertEquals("Product not found with id: 1L", exception.getMessage());
        verify(productRepository, times(1)).findById("1L");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete product when product exists")
    void deleteProduct_whenProductExists_shouldDeleteProduct() {
        // Given
        when(productRepository.existsById("1L")).thenReturn(true);
        doNothing().when(productRepository).deleteById("1L");

        // When
        assertDoesNotThrow(() -> productService.deleteProduct("1L"));

        // Then
        verify(productRepository, times(1)).existsById("1L");
        verify(productRepository, times(1)).deleteById("1L");
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when deleting a non-existent product")
    void deleteProduct_whenProductDoesNotExist_shouldThrowException() {
        // Given
        when(productRepository.existsById("1L")).thenReturn(false);

        // When & Then
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct("1L"));

        assertEquals("Product not found with id: 1L", exception.getMessage());
        verify(productRepository, times(1)).existsById("1L");
        verify(productRepository, never()).deleteById("1L");
    }
}