package com.springnote.api.utils.context;

import com.springnote.api.dto.user.common.UserResponseCommonDto;
import com.springnote.api.dto.user.common.UserSimpleResponseCommonDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * 각 Request에 대하여 해당 Request를 요청한 유저의 정보를 담고 있는 클래스
 *
 * @author 황준서(' hzser123 @ gmail.com ')
 * @since 1.0.0
 */
@Setter
@Getter
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserContext {

    // 해당 유저가 블로그 관리자인지 여부
    private boolean isAdmin = false;
    private String uid;
    private String displayName;
    private String profileImg;

    // 실제 로직에서 사용하는 사용자 정보가 아닌, 단순 firebase 정보임
    private String fbUid;
    private String fbEmail;


    public boolean isAuthed() {
        return uid != null;
    }

//    public void init(boolean isAdmin, String uid, String displayName) {
//        this.isAdmin = isAdmin;
//        this.uid = uid;
//        this.displayName = displayName;
//    }

    public void init(UserResponseCommonDto userResponseCommonDto) {
        this.uid = userResponseCommonDto.getId();
        this.displayName = userResponseCommonDto.getName();
        this.isAdmin = userResponseCommonDto.isAdmin();
        this.profileImg = userResponseCommonDto.getProfileImg();

    }

    public void init() {
        this.uid = null;
        this.displayName = null;
        this.isAdmin = false;
        this.profileImg = null;
    }

    public UserSimpleResponseCommonDto toDto() {
        return UserSimpleResponseCommonDto.builder()
                .uid(uid)
                .displayName(displayName)
                .profileImg(profileImg)
                .isAdmin(isAdmin)
                .build();
    }

    public void setFbInfo(String fbUid, String fbEmail) {
        this.fbUid = fbUid;
        this.fbEmail = fbEmail;
    }
}