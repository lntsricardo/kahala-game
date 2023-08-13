package com.bol.kahala.service;

import static com.bol.kahala.entity.MatchStatus.ACTIVE;
import static com.bol.kahala.entity.MatchStatus.FINISHED;
import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.hasText;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.bol.kahala.exception.KahalaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bol.kahala.dto.MoveDTO;
import com.bol.kahala.dto.NewMatchRequestDTO;
import com.bol.kahala.entity.Match;
import com.bol.kahala.entity.Pit;
import com.bol.kahala.entity.Player;
import com.bol.kahala.repository.MatchRepository;

import jakarta.transaction.Transactional;
import org.springframework.util.StringUtils;

@Service
public class MatchService {
	
	private static final int BIG_PIT_DEFAULT_VALUE = 0;
	private static final int SMALL_PIT_DEFAULT_VALUE = 6;
	private static final int SMALL_PIT_MAX_ORDER = 6;
	private static final int BIG_PIT_ORDER = 7;
	
	@Autowired
	private MatchRepository repository;

	/**
	 * This method creates a new match with only two players.
	 * @param newMatchRequestDto 
	 * 
	 * @return A new Match.
	 */
	@Transactional
	public Match newMatch(NewMatchRequestDTO newMatchRequestDto) {
		this.validatePlayersName(newMatchRequestDto);
		Match match = new Match();
		match.setStatus(ACTIVE);
		Player firstPlayer = Player
				.builder()
				.match(match)
				.turn(true)
				.name(newMatchRequestDto.player1())
				.build();
		Player secondPlayer = Player
				.builder()
				.match(match)
				.turn(false)
				.name(newMatchRequestDto.player2())
				.build();
		this.createPits(firstPlayer);
		this.createPits(secondPlayer);
		match.setPlayers(Arrays.asList(firstPlayer, secondPlayer));
		this.repository.save(match);
		return match;
	}

	/**
	 * This method applies some validations to guarantee that a new match can be created successfully.
	 * @param newMatchRequestDto
	 */
	private void validatePlayersName(NewMatchRequestDTO newMatchRequestDto) {
		if (isNull(newMatchRequestDto) || !hasText(newMatchRequestDto.player1()) || !hasText(newMatchRequestDto.player2())) {
			throw new IllegalArgumentException("Player's name can't be null or empty.");
        }
	}

	/**
	 * This method get the pits from database and moves the stones from one pit to each of the following pits.
	 * 
	 * @param moveDto
	 * @return The match after the move.
	 */
	@Transactional
	public Match move(MoveDTO moveDto) {
		this.validateMoveDto(moveDto);
		Match match = this.findMatch(moveDto);
		List<Pit> pits = match
							.getPlayers()
							.stream()
							.map(Player::getPits)
							.flatMap(Collection<Pit>::stream)
							.sorted(Comparator
									.comparing(Pit::getPlayer, Comparator.comparing(Player::getId))
									.thenComparingInt(Pit::getPitOrder))
							.toList();
		
		Pit movePit = pits.stream().filter(p -> p.getId().equals(moveDto.pitId())).findFirst().get();
		Pit lastPit = this.sowStones(moveDto, pits, movePit);
		return this.applyFinalSteps(match, pits, movePit, lastPit);
	}

	/**
	 * Finds the match in database. If match is not found, that means the pit id is invalid.
	 *
	 * @param moveDto
	 * @return
	 */
	private Match findMatch(MoveDTO moveDto) {
		Match match = this.repository.findMatchByPitId(moveDto.pitId());
		if (isNull(match)){
			throw new IllegalArgumentException("Pit id is invalid.");
		}
		return match;
	}

	/**
	 * This method applies some validation to guarantee that the stones can be moved successfully.
	 * @param moveDto
	 */
	private void validateMoveDto(MoveDTO moveDto) {
		if (isNull(moveDto) || isNull(moveDto.pitId())){
			throw new IllegalArgumentException("Pit id can't be null");
		}
	}

	/**
	 * This method applies final steps to the move.
	 * <ul>
	 * <li>1 - If the last stone landed in the big pit it returns the match and the player gets another turn.</li>
	 * <li>2 - If the last stone landed in an empty small pit, steal the stones from the opposite player.</li>
	 * <li>3 - If any player is out of stones, finishes the match and moves the remaining stones to the player's big pit.</li>
	 * <li>4 - If 1 and 3 are not true, change the player's turn and returns the match.</li>
	 * </ul>
	 * 
	 * @param match
	 * @param pits
	 * @param movePit
	 * @param lastPit
	 * @return
	 */
	private Match applyFinalSteps(Match match, List<Pit> pits, Pit movePit, Pit lastPit) {
		if (lastPit.getPitOrder() == BIG_PIT_ORDER) {
			return match;
		}
		Player movePlayer = movePit.getPlayer();
		this.stealOppositeStones(pits, lastPit, movePlayer);
		Map<Player, Integer> playerStones = pits
		 .stream()
		 .filter(p -> p.getPitOrder() != BIG_PIT_ORDER)
		 .collect(Collectors.groupingBy(Pit::getPlayer, Collectors.summingInt(Pit::getStones)));
		boolean isOutOfStones = playerStones.values().stream().anyMatch(t -> t.equals(0));
		if (isOutOfStones) {
			return this.finishMatch(match, playerStones);
		}
		return this.changePlayersTurn(match, movePlayer);
	}

	/**
	 * This method changes each players turn variable.
	 * @param match
	 * @param movePlayer
	 * @return
	 */
	private Match changePlayersTurn(Match match, Player movePlayer) {
		movePlayer.setTurn(false);
		Optional<Player> otherPlayer = match.getPlayers().stream().filter(p -> !(p.getId().equals(movePlayer.getId()))).findFirst();
		otherPlayer.ifPresent(op -> op.setTurn(true));
		return match;
	}

	/**
	 * Gets all the stones from small pits and adds it to the big pit. Also, set the match status to {@link com.bol.kahala.entity.MatchStatus}.FINISHED.
	 * @param match
	 * @param playerStones
	 * @return
	 */
	private Match finishMatch(Match match, Map<Player, Integer> playerStones) {
		playerStones
			.keySet()
			.forEach(player -> {
				player.setTurn(false);
				Optional<Pit> bigPit = player.getPits().stream().filter(p -> p.getPitOrder().equals(BIG_PIT_ORDER)).findFirst();
				bigPit.ifPresent(bp -> bp.addToStones(playerStones.get(player)));
			});
		match.setStatus(FINISHED);
		return match;
	}

	/**
	 * Verifies if the last stone landed in a player's empty pit. If it is empty and the opposite pit has stones,
	 * it captures all the stones from the opposite pit and adds them to the big pit. If it is not empty or the opposite
	 * pit doesn't have stones, no stones are added to the big pit.
	 * 
	 * @param pits
	 * @param lastPit
	 * @param movePlayer
	 */
	private void stealOppositeStones(List<Pit> pits, Pit lastPit, Player movePlayer) {
		final Player lastPitPlayer = lastPit.getPlayer();
		if (!hasLandedInEmptyPit(lastPit, movePlayer, lastPitPlayer)) {
			return;
		}
		Integer oppositePitOrder = (SMALL_PIT_MAX_ORDER + 1) - lastPit.getPitOrder();
		Optional<Pit> oppositePit = pits
				.stream()
				.filter(p -> p.getPitOrder().equals(oppositePitOrder) && !(p.getPlayer().equals(movePlayer)))
				.findFirst();
		if (oppositePit.isEmpty() || oppositePit.get().getStones().equals(0)) {
			return;
		}
		Optional<Pit> bigPit = pits
				.stream()
				.filter(p -> p.getPitOrder().equals(BIG_PIT_ORDER) && p.getPlayer().equals(movePlayer))
				.findFirst();
		Consumer<Pit> addStonesConsumer = p -> p.addToStones(lastPit.getStones() + oppositePit.get().getStones());
		bigPit.ifPresentOrElse(addStonesConsumer, () -> { throw new KahalaException("Big pit not found."); });
		oppositePit.get().setStones(0);
		lastPit.setStones(0);
	}

	/**
	 * Validates if the stone landed in player's empty pit.
	 *
	 * @param lastPit
	 * @param movePlayer
	 * @param lastPitPlayer
	 * @return True if
	 */
	private boolean hasLandedInEmptyPit(Pit lastPit, Player movePlayer, final Player lastPitPlayer) {
		return lastPitPlayer.equals(movePlayer) && lastPit.getStones().equals(1);
	}

	/**
	 * This method sows the stones from the movePit to each one of the following pits, excluding only the opposite player's big pit.
	 * 
	 * @param moveDto
	 * @param pits
	 * @param movePit
	 * @return Pit - The last pit to receive a stone.
	 */
	private Pit sowStones(MoveDTO moveDto, List<Pit> pits, Pit movePit) {
		int pitIndex = IntStream.range(0, pits.size()).filter(i -> moveDto.pitId().equals(pits.get(i).getId())).findFirst().getAsInt();
		Integer stones = movePit.getStones();
		movePit.setStones(0);
		Pit lastPit = null;
		while (stones > 0) {
			pitIndex++;
			if (pitIndex == pits.size()) {
				pitIndex = 0;
			}
			Pit pit = pits.get(pitIndex);
			if (pit.getPitOrder() == BIG_PIT_ORDER && !(movePit.getPlayer().equals(pit.getPlayer()))) {
				continue;
			}
			pit.setStones(pit.getStones() + 1);
			lastPit = pit;
			stones--;
		}
		return lastPit;
	}

	/**
	 * Creates the smallPits ArrayList ordered with STONES_DEFAULT_VALUE 
	 * @param player
	 */
	private void createPits(Player player) {
		for (int index = 1; index <= SMALL_PIT_MAX_ORDER; index++) {
			player.addToPits(Pit.builder()
					.stones(SMALL_PIT_DEFAULT_VALUE)
					.player(player)
					.pitOrder(index)
					.build());
		}
		player.addToPits(Pit.builder()
					.stones(BIG_PIT_DEFAULT_VALUE)
					.player(player)
					.pitOrder(BIG_PIT_ORDER)
					.build());
	}

}
