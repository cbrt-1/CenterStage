package com.roboticslib.statemachine;


public class SimpleState{
    public SimpleState nextState;
    public String name;
    
    public boolean isDone = false;
    
    public SimpleState(){
        this.name = "unnamed";
    }
    public SimpleState(String name){
        this.name = name;
    }
    
    public void start(){
        
    }
    
    public void update(){
        
    }
    
    public void end(){
        
    }
    
    public boolean isDone(){
        return isDone;
    }
    
    public String toString(){
        return name;
    }
    
}
