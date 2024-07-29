package com.akshathsaipittala.streamspace.services;

import com.akshathsaipittala.streamspace.helpers.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Repository
public interface UserPreferences extends ListCrudRepository<Preference, Integer> {
}

@Slf4j
@RestController
@RequestMapping("/preference")
@RequiredArgsConstructor
class PreferencesAPI {

    final UserPreferences userPreferences;

    @PatchMapping
    public ResponseEntity<Void> saveDarkModePreference(@RequestBody Preference preference) {
        Optional<Preference> existingPref = userPreferences.findById(preference.getPrefId());
        if (existingPref.isPresent()) {
            Preference updatedPref = existingPref.get();
            updatedPref.setEnabled(preference.isEnabled());
            userPreferences.save(updatedPref);
        } else {
            //userPreferences.save(preference);
            log.info("Preference with id {} not found", preference.getPrefId());
        }
        return ResponseEntity.ok().build();
    }
}