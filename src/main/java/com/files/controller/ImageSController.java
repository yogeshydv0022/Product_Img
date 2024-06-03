package com.files.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.files.exciption.FileNotSupportedException;
import com.files.model.ImagesData;
import com.files.service.ImageService;

@RestController
@RequestMapping("/api/images")
public class ImageSController {

	@Autowired
	private ImageService imageService;

//	// Upload Images
//	@PostMapping("/")
//	public ResponseEntity<Object> uploadFiles(@RequestParam("files") MultipartFile[] files) {
//		try {
//			// Process uploaded files
//			for (MultipartFile file : files) {
//				imageService.AddImage(file);
//			}
//			return new ResponseEntity<>("Files uploaded successfully", HttpStatus.CREATED);
//		} catch (IOException e) {
//			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//		} catch (FileNotSupportedException e) {
//			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//		}
//	}

	    // Upload Images and associate with product
	    @PostMapping("/")
	    public ResponseEntity<Object> uploadFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("productId") long productId) throws IOException {
	        try {
	            List<ImagesData> imageDataList = Arrays.stream(files)
	                    .map(file -> {
	                        try {
	                            return imageService.addImageToProduct(productId, file);
	                        } catch (IOException e) {
	                            throw new UncheckedIOException(e);
	                        }
	                    })
	                    .collect(Collectors.toList());

	            return new ResponseEntity<>(imageDataList, HttpStatus.CREATED);
	        } catch (UncheckedIOException e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	        } catch (FileNotSupportedException e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	        }
	    }
	

}
