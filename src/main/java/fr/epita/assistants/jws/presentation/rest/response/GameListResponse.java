    package fr.epita.assistants.jws.presentation.rest.response;

import fr.epita.assistants.jws.utils.GameState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class GameListResponse {
    public Long id;
    public Long players;
    public GameState state;
/* mais ouioui
    //constructor
    public GameListResponse(Long id, Long players, GameState state) {
        this.id = id;
        this.players = players;
        this.state = state;
    }*/
}
