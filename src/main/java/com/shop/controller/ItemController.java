package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";

    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult
                          bindingResult, Model model, @RequestParam("itemImgFile")List<MultipartFile>
                          itemImgFileList){
        
        //상품 등록 시 필수 값이 없다면 다시 상품 등록 페이지로 전환
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        //상품 등록 시 첫 번째 이미지가 없다면 에러 메세지와 함께 상품 등록 페이지로 전환
        //상품의 첫 번째 이미지는 메인페이지에서 보여줄 상품 이미지로 사용하기 위해서 필수 값으로 지정
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try {
            //상품 저장 로직을 호출 매개변수로 상품정보와 상품이미지 정보 담고 있는 itemImgFileList를 넘겨줌
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage","상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        
        //상품이 정상적으로 등록되었다면 메인페이지로 이동
        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){

        try{
            //조회한 상품 데이터를 모델에 담아서 뷰로 전달
            ItemFormDto itemFormDto = itemService.getItemDil(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
            
            //상품 엔티티가 존재하지 않을 경우 에러메세지를 담아서 상품 등록 페이지로 이동
        }catch (EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto,
                             BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile>
    itemImgFileList, Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try{
            itemService.updateItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage","상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }
}
