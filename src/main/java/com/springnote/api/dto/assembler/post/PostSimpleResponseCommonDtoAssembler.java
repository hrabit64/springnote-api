package com.springnote.api.dto.assembler.post;

import com.springnote.api.dto.post.common.PostSimpleResponseCommonDto;
import com.springnote.api.web.controller.PostApiController;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PostSimpleResponseCommonDtoAssembler
        implements RepresentationModelAssembler<PostSimpleResponseCommonDto, EntityModel<PostSimpleResponseCommonDto>> {

    @Override
    public @NotNull EntityModel<PostSimpleResponseCommonDto> toModel(PostSimpleResponseCommonDto entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(PostApiController.class).getPostById(entity.getId())).withSelfRel(),
                linkTo(methodOn(PostApiController.class).deletePostById(entity.getId())).withRel("delete"),
                linkTo(methodOn(PostApiController.class).updatePost(entity.getId(), null)).withRel("update"),
                linkTo(methodOn(PostApiController.class).updatePostStatus(entity.getId(), null))
                        .withRel("update-status"));
    }

}
