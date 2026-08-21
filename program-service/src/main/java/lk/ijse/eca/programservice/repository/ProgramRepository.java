package lk.ijse.eca.programservice.repository;

import lk.ijse.eca.programservice.entity.Program;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends MongoRepository<Program, String> {
}
