package com.koreait.sssandupload.app.security.service;

import com.koreait.sssandupload.app.member.entity.Member;
import com.koreait.sssandupload.app.member.exception.MemberNotFoundException;
import com.koreait.sssandupload.app.member.repository.MemberRepository;
import com.koreait.sssandupload.app.member.service.MemberService;
import com.koreait.sssandupload.app.security.dto.MemberContext;
import com.koreait.sssandupload.app.security.exception.OAuthTypeMatchNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String oauthId = oAuth2User.getName();

        Member member = null;
        String oauthType = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        if (!"KAKAO".equals(oauthType)) {
            throw new OAuthTypeMatchNotFoundException();
        }

        if (isNew(oauthType, oauthId)) {
            switch (oauthType) {
                case "KAKAO" -> {
                    log.debug("attibutes : " + attributes);

                    Map attributesProperties = (Map) attributes.get("properties");
                    Map attributesKakaoAcount = (Map) attributes.get("kakao_account");
                    System.out.println(attributesProperties);
                    String nickname = (String) attributesProperties.get("nickname");
                    String profile_image = (String) attributesProperties.get("profile_image");
                    String username = "KAKAO_%s".formatted(oauthId);
                    String email = "%s@kakao.com".formatted(oauthId);

//                    if ((boolean) attributesKakaoAcount.get("has_email")) {
//                        username = (String) attributesKakaoAcount.get("email");
//                    }

                    member = Member.builder()
//                            .email(email)
                            .nickname(nickname)
                            .username(username)
                            .profileImg(profile_image)
                            .password("")
                            .build();

                    memberRepository.save(member);

//                    memberService.setProfileImgByUrl(member,"https://picsum.photos/200/300");
                    memberService.setProfileImgByUrl(member,profile_image);
                }
            }
        } else {
            member = memberRepository.findByUsername("%s_%s".formatted(oauthType, oauthId))
                    .orElseThrow(MemberNotFoundException::new);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("member"));
        return new MemberContext(member, authorities, attributes, userNameAttributeName);
    }

    private boolean isNew(String oAuthType, String oAuthId) {
        return memberRepository.findByUsername("%s_%s".formatted(oAuthType, oAuthId)).isEmpty();
    }
}
