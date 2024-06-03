package com.files.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.files.model.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {

}
