package com.koreait.sssandupload.app.home.contoller;

import com.koreait.sssandupload.app.member.entity.Member;
import com.koreait.sssandupload.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MemberService memberService;

    @GetMapping("/")
    public String showMain(Principal principal, Model model) {
        Member loginedMember = null;
        String loginedMemberProfileImgUrl = null;

        if (principal != null && principal.getName() != null) {
            loginedMember = memberService.getMemberByUsername(principal.getName());
        }

        if (loginedMember != null) {
            loginedMemberProfileImgUrl = loginedMember.getProfileImgUrl();
        }

        model.addAttribute("loginedMember", loginedMember);
        model.addAttribute("loginedMemberProfileImgUrl", loginedMemberProfileImgUrl);

        return "home/main";
    }

    @GetMapping("/about")
    public String showAbout(Principal principal, Model model) {
        Member loginedMember = null;
        String loginedMemberProfileImgUrl = null;

        if (principal != null && principal.getName() != null) {
            loginedMember = memberService.getMemberByUsername(principal.getName());
        }

        if (loginedMember != null) {
            loginedMemberProfileImgUrl = loginedMember.getProfileImgUrl();
        }

        model.addAttribute("loginedMember", loginedMember);
        model.addAttribute("loginedMemberProfileImgUrl", loginedMemberProfileImgUrl);

        return "home/about";
    }
}