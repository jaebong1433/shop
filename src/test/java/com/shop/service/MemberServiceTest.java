package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional//테스트 실행 후 롤백 처리
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){//회원 정보를 입력한 Member 엔티티를 만드는 메소드
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("bong@email.com");
        memberFormDto.setName("이재봉");
        memberFormDto.setAddress("경남 양산시 동면");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 테스트")
    //저장하려고 요청했던 값과 실제 저장된 데이터를 비교,첫 번째 파라미터 기대 값, 두번째 파라미터 실제로 저장된 값
    public void saveMemberTest(){
        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getName(), savedMember.getName());
        assertEquals(member.getAddress(), savedMember.getAddress());
        assertEquals(member.getPassword(), savedMember.getPassword());
        assertEquals(member.getRole(), savedMember.getRole());

    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateMemberTest(){
        Member member1 = createMember();
        Member member2 = createMember();
        memberService.saveMember(member1);

        Throwable e = assertThrows(IllegalStateException.class, () -> {//예외처리 테스트 첫번째 파라미터에는 발생할 예외타입을 넣어줌
           memberService.saveMember(member2);
        });

        assertEquals("이미 가입된 회원입니다.", e.getMessage());//발생한 예외 메세지가 예상결과와 맞는지 검증
    }
}