package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.roboticslib.motion.TeleopController;
import com.roboticslib.util.Mathf;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.hardware.RobotHardware;

@TeleOp

public class Teleop extends OpMode {

    private RobotHardware robot;
    private TeleopController chassis;

    // Lift Limits
    private final double upperLimit = 1500;
    private final double lowerLimit = 0;

    // Driving
    private boolean autoCorrect = false;
    private double targetHeading = 0;

    // Gripper
    private double onePixelGrab = 0.19;
    private double delay = .8;
    private ElapsedTime timer;
    private boolean wristUp = false;
    private boolean clawClosed = false;

    @Override
    public void init() {
        robot = new RobotHardware(hardwareMap);
        chassis = new TeleopController(robot.mc);
        robot.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wristDown();
        robot.openClaw();
        robot.closePlane();

        timer = new ElapsedTime();
    }

    @Override
    public void init_loop() {

    }
    @Override
    public void start() {
    }

    @Override
    public void loop() {

        // DRIVING
        if(gamepad1.dpad_up) robot.imu.resetYaw();
        double botHeading = robot.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);;

        //TODO: Add a button that slows the robot down
        double inputTheta = Math.atan2(-gamepad1.left_stick_y, gamepad1.left_stick_x);
        double inputPower = Math.sqrt(gamepad1.left_stick_y * gamepad1.left_stick_y + gamepad1.left_stick_x * gamepad1.left_stick_x);;
        double inputTurn = -gamepad1.right_stick_x * .7;;

        autoCorrectTurn();

        if(autoCorrect) {
            double deltaAngle = targetHeading - botHeading;
            deltaAngle = Mathf.angleWrap(deltaAngle);

            if(Math.abs(deltaAngle) > Math.toRadians(3)) {
                inputTurn = deltaAngle * .75;
            }
            else {
                inputTurn = 0;
            }
        }

        inputTurn = Range.clip(inputTurn, -.5, .5);

        chassis.fieldCentric(inputTheta,inputPower,inputTurn, botHeading);

        // GUNNER CONTROLS
        double hangerPower = gamepad2.left_stick_y;
        robot.hanger.setPower(hangerPower);

        //TODO: Add limits for viper slide
        //TODO: Add limit switch that resets the lift encoder in case of viper slides belt slipping

        double pulleyPower = -gamepad2.right_stick_y;
        robot.lift.setPower(pulleyPower);

        if(gamepad2.y) robot.openPlane();

        // ARM CONTROLS
        if(gamepad2.dpad_up) {
            robot.wristUp();
            wristUp = true;
        }
        else if(gamepad2.dpad_down) {
            robot.wristDown();
            wristUp = false;
        }

        if(gamepad2.left_trigger > .2){
            robot.closeClaw();
            clawClosed = true;
        }

        if(gamepad2.right_trigger > .2 && !wristUp){
            robot.openClaw();
            clawClosed = false;
        }

        /*
            When the wrist is up, we delay the drop of the second pixel by a delay.
            This allows us to drop both pixels on the backdrop.
         */
        if(gamepad2.right_trigger > .2 && wristUp){
            robot.claw.setPosition(onePixelGrab);
            timer.reset();
            clawClosed = true;
        }
        if(clawClosed && timer.seconds() > delay){
            robot.openClaw();
            clawClosed = false;
        }

    }

    @Override
    public void stop() {

    }

    public void autoCorrectTurn(){
        if(Math.abs(gamepad1.right_stick_x) > .05){
            autoCorrect = false;
        }
        if(gamepad1.x){
            autoCorrect = true;
            targetHeading = Math.PI /2;
        }
        if(gamepad1.y){
            autoCorrect = true;
            targetHeading = 0;
        }
        if(gamepad1.b){
            autoCorrect = true;
            targetHeading = -Math.PI /2;
        }
        if(gamepad1.a){
            autoCorrect = true;
            targetHeading = Math.PI;
        }
    }
}