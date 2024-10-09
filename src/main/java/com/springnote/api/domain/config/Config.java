package com.springnote.api.domain.config;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;


/**
 * 설정을 나타내는 엔티티 클래스입니다.
 *
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@EqualsAndHashCode
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "CONFIG")
public class Config {

    @Id
    @Column(name = "CONFIG_KEY", length = 300)
    private String key;

    @Column(name = "CONFIG_VALUE", length = 300)
    private String value;
}
