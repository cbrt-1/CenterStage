package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
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

import java.util.List;
@Autonomous

public class BlueRight extends OpMode {
    //TODO: Use computer vision to detect team prop
    private RobotHardware robot;
    private ThreeWheelOdo odo;
    private List<LynxModule> hubs; // Used for bulk reading

    // TODO: Scale motor power based on voltage
    private VoltageSensor voltage;

    private HuskyLens huskyLens;

    MecanumMotionController mmc;
    MecanumPID pid;
    @Override
    public void init() {
        //voltage = hardwareMap.voltageSensor.iterator().next();
        robot = new RobotHardware(hardwareMap);

        robot.mc.enableBrakeMode(false);
        robot.mc.reverseFL(true);
        robot.mc.reverseFR(false);
        robot.mc.reverseBL(true);
        robot.mc.reverseBR(false);
        robot.mc.resetEncoders();

        robot.lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.lift.setTargetPosition(0);
        robot.lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.lift.setVelocity(1300);
        robot.lift.setTargetPosition(0);


        huskyLens = hardwareMap.get(HuskyLens.class, "huskyLensBlue");
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);

        odo = new ThreeWheelOdo(robot.mc.getBL(), robot.mc.getFL(), robot.mc.getBR(), 2000, 1.88976);

        // Increase diameter if angle offset is positive, decrease if negative
        odo.configure(-6.58, 6.58, -4.92);
        odo.reverseEncoders(true, false, false);

        hubs = hardwareMap.getAll(LynxModule.class);
        for(LynxModule hubs : hubs){
            hubs.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        robot.openClaw();
        robot.wristDown();

        PIDController xPid = new PIDController(.1,0.16,.01);
        PIDController yPid = new PIDController(.12,0.16,.01);

        PIDController thetaPID = new PIDController(1.3,.16,0);
        pid = new MecanumPID(robot.mc, odo);
        pid.setPID(xPid, yPid);
        pid.setTurnPID(thetaPID);
        pid.maxAngSpeed = .4;

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

        int detected = 3;
        HuskyLens.Block[] blocks = huskyLens.blocks();
        for (int i = 0; i < blocks.length; i++) {
            telemetry.addData("id " + i, blocks[i].toString());
            if(blocks[i].x > 170) detected = 1;
            else if(blocks[i].x > 70) detected = 2;
            else if(blocks[i].x < 100) detected = 3;
        }
        parking = detected;

        telemetry.addData("parking", parking);
        telemetry.addLine(odo.toString());
        telemetry.update();

        // Arm Controls
        if(gamepad2.left_trigger > .2) robot.closeClaw();
        if(gamepad2.right_trigger > .2) robot.openClaw();

        if(gamepad2.dpad_up) robot.wrist.setPosition(.27);
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
        mmc.waitForSeconds(.4, () -> robot.wrist.setPosition(.2));

        // parking = 3;  // Hard coded value for testing

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
        mmc.moveTo(10,5,30);

        mmc.moveTo(10,25,-180);

        mmc.moveTo(4,28,-180);
        mmc.waitForSeconds(.5);
        mmc.waitForSeconds(.5, () -> robot.dropOnePixel());
        mmc.waitForSeconds(.5, () -> robot.closeClaw());
        mmc.moveTo(15,28,-180);
        mmc.waitForSeconds(.4);
        mmc.moveTo(15,46,-180);
        mmc.waitForSeconds(4);
        mmc.moveTo(-60,46,-180);
        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(1000));
        mmc.waitForSeconds(.1, () -> robot.wristUp());
        mmc.moveTo(-60,18,-180);
        mmc.setMaxSpeed(.1);
        mmc.waitForSeconds(1);
        mmc.moveTo(-80,19,-180);
        mmc.waitForSeconds(.8);
        mmc.waitForSeconds(1, () -> robot.openClaw());
        mmc.moveTo(-60,19,-180);
        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(0));
        mmc.waitForSeconds(.1, () -> robot.wristDown());
        mmc.moveTo(-60,42,-180);
        mmc.moveTo(-90,46,-180);

    }

    void two(){
        mmc.moveTo(17,7,60);
        //mmc.waitForSeconds(.1);
        mmc.moveTo(17,30,180);
        mmc.waitForSeconds(.7);
        mmc.moveTo(17,37,180);
        mmc.waitForSeconds(.7);
        mmc.waitForSeconds(.5, () -> robot.dropOnePixel());
        mmc.waitForSeconds(.5, () -> robot.closeClaw());
        mmc.moveTo(17,46,-180);
        mmc.waitForSeconds(4);
        mmc.moveTo(-60,46,-180);
        // start drop

        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(1000));
        mmc.waitForSeconds(.1, () -> robot.wristUp());
        mmc.moveTo(-60,18,-180);
        mmc.setMaxSpeed(.1);
        mmc.waitForSeconds(.3);
        mmc.moveTo(-80,20,-180);
        mmc.waitForSeconds(.8);
        mmc.waitForSeconds(1, () -> robot.openClaw());
        mmc.moveTo(-60,20,-180);
        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(0));
        mmc.waitForSeconds(.1, () -> robot.wristDown());
        mmc.moveTo(-60,42,-180);
        mmc.waitForSeconds(.7);
        mmc.moveTo(-90,42,-180);

    }

    void three(){
        mmc.moveTo(6,5,0);
        mmc.waitForSeconds(.1);
        mmc.moveTo(6,5,90);
        mmc.waitForSeconds(.1);
        mmc.moveTo(6,43,90);
        mmc.moveTo(5,36,0);
        mmc.waitForSeconds(1);
        mmc.waitForSeconds(.5, () -> robot.dropOnePixel());
        mmc.waitForSeconds(.5, () -> robot.closeClaw());
        mmc.moveTo(10,50,-180);
        mmc.waitForSeconds(4);
        mmc.moveTo(-60,51,-180);

        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(1000));
        mmc.waitForSeconds(.1, () -> robot.wristUp());
        mmc.moveTo(-60,40,-180);
        mmc.setMaxSpeed(.1);
        mmc.waitForSeconds(.3);
        mmc.moveTo(-79,44,-180);
        mmc.waitForSeconds(.8);
        mmc.waitForSeconds(1, () -> robot.closeClaw());
        mmc.moveTo(-60,41,-180);
        mmc.waitForSeconds(.1, () -> robot.lift.setTargetPosition(0));
        mmc.waitForSeconds(.1, () -> robot.wristDown());
        mmc.moveTo(-60,59,-180);
        mmc.waitForSeconds(.7);
        mmc.moveTo(-88,59,-180);
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

    }

}