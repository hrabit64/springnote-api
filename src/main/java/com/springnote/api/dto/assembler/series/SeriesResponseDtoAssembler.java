package com.springnote.api.dto.assembler.series;

import com.springnote.api.dto.series.common.SeriesResponseCommonDto;
import com.springnote.api.web.controller.SeriesApiController;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class SeriesResponseDtoAssembler
        implements RepresentationModelAssembler<SeriesResponseCommonDto, EntityModel<SeriesResponseCommonDto>> {

    @Override
    public @NotNull EntityModel<SeriesResponseCommonDto> toModel(@NotNull SeriesResponseCommonDto entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(SeriesApiController.class).getSeriesById(entity.getId())).withSelfRel(),
                linkTo(methodOn(SeriesApiController.class).deleteSeries(entity.getId())).withRel("delete"),
                linkTo(methodOn(SeriesApiController.class).updateSeries(entity.getId(), null)).withRel("update"));
    }

}
