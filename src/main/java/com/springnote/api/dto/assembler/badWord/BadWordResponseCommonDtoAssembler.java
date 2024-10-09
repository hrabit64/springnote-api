package com.springnote.api.dto.assembler.badWord;

import com.springnote.api.dto.badWord.common.BadWordResponseCommonDto;
import com.springnote.api.web.controller.BadWordApiController;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BadWordResponseCommonDtoAssembler implements
        RepresentationModelAssembler<BadWordResponseCommonDto, EntityModel<BadWordResponseCommonDto>> {

    @Override
    public @NotNull EntityModel<BadWordResponseCommonDto> toModel(@NotNull BadWordResponseCommonDto entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(BadWordApiController.class).getBadWordById(entity.getId())).withSelfRel(),
                linkTo(methodOn(BadWordApiController.class).deleteBadWord(entity.getId())).withRel("delete"));
    }
}
