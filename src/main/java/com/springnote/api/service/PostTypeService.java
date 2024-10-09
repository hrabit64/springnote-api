package com.springnote.api.service;

import com.springnote.api.domain.postType.PostTypeRepository;
import com.springnote.api.dto.postType.common.PostTypeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostTypeService {
    private final PostTypeRepository postTypeRepository;

    @Cacheable(value = "postType")
    public Page<PostTypeResponseDto> get(Pageable pageable) {
        return postTypeRepository.findAll(pageable).map(PostTypeResponseDto::new);
    }
}
