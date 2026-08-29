package com.chari.chariapp.document.infrastructure;

import com.chari.chariapp.account.application.port.out.AccountStore;
import com.chari.chariapp.citizen.application.port.out.CitizenStore;
import com.chari.chariapp.document.application.MyDocumentsService;
import com.chari.chariapp.document.application.MyDocumentsUseCase;
import com.chari.chariapp.document.application.port.out.CitizenDocumentReadStore;
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
