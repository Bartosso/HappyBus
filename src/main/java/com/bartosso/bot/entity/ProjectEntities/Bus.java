package com.bartosso.bot.entity.ProjectEntities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Bus implements Entity {
    private       long      id;
    private       long      driver_id;
    private final String    number;
    private final String    brand;
    private final String    model;
    private final String    color;
    private       boolean   ready_to_school;
    private       boolean   ready_to_home;
    @Setter
    private       ArrayList<Long> to_school_kids;
    @Setter
    private       ArrayList<Long> to_home_kids;
    @Setter
    private       String    last_gps_cords;
    private       long      last_coordinator_id;
    @Override
    public String getTextToButton() {
        return number + "\n" + brand + " " + color + " " + model;
    }


    @Override
    public String toString(){
       return "Номер автомобиля: "+ number+
              "\nМарка автомобиля: "+ brand+
              "\nМодель автомобиля: " + model+
              "\nЦвет автомобиля: " + color+"\n";
    }

}
