package com.springnote.api.domain.tmpPost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TmpPostRepository extends JpaRepository<TmpPost, String> {

    @EntityGraph(value = "TmpPost.detail")
    Optional<TmpPost> findPostById(String id);
    
    Page<TmpPost> findAllBy(Pageable pageable);

}
