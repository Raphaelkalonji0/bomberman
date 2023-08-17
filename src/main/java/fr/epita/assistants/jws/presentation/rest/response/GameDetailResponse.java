package fr.epita.assistants.jws.presentation.rest.response;

import fr.epita.assistants.jws.utils.GameState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor @NoArgsConstructor
public class GameDetailResponse {
    public LocalDateTime startTime;
    public GameState state;
    public List<Player> players;
    public List<String> map;
    public Long id;

    @AllArgsConstructor
    public static class Player
    {
        public Long id;
        public String name;
        public Long lives;
        public Long posX;
        public Long posY;
    }

    public List<Player> getPlayers() {
        return this.players;
    }


    //TODO create PlayerDetailResponse class
    // and fix attributes to match Swagger
}
