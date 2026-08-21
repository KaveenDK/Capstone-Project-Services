package lk.ijse.eca.programservice.mapper;

import lk.ijse.eca.programservice.dto.ProgramDto;
import lk.ijse.eca.programservice.entity.Program;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProgramMapper {

    ProgramDto toDto(Program program);

    Program toEntity(ProgramDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "programId", ignore = true)
    void updateEntity(ProgramDto dto, @MappingTarget Program program);
}
