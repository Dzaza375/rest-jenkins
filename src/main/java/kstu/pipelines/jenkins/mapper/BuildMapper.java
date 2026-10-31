package kstu.pipelines.jenkins.mapper;

import kstu.pipelines.jenkins.dto.response.BuildConsoleResponse;
import kstu.pipelines.jenkins.dto.response.BuildResponse;
import kstu.pipelines.jenkins.entity.Build;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BuildMapper {
    @Mapping(source = "job.id", target = "jobId")
    BuildResponse toResponse(Build build);

    @Mapping(source = "id", target = "buildId")
    BuildConsoleResponse toConsoleResponse(Build build);
}
