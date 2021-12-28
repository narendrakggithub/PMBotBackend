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

	List<User> findByManagerId(Long managerId);

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

	@Query(value = "WITH RECURSIVE subordinate AS (SELECT  id, created_at, updated_at, email, full_name, is_active, is_approved, is_verified, password, "+
			"user_name, grade_id, manager_id, roleid, 0 AS level FROM user WHERE manager_id=:managerId "+
			"UNION ALL "+
			"SELECT e.id, e.created_at, e.updated_at, e.email, e.full_name, e.is_active, e.is_approved, e.is_verified, e.password, "+
			"e.user_name, e.grade_id, e.manager_id, e.roleid, level + 1 FROM user e JOIN subordinate s ON e.manager_id = s.id) "+
			"SELECT s.id, s.created_at, s.updated_at, s.email, s.full_name, s.is_active, s.is_approved, s.is_verified, s.password, s.user_name, s.grade_id, s.manager_id, s.roleid "+
			"FROM subordinate s JOIN user m ON s.manager_id = m.id ORDER BY level", nativeQuery = true)
	List<Object[]> getAllUserUnderManagerWithId(Long managerId);
}
