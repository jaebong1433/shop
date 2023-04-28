package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "cart")
@Getter @Setter
@ToString
public class Cart {
    @Id
    @Column(name="cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)//회원 엔티티 일대일로 매핑
    @JoinColumn(name="member_id")//매핑할 외래키를 지정 name속성에는 매핑할 외래캐의 이름을 설정
    private Member member;

}
