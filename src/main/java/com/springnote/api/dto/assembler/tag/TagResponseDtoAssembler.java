package com.springnote.api.dto.assembler.tag;

import com.springnote.api.dto.tag.common.TagResponseDto;
import com.springnote.api.web.controller.TagApiController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class TagResponseDtoAssembler
        implements RepresentationModelAssembler<TagResponseDto, EntityModel<TagResponseDto>> {

    @Override
    public EntityModel<TagResponseDto> toModel(TagResponseDto entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(TagApiController.class).getTagById(entity.getId())).withSelfRel(),
                linkTo(methodOn(TagApiController.class).deleteTag(entity.getId())).withRel("delete"),
                linkTo(methodOn(TagApiController.class).updateTag(entity.getId(), null)).withRel("update"));
    }

}
