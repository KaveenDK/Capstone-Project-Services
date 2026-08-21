package lk.ijse.eca.studentservice.service;

import lk.ijse.eca.studentservice.dto.StudentRequestDTO;
import lk.ijse.eca.studentservice.dto.StudentResponseDTO;

import java.util.List;

public interface StudentService {

    StudentResponseDTO createStudent(StudentRequestDTO dto);

    StudentResponseDTO updateStudent(String nic, StudentRequestDTO dto);

    void deleteStudent(String nic);

    StudentResponseDTO getStudent(String nic);

    List<StudentResponseDTO> getAllStudents();

    byte[] getStudentPicture(String nic);
}
