import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import NewMatch from './NewMatch';
import Board from './Board';

function App() {
    return (
        <Router>
            <Switch>
                <Route path="/" exact component={NewMatch} />
                <Route path="/board" component={Board} />
            </Switch>
        </Router>
    );
}

export default App;