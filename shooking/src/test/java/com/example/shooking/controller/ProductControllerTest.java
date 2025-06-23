package com.example.shooking.controller;

import com.example.shooking.dto.ImageData;
import com.example.shooking.entity.Product;
import com.example.shooking.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("정상적인 모든 상품 조회 API 요청")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void showAll_ShouldReturnProductList() throws Exception {
        List<Product> productList = List.of(
                new Product(1L, "브랜드1", "편한 신발", 16000, "img1.jpg"),
                new Product(2L, "브랜드2", "멋진 신발", 15000, "img2.jpg")
        );
        given(productService.getAllProducts()).willReturn(productList);

        mockMvc.perform(get("/api/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품 조회"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].brand").value("브랜드1"))
                .andExpect(jsonPath("$.data[0].description").value("편한 신발"))
                .andExpect(jsonPath("$.data[0].price").value(16000))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].brand").value("브랜드2"))
                .andExpect(jsonPath("$.data[1].description").value("멋진 신발"))
                .andExpect(jsonPath("$.data[1].price").value(15000))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].length()").value(4));
    }

    @Test
    @DisplayName("정상적인 상품 이미지 조회 API 요청")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void showImage_ShouldReturnImageData() throws Exception {
        byte[] imageBytes = new byte[]{1, 2, 3}; // 임의 이미지 바이트
        String contentType = "image/jpeg;charset=UTF-8";

        ImageData imageData = new ImageData(imageBytes, contentType);
        given(productService.getImage(anyLong())).willReturn(imageData);

        mockMvc.perform(get("/api/product/image/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().bytes(imageBytes));
    }
}
