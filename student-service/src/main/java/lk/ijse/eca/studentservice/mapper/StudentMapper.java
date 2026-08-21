package lk.ijse.eca.studentservice.mapper;

import lk.ijse.eca.studentservice.dto.StudentRequestDTO;
import lk.ijse.eca.studentservice.dto.StudentResponseDTO;
import lk.ijse.eca.studentservice.entity.Student;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class StudentMapper {

    @Mapping(target = "picture", expression = "java(buildPictureUrl(student))")
    public abstract StudentResponseDTO toResponseDto(Student student);

    @Mapping(target = "picture", ignore = true)
    public abstract Student toEntity(StudentRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "nic", ignore = true)
    @Mapping(target = "picture", ignore = true)
    public abstract void updateEntity(StudentRequestDTO dto, @MappingTarget Student student);

    protected String buildPictureUrl(Student student) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/students/{nic}/picture")
                .buildAndExpand(student.getNic())
                .toUriString();
    }
}
