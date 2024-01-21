package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.roboticslib.motion.MecanumChassis;
import com.roboticslib.motion.MecanumMotionController;
import com.roboticslib.motion.MecanumPID;
import com.roboticslib.motion.PIDController;
import com.roboticslib.motion.ThreeWheelOdo;

import java.util.List;
@Autonomous

public class BlueRight extends OpMode {
    // support
    private MecanumChassis mc;
    private ThreeWheelOdo odo;
    private List<LynxModule> hubs;
    private VoltageSensor voltage;

    //hardware
    private DcMotorEx hanger;
    private DcMotorEx lift;

    private HuskyLens huskyLens;
    private Servo claw;
    private Servo wrist;

    MecanumMotionController mmc;
    MecanumPID pid;
    @Override
    public void init() {
        voltage = hardwareMap.get(VoltageSensor.class, "Control Hub");

        mc = new MecanumChassis(hardwareMap);
        mc.enableBrakeMode(false);
        mc.reverseFL(true);
        mc.reverseFR(false);
        mc.reverseBL(true);
        mc.reverseBR(false);
        mc.resetEncoders();
        hanger = hardwareMap.get(DcMotorEx.class, "hanger");
        lift = hardwareMap.get(DcMotorEx.class, "lift");
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setTargetPosition(0);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setVelocity(1300);
        lift.setTargetPosition(0);
        huskyLens = hardwareMap.get(HuskyLens.class, "huskyLensBlue");

        huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);

        odo = new ThreeWheelOdo(mc.getBL(), mc.getFL(),mc.getBR(), 2000, 1.88976);
        // increase if possitive, decrease if negative
        // -4.92


        odo.configure(-6.58, 6.58, -4.92);
        odo.reverseEncoders(true, false, false);

        hubs = hardwareMap.getAll(LynxModule.class);
        for(LynxModule hubs : hubs){
            hubs.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        // // light
        // Servo light;
        // light = hardwareMap.get(Servo.class, "light");
        // light.setPosition(0);

        claw = hardwareMap.get(Servo.class, "claw");
        wrist = hardwareMap.get(Servo.class, "wrist");
        claw.setPosition(0);
        wrist.setPosition(.33);



        PIDController xPid = new PIDController(.1,0.16,.01);
        PIDController yPid = new PIDController(.12,0.16,.01);

        PIDController thetaPID = new PIDController(1.3,.16,0);
        pid = new MecanumPID(mc, odo);
        pid.setPID(xPid, yPid);
        pid.setTurnPID(thetaPID);
        pid.maxAngSpeed = .4;

        mmc = new MecanumMotionController(pid);
    }

    int parking = 3;
    @Override
    public void init_loop() {
        telemetry.addData("Encoder", lift.getCurrentPosition());
        for(LynxModule hubs : hubs){
            hubs.clearBulkCache();
        }
        mc.updateEncoders();
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
        //telemetry.addLine(mc.toString());
        telemetry.addLine(odo.toString());
        telemetry.update();

        if(gamepad2.right_trigger>.1)
        {
            claw.setPosition(.39);
        }
        if(gamepad2.left_trigger>.1)
        {
            claw.setPosition(.1);
        }

        if(gamepad2.dpad_up){
            wrist.setPosition(.27);
        }
        if(gamepad2.dpad_down){
            wrist.setPosition(.32);
        }
        hanger.setPower(gamepad2.right_stick_y * .4);

    }

    @Override
    public void start() {
        mmc.waitForSeconds(.2, () -> claw.setPosition(.4));
        mmc.waitForSeconds(.4, () -> wrist.setPosition(.2));
        parking = 3;
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
        mmc.moveTo(10,5,10);

        mmc.moveTo(10,25,-180);

        mmc.moveTo(4,28,-180);
        mmc.waitForSeconds(.5);
        mmc.waitForSeconds(.5, () -> claw.setPosition(.24));
        mmc.waitForSeconds(.5, () -> claw.setPosition(.4));
        mmc.moveTo(15,28,-180);
        mmc.waitForSeconds(.4);
        //mmc.moveTo(15,46,-180);
        mmc.waitForSeconds(.4);
        //mmc.moveTo(-60,46,-180);

    }

    void two(){
        mmc.moveTo(20,7,60);
        //mmc.waitForSeconds(.1);
        mmc.moveTo(20,30,180);
        mmc.waitForSeconds(.1);
        mmc.moveTo(20,39,180);
        mmc.waitForSeconds(.5);
        //mmc.waitForSeconds(.5, () -> claw.setPosition(RobotHardware.ONE_PIXEL));
        //mmc.waitForSeconds(.5, () -> claw.setPosition(RobotHardware.CLAW_CLOSE));

    }

    void three(){
        mmc.moveTo(4,18,0);
        mmc.waitForSeconds(.3);
        //mmc.waitForSeconds(.5, () -> claw.setPosition(RobotHardware.ONE_PIXEL));
        //mmc.waitForSeconds(.5, () -> claw.setPosition(RobotHardware.CLAW_CLOSE));
        mmc.waitForSeconds(.5, () -> wrist.setPosition(0));
        mmc.moveTo(4,40,0);
        mmc.waitForSeconds(.5, () -> wrist.setPosition(.2));
        // testing
        mmc.waitForSeconds(.5, () -> lift.setTargetPosition(1300));
        mmc.moveTo(14,40,0);
        mmc.waitForSeconds(5);
        mmc.waitForSeconds(.5, () -> lift.setTargetPosition(0));
    }


    @Override
    public void loop() {
        for(LynxModule hubs : hubs){
            hubs.clearBulkCache();
        }
        mc.updateEncoders();
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
