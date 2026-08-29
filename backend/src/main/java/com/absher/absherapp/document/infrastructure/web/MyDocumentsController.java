package com.absher.absherapp.document.infrastructure.web;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.document.application.MyDocumentsUseCase;
import com.absher.absherapp.document.domain.MyBirthCertificate;
import com.absher.absherapp.document.domain.MyNationalIdentity;
import com.absher.absherapp.document.domain.MyPassport;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/me/documents")
public class MyDocumentsController {

    private final MyDocumentsUseCase myDocumentsUseCase;

    public MyDocumentsController(MyDocumentsUseCase myDocumentsUseCase) {
        this.myDocumentsUseCase = myDocumentsUseCase;
    }

    @GetMapping("/passport")
    public MyPassport passport(@AuthenticationPrincipal Jwt jwt) {
        return myDocumentsUseCase.getPassport(accountId(jwt));
    }

    @GetMapping("/national-identity")
    public MyNationalIdentity nationalIdentity(@AuthenticationPrincipal Jwt jwt) {
        return myDocumentsUseCase.getNationalIdentity(accountId(jwt));
    }

    @GetMapping("/birth-certificate")
    public MyBirthCertificate birthCertificate(@AuthenticationPrincipal Jwt jwt) {
        return myDocumentsUseCase.getBirthCertificate(accountId(jwt));
    }

    private static AccountId accountId(Jwt jwt) {
        return new AccountId(UUID.fromString(jwt.getSubject()));
    }
}
