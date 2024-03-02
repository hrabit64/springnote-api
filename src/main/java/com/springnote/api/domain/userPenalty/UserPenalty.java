package com.springnote.api.domain.userPenalty;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.springnote.api.domain.user.User;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원에게 부여된 경고를 나타내는 엔티티 클래스입니다.
 * 
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */ 
@NamedEntityGraphs({
        @NamedEntityGraph(name = "UserPenalty", attributeNodes = @NamedAttributeNode("user")),
})
@AttributeOverrides({
        @AttributeOverride(name = "createdDate", column = @Column(name = "POST_CREATED_AT", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "POST_LAST_UPDATED_AT"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "USER_PENALTY")
public class UserPenalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_PENALTY_PK", nullable = false)
    private Long id;

    @Column(name = "USER_PENALTY_REASON", nullable = false, length = 500)
    private String reason;

    @Column(name = "USER_PENALTY_CNT", nullable = false)
    private int count;

    @CreatedDate
    @Column(name = "USER_PENALTY_CREATED_AT", nullable = false,updatable = false)
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "USER_PK", nullable = false)
    private User user;
    
}
