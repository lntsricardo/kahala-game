package com.bol.kahala.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
	
	@Id
	@SequenceGenerator(name = "pkPlayer", sequenceName = "player_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = SEQUENCE, generator = "pkPlayer")
	private Long id;
	
	@Column
	private boolean turn;
	
	@Column
	private String name;
	
	@Builder.Default
	@OneToMany(cascade = ALL, mappedBy = "player")
	private List<Pit> pits = new ArrayList<>();
	
	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn
	@JsonIgnore
	@ToString.Exclude
	private Match match;
	
	public void addToPits(Pit pit) {
		this.pits.add(pit);
	}

}
