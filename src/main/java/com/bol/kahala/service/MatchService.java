package com.bol.kahala.service;

import static com.bol.kahala.entity.MatchStatus.ACTIVE;
import static com.bol.kahala.entity.MatchStatus.FINISHED;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bol.kahala.dto.MoveDTO;
import com.bol.kahala.dto.NewMatchRequestDTO;
import com.bol.kahala.entity.Match;
import com.bol.kahala.entity.MatchStatus;
import com.bol.kahala.entity.Pit;
import com.bol.kahala.entity.Player;
import com.bol.kahala.repository.MatchRepository;

import jakarta.transaction.Transactional;

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
	 * This method get the pits from database and moves the stones from one pit to each of the following pits.
	 * 
	 * @param moveDto
	 * @return
	 */
	@Transactional
	public Match move(MoveDTO moveDto) {
		Match match = this.repository.findMatchByPitId(moveDto.pitId());
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
		if (lastPit.getPitOrder() == BIG_PIT_ORDER) {
			return match;
		}
		final Player lastPitPlayer = lastPit.getPlayer();
		Player movePlayer = movePit.getPlayer();
		if (lastPitPlayer.equals(movePlayer) && lastPit.getStones().equals(1)) {
			Integer reversePitOrder = (SMALL_PIT_MAX_ORDER + 1) - lastPit.getPitOrder();
			Pit reversePit = pits
			.stream()
			.filter(p -> p.getPitOrder() == reversePitOrder && !(p.getPlayer().equals(movePlayer)))
			.findFirst()
			.get();
			if (reversePit.getStones() > 0) {
				Pit bigPit = pits
				.stream()
				.filter(p -> p.getPitOrder() == BIG_PIT_ORDER && p.getPlayer().equals(movePlayer))
				.findFirst()
				.get();
				bigPit.setStones(bigPit.getStones() + lastPit.getStones() + reversePit.getStones());
				reversePit.setStones(0);
				lastPit.setStones(0);
			}
		}
		Map<Player, Integer> playerStones = pits
		 .stream()
		 .filter(p -> p.getPitOrder() != BIG_PIT_ORDER)
		 .collect(Collectors.groupingBy(Pit::getPlayer, Collectors.summingInt(Pit::getStones)));
		boolean isOutOfStones = playerStones.values().stream().anyMatch(t -> t.equals(0));
		if (isOutOfStones) {
			for (Map.Entry<Player, Integer> entry : playerStones.entrySet()) {
				Player player = entry.getKey();
				Integer sum = entry.getValue();
				if (sum == 0) {
					continue;
				}
				Pit bigPit = player.getPits().stream().filter(p -> p.getPitOrder().equals(BIG_PIT_ORDER)).findFirst().get();
				bigPit.setStones(bigPit.getStones() + sum);
			}
			match.setStatus(FINISHED);
			return match;
		}
		
		movePlayer.setTurn(false);
		Player otherPlayer = match.getPlayers().stream().filter(p -> !(p.getId().equals(movePlayer.getId()))).findFirst().get();
		otherPlayer.setTurn(true);
		return match;
	}

	/**
	 * This method sows the stones from the movePit to each one of the following pits, excluding only the opposite player's big.
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
