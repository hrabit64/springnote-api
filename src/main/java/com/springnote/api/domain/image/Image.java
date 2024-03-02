package com.springnote.api.domain.image;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * 이미지를 나타내는 엔티티 클래스입니다.
 * 
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */ 
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "IMAGE")
public class Image {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_PK", nullable = false)
    private Long id;
    
    @Column(name = "IMAGE_ORIGIN_NM", nullable = false, length = 255)
    private String originalName;

    @Column(name = "IMAGE_CONVERT_NM", nullable = false, length = 255, unique = true)
    private String convertedName;

    @CreatedDate
    @Column(name = "IMAGE_CREATE_AT", nullable = false,updatable = false)
    private LocalDateTime createdAt;

}
