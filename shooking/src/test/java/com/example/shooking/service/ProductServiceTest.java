package com.example.shooking.service;

import com.example.shooking.dto.ImageData;
import com.example.shooking.entity.Product;
import com.example.shooking.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private final String TEST_IMAGE_PATH = "C:/shooking/ShookingShop/shooking-shop/public/img1.jpg";

    @BeforeEach
    void setup() throws IOException {
        File imageFile = new File(TEST_IMAGE_PATH);
        imageFile.getParentFile().mkdirs();
        Files.write(imageFile.toPath(), "fake image data".getBytes());
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Path.of(TEST_IMAGE_PATH));
    }

    @Test
    void getAllProducts_ShouldReturnProductList() {
        List<Product> mockList = List.of(
                new Product(1L, "브랜드1", "편한 신발", 16000, "img1.jpg"),
                new Product(2L, "브랜드2", "멋진 신발", 15000, "img2.jpg")
        );
        when(productRepository.findAll()).thenReturn(mockList);

        List<Product> result = productService.getAllProducts();
        Product product = result.get(0);
        Field[] fields = product.getClass().getDeclaredFields();
        assertEquals(1L, product.getId());
        assertEquals("브랜드1", product.getBrand());
        assertEquals("편한 신발", product.getDescription());
        assertEquals(16000, product.getPrice());
        assertEquals("img1.jpg", product.getImagePath());
        assertEquals(5, fields.length);
    }

    @Test
    void getImage_ShouldReturnImageData() throws IOException {
        Product product = new Product(1L, "브랜드", "신발", 10000, "img1.jpg");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ImageData result = productService.getImage(1L);
        assertNotNull(result);
        assertEquals("image/jpeg", result.contentType());
        assertTrue(result.data().length > 0);
    }

    @Test
    void getImage_ShouldThrow_WhenProductNotFound() {
        given(productRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            productService.getImage(1L);
        });
    }

    @Test
    void getImage_ShouldThrow_WhenImageFileNotFound() {
        Product product = new Product(1L, "브랜드", "신발", 10000, "nonexistent.jpg");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        assertThrows(FileNotFoundException.class, () -> {
            productService.getImage(1L);
        });
    }
}
