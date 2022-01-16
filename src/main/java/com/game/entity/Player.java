package com.game.entity;

import com.game.entity.Profession;
import com.game.entity.Race;
import com.sun.xml.internal.bind.v2.TODO;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// ID игрока

    private String name;// Имя персонажа (до 12 знаков включительно)
    private String title;// Титул персонажа (до 30 знаков включительно)
    @Enumerated(EnumType.STRING)
    private Race race;// Раса персонажа
    @Enumerated(EnumType.STRING)
    private Profession profession;// Профессия персонажа
    private Integer experience; // Опыт персонажа. Диапазон значений 0...10 000 000
    private Integer level;// Уровень персонажа
    private Integer untilNextLevel; // Остаток опыта до следующего уровня
    @Temporal(TemporalType.DATE)
    private Date birthday; // Дата регистрации. Диапазон значений 2000...3000 включительно
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @ColumnDefault("0")
    private Boolean banned;// Забанен/не забанен

    public Player() {
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
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
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
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel() {
        this.level = (int)((Math.sqrt(2500.0+200.0*(double)getExperience())-50.0)/100.0);
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel() {
        this.untilNextLevel = 50*(getLevel()+1)*(getLevel()+2)-getExperience();
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public boolean hasRequiredFields() {
        //Проверка, что обязательные поля существуют
        if (
                Objects.isNull(getName()) ||
                        Objects.isNull(getTitle()) ||
                        Objects.isNull(getRace()) ||
                        Objects.isNull(getProfession()) ||
                        Objects.isNull(getBirthday()) ||
                        Objects.isNull(getExperience())
        ) {
            return false;
        }
        //Проверка длин полей Name и Title
        if (getName().length() > 12 || getTitle().length() > 30) {
            return false;
        }
        //Проверка, что поле Name не пустое
        if (getName().isEmpty()) {
            return false;
        }
        // Проверка, что опыт лежит в заданных пределах
        if (getExperience().compareTo(10_000_000) > 0 || getExperience().compareTo(0) < 0) {
            return false;
        }
        //Проверка, что дата регистрации не ранее 2000-ого и не позже 3000-ого года
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date dateMin = null;
        Date dateMax = null;
        try {
            dateMin = df.parse("01/01/2000");
            dateMax = df.parse("31/12/3000");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (getBirthday().before(dateMin) ||
                getBirthday().after(dateMax)
        ) {
            return false;
        }

        return true;
    }

    @PrePersist
    @PreUpdate
    public void setLevels() {
        setLevel();
        setUntilNextLevel();
    }
}
