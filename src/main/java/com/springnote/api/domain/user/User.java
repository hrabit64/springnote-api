package com.springnote.api.domain.user;

import jakarta.persistence.*;
import lombok.*;

@AttributeOverrides({
        @AttributeOverride(name = "createdDate", column = @Column(name = "TAG_CREATED_AT", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "TAG_LAST_UPDATED_AT"))
})
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "USER")
public class User {

    @Id
    @Column(name = "USER_PK", nullable = false, length = 28)
    private String id;

    @Column(name = "USER_NM", nullable = false, length = 256)
    private String name;

    @Column(name = "USER_EMAIL", nullable = false, length = 256)
    private String email;

    @Column(name = "USER_PROVIDER", nullable = false, length = 256)
    private String provider;

    @Column(name = "USER_PROFILE_IMG", nullable = false, length = 256)
    private String profileImg;

    @Column(name = "USER_IS_ADMIN", nullable = false)
    private boolean isAdmin;

    @Column(name = "USER_IS_ENABLED", nullable = false)
    private boolean isEnabled;
}
