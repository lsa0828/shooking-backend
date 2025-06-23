package com.example.shooking.service;

import com.example.shooking.dto.ImageData;
import com.example.shooking.entity.Product;
import com.example.shooking.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public ImageData getImage(Long id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        File imageFile = new File("C:/shooking/ShookingShop/shooking-shop/public/" + product.getImagePath());
        if (!imageFile.exists()) {
            throw new FileNotFoundException("이미지 파일이 존재하지 않습니다.");
        }

        byte[] data = Files.readAllBytes(imageFile.toPath());
        String contentType = Files.probeContentType(imageFile.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return new ImageData(data, contentType);
    }
}
