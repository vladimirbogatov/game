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

import javax.persistence.PostUpdate;
import java.awt.print.Pageable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable(name = "id") String id) {
        //Проверка id на валидность
        if (!isValidId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Player newPlayer = playerService.getPlayerById(Long.parseLong(id));
        return Objects.nonNull(newPlayer) ?
                new ResponseEntity<>(newPlayer, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/rest/players/count")
    public ResponseEntity<Integer> count(@RequestParam(required = false) Map<String, String> allParams) {
        return new ResponseEntity<>(playerService.countPlayers(allParams), HttpStatus.OK);
    }

    @PostMapping("/rest/players")
    public ResponseEntity<Player> create(@RequestBody Player player) {
        try {
            Player newPlayer = playerService.createPlayer(player);
            return Objects.nonNull(newPlayer) ? new ResponseEntity<>(newPlayer, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping ("/rest/players/{id}")
    public ResponseEntity<?> delete (@PathVariable(name = "id") String id){
        //Проверка id на валидность
        if (!isValidId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean result = playerService.delPlayerById(Long.parseLong(id));
        return  result ?
                new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PostMapping("/rest/players/{id}")
    public ResponseEntity<Player> update(@PathVariable(name = "id") String id,
                                    @RequestBody (required = false) Player player) {
        if (isValidId(id) ) {
            try {
                Player newPlayer = playerService.updatePlayer(player, Long.parseLong(id));
                return Objects.nonNull(newPlayer) ?
                        new ResponseEntity<>(newPlayer, HttpStatus.OK) :
                        new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Проверка ID на валидность в соответствии с условиями задачи
     * @param id
     * @return
     */
    boolean isValidId(String id) {
        Long idLong;
        try {
            idLong = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return false;
        }
        // Не может быть меньше или равно 0
        if (idLong <= 0) {
            return false;
        }
        return true;
    }
}
