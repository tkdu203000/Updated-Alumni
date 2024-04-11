package com.boostmytool.beststore.controllers;

import java.io.IOException;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;



import java.text.SimpleDateFormat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;
import com.boostmytool.beststore.models.Student;
import com.boostmytool.beststore.models.StudentDto;
import com.boostmytool.beststore.services.StudentRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@Controller
// @RestController
@RequestMapping("/students")
public class StudentsController {
	
	@Autowired
	private StudentRepository repo;

	

	@GetMapping({ "", "/" })
	// public String showStudentList(Model model, Authentication authentication) {
	// 	UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	// 	boolean isAdmin = authentication.getAuthorities().stream()
	// 			.anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

	// 	List<Student> students;

	// 	// Retrieve products based on the user's role
	// 	students = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		

	// 	model.addAttribute("students", students);

	// 	// 
	// 	return isAdmin ? "students/admin" : "students/index";
	// }
	public String showStudentList(Model model) {
		List<Student> students = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("students", students);
		return "students/index";
		
	}
	
	
	
	
	
	
	@GetMapping("/create")
	public String showCreatePage(Model model) {
        StudentDto studentDto = new StudentDto();
        model.addAttribute("studentDto", studentDto);
        return "students/CreateStudent"; 
    }
	



	
	// submit validation form
	@PostMapping("/create")
	public String createStudent(
			@Valid @ModelAttribute StudentDto studentDto,
			BindingResult result) {
		if (studentDto.getImageFile().isEmpty()) {
			result.addError(new FieldError("studentDto", "imageFile", "The image is required"));
		}

		

		if (result.hasErrors()) {
			return "students/CreateStudent";
		}

		// save image file
		MultipartFile image = studentDto.getImageFile();
		Date createAt = new Date();
		String storageFileName = createAt.getTime() + "_" + image.getOriginalFilename();

		try {
			String uploadDir = "public/images/";
			Path uploadPath = Paths.get(uploadDir);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
						StandardCopyOption.REPLACE_EXISTING);
			}

		} catch (Exception ex) {
			System.out.println("Error saving image file: " + ex.getMessage());
		}

		// Create a new Student object and set its properties
		Student student = new Student();
		student.setName(studentDto.getName());
		student.setMajor(studentDto.getMajor());
		student.setCollege(studentDto.getCollege());
		student.setGraduationdate(studentDto.getGraduationdate());
		student.setDescription(studentDto.getDescription());
		student.setCreatedAt(createAt);
		student.setImageFileName(storageFileName);

		// Save the student to the database
		repo.save(student);

		return "redirect:/students";
	}

	
	
	@GetMapping("/edit")
	public String showEditPage(Model model, @RequestParam int id, Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		boolean isAdmin = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(role -> role.equals("ROLE_ADMIN"));

		try {
			Student student = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid student id"));
			model.addAttribute("student", student);

			StudentDto studentDto = new StudentDto();
			studentDto.setName(student.getName());
			studentDto.setMajor(student.getMajor());
			studentDto.setCollege(student.getCollege());
			studentDto.setGraduationdate(student.getGraduationdate());
			studentDto.setDescription(student.getDescription());

			model.addAttribute("studentDto", studentDto);
			model.addAttribute("isAdmin", isAdmin); // Pass isAdmin flag to the view
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			return "redirect:/students";
		}

		return "students/EditStudent";
	}


	@PostMapping("/edit")
	public String updateStudent(
			Model model,
			@RequestParam int id,
			@Valid @ModelAttribute StudentDto studentDto,
			BindingResult result) {
		try {
			

			if (result.hasErrors()) {
				return "students/EditStudent";
			}
			Student student = repo.findById(id).get();
			model.addAttribute("student", student);

			if (!studentDto.getImageFile().isEmpty()) {
				// delete old image file
				String uploadDir = "public/images/";
				Path oldImagePath = Paths.get(uploadDir + student.getImageFileName());

				try {
					Files.delete(oldImagePath);
				} catch (Exception ex) {
					System.out.println("Error deleting old image file: " + ex.getMessage());
				}

				// save new image file
				MultipartFile image = studentDto.getImageFile();
				java.sql.Date createAt = java.sql.Date.valueOf(LocalDate.now());
				String storageFileName = createAt.getTime() + "_" + image.getOriginalFilename();

				try (InputStream inputStream = image.getInputStream()) {
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
							StandardCopyOption.REPLACE_EXISTING);
				}

				student.setImageFileName(storageFileName);
			}

			student.setName(studentDto.getName());
			student.setMajor(studentDto.getMajor());
			student.setCollege(studentDto.getCollege());
			student.setGraduationdate(studentDto.getGraduationdate());
			student.setDescription(studentDto.getDescription());

			repo.save(student);

		} catch (Exception ex) {
			System.out.println("Error getting student: " + ex.getMessage());
		}

		return "redirect:/students";
	}
	


	@GetMapping("/delete")
	public String deleteStudent (
			@RequestParam int id 
			) {
		
		try {
			Student student = repo.findById(id).get();

			//delete image file
			Path imagePath = Paths.get("public/images/" + student.getImageFileName());

			try {
				Files.delete(imagePath);
			} 
			catch (Exception ex) {
				System.out.println("Error deleting image file: " + ex.getMessage());
			}


			repo.delete(student);
		}
		catch (Exception ex) {
			System.out.println("Error deleting student: " + ex.getMessage());
		

		}
		return "redirect:/students";}

	

		@GetMapping("/login")
		public String login() {
			  return "login_form";
		}

		



	
		


	


	
}
