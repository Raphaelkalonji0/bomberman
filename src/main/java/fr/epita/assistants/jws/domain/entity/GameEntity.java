package fr.epita.assistants.jws.domain.entity;

import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.utils.GameState;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class GameEntity {
    public Long id;
    public LocalDateTime startTime;
    public GameState state;
    public List<PlayerEntity> players;
    public List<String> map;
}
