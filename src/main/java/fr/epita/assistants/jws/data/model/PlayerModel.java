package fr.epita.assistants.jws.data.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "player")
@AllArgsConstructor @NoArgsConstructor @With @ToString
public class PlayerModel extends PanacheEntityBase {
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public @Column(name = "last_bomb") LocalDateTime lastBomb;
    public @Column(name = "last_movement") LocalDateTime lastMovement;
    public @Column(name = "lives") int lives;
    public @Column(name = "name") String name;
    public @Column(name = "posx") int posx;
    public @Column(name = "posy") int posy;
    public @Column(name = "position") int position;
    //public @Column(name = "game_id") int gameId;
    public @ManyToOne GameModel game;

    //getter and setter
    public Long getId() {
        return this.id;
    }

    public LocalDateTime getLastBomb() {
        return this.lastBomb;
    }

    public LocalDateTime getLastMovement() {
        return this.lastMovement;
    }

    public int getLives() {
        return this.lives;
    }

    public String getName() {
        return this.name;
    }

    public int getPosx() {
        return this.posx;
    }

    public int getPosy() {
        return this.posy;
    }

    public int getPosition() {
        return this.position;
    }

    public GameModel getGame() {
        return this.game;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastBomb(LocalDateTime lastBomb) {
        this.lastBomb = lastBomb;
    }

    public void setLastMovement(LocalDateTime lastMovement) {
        this.lastMovement = lastMovement;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosx(int posx) {
        this.posx = posx;
    }

    public void setPosy(int posy) {
        this.posy = posy;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setGame(GameModel game) {
        this.game = game;
    }

}
