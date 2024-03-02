package com.springnote.api.domain.post.custom;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.MultiValueMap;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springnote.api.domain.comment.QComment;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.post.QPost;
import com.springnote.api.domain.postTag.QPostTag;
import com.springnote.api.domain.postType.QPostType;
import com.springnote.api.domain.series.QSeries;
import com.springnote.api.utils.TypeParser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostQRepositoryImpl implements PostQRepository {

    private final TypeParser typeParser;

    private final JPAQueryFactory jpaQueryFactory;
    private final QPost qPost = QPost.post;
    private final QPostType qPostType = QPostType.postType;
    private final QPostTag qPostTag = QPostTag.postTag;
    private final QSeries qSeries = QSeries.series;
    private final QComment qComment = QComment.comment;

    /**
     * 주어진 keyword가 제목에 포함되는 게시글을 불러옴.
     * 
     * @param keyword 검색 키워드
     * @param isOpen  공개 게시글만 조회할지 여부
     * 
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    @Override
    public Page<Post> matchByTitle(String keyword, MultiValueMap<String, String> searchOptions, Pageable pageable) {

        Predicate whereExpressions;
        if (!searchOptions.isEmpty()) {
            var whereExpressionSet = new HashSet<BooleanExpression>();

            whereExpressionSet.add(createSearchWithKeywordQuery(keyword, true));
            whereExpressionSet.add(createSearchOptionQuery(searchOptions));

            whereExpressions = whereExpressionSet.stream()
                    .reduce(BooleanExpression::and)
                    .orElse(null);
        }

        else {
            whereExpressions = createSearchWithKeywordQuery(keyword, true);
        }

        var orderSpecifiers = createOrderSpecifiers(pageable.getSort());

        var fetchedPost = createPostPageQuery(whereExpressions, orderSpecifiers, pageable).fetch();

        return PageableExecutionUtils.getPage(fetchedPost, pageable, () -> getPostCnt(whereExpressions));
    }
    
    /**
     * 주어진 keyword가 본문에 포함되는 게시글을 불러옴.
     * 
     * @param keyword        검색 키워드
     * @param isOnlyOpenPost 공개 게시글만 조회할지 여부
     * 
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    @Override
    public Page<Post> matchByContent(String keyword, MultiValueMap<String, String> searchOptions, Pageable pageable) {

        Predicate whereExpressions;
        if (!searchOptions.isEmpty()) {
            var whereExpressionSet = new HashSet<BooleanExpression>();

            whereExpressionSet.add(createSearchWithKeywordQuery(keyword, false));
            whereExpressionSet.add(createSearchOptionQuery(searchOptions));

            whereExpressions = whereExpressionSet.stream()
                    .reduce(BooleanExpression::and)
                    .orElse(null);
        }

        else {
            whereExpressions = createSearchWithKeywordQuery(keyword, false);
        }

        var orderSpecifiers = createOrderSpecifiers(pageable.getSort());

        var fetchedPost = createPostPageQuery(whereExpressions, orderSpecifiers, pageable).fetch();

        return PageableExecutionUtils.getPage(fetchedPost, pageable, () -> getPostCnt(whereExpressions));
    }

    /**
     * 주어진 keyword가 본문 혹은 제목에 포함되는 게시글을 불러옴.
     * 
     * @param keyword        검색 키워드
     * @param isOnlyOpenPost 공개 게시글만 조회할지 여부
     * 
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    @Override
    public Page<Post> matchByTitleOrContent(String keyword, MultiValueMap<String, String> searchOptions,
            Pageable pageable) {

        Predicate whereExpressions;
        if (!searchOptions.isEmpty()) {
            var whereExpressionSet = new HashSet<BooleanExpression>();

            whereExpressionSet
                    .add(createSearchWithKeywordQuery(keyword, true).or(createSearchWithKeywordQuery(keyword, false)));
            whereExpressionSet.add(createSearchOptionQuery(searchOptions));

            whereExpressions = whereExpressionSet.stream()
                    .reduce(BooleanExpression::and)
                    .orElse(null);
        }

        else {
            whereExpressions = createSearchWithKeywordQuery(keyword, true)
                    .or(createSearchWithKeywordQuery(keyword, false));
        }

        var orderSpecifiers = createOrderSpecifiers(pageable.getSort());

        var fetchedPost = createPostPageQuery(whereExpressions, orderSpecifiers, pageable).fetch();

        return PageableExecutionUtils.getPage(fetchedPost, pageable, () -> getPostCnt(whereExpressions));
    }

    /**
     * title 혹은 content에 대해 fully text search 쿼리를 생성함.
     * 
     * @param isTitle title 검색 여부, false 시 content 검색
     * @param keyword 검색 키워드
     * 
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    private BooleanExpression createSearchWithKeywordQuery(String keyword, boolean isTitle) {

        if (isTitle)
            return Expressions.numberTemplate(Double.class, "function('match', {0}, {1})",
                    qPost.title, keyword)
                    .gt(0.0);
        else
            return Expressions.numberTemplate(Double.class, "function('match', {0}, {1})",
                    qPost.content, keyword)
                    .gt(0.0);
    }

    /**
     * 검색 결과의 총 개수를 반환함.
     * 
     * @param queryOption 검색 쿼리
     * @return 검색 결과의 총 개수
     * 
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    private Long getPostCnt(Predicate queryOption) {

        return jpaQueryFactory.select(qPost.count()).from(qPost).where(queryOption).fetchOne();
    }

    /**
     * 주어진 쿼리문을 통해 pagenation으로 게시글을 불러오는 쿼리를 생성함.
     * 
     * @param queryOption 검색 쿼리
     * @return pagenation 쿼리
     * 
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    private JPAQuery<Post> createPostPageQuery(Predicate queryOptions, OrderSpecifier<?>[] orderSpecifiers,
            Pageable pageable) {

        var query = jpaQueryFactory.selectFrom(qPost)
                .leftJoin(qPost.postType, qPostType)
                .fetchJoin()
                .leftJoin(qPost.postTags, qPostTag)
                .fetchJoin()
                .leftJoin(qPost.series, qSeries)
                .fetchJoin()
                .where(queryOptions)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return query;
    }

    /**
     * 주어진 검색옵션을 검색 쿼리로 변환함.
     * 
     * 
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    private BooleanExpression createSearchOptionQuery(MultiValueMap<String, String> searchOptions) {

        if (searchOptions.isEmpty())
            return null;

        var whereOptions = new HashSet<BooleanExpression>();

        // 게시글 유형
        if (searchOptions.containsKey("postType")) {

            var postTypeOptions = new HashSet<BooleanExpression>();

            for (var value : searchOptions.get("postType")) {
                var targetTypeId = typeParser.parseLong(value);
                if (targetTypeId.isEmpty())
                    continue;

                postTypeOptions.add(qPost.postType.id.eq(targetTypeId.get()));
            }

            whereOptions.add(postTypeOptions
                    .stream()
                    .reduce(BooleanExpression::or)
                    .orElse(null));

        }

        // 시리즈 id
        if (searchOptions.containsKey("series")) {

            var postTypeOptions = new HashSet<BooleanExpression>();

            for (var value : searchOptions.get("series")) {
                var targetSeriesId = typeParser.parseLong(value);
                if (targetSeriesId.isEmpty())
                    continue;

                postTypeOptions.add(qPost.series.id.eq(targetSeriesId.get()));
            }

            whereOptions.add(postTypeOptions
                    .stream()
                    .reduce(BooleanExpression::or)
                    .orElse(null));

        }

        // 태그 id
        if (searchOptions.containsKey("tag")) {

            var postTypeOptions = new HashSet<BooleanExpression>();

            for (var value : searchOptions.get("tag")) {
                var targetTagId = typeParser.parseLong(value);
                if (targetTagId.isEmpty())
                    continue;

                postTypeOptions.add(qPost.postTags.any().tag.id.eq(targetTagId.get()));
            }

            whereOptions.add(postTypeOptions
                    .stream()
                    .reduce(BooleanExpression::or)
                    .orElse(null));

        }

        // 기본적으로 검색 옵션을 미지정하면, 모든 게시글을 검색하게 설정됨.
        // 반드시 Service 단에서 isOnlyOpenPost 옵션이 있는지 없는지 권한 검사를 수행할 것!
        if (searchOptions.containsKey("isOnlyOpenPost")) {

            // isOnlyOpenPost 검색옵션은 맨 처음 value만 인식하고, 나머지 value는 무시함.
            var value = searchOptions.getFirst("isOnlyOpenPost");
            var isOnlyOpenPost = Boolean.parseBoolean(value);
            whereOptions.add(qPost.isOpen.eq(isOnlyOpenPost));
        }

        return whereOptions.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);
    }

    /**
     * 주어진 정렬 옵션을 정렬 쿼리로 변환함.
     * 
     * 
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    private OrderSpecifier<?>[] createOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new LinkedList<>();

        sort.get().forEach(
                sortKey -> {
                    var key = sortKey.getProperty();
                    switch (key) {

                        case "id" -> orderSpecifiers.add(
                                new OrderSpecifier<>((sortKey.isAscending()) ? Order.ASC : Order.DESC, qPost.id));

                        case "createdDate" -> orderSpecifiers.add(
                                new OrderSpecifier<>((sortKey.isAscending()) ? Order.ASC : Order.DESC,
                                        qPost.createdDate));

                        case "lastModifiedDate" -> orderSpecifiers.add(
                                new OrderSpecifier<>((sortKey.isAscending()) ? Order.ASC : Order.DESC,
                                        qPost.lastModifiedDate));

                        case "views" -> orderSpecifiers.add(
                                new OrderSpecifier<>((sortKey.isAscending()) ? Order.ASC : Order.DESC, qPost.viewCnt));

                        case "likes" -> orderSpecifiers.add(
                                new OrderSpecifier<>((sortKey.isAscending()) ? Order.ASC : Order.DESC, qPost.likeCnt));

                        case "title" -> orderSpecifiers.add(
                                new OrderSpecifier<>((sortKey.isAscending()) ? Order.ASC : Order.DESC, qPost.title));

                    }
                });

        return orderSpecifiers.stream().toArray(OrderSpecifier[]::new);
    }

    /**
     * 게시글의 댓글 수를 가져오는 메서드
     *
     * @param postId 게시글 식별자
     * @return 댓글 수
     * 
     * @auther 황준서 (hzser123@gmail.com)
     * @since 1.0.0
     */
    private Long getCommentCount(Long postId) {
        return jpaQueryFactory.select(qComment.count())
                .from(qComment)
                .where(qComment.post.id.eq(postId))
                .fetchOne();
    }
}
