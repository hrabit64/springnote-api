package com.springnote.api.tests.service;

import com.springnote.api.domain.user.User;
import com.springnote.api.domain.user.UserRepository;
import com.springnote.api.dto.user.common.UserResponseCommonDto;
import com.springnote.api.dto.user.service.UserCreateRequestServiceDto;
import com.springnote.api.service.UserService;
import com.springnote.api.testUtils.template.ServiceTestTemplate;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@DisplayName("Service Test - UserService")
public class UserServiceTest extends ServiceTestTemplate {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;


    @DisplayName("userRepository.getUser")
    @Nested
    class getUSer {

        @DisplayName("올바른 id를 입력하면, 해당 유저를 반환한다.")
        @Test
        void getUser_success() {
            // given
            var targetId = "testId";
            var targetUser = User
                    .builder()
                    .id(targetId)
                    .email("test@springnote.blog")
                    .name("test")
                    .isAdmin(false)
                    .isEnabled(true)
                    .profileImg("test.jpg")
                    .provider("local")
                    .build();

            doReturn(Optional.of(targetUser)).when(userRepository).findById(targetId);

            // when
            var result = userService.getUser(targetId);

            // then
            var expected = UserResponseCommonDto.builder()
                    .id(targetId)
                    .email("test@springnote.blog")
                    .name("test")
                    .isAdmin(false)
                    .isEnabled(true)
                    .profileImg("test.jpg")
                    .provider("local")
                    .build();

            assertEquals(result, expected);
            verify(userRepository).findById(targetId);
        }

        @DisplayName("올바르지 않은 id를 입력하면, BusinessException을 반환한다.")
        @Test
        void getUser_fail() {
            // given
            var targetId = "testId";

            doReturn(Optional.empty()).when(userRepository).findById(targetId);

            // when
            var result = assertThrows(BusinessException.class, () -> userService.getUser(targetId));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(userRepository).findById(targetId);
        }

    }

    @DisplayName("userRepository.register")
    @Nested
    class register {

        @DisplayName("올바른 UserCreateRequestServiceDto를 입력하면, 해당 유저를 등록한다.")
        @Test
        void register_success() {
            // given
            var targetUser = User
                    .builder()
                    .id("testId")
                    .name("test")
                    .profileImg("test.jpg")
                    .isEnabled(true)
                    .isAdmin(false)
                    .build();

            var requestDto = UserCreateRequestServiceDto.builder()
                    .name("test")
                    .uid("testId")
                    .profileImage("test.jpg")
                    .build();

            doReturn(targetUser).when(userRepository).save(targetUser);

            // when
            var result = userService.register(requestDto);

            // then
            var expected = UserResponseCommonDto.builder()
                    .id("testId")
                    .name("test")
                    .profileImg("test.jpg")
                    .isEnabled(true)
                    .isAdmin(false)
                    .build();

            assertEquals(expected, result);
            verify(userRepository).save(targetUser);
        }
    }
}
