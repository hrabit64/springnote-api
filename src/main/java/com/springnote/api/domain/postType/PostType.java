package com.springnote.api.domain.postType;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * 포스트 타입을 나타내는 엔티티 클래스입니다.
 *
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@EqualsAndHashCode
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "POST_TYPE")
public class PostType {

    @Id
    @Column(name = "POST_TYPE_PK", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "POST_TYPE_NM", nullable = false, updatable = false, unique = true, length = 25)
    private String name;

    @Column(name = "POST_TYPE_POLICY_CAN_ADD_COMMENT", nullable = false, updatable = false, unique = true, length = 25)
    private boolean isCanAddComment;

    @Column(name = "POST_TYPE_POLICY_NEED_SERIES", nullable = false, updatable = false, unique = true, length = 25)
    private boolean isNeedSeries;
}
