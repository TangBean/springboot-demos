package org.tangbean.congratulations.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CongratulationsController {

    @RequestMapping("/congrats")
    public String congratulations() {
        return "congrats";
    }

}
