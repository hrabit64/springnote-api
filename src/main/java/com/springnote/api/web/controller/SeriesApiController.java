package com.springnote.api.web.controller;

import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.domain.series.SeriesSortKeys;
import com.springnote.api.dto.assembler.series.SeriesResponseDtoAssembler;
import com.springnote.api.dto.series.common.SeriesResponseCommonDto;
import com.springnote.api.dto.series.controller.SeriesCreateRequestControllerDto;
import com.springnote.api.dto.series.controller.SeriesUpdateRequestControllerDto;
import com.springnote.api.service.SeriesService;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.utils.validation.pageable.size.PageableSizeCheck;
import com.springnote.api.utils.validation.pageable.sort.PageableSortKeyCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//TODO
@Validated
@RequestMapping("/api/v1/series")
@RequiredArgsConstructor
@RestController
public class SeriesApiController {

    private final SeriesService seriesService;
    private final SeriesResponseDtoAssembler assembler;
    private final PagedResourcesAssembler<SeriesResponseCommonDto> pagedResourcesAssembler;

    @GetMapping("/{id}")
    public EntityModel<SeriesResponseCommonDto> getSeriesById(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "시리즈 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "시리즈 ID가 유효한 범위를 벗어났습니다.")
            Long id
    ) {
        var data = seriesService.getSeriesById(id);
        return assembler.toModel(data);
    }

    @GetMapping("")
    public PagedModel<EntityModel<SeriesResponseCommonDto>> getSeries(
            @RequestParam(value = "name", required = false)
            @Size(min = 2, max = 40, message = "시리즈 이름은 2자 이상, 40자 이하여야 합니다.")
            String name,

            @PageableSortKeyCheck(sortKey = SeriesSortKeys.class)
            @PageableSizeCheck(max = 50)
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {

        var data = (name == null) ? getSeriesWithNoName(pageable) : getSeriesWithName(name, pageable);

        return pagedResourcesAssembler.toModel(data, assembler);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PostMapping("")
    public EntityModel<SeriesResponseCommonDto> createSeries(
            @RequestBody
            @Valid
            SeriesCreateRequestControllerDto requestDto
    ) {

        var data = seriesService.create(requestDto.toServiceDto());

        return assembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PutMapping("/{id}")
    public EntityModel<SeriesResponseCommonDto> updateSeries(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "시리즈 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "시리즈 ID가 유효한 범위를 벗어났습니다.")
            Long id,

            @RequestBody
            @Valid
            SeriesUpdateRequestControllerDto requestDto
    ) {

        var data = seriesService.update(requestDto.toServiceDto(id));

        return assembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeries(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "시리즈 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "시리즈 ID가 유효한 범위를 벗어났습니다.")
            Long id
    ) {

        seriesService.delete(id);

        return ResponseEntity.noContent().build();
    }

    private Page<SeriesResponseCommonDto> getSeriesWithName(String name, Pageable pageable) {
        return seriesService.getSeriesByName(name, pageable);
    }

    private Page<SeriesResponseCommonDto> getSeriesWithNoName(Pageable pageable) {
        return seriesService.getSeries(pageable);
    }

}
