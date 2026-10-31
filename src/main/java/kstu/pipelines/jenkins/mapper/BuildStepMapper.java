package kstu.pipelines.jenkins.mapper;

import kstu.pipelines.jenkins.dto.response.BuildStepResponse;
import kstu.pipelines.jenkins.entity.BuildStep;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuildStepMapper {
    BuildStepResponse toResponse(BuildStep buildStep);
}
