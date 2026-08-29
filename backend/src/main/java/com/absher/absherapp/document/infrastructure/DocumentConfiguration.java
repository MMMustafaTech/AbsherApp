package com.absher.absherapp.document.infrastructure;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.document.application.MyDocumentsService;
import com.absher.absherapp.document.application.MyDocumentsUseCase;
import com.absher.absherapp.document.application.port.out.CitizenDocumentReadStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentConfiguration {

    @Bean
    public MyDocumentsUseCase myDocumentsUseCase(
            AccountStore accountStore,
            CitizenStore citizenStore,
            CitizenDocumentReadStore documentStore
    ) {
        return new MyDocumentsService(accountStore, citizenStore, documentStore);
    }
}
