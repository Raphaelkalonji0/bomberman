package fr.epita.assistants.jws.presentation.mapper.impl;

import fr.epita.assistants.jws.presentation.dto.GameDto;
import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.presentation.mapper.GameMapper;
import fr.epita.assistants.jws.presentation.dto.PlayerDto;
import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.presentation.mapper.PlayerMapper;

import java.util.stream.Collectors;

public class PlayerMapperImpl implements PlayerMapper {
    @Override
    public PlayerDto modelToDto(PlayerModel model) {
        PlayerDto dto = new PlayerDto();
        dto.setId(model.getId());
        dto.setLives(model.getLives());
        dto.setName(model.getName());
        dto.setPosx((double)model.getPosx());
        dto.setPosy((double)model.getPosy());
        return dto;
    }

    @Override
    public PlayerModel dtoToModel(PlayerDto dto) {
        PlayerModel model = new PlayerModel();
        model.setId(dto.getId());
        model.setLives(dto.getLives());
        model.setName(dto.getName());
        model.setPosx((int)dto.getPosx());
        model.setPosy((int)dto.getPosy());
        return model;
    }
}
