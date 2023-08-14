import React, { useState } from 'react';
import axios from 'axios';
import { useLocation } from "react-router-dom";

function Board() {

	const location = useLocation();
	const props = location.state;
	const [match, setMatch] = useState(props.match);
	const makeMove = async (pitId) => {
        try {
            const response = await axios.put('/match', {
                matchId: match.id,
                pitId: pitId,
            });
            console.log(response.data);
			setMatch(response.data);
        } catch (error) {
            console.error('Error making move:', error);
        }
    };
	if (!match){
		return <p>Loading...</p>
	}
	const isMatchFinished = match.status === 'FINISHED';

    const winningPlayer = match.players.reduce((maxPlayer, player) => {
        const lastPit = player.pits.find(pit => pit.pitOrder === 7);
        if (!maxPlayer || (lastPit && lastPit.stones > maxPlayer.pits.find(pit => pit.pitOrder === 7).stones)) {
            return player;
        }
        return maxPlayer;
    }, null);
    return (
        <div className="page-container">
            <h2>Kahala</h2>
            {isMatchFinished && (
                <p className="winner-message">Match is over! {winningPlayer.name} is the winner!</p>
            )}
            {match.players.map((player, index) => (
                <div key={player.id} className={`pit-container ${player.turn ? 'turn' : ''}`}>
                    <h3>{player.name}</h3>
                    {player.pits
                    	.sort((pit1, pit2) => index === 0 ? pit2.pitOrder - pit1.pitOrder : pit1.pitOrder - pit2.pitOrder)
                    	.map((pit, pitIndex) => (
						<div key={pit.id}
						     className={`pit ${(index === 0 && pitIndex === 0) ||  (index > 0 && pitIndex === player.pits.length - 1)? 'big-pit' : ''}`}
						     onClick={() => makeMove(pit.id)}>
						    <div className="stones">{pit.stones}</div>
						</div>
                    ))}
                    <hr />
                </div>
            ))}
        </div>
    );
}

export default Board;