package vn.homthugopy.suggestion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.homthugopy.suggestion.entity.Suggestion;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
	Optional<Suggestion> findByTrackingCode(String trackingCode);
	List<Suggestion> findByIsDeletedFalseOrderBySuggestAtDesc();
}
