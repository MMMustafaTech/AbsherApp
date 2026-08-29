package com.absher.absherapp.account.application;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.application.port.out.RefreshSessionStore;
import com.absher.absherapp.account.application.port.out.RefreshTokenHasher;
import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountStatus;
import com.absher.absherapp.account.domain.RefreshSession;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public class RefreshTokenService implements RefreshTokenUseCase, LogoutUseCase {

    private final AccountStore accountStore;
    private final RefreshSessionStore refreshSessionStore;
    private final RefreshTokenHasher refreshTokenHasher;
    private final LoginService loginService;
    private final Clock clock;

    public RefreshTokenService(
            AccountStore accountStore,
            RefreshSessionStore refreshSessionStore,
            RefreshTokenHasher refreshTokenHasher,
            LoginService loginService,
            Clock clock
    ) {
        this.accountStore = Objects.requireNonNull(accountStore, "Account store is required");
        this.refreshSessionStore = Objects.requireNonNull(refreshSessionStore, "Refresh session store is required");
        this.refreshTokenHasher = Objects.requireNonNull(refreshTokenHasher, "Refresh token hasher is required");
        this.loginService = Objects.requireNonNull(loginService, "Login service is required");
        this.clock = Objects.requireNonNull(clock, "Clock is required");
    }

    @Override
    @Transactional
    public TokenPair refresh(String refreshToken) {
        Instant now = Instant.now(clock);
        RefreshSession session = usableSession(refreshToken, now);
        Account account = accountStore.findById(session.accountId())
                .filter(candidate -> candidate.status() == AccountStatus.ACTIVE)
                .orElseThrow(InvalidRefreshTokenException::new);
        refreshSessionStore.save(session.revoke(now));
        return loginService.issueTokenPair(account, now);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        Instant now = Instant.now(clock);
        refreshSessionStore.findByTokenHashForUpdate(refreshTokenHasher.hash(refreshToken))
                .filter(session -> session.isUsable(now))
                .ifPresent(session -> refreshSessionStore.save(session.revoke(now)));
    }

    private RefreshSession usableSession(String refreshToken, Instant now) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException();
        }
        return refreshSessionStore.findByTokenHashForUpdate(refreshTokenHasher.hash(refreshToken))
                .filter(session -> session.isUsable(now))
                .orElseThrow(InvalidRefreshTokenException::new);
    }
}
