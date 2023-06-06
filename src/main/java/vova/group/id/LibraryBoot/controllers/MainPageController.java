package vova.group.id.LibraryBoot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @GetMapping("/library")
    public String index() {
        return "index";
    }
}
