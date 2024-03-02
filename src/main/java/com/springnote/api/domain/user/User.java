package com.springnote.api.domain.user;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원을 나타내는 엔티티 클래스입니다.
 * 
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */ 
@AttributeOverrides({
        @AttributeOverride(name = "createdDate", column = @Column(name = "POST_CREATED_AT", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "POST_LAST_UPDATED_AT"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "USER")
public class User {

    @Id
    @Column(name = "USER_PK", nullable = false, length = 32)
    private String id;

    @Column(name = "USER_NM", nullable = false, length = 36)
    private String name;

    @Column(name = "USER_EMAIL", nullable = false, length = 320)
    private String email;

    @Column(name = "USER_IS_ADMIN", nullable = false)
    private boolean isAdmin;

    @Column(name = "USER_RCV_NWSLT", nullable = false)
    private boolean isReceiveNewsletter;

    @Column(name = "USER_IS_ACTIVE", nullable = false)
    private boolean isActive;


    /**
     * 주어진 정보로 회원 정보를 수정합니다.
     * name, email, isReceiveNewsletter, isActive 를 수정할 수 있습니다.
     * 
     * @param user 수정할 회원 정보
     * 
     * @auther 황준서 (hzser123@gmail.com) 
     * @since 1.0.0
     */
    public void update(User user){
        this.name = user.getName();
        this.email = user.getEmail();
        this.isReceiveNewsletter = user.isReceiveNewsletter();
        this.isActive = user.isActive();
    }
    
}
