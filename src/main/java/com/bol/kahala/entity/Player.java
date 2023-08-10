package com.bol.kahala.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.AUTO;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
	
	@Id
	@GeneratedValue(strategy = AUTO)
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
	private Match match;
	
	public void addToPits(Pit pit) {
		this.pits.add(pit);
	}

}
