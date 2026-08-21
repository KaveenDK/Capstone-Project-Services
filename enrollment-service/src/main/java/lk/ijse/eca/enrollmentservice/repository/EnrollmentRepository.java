package lk.ijse.eca.enrollmentservice.repository;

import lk.ijse.eca.enrollmentservice.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByProgramIdOrderByDateDescIdDesc(String programId);
}
