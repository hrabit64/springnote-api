package com.springnote.api.utils.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;


/**
 * 각 Request에 대하여 해당 Request를 요청한 유저의 정보를 담고 있는 클래스
 * 
 * @author 황준서('hzser123@gmail.com')
 * @since 1.0.0
 */
@Setter
@Getter
@Component
@Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserContext {

    // 해당 유저의 uid (26자리 - firebase uid)
    private String uid = null;

    // 해당 유저의 닉네임
    private String name = null;

    // 해당 유저가 블로그 관리자인지 여부
    private boolean isAdmin = false;

    /**
     * 유저 정보가 Context 상에 존재하는지 확인하는 함수
     * 
     * @return 유저 정보가 context에 존재하면 true 리턴
     */
    public boolean isExist(){
        return this.uid != null;
    }
}