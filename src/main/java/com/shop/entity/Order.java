package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")//정렬할 때 사용하는 "order"키워드가 있기 때문에 Order엔티티에 매핑되는 테이블로 "orders"를 지정합니다.
@Getter @Setter
public class Order {
    
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;//한 명의 회원은 여러 번 주문을 할 수 있으므로 주문 엔티티 기준에서 다대일 단방향 매핑을 합니다.
    
    private LocalDateTime orderDate;//주문일
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;//주문상태

    //주문 상품 엔티티와 일대다 매핑
    //부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이하는 CascadeTypeAll 옵션을 설정
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    //하나의 주문이 여러 개의 주문 상품을 갖으므로 List 자료형을 사용해서 매핑
    private List<OrderItem> orderItems = new ArrayList<>();
    
    private LocalDateTime regTime;
    
    private LocalDateTime updateTime;

}
