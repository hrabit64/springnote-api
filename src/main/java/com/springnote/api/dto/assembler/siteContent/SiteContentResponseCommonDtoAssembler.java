package com.springnote.api.dto.assembler.siteContent;

import com.springnote.api.dto.siteContent.common.SiteContentResponseCommonDto;
import com.springnote.api.web.controller.SiteContentApiController;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SiteContentResponseCommonDtoAssembler
        implements RepresentationModelAssembler<SiteContentResponseCommonDto, EntityModel<SiteContentResponseCommonDto>> {

    @Override
    public @NotNull EntityModel<SiteContentResponseCommonDto> toModel(SiteContentResponseCommonDto entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(SiteContentApiController.class).getSiteContentByKey(entity.getKey())).withSelfRel());
    }

}
