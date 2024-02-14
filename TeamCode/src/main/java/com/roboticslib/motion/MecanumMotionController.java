package com.roboticslib.motion;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.roboticslib.util.Mathf;

import java.util.ArrayList;

public class MecanumMotionController {

    MecanumPID pid = null;
    ArrayList<MecanumAction> actions = new ArrayList<MecanumAction>();
    int current = 0;
    
    public MecanumMotionController(MecanumPID pid){
        this.pid = pid;
    }
    
    public void start()
    {
        actions.get(current).start();
    }
    
    public void update(){
        if(true){//ended == false){
            actions.get(current).update();
        }
    }
    
    public void end(){
        
    }
    public double lastX = 0;
    public double lastY = 0;
    public double lastAngle = 0;
    
    public void moveTo(double x, double y, double theta){
        
        actions.add(new MecanumAction(){
            double tx = x;
            double ty = y;
            double tAngle = Math.toRadians(theta);


            ElapsedTime timer;
            double seconds = 4;
        
            @Override
            void start(){
                pid.moveTo(x,y,theta);
                lastX = x;
                lastY = y;
                lastAngle = theta;
                timer = new ElapsedTime();
            }
            @Override
            void update(){
                pid.update();
                double deltaX = tx - pid.odo.getX();
                double deltaY = ty - pid.odo.getY();
                double deltaAngle = tAngle - pid.odo.getAngle();
                deltaAngle = Mathf.angleWrap(deltaAngle);
                double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                
                if(timer.seconds() > seconds || dist < 2 && Math.abs(deltaAngle) < Math.toRadians(2)){
                    nextState();
                }
                
            }
        });
    }
    public void waitForSeconds(double seconds){
        actions.add(new MecanumAction(){
            ElapsedTime timer;

            @Override
            void start(){
                pid.moveTo(lastX,lastY,lastAngle);
                timer = new ElapsedTime();
            }
            @Override
            void update(){
                pid.update();
                if(timer.seconds() > seconds) nextState();
            }
        });
    }
    public void waitForSeconds(double seconds, Runnable command){
        actions.add(new MecanumAction(){
            ElapsedTime timer;

            @Override
            void start(){
                command.run();
                pid.moveTo(lastX,lastY,lastAngle);
                timer = new ElapsedTime();
            }
            @Override
            void update(){
                pid.update();
                if(timer.seconds() > seconds) nextState();
            }
        });
    }
    public void setMaxSpeed(double speed){
        actions.add(new MecanumAction(){
            @Override
            void start(){
                pid.maxSpeed = speed;
                nextState();
            }
        });
    }
    public void nextState(){
        if(current < actions.size()-1){
            actions.get(current).end();
            current++;
            actions.get(current).start();
        }
        else{
            actions.get(current).end();
            current++;
            actions.add(new MecanumAction(){

    
                @Override
                void start(){
                    pid.moveTo(lastX, lastY, lastAngle);
                }
                @Override
                void update(){
                    pid.update();
                    
                }
            });
        }
    }
    
    
    
    
}

class MecanumAction{
    void start(){};
    void update(){};
    void end(){};
}








