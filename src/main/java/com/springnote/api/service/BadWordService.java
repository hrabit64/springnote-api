package com.springnote.api.service;

import com.springnote.api.domain.badWord.BadWord;
import com.springnote.api.domain.badWord.BadWordRepository;
import com.springnote.api.dto.badWord.common.BadWordResponseCommonDto;
import com.springnote.api.dto.badWord.service.BadWordCreateRequestServiceDto;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.formatter.ExceptionMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BadWordService {
    private final BadWordRepository badWordRepository;

    @Transactional(readOnly = true)
    public Page<BadWordResponseCommonDto> getAll(Pageable pageable, @Nullable Boolean type) {
        if (type == null) {
            return badWordRepository.findAll(pageable).map(BadWordResponseCommonDto::new);
        }
        var data = badWordRepository.findBadWordByType(type, pageable);
        return data.map(BadWordResponseCommonDto::new);
    }

    @Transactional(readOnly = true)
    public Page<BadWordResponseCommonDto> getAllByWord(String word, Boolean type, Pageable pageable) {
        var data = badWordRepository.matchByWord(word, type, pageable);
        return data.map(BadWordResponseCommonDto::new);
    }

    @Transactional(readOnly = true)
    public BadWordResponseCommonDto getById(Long id) {
        var data = fetchBadWordById(id);
        return new BadWordResponseCommonDto(data);
    }


    @Transactional
    public BadWordResponseCommonDto create(BadWordCreateRequestServiceDto requestDto) {
        if (badWordRepository.existsByWord(requestDto.getWord())) {
            throw new BusinessException(
                    ExceptionMessageFormatter.createItemAlreadyExistMessage(requestDto.getWord(), "BadWord"),
                    BusinessErrorCode.ITEM_ALREADY_EXIST
            );
        }
        var newBadWord = badWordRepository.save(requestDto.toEntity());
        return new BadWordResponseCommonDto(newBadWord);
    }

    @Transactional
    public BadWordResponseCommonDto delete(Long id) {
        var data = fetchBadWordById(id);
        badWordRepository.delete(data);
        return new BadWordResponseCommonDto(data);
    }

    private BadWord fetchBadWordById(Long id) {
        return badWordRepository.findById(id).orElseThrow(
                () -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(id.toString(), "BadWord"),
                        BusinessErrorCode.ITEM_NOT_FOUND
                )
        );
    }
}
