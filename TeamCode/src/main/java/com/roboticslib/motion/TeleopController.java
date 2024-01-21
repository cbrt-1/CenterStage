package com.roboticslib.motion;

import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class TeleopController {

    private final MecanumChassis mc;
    private final IMU imu;
    public double botHeading = 0;
    public TeleopController(MecanumChassis mc, IMU imu){
        this.mc = mc;
        this.imu = imu;
    }

    public void update(double inputTheta, double inputPower, double turnPower){
        botHeading = getBotHeading();
        double sin = Math.sin(inputTheta - Math.PI/4 - botHeading);
        double cos = Math.cos(inputTheta - Math.PI/4 - botHeading);

        double maxed = Math.max(Math.abs(cos), Math.abs(sin));

        double frontLeft = inputPower * cos/maxed - turnPower;
        double frontRight = inputPower * sin/maxed + turnPower;
        double backLeft = inputPower * sin/maxed - turnPower;
        double backRight = inputPower * cos/maxed + turnPower;

        double max1 = Math.max(Math.abs(frontLeft), Math.abs(frontRight));
        double max2 = Math.max(Math.abs(backLeft), Math.abs(backRight));
        double max = Math.max(1, Math.max(max1, max2));

        mc.getFL().setPower(frontLeft / max);
        mc.getFR().setPower(frontRight / max);
        mc.getBL().setPower(backLeft / max);
        mc.getBR().setPower(backRight / max);
    }

    public double getBotHeading(){
        botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        return botHeading;
    }

    public void resetYaw(){
        imu.resetYaw();
    }
}
