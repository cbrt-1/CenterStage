package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import org.firstinspires.ftc.robotcore.external.StateMachine;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.*;
import com.roboticslib.motion.*;
import com.roboticslib.statemachine.*;
@Autonomous

public class PIDTester extends OpMode {
    // support
    private MecanumChassis mc;
    private ThreeWheelOdo odo;
    private List<LynxModule> hubs;
    private VoltageSensor voltage;
    
    
    
    MecanumPID pid;
    MecanumMotionController mmc = null;
    @Override
    public void init() {
        voltage = hardwareMap.get(VoltageSensor.class, "Control Hub");
        
        mc = new MecanumChassis(hardwareMap);
        mc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        mc.enableBrakeMode(false);
        mc.reverseFL(true);
        mc.reverseFR(false);
        mc.reverseBL(true);
        mc.reverseBR(false);
        mc.resetEncoders();
        
        odo = new ThreeWheelOdo(mc.getBL(), mc.getFL(),mc.getBR(), 2000, 1.88976);
        
        
        
        //odo.configure(-6.55, 6.55, -4.92);
        odo.configure(-6.8, 6.8, -4.92);
        odo.reverseEncoders(false, true, false);
        
        hubs = hardwareMap.getAll(LynxModule.class);
        for(LynxModule hubs : hubs){
            hubs.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
        PIDController xPid = new PIDController(.1,.9,0);//(.1,0.08,.01);
        PIDController yPid = new PIDController(.12,.9,0);//(.12,0.08,.01);

        PIDController thetaPID = new PIDController(2,.9,0);
        pid = new MecanumPID(mc, odo);
        pid.v = 12.0 / hardwareMap.voltageSensor.iterator().next().getVoltage();
        
        pid.setPID(xPid, yPid);
        pid.setTurnPID(thetaPID);
        pid.maxAngSpeed = .5;
        pid.maxSpeed = .7;
        
        
        
        
        mmc = new MecanumMotionController(pid);
    }

    @Override
    public void init_loop() {
        for(LynxModule hubs : hubs){
            hubs.clearBulkCache();
        }
        mc.updateEncoders();
        odo.update();
        telemetry.addLine(mc.toString());
        telemetry.addLine(odo.toString());
        telemetry.addData("Volts", hardwareMap.voltageSensor.iterator().next().getVoltage());
        telemetry.update();
        
    
        
    }

    @Override
    public void start() {
        mmc.moveTo(20,10,0);
        //mmc.moveTo(0,0,0);
        mmc.start();
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
        telemetry.addData("x:",mc.getFL().getPower());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }

}
