package com.roboticslib.motion;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.roboticslib.util.Mathf;

public class ThreeWheelOdo implements Odometry{
    private final DcMotor left;
    private final DcMotor right;
    private final DcMotor strafe;

    private double leftDisplacement = 0;
    private double rightDisplacement = 0;
    private double strafeDisplacement = 0;
    private final double TO_INCHES;

    private double x = 0;
    private double y = 0;
    private double angle = 0;

    public ThreeWheelOdo(DcMotor left, DcMotor right, DcMotor strafe, double ticksPerRot, double wheelDiamInches){
        this.left = left;
        this.right = right;
        this.strafe = strafe;
        TO_INCHES = (wheelDiamInches * Math.PI) / (ticksPerRot);
        prevLeftPos = left.getCurrentPosition();
        prevRightPos = right.getCurrentPosition();
        prevStrafePos = strafe.getCurrentPosition();
        angle = 0;
        
        x = 0;
        y = 0;
        angle = 0;
    }
    // public ThreeWheelOdo(MecanumChassis mc, int left, int right, int strafe, double ticksPerRot, double wheelDiamInches){
    //     if(left != right && right != strafe)
    //     throw new IndexOutOfBoundsException();
        
    //     DcMotor[] motors = new DcMotor[4];
    //     motors[mc.frontLeft.getPortNumber()] = mc.frontLeft;
    //     motors[mc.frontRight.getPortNumber()] = mc.frontRight;
    //     motors[mc.backLeft.getPortNumber()] = mc.backLeft;
    //     motors[mc.backRight.getPortNumber()] = mc.backRight;
        
    //     this.left = motors[left];
    //     this.right = motors[right];
    //     this.strafe = motors[strafe];
    //     TO_INCHES = (wheelDiamInches * Math.PI) / (ticksPerRot);
    //     prevLeftPos = this.left.getCurrentPosition();
    //     prevRightPos = this.right.getCurrentPosition();
    //     prevStrafePos = this.strafe.getCurrentPosition();
    // }
    
    double driveWidth = Double.MIN_VALUE;
    public void configure(double leftDisplacement, double rightDisplacement, double strafeDisplacement){
        this.leftDisplacement = leftDisplacement;
        this.rightDisplacement = rightDisplacement;
        this.strafeDisplacement = strafeDisplacement;
        driveWidth = (leftDisplacement - rightDisplacement);
    }
    
    int leftReversed = 1;
    int rightReversed = 1;
    int strafeReversed = 1;
    public void reverseEncoders(boolean left, boolean right, boolean strafe){
        leftReversed = (left) ? -1 : 1;
        rightReversed = (right) ? -1 : 1;
        strafeReversed = (strafe) ? -1 : 1;
    }

    private int prevLeftPos;
    private int prevRightPos;
    private int prevStrafePos;

    public void update(){
        double deltaL = (left.getCurrentPosition() - prevLeftPos);
        double deltaR = (right.getCurrentPosition() - prevRightPos);
        double deltaS = (strafe.getCurrentPosition() - prevStrafePos);
        
        deltaL = deltaL * leftReversed * TO_INCHES;
        deltaR = deltaR * rightReversed * TO_INCHES;
        deltaS = deltaS * strafeReversed * TO_INCHES;
        
        double deltaX = (deltaR * leftDisplacement - deltaL * rightDisplacement) / driveWidth;
        double deltaTheta = (deltaR - deltaL) / driveWidth;
        angle += deltaTheta;
        
        if(angle > Math.PI || angle < -Math.PI)
        {
            angle = Mathf.angleWrap(angle);
        }

        double deltaY = deltaS - (strafeDisplacement * deltaTheta);
        
        double rX = (deltaX * Math.cos(angle)) - (deltaY * Math.sin(angle));
        double rY = (deltaX * Math.sin(angle)) + (deltaY * Math.cos(angle));
        x += rX;
        y += rY;
        
        prevLeftPos = left.getCurrentPosition();
        prevRightPos = right.getCurrentPosition();
        prevStrafePos = strafe.getCurrentPosition();
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getAngle() {
        return angle;
    }

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setAngle(double angle){
        this.angle = angle;
    }
    @Override
    public String toString(){
        return "X: " + x + "\nY: " + y + "\nAngle: " + Math.toDegrees(angle);
    }
}
