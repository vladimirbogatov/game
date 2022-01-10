package com.game.service;

import com.game.entity.Player;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Map;

public interface PlayerService {


    /**
     * Подсчёт количества игроков по условию
     * @return количество имеющихся игороков
     */
    Integer countPlayers();

    /**
     * Создаёт игрока. Мы не может создать игорока, если:
     * - указаны не все параметры из Data params (id, name, title, race, profession, birthday, experience, level, untilNextLevel (кроме banned)
     * - длина name или title превышает указанные ограничения
     * - опыт находится вне заданных пределов
     * - birthday<0
     * - дата регистрации находится вне заданных пределов
     * @param player
     * @return если создали - true, если не создали - false
     */
    boolean createPlayer(Player player);

    /**
     * Возвращает игрока по его id
     * если игрое не найден - отвечаем с ошибкой 404
     * если значение id не валидное - отвечаем с ошибкой 400
     * @param id
     * @return возвращаем игрока
     */
    Player getPlayerById (Long id);

    /**
     * Обновляем данные по игроку в соответствии с переданными значениями
     * Обновлять нужно только те параметры, котоыре не null
     * Если игрок не найден в БД - 404
     * Если id не валидное - 400
     * @param player - здесь храняться новые параметры
     * @param id - id игрока, которого нужно обновить
     * @return если обновили - tru, если не обновили - false
     */
    boolean updatePlayer (Player player, Long id);

    /**
     * Удалить игрока по его id
     *Если игрок не найден в БД - 404
     *Если id не валидное - 400
     * @param id - id игрока, которого нужно удалить
     * @return если удалили - tru, если не удалили - false
     */
    boolean delPlayerById(Long id);

    /**
     * Выборка игроков по условию.
     * @param allParams - Map с условиями выбора игроков
     * @return список игроков, котоыре мы выбрали
     */
    List <Player> getPlayersByParams(Map<String, String> allParams);
}
