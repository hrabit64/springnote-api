package com.springnote.api.domain.post;

import com.springnote.api.domain.post.queryDsl.PostQRepository;
import com.springnote.api.domain.series.Series;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * PostRepository
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostQRepository {

    /**
     * 포스트를 조회합니다.
     *
     * @param id 포스트 Id
     * @return
     */
    @EntityGraph(value = "Post.detail")
    Optional<Post> findById(Long id);

    /**
     * 포스트 제목이 존재하는지 확인합니다.
     *
     * @param title 포스트 제목
     * @return
     */
    boolean existsByTitle(String title);

    List<Post> findAllBySeries(Series series);

}