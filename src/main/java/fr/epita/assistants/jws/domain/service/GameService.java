package fr.epita.assistants.jws.domain.service;

import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.data.repository.GameRepository;
import fr.epita.assistants.jws.data.repository.PlayerRepository;
import fr.epita.assistants.jws.domain.entity.GameEntity;
import fr.epita.assistants.jws.domain.entity.PlayerEntity;

import fr.epita.assistants.jws.presentation.dto.GameDto;
import fr.epita.assistants.jws.presentation.dto.PlayerDto;

import fr.epita.assistants.jws.presentation.rest.GameEndpoint;
import fr.epita.assistants.jws.utils.GameState;
import fr.epita.assistants.jws.utils.MapConverter;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ApplicationScoped
public class GameService {
    @Inject
    GameRepository gameRepository;

    @Inject
    PlayerRepository playerRepository;

    @Inject
    GameService service;

    @ConfigProperty(name = "JWS_MAP_PATH") String mapPath;

    public List<GameEntity> getGameSet() {
        List<GameEntity> res = new ArrayList<>();

        gameRepository.findAll().stream().map(game -> {
            var tmp = new GameEntity(game.id, game.startTime, game.state, new ArrayList<>(), List.copyOf(game.map));
            for (var p: game.players) {
                tmp.players.add(new PlayerEntity(p.id, p.name, p.lives, p.posx, p.posy));
            }
            res.add(tmp);
            return game;
        });
        return res;
        /*gameModels.forEach(i -> {
            var gameEntity = new GameEntity(i.id, i.startTime, i.state, new ArrayList<>(), List.copyOf(i.map));
            for (var player: i.players) {
                gameEntity.players.add(new PlayerEntity(player.id, player.name, player.lives, player.posx, player.posy));
            }
            res.add(gameEntity);
        });*/
    }

    public List<String> mapParser(final String path) {
        List<String> map = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;
            while ((line = br.readLine()) != null)
                map.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Transactional //Pour pouvoir modif la db
    public GameEntity createGame(String playerName) {
        // On cree le modele
        GameModel gameModel = new GameModel()
                .withPlayers(new ArrayList<>())
                .withStartTime(LocalDateTime.now())
                .withState(GameState.STARTING)
                .withMap(mapParser(mapPath));

        PlayerModel playerModel = new PlayerModel()
                .withName(playerName)
                .withLives(3)
                .withPosx(1)
                .withPosy(1)
                .withGame(gameModel);

        //playerRepository.persist(playerModel);

        gameModel.players.add(playerModel);

        // On l'ajoute a la base de donnes
        gameRepository.persist(gameModel);

        // On renvoie une entitee qui sera convertie en  pour pouvoir l'afficher en Json
        return new GameEntity(gameModel.id, LocalDateTime.now(), GameState.STARTING, new ArrayList<>(), new ArrayList<>());
    }

    @Transactional //Pour pouvoir modif la db
    public GameModel createGameModel(String playerName) {
        // On cree le modele
        GameModel gameModel = new GameModel()
                .withPlayers(new ArrayList<>())
                .withStartTime(LocalDateTime.now())
                .withState(GameState.STARTING)
                .withMap(mapParser(mapPath));

        PlayerModel playerModel = new PlayerModel()
                .withName(playerName)
                .withLives(3)
                .withPosx(1)
                .withPosy(1)
                .withGame(gameModel);

        //playerRepository.persist(playerModel);

        gameModel.players.add(playerModel);

        // On l'ajoute a la base de donnes
        playerRepository.persist(playerModel);
        gameRepository.persist(gameModel);

        return gameModel;
    }

    @Transactional
    public GameEntity gameById(int gameId)
    {
        //var opt = gameRepository.listAll().stream().filter(i -> i.id == gameId).findFirst();
        var opt = gameRepository.findByIdOptional((long) gameId);
        if (opt.isEmpty())
            return null;
        var gameModel = opt.get();
        var gameEntity = new GameEntity(gameModel.id, gameModel.startTime, gameModel.state, new ArrayList<>(), List.copyOf(gameModel.map));
        for (var player: gameModel.players) {
            gameEntity.players.add(new PlayerEntity(player.id, player.name, player.lives, player.posx, player.posy));
        }
        return gameEntity;
    }

    public PlayerModel playerById(GameModel gameModel, int playerId) {
        var opt = gameModel.players.stream().filter(p -> p.id == playerId).findFirst();
        if (opt.isEmpty())
            return null;
        return opt.get();
    }

    @Transactional
    public GameEntity addPlayer(int id, String playerName) {
        //var opt = gameRepository.listAll().stream().filter(i -> i.id == id).findFirst();
        var opt = gameRepository.findByIdOptional((long) id);
        if (opt.isEmpty())
            throw new NoSuchElementException();
        var gameModel = opt.get();

        PlayerModel playerModel = new PlayerModel()
                .withName(playerName)
                .withLives(3)
                .withPosx(1)
                .withPosy(1)
                .withGame(gameModel);


        gameModel.players.add(playerModel);
        playerRepository.persist(playerModel);

        var gameEntity = service.gameById(id);

        return gameEntity;

    }

    @Transactional
    public GameEntity startGame(int id) {
        //var opt = gameRepository.listAll().stream().filter(i -> i.id == id).findFirst();
        var opt = gameRepository.findByIdOptional((long) id);
        if (opt.isEmpty())
            throw new NoSuchElementException();
        var gameModel = opt.get();

        gameModel.state = GameState.RUNNING;
        //gameRepository.persist(gameModel);

        var gameEntity = gameById(id);

        return gameEntity;
    }

    @Transactional
    public GameEntity plantBomb(int gameId, int playerId, int posX, int posY) {
        //var opt = gameRepository.listAll().stream().filter(i -> i.id == gameId).findFirst();
        var opt = gameRepository.findByIdOptional((long) gameId);
        if (opt.isEmpty())
            throw new NoSuchElementException();
        var gameModel = opt.get();

        var playerModel = service.playerById(gameModel, playerId);
        if (playerModel == null)
            throw new NoSuchElementException();

        if (playerModel.posx != posX || playerModel.posy != posY)
            throw new BadRequestException();

        playerModel.lastBomb = LocalDateTime.now();

        MapConverter mapConverter = new MapConverter();

        var newMap = mapConverter.toFullMap(gameModel.map);
        var line = new StringBuilder(newMap.get(posY));
        line.setCharAt(posX, 'B');
        newMap.set(posY, line.toString());

        var newRLEMap = mapConverter.toRle(newMap);
        gameModel.map = List.copyOf(newRLEMap);

        GameEntity gameEntity = new GameEntity(gameModel.id, gameModel.startTime, gameModel.state, new ArrayList<>(), List.copyOf(gameModel.map));

        gameModel.players.forEach(p -> gameEntity.players.add(new PlayerEntity(p.id, p.name, p.lives, p.posx, p.posy)));

        return gameEntity;
    }

    @Transactional
    public void explode(int gameId, int x, int y) {
        var opt = gameRepository.findByIdOptional((long) gameId);
        var gameModel = opt.get();

        gameModel.players.forEach(p -> {
            if (p.posx == x && (p.posy == y - 1 || p.posy == y + 1))
                p.lives--;
            if (p.posy == y && (p.posx == x - 1 || p.posx == x + 1))
                p.lives--;
            if (p.posy == y && p.posx == x)
                p.lives--;
        });
        if (!gameModel.players.stream().anyMatch(p -> p.lives > 0))
            gameModel.state = GameState.FINISHED;

        MapConverter mapConverter = new MapConverter();

        var fullMap = mapConverter.toFullMap(gameModel.map);
        // Vertical checks
        var line = new StringBuilder(fullMap.get(y + 1));
        if (line.charAt(x) == 'W')
            line.setCharAt(x, 'G');
        fullMap.set(y + 1, line.toString());

        line = new StringBuilder(fullMap.get(y - 1));
        if (line.charAt(x) == 'W')
            line.setCharAt(x, 'G');
        fullMap.set(y - 1, line.toString());

        //Horizontal checks
        line = new StringBuilder(fullMap.get(y));
        if (line.charAt(x + 1) == 'W')
            line.setCharAt(x + 1, 'G');
        if (line.charAt(x - 1) == 'W')
            line.setCharAt(x - 1, 'G');
        line.setCharAt(x, 'G');
        fullMap.set(y, line.toString());

        var rleMap = mapConverter.toRle(fullMap);

        gameModel.map = rleMap;
    }

    private boolean isMoveLegal(int a, int b, int x, int y, int gameId) {
        //var opt = gameRepository.listAll().stream().filter(i -> i.id == gameId).findFirst();
        var opt = gameRepository.findByIdOptional((long) gameId);
        if (opt.isEmpty())
            return false;
        var gameModel = opt.get();

        MapConverter mapConverter = new MapConverter();
        var fullMap = mapConverter.toFullMap(gameModel.map);
        var line = fullMap.get(y);
        if (line.charAt(x) != 'G')
            return false;

        int diffX = a - x;
        int diffY = b - y;
        if (diffX > 1 || diffY > 1)
            return false;
        return ((diffX != 0) ^ (diffY != 0));
    }

    @Transactional
    public GameEntity move(int gameId, int playerId, int posX, int posY) {
        //var opt = gameRepository.listAll().stream().filter(i -> i.id == gameId).findFirst();
        var opt = gameRepository.findByIdOptional((long) gameId);
        if (opt.isEmpty())
            throw new NoSuchElementException();
        var gameModel = opt.get();
        var optP = gameModel.players.stream().filter(p -> p.id == (long) playerId).findFirst();
        if (optP.isEmpty())
            throw new NoSuchElementException();
        var playerModel = optP.get();

        if (gameModel.state != GameState.RUNNING || playerModel.lives == 0)
            throw new BadRequestException();

        if (!(isMoveLegal(playerModel.posx, playerModel.posy, posX, posY, gameId)))
            throw new BadRequestException();

        playerModel.posx = posX;
        playerModel.posy = posY;

        var gameEntity = new GameEntity(gameModel.id, gameModel.startTime, gameModel.state, new ArrayList<>(), List.copyOf(gameModel.map));
        gameModel.players.forEach(p -> gameEntity.players.add(new PlayerEntity(p.id, p.name, p.lives, p.posx, p.posy)));
        return gameEntity;
    }


    public GameDto createGameDto(String name) {
        // Creating a new GameModel object
        GameModel gameModel = new GameModel();
       // gameModel.setName(name);
        gameModel.setState(GameState.STARTING); // Assuming a GameState enum

        // Save the game to the database
        GameModel savedGame = gameRepository.save(gameModel);

        // Return a DTO for the presentation layer
        return mapToDto(savedGame);
    }

    private GameDto mapToDto(GameModel gameModel) {
        GameDto gameDto = new GameDto();

        // Map all the fields from gameModel to gameDto
        gameDto.setId(gameModel.getId());
        gameDto.setState(gameModel.getState());
        gameDto.setMap(new ArrayList<>(gameModel.getMap()));
        
        // Map player models to player DTOs
        List<PlayerDto> playerDtos = gameModel.getPlayers().stream()
                .map(this::mapPlayerToDto)
                .collect(Collectors.toList());

        gameDto.setPlayers(playerDtos);

        return gameDto;
    }

    private PlayerDto mapPlayerToDto(PlayerModel playerModel) {
        PlayerDto playerDto = new PlayerDto();

        // Map all the fields from playerModel to playerDto
        playerDto.setId(playerModel.getId());
        playerDto.setName(playerModel.getName());
        playerDto.setLives(playerModel.getLives());
        playerDto.setPosx(playerModel.getPosx());
        playerDto.setPosy(playerModel.getPosy());

        return playerDto;
    }


}

