package com.roboticslib.motion;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.roboticslib.util.Mathf;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class TwoWheelOdo implements Odometry{
    private final DcMotor fwd;
    private final DcMotor strafe;
    private final IMU imu;

    private double fwdDisplacement = 0;
    private double strafeDisplacement = 0;
    private final double TO_INCHES;
    private double angleOffset = 0;

    private double x = 0;
    private double y = 0;
    private double angle = 0;

    public TwoWheelOdo(DcMotor fwd, DcMotor strafe, IMU imu, double ticksPerRot, double wheelDiamInches){
        this.fwd = fwd;
        this.strafe = strafe;
        this.imu = imu;
        TO_INCHES = (wheelDiamInches * Math.PI) / (ticksPerRot);
        prevFwdPos = fwd.getCurrentPosition();
        prevStrafePos = strafe.getCurrentPosition();
        
        x = 0;
        y = 0;
        angle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public void configure(double leftDisplacement, double strafeDisplacement){
        this.fwdDisplacement = leftDisplacement;
        this.strafeDisplacement = strafeDisplacement;
    }
    
    int fwdReversed = 1;
    int strafeReversed = 1;

    public void reverseEncoders(boolean forward, boolean strafe){
        fwdReversed = (forward) ? -1 : 1;
        strafeReversed = (strafe) ? -1 : 1;
    }

    private int prevFwdPos;
    private int prevStrafePos;
    private double prevAngle;

    public void update(){
        angle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        double deltaF = (fwd.getCurrentPosition() - prevFwdPos);
        double deltaS = (strafe.getCurrentPosition() - prevStrafePos);
        double deltaTheta = Mathf.angleWrap(angle - prevAngle);
        
        deltaF = deltaF * fwdReversed * TO_INCHES;
        deltaS = deltaS * strafeReversed * TO_INCHES;
        
        double deltaX = deltaF - (fwdDisplacement * deltaTheta);
        double deltaY = deltaS - (strafeDisplacement * deltaTheta);
        
        double rX = (deltaX * Math.cos(angle)) - (deltaY * Math.sin(angle));
        double rY = (deltaX * Math.sin(angle)) + (deltaY * Math.cos(angle));
        x += rX;
        y += rY;
        
        prevFwdPos = fwd.getCurrentPosition();
        prevStrafePos = strafe.getCurrentPosition();
        prevAngle = angle;

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

    //TODO: use angle offset to offset the imu angle when performing calculations
    //public void setAngle(double angle){
    //    this.angle = angle;
    //}
    @Override
    public String toString(){
        return "X: " + x + "\nY: " + y + "\nAngle: " + Math.toDegrees(angle);
    }
}
