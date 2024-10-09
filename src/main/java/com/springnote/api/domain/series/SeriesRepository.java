package com.springnote.api.domain.series;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    
    Page<Series> findAllByNameContaining(String name, Pageable pageable);
    Boolean existsByName(String name);
    
}
