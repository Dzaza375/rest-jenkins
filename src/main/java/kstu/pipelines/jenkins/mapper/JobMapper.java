package kstu.pipelines.jenkins.mapper;

import kstu.pipelines.jenkins.dto.response.JobResponse;
import kstu.pipelines.jenkins.entity.Job;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobMapper {
    JobResponse toResponse(Job job);
}
