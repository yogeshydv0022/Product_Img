package com.files.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_book")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String bookName;

	@ElementCollection
	private List<String> bookLanguage;

	private String bookAuthor;

	private LocalDate createAt = LocalDate.now();

//	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "product_id")
	private List<ImagesData> images = new ArrayList<>();

	// Constructors, getters, setters, etc.
}
