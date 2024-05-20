package com.koreait.sssandupload.app.member.controller;

import com.koreait.sssandupload.app.member.entity.Member;
import com.koreait.sssandupload.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/join")
    public String showJoin() {
        return "member/join";
    }

    @PostMapping("/join")
    @ResponseBody
    public String join(String username, String password, String email, MultipartFile profileImg) {
        Member oldMember = memberService.getMemberByUsername(username);

        if (oldMember != null) {
            return "이미 있음";
        }

        Member member = memberService.join(username, "{noop}" + password, email, profileImg);

        return "success";
    }
}
