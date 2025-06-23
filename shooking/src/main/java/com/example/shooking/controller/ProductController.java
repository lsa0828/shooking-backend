package com.example.shooking.controller;

import com.example.shooking.dto.ApiResponse;
import com.example.shooking.dto.ImageData;
import com.example.shooking.dto.ProductDTO;
import com.example.shooking.entity.Product;
import com.example.shooking.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<?> showAll() {
        try {
            List<Product> products = productService.getAllProducts();
            List<ProductDTO> dtos = products.stream().map(ProductDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok().body(ApiResponse.success("상품 조회", dtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("상품 조회 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<?> showImage(@PathVariable Long id) {
        try {
            ImageData image = productService.getImage(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.contentType()))
                    .body(image.data());
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("이미지 파일이 존재하지 않습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("이미지를 불러오는 중 오류가 발생했습니다."));
        }
    }
}
