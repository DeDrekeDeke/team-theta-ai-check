package com.example.cvmanager.cv.repository;

import com.example.cvmanager.cv.model.Cv;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CvRepository extends JpaRepository<Cv, Long> {

    List<Cv> findByOwnerId(Long ownerId);

    //List<Cv> findByOwnerIdAndArchivedAtIsNull(Long ownerId);

    // List<Cv> findByArchivedAtIsNull();

    List<Cv> findByArchivedAtIsNull(Sort sort);

    Optional<Cv> findByIdAndArchivedAtIsNull(Long id);
}
