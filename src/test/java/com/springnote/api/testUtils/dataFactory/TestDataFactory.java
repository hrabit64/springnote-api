package com.springnote.api.testUtils.dataFactory;

import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.domain.post.PostQueryKeys;
import com.springnote.api.utils.context.UserContext;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class TestDataFactory {
    public static PageRequest getPageRequest() {
        return PageRequest.of(0, 10);
    }

    public static Pageable getMockPageable() {
        return Mockito.mock(Pageable.class);
    }

    public static Page<Object> createPageObject(List<Object> items) {
        return new PageImpl<Object>(items);
    }

    public static Page<Object> createPageObject(Object item) {
        return new PageImpl<Object>(List.of(item));
    }

    public static MultiValueMap<String, String> getMockQueryParam() {
        return Mockito.mock(MultiValueMap.class);
    }

    public static MultiValueMap<PostQueryKeys, String> getMockPostQueryParam() {
        return Mockito.mock(MultiValueMap.class);
    }


    public static LocalDateTime testLocalDateTime() {
        return LocalDateTime.of(2002, 8, 28, 0, 0, 0);
    }

    public static LocalDate testLocalDate() {
        return LocalDate.of(2002, 8, 28);
    }

    public static Page<Object> createPageObject(List<Object> items, Integer page, Integer size, String sortKey, Sort.Direction direction) {
        return new PageImpl<Object>(items, PageRequest.of(page, size, direction, sortKey), items.size());
    }

    public static void createUserContextReturns(UserContext userContext, AuthLevel authLevel) {
        switch (authLevel) {
            case ADMIN -> {
                Mockito.doReturn(true).when(userContext).isAdmin();
                Mockito.doReturn(true).when(userContext).isAuthed();
            }
            case USER -> {
                Mockito.doReturn(false).when(userContext).isAdmin();
                Mockito.doReturn(true).when(userContext).isAuthed();
            }
            case NONE -> {
                Mockito.doReturn(false).when(userContext).isAdmin();
                Mockito.doReturn(false).when(userContext).isAuthed();
            }
        }
    }

}
