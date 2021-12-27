package com.example.myjwt.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.example.myjwt.models.Sbu;
import com.example.myjwt.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserName(String userName);

	User findByEmail(String email);

	Boolean existsByUserName(String userName);

	Boolean existsByEmail(String email);

	// TODO: Remove native queries

	@Query(value = "SELECT * FROM user v where v.grade_id in :gradeIds", nativeQuery = true)
	List<User> findEligibleSBUHeads(@Param("gradeIds") List<Long> gradeIds);

	// TODO: Remove native queries
	@Query(value = "SELECT * FROM user v where v.user_name=:userName and v.grade_id in :gradeIds", nativeQuery = true)
	List<User> findByUserNameAndGradeIds(String userName, List<Long> gradeIds);

	// TODO: Remove native queries
	@Query(value = "SELECT * FROM user v where v.manager_id=:managerId and v.grade_id in :gradeIds", nativeQuery = true)
	List<User> findByManagerAndGradeIds(Long managerId, List<Long> gradeIds);

	// TODO: Remove native queries
	@Query("SELECT v FROM User v where v.manager.id=:userId and v.userName=:userName and v.grade.id in :gradeIds")
	List<User> getUserWithGradeOwnedByCurrentUser(Long userId, String userName, List<Long> gradeIds);
}
