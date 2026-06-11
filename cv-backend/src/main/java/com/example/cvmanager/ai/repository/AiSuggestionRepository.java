package com.example.cvmanager.ai.repository;

import com.example.cvmanager.ai.model.AiSuggestion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiSuggestionRepository extends JpaRepository<AiSuggestion, Long> {

    List<AiSuggestion> findByCvIdOrderByCreatedAtDesc(Long cvId);

    Optional<AiSuggestion> findByIdAndCvId(Long id, Long cvId);
}
