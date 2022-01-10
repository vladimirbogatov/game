package com.game.entity;

import com.game.entity.Profession;
import com.game.entity.Race;
import com.sun.xml.internal.bind.v2.TODO;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// ID игрока

    private String name;// Имя персонажа (до 12 знаков включительно)
    private String title;// Титул персонажа (до 30 знаков включительно)
    @Enumerated
    private Race race;// Раса персонажа
    @Enumerated
    private Profession profession;// Провессия персонажа
    private Integer experience; // опыт персонажа. Дапазон значений 0...10 000 000
    private Integer level;// Уровень персонажа
    private Integer untilNextLevel; // Остатоко опыта до следующего уровня
    @Temporal(TemporalType.DATE)
    private Date birthday; // Дата регистрации. Даипазон значений 2000...3000 включительно
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean banned;// Забанен/не забанен

    public Player(Long id, String name, String title, Race race, Profession profession, Date birthday, Integer experience) {
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.birthday = birthday;
        this.banned = false;
        this.experience = experience;

        this.level = (int)((Math.sqrt(2500.0+200.0*(double)experience)-50.0)/100.0);
        this.untilNextLevel = 50*(level+1)*(level+2)-experience;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        // TODO: 07.01.2022 Сделать проверку до 12 знаков включительно
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        // TODO: 07.01.2022 сделать проверку до 30 знаков включительно
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        // TODO: 07.01.2022 Добавить проверку на диапазон значений 0...10 000 000
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        // TODO: 07.01.2022 Добавить проверку на диапазон 2000...3000 включительно 
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }
}
