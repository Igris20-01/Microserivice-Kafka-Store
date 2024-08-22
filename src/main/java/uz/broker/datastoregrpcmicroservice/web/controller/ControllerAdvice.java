package uz.broker.datastoregrpcmicroservice.web.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uz.broker.datastoregrpcmicroservice.exception.SensorNotFoundException;

@RestControllerAdvice
public class ControllerAdvice {


    @ExceptionHandler(SensorNotFoundException.class)
    public String sensorNotFound(SensorNotFoundException ex) {
        return "Sensor not found";
    }

    @ExceptionHandler
    public String server(Exception ex) {
        ex.printStackTrace();
        return "Something went wrong";
    }

}
