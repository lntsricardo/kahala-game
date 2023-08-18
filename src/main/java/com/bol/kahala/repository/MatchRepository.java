package com.bol.kahala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bol.kahala.entity.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long>{
	
	@Query("SELECT m FROM Match m JOIN m.players p JOIN p.pits sp WHERE sp.id = ?1")
	Match findMatchByPitId(Long pitId);

}
