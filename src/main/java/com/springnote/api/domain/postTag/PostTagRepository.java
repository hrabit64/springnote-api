package com.springnote.api.domain.postTag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    // @Modifying
    // @Query("DELETE FROM PostTag pt WHERE pt.id in :ids")
    // void deleteByIds(@Param("ids") List<Long> ids);
    
}
