package com.roboticslib.motion;

import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.roboticslib.statemachine.*;
import com.roboticslib.util.Mathf;


public class MotionController {
    
    public MecanumChassis mc = null;
    public Odometry odo = null;
    public VoltageSensor vs = null;
    public SimpleStateMachine sm = null;
    public SimpleState lastState = null;
    public ElapsedTime timer = null;
    public MotionController(MecanumChassis chassis, Odometry odom, VoltageSensor volts){
        mc = chassis;
        odo = odom;
        vs = volts;
        sm = new SimpleStateMachine();
        timer = new ElapsedTime();
        
    }
    public void start(){
        sm.start();
        timer.reset();
    }
    
    public double lastX = 0;
    public double lastY = 0;
    public double lastAngle = 0;
    public double lastTime = 0;
    
    public double xSpeed = 0;
    public double ySpeed = 0;
    public double angleSpeed = 0;
    
    
    public void update(){
        double deltaTime = timer.seconds() - lastTime;
        double deltaX = odo.getX() - lastX;
        double deltaY = odo.getY() - lastY;
        double deltaAngle = odo.getAngle() - lastAngle;
        
        xSpeed = deltaX / deltaTime;
        ySpeed = deltaY / deltaTime;
        angleSpeed = deltaAngle / deltaTime;
        
        sm.update();
        lastX = odo.getX();
        lastY = odo.getY();
        lastAngle = odo.getAngle();
        lastTime = timer.seconds();
        
    }
    public void end(){
        sm.end();
    }
    
    public void moveTo(double tx, double ty, double tAngle){
        lastX = tx;
        lastY = ty;
        lastAngle = tAngle;
        
        SimpleState state = new SimpleState(){
            double x = tx;
            public boolean isDone = false;

            public void start(){
                
            }
            
            public void update(){
                double v = 12 / vs.getVoltage();
                
                double deltaX = tx - odo.getX();
                double deltaY = ty - odo.getY();
                double deltaAngle = Mathf.angleWrap(tAngle - odo.getAngle());
                double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                
                boolean isMoving = Math.abs(xSpeed) < .5 && Math.abs(ySpeed) < .5 && Math.abs(angleSpeed) < .5;
                if(dist < 1 && Math.abs(deltaAngle) < Math.toRadians(3)  && !isMoving){
                    //nextState();
                }
                
                double inputTheta = Math.atan2(deltaY, deltaX) + Math.PI / 2;
                
                double inputPower =  (dist * .15);
                // max power
                
                double inputTurn = 0;

                if(Math.abs(deltaAngle) > Math.toRadians(2)){
                    inputTurn = (deltaAngle * .6) + Math.signum(deltaAngle) * .8;
                }
                
                inputTurn = Range.clip(inputTurn, -.5, .5) * v;
                inputPower = Range.clip(inputPower, -.6, .6) * v;
                double sin = Math.sin(inputTheta - Math.PI/4 - odo.getAngle());
                double cos = Math.cos(inputTheta - Math.PI/4 - odo.getAngle());
                double max = Math.max(Math.abs(cos), Math.abs(sin));

                double frontLeft = inputPower * cos/max - inputTurn;
                double frontRight = inputPower * sin/max + inputTurn;
                double backLeft = inputPower * sin/max - inputTurn;
                double backRight = inputPower * cos/max + inputTurn;
                
                mc.frontLeft.setPower(frontLeft);
                mc.frontRight.setPower(frontRight);
                mc.backLeft.setPower(backLeft);
                mc.backRight.setPower(backRight);
                
            }
            
            public void end(){
                
            }

            public String toString(){
                return "Moving";
            }
        };
    }
    
    public void nextState(){
        
    }
    
}