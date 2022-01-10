package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayersRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class PlayerServiceImp implements PlayerService {
    private final PlayersRepo playersRepo;
    public static final Logger LOGGER = LoggerFactory.getLogger(PlayerServiceImp.class);

    public PlayerServiceImp(PlayersRepo playersRepo) {
        this.playersRepo = playersRepo;
    }

    @Override
    public List<Player> getPlayersByParams(Map<String, String> allParams) {
        LOGGER.info("Метод получения игроков на основе переданных параметров");
        //Для начала, нет ли у нас указания по пейджингу
        //Если указаний нет - то задаём дефолтные значений
        Set<String> keys = allParams.keySet();
        Integer pageNumber = Integer.valueOf(DefaultValueOfPaging.PAGE_NUMBER.getFieldValue());// по определению
        Integer pageSize = Integer.valueOf(DefaultValueOfPaging.PAGE_SIZE.getFieldValue()); // по определению
        //Если какие-то указания есть в параметрах, то дефолтные значения меняем
        if (keys.contains(PlayerOrder.PAGE_NUMBER.getFieldName()) || keys.contains(PlayerOrder.PAGE_SIZE.getFieldName())) {
            String pageNumberString = allParams.get(PlayerOrder.PAGE_NUMBER.getFieldName());
            if (Objects.nonNull(pageNumberString)){
                try {
                    pageNumber = Integer.parseInt(pageNumberString);
                } catch (NumberFormatException e) {
                    LOGGER.error(String.format("Ошибка форматирования поля: %s. Значение остаётся default: %d",
                            PlayerOrder.PAGE_NUMBER.getFieldName(), DefaultValueOfPaging.PAGE_NUMBER.getFieldValue()));
                }
            }
            String pageSizeString = allParams.get(PlayerOrder.PAGE_SIZE.getFieldName());
            if (Objects.nonNull(pageSizeString)) {
                try {
                    pageSize = Integer.parseInt(pageSizeString);
                } catch (NumberFormatException e) {
                    LOGGER.error(String.format("Ошибка форматирования поля: %s. Значение остаётся default: %d",
                            PlayerOrder.PAGE_SIZE.getFieldName(),DefaultValueOfPaging.PAGE_SIZE.getFieldValue()));
                }
            }
        }
        // TODO: 09.01.2022 Тут нужно что-то решить с сортировкой
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return  playersRepo.findAll(new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery cq, CriteriaBuilder cb) {
                Predicate p = cb.conjunction();
                //будем искать все параметры из задания
                //Помним, что пейджинг и order мы уже обработали
                //Сначала обработаем параметры "не границы диапазонов"
                for (String par:keys){
                    switch (par) {
                        case "id":
                            Long id = Long.parseLong(allParams.get(par));
                            cb.and(p, cb.like(root.get(par),"%" + id +"%"));
                            break;
                        case "name":
                        case "title":
                            //тут все значения String, поэтому можно объеденить
                            cb.and(p, cb.like(root.get(par),"%" + allParams.get(par) +"%"));
                            break;
                        case "race":
                            Race race = Race.valueOf(allParams.get(par));
                            cb.and(p, cb.like(root.get(par),"%" + race +"%"));
                            break;
                        case "profession":
                            Profession profession = Profession.valueOf(allParams.get(par));
                            cb.and(p, cb.like(root.get(par),"%" + profession +"%"));
                            break;
                        case "banned":
                            Boolean banned = Boolean.getBoolean(allParams.get(par));
                            cb.and(p, cb.like(root.get(par),"%" + banned +"%"));
                    }
                }
                //Теперь нужно обработать параметры "границы диапазонов"
                //Сначала просто считаем все значения, не заботясь, есть они там или нет
                String afterString = allParams.get(PlayerOrder.AFTER.getFieldName());
                String beforeString = allParams.get(PlayerOrder.BEFORE.getFieldName());
                String minExperience = allParams.get(PlayerOrder.MIN_EXPERIENCE.getFieldName());
                String maxExperience = allParams.get(PlayerOrder.MAX_EXPERIENCE.getFieldName());
                String minLevel = allParams.get(PlayerOrder.MIN_LEVEL.getFieldName());
                String maxLevel = allParams.get(PlayerOrder.MAX_LEVEL.getFieldName());

                //Теперь будем строить запросы по фильтрации
                //... по дате создания
                try {
                if (Objects.nonNull(afterString) && Objects.nonNull(beforeString)) {
                    Long afterLong = Long.parseLong(afterString);
                    Long beforeLong = Long.parseLong(beforeString);
                    Date after = new Date(afterLong);
                    Date before = new Date(beforeLong);
                    //если дата after до before
                    if (after.before(before)) {
                        cb.and(p, cb.between(root.get(PlayerOrder.BIRTHDAY.getFieldName()), after, before));
                    }
                }
                } catch (NumberFormatException e) {
                    LOGGER.error(String.format("Ошибка приведения к Long строки: %s",
                            afterString + " или " + beforeString));
                }
                //...по опыту


                return null;
            }
        });
    }

    @Override
    public Integer countPlayers() {
        // TODO: 08.01.2022 должне ли я здесь проверять что-то?
        LOGGER.info("Подсчёт количества игроков в базе");
        long result = playersRepo.count();
        LOGGER.info("Сейчас в базе игроков: long = %d; int = %d",result, (int)result);
        return (int) result;
    }

    @Override
    public boolean createPlayer(Player player) {
        LOGGER.info("Создаю в базе запись с игроком");
        boolean result = true;
        try {
            playersRepo.save(player);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Ошибка создания новго игрока в базе. Возможно player == null", e);
            result =  false;
        }
        LOGGER.info(String.format("В базе создана запись с игроком id = %d? - %b",player.getId(),result));
        return result;
    }

    @Override
    public Player getPlayerById(Long id) {
        LOGGER.info(String.format("Ищем в базе игрока с id = %d", id));
        Player player = null;
        try {
            player = playersRepo.getOne(id);
        } catch (EntityNotFoundException e) {
            LOGGER.error(String.format("Игрок с id = %d в базе не найден. Возвращаем null", id), e);
        }
        player = playersRepo.getOne(id);
        LOGGER.info(String.format("В базе нейден игрок с id = %id",id));
        return player;
    }

    @Override
    public boolean updatePlayer(Player player, Long id) {
        LOGGER.info(String.format("Ообновляем данные игрока с id = %d",id));
        boolean result = false;
        //Ишем, если ли данный игрок в базе, чтобы обновить его данные
        Player oldPlayer = getPlayerById(id);
        if (oldPlayer == null) {
            LOGGER.error(String.format("Игрок с id = %d в базе не найден. Возвращаем false", id));
            return false;
        }
        //Задаём, переданному player id. Если у player будет id, метод save сработает как update
        player.setId(id); //
        // сохраняем игрока в базу и проверяем как у нас всё сохранилос
        result = createPlayer(player);
        LOGGER.info(String.format("Данные игрока id = %d обновлены? - %b", id, result));
        return result;
    }

    @Override
    public boolean delPlayerById(Long id) {
        LOGGER.info(String.format("Удаление игрока с id = %d", id));
        boolean result = true;
        try {
            playersRepo.deleteById(id);
        } catch (IllegalArgumentException e) {
            LOGGER.error(String.format("Переданынй id = null"));
            result = false;
        }
        LOGGER.info(String.format("Игрок с id = %d удалён? - %b",id,result));
        return result;
    }
}
