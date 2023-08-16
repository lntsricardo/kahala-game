package com.bol.kahala.service;

import com.bol.kahala.constants.KahalaConstants;
import com.bol.kahala.dto.MoveDTO;
import com.bol.kahala.dto.NewMatchRequestDTO;
import com.bol.kahala.entity.Match;
import com.bol.kahala.entity.Pit;
import com.bol.kahala.entity.Player;
import com.bol.kahala.exception.KahalaException;
import com.bol.kahala.repository.MatchRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.bol.kahala.constants.KahalaConstants.*;
import static com.bol.kahala.entity.MatchStatus.ACTIVE;
import static com.bol.kahala.entity.MatchStatus.FINISHED;
import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.hasText;

@Service
public class MatchService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MatchService.class);
	
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
		LOGGER.info("Creating a new match with players {}", newMatchRequestDto);
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
		LOGGER.info("Match created successfully. {}", match);
		return match;
	}

	/**
	 * This method get the pits from database and moves the stones from one pit to each of the following pits.
	 *
	 * @param moveDto
	 * @return The match after the move.
	 */
	@Transactional
	public Match move(MoveDTO moveDto) {
		LOGGER.info("Moving stones. {}", moveDto);
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
		Pit movePit = pits
				.stream()
				.filter(p -> p.getId().equals(moveDto.pitId()))
				.findFirst()
				.get();
		LOGGER.info("Moving stones from pit {}", movePit);
		this.validatePlayersTurn(movePit);
		Pit lastPit = this.sowStones(moveDto, pits, movePit);
		return this.applyFinalSteps(match, pits, movePit, lastPit);
	}

	/**
	 * This method applies some validations to guarantee that a new match can be created successfully.
	 * @param newMatchRequestDto
	 */
	private void validatePlayersName(NewMatchRequestDTO newMatchRequestDto) {
		if (isNull(newMatchRequestDto) || !hasText(newMatchRequestDto.player1()) || !hasText(newMatchRequestDto.player2())) {
			LOGGER.error("Player name cannot be null or empty.");
			throw new IllegalArgumentException(ERROR_MESSAGE_PLAYER_NAME_EMPTY);
		}
	}

	private void validatePlayersTurn(Pit movePit) {
		Player player = movePit.getPlayer();
		if (player.isTurn()){
			return;
		}
		LOGGER.error("Wrong player making move.");
		throw new IllegalArgumentException(KahalaConstants.ERROR_MESSAGE_PLAYERS_TURN);
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
			LOGGER.error("Match not found. Probably pit id is invalid.");
			throw new IllegalArgumentException(ERROR_MESSAGE_PIT_ID_INVALID);
		}
		return match;
	}

	/**
	 * This method applies some validation to guarantee that the stones can be moved successfully.
	 * @param moveDto
	 */
	private void validateMoveDto(MoveDTO moveDto) {
		if (isNull(moveDto) || isNull(moveDto.pitId())){
			LOGGER.error("Pit id cannot be null.");
			throw new IllegalArgumentException(ERROR_MESSAGE_PIT_ID_NULL);
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
		LOGGER.debug("Applying final steps to match {}", match.getId());
		Player movePlayer = movePit.getPlayer();
		this.stealOppositeStones(pits, lastPit, movePlayer);
		Map<Player, Integer> playerStones = pits
				.stream()
				.filter(p -> p.getPitOrder() != BIG_PIT_ORDER)
				.collect(Collectors.groupingBy(Pit::getPlayer, Collectors.summingInt(Pit::getStones)));
		boolean isOutOfStones = playerStones.values().stream().anyMatch(t -> t.equals(0));
		if (isOutOfStones) {
			return this.finishMatch(match);
		}
		if (lastPit.getPitOrder() == BIG_PIT_ORDER) {
			LOGGER.info("Last pit is {}. Returning without changing the turn to the opposite player.", lastPit.getId());
			return match;
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
		LOGGER.info("Changing player {} turn from {} to {}", movePlayer.getId(), movePlayer.isTurn(), !movePlayer.isTurn());
		movePlayer.setTurn(false);
		Optional<Player> otherPlayer = match.getPlayers().stream().filter(p -> !(p.getId().equals(movePlayer.getId()))).findFirst();
		otherPlayer.ifPresent(op -> op.setTurn(true));
		return match;
	}

	/**
	 * Gets all the stones from small pits and adds it to the big pit. Also, set the match status to {@link com.bol.kahala.entity.MatchStatus}.FINISHED.
	 * @param match
	 * @return
	 */
	private Match finishMatch(Match match) {
		LOGGER.info("Finishing match...");
		match.getPlayers()
			.forEach(player -> {
				player.setTurn(false);
				Optional<Pit> bigPit = player.getPits().stream().filter(p -> p.getPitOrder().equals(BIG_PIT_ORDER)).findFirst();
				player
					.getPits()
					.forEach(pit -> {
						if (!pit.getPitOrder().equals(BIG_PIT_ORDER)){
							LOGGER.debug("Adding {} stones from small pit {} to big", pit.getStones(), pit.getId());
							bigPit.ifPresent(bp -> bp.addToStones(pit.getStones()));
							pit.setStones(0);
						}
					});
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
		LOGGER.debug("Validating the possibility to steal the opposite pit stones");
		final Player lastPitPlayer = lastPit.getPlayer();
		if (!hasLandedInEmptyPit(lastPit, movePlayer, lastPitPlayer)) {
			LOGGER.debug("Last stone didn't land on empty pit. Returning...");
			return;
		}
		Integer oppositePitOrder = (SMALL_PIT_MAX_ORDER + 1) - lastPit.getPitOrder();
		Optional<Pit> oppositePit = pits
				.stream()
				.filter(p -> p.getPitOrder().equals(oppositePitOrder) && !(p.getPlayer().equals(movePlayer)))
				.findFirst();
		if (oppositePit.isEmpty() || oppositePit.get().getStones().equals(0)) {
			LOGGER.debug("Opposite pit has no stones. Returning...");
			return;
		}
		Optional<Pit> bigPit = pits
				.stream()
				.filter(p -> p.getPitOrder().equals(BIG_PIT_ORDER) && p.getPlayer().equals(movePlayer))
				.findFirst();
		Consumer<Pit> addStonesConsumer = p -> p.addToStones(lastPit.getStones() + oppositePit.get().getStones());
		bigPit.ifPresentOrElse(addStonesConsumer, () -> {
			LOGGER.error("Error adding stones to big pit.");
			throw new KahalaException("Big pit not found.");
		});
		LOGGER.info("Stealing opposite pit stones. lastPit {}. oppositePit {}", lastPit.getId(), oppositePit.get().getId());
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
		return lastPitPlayer.equals(movePlayer) && lastPit.getStones().equals(1) && !lastPit.getPitOrder().equals(7);
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
			LOGGER.info("{} stones left to move. Pit index {}", stones, pitIndex);
			pitIndex++;
			if (pitIndex == pits.size()) {
				pitIndex = 0;
			}
			Pit pit = pits.get(pitIndex);
			if (pit.getPitOrder() == BIG_PIT_ORDER && !(movePit.getPlayer().equals(pit.getPlayer()))) {
				LOGGER.debug("Opposite big pit. Ignoring...");
				continue;
			}
			pit.addToStones(1);
			lastPit = pit;
			stones--;
			LOGGER.info("Pit {} has now {} stones.", pit.getId(), pit.getStones());
		}
		return lastPit;
	}

	/**
	 * Creates the smallPits ArrayList ordered with STONES_DEFAULT_VALUE 
	 * @param player
	 */
	private void createPits(Player player) {
		LOGGER.info("Creating small pits for player {}", player.getName());
		for (int index = 1; index <= SMALL_PIT_MAX_ORDER; index++) {
			LOGGER.debug("Creating small pit with order {} for player {}", index, player.getName());
			player.addToPits(Pit.builder()
					.stones(SMALL_PIT_DEFAULT_VALUE)
					.player(player)
					.pitOrder(index)
					.build());
		}
		LOGGER.debug("Creating big pit for player {}", player.getName());
		player.addToPits(Pit.builder()
					.stones(BIG_PIT_DEFAULT_VALUE)
					.player(player)
					.pitOrder(BIG_PIT_ORDER)
					.build());
	}

}
