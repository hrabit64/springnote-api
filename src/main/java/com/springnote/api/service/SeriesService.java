package com.springnote.api.service;

import com.springnote.api.domain.post.PostRepository;
import com.springnote.api.domain.series.Series;
import com.springnote.api.domain.series.SeriesRepository;
import com.springnote.api.dto.series.common.SeriesResponseCommonDto;
import com.springnote.api.dto.series.service.SeriesCreateRequestServiceDto;
import com.springnote.api.dto.series.service.SeriesUpdateRequestServiceDto;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.formatter.ExceptionMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final PostRepository postRepository;

    @Transactional
    public SeriesResponseCommonDto create(SeriesCreateRequestServiceDto requestDto) {
        checkIsNameExist(requestDto.getName());
        var series = requestDto.toEntity();
        var newSeries = seriesRepository.save(series);
        return new SeriesResponseCommonDto(newSeries);
    }

    @Transactional
    public SeriesResponseCommonDto delete(Long id) {
        var series = fetchSeriesById(id);
        seriesRepository.delete(series);
        var targetPosts = postRepository.findAllBySeries(series);
        if (!targetPosts.isEmpty()) postRepository.deleteAll(targetPosts);
        return new SeriesResponseCommonDto(series);
    }

    private Series fetchSeriesById(Long id) {
        return seriesRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(id.toString(), "시리즈"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }

    @Transactional
    public SeriesResponseCommonDto update(SeriesUpdateRequestServiceDto requestDto) {
        var series = fetchSeriesById(requestDto.getId());

        if (isTitleNeedUpdate(requestDto.getName(), series.getName())) {
            checkIsNameExist(requestDto.getName());
            series.setName(requestDto.getName());
        }

        series.update(requestDto.toEntity());
        var updatedSeries = seriesRepository.save(series);

        return new SeriesResponseCommonDto(updatedSeries);
    }

    @Transactional(readOnly = true)
    public SeriesResponseCommonDto getSeriesById(Long id) {
        var series = fetchSeriesById(id);
        return new SeriesResponseCommonDto(series);
    }

    @Transactional(readOnly = true)
    public Page<SeriesResponseCommonDto> getSeries(Pageable pageable) {
        return seriesRepository.findAll(pageable)
                .map(SeriesResponseCommonDto::new);
    }

    @Transactional(readOnly = true)
    public Page<SeriesResponseCommonDto> getSeriesByName(String name, Pageable pageable) {
        return seriesRepository.findAllByNameContaining(name, pageable)
                .map(SeriesResponseCommonDto::new);
    }

    @Transactional(readOnly = true)
    public boolean isNameExist(String name) {
        checkIsNameExist(name);
        return true;
    }

    private void checkIsNameExist(String name) {
        if (seriesRepository.existsByName(name)) {
            throw new BusinessException("( " + name + " ) 에 해당하는 시리즈가 이미 존재합니다.",
                    BusinessErrorCode.ITEM_ALREADY_EXIST);
        }
    }

    private boolean isTitleNeedUpdate(String newTitle, String oldTitle) {
        return !newTitle.equals(oldTitle);
    }

}
