package NextLevel.demo.user.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.role.UserRole;
import NextLevel.demo.user.dto.RequestUserCreateDto;
import NextLevel.demo.user.dto.login.RequestEmailLoginDto;
import NextLevel.demo.user.entity.UserDetailEntity;
import NextLevel.demo.user.service.UserValidateService;
import NextLevel.demo.user.service.EmailService;
import NextLevel.demo.user.service.LoginService;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/public/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final UserValidateService userValidateService;
    private final JWTUtil jwtUtil;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<?> register(
        @ModelAttribute @Valid RequestUserCreateDto requestUserCreateDto,
        HttpServletResponse httpServletResponse) {

        UserDetailEntity createdUser = loginService.register(requestUserCreateDto, null);

        jwtUtil.addRefresh(httpServletResponse, createdUser.getUser().getId(), createdUser.getUUID());

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    @PutMapping
    public ResponseEntity<?> login(
        @RequestBody @Valid RequestEmailLoginDto requestEmailLoginDto,
        HttpServletRequest httpServletRequest ,
        HttpServletResponse httpServletResponse) {

        UserDetailEntity user = loginService.login(requestEmailLoginDto);

        jwtUtil.addRefresh(httpServletResponse, user.getUser().getId(), user.getUUID());
        jwtUtil.addAccess(httpServletResponse, user.getUserId(), httpServletRequest, user.getUser().getRole());

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    @GetMapping("/token")
    public ResponseEntity<?> auth() {
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        String role;

        System.out.println(authorities);

        if(authorities.contains(new SimpleGrantedAuthority("ROLE_"+UserRole.ADMIN.name())))
            role = UserRole.ADMIN.name();
        else if(authorities.contains(new SimpleGrantedAuthority("ROLE_"+UserRole.USER.name())))
            role = UserRole.USER.name();
        else if(authorities.contains(new SimpleGrantedAuthority("ROLE_"+UserRole.SOCIAL.name())))
            role = UserRole.SOCIAL.name();
        else
            role = "no login";

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", role));
    }


    @GetMapping("/nickName")
    public ResponseEntity<?> checkNickName(@RequestParam("nickName") String nickName) {
        if(userValidateService.checkNickNameIsNotExist(nickName))
            return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("not exist", null));
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new SuccessResponse("exist", null));
    }

    @GetMapping("/email")
    public ResponseEntity<?> checkEmailIsNotExist(@RequestParam("email") String email) {
        if(userValidateService.checkEmailIsNotExist(email))
            return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("not exist", null));
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new SuccessResponse("exist", null));
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String , String> requestMap) {
        emailService.sendEmailCode(requestMap.get("email"));
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    @PutMapping("/email")
    public ResponseEntity<?> checkEmail(@RequestBody Map<String , String> requestMap) {
        if(emailService.checkEmailKey(requestMap.get("email"), requestMap.get("key")))
            return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("correct", null));
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new SuccessResponse("not correct", null));
    }

//
//    @PostMapping("/new-password")
//    public ResponseEntity<?> newPassword(@RequestBody String email) {
//
//    }

}
