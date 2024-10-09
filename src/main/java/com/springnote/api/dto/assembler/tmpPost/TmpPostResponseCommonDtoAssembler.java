package com.springnote.api.dto.assembler.tmpPost;

import com.springnote.api.dto.tmpPost.common.TmpPostResponseCommonDto;
import com.springnote.api.web.controller.TmpPostApiController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TmpPostResponseCommonDtoAssembler implements RepresentationModelAssembler<TmpPostResponseCommonDto, EntityModel<TmpPostResponseCommonDto>> {

    @Override
    public EntityModel<TmpPostResponseCommonDto> toModel(TmpPostResponseCommonDto entity) {

        return EntityModel.of(entity,
                linkTo(methodOn(TmpPostApiController.class).getTmpPostById(entity.getId())).withSelfRel(),
                linkTo(methodOn(TmpPostApiController.class).updateTmpPost(entity.getId(), null)).withRel("update"),
                linkTo(methodOn(TmpPostApiController.class).deleteTmpPost(entity.getId())).withRel("delete")
//                linkTo(methodOn(TmpPostApiController.class).publishTmpPost(entity.getId())).withRel("publish")
        );
    }
}
