package com.springnote.api.tests.domain;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.domain.image.Image;
import com.springnote.api.domain.image.ImageRepository;
import com.springnote.api.testUtils.template.RepositoryTestTemplate;
import com.springnote.api.testUtils.validator.ListValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Repository Test - Image")
public class ImageRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private ImageRepository imageRepository;

    @Nested
    @DisplayName("imageRepository.save")
    class save {
        @DisplayName("올바른 이미지가 주어지면, 해당 이미지를 저장한다.")
        @DataSet(value = "datasets/repository/image/empty-image.yaml")
        @ExpectedDataSet(value = "datasets/repository/image/base-image.yaml")
        @Test
        void save_successWithValidImage() {
            // given
            var image = Image.builder()
                    .convertedName("c5ba0314-787a-4df2-bb92-d444adf79b9d.webp")
                    .createdAt(LocalDateTime.of(2002, 8, 28, 0, 0, 0))
                    .postId(null)
                    .width(100)
                    .height(100)
                    .format("png")
                    .build();

            // when
            imageRepository.save(image);
        }

        private static Stream<Arguments> provideWrongImage() throws Throwable {
            var notValidFormat = Image.builder()
                    .convertedName("c5ba0314-787a-4df2-bb92-d444adf79b9d.webp")
                    .createdAt(LocalDateTime.of(2002, 8, 28, 0, 0, 0))
                    .postId(null)
                    .width(100)
                    .height(100)
                    .format("umm...this is not valid format")
                    .build();

            var notValidFileName = Image.builder()
                    .convertedName("a".repeat(256))
                    .createdAt(LocalDateTime.of(2002, 8, 28, 0, 0, 0))
                    .postId(null)
                    .width(100)
                    .height(100)
                    .format("png")
                    .build();

            return Stream.of(
                    //type, word, 틀린 이유
                    Arguments.of(notValidFormat, "잘못된 포맷"),
                    Arguments.of(notValidFileName, "잘못된 파일명")
            );
        }

        @DisplayName("올바르지 않은 이미지가 주어지면, 해당 이미지를 저장하지 않는다.")
        @DataSet(value = "datasets/repository/image/empty-image.yaml")
        @ExpectedDataSet(value = "datasets/repository/image/empty-image.yaml")
        @MethodSource("provideWrongImage")
        @ParameterizedTest(name = "{index} : {1} 을(를) 저장하려고 하면, 저장하지 않는다.")
        void save_failWithInvalidImage(Image image, String word) {
            // when
            assertThrows(Exception.class, () -> imageRepository.save(image));

        }
    }

    @Nested
    @DisplayName("imageRepository.findByConvertedName")
    class findByConvertedName {
        @DisplayName("올바른 이미지 이름이 주어지면, 해당 이미지를 반환한다.")
        @DataSet(value = "datasets/repository/image/base-image.yaml")
        @Test
        void findByConvertedName_successWithValidConvertedName() {
            // given
            var convertedName = "c5ba0314-787a-4df2-bb92-d444adf79b9d.webp";

            // when
            var image = imageRepository.findByConvertedName(convertedName);

            // then
            assertTrue(image.isPresent());
            assertEquals(convertedName, image.get().getConvertedName());
        }

        @DisplayName("올바르지 않은 이미지 이름이 주어지면, 해당 이미지를 반환하지 않는다.")
        @DataSet(value = "datasets/repository/image/base-image.yaml")
        @Test
        void findByConvertedName_failWithInvalidConvertedName() {
            // given
            var convertedName = "c5ba0314-787a-4df2-bb92-d444adf79b9d.png";

            // when
            var image = imageRepository.findByConvertedName(convertedName);

            // then
            assertTrue(image.isEmpty());
        }
    }

    @DisplayName("imageRepository.getAll")
    @Nested
    class getAll {
        @DisplayName("이미지가 존재하면, 모든 이미지를 반환한다.")
        @DataSet(value = "datasets/repository/image/base-image.yaml")
        @Test
        void getAll_successWithExistImages() {
            // when
            var images = imageRepository.findAll();

            // then
            assertEquals(1, images.size());
        }

        @DisplayName("이미지가 존재하지 않으면, 빈 리스트를 반환한다.")
        @DataSet(value = "datasets/repository/image/empty-image.yaml")
        @Test
        void getAll_successWithNotExistImages() {
            // when
            var images = imageRepository.findAll();

            // then
            assertTrue(images.isEmpty());
        }

        private static Stream<Arguments> provideSortKey() {
            return Stream.of(
                    Arguments.of("id", Sort.Direction.DESC, List.of(2L, 1L)),
                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L)),
                    Arguments.of("postId", Sort.Direction.DESC, List.of(2L, 1L)),
                    Arguments.of("postId", Sort.Direction.ASC, List.of(1L, 2L)),
                    Arguments.of("createdAt", Sort.Direction.DESC, List.of(2L, 1L)),
                    Arguments.of("createdAt", Sort.Direction.ASC, List.of(1L, 2L))
            );
        }

        @DisplayName("정렬 키가 주어지면, 해당 키로 정렬된 이미지를 반환한다.")
        @DataSet(value = "datasets/repository/image/sort-image.yaml")
        @MethodSource("provideSortKey")
        @ParameterizedTest(name = "{index} : {0} 키로 {1} 순서로 정렬하면, {2} 순서로 반환된다.")
        void getAll_successWithSortKey(String key, Sort.Direction direction, List<Long> expected) {
            // when
            var result = imageRepository.findAll(Sort.by(direction, key));

            // then
            assertEquals(expected.size(), result.size());
            assertTrue(ListValidator.isSameList(result.stream().map(Image::getId).toList(), expected));
        }
    }


}
