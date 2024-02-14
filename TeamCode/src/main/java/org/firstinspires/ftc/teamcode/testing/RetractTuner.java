package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.hardware.*;

@TeleOp

public class RetractTuner extends OpMode{
    
    RobotHardware robot;
    public void init(){
        robot = new RobotHardware(hardwareMap);
        robot.retract();
    }
    public void loop(){
        robot.unretract();
    }
}