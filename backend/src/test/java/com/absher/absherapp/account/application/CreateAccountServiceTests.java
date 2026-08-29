package com.absher.absherapp.account.application;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.application.port.out.PasswordHasher;
import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountRole;
import com.absher.absherapp.citizen.domain.CitizenId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateAccountServiceTests {

    private static final String EMAIL_LOOKUP = "b".repeat(64);
    private static final PasswordHasher DETERMINISTIC_HASHER = new PasswordHasher() {
        @Override
        public String hash(String rawPassword) {
            return "bcrypt:" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String passwordHash) {
            return hash(rawPassword).equals(passwordHash);
        }
    };

    @Test
    void hashesPasswordBeforePersistingTheAccount() {
        InMemoryAccountStore store = new InMemoryAccountStore();
        CreateAccountService service = new CreateAccountService(
                store,
                DETERMINISTIC_HASHER,
                Clock.fixed(Instant.parse("2026-08-28T00:00:00Z"), ZoneOffset.UTC)
        );

        service.create(new CreateAccountCommand(
                CitizenId.newId(), EMAIL_LOOKUP, "encrypted-email", "long-enough-password", Set.of(AccountRole.CITIZEN)
        ));

        Account savedAccount = store.accountsByEmailLookup.get(EMAIL_LOOKUP);
        assertThat(savedAccount.passwordHash()).isEqualTo("bcrypt:long-enough-password");
        assertThat(savedAccount.passwordHash()).isNotEqualTo("long-enough-password");
    }

    @Test
    void rejectsPasswordsShorterThanTwelveCharacters() {
        CreateAccountService service = new CreateAccountService(
                new InMemoryAccountStore(),
                DETERMINISTIC_HASHER,
                Clock.systemUTC()
        );

        assertThatThrownBy(() -> service.create(new CreateAccountCommand(
                null, EMAIL_LOOKUP, "encrypted-email", "short", Set.of(AccountRole.EMPLOYEE)
        ))).isInstanceOf(WeakPasswordException.class);
    }

    private static final class InMemoryAccountStore implements AccountStore {
        private final Map<String, Account> accountsByEmailLookup = new HashMap<>();

        @Override
        public boolean existsByEmailLookup(String emailLookup) {
            return accountsByEmailLookup.containsKey(emailLookup);
        }

        @Override
        public boolean existsByCitizenId(CitizenId citizenId) {
            return accountsByEmailLookup.values().stream()
                    .flatMap(account -> account.citizenIdOptional().stream())
                    .anyMatch(citizenId::equals);
        }

        @Override
        public Optional<Account> findByEmailLookup(String emailLookup) {
            return Optional.ofNullable(accountsByEmailLookup.get(emailLookup));
        }

        @Override
        public Optional<Account> findById(com.absher.absherapp.account.domain.AccountId accountId) {
            return accountsByEmailLookup.values().stream().filter(account -> account.id().equals(accountId)).findFirst();
        }

        @Override
        public Account save(Account account) {
            accountsByEmailLookup.put(account.email().lookup(), account);
            return account;
        }
    }
}
