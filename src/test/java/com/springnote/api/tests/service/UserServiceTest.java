package com.springnote.api.tests.service;

import com.springnote.api.domain.user.User;
import com.springnote.api.domain.user.UserRepository;
import com.springnote.api.dto.user.common.UserResponseCommonDto;
import com.springnote.api.dto.user.service.UserCreateRequestServiceDto;
import com.springnote.api.service.UserService;
import com.springnote.api.testUtils.auth.TestFirebaseAuthManager;
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

    @Mock
    private TestFirebaseAuthManager authManager;


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

    @DisplayName("userRepository.deleteUser")
    @Nested
    class deleteUser {

        @DisplayName("올바른 id를 입력하면, 해당 유저를 삭제한다.")
        @Test
        void deleteUser_success() {
            // given
            var targetId = "testId";
            var targetUser = User
                    .builder()
                    .id("testId")
                    .name("test")
                    .profileImg("test.jpg")
                    .isEnabled(true)
                    .isAdmin(false)
                    .build();

            doReturn(Optional.of(targetUser)).when(userRepository).findById(targetId);
            doReturn(true).when(authManager).deactive(targetId);

            // when
            userService.deleteUser(targetId);

            // then
            verify(userRepository).delete(targetUser);
            verify(authManager).deactive(targetId);
        }

        @DisplayName("올바르지 않은 id를 입력하면, BusinessException을 반환한다.")
        @Test
        void deleteUser_fail() {
            // givenU
            var targetId = "testId";

            doReturn(Optional.empty()).when(userRepository).findById(targetId);

            // when
            var result = assertThrows(BusinessException.class, () -> userService.deleteUser(targetId));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(userRepository).findById(targetId);
        }

        @DisplayName("Firebase에서 유저 삭제에 실패하면, BusinessException을 반환한다.")
        @Test
        void deleteUser_fail2() {
            // given
            var targetId = "testId";
            var targetUser = User
                    .builder()
                    .id("testId")
                    .name("test")
                    .profileImg("test.jpg")
                    .isEnabled(true)
                    .isAdmin(false)
                    .build();

            doReturn(Optional.of(targetUser)).when(userRepository).findById(targetId);
            doReturn(false).when(authManager).deactive(targetId);

            // when
            var result = assertThrows(BusinessException.class, () -> userService.deleteUser(targetId));

            // then
            assertEquals(BusinessErrorCode.SERVER_ERROR, result.getErrorCode());
            verify(userRepository).findById(targetId);
            verify(authManager).deactive(targetId);
        }

    }


}
