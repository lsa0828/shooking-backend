package com.example.shooking.integration;

import com.example.shooking.entity.Product;
import com.example.shooking.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private final String TEST_IMAGE_PATH = "C:/shooking/ShookingShop/shooking-shop/public/img1.jpg";

    @BeforeEach
    void setup() throws Exception {
        productRepository.save(new Product(1L, "브랜드1", "편한 신발", 16000, "img1.jpg"));
        File imageFile = new File(TEST_IMAGE_PATH);
        imageFile.getParentFile().mkdirs();
        Files.write(imageFile.toPath(), "fake image data".getBytes());
    }

    @AfterEach
    void cleanup() throws Exception {
        Files.deleteIfExists(Path.of(TEST_IMAGE_PATH));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품 조회"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].brand").value("브랜드1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetImage() throws Exception {
        mockMvc.perform(get("/api/product/image/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg;charset=UTF-8"))
                .andExpect(content().bytes("fake image data".getBytes()));
    }
}
