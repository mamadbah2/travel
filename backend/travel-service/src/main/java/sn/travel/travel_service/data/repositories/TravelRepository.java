package sn.travel.travel_service.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.travel.travel_service.data.entities.Travel;
import sn.travel.travel_service.data.enums.TravelStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Repository for Travel entity.
 */
@Repository
public interface TravelRepository extends JpaRepository<Travel, UUID> {

    Page<Travel> findByManagerId(UUID managerId, Pageable pageable);

    Page<Travel> findByStatus(TravelStatus status, Pageable pageable);

    Page<Travel> findByManagerIdAndStatus(UUID managerId, TravelStatus status, Pageable pageable);

    @Query("SELECT t FROM Travel t WHERE t.status = 'PUBLISHED' AND t.startDate > :now")
    Page<Travel> findAvailableTravels(@Param("now") LocalDate now, Pageable pageable);

    @Query("""
        SELECT t FROM Travel t
        WHERE t.status = 'PUBLISHED'
        AND t.startDate > :now
        AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<Travel> searchPublishedTravels(@Param("search") String search, @Param("now") LocalDate now, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Travel t WHERE t.managerId = :managerId")
    long countByManagerId(@Param("managerId") UUID managerId);

    @Query("SELECT COUNT(t) FROM Travel t WHERE t.managerId = :managerId AND t.status = :status")
    long countByManagerIdAndStatus(@Param("managerId") UUID managerId, @Param("status") TravelStatus status);
}
