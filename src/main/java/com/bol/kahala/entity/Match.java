package com.bol.kahala.entity;

import static jakarta.persistence.GenerationType.AUTO;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
//@Builder
public class Match {
	
	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "match")
	private List<Player> players = new ArrayList<>();
	
	private MatchStatus status;
	
	public void addToPlayers(Player player) {
		player.setMatch(this);
		this.players.add(player);
	}
	

}
