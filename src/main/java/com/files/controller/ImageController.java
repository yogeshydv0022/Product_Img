package com.files.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.files.exciption.FileNotSupportedException;
import com.files.model.ImagesData;
import com.files.service.ImageService;


import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/image")
public class ImageController {
	
	@Autowired
	private ImageService imageService;
	
	// Upload Images
		@PostMapping("/")
		public ResponseEntity<Object> uploadFiles(@RequestParam("files") MultipartFile[] files) {

			try {
				List<ImagesData> filedata = Arrays.stream(files).map(file -> {
					try {
						return imageService.AddImage(file);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}).collect(Collectors.toList());

				return new ResponseEntity<>(filedata, HttpStatus.CREATED);
			} catch (UncheckedIOException e) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (FileNotSupportedException e) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
		}

		// Download Images
		@GetMapping("/download/{filename:.+}")
		public ResponseEntity<Object> downloadFile(@PathVariable String filename, HttpServletRequest request) {
			try {
				Resource resource = this.imageService.fetchFileAsResource(filename);
				String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
				if (contentType == null) {
					contentType = "application/octet-stream";
				}
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
						.body(resource);
			} catch (IOException ex) {
				return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
			}
		}

		// Get All Images
		@GetMapping("/")
		@ResponseStatus(code = HttpStatus.OK)
		public List<ImagesData> getAllFiles() {
			return imageService.getAllImages();
		}

		// Delete By Id
		@DeleteMapping("/{id}")
		@ResponseStatus(code = HttpStatus.OK)
		public void deleteById(@PathVariable("id") long id) {
			imageService.DeleteById(id);
		}
	
	

}
