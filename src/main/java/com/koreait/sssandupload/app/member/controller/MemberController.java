package com.koreait.sssandupload.app.member.controller;

import com.koreait.sssandupload.app.member.entity.Member;
import com.koreait.sssandupload.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

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
    public String join(String username, String password, String email, MultipartFile profileImg, HttpSession session) {
        Member oldMember = memberService.getMemberByUsername(username);

        if (oldMember != null) {
            return "redirect:/?errorMsg=AlreadyExists";
        }

        Member member = memberService.join(username, "{noop}" + password, email, profileImg);

        session.setAttribute("loginedMemberId", member.getId());

        return "redirect:/member/profile";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Long loginedMemberId = (Long) session.getAttribute("loginedMemberId");
        boolean isLogined = loginedMemberId != null;

        if(isLogined == false) {
            return "redirect:/?errorMsg=Need to Login";
        }

        Member loginedMember = memberService.getMemberById(loginedMemberId);

        model.addAttribute("loginedMember", loginedMember);

        return "member/profile";
    }
}
