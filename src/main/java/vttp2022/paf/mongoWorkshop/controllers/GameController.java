package vttp2022.paf.mongoWorkshop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GameController {
    
    @GetMapping(path = "/")
    public String home(){
        return "index";
    }
}
