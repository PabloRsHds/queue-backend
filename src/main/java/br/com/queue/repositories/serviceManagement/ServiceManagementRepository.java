package br.com.queue.repositories.serviceManagement;

import br.com.queue.entities.serviceManagement.ServiceManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ServiceManagementRepository extends JpaRepository<ServiceManagement, String> {

    Optional<ServiceManagement> findByServiceManagementId(String serviceManagementId);

    @Query("""
            SELECT sm
            FROM ServiceManagement sm
            WHERE sm.serviceManagementId IN :serviceManagementIds
        """)
    Set<ServiceManagement> findAllByServiceManagementIdIn(
            @Param("serviceManagementIds") Set<String> serviceManagementIds);
}
