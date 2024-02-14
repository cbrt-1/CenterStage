package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.roboticslib.motion.MecanumChassis;

public class RobotHardware {

    /* CONSTANTS */
    private static final double OPEN_CLAW = .1;
    private static final double ONE_PIXEL_CLAW = .26;
    private static final double CLOSE_CLAW = .4;

    private static final double WRIST_UP = 1;
    private static final double WRIST_DOWN = .83;

    /* DRIVING */
    public MecanumChassis mc;
    public IMU imu;

    /* Arm */
    public DcMotorEx lift;
    public Servo claw;
    public Servo wrist;

    /* End Game Mechanisms */
    public DcMotorEx hanger;
    public Servo plane;

    /* Retract */
    public Servo strafeRetract;
    public Servo leftRetract;
    public Servo rightRetract;

    public RobotHardware(HardwareMap hardwareMap){
        // Drive Motors
        mc = new MecanumChassis(hardwareMap);
        mc.enableBrakeMode(true);
        mc.reverseFL(true);
        mc.reverseBL(true);

        // IMU
        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD));
        imu.initialize(parameters);

        // Hanger
        hanger = hardwareMap.get(DcMotorEx.class, "hanger");

        // Lift
        lift = hardwareMap.get(DcMotorEx.class, "lift");
        wrist = hardwareMap.get(Servo.class, "wrist");
        claw = hardwareMap.get(Servo.class, "claw");
        plane = hardwareMap.get(Servo.class, "plane");

        strafeRetract = hardwareMap.get(Servo.class, "strafeRetract");
        leftRetract = hardwareMap.get(Servo.class, "leftRetract");
        rightRetract = hardwareMap.get(Servo.class, "rightRetract");
    }
    

    public void resetYaw(){
        imu.resetYaw();
    }

    // Arm
    public void openClaw(){
        claw.setPosition(OPEN_CLAW);
    }

    public void dropOnePixel(){
        claw.setPosition(ONE_PIXEL_CLAW);
    }

    public void closeClaw(){
        claw.setPosition(CLOSE_CLAW);
    }

    public void wristUp(){
        wrist.setPosition(WRIST_UP);
    }

    public void wristDown(){
        wrist.setPosition(WRIST_DOWN);
    }

    // Plane
    public void closePlane(){
        plane.setPosition(.35);
    }
    public void openPlane(){
        plane.setPosition(.2);
    }
    public void retract(){
        strafeRetract.setPosition(.59);
        leftRetract.setPosition(.6);
        rightRetract.setPosition(.45);
    }
    public void unretract(){
        strafeRetract.setPosition(.56);
        leftRetract.setPosition(.53);
        rightRetract.setPosition(.9);
    }
    

}



