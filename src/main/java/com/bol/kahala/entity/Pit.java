package com.bol.kahala.entity;

import static jakarta.persistence.GenerationType.SEQUENCE;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	@GeneratedValue(strategy = SEQUENCE)
	private Long id;

	@Column
	private Integer stones;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn
	@JsonIgnore
	private Player player;
	
	@Column
	private Integer pitOrder;
	
}
