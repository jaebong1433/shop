package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name="cart_item")
public class CartItem extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name="cart_item_id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id")
    private Cart cart;//하나의 장바구니에는 여러 개의 상품을 담을 수 있으므로 @ManyToOne 어노테이션을 이용하여 다대일 관계로 매핑
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;//하나의 상품은 여러 장바구니의 장바구니 상품으로 담길 수 있으므로 마찬가지로 @ManyToOne 어노테이션을 이용하여 다대일 관계로 매핑
    
    private int count;//같은 상품을 장바구니에 몇 개 담을지 저장

    public static CartItem createCartItem(Cart cart, Item item, int count){
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    //장바구니에 기존에 담겨 있는 상품인데, 해당 상품을 추가로 장바구니에 담을 때 기존 수량에 현재 담을 수량을 더 해줄 때 사용할 메서드
    public void addCount(int count){
        this.count += count;
    }
}
