package com.psc.malaysia.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/capi")
public class CapiController {

    public String title = " Conversion API ";
    public String previousPath = "capi";

    @RequestMapping(value ={ ""})
    public String index(HttpServletRequest  request, ModelMap modelMap){
        log.debug(request.getRequestURI());
        modelMap.addAttribute("title", title);
        modelMap.addAttribute("message", request.getRequestURI());
        modelMap.addAttribute("previousPath", previousPath);
        return previousPath + "/index";
    }

    @RequestMapping(value ={ "AddToCart","PageView"})
    public String view(HttpServletResponse response, HttpServletRequest  request, ModelMap modelMap) throws IOException {
        String url = request.getRequestURI();
        log.debug(url);

        modelMap.addAttribute("title", title);
        modelMap.addAttribute("message", url );
        modelMap.addAttribute("previousPath", previousPath);
        return previousPath + "/index";
    }

    @RequestMapping(value = {"Purchase"})
    public String purchase(HttpServletRequest  request, String cost, ModelMap modelMap){
        log.debug(request.getRequestURI());

        modelMap.addAttribute("title", title);
        cost = cost == null?"":cost;
        modelMap.addAttribute("cost", cost);
        modelMap.addAttribute("message", request.getRequestURI() + " Cost:" + cost);
        modelMap.addAttribute("previousPath", previousPath);
        return previousPath + "/purchase";
    }
}
