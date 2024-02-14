package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.roboticslib.motion.MecanumMotionController;
import com.roboticslib.motion.MecanumPID;
import com.roboticslib.motion.PIDController;
import com.roboticslib.motion.ThreeWheelOdo;

import org.firstinspires.ftc.teamcode.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.vision.*;

import java.util.List;
@Autonomous

public class BlueLeft extends OpMode {
    //TODO: Use computer vision to detect team prop
    private RobotHardware robot;
    private ThreeWheelOdo odo;
    private List<LynxModule> hubs; // Used for bulk reading

    // TODO: Scale motor power based on voltage
    private VoltageSensor voltage;

    private HuskyLens huskyLens;

    MecanumMotionController mmc;
    MecanumPID pid;
    
    public WebcamName webcamName;
    CompetitionBlueLeft pipeline;
    public VisionPortal portal;
    @Override
    public void init() {
        pipeline = new CompetitionBlueLeft(telemetry);
        portal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"),pipeline);
        
        //voltage = hardwareMap.voltageSensor.iterator().next();
        robot = new RobotHardware(hardwareMap);
        robot.mc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.mc.enableBrakeMode(false);
        robot.mc.reverseFL(true);
        robot.mc.reverseFR(false);
        robot.mc.reverseBL(true);
        robot.mc.reverseBR(false);
        robot.mc.resetEncoders();

        robot.lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.lift.setDirection(DcMotor.Direction.REVERSE);
        robot.lift.setTargetPosition(0);
        robot.lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.lift.setVelocity(1300);
        robot.lift.setTargetPosition(0);
        robot.unretract();


        odo = new ThreeWheelOdo(robot.mc.getBL(), robot.mc.getFL(), robot.mc.getBR(), 2000, 1.88976);

        // Increase diameter if angle offset is positive, decrease if negative
        odo.configure(-6.8, 6.8, -4.92);
        odo.reverseEncoders(false, true, false);

        hubs = hardwareMap.getAll(LynxModule.class);
        for(LynxModule hubs : hubs){
            hubs.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        robot.openClaw();
        robot.wristDown();

        PIDController xPid = new PIDController(.12,.9,.01);//(.1,0.08,.01);
        PIDController yPid = new PIDController(.14,.9, .01);//(.12,0.08,.01);

        PIDController thetaPID = new PIDController(2,.9,0);
        pid = new MecanumPID(robot.mc, odo);
        pid.v = hardwareMap.voltageSensor.iterator().next().getVoltage() / 12.0;
        pid.setPID(xPid, yPid);
        pid.setTurnPID(thetaPID);
        pid.maxAngSpeed = .5;
        pid.maxSpeed = .6;
        mmc = new MecanumMotionController(pid);

        robot.closePlane();
    }

    int parking = 3;
    @Override
    public void init_loop() {
        for(LynxModule hubs : hubs){
            hubs.clearBulkCache();
        }
        robot.mc.updateEncoders();
        odo.update();

        int detected = pipeline.position;
        parking = detected;
        if(parking == 0) telemetry.addLine("initializing webcam");
        else telemetry.addData("parking", parking);
        telemetry.addLine(odo.toString());
        telemetry.update();

        // Arm Controls
        if(gamepad2.left_trigger > .2) robot.closeClaw();
        if(gamepad2.right_trigger > .2) robot.openClaw();

        //if(gamepad2.dpad_up) robot.wrist.setPosition();
        if(gamepad2.dpad_down) robot.wristDown();

        robot.hanger.setPower(gamepad2.right_stick_y * .4);

    }

    @Override
    public void start() {
        robot.hanger.setPower(0);
        robot.mc.resetEncoders();
        odo.setPosition(0,0);
        odo.setAngle(0);

        mmc.waitForSeconds(.2, () -> robot.closeClaw());
        mmc.waitForSeconds(.4, () -> robot.wrist.setPosition(.9));

         //parking = 1;  // Hard coded value for testing

        if(parking == 1){
            one();
        }
        else if(parking == 2){
            two();
        }
        else if(parking == 3){
            three();
        }

        mmc.start();
    }
        void one(){
        mmc.moveTo(17.5,6,0);
        mmc.waitForSeconds(.5);
        mmc.waitForSeconds(.5, () -> robot.dropOnePixel());
        mmc.waitForSeconds(.5, () -> robot.closeClaw());
        mmc.waitForSeconds(.5, () -> robot.wristUp());
        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(1100));
        mmc.moveTo(14,6,0);
        mmc.waitForSeconds(.3);
        mmc.moveTo(18,25,90);
        mmc.waitForSeconds(.3);
        mmc.moveTo(21.5,28,90);
        mmc.waitForSeconds(.3);
        mmc.moveTo(21.5,36.5,90);
        mmc.waitForSeconds(1);
        mmc.waitForSeconds(1, () -> robot.openClaw());
        mmc.waitForSeconds(.3);
        mmc.moveTo(21.5,28,90);
        mmc.waitForSeconds(.5, () -> robot.wristDown());
        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(0));
        mmc.waitForSeconds(.3);
        mmc.moveTo(3,28,0);
        mmc.waitForSeconds(.3);
        mmc.moveTo(3,45,0);
        
        

    }

    void two(){
        mmc.moveTo(24,1,0);
        mmc.waitForSeconds(.3);
        mmc.moveTo(30,1,0);
        mmc.waitForSeconds(.5);
        mmc.waitForSeconds(.5, () -> robot.dropOnePixel());
        mmc.waitForSeconds(.5, () -> robot.closeClaw());
        mmc.waitForSeconds(.5, () -> robot.wristUp());
        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(1100));
        mmc.moveTo(14,6,0);
        mmc.waitForSeconds(.3);
        mmc.moveTo(26,25,90);
        mmc.waitForSeconds(.3);
        mmc.moveTo(26,28,90);
        mmc.waitForSeconds(.3);
        mmc.moveTo(26,36.5,90);
        mmc.waitForSeconds(1);
        mmc.waitForSeconds(1, () -> robot.openClaw());
        mmc.waitForSeconds(.5, () -> robot.wristDown());
        mmc.waitForSeconds(.3);
        mmc.moveTo(26,28,90);
        mmc.waitForSeconds(.5, () -> robot.wristDown());
        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(0));
        mmc.waitForSeconds(.3);
        mmc.moveTo(3,28,0);
        mmc.waitForSeconds(.3);
        mmc.moveTo(3,45,0);
    }

    void three(){
        mmc.moveTo(28,5,90);
        mmc.waitForSeconds(.3);
        mmc.moveTo(28,4,90);
        mmc.waitForSeconds(.3);
        mmc.moveTo(28,8,90);
        mmc.waitForSeconds(.5);
        mmc.waitForSeconds(.5, () -> robot.dropOnePixel());
        mmc.waitForSeconds(.5, () -> robot.closeClaw());
        mmc.moveTo(28,0,90);
        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(1100));
        mmc.moveTo(35,25,90);
        mmc.waitForSeconds(.5, () -> robot.wristUp());
        mmc.waitForSeconds(.4);
        mmc.moveTo(35,28,90);
        mmc.waitForSeconds(.3);
        mmc.moveTo(35,35,90);
        mmc.waitForSeconds(.6);
        mmc.waitForSeconds(1, () -> robot.openClaw());
         mmc.moveTo(35,28,90);
        mmc.waitForSeconds(.5, () -> robot.wristDown());
        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(0));
        mmc.waitForSeconds(.3);
        mmc.moveTo(3,28,0);
        mmc.waitForSeconds(.3);
        mmc.moveTo(3,45,0);
        
    }


    @Override
    public void loop() {
        for(LynxModule hubs : hubs){
            hubs.clearBulkCache();
        }
        robot.mc.updateEncoders();
        odo.update();
        mmc.update();
        telemetry.addLine(odo.toString());
        telemetry.addData("parking", parking);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        portal.close();
    }

}