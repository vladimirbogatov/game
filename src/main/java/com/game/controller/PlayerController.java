package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import com.game.service.PlayerServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.NumberUtils;
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
    public ResponseEntity <List<Player>> getPlayerList(@RequestParam(required = false) Map<String, String> allParams) {
        return new ResponseEntity<>(playerService.getPlayerList(allParams), HttpStatus.OK);
    }

    @GetMapping("/rest/players/count")
    public ResponseEntity<Integer> count(@RequestParam(required = false) Map<String, String> allParams) {

        return new ResponseEntity<>(playerService.countPlayers(allParams), HttpStatus.OK);
    }

    @PostMapping("/rest/players")
    public ResponseEntity<?> create(@RequestBody Player player) {
        if (!player.hasRequiredFields()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean result = playerService.createPlayer(player);
        return result ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping ("/rest/players/{id}")
    public ResponseEntity<?> delete (@PathVariable(name = "id") String id){
        //Проверка id на валидность
        if (!isValidId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return playerService.delPlayerById(Long.parseLong(id)) ?
                new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping("/rest/players/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") String id,
                                    @RequestParam (required = false) Map<String,String> allParam) {
        if (!isValidId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return playerService.updatePlayer(new Player(), Long.parseLong(id)) ?
                new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    boolean isValidId(String id) {
        Long idLong;
        try {
            idLong = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return false;
        }

        if (idLong < 0) {
            return false;
        }
        return true;
    }
}
