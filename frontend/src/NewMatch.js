import React, { useState } from 'react';
import axios from 'axios';
import { useHistory } from 'react-router-dom';

function NewMatch() {
	const history = useHistory();
	
    const [player1, setPlayer1] = useState('');
    const [player2, setPlayer2] = useState('');
    const [error, setError] = useState('');

	const startMatch = async () => {
        try {
            const response = await axios.post('/match', {
                player1,
                player2,
            });
            history.push({
				pathname: '/board',
				state: {
					match: response.data
				},
				});
        } catch (error) {
            console.error('Error starting match:', error);
            setError('An error occurred while starting the match.');
        }
    };

    return (
        <div className="page-container">
            <div className="title">
                <h2>Kahala</h2>
            </div>
            <div>
                {error && <p className="error-message">{error}</p>}
                <h3>New Match</h3>
                <input
                    type="text"
                    placeholder="Player 1"
                    value={player1}
                    onChange={(e) => setPlayer1(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Player 2"
                    value={player2}
                    onChange={(e) => setPlayer2(e.target.value)}
                />
                <button onClick={startMatch}>Start Match</button>
            </div>
        </div>
    );
}

export default NewMatch;
