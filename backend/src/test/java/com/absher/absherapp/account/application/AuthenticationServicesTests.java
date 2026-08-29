package com.absher.absherapp.account.application;

import com.absher.absherapp.account.application.port.out.AccessTokenIssuer;
import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.application.port.out.PasswordHasher;
import com.absher.absherapp.account.application.port.out.RefreshSessionStore;
import com.absher.absherapp.account.application.port.out.RefreshTokenGenerator;
import com.absher.absherapp.account.application.port.out.RefreshTokenHasher;
import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.AccountRole;
import com.absher.absherapp.account.domain.AccountStatus;
import com.absher.absherapp.account.domain.EmailReference;
import com.absher.absherapp.account.domain.RefreshSession;
import com.absher.absherapp.citizen.domain.CitizenId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticationServicesTests {

    private static final Instant NOW = Instant.parse("2026-08-28T00:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final String EMAIL_LOOKUP = "a".repeat(64);

    @Test
    void loginStoresOnlyTheRefreshTokenHashAndIssuesAnAccessToken() {
        InMemoryAccountStore accounts = new InMemoryAccountStore(activeAccount());
        InMemoryRefreshSessions sessions = new InMemoryRefreshSessions();
        LoginService login = loginService(accounts, sessions);

        TokenPair tokenPair = login.login(new LoginCommand(EMAIL_LOOKUP, "a-secure-password"));

        assertThat(tokenPair.accessToken()).isEqualTo("access-token");
        assertThat(tokenPair.refreshToken()).isEqualTo("refresh-token-1");
        assertThat(sessions.sessions).singleElement().satisfies(session -> {
            assertThat(session.tokenHash()).isEqualTo("hash:refresh-token-1");
            assertThat(session.tokenHash()).isNotEqualTo(tokenPair.refreshToken());
        });
    }

    @Test
    void refreshRotatesTheTokenAndRejectsItsPreviousValue() {
        InMemoryAccountStore accounts = new InMemoryAccountStore(activeAccount());
        InMemoryRefreshSessions sessions = new InMemoryRefreshSessions();
        LoginService login = loginService(accounts, sessions);
        RefreshTokenService refresh = new RefreshTokenService(
                accounts, sessions, tokenHasher(), login, CLOCK
        );
        TokenPair initial = login.login(new LoginCommand(EMAIL_LOOKUP, "a-secure-password"));

        TokenPair rotated = refresh.refresh(initial.refreshToken());

        assertThat(rotated.refreshToken()).isEqualTo("refresh-token-2");
        assertThat(sessions.sessions).hasSize(2);
        assertThat(sessions.sessions.getFirst().revokedAt()).isEqualTo(NOW);
        assertThatThrownBy(() -> refresh.refresh(initial.refreshToken()))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void logoutRevokesTheRefreshSession() {
        InMemoryAccountStore accounts = new InMemoryAccountStore(activeAccount());
        InMemoryRefreshSessions sessions = new InMemoryRefreshSessions();
        LoginService login = loginService(accounts, sessions);
        RefreshTokenService refresh = new RefreshTokenService(
                accounts, sessions, tokenHasher(), login, CLOCK
        );
        TokenPair initial = login.login(new LoginCommand(EMAIL_LOOKUP, "a-secure-password"));

        refresh.logout(initial.refreshToken());

        assertThat(sessions.sessions).singleElement().extracting(RefreshSession::revokedAt).isEqualTo(NOW);
        assertThatThrownBy(() -> refresh.refresh(initial.refreshToken()))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void rejectsIncorrectCredentialsWithoutCreatingASession() {
        InMemoryRefreshSessions sessions = new InMemoryRefreshSessions();
        LoginService login = loginService(new InMemoryAccountStore(activeAccount()), sessions);

        assertThatThrownBy(() -> login.login(new LoginCommand(EMAIL_LOOKUP, "wrong-password")))
                .isInstanceOf(InvalidCredentialsException.class);
        assertThat(sessions.sessions).isEmpty();
    }

    private static LoginService loginService(InMemoryAccountStore accounts, InMemoryRefreshSessions sessions) {
        return new LoginService(
                accounts,
                new PasswordHasher() {
                    @Override
                    public String hash(String rawPassword) {
                        return "hash:" + rawPassword;
                    }

                    @Override
                    public boolean matches(String rawPassword, String passwordHash) {
                        return hash(rawPassword).equals(passwordHash);
                    }
                },
                sessions,
                new RefreshTokenGenerator() {
                    private int counter;

                    @Override
                    public String generate() {
                        return "refresh-token-" + ++counter;
                    }
                },
                tokenHasher(),
                (account, issuedAt) -> new AccessTokenIssuer.IssuedAccessToken("access-token", issuedAt.plusSeconds(900)),
                CLOCK
        );
    }

    private static RefreshTokenHasher tokenHasher() {
        return token -> "hash:" + token;
    }

    private static Account activeAccount() {
        return new Account(
                AccountId.newId(),
                CitizenId.newId(),
                new EmailReference(EMAIL_LOOKUP, "ciphertext-email"),
                "hash:a-secure-password",
                AccountStatus.ACTIVE,
                Set.of(AccountRole.CITIZEN),
                NOW.minusSeconds(60)
        );
    }

    private static final class InMemoryAccountStore implements AccountStore {
        private final Account account;

        private InMemoryAccountStore(Account account) {
            this.account = account;
        }

        @Override
        public boolean existsByEmailLookup(String emailLookup) {
            return account.email().lookup().equals(emailLookup);
        }

        @Override
        public boolean existsByCitizenId(CitizenId citizenId) {
            return account.citizenId().equals(citizenId);
        }

        @Override
        public Optional<Account> findByEmailLookup(String emailLookup) {
            return account.email().lookup().equals(emailLookup) ? Optional.of(account) : Optional.empty();
        }

        @Override
        public Optional<Account> findById(AccountId accountId) {
            return account.id().equals(accountId) ? Optional.of(account) : Optional.empty();
        }

        @Override
        public Account save(Account account) {
            return account;
        }
    }

    private static final class InMemoryRefreshSessions implements RefreshSessionStore {
        private final List<RefreshSession> sessions = new ArrayList<>();

        @Override
        public RefreshSession save(RefreshSession session) {
            sessions.removeIf(existing -> existing.id().equals(session.id()));
            sessions.add(session);
            return session;
        }

        @Override
        public Optional<RefreshSession> findByTokenHashForUpdate(String tokenHash) {
            return sessions.stream().filter(session -> session.tokenHash().equals(tokenHash)).findFirst();
        }
    }
}
