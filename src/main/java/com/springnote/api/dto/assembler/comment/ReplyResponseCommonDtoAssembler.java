package com.springnote.api.dto.assembler.comment;

import com.springnote.api.dto.comment.common.ReplyResponseCommonDto;
import com.springnote.api.web.controller.CommentApiController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReplyResponseCommonDtoAssembler implements
        RepresentationModelAssembler<ReplyResponseCommonDto, EntityModel<ReplyResponseCommonDto>> {

    @Override
    public EntityModel<ReplyResponseCommonDto> toModel(ReplyResponseCommonDto entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(CommentApiController.class).getCommentById(entity.getId())).withSelfRel(),
                linkTo(methodOn(CommentApiController.class).updateComment(entity.getId(), null)).withRel("update"),
                linkTo(methodOn(CommentApiController.class).updateCommentStatus(entity.getId())).withRel("disable"),
                linkTo(methodOn(CommentApiController.class).getCommentById(entity.getParentId())).withRel("parent"));
    }
}
