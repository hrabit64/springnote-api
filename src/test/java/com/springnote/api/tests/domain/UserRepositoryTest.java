package com.springnote.api.tests.domain;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.domain.user.User;
import com.springnote.api.domain.user.UserRepository;
import com.springnote.api.testUtils.template.RepositoryTestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

//TODO
@DisplayName("Repository Test - User")
public class UserRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("userRepository.findById")
    @Nested
    class findById {

        @DisplayName("올바른 id로 조회하면, 해당 User를 반환한다.")
        @DataSet(value = "datasets/repository/user/base-user.yaml")
        @Test
        void findById_successWithValidUserId() {
            // given
            var validUserId = "this-is-a-test-user-uid123";

            // when
            var result = userRepository.findById(validUserId).orElse(null);

            // then
            assertNotNull(result);
            assertEquals(validUserId, result.getId());
        }

        @DisplayName("올바르지 않은 id로 조회하면, 빈 값을 반환한다.")
        @DataSet(value = "datasets/repository/user/base-user.yaml")
        @Test
        void findById_failWithInvalidUserId() {
            // given
            var invalidUserId = "notValidUserId";

            // when
            var result = userRepository.findById(invalidUserId).orElse(null);

            // then
            assertNull(result);
        }
    }

    @DisplayName("userRepository.save")
    @Nested
    class save {

        @DisplayName("올바른 User를 저장하면, 성공적으로 저장된다.")
        @DataSet(value = "datasets/repository/user/base-user.yaml")
        @ExpectedDataSet(value = "datasets/repository/user/base-user.yaml")
        @Test
        void save_successWithValidUser() {
            // given
            var validUser = User.builder()
                    .id("this-is-a-test-user-uid123")
                    .name("Test User")
                    .email("notreal@springnote.blog")
                    .provider("springnote")
                    .profileImg("https://springnote.blog/profile/this-is-a-test-user-uid123")
                    .isAdmin(false)
                    .isEnabled(true)
                    .build();

            // when
            var result = userRepository.save(validUser);
            userRepository.flush();

            // then
            assertEquals(validUser.getId(), result.getId());
        }
    }
}
