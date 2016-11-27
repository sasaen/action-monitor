package com.sasaen.monitor.rest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest endpoint which provides the Version of the application. 
 * @author sasaen
 *
 */
@RestController
public class VersionRestController {

    private static final String VERSION = "Version: action-monitor 1.0";

    /**
     * 
     * @return the current version of the action-monitor application.  
     */
    @RequestMapping(path="/version", method=RequestMethod.GET)
    public HttpEntity<Version> version() {

        Version greeting = new Version(VERSION);
        
        return new ResponseEntity<Version>(greeting, HttpStatus.OK);
    }
}
