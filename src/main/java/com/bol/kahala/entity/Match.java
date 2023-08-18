package com.bol.kahala.entity;

import static jakarta.persistence.GenerationType.AUTO;
import static jakarta.persistence.GenerationType.SEQUENCE;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Match {
	
	@Id
	@SequenceGenerator(name = "pkMatch", sequenceName = "match_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = SEQUENCE, generator = "pkMatch")
	private Long id;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "match")
	private List<Player> players = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column
	private MatchStatus status;
	
	public void addToPlayers(Player player) {
		player.setMatch(this);
		this.players.add(player);
	}
	

}
