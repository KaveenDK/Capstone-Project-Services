package lk.ijse.eca.enrollmentservice.mapper;

import lk.ijse.eca.enrollmentservice.dto.EnrollmentDto;
import lk.ijse.eca.enrollmentservice.dto.StudentDto;
import lk.ijse.eca.enrollmentservice.entity.Enrollment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EnrollmentMapper {

    @Mapping(target = "student", source = "student")
    EnrollmentDto toDto(Enrollment enrollment, StudentDto student);

    @Mapping(target = "id", ignore = true)
    Enrollment toEntity(EnrollmentDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(EnrollmentDto dto, @MappingTarget Enrollment enrollment);
}
