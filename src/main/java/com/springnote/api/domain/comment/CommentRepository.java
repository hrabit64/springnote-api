package com.springnote.api.domain.comment;

import com.springnote.api.domain.post.Post;
import com.springnote.api.dto.comment.service.CommentReplyCountResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 게시글에 대한 일반 댓글 목록을 조회합니다.
     *
     * @param post
     * @param pageable
     * @return
     */
    @EntityGraph(value = "Comment.user")
    Page<Comment> findAllByPostAndParentIsNull(Post post, Pageable pageable);

    /**
     * 대댓글을 조회합니다.
     *
     * @param parent   부모 댓글
     * @param pageable
     * @return
     */
//    @EntityGraph(value = "Comment.user")
    Page<Comment> findAllByParent(Comment parent, Pageable pageable);

    /**
     * 일반 댓글의 대댓글 수를 조회합니다.
     *
     * @param parentIds
     * @return
     */
    @Query("SELECT new com.springnote.api.dto.comment.service.CommentReplyCountResponseDto(c.parent.id, COUNT(c)) " +
            "FROM COMMENT c " +
            "WHERE c.parent.id IN :parentIds " +
            "GROUP BY c.parent.id")
    List<CommentReplyCountResponseDto> countReplyByParentIds(@Param("parentIds") Set<Long> parentIds);

}
