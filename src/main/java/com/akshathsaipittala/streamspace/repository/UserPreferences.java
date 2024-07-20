package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.helpers.Preference;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferences extends ListCrudRepository<Preference, Integer> {
}