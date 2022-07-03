package com.example.myjwt.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.myjwt.models.Project;
import com.example.myjwt.models.SubLob;
import com.example.myjwt.models.User;

@Repository
public interface SubLobRepository extends JpaRepository<SubLob, Long> {
	Optional<SubLob> findBySubLobName(String subLobName);
	
	Boolean existsBySubLobName(String subLobName);

	Boolean existsBySubLobNameAndLobId(String subLobName, Long lobId);
	
	SubLob findByIdAndOwnerId(Long id, Long ownerId);
	
	List<SubLob> findByOwnerId(Long ownerId);
	
	@Query(value = "SELECT id, sub_lob_name FROM sublob WHERE lob_id in (SELECT id FROM "
			+ "lob WHERE account_id=(SELECT account_id FROM pmbot.customer WHERE owner_id=:ownerId))", nativeQuery = true)
	List<Object[]> findAllSubLobsInSameAccountOfLoggedInCustomerHead(Long ownerId);
}
