package ru.merkulyevsasha.news.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    public static <From, To> To[] convertToArray(List<From> input, Mapper<From, To> mapper, Class<To> targetClass) {
        To[] targets = (To[]) Array.newInstance(targetClass, input.size());
        for(int i=0; i < input.size(); i++){
            From source = input.get(i);
            targets[i] = mapper.map(source);
        }
        return targets;
    }

    public static <From, To> To[] convertToArray(From[] input, Mapper<From, To> mapper, Class<To> targetClass) {
        To[] targets = (To[]) Array.newInstance(targetClass, input.length);
        for(int i=0; i < input.length; i++){
            From source = input[i];
            targets[i] = mapper.map(source);
        }
        return targets;
    }

    public static <TIn, TOut> List<TIn> convert(List<TOut> items, Mapper<TOut, TIn> converter){
        List<TIn> entities = new ArrayList<>();
        for(TOut item : items){
            entities.add(converter.map(item));
        }
        return entities;
    }

}
