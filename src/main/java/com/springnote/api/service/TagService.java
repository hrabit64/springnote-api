package com.springnote.api.service;

import com.springnote.api.domain.tag.Tag;
import com.springnote.api.domain.tag.TagRepository;
import com.springnote.api.dto.tag.common.TagResponseDto;
import com.springnote.api.dto.tag.service.TagCreateRequestServiceDto;
import com.springnote.api.dto.tag.service.TagUpdateRequestServiceDto;
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
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public TagResponseDto create(TagCreateRequestServiceDto requestDto) {
        var tag = requestDto.toEntity();

        checkIsNameExist(requestDto.getName());

        var savedTag = tagRepository.save(tag);

        return new TagResponseDto(savedTag);
    }

    @Transactional
    public TagResponseDto delete(Long id) {
        var tag = fetchTagById(id);

        tagRepository.delete(tag);

        return new TagResponseDto(tag);
    }

    private Tag fetchTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(id.toString(), "태그"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }

    @Transactional
    public TagResponseDto update(TagUpdateRequestServiceDto requestDto) {

        var tag = fetchTagById(requestDto.getId());

        if (!isNameNeedUpdate(requestDto.getName(), tag.getName())) {
            return new TagResponseDto(tag);
        }
        checkIsNameExist(requestDto.getName());
        tag.setName(requestDto.getName());

        var updatedTag = tagRepository.save(tag);
        return new TagResponseDto(updatedTag);
    }

    @Transactional(readOnly = true)
    public TagResponseDto getById(Long id) {
        var tag = fetchTagById(id);
        return new TagResponseDto(tag);
    }

    @Transactional(readOnly = true)
    public Page<TagResponseDto> getByName(String name, Pageable pageable) {
        return tagRepository.findAllByNameContaining(name, pageable)
                .map(TagResponseDto::new);
    }

    @Transactional(readOnly = true)
    public Page<TagResponseDto> getAll(Pageable pageable) {
        return tagRepository.findAll(pageable)
                .map(TagResponseDto::new);
    }

    @Transactional(readOnly = true)
    public boolean isExistByName(String name) {
        return tagRepository.existsByName(name);
    }

    private void checkIsNameExist(String name) {
        if (tagRepository.existsByName(name)) {
            throw new BusinessException(ExceptionMessageFormatter.createItemAlreadyExistMessage(name, "태그"),
                    BusinessErrorCode.ITEM_ALREADY_EXIST);
        }
    }

    private boolean isNameNeedUpdate(String newName, String oldName) {
        return !newName.equals(oldName);
    }

}
