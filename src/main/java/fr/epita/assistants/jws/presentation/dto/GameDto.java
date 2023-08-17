package fr.epita.assistants.jws.presentation.dto;

import fr.epita.assistants.jws.utils.GameState;
import java.util.List;


public class GameDto {
    private Long id;
    private List<String> map;
    private List<PlayerDto> players;
    private GameState state;

    // Getters and setters

    public GameDto(Long id, List<String> map, List<PlayerDto> players, GameState state) {
        this.id = id;
        this.map = map;
        this.players = players;
        this.state = state;
    }

    public GameDto() {
    }

    public Long getId() {
        return this.id;
    }

    public List<String> getMap() {
        return this.map;
    }

    public List<PlayerDto> getPlayers() {
        return this.players;
    }

    public GameState getState() {
        return this.state;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMap(List<String> map) {
        this.map = map;
    }

    public void setPlayers(List<PlayerDto> players) {
        this.players = players;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}
