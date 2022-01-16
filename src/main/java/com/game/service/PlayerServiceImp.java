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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class PlayerServiceImp implements PlayerService {
    private final PlayersRepo playersRepo;
    public static final Logger LOGGER = LoggerFactory.getLogger(PlayerServiceImp.class);

    public PlayerServiceImp(PlayersRepo playersRepo) {
        this.playersRepo = playersRepo;
    }

    @Override
    public List<Player> getPlayerList(Map<String, String> allParams){
        LOGGER.info("Метод получения игроков");
        //Задаём дефолтные значения по пейджингу
        Pageable pageable;
        Integer pageNumber = Integer.valueOf(DefaultValueOfPaging.PAGE_NUMBER.getFieldValue());// по определению
        Integer pageSize = Integer.valueOf(DefaultValueOfPaging.PAGE_SIZE.getFieldValue()); // по определению
        //Для начала, нет ли у нас указания по пейджингу
        //Если указаний нет - то используем дефолтные значений
        Set<String> keys = allParams.keySet();
        //Если какие-то указания есть в параметрах, то дефолтные значения меняем
        if (keys.contains(PlayerOrder.PAGE_NUMBER.getFieldName()) || keys.contains(PlayerOrder.PAGE_SIZE.getFieldName())) {
            String pageNumberString = allParams.get(PlayerOrder.PAGE_NUMBER.getFieldName());
            if (Objects.nonNull(pageNumberString)) {
                try {
                    pageNumber = Integer.parseInt(pageNumberString);
                } catch (NumberFormatException e) {
                    LOGGER.error(String.format("Ошибка форматирования поля: %s. Значение остаётся default: %d",
                            PlayerOrder.PAGE_NUMBER.getFieldName(), DefaultValueOfPaging.PAGE_NUMBER.getFieldValue()));
                }
                allParams.remove(PlayerOrder.PAGE_NUMBER.getFieldName());
            }
            String pageSizeString = allParams.get(PlayerOrder.PAGE_SIZE.getFieldName());
            if (Objects.nonNull(pageSizeString)) {
                try {
                    pageSize = Integer.parseInt(pageSizeString);
                } catch (NumberFormatException e) {
                    LOGGER.error(String.format("Ошибка форматирования поля: %s. Значение остаётся default: %d",
                            PlayerOrder.PAGE_SIZE.getFieldName(), DefaultValueOfPaging.PAGE_SIZE.getFieldValue()));
                }
                allParams.remove(PlayerOrder.PAGE_SIZE.getFieldName());
            }
        }

        //обрабатываем Sort
        //Присваиваем дефолтное значение
        Sort sort;
        String sortByParam = PlayerOrder.ID.getFieldName();

        if (keys.contains(PlayerOrder.PLAYER_ORDER.getFieldName())) {
            sortByParam = allParams.get(PlayerOrder.PLAYER_ORDER.getFieldName()).toLowerCase();
            allParams.remove(PlayerOrder.PLAYER_ORDER.getFieldName());
        }
        sort = Sort.by(Sort.Direction.ASC, sortByParam);
        pageable = PageRequest.of(pageNumber, pageSize,sort);

        return getPlayersByParams(allParams, pageable);
    }

    @Override
    public List <Player> getPlayersByParams (Map<String, String> allParams, Pageable pageable) {

        LOGGER.info("Метод получения игроков на основе переданных параметров");

        //обработка параметров фильтрации
        List<Filter> filters = new ArrayList<>();
        Set<String> key = allParams.keySet();
        LOGGER.info("Сначала обработаем параметры \"не границы диапазонов\"");
        for (String field : key) {
            switch (field) {
                case ("name"):
                case ("title"):
                    filters.add(FilterBuilder.aFilter()
                            .withField(field).withValue(allParams.get(field))
                            .withOperator(QueryOperator.LIKE).build());
                    break;
                case ("race"):
                case ("profession"):
                case ("banned"):
                    filters.add(FilterBuilder.aFilter()
                            .withField(field).withValue(allParams.get(field))
                            .withOperator(QueryOperator.EQUALS).build());
            }
        }

        //Теперь нужно обработать параметры "границы диапазонов"
        //Сначала просто считаем все значения, не заботясь, есть они там или нет
        String afterString = allParams.get(PlayerOrder.AFTER.getFieldName());
        String beforeString = allParams.get(PlayerOrder.BEFORE.getFieldName());
        String minExperienceString = allParams.get(PlayerOrder.MIN_EXPERIENCE.getFieldName());
        String maxExperienceString = allParams.get(PlayerOrder.MAX_EXPERIENCE.getFieldName());
        String minLevelString = allParams.get(PlayerOrder.MIN_LEVEL.getFieldName());
        String maxLevelString = allParams.get(PlayerOrder.MAX_LEVEL.getFieldName());

        //... по дню рождения
            if (Objects.nonNull(afterString) && Objects.nonNull(beforeString)) {
                List<String> minMax = new ArrayList<>();
                minMax.add(afterString);
                minMax.add(beforeString);
                filters.add(FilterBuilder.aFilter().withOperator(QueryOperator.BETWEEN).withValues(minMax).withField(PlayerOrder.BIRTHDAY.getFieldName()).build());
            }

        //...по опыту

            if (Objects.nonNull(minExperienceString) && Objects.nonNull(maxExperienceString)) {
                List<String> minMax = new ArrayList<>();
                minMax.add(minExperienceString);
                minMax.add(maxExperienceString);
                filters.add(FilterBuilder.aFilter().withOperator(QueryOperator.BETWEEN).withValues(minMax).withField(PlayerOrder.EXPERIENCE.getFieldName()).build());
            }

        //... по level

            if (Objects.nonNull(minLevelString) && Objects.nonNull(maxLevelString)) {
                List<String> minMax = new ArrayList<>();
                minMax.add(minLevelString);
                minMax.add(maxLevelString);
                filters.add(FilterBuilder.aFilter().withOperator(QueryOperator.BETWEEN).withValues(minMax).withField(PlayerOrder.LEVEL.getFieldName()).build());
            }

        return new CustomProductRepository(playersRepo).getQueryResult(filters, pageable);
    }

    @Override
    public Integer countPlayers(Map<String, String> allParams) {
        // TODO: 08.01.2022 должне ли я здесь проверять что-то?
        LOGGER.info("Подсчёт количества игроков в базе");
        Sort sort = Sort.by(Sort.Direction.ASC, PlayerOrder.ID.getFieldName());
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
        long result = getPlayersByParams(allParams,pageable).size();
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
            LOGGER.error("Ошибка создания нового игрока в базе. Возможно player == null", e);
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
        //player = playersRepo.getOne(id);
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
        } catch (Exception e) {
            LOGGER.error(String.format("Переданынй id = null"));
            result = false;
        }
        LOGGER.info(String.format("Игрок с id = %d удалён? - %b",id,result));
        return result;
    }

}
