package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.entity.WatchListItems;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchListRepository extends ListCrudRepository<WatchListItems, Integer> {

    boolean existsByName(String name);

}
