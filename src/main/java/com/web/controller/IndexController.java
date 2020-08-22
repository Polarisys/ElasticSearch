package com.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @anthor Tolaris
 * @date 2020/4/14 - 1:45
 */
@Controller
public class IndexController {

    @GetMapping({"/", "index"})
    public String index() {
        return "index";
    }
}
