package com.springnote.api.domain.postEditorContent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEditorContentRepository extends JpaRepository<PostEditorContent, Long> {
    
}
