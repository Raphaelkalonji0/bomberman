package fr.epita.assistants.jws.domain.entity;

import fr.epita.assistants.jws.data.model.GameModel;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
public class PlayerEntity {
    public Long id;
    public String name;
    public int lives;
    public int posX;
    public int posY;
}
