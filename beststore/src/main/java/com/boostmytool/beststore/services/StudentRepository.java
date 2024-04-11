package com.boostmytool.beststore.services;

import org.springframework.data.jpa.repository.JpaRepository;


import com.boostmytool.beststore.models.Student;

public interface StudentRepository extends JpaRepository<Student, Integer>  {

}
