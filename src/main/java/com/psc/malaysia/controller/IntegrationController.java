package com.psc.malaysia.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class IntegrationController {

    @RequestMapping(value ={ ""})
    public String mainContent(HttpServletRequest  request, ModelMap modelMap){
        log.debug(request.getRequestURI());
        modelMap.addAttribute("single", "Integration");
        modelMap.addAttribute("pixel", "Pixel");
        modelMap.addAttribute("gtm", "GTM-FACEBOOK");
        modelMap.addAttribute("gtm2", "GTM-GA4-FACEBOOK");
        modelMap.addAttribute("capi", "Conversion API");
        modelMap.addAttribute("capi2", "Conversion API with Stape.io");
        return "index";
    }
}
