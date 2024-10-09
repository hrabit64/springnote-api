package com.springnote.api.domain.tag;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>{
    List<Tag> findTagsByIdIn(List<Long> tagIds);
    Page<Tag> findAllByNameContaining(String name, Pageable pageable);
    boolean existsByName(String name);
}
