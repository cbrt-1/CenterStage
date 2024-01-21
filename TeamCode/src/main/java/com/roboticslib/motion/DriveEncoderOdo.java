package com.roboticslib.motion;

import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class DriveEncoderOdo implements Odometry {
    private double x = 0;
    private double y = 0;
    private double angle = 0;
    private final MecanumChassis mc;
    private final IMU imu;

    private final double TO_INCHES;

    public DriveEncoderOdo(MecanumChassis mc, IMU imu, double ticksPerRot, double wheelDiamInches) {
        this.mc = mc;
        this.imu = imu;
        TO_INCHES = (wheelDiamInches * Math.PI) / (4 * ticksPerRot);
    }

    private int prevFLPos;
    private int prevFRPos;
    private int prevBLPos;
    private int prevBRPos;
    
    public void update() {
        angle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        int deltaFL = mc.frontLeftPos - prevFLPos;
        int deltaFR = mc.frontRightPos - prevFRPos;
        int deltaBL = mc.backLeftPos - prevBLPos;
        int deltaBR = mc.backRightPos - prevBRPos;

        double deltaX = (deltaFL + deltaFR + deltaBL + deltaBR) * TO_INCHES;
        double deltaY = (-deltaFL + deltaFR + deltaBL - deltaBR) * TO_INCHES;

        double rotatedX = (deltaX * cos) - (deltaY * sin);
        double rotatedY = (deltaX * sin) + (deltaY * cos);

        x += rotatedX;
        y += rotatedY;

        prevFLPos = mc.frontLeftPos;
        prevFRPos = mc.frontRightPos;
        prevBLPos = mc.backLeftPos;
        prevBRPos = mc.backRightPos;
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

    public void resetIMU(){
        imu.resetYaw();
    }

    @Override
    public String toString(){
        return "X: " + x + "\nY: " + y + "\nAngle: " + Math.toDegrees(angle);
    }
}
