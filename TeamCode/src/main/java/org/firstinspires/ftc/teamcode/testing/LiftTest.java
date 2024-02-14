package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
@Disabled
public class LiftTest extends OpMode {

    private DcMotorEx lift = null;
    @Override
    public void init() {
        lift = hardwareMap.get(DcMotorEx.class, "lift");
    }

    @Override
    public void loop() {
        lift.setPower(gamepad1.right_stick_y * .5);
    }

}