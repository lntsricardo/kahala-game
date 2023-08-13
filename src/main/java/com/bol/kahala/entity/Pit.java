package com.bol.kahala.entity;

import static jakarta.persistence.GenerationType.SEQUENCE;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pit {
	
	@Id
	@SequenceGenerator(name = "pkPit", sequenceName = "pit_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = SEQUENCE, generator = "pkPit")
	private Long id;

	@Column
	private Integer stones;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn
	@JsonIgnore
	private Player player;
	
	@Column
	private Integer pitOrder;
	
	public void addToStones(Integer stones) {
		this.stones += stones;
	}
	
}
