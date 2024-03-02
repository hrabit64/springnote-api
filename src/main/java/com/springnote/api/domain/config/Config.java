package com.springnote.api.domain.config;


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
 * 설정을 나타내는 엔티티 클래스입니다.
 * 
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */ 
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "CONFIG")
public class Config {

    @Id
    @Column(name = "CONFIG_PK", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CONFIG_KEY", nullable = false, updatable = false ,unique = true, length = 100)
    private String key;
    
    @Column(name = "CONFIG_VALUE", nullable = false, length = 300)
    private String value;
}
