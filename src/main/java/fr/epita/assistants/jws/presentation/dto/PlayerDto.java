package fr.epita.assistants.jws.presentation.dto;

import java.util.*;

public class PlayerDto {
    private Long id;
    private int lives;
    private String name;
    private double posx;
    private double posy;

    // Getters and setters

    public PlayerDto(Long id, int lives, String name, double posx, double posy) {
        this.id = id;
        this.lives = lives;
        this.name = name;
        this.posx = posx;
        this.posy = posy;
    }

    public PlayerDto() {
    }

    public Long getId() {
        return this.id;
    }

    public int getLives() {
        return this.lives;
    }

    public String getName() {
        return this.name;
    }

    public double getPosx() {
        return this.posx;
    }

    public double getPosy() {
        return this.posy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosx(double posx) {
        this.posx = posx;
    }

    public void setPosy(double posy) {
        this.posy = posy;
    }

}


