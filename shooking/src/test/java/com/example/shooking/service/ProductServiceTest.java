package com.example.shooking.service;

import com.example.shooking.dto.ImageData;
import com.example.shooking.dto.ProductDTO;
import com.example.shooking.entity.Brand;
import com.example.shooking.entity.Product;
import com.example.shooking.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("정상적인 모든 상품 정보 조회")
    void getAllProducts_ShouldReturnProductList() {
        Brand brand1 = new Brand(1L, "브랜드1");
        Brand brand2 = new Brand(2L, "브랜드2");
        List<Product> mockList = List.of(
                new Product(1L, brand1, "편한 신발", 16000, "img1.jpg"),
                new Product(2L, brand2, "멋진 신발", 15000, "img2.jpg")
        );
        when(productRepository.findAll()).thenReturn(mockList);

        List<ProductDTO> result = productService.getAllProducts();
        ProductDTO product = result.get(0);
        Field[] fields = product.getClass().getDeclaredFields();
        assertEquals(1L, product.getId());
        assertEquals("브랜드1", product.getBrand());
        assertEquals("편한 신발", product.getDescription());
        assertEquals(16000, product.getPrice());
        assertEquals(4, fields.length);
    }

    @Test
    @DisplayName("정상적인 상품 이미지 조회")
    void getImage_ShouldReturnImageData() throws IOException {
        Brand brand1 = new Brand(1L, "브랜드1");
        Product product = new Product(1L, brand1, "신발", 10000, "img1.jpg");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ImageData result = productService.getImage(1L);
        assertNotNull(result);
        assertEquals("image/jpeg", result.contentType());
        assertTrue(result.data().length > 0);
    }

    @Test
    @DisplayName("없는 상품 이미지 조회")
    void getImage_ShouldThrow_WhenProductNotFound() {
        given(productRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            productService.getImage(1L);
        });
    }

    @Test
    @DisplayName("존재하지 않는 이미지 파일 조회")
    void getImage_ShouldThrow_WhenImageFileNotFound() {
        Brand brand1 = new Brand(1L, "브랜드1");
        Product product = new Product(1L, brand1, "신발", 10000, "nonexistent.jpg");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        assertThrows(FileNotFoundException.class, () -> {
            productService.getImage(1L);
        });
    }
}
