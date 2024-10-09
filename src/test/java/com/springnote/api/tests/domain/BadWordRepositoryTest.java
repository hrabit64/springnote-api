package com.springnote.api.tests.domain;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.domain.badWord.BadWord;
import com.springnote.api.domain.badWord.BadWordRepository;
import com.springnote.api.testUtils.template.RepositoryTestTemplate;
import com.springnote.api.testUtils.validator.ListValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DisplayName("Repository Test - BadWord")
class BadWordRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private BadWordRepository badWordRepository;

    @Nested
    @DisplayName("badWordRepository.findBadWordByType")
    class findBadWordByType {

        @DisplayName("금지 단어 타입으로 조회할 경우, 해당 타입의 BadWord 리스트를 반환한다.")
        @DataSet(value = "datasets/repository/badWord/base-badWord.yaml")
        @Test
        void findBadWordByType_withDisallowType() {
            // given
            var targetType = false;

            // when
            var result = badWordRepository.findBadWordByType(targetType, PageRequest.of(0, 10));

            // then
            assertEquals(1, result.getTotalElements());
            assertEquals(targetType, result.getContent().get(0).getType());
        }

        @DisplayName("허용 단어 타입으로 조회할 경우, 해당 타입의 BadWord 리스트를 반환한다.")
        @DataSet(value = "datasets/repository/badWord/base-badWord.yaml")
        @Test
        void findBadWordByType_withAllowType() {
            // given
            var targetType = false;

            // when
            var result = badWordRepository.findBadWordByType(targetType, PageRequest.of(0, 10));

            // then
            assertEquals(1, result.getTotalElements());
            assertEquals(targetType, result.getContent().get(0).getType());
        }

        private static Stream<Arguments> provideSortKeys() throws Throwable {

            // 검색 옵션 ; 타켓 게시글 아이디
            return Stream.of(
                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("word", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("word", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("type", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("type", Sort.Direction.DESC, List.of(1L, 2L, 3L))
            );
        }

        // 단어 유형 정렬은 해당 메소드에서 검증 불가능하므로 테스트 제외
        @DisplayName("금지 단어 유형과 정렬 옵션이 주어졌을 때, 해당 순으로 정렬된다.")
        @ParameterizedTest(name = "{index} : {0} 정렬 옵션이 {1} 방향으로 주어졌을 때, {2} 순으로 정렬된다.")
        @MethodSource("provideSortKeys")
        @DataSet(value = "datasets/repository/badWord/sort-badWord.yaml")
        void findBadWordByType_withSort(String sortKey, Sort.Direction direction, List<Long> expectedIds) {
            // given
            var targetType = false;
            var testPageable = PageRequest.of(0, 10, direction, sortKey);

            // when
            var result = badWordRepository.findBadWordByType(targetType, testPageable);

            // then
            assertEquals(3, result.getTotalElements());
            assertTrue(ListValidator.isSameList(expectedIds, result.getContent().stream().map(BadWord::getId).toList()));
        }

    }

    @Nested
    @DisplayName("badWordRepository.existsByWord")
    class existsByWord {

        @DisplayName("존재하는 단어가 주어지면, True를 리턴한다.")
        @DataSet(value = "datasets/repository/badWord/base-badWord.yaml")
        @Test
        void existsByWord_withExistWord() {
            // given
            var existsWord = "나쁜말";

            // when
            var result = badWordRepository.existsByWord(existsWord);

            // then
            assertTrue(result);
        }

        @DisplayName("존재하지 않는 단어가 주어지면, False 를 리턴한다.")
        @DataSet(value = "datasets/repository/badWord/base-badWord.yaml")
        @Test
        void existsByWord_withNotExistWord() {
            // given
            var notExistsWord = "전역";

            // when
            var result = badWordRepository.existsByWord(notExistsWord);

            // then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("badWordRepository.findByWordContaining")
    class findByWordContaining {

        @DisplayName("포함하는 단어가 주어지면, 해당 단어를 포함하는 금칙어 리스트를 반환한다.")
        @DataSet(value = "datasets/repository/badWord/match-badWord.yaml")
        @Test
        void findByWordContaining_successMatch() {
            // given
            var containWord = "맘";

            // when
            var result = badWordRepository.findByWordContaining(containWord, PageRequest.of(0, 10));

            // then
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getWord().contains(containWord));
        }

        @DisplayName("포함하지 않는 단어가 주어지면, 빈 결과를 리턴한다.")
        @DataSet(value = "datasets/repository/badWord/match-badWord.yaml")
        @Test
        void findByWordContaining_failMatch() {
            // given
            var notContainWord = "전역";

            // when
            var result = badWordRepository.findByWordContaining(notContainWord, PageRequest.of(0, 10));

            // then
            assertEquals(0, result.getTotalElements());
        }

        private static Stream<Arguments> provideSortKeys() throws Throwable {

            // 검색 옵션 ; 타켓 게시글 아이디
            return Stream.of(
                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L, 3L, 4L, 5L, 6L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(6L, 5L, 4L, 3L, 2L, 1L)),
                    Arguments.of("word", Sort.Direction.ASC, List.of(6L, 3L, 5L, 2L, 4L, 1L)),
                    Arguments.of("word", Sort.Direction.DESC, List.of(1L, 4L, 2L, 5L, 3L, 6L)),
                    Arguments.of("type", Sort.Direction.ASC, List.of(1L, 2L, 3L, 4L, 5L, 6L)),
                    Arguments.of("type", Sort.Direction.DESC, List.of(4L, 5L, 6L, 1L, 2L, 3L))
            );
        }

        @DisplayName("포함하는 단어와 정렬 옵션이 주어졌을 때, 해당 순으로 정렬된다.")
        @ParameterizedTest(name = "{index} : {0} 정렬 옵션이 {1} 방향으로 주어졌을 때, {2} 순으로 정렬된다.")
        @MethodSource("provideSortKeys")
        @DataSet(value = "datasets/repository/badWord/matchAndSort-badWord.yaml")
        void findByWordContaining_successMatchWithSort(String sortKey, Sort.Direction direction, List<Long> expectedIds) {
            // given
            var containWord = "전역";
            var testPageable = PageRequest.of(0, 10, direction, sortKey);
            // when
            var result = badWordRepository.findByWordContaining(containWord, testPageable);

            // then
            assertEquals(6, result.getTotalElements());
            assertTrue(ListValidator.isSameList(expectedIds, result.getContent().stream().map(BadWord::getId).toList()));
        }
    }

    @Nested
    @DisplayName("badWordRepository.findByWordContainingAndType")
    class findByWordContainingAndType {


        private static Stream<Arguments> provideSuccessMatchWithType() throws Throwable {

            // 검색 옵션 ; 타켓 게시글 아이디
            return Stream.of(
                    Arguments.of("나쁜", true, "허용단어"),
                    Arguments.of("나쁜", false, "금지단어")
            );
        }

        @DisplayName("포함하는 단어와 금칙어 유형이 주어지면, 해당 단어를 포함하는 금칙어 리스트를 반환한다.")
        @DataSet(value = "datasets/repository/badWord/match-badWord.yaml")
        @MethodSource("provideSuccessMatchWithType")
        @ParameterizedTest(name = "{index} : {0} 을 포함하는 {2} 금칙어 리스트를 반환한다.")
        void findByWordContainingAndType_successMatchWithType(String containWord, boolean type, String typeStr) {
            // given


            // when
            var result = badWordRepository.findByWordContainingAndType(containWord, type, PageRequest.of(0, 10));

            // then
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getWord().contains(containWord));
            assertEquals(type, result.getContent().get(0).getType());
        }

        private static Stream<Arguments> provideFailMatchWithType() throws Throwable {

            // 검색 옵션 ; 타켓 게시글 아이디
            return Stream.of(
                    Arguments.of("전역", true, "허용단어"),
                    Arguments.of("전역", false, "금지단어")
            );
        }

        @DisplayName("포함하지 않는 단어와 금칙어 유형이 주어지면, 빈 결과를 리턴한다.")
        @DataSet(value = "datasets/repository/badWord/match-badWord.yaml")
        @MethodSource("provideFailMatchWithType")
        @ParameterizedTest(name = "{index} : {0} 을 포함하는 {2} 금칙어 리스트를 반환한다.")
        void findByWordContainingAndType_failMatchWithType(String notContainWord, boolean type, String typeStr) {
            // given

            // when
            var result = badWordRepository.findByWordContainingAndType(notContainWord, type, PageRequest.of(0, 10));

            // then
            assertEquals(0, result.getTotalElements());
        }

        private static Stream<Arguments> provideSortKeysType() throws Throwable {

            // 검색 옵션 ; 타켓 게시글 아이디
            return Stream.of(
                    Arguments.of("전역", "id", Sort.Direction.ASC, false, List.of(1L, 2L, 3L), "금지단어"),
                    Arguments.of("전역", "id", Sort.Direction.DESC, false, List.of(3L, 2L, 1L), "금지단어"),
                    Arguments.of("전역", "word", Sort.Direction.ASC, false, List.of(3L, 2L, 1L), "금지단어"),
                    Arguments.of("전역", "word", Sort.Direction.DESC, false, List.of(1L, 2L, 3L), "금지단어"),
                    Arguments.of("전역", "id", Sort.Direction.ASC, true, List.of(4L, 5L, 6L), "허용단어"),
                    Arguments.of("전역", "id", Sort.Direction.DESC, true, List.of(6L, 5L, 4L), "허용단어"),
                    Arguments.of("전역", "word", Sort.Direction.ASC, true, List.of(6L, 5L, 4L), "허용단어"),
                    Arguments.of("전역", "word", Sort.Direction.DESC, true, List.of(4L, 5L, 6L), "허용단어")
            );
        }

        @DisplayName("포함하는 단어와 단어유형 그리고 정렬 옵션이 주어졌을 때, 해당 순으로 정렬된다.")
        @DataSet(value = "datasets/repository/badWord/matchAndSort-badWord.yaml")
        @MethodSource("provideSortKeysType")
        @ParameterizedTest(name = "{index} : {0} 을 포함하는 {5} 유형의 금칙어를 {1} 정렬 옵션으로 {2} 방향으로 주어졌을 때, {4} 순으로 정렬된다.")
        void findByWordContainingAndType_successMatchWithSortAndType(String containWord, String sortKey, Sort.Direction direction, boolean type, List<Long> expectedIds, String typeStr) {
            // given
            var testPageable = PageRequest.of(0, 10, direction, sortKey);
            // when
            var result = badWordRepository.findByWordContainingAndType(containWord, type, testPageable);

            // then
            assertEquals(3, result.getTotalElements());
            assertTrue(ListValidator.isSameList(expectedIds, result.getContent().stream().map(BadWord::getId).toList()));
        }

    }

    @Nested
    @DisplayName("badWordRepository.findAll")
    class findAll {
        private static Stream<Arguments> provideSortKeys() throws Throwable {

            // 검색 옵션 ; 타켓 게시글 아이디
            return Stream.of(
                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L, 3L, 4L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(4L, 3L, 2L, 1L)),
                    Arguments.of("word", Sort.Direction.ASC, List.of(3L, 2L, 1L, 4L)),
                    Arguments.of("word", Sort.Direction.DESC, List.of(4L, 1L, 2L, 3L)),
                    Arguments.of("type", Sort.Direction.ASC, List.of(1L, 2L, 3L, 4L)),
                    Arguments.of("type", Sort.Direction.DESC, List.of(4L, 1L, 2L, 3L))
            );
        }

        @DisplayName("정렬 옵션이 주어졌을 때, 해당 순으로 정렬된다.")
        @ParameterizedTest(name = "{index} : {0} 정렬 옵션이 {1} 방향으로 주어졌을 때, {2} 순으로 정렬된다.")
        @MethodSource("provideSortKeys")
        @DataSet(value = "datasets/repository/badWord/sort-badWord.yaml")
        void findAll_withSort(String sortKey, Sort.Direction direction, List<Long> expectedIds) {
            // given
            var testPageable = PageRequest.of(0, 10, direction, sortKey);

            // when
            var result = badWordRepository.findAll(testPageable);

            // then
            assertEquals(4, result.getTotalElements());
            assertTrue(ListValidator.isSameList(expectedIds, result.getContent().stream().map(BadWord::getId).toList()));
        }
    }

    @Nested
    @DisplayName("badWordRepository.findById")
    class findById {

        @DisplayName("올바른 ID로 조회할 경우, 해당 ID의 BadWord를 반환한다.")
        @DataSet(value = "datasets/repository/badWord/base-badWord.yaml")
        @Test
        void findById_withValidId() {
            // given
            var validId = 1L;

            // when
            var result = badWordRepository.findById(validId);

            // then
            assertTrue(result.isPresent());
            assertEquals(validId, result.get().getId());
        }

        @DisplayName("올바르지 않은 ID로 조회할 경우, 빈 Optional을 반환한다.")
        @DataSet(value = "datasets/repository/badWord/base-badWord.yaml")
        @Test
        void findById_withNotValidId() {
            // given
            var notValidId = 3L;

            // when
            var result = badWordRepository.findById(notValidId);

            // then
            assertFalse(result.isPresent());
        }

    }


    @Nested
    @DisplayName("badWordRepository.save")
    class save {


        @DisplayName("정상적인 허용 단어 유형의 금칙어를 저장할 경우, 성공적으로 저장된다.")
        @DataSet(value = "datasets/repository/badWord/empty-badWord.yaml", cleanBefore = true, executorId = "TransactionIt")
        @ExpectedDataSet(value = "datasets/repository/badWord/saved-allow-badWord.yaml")
        @Test
        void save_successWithAllowType() {
            // given
            var normalAllowBadWord = BadWord.builder()
                    .word("나쁜말")
                    .type(true)
                    .build();

            // when
            badWordRepository.save(normalAllowBadWord);
        }

        @DisplayName("정상적인 금지 단어 유형의 금칙어를 저장할 경우, 성공적으로 저장된다.")
        @DataSet(value = "datasets/repository/badWord/empty-badWord.yaml", cleanBefore = true, executorId = "TransactionIt")
        @ExpectedDataSet(value = "datasets/repository/badWord/saved-disallow-badWord.yaml")
        @Test
        void save_successWithDisallowType() {
            // given
            var normalDisallowBadWord = BadWord.builder()
                    .word("나쁜말")
                    .type(false)
                    .build();

            // when
            var badWord = badWordRepository.save(normalDisallowBadWord);
            badWordRepository.flush();
            log.info("badWord : {}", badWord);
            assertEquals("나쁜말", badWord.getWord());
        }
//
//        private static Stream<Arguments> provideWrongBadWord() throws Throwable {
//            return Stream.of(
//                    //type, word, 틀린 이유
//                    Arguments.of(true, "허용단어", "최대금칙어는15글자이지만해당금칙어는이는초과합니다.", "금칙어 길이 초과"),
//                    Arguments.of(false, "금지단어", "최대금칙어는15글자이지만해당금칙어는이는초과합니다.", "금칙어 길이 초과"),
//                    Arguments.of(true, "허용단어", "나쁜말", "중복되는 금칙어"),
//                    Arguments.of(false, "금지단어", "나쁜말", "중복되는 금칙어")
//            );
//        }
//
//        @DisplayName("잘못된 금칙어를 저장할 경우, 예외를 던진다.")
//        @ParameterizedTest(name = "{index} : {1} 유형의 금칙어와 {3}가 주어진 경우 경우, 예외를 던진다.")
//        @MethodSource("provideWrongBadWord")
//        @DataSet(value = "datasets/repository/badWord/saved-allow-badWord.yaml")
//        @ExpectedDataSet(value = "datasets/repository/badWord/saved-allow-badWord.yaml")
//        void save_failWithWrongBadWord(boolean type, String typeStr, String word, String reason) {
//            // given
//            var wrongBadWord = BadWord.builder()
//                    .word(word)
//                    .type(type)
//                    .build();
//
//            // when, then
//            assertThrows(Exception.class, () -> badWordRepository.save(wrongBadWord));
//        }
    }
}