package com.springnote.api.domain.badWord.queryDsl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springnote.api.domain.SortKeyUtil;
import com.springnote.api.domain.badWord.BadWord;
import com.springnote.api.domain.badWord.BadWordSortKeys;
import com.springnote.api.domain.badWord.QBadWord;
import com.springnote.api.utils.type.TypeParser;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public class BadWordQRepositoryImpl implements BadWordQRepository {
    private final TypeParser typeParser;

    private final JPAQueryFactory jpaQueryFactory;

    private final QBadWord qBadWord = QBadWord.badWord;

    /**
     * 주어진 단어로 금칙어를 검색합니다. (FullText Match)
     *
     * @param word     검색할 단어
     * @param type     금칙어 유형(true: 허용, false: 금지)
     * @param pageable the pageable
     * @return the page
     */
    @Override
    public Page<BadWord> matchByWord(String word, @Nullable Boolean type, Pageable pageable) {
        // 검색 옵션 (where 절)
        var whereExpressionSet = new HashSet<BooleanExpression>();
        whereExpressionSet.add(createMatchWithWord(word));
        if (type != null) whereExpressionSet.add(createMatchWithType(type));


        var whereExpression = whereExpressionSet.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);

        // 정렬 옵션 (order by 절)
        var orderSpecifiers = createOrderSpecifiers(pageable.getSort());

        return fetchItem(pageable, whereExpression, orderSpecifiers);
    }

    /**
     * 주어진 검색 조건을 결함하여 금칙어를 검색합니다.
     *
     * @param pageable        the pageable
     * @param whereExpression the where expression (where 절)
     * @param orderSpecifiers the order specifiers (order by 절)
     */
    private Page<BadWord> fetchItem(Pageable pageable, Predicate whereExpression, OrderSpecifier<?>[] orderSpecifiers) {
        var content = jpaQueryFactory.selectFrom(qBadWord)
                .where(whereExpression)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        var count = getCount(whereExpression);

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }


    /**
     * 주어진 검색 조건에 해당하는 금칙어의 개수를 반환합니다.
     *
     * @param whereExpression the where expression
     * @return the count
     */
    private Long getCount(Predicate whereExpression) {
        return jpaQueryFactory.select(qBadWord.count()).from(qBadWord).where(whereExpression).fetchOne();
    }

    /**
     * 주어진 단어로 FullText Match 검색 Expression 을 생성합니다.
     *
     * @param word 검색할 단어
     * @return the boolean expression
     */
    private BooleanExpression createMatchWithWord(String word) {

        return Expressions.numberTemplate(Double.class, "function('match', {0}, {1})",
                        qBadWord.word, word)
                .gt(0.0);


    }

    /**
     * 주어진 금칙어 유형으로 검색 Expression 을 생성합니다.
     *
     * @param type 검색할 금칙어 유형 (true: 허용, false: 금지)
     * @return the boolean expression
     */
    private BooleanExpression createMatchWithType(@NotNull Boolean type) {
        return qBadWord.type.eq(type);
    }

    /**
     * 주어진 Sort 에 따라 정렬 옵션(Order by 절)을 생성합니다.
     *
     * @param sort the sort
     * @return the order specifier [ ]
     */
    private OrderSpecifier<?>[] createOrderSpecifiers(Sort sort) {

        List<OrderSpecifier<?>> orderSpecifiers = new LinkedList<>();

        var pageSortKeys = sort.get().toList();

        for (var pageSortKey : pageSortKeys) {
            var direction = pageSortKey.getDirection();

            var sortKey = SortKeyUtil.getKey(BadWordSortKeys.class, pageSortKey.getProperty());

            if (sortKey == null) {
                throw new IllegalArgumentException("Invalid sort key: " + pageSortKey.getProperty());
            }

            switch (sortKey) {
                case ID -> orderSpecifiers.add(direction.isAscending() ? qBadWord.id.asc() : qBadWord.id.desc());
                case WORD -> orderSpecifiers.add(direction.isAscending() ? qBadWord.word.asc() : qBadWord.word.desc());
                case TYPE -> orderSpecifiers.add(direction.isAscending() ? qBadWord.type.asc() : qBadWord.type.desc());
            }
        }

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }
}


