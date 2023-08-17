package fr.epita.assistants.jws.presentation.rest.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.With;

import javax.swing.plaf.nimbus.State;
import java.time.LocalDateTime;

@Value @With
public class GameResponse {
    Long id;
    LocalDateTime startTime;
    State state;
    Long players;
}
