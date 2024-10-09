package com.springnote.api.dto.assembler.comment;

import com.springnote.api.dto.comment.common.CommentResponseCommonDto;
import com.springnote.api.web.controller.CommentApiController;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CommentResponseCommonDtoAssembler implements
        RepresentationModelAssembler<CommentResponseCommonDto, EntityModel<CommentResponseCommonDto>> {

    @Override
    public @NotNull EntityModel<CommentResponseCommonDto> toModel(@NotNull CommentResponseCommonDto entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(CommentApiController.class).getCommentById(entity.getId())).withSelfRel(),
                linkTo(methodOn(CommentApiController.class).updateComment(entity.getId(), null)).withRel("update"),
                linkTo(methodOn(CommentApiController.class).updateCommentStatus(entity.getId())).withRel("disable"));
    }
}
