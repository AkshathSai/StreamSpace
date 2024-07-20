package com.akshathsaipittala.streamspace.www;

import com.akshathsaipittala.streamspace.helpers.Preference;
import com.akshathsaipittala.streamspace.repository.UserPreferences;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class WebController {

    final UserPreferences userPreferences;

    @GetMapping("/")
    public String index(Model model) {
        Optional<Preference> darkModePreference = userPreferences.findById(1);
        boolean darkModeEnabled = darkModePreference.map(Preference::isEnabled).orElse(false);
        model.addAttribute("darkmodeenabled", darkModeEnabled);
        return "index";
    }

}
