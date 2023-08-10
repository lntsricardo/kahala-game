package com.bol.kahala.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bol.kahala.dto.MoveDTO;
import com.bol.kahala.dto.NewMatchRequestDTO;
import com.bol.kahala.entity.Match;
import com.bol.kahala.service.MatchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/match")
public class MatchController {
	
	@Autowired
	private MatchService service;
	
	/**
	 * This endpoint creates a new match.
	 *
	 * @return Mono<Match> representing the new match.
	 */
	 @Operation(summary = "New match", description = "This endpoint creates a new match.")
	    @ApiResponses({
	            @ApiResponse(responseCode = "201", description = "Match created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Match.class)))
	    })
	@PostMapping
	public ResponseEntity<Match> newMatch(@RequestBody NewMatchRequestDTO newMatchRequestDto){
		Match match = this.service.newMatch(newMatchRequestDto);
		return new ResponseEntity<Match>(match, HttpStatus.CREATED);
	}
	 
	 /**
	 * This endpoint moves the stones from one pit.
	 *
	 * @return Mono<Match> representing the new match.
	 */
	 @Operation(summary = "Move", description = "This endpoint moves the stones from one pit.")
	    @ApiResponses({
	            @ApiResponse(responseCode = "200", description = "Stones moved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Match.class)))
	    })
	@PutMapping
	public ResponseEntity<Match> move(@RequestBody MoveDTO moveDto){
		Match match = this.service.move(moveDto);
		return ResponseEntity.ok(match);
	}
	 
	

}
