package fr.epita.assistants.jws.presentation.rest;

import fr.epita.assistants.jws.domain.entity.GameEntity;
import fr.epita.assistants.jws.domain.service.GameService;

import fr.epita.assistants.jws.presentation.dto.GameDto;
import fr.epita.assistants.jws.presentation.dto.PlayerDto;

import fr.epita.assistants.jws.presentation.rest.request.CoordsRequest;
import fr.epita.assistants.jws.presentation.rest.request.GameRequest;
import fr.epita.assistants.jws.presentation.rest.response.GameDetailResponse;
import fr.epita.assistants.jws.presentation.rest.response.GameListResponse;
import fr.epita.assistants.jws.utils.GameState;
import lombok.Value;
import lombok.With;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.management.PlatformLoggingMXBean;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;


@Path("/")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameEndpoint {
    @Inject
    GameService service;

    @ConfigProperty(name = "JWS_TICK_DURATION") int tickDuration;
    @ConfigProperty(name = "JWS_DELAY_BOMB") int delayBomb;

    @GET
    @Path("/games")
    public List<GameListResponse> getGames() {
        var gameSet = service.getGameSet();
        if (gameSet == null)
            return new ArrayList<>();

        List<GameListResponse> glr = new ArrayList<>();

        for (var gameEntity:gameSet) {
            glr.add(new GameListResponse(gameEntity.id,
                                            (long) gameEntity.players.size(),
                                            gameEntity.state));
        }

        return glr;
    }

    @POST
    @Path("/games")
    public Response createGame(GameRequest request) {
        if (request == null || request.name == null)
            return Response.status(400).build();

        // createGameModel now returns a DTO instead of a Model
        var gameDto = service.createGameDto(request.name);

        // No direct usage of Model classes here; use the DTO instead
        GameDetailResponse game = new GameDetailResponse(
                LocalDateTime.now(),
                gameDto.getState(),
                new ArrayList<>(),
                List.copyOf(gameDto.getMap()),
                gameDto.getId());

        gameDto.getPlayers().forEach(i -> game.getPlayers().add(new GameDetailResponse.Player(
                        i.getId(), i.getName(), Long.valueOf(i.getLives()), Long.valueOf((long)i.getPosx()), Long.valueOf((long)i.getPosy()))));

        return Response.ok(game).build();
    } 

    @GET
    @Path("/games/{gameId}")
    public Response getGameInfo(@PathParam("gameId") String gameId) {
        if (gameId == null || gameId.isEmpty())
            return Response.status(400).build();

        int id = Integer.parseInt(gameId);
        var gameEntity = service.gameById(id);
        if (gameEntity == null)
            return Response.status(404).build();

        GameDetailResponse res = new GameDetailResponse(
                LocalDateTime.now(),
                gameEntity.state,
                new ArrayList<GameDetailResponse.Player>(),
                List.copyOf(gameEntity.map),
                gameEntity.id
        );

        gameEntity.players.forEach(i -> res.players.add(new GameDetailResponse.Player(i.id,
                i.name, (long) i.lives, (long) i.posX, (long) i.posY)));

        return Response.ok(res).build();
    }

    @POST
    @Path("/games/{gameId}")
    public Response joinGame(@PathParam("gameId") String gameId, GameRequest request) {
        if (gameId == null || gameId.isEmpty())
            return Response.status(400).build();

        if (request == null || request.name == null || request.name.isEmpty())
            return Response.status(400).build();

        int id = Integer.parseInt(gameId);

        GameEntity gameEntity;

        try {
            gameEntity = service.addPlayer(id, request.name);
        }
        catch (BadRequestException bre) {
            return Response.status(400).build();
        }
        catch (NoSuchElementException e) {
            return Response.status(404).build();
        }


        GameDetailResponse res = new GameDetailResponse(
                gameEntity.startTime,
                gameEntity.state,
                new ArrayList<GameDetailResponse.Player>(),
                List.copyOf(gameEntity.map),
                gameEntity.id
        );

        gameEntity.players.forEach(i -> res.players.add(new GameDetailResponse.Player(i.id,
                i.name, (long) i.lives, (long) i.posX, (long) i.posY)));

        return Response.ok(res).build();
    }

    @PATCH
    @Path("/games/{gameId}/start")
    public Response startGame(@PathParam("gameId") String gameId) {
        if (gameId == null || gameId.isEmpty())
        return Response.status(400).build();

        int id = Integer.parseInt(gameId);

        GameEntity gameEntity;

        try {
            gameEntity = service.startGame(id);
        }
        catch (NoSuchElementException e) {
            return Response.status(404).build();
        }

        GameDetailResponse res = new GameDetailResponse(
                LocalDateTime.now(),
                gameEntity.state,
                new ArrayList<GameDetailResponse.Player>(),
                List.copyOf(gameEntity.map),
                gameEntity.id
        );

        gameEntity.players.forEach(i -> res.players.add(new GameDetailResponse.Player(i.id,
                i.name, (long) i.lives, (long) i.posX, (long) i.posY)));

        return Response.ok(res).build();
    }

    @POST
    @Path("/games/{gameId}/players/{playerId}/bomb")
    public Response plantBomb(@PathParam("gameId") String gameId,
                              @PathParam("playerId") String playerId,
                              CoordsRequest request) {
        if (gameId == null || gameId.isEmpty() ||
                playerId == null || playerId.isEmpty() || request == null)
            return Response.status(400).build();


        int intGameId = Integer.parseInt(gameId);
        int intPlayerId = Integer.parseInt(playerId);

        GameEntity gameEntity;

        try {
            gameEntity = service.plantBomb(intGameId, intPlayerId, request.posX, request.posY);
        }
        catch (BadRequestException bre) {
            return Response.status(400).build();
        }
        catch (NoSuchElementException e) {
            return Response.status(404).build();
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                service.explode(Math.toIntExact(gameEntity.id), request.posX, request.posY);
            }
        }, tickDuration * delayBomb);

        GameDetailResponse game = new GameDetailResponse(gameEntity.startTime, gameEntity.state, new ArrayList<>(), List.copyOf(gameEntity.map), gameEntity.id);
        gameEntity.players.forEach(p -> game.players.add(new GameDetailResponse.Player(p.id, p.name, (long) p.lives, (long) p.posX, (long) p.posY)));

        return Response.ok(game).build();
    }

    @POST
    @Path("/games/{gameId}/players/{playerId}/move")
    public Response move(@PathParam("gameId") String gameId,
                         @PathParam("playerId") String playerId,
                         CoordsRequest request) {
        if (gameId == null || gameId.isEmpty() ||
                playerId == null || playerId.isEmpty() || request == null)
            return Response.status(400).build();


        int intGameId = Integer.parseInt(gameId);
        int intPlayerId = Integer.parseInt(playerId);

        GameEntity gameEntity;

        try {
            gameEntity = service.move(intGameId, intPlayerId, request.posX, request.posY);
        }
        catch (BadRequestException bre) {
            return Response.status(400).build();
        }
        catch (NoSuchElementException e) {
            return Response.status(404).build();
        }

        return Response.ok(gameEntity).build();
    }


    @Value @With public static class Game {
        public Long id;
        public LocalDateTime startTime;
        public GameState state;
    }
}
