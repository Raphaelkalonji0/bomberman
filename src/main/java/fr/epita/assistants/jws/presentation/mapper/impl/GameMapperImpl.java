package fr.epita.assistants.jws.presentation.mapper.impl;

import fr.epita.assistants.jws.presentation.dto.GameDto;
import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.presentation.mapper.GameMapper;
import fr.epita.assistants.jws.presentation.dto.PlayerDto;
import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.presentation.mapper.PlayerMapper;

import java.util.stream.Collectors;

public class GameMapperImpl implements GameMapper {
    private PlayerMapper playerMapper = new PlayerMapperImpl();

    @Override
    public GameDto modelToDto(GameModel model) {
        GameDto dto = new GameDto();
        dto.setId(model.getId());
        dto.setMap(model.getMap());
        dto.setState(model.getState());
        dto.setPlayers(model.getPlayers().stream()
            .map(playerMapper::modelToDto)
            .collect(Collectors.toList()));
        return dto;
    }

    @Override
    public GameModel dtoToModel(GameDto dto) {
        GameModel model = new GameModel();
        model.setId(dto.getId());
        model.setMap(dto.getMap());
        model.setState(dto.getState());
        model.setPlayers(dto.getPlayers().stream()
            .map(playerMapper::dtoToModel)
            .collect(Collectors.toList()));
        return model;
    }
}


