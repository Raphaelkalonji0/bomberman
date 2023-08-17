package fr.epita.assistants.jws.presentation.mapper;

import fr.epita.assistants.jws.presentation.dto.GameDto;
import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.presentation.dto.PlayerDto;
import fr.epita.assistants.jws.data.model.PlayerModel;

public interface GameMapper {
    GameDto modelToDto(GameModel model);
    GameModel dtoToModel(GameDto dto);
}

