package com.springnote.api.domain.post.queryDsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springnote.api.domain.SortKeyUtil;
import com.springnote.api.domain.content.QContent;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.post.PostQueryKeys;
import com.springnote.api.domain.post.PostSortKeys;
import com.springnote.api.domain.post.QPost;
import com.springnote.api.domain.postTag.QPostTag;
import com.springnote.api.domain.postType.QPostType;
import com.springnote.api.domain.series.QSeries;
import com.springnote.api.utils.type.TypeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 게시글 검색을 위한 QueryDSL Repository 구현체
 *
 * @auther 황준서 ( hzser123@gmail.com)
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class PostQRepositoryImpl implements PostQRepository {

    private final TypeParser typeParser;

    private final JPAQueryFactory jpaQueryFactory;

    private final QPost qPost = QPost.post;
    private final QPostType qPostType = QPostType.postType;
    private final QPostTag qPostTag = QPostTag.postTag;
    private final QSeries qSeries = QSeries.series;
    private final QContent qContent = QContent.content;

    /**
     * 주어진 keyword가 제목에 포함되는 게시글을 불러옴.
     *
     * @param keyword     검색 키워드
     * @param queryParams 검색옵션
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    @Override
    public Page<Post> matchByTitle(String keyword, MultiValueMap<PostQueryKeys, String> queryParams, Pageable pageable) {


        var whereExpressionSet = new HashSet<BooleanExpression>();

        whereExpressionSet.add(createWhereExpressionsWithTitleKeyword(keyword));

        if (!queryParams.isEmpty()) {
            whereExpressionSet.add(createWhereExpression(queryParams));
        }

        var whereExpressions = whereExpressionSet.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);

        var orderSpecifiers = createOrderSpecifiers(pageable.getSort());

        return fetchItemWithWhereExpression(whereExpressions, orderSpecifiers, pageable);
    }


    /**
     * 주어진 keyword가 본문에 포함되는 게시글을 불러옴.
     *
     * @param keyword     검색 키워드
     * @param queryParams 검색옵션
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    @Override
    public Page<Post> matchByContent(String keyword, MultiValueMap<PostQueryKeys, String> queryParams, Pageable pageable) {

        var whereExpressionSet = new HashSet<BooleanExpression>();

        whereExpressionSet.add(createWhereExpressionsWithContentKeyword(keyword));

        if (!queryParams.isEmpty()) {
            whereExpressionSet.add(createWhereExpression(queryParams));
        }

        var whereExpressions = whereExpressionSet.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);

        var orderSpecifiers = createOrderSpecifiers(pageable.getSort());

        return fetchItemWithWhereExpression(whereExpressions, orderSpecifiers, pageable);
    }

    /**
     * 주어진 keyword가 본문 혹은 제목에 포함되는 게시글을 불러옴.
     *
     * @param keyword     검색 키워드
     * @param queryParams 검색옵션
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    @Override
    public Page<Post> matchByMix(String keyword, MultiValueMap<PostQueryKeys, String> queryParams,
                                 Pageable pageable) {

        var whereExpressionSet = new HashSet<BooleanExpression>();

        whereExpressionSet.add(createWhereExpressionsWithMixKeyword(keyword));

        if (!queryParams.isEmpty()) {
            whereExpressionSet.add(createWhereExpression(queryParams));
        }

        var whereExpressions = whereExpressionSet.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);


        var orderSpecifiers = createOrderSpecifiers(pageable.getSort());


        return fetchItemWithWhereExpression(whereExpressions, orderSpecifiers, pageable);
    }

    /**
     * 포스트를 전체 조회합니다.
     *
     * @param pageable
     * @return
     */
    //fetch 때문에 별도 메소드를 생성하여 사용함.
    @Override
    public Page<Post> findAllPost(Pageable pageable) {
        var orderSpecifiers = createOrderSpecifiers(pageable.getSort());
        return fetchItem(orderSpecifiers, pageable);
    }

    /**
     * 쿼리 옵션을 이용하여 포스트를 조회합니다.
     *
     * @param queryParams 쿼리 옵션
     * @param pageable
     * @return
     */
    @Override
    public Page<Post> findAllPostWithQueryParam(MultiValueMap<PostQueryKeys, String> queryParams, Pageable pageable) {
        var whereExpression = createWhereExpression(queryParams);
        var orderSpecifiers = createOrderSpecifiers(pageable.getSort());
        return fetchItemWithWhereExpression(whereExpression, orderSpecifiers, pageable);
    }

    /**
     * 주어진 whereExpression에 해당하는 게시글의 개수를 반환합니다.
     *
     * @param whereExpression
     * @return
     */
    private Long getCountWithWhereExpression(Predicate whereExpression) {

        return jpaQueryFactory.select(qPost.count()).from(qPost).where(whereExpression).fetchOne();
    }

    /**
     * 게시글의 전체 개수를 반환합니다.
     *
     * @return
     */
    private Long getCount() {

        return jpaQueryFactory.select(qPost.count()).from(qPost).fetchOne();
    }


    /**
     * 주어진 옵션들에 해당하는 게시글을 불러옴.
     *
     * @param whereExpression (where 절)
     * @param orderSpecifiers (order by 절)
     * @param pageable
     * @return
     */
    private Page<Post> fetchItemWithWhereExpression(Predicate whereExpression, OrderSpecifier<?>[] orderSpecifiers,
                                                    Pageable pageable) {
        var postIds = jpaQueryFactory
                .select(qPost.id)
                .from(qPost)
                .where(whereExpression)
                .orderBy(orderSpecifiers)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        log.debug("postIds: {}", postIds);

        var fetchedPosts = jpaQueryFactory.selectFrom(qPost)
                .leftJoin(qPost.postType, qPostType)
                .fetchJoin()
                .leftJoin(qPost.postTags, qPostTag)
                .fetchJoin()
                .leftJoin(qPost.series, qSeries)
                .fetchJoin()
                .leftJoin(qPost.content, qContent)
                .fetchJoin()
                .where(qPost.id.in(postIds))
                .orderBy(orderSpecifiers)
                .fetch();

        return PageableExecutionUtils.getPage(fetchedPosts, pageable, () -> getCountWithWhereExpression(whereExpression));

    }

    /**
     * 주어진 옵션들에 해당하는 게시글을 불러옴.
     *
     * @param orderSpecifiers (order by 절)
     * @param pageable
     * @return
     */
    private Page<Post> fetchItem(OrderSpecifier<?>[] orderSpecifiers, Pageable pageable) {

        var postIds = jpaQueryFactory
                .select(qPost.id)
                .from(qPost)
                .orderBy(orderSpecifiers)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        log.debug("postIds: {}", postIds);

        var fetchedPosts = jpaQueryFactory.selectFrom(qPost)
                .leftJoin(qPost.postType, qPostType)
                .fetchJoin()
                .leftJoin(qPost.postTags, qPostTag)
                .fetchJoin()
                .leftJoin(qPost.series, qSeries)
                .fetchJoin()
                .orderBy(orderSpecifiers)
                .where(qPost.id.in(postIds))
                .fetch();

        return PageableExecutionUtils.getPage(fetchedPosts, pageable, this::getCount);
    }

    /**
     * 주어진 쿼리 옵션에 해당하는 where 절을 생성합니다.
     *
     * @param queryParams 쿼리 옵션
     * @return
     */
    private BooleanExpression createWhereExpression(MultiValueMap<PostQueryKeys, String> queryParams) {

        if (queryParams.isEmpty())
            return null;

        var whereExpressions = new HashSet<BooleanExpression>();

        for (var entry : queryParams.entrySet()) {
            var key = entry.getKey();

            if (key == PostQueryKeys.IS_ONLY_OPEN_POST) {
                var value = entry.getValue().get(0);
                var isOnlyOpenPost = Boolean.parseBoolean(value);
                whereExpressions.add(qPost.isEnabled.eq(isOnlyOpenPost));
                continue;
            }

            var queryOptions = new HashSet<BooleanExpression>();

            for (var value : entry.getValue()) {
                BooleanExpression expression;
                switch (key) {
                    case POST_TYPE -> expression = typeParser.parseLong(value)
                            .map(qPost.postType.id::eq)
                            .orElseThrow(() -> new IllegalArgumentException("Given Not Valid " + key.getQueryString() + " Value."));
                    case SERIES -> expression = typeParser.parseLong(value)
                            .map(qPost.series.id::eq)
                            .orElseThrow(() -> new IllegalArgumentException("Given Not Valid " + key.getQueryString() + " Value."));
                    case TAG -> expression = typeParser.parseLong(value)
                            .map(id -> qPost.postTags.any().tag.id.eq(id))
                            .orElseThrow(() -> new IllegalArgumentException("Given Not Valid " + key.getQueryString() + " Value."));
                    default -> {
                        continue;
                    }
                }
                ;

                queryOptions.add(expression);
            }

            whereExpressions.add(queryOptions
                    .stream()
                    .reduce(BooleanExpression::or)
                    .orElse(null));
        }

        return whereExpressions.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);
    }

    /**
     * 주어진 Sort 옵션에 해당하는 OrderSpecifier 배열을 생성합니다.
     *
     * @param sort
     * @return
     */
    private OrderSpecifier<?>[] createOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new LinkedList<>();
        var userSortOptions = sort.get().toList();

        for (var userSortOption : userSortOptions) {
            var sortKey = SortKeyUtil.getKey(PostSortKeys.class, userSortOption.getProperty());

            switch (sortKey) {
                case ID ->
                        orderSpecifiers.add(new OrderSpecifier<>((userSortOption.isAscending()) ? Order.ASC : Order.DESC, qPost.id));
                case TITLE ->
                        orderSpecifiers.add(new OrderSpecifier<>((userSortOption.isAscending()) ? Order.ASC : Order.DESC, qPost.title));
                case CONTENT ->
                        orderSpecifiers.add(new OrderSpecifier<>((userSortOption.isAscending()) ? Order.ASC : Order.DESC, qPost.content.plainText));
                case IS_OPEN ->
                        orderSpecifiers.add(new OrderSpecifier<>((userSortOption.isAscending()) ? Order.ASC : Order.DESC, qPost.isEnabled));
                case SERIES ->
                        orderSpecifiers.add(new OrderSpecifier<>((userSortOption.isAscending()) ? Order.ASC : Order.DESC, qPost.series.id));
                case LAST_MODIFIED_DATE ->
                        orderSpecifiers.add(new OrderSpecifier<>((userSortOption.isAscending()) ? Order.ASC : Order.DESC, qPost.lastModifiedDate));
                case CREATED_DATE ->
                        orderSpecifiers.add(new OrderSpecifier<>((userSortOption.isAscending()) ? Order.ASC : Order.DESC, qPost.createdDate));
            }
        }

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

    /**
     * 제목에 키워드가 포함되는 게시글을 찾기 위한 where 절을 생성합니다.
     *
     * @param keyword
     * @return
     */
    private BooleanExpression createWhereExpressionsWithTitleKeyword(String keyword) {

        return Expressions.numberTemplate(Double.class, "function('match', {0}, {1})",
                        qPost.title, keyword)
                .gt(0.0);
    }

    /**
     * 본문에 키워드가 포함되는 게시글을 찾기 위한 where 절을 생성합니다.
     *
     * @param keyword
     * @return
     */
    private BooleanExpression createWhereExpressionsWithContentKeyword(String keyword) {

        return Expressions.numberTemplate(Double.class, "function('match', {0}, {1})",
                        qPost.content.plainText, keyword)
                .gt(0.0);


    }

    /**
     * 제목 혹은 본문에 키워드가 포함되는 게시글을 찾기 위한 where 절을 생성합니다.
     *
     * @param keyword
     * @return
     */
    private BooleanExpression createWhereExpressionsWithMixKeyword(String keyword) {
        return createWhereExpressionsWithTitleKeyword(keyword)
                .or(createWhereExpressionsWithContentKeyword(keyword));

    }

}
