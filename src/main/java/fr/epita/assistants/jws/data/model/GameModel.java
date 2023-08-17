package fr.epita.assistants.jws.data.model;


import fr.epita.assistants.jws.utils.GameState;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity @Table(name = "game")
@AllArgsConstructor @NoArgsConstructor @With @ToString
public class GameModel extends PanacheEntityBase {
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public @Column(name = "start_time") LocalDateTime startTime;
    public @Column(name = "state") GameState state;
    public @OneToMany(cascade = CascadeType.ALL) List<PlayerModel> players;
    public @ElementCollection List<String> map;

    //getter and setter
    public Long getId() {
        return this.id;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public GameState getState() {
        return this.state;
    }

    public List<PlayerModel> getPlayers() {
        return this.players;
    }

    public List<String> getMap() {
        return this.map;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setPlayers(List<PlayerModel> players) {
        this.players = players;
    }

    public void setMap(List<String> map) {
        this.map = map;
    }

    public void addPlayer(PlayerModel player) {
        this.players.add(player);
    }

    public void removePlayer(PlayerModel player) {
        this.players.remove(player);
    }

    public void setPlayer(PlayerModel player) {
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getId().equals(player.getId())) {
                this.players.set(i, player);
                return;
            }
        }
    }

    public PlayerModel getPlayer(Long id) {
        for (PlayerModel player : this.players) {
            if (player.getId().equals(id))
                return player;
        }
        return null;
    }

    public void setPlayerPos(Long id, int posx, int posy) {
        for (PlayerModel player : this.players) {
            if (player.getId().equals(id)) {
                player.setPosx(posx);
                player.setPosy(posy);
                return;
            }
        }
    }

    public void setPlayerPos(Long id, int pos) {
        for (PlayerModel player : this.players) {
            if (player.getId().equals(id)) {
                player.setPosition(pos);
                return;
            }
        }
    }

    public void setPlayerLastBomb(Long id, LocalDateTime lastBomb) {
        for (PlayerModel player : this.players) {
            if (player.getId().equals(id)) {
                player.setLastBomb(lastBomb);
                return;
            }
        }
    }

    public void setPlayerLastMovement(Long id, LocalDateTime lastMovement) {
        for (PlayerModel player : this.players) {
            if (player.getId().equals(id)) {
                player.setLastMovement(lastMovement);
                return;
            }
        }
    }

    
}


