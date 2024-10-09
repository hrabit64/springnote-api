package com.springnote.api.domain.tmpPostTag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TmpPostTagRepository extends JpaRepository<TmpPostTag, Long> {
}
