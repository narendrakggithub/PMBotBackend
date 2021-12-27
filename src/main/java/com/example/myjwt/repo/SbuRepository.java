package com.example.myjwt.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.myjwt.models.Customer;
import com.example.myjwt.models.Project;
import com.example.myjwt.models.Sbu;
import com.example.myjwt.models.User;

@Repository
public interface SbuRepository extends JpaRepository<Sbu, Long> {
	Optional<Sbu> findBySbuName(String sbuName);

	Boolean existsBySbuName(String sbuName);
	
	List<Sbu> findBySbuHeadId(Long sbuHeadId);

	// TODO: Remove native queries
	@Query(value = "SELECT * FROM sbu v where v.user_id=:userId", nativeQuery = true)
	List<Sbu> getAllSBUOwnedByUser(Long userId);
	
	List<Sbu> findBySbuHeadIdAndSbuName (Long sbuHeadId, String sbuName);
}
