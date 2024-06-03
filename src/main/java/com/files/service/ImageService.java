package com.files.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.files.exciption.FileNotSupportedException;
import com.files.exciption.ProductNotFoundException;
import com.files.model.ImagesData;
import com.files.model.Product;
import com.files.repository.ImageDataRepository;
import com.files.repository.ProductRepository;


@Service
public class ImageService {

	@Autowired
	private ImageDataRepository imageRepository;
	
	@Autowired
	private ProductRepository productRepository;

	public ImageService() throws IOException {
	}

	// Path
	private final Path UPLOAD_PATH = Paths.get(new ClassPathResource("").getFile().getAbsolutePath() + File.separator
			+ "static" + File.separator + "images");

	public ImagesData AddImage(MultipartFile file) throws IOException {
		// create Directories
		if (!Files.exists(UPLOAD_PATH)) {
			Files.createDirectories(UPLOAD_PATH);
		}

		// file format validation
		if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png") && !file.getContentType().equals("image/jpg")) {
			throw new FileNotSupportedException("only .jpeg and .png images are " + "supported");
		}

		//
		String timeStampedFileName = new SimpleDateFormat("ssmmHHddMMyyyy").format(new Date()) + "_"
				+ file.getOriginalFilename();
		Path filePath = UPLOAD_PATH.resolve(timeStampedFileName);
		// File Copy In your Path
		Files.copy(file.getInputStream(), filePath);
		// FileUri
		String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/images/").path(timeStampedFileName)
				.toUriString();
		// Set Download URI
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/file/download/")
				.path(timeStampedFileName).toUriString();
		// Save In Data Base
		ImagesData fileDetails = new ImagesData(file.getOriginalFilename(), fileUri, fileDownloadUri, file.getSize());
		ImagesData Records = imageRepository.save(fileDetails);
		return Records;
	}

	// get All Images
	public List<ImagesData> getAllImages() {
		return imageRepository.findAll();
	}

	// Delete By Id
	public void DeleteById(long id) {
		imageRepository.deleteById(id);
	}

	// fatch Single Image
	// ============================Not
	// Working========================================
	public Resource fetchFileAsResource(String fileName) throws FileNotFoundException {
		try {
			Path filePath = UPLOAD_PATH.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new FileNotFoundException("File not found " + fileName);
		}
	}
	
	public ImagesData addImageToProduct(long productId, MultipartFile file) throws IOException {
	    // Find the product by ID
	    Optional<Product> optionalProduct = productRepository.findById(productId);
	    if (optionalProduct.isPresent()) {
	        Product product = optionalProduct.get();

	        // create Directories
	        if (!Files.exists(UPLOAD_PATH)) {
	            Files.createDirectories(UPLOAD_PATH);
	        }

	        // file format validation
	        if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png") && !file.getContentType().equals("image/jpg")) {
	            throw new FileNotSupportedException("only .jpeg and .png images are supported");
	        }

	        // Generate timestamped file name
	        String timeStampedFileName = new SimpleDateFormat("ssmmHHddMMyyyy").format(new Date()) + "_" + file.getOriginalFilename();
	        Path filePath = UPLOAD_PATH.resolve(timeStampedFileName);

	        // Copy file to the specified path
	        Files.copy(file.getInputStream(), filePath);

	        // File URI
	        String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/images/").path(timeStampedFileName).toUriString();

	        // Set Download URI
	        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/file/download/").path(timeStampedFileName).toUriString();

	        // Save image data to the database
	        ImagesData imageData = new ImagesData(file.getOriginalFilename(), fileUri, fileDownloadUri, file.getSize());
	        ImagesData savedImageData = imageRepository.save(imageData);

	        // Associate the image with the product
	        product.getImages().add(savedImageData);
	        productRepository.save(product);

	        return savedImageData;
	    } else {
	        throw new ProductNotFoundException("Product with ID " + productId + " not found");
	    }
	}


}
