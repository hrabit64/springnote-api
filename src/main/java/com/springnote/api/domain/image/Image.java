package com.springnote.api.domain.image;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


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
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
@Entity(name = "IMAGE")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_PK", nullable = false)
    private Long id;

    @Column(name = "IMAGE_NM", nullable = false, length = 255, unique = true)
    private String convertedName;

    @CreatedDate
    @Column(name = "IMAGE_CREATE_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "IMAGE_POST", nullable = true)
    private Long postId;

    @Column(name = "IMAGE_WIDTH", nullable = false)
    private int width;

    @Column(name = "IMAGE_HEIGHT", nullable = false)
    private int height;

    @Column(name = "IMAGE_FORMAT", nullable = false, length = 4)
    private String format;
}
