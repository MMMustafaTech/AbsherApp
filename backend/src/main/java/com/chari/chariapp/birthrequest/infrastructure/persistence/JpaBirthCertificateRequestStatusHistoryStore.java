package com.chari.chariapp.birthrequest.infrastructure.persistence;
import com.chari.chariapp.birthrequest.application.port.out.BirthCertificateRequestStatusHistoryStore;
import com.chari.chariapp.birthrequest.domain.BirthCertificateRequestStatusChange;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository public class JpaBirthCertificateRequestStatusHistoryStore implements BirthCertificateRequestStatusHistoryStore{
    private final SpringDataBirthCertificateRequestStatusHistoryRepository repository;
    public JpaBirthCertificateRequestStatusHistoryStore(SpringDataBirthCertificateRequestStatusHistoryRepository repository){this.repository=repository;}
    public void append(BirthCertificateRequestStatusChange c){repository.save(BirthCertificateRequestStatusHistoryJpaEntity.from(c));}
    public List<BirthCertificateRequestStatusChange> findByRequestId(UUID id){return repository.findByRequestIdOrderByChangedAtAsc(id.toString()).stream().map(BirthCertificateRequestStatusHistoryJpaEntity::toDomain).toList();}
}
