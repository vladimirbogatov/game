package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import com.game.service.PlayerServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Map;

@RestController
public class PlayerController {
    PlayerService playerService;


    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/rest/players")
    public List <Player> getPlayersByParams (@RequestParam(required = false) Map<String,String> allParams){
        return playerService.getPlayersByParams(allParams);
    }
}
