package com.jh.dangerzone.controller;


import com.jh.dangerzone.domain.Config;
import com.jh.dangerzone.domain.Frequency;
import com.jh.dangerzone.service.ServiceDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@RestController
@RequestMapping("home")
public class HomeController {


    @Autowired
    ServiceDispatcher serviceDispatcher;

    @Value("${stationId}")
    private int stationId;

    @Value("${directoryLocation}")
    private String directoryLocation;

    @Value("${frequency}")
    private Frequency frequency;

    /** Handles the POST-request to /home
     * @param model - state object for view
     * @return
     */
    @GetMapping
    public ModelAndView home(Model model) {
        Config config = new Config(stationId,directoryLocation,frequency);
        System.out.println(config);
        model.addAttribute("config", config);
        return new ModelAndView("apiPage");
    }

    /** Handles the POST-request to /home
     *
     * @param config - contains the POST-data
     * @param bindingResult - contains form errors from the view
     * @param model - state object for view
     * @return
     */
    @PostMapping
    public ModelAndView postConfig(@ModelAttribute @Valid Config config, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("apiPage");
        }

        StringBuilder sb = new StringBuilder(config.getDirectoryLocation());
        if(sb.charAt(sb.length() - 1) != '/'){
            sb.append("/");
        }
        config.setDirectoryLocation(sb.toString());

        // CALL Service Tier
        try{
            serviceDispatcher.handleRequest(config);
        }catch (RuntimeException e){
            model.addAttribute("exception",e.getMessage());
            System.err.println(e.getMessage());
        }
        model.addAttribute("config", config);
        return new ModelAndView("apiPage");
    }




}
