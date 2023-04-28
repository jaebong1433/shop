package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemImgService itemImgService;

    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if(i == 0)
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    //상품 데이터를 읽어오는 트랜잭션을 읽기 전용으로 설정함
    //이럴 경우 JPA가 더티체킹(변경감지)을 수행하지 않아서 성능을 향상 시킬 수 있음
    public ItemFormDto getItemDil(Long itemId){

        List<ItemImg> itemImgList =
                //해당 상품의 이미지를 조회 등록순으로 가지고 오기 위해서 상품 이미지 아이디 오름차순으로 가지고 오겠음
                itemImgRepository.findByItemIdOrderByIdAsc(itemId);
            List<ItemImgDto> itemImgDtoList = new ArrayList<>();
            for(ItemImg itemImg : itemImgList){//조회한 ItemImg 엔티티를 ItemImgDto 객체로 만들어서 리스트에 추가
                ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
                itemImgDtoList.add(itemImgDto);
            }
            
            //상품의 아이디를 통해 상품 엔티티를 조회 존재하지 않을 때는 EntityNotFoundException을 발생
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(EntityNotFoundException::new);
            ItemFormDto itemFormDto = ItemFormDto.of(item);
            itemFormDto.setItemImgDtoList(itemImgDtoList);
            return itemFormDto;
        }

    public Long updateItem(ItemFormDto itemFormDto,
                           List<MultipartFile> itemImgFileList) throws Exception{
        //상품 수정
        //상품 등록 화면으로부터 전달 받은 상품 아이디를 이용하여 상품 엔티티를 조회
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);

        //상품 등록 화면으로부터 전달 받은 itemFormDto를 통해 상품 엔티티를 업데이트
        item.updateItem(itemFormDto);

        //상품 이미지 아이디 리스트를 조회
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        //이미지 등록
        for(int i=0; i<itemImgFileList.size(); i++){

            //상품 이미지를 업데이트하기 위해서 updateItemImg()메서드에 상품 이미지 아이디와, 상품 이미지 파일 정보를 파라미터로 전달
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i));
        }

        return item.getId();
    }
}

