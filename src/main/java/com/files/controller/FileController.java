package com.files.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.files.exciption.FileNotSupportedException;
import com.files.model.ImagesData;
import com.files.model.Product;
import com.files.service.ImageService;
import com.files.service.ProductService;

@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable("id") long id) {
        productService.deleteProductById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

 // Set product images
    @PostMapping("/product/{productId}")
    public ResponseEntity<Object> setProductImages(@PathVariable long productId, @RequestParam("files") MultipartFile[] files) {
        try {
            List<ImagesData> imageDataList = new ArrayList<>();
            for (MultipartFile file : files) {
                ImagesData imageData = imageService.addImageToProduct(productId, file);
                if (imageData != null) {
                    imageDataList.add(imageData);
                }
            }
            if (!imageDataList.isEmpty()) {
                return new ResponseEntity<>(imageDataList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Product not found or no valid images uploaded", HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileNotSupportedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

