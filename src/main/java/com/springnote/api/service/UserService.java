package com.springnote.api.service;

import com.springnote.api.domain.user.User;
import com.springnote.api.domain.user.UserRepository;
import com.springnote.api.dto.user.common.UserResponseCommonDto;
import com.springnote.api.dto.user.service.UserCreateRequestServiceDto;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.formatter.ExceptionMessageFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Cacheable(value = "user", key = "#id")
    @Transactional(readOnly = true)
    public UserResponseCommonDto getUser(String id) {
        var user = fetchUserById(id);
        return new UserResponseCommonDto(user);
    }

    @CachePut(value = "user", key = "#requestDto.getUid()")
    @Transactional
    public UserResponseCommonDto register(UserCreateRequestServiceDto requestDto) {
        var user = requestDto.toEntity();

        var savedUser = userRepository.save(user);

        return new UserResponseCommonDto(savedUser);
    }

    private User fetchUserById(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(id, "유저"),
                        BusinessErrorCode.ITEM_NOT_FOUND
                )
        );
    }


}
