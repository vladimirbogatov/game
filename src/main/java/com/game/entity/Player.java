package com.game.entity;


import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import javax.persistence.*;
import java.lang.reflect.Field;
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

    @PrePersist
    @PreUpdate
    public void hasRequiredFields() {
        //Проверка, что обязательные поля существуют
        if (
                Objects.isNull(getName()) ||
                        Objects.isNull(getTitle()) ||
                        Objects.isNull(getRace()) ||
                        Objects.isNull(getProfession()) ||
                        Objects.isNull(getBirthday()) ||
                        Objects.isNull(getExperience())
        ) {
            throw new IllegalArgumentException("Title, Race, Profession, Birthday, Experience must be NON NULL");
        }
        //Проверка длин полей Name и Title
        if (getName().length() > 12 || getTitle().length() > 30) {
            throw new IllegalArgumentException("Check Name and Title length");
        }
        //Проверка, что поле Name не пустое
        if (getName().isEmpty()) {
            throw new IllegalArgumentException("Empty name");
        }
        // Проверка, что опыт лежит в заданных пределах
        if (getExperience().compareTo(10_000_000) > 0 || getExperience().compareTo(0) < 0) {
            throw new IllegalArgumentException("Check Experience limit");
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
            throw new IllegalArgumentException("Birthday must be between 2000th and 3000th year");
        }
        setLevels();
    }

    public void setLevels() {
        setLevel();
        setUntilNextLevel();
    }

    public boolean isEmpty()  {

        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.get(this)!=null) {
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Exception occured in processing");
            }
        }
        return true;
    }

    public void copyDiff(Player source) throws
            IllegalAccessException, NoSuchFieldException {
        for (Field field : source.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            if (name.equals(PlayerNameConstants.ID)) {
                continue;
            }
            Object value = field.get(source);
            //If it is a non null value copy to destination
            if (null != value)
            {
                Field destField = this.getClass().getDeclaredField(name);
                destField.setAccessible(true);
                destField.set(this, value);
            }
        }
    }

}
