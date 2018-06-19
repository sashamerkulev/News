package ru.merkulyevsasha.news.data;


public interface Mapper<From, To> {

    To map(From item);

}
