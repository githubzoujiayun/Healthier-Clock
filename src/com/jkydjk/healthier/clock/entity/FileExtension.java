package com.jkydjk.healthier.clock.entity;

import java.io.File;

import org.apache.http.entity.FileEntity;

public class FileExtension extends FileEntity {

	public FileExtension(File file, String contentType) {
		super(file, contentType);
		// TODO Auto-generated constructor stub
	}

}
