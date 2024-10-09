package com.springnote.api.tests.service;

import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.postType.PostTypeRepository;
import com.springnote.api.dto.postType.common.PostTypeResponseDto;
import com.springnote.api.service.PostTypeService;
import com.springnote.api.testUtils.dataFactory.TestDataFactory;
import com.springnote.api.testUtils.template.ServiceTestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@DisplayName("Service Test - PostTypeService")
public class PostTypeServiceTest extends ServiceTestTemplate {

    @InjectMocks
    private PostTypeService postTypeService;

    @Mock
    private PostTypeRepository postTypeRepository;

    @DisplayName("PostTypeService - get")
    @Nested
    class get {

        @DisplayName("전체 Post Type을 조회한다.")
        @Test
        void get_success() {
            //given
            var targetPostType = PostType.builder()
                    .id(1L)
                    .name("postType")
                    .isCanAddComment(true)
                    .isNeedSeries(true)
                    .build();

            doReturn(new PageImpl<>(List.of(targetPostType)))
                    .when(postTypeRepository)
                    .findAll(ArgumentMatchers.any(Pageable.class));

            //when
            var result = postTypeService.get(TestDataFactory.getMockPageable());

            //then
            var expected = PostTypeResponseDto.builder()
                    .id(1L)
                    .name("postType")
                    .build();

            assertEquals(1, result.getContent().size());
            assertEquals(expected, result.getContent().get(0));
            verify(postTypeRepository).findAll(ArgumentMatchers.any(Pageable.class));
        }

    }

}
