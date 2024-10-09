package com.springnote.api.service;

import com.springnote.api.domain.siteContent.SiteContent;
import com.springnote.api.domain.siteContent.SiteContentRepository;
import com.springnote.api.dto.siteContent.common.SiteContentResponseCommonDto;
import com.springnote.api.dto.siteContent.service.SiteContentCreateRequestServiceDto;
import com.springnote.api.dto.siteContent.service.SiteContentUpdateRequestServiceDto;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.formatter.ExceptionMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SiteContentService {
    private final SiteContentRepository siteContentRepository;

    @Transactional
    public void delete(String id) {
        var siteContent = fetchSiteContentById(id);
        siteContentRepository.delete(siteContent);
    }

    @Transactional(readOnly = true)
    public SiteContentResponseCommonDto getSiteContentById(String id) {
        var siteContent = fetchSiteContentById(id);
        return new SiteContentResponseCommonDto(siteContent);
    }

    @Transactional
    public SiteContentResponseCommonDto update(SiteContentUpdateRequestServiceDto requestDto) {
        var siteContent = fetchSiteContentById(requestDto.getKey());
        siteContent.setValue(requestDto.getValue());

        var savedSiteContent = siteContentRepository.save(siteContent);

        return new SiteContentResponseCommonDto(savedSiteContent);
    }

    @Transactional
    public SiteContentResponseCommonDto create(SiteContentCreateRequestServiceDto requestDto) {
        checkKeyExist(requestDto.getKey());

        var siteContent = requestDto.toEntity();
        var savedSiteContent = siteContentRepository.save(siteContent);

        return new SiteContentResponseCommonDto(savedSiteContent);
    }

    private SiteContent fetchSiteContentById(String id) {
        return siteContentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(id, "사이트 컨텐츠"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }

    private void checkKeyExist(String key) {
        if (siteContentRepository.existsByKey(key)) {
            throw new BusinessException(
                    ExceptionMessageFormatter.createItemAlreadyExistMessage(key, "사이트 컨텐츠"),
                    BusinessErrorCode.ITEM_ALREADY_EXIST);
        }
    }
}
