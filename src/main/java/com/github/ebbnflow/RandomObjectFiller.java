package com.github.ebbnflow;


import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomObjectFiller {

    private static final Logger log  = LoggerFactory.getLogger(RandomObjectFiller.class);

    private Random random = new Random();
    private int listMin = 1;
    private int listMax = 5;

    public <T> T createAndFill(Class<T> clazz) throws Exception {
        T instance = clazz.newInstance();
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        if (clazz.getSuperclass() != null) {
            fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        }

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = getRandomValueForField(field);
            field.set(instance, value);
        }
        return instance;

    }

    private Object getRandomValueForField(Field field) throws Exception {
        Class<?> type = field.getType();
        if (type.equals(List.class)) {
            return createList(field);
        } else if (type.equals(Map.class)) {
            return createMap(field);
        }
        return getRandomValueForType(type);
    }


    private Object getRandomValueForType(Class<?> type) throws Exception {
        if (type.isEnum()) {
            Object[] enumValues = type.getEnumConstants();
            return enumValues[random.nextInt(enumValues.length)];
        } else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return random.nextInt();
        } else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            return random.nextLong();
        } else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
            return random.nextDouble();
        } else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
            return random.nextFloat();
        } else if (type.equals(String.class) || type.equals(Object.class)) {
            return UUID.randomUUID().toString();
        } else if (type.equals(BigInteger.class)) {
            return BigInteger.valueOf(random.nextInt());
        } else if (type.equals(LocalDateTime.class)) {
            return LocalDateTime.now();
        }else if (type.equals(LocalDate.class)) {
            return LocalDate.now();
        }

        return createAndFill(type);
    }

    private Object createMap(Field field) throws Exception {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        Class<?> keyClass = (Class<?>) listType.getActualTypeArguments()[0];
        Class<?> valueClass = (Class<?>) listType.getActualTypeArguments()[1];
        return getGenericMap(keyClass, valueClass);
    }

    @SuppressWarnings("unchecked")
    private <T, V> Map<T, V> getGenericMap(Class<T> keyClass, Class<V> valueClass) throws Exception {
        Map<T, V> map = new HashMap<>();
        T keyObj = (T) getRandomValueForType(keyClass);
        V valueObj = (V) getRandomValueForType(valueClass);
        map.put(keyObj, valueObj);
        return map;
    }

    private Object createList(Field field) throws Exception {

        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        Class<?> clazz = (Class<?>) listType.getActualTypeArguments()[0];
        return getGenericList(clazz);
    }

    @SuppressWarnings("unchecked")
    private <Type> List<Type> getGenericList(Class<Type> clazz) throws Exception {
        int count = random.nextInt((listMax - listMin) + 1) + listMin;
        List<Type> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Type randomVal = (Type)getRandomValueForType(clazz);
            list.add(randomVal);
        }
        return list;
    }
}