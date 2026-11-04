package com.k955.subnet.mapper;

import com.k955.subnet.dtos.Workspace.WorkspaceMemberResponse;
import com.k955.subnet.entity.WorkspaceMember;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkspaceMemberMapper {

    WorkspaceMemberResponse toWorkspaceMemberResponse(WorkspaceMember workspaceMember);

}
