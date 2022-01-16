package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayersRepo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component

public class CustomProductRepository {
    private final PlayersRepo playersRepo;

    public CustomProductRepository(PlayersRepo playersRepo) {
        this.playersRepo = playersRepo;
    }

    public List<Player > getQueryResult(List<Filter> filters, Pageable pageable){
        if(filters.size()>0) {
            return playersRepo.findAll(getSpecificationFromFilters(filters), pageable).getContent();
        }else {
            return playersRepo.findAll(pageable).getContent();
        }
    }

    private Specification<Player> getSpecificationFromFilters(List<Filter> filter) {
        Specification<Player> specification = Specification.where(createSpecification(filter.remove(0)));
        for (Filter input : filter) {
            specification = specification.and(createSpecification(input));
        }
        return specification;
    }

    private Specification<Player> createSpecification(Filter input) {
        switch (input.getOperator()){
            case EQUALS:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get(input.getField()),
                                castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case NOT_EQ:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.notEqual(root.get(input.getField()),
                                castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case GREATER_THAN:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.gt(root.get(input.getField()),
                                (Number) castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case LESS_THAN:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.lt(root.get(input.getField()),
                                (Number) castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case LIKE:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get(input.getField()), "%"+input.getValue()+"%");
            case IN:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.in(root.get(input.getField()))
                                .value(castToRequiredType(root.get(input.getField()).getJavaType(), input.getValues()));
           case BETWEEN:
               switch (input.getField()) {
                   case "birthday":
                       Date minDate = (Date) castToRequiredType(Date.class, input.getValues().get(0));
                       Date maxDate = (Date) castToRequiredType(Date.class, input.getValues().get(1));
                       return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(input.getField()), minDate, maxDate);
                   case "experience":
                   case "level":
                       Integer minInt = (Integer) castToRequiredType(Integer.class, input.getValues().get(0));
                       Integer maxInt = (Integer) castToRequiredType(Integer.class, input.getValues().get(1));
                       return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(input.getField()), minInt, maxInt);
               }
            default:
                throw new RuntimeException("Operation not supported yet");
        }
    }

    private Object castToRequiredType(Class fieldType, String value) {
        if(fieldType.isAssignableFrom(Long.class)){
            return Long.valueOf(value);
        }else if(fieldType.isAssignableFrom(Integer.class)){
            return Integer.valueOf(value);
        }else if(Enum.class.isAssignableFrom(fieldType)){
            return Enum.valueOf(fieldType, value);
        } else if (fieldType.isAssignableFrom(Date.class)) {
            return new Date(Long.valueOf(value));
        } else if (fieldType.isAssignableFrom(Boolean.class)) {
            return Boolean.valueOf(value);
        }
        return null;
    }

    private Object castToRequiredType(Class fieldType, List<String> value) {
        List<Object> lists = new ArrayList<>();
        for (String s : value) {
            lists.add(castToRequiredType(fieldType, s));
        }
        return lists;
    }

}
