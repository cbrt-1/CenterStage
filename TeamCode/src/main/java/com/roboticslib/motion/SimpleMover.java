package com.roboticslib.motion;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.Range;

import java.util.ArrayList;

public class SimpleMover {
    private final MecanumChassis mc;
    private final Odometry odo;
    public boolean ended = false;
    ArrayList<RobotAction> actions = new ArrayList<RobotAction>();
    int current = 0;
    
    public SimpleMover(MecanumChassis mecanumChassis, Odometry odometry){
        this.mc = mecanumChassis;
        this.odo = odometry;
    }

    public void start(){
        actions.get(current).start();
    }

    public void update(){
        if(true){//ended == false){
            actions.get(current).update();
        }
    }
    VoltageSensor vs = null;
    public void setVoltageSensor(VoltageSensor volts){
        vs = volts;
    }
    public double lastX = 0;
    public double lastY = 0;
    public double lastAngle = 0;
    public void moveTo(double xPos, double yPos, double targetAngle, double finalSpeed){
        lastX = xPos;
        lastY = yPos;
        lastAngle = Math.toRadians(targetAngle);
        actions.add(new RobotAction(){
            double tx = xPos;
            double ty = yPos;
            double tAngle = Math.toRadians(targetAngle);

            @Override
            void start(){
                lastX = xPos;
                lastY = yPos;
                lastAngle = tAngle;
            }
            @Override
            void update(){
                double v = vs.getVoltage();
                v = 1;
                double deltaX = tx - odo.getX();
                double deltaY = ty - odo.getY();
                double dist = deltaX * deltaX + deltaY * deltaY;
                dist = Math.sqrt(dist);

                

                double v1x = Math.cos(odo.getAngle());
                double v1y = Math.sin(odo.getAngle());

                double v2x = Math.cos(tAngle);
                double v2y = Math.sin(tAngle);


                double deltaAngle = Math.atan2((v2y * v1x) - (v2x * v1y), (v1x * v2x) + (v1y * v2y));
                //deltaAngle = 0;
                if(dist < 2 && Math.abs(deltaAngle) < Math.toRadians(4)){
                    nextState();
                }
                if(dist > 10) dist = 10;
                double inputTheta = Math.atan2(deltaY, deltaX) + Math.PI / 2;
                
                double inputPower =  (dist * .13 + finalSpeed);
                // max power
                
                double inputTurn = 0;

                if(Math.abs(deltaAngle) > Math.toRadians(3)){
                    inputTurn = (deltaAngle * .5) + Math.signum(deltaAngle) * .1;
                }
                inputTurn = Range.clip(inputTurn, -.3, .3) * v;
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
        });
    }

    public void waitForSeconds(double seconds, Runnable runner){
        actions.add(new RobotAction(){
            double tx = lastX;
            double ty = lastY;
            double tAngle = lastAngle;
            ElapsedTime timer = new ElapsedTime();
            double secondsToWait = seconds;
            @Override
            void start(){
                runner.run();
                mc.backLeft.setPower(0);
                mc.backRight.setPower(0);
                mc.frontLeft.setPower(0);
                mc.frontRight.setPower(0);
                timer.reset();
            }
            @Override
            void update(){
                double v = vs.getVoltage();
                v = 12 / v;
                double deltaX = tx - odo.getX();
                double deltaY = ty - odo.getY();
                double dist = deltaX * deltaX + deltaY * deltaY;
                dist = Math.sqrt(dist);

                

                double v1x = Math.cos(odo.getAngle());
                double v1y = Math.sin(odo.getAngle());

                double v2x = Math.cos(tAngle);
                double v2y = Math.sin(tAngle);


                double deltaAngle = Math.atan2((v2y * v1x) - (v2x * v1y), (v1x * v2x) + (v1y * v2y));
                //deltaAngle = 0;
                if(timer.seconds() > seconds) nextState();
                if(dist > 10) dist = 10;
                double inputTheta = Math.atan2(deltaY, deltaX) + Math.PI / 2;
                
                double inputPower =  (dist * .08);
                // max power
                
                double inputTurn = 0;

                if(Math.abs(deltaAngle) > Math.toRadians(2)){
                    inputTurn = (deltaAngle * .5 + Math.signum(deltaAngle) * .1);
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
        public String toString(){
            return "wait";
        }
        });
        
    }
    public void waitForSeconds(double seconds, Runnable runner, Runnable updateLoop){
        actions.add(new RobotAction(){
            double tx = lastX;
            double ty = lastY;
            double tAngle = lastAngle;
            ElapsedTime timer = new ElapsedTime();
            double secondsToWait = seconds;
            @Override
            void start(){
                runner.run();
                mc.backLeft.setPower(0);
                mc.backRight.setPower(0);
                mc.frontLeft.setPower(0);
                mc.frontRight.setPower(0);
                timer.reset();
            }
            @Override
            void update(){
                updateLoop.run();
                double v = vs.getVoltage();
                v = 12 / v;
                double deltaX = tx - odo.getX();
                double deltaY = ty - odo.getY();
                double dist = deltaX * deltaX + deltaY * deltaY;
                dist = Math.sqrt(dist);

                

                double v1x = Math.cos(odo.getAngle());
                double v1y = Math.sin(odo.getAngle());

                double v2x = Math.cos(tAngle);
                double v2y = Math.sin(tAngle);


                double deltaAngle = Math.atan2((v2y * v1x) - (v2x * v1y), (v1x * v2x) + (v1y * v2y));
                //deltaAngle = 0;
                if(timer.seconds() > seconds) nextState();
                if(dist > 10) dist = 10;
                double inputTheta = Math.atan2(deltaY, deltaX) + Math.PI / 2;
                
                double inputPower =  (dist * .08);
                // max power
                
                double inputTurn = 0;

                if(Math.abs(deltaAngle) > Math.toRadians(2)){
                    inputTurn = (deltaAngle * .5 + Math.signum(deltaAngle) * .1);
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
        public String toString(){
            return "wait";
        }
        });
        
    }
    public void waitForSeconds(double seconds){
        actions.add(new RobotAction(){
            double tx = lastX;
            double ty = lastY;
            double tAngle = lastAngle;
            ElapsedTime timer = new ElapsedTime();
            double secondsToWait = seconds;
            @Override
            void start(){
                mc.backLeft.setPower(0);
                mc.backRight.setPower(0);
                mc.frontLeft.setPower(0);
                mc.frontRight.setPower(0);
                timer.reset();
            }
            @Override
            void update(){
                double v = vs.getVoltage();
                v = 12 / v;
                double deltaX = tx - odo.getX();
                double deltaY = ty - odo.getY();
                double dist = deltaX * deltaX + deltaY * deltaY;
                dist = Math.sqrt(dist);

                

                double v1x = Math.cos(odo.getAngle());
                double v1y = Math.sin(odo.getAngle());

                double v2x = Math.cos(tAngle);
                double v2y = Math.sin(tAngle);


                double deltaAngle = Math.atan2((v2y * v1x) - (v2x * v1y), (v1x * v2x) + (v1y * v2y));
                //deltaAngle = 0;
                if(timer.seconds() > seconds) nextState();
                if(dist > 10) dist = 10;
                double inputTheta = Math.atan2(deltaY, deltaX) + Math.PI / 2;
                
                double inputPower =  (dist * .08);
                // max power
                
                double inputTurn = 0;

                if(Math.abs(deltaAngle) > Math.toRadians(2)){
                    inputTurn = (deltaAngle * .5 + Math.signum(deltaAngle) * .4);
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
        public String toString(){
            return "wait";
        }
        });
    }

    void nextState(){
        if(current < actions.size()-1){
            actions.get(current).end();
            current++;
            actions.get(current).start();
        }
        else{
            actions.get(current).end();
            current++;
            mc.backLeft.setPower(0);
            mc.backRight.setPower(0);
            mc.frontLeft.setPower(0);
            mc.frontRight.setPower(0);
            ended = true;
            actions.add(new RobotAction(){
            double tx = odo.getX();
            double ty = odo.getY();
            double tAngle = odo.getAngle();
                
            @Override
            void start(){
                tx = odo.getX();
                ty = odo.getY();
                tAngle = odo.getAngle();
            }
            @Override
            void update(){
                double v = vs.getVoltage();
                v = 12 / v;
                double deltaX = tx - odo.getX();
                double deltaY = ty - odo.getY();
                double dist = deltaX * deltaX + deltaY * deltaY;
                dist = Math.sqrt(dist);

                

                double v1x = Math.cos(odo.getAngle());
                double v1y = Math.sin(odo.getAngle());

                double v2x = Math.cos(tAngle);
                double v2y = Math.sin(tAngle);


                double deltaAngle = Math.atan2((v2y * v1x) - (v2x * v1y), (v1x * v2x) + (v1y * v2y));
                if(dist < .4 && Math.abs(deltaAngle) < Math.toRadians(2)){
                    //nextState();
                }
                if(dist > 10) dist = 10;
                double inputTheta = Math.atan2(deltaY, deltaX) + Math.PI / 2;
                double inputPower =  (dist * .1) * v;
                double inputTurn = 0;

                if(Math.abs(deltaAngle) > Math.toRadians(2)){
                    inputTurn = (deltaAngle * .5 + Math.signum(deltaAngle) * .3) * v;
                }
                double sin = Math.sin(inputTheta - Math.PI/4 - odo.getAngle());
                double cos = Math.cos(inputTheta - Math.PI/4 - odo.getAngle());
                double max = Math.max(Math.abs(cos), Math.abs(sin));


                inputPower = Range.clip(inputPower, -.3,.3);
                double frontLeft = inputPower * cos/max - inputTurn;
                double frontRight = inputPower * sin/max + inputTurn;
                double backLeft = inputPower * sin/max - inputTurn;
                double backRight = inputPower * cos/max + inputTurn;
                
                mc.frontLeft.setPower(frontLeft);
                mc.frontRight.setPower(frontRight);
                mc.backLeft.setPower(backLeft);
                mc.backRight.setPower(backRight);
                
                //mc.frontLeft.setPower(0);
                //mc.frontRight.setPower(0);
                //mc.backLeft.setPower(0);
                //mc.backRight.setPower(0);
                
                
            }
        });
        }
    }

}
class RobotAction{
    void start(){};
    void update(){};
    void end(){};
}
