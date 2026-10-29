package com.k955.subnet.mapper;

import com.k955.subnet.dtos.Workspace.WorkspaceResponse;
import com.k955.subnet.entity.Workspace;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {

    WorkspaceResponse toWorkspaceResponse(Workspace workspace);

}
