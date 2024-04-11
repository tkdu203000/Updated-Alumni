package com.boostmytool.beststore.models;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;

public class StudentDto {
    @NotEmpty(message = "The name is required")
    private String name;
    
    @NotEmpty(message = "The major is required")
    private String major;
    
    @NotEmpty(message = "The college is required")
    private String college;
    
    @NotEmpty (message = "The graduation date is required")
    private String graduationdate;
    
   

	@Size(min = 10, message= "The description should be at least 10 characters")
    @Size(max = 2000, message = "The description cannot exceed 2000 characters")
    private String description;
    
    private MultipartFile imageFile;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	

	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}

	public String getGraduationdate() {
		return graduationdate;
	}

	public void setGraduationdate(String graduationdate) {
		this.graduationdate = graduationdate;
	}
	
	
    
    
    
    

    
}
