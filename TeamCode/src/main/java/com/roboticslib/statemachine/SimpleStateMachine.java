package com.roboticslib.statemachine;

import com.roboticslib.statemachine.SimpleState;
public class SimpleStateMachine
{
    public SimpleState current = null;
    
    public void setEntry(SimpleState entry){
        current = entry;
    }
    
    public void start(){
        if (current != null){
            current.start();
        }
    }
    
    public void update(){
        if (current != null){
            if(current.isDone()){
                current.end();
                if(current.nextState != null){
                    current = current.nextState;
                    current.start();
                }
            }
            current.update();
        }
    }
    
    public void end(){
        current.end();
    }
    public String toString(){
        return current.toString();
    }
}
       