package com.springnote.api.dto.assembler.config;

import com.google.firebase.database.annotations.NotNull;
import com.springnote.api.dto.config.common.ConfigResponseCommonDto;
import com.springnote.api.web.controller.ConfigApiController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class ConfigResponseCommonDtoAssembler implements RepresentationModelAssembler<ConfigResponseCommonDto, EntityModel<ConfigResponseCommonDto>> {

    @Override
    public @NotNull EntityModel<ConfigResponseCommonDto> toModel(@NotNull ConfigResponseCommonDto entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(ConfigApiController.class).getConfigByKey(entity.getKey())).withSelfRel());
    }
}
