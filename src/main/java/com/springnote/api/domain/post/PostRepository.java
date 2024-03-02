package com.springnote.api.domain.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springnote.api.domain.post.custom.PostQRepository;


/**
 * PostRepository
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostQRepository{

    @EntityGraph(attributePaths = {"detail"})
    Optional<Post> findById(Long id);
    
}