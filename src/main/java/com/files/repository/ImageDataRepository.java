package com.files.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.files.model.ImagesData;

public interface ImageDataRepository  extends JpaRepository<ImagesData,Long>{

}
