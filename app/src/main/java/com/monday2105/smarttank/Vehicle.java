package com.monday2105.smarttank;

import java.util.HashMap;
import java.util.Map;

class Vehicle {
    private String name;
    private Float fuel;
    private String number;

    public Vehicle(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public Float getFuel(){
        return fuel;
    }

    public void setName(String name){
        this.name = name;
    }

    public void getName(Float fuel){
        this.fuel = fuel;
    }

    public String getNumber(){
        return this.number;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("fuel", fuel);
        result.put("number",number);

        return result;
    }
}
