package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;//상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm;//상품명

    @Column(name = "price", nullable = false)
    private int price;//가격

    @Column(nullable = false)
    private int stockNumber;//재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;//상품 상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;//상품 판매상태

    private LocalDateTime regTime;//등록 시간

    private LocalDateTime updateTime;//수정 시간

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int stockNumber){
        //상품의 재고 수량에서 주문 후 남은 재고 수량을 구함
        int restStock = this.stockNumber - stockNumber;

        //상품의 재고가 주문 수량보다 작을 경우 재고 부족 예외를 발생
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock; //주문 후 남은 재고 수량을 현재 재고값으로 할당합니다.

        }

    //상품의 재고를 증가시키는 메서드
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }
}

