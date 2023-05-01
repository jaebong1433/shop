package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private  final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                                              BindingResult bindingResult, Principal principal){

        //장바구니에 담을 상품 정보를 받는 cartItemDto 객체에 데이터 바인딩 시 에러가 있는지 검사
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        //현재 로그인한 회원의 이메일 정보를 변수에 저장
        String email = principal.getName();
        Long cartItemId;

        try{//화면으로부터 넘어온 장바구니에 담을 상품 정보와 현재 로그인한 회원의 이메일 정보를 이용하여 장바구니에 상품을 담는 로직을 호출
            cartItemId = cartService.addCart(cartItemDto, email);

        }catch (Exception e){
         return new ResponseEntity<String>(e.getMessage(),
                 HttpStatus.BAD_REQUEST);
        }
        //결과값으로 생성된 장바구니 상품 아이디와 요청이 성공하였다는 HTTP 응답 상태 코드를 반환
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }
    
    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        List<CartDetailDto> cartDetailList =
                //현재 로그인한 사용자의 이메일 정보를 이용하여 장바구니에 담겨있는 상품 정보를 조회
                cartService.getCartList(principal.getName());
        //조회한 장바구니 상품 정보를 뷰로 전달
        model.addAttribute("cartItems", cartDetailList);
        return "cart/cartList";
    }
    
    //장바구니 상품의 수량을 업데이트하는 요청을 처리하는 로직
    //HTTP메서드에서 PATCH는 요청된 자원의 일부를 업데이트할 때 PATCH를 사용함
    //장바구니 상품의 수량만 업데이트하기 때문에 @PatchMapping을 사용
    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem
    (@PathVariable("cartItemId") Long cartItemId, int count, Principal principal){
        
        if(count <= 0){//장바구니에 담겨있는 상품의 개수를 0개 이하로 업데이트 요청을 할 때 에러 메시지를 담아서 반환
            
            return new ResponseEntity<String>
                    ("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        
            //수정 권한을 체크함    
        }else if(!cartService.validateCartItem(cartItemId, principal.getName())){
            
            return new ResponseEntity<String>("수정 권한이 없습니다.",HttpStatus.FORBIDDEN);
        }
        
        //장바구니 상품의 개수를 업데이트
        cartService.updateCartItemCount(cartItemId, count);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }
    
    //장바구니 삭제 요청을 처리
    //HTTP 메서드에서 DELETE의 경우 요청된 자원을 삭제할 때 사용
    //장바구니 상품을 삭제하기 때문에 @DeleteMapping을 사용
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem
    (@PathVariable("cartItemId") Long cartItemId, Principal principal){
        
        //수정 권한을 체크
        if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        
        //해당장바구니 상품을 삭제함
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    //장바구니 상품의 수량을 업데이트하는 요청을 처리
    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal){

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        //주문할 상품을 선택하지 않았는지 체크
        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        //주문 권한을 체크
        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }
        
        //주문 로직 호출 결과 생성된 주문 번호를 반환
        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
        
        //생성된 주문 번호와 요청이 성공했다는 HTTP 응답 상태 코드를 반환
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
