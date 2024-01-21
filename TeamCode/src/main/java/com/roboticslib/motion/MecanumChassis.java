package com.roboticslib.motion;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MecanumChassis {

    public final DcMotorEx frontLeft;
    public final DcMotorEx frontRight;
    public final DcMotorEx backLeft;
    public final DcMotorEx backRight;
    

    public MecanumChassis(HardwareMap hm){
        frontLeft = hm.get(DcMotorEx.class, "frontLeft");
        frontRight = hm.get(DcMotorEx.class, "frontRight");
        backLeft = hm.get(DcMotorEx.class, "backLeft");
        backRight = hm.get(DcMotorEx.class, "backRight");

    }
    
    public MecanumChassis(DcMotor fl, DcMotor fr, DcMotor bl, DcMotor br){
        frontLeft = (DcMotorEx)fl;
        frontRight = (DcMotorEx)fr;
        backLeft = (DcMotorEx)bl;
        backRight = (DcMotorEx)br;
    }

    int frontLeftPos;
    int frontRightPos;
    int backLeftPos;
    int backRightPos;

    public void updateEncoders(){
        frontLeftPos = frontLeft.getCurrentPosition();
        frontRightPos = frontRight.getCurrentPosition();
        backLeftPos = backLeft.getCurrentPosition();
        backRightPos = backRight.getCurrentPosition();
    }

    double frontLeftVelocity;
    double frontRightVelocity;
    double backLeftVelocity;
    double backRightVelocity;

    public void updateVelocities(){
        frontLeftVelocity = frontLeft.getVelocity();
        frontRightVelocity = frontRight.getVelocity();
        backLeftVelocity = backLeft.getVelocity();
        backRightVelocity = backRight.getVelocity();
    }

    public void setMode(DcMotor.RunMode mode){
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }
    
    public void enableBrakeMode(boolean enabled){
        DcMotor.ZeroPowerBehavior state = enabled ? DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT;
        
        frontLeft.setZeroPowerBehavior(state);
        frontRight.setZeroPowerBehavior(state);
        backLeft.setZeroPowerBehavior(state);
        backRight.setZeroPowerBehavior(state);
    }
    
    public void resetEncoders(){
        DcMotor.RunMode mode = frontLeft.getMode();

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }

    public void reverseFL(boolean reversed){
        frontLeft.setDirection((reversed) ? DcMotor.Direction.REVERSE : DcMotor.Direction.FORWARD);
    }

    public void reverseFR(boolean reversed){
        frontRight.setDirection((reversed) ? DcMotor.Direction.REVERSE : DcMotor.Direction.FORWARD);
    }

    public void reverseBL(boolean reversed){
        backLeft.setDirection((reversed) ? DcMotor.Direction.REVERSE : DcMotor.Direction.FORWARD);
    }

    public void reverseBR(boolean reversed){
        backRight.setDirection((reversed) ? DcMotor.Direction.REVERSE : DcMotor.Direction.FORWARD);
    }

    public DcMotorEx getFL() {
        return frontLeft;
    }

    public DcMotorEx getFR() {
        return frontRight;
    }

    public DcMotorEx getBL() {
        return backLeft;
    }

    public DcMotorEx getBR() {
        return backRight;
    }

    public int getFLPos() {
        return frontLeftPos;
    }

    public int getFRPos() {
        return frontRightPos;
    }

    public int getBLPos() {
        return backLeftPos;
    }

    public int getBRPos() {
        return backRightPos;
    }

    public double getFLVelocity() {
        return frontLeftVelocity;
    }

    public double getFRVelocity() {
        return frontRightVelocity;
    }

    public double getBLVelocity() {
        return backLeftVelocity;
    }

    public double getBRVelocity() {
        return backRightVelocity;
    }

    public String filteredToString(boolean printReversed, boolean printPositions, boolean printVelocities){
        int capacity = printReversed ? 64: 0;
        capacity += printPositions ? 64: 0;
        capacity += printVelocities ? 64: 0;
        StringBuilder sb = new StringBuilder(capacity);
        if(printReversed){
            sb.append("FL (Reversed): ");
            sb.append(frontLeft.getDirection() == DcMotor.Direction.REVERSE);
            sb.append('\n');
            sb.append("FR (Reversed): ");
            sb.append(frontRight.getDirection() == DcMotor.Direction.REVERSE);
            sb.append('\n');
            sb.append("BL (Reversed): ");
            sb.append(backLeft.getDirection() == DcMotor.Direction.REVERSE);
            sb.append('\n');
            sb.append("BR (Reversed): ");
            sb.append(backRight.getDirection() == DcMotor.Direction.REVERSE);
            sb.append("\n\n");
        }
        if(printPositions){
            sb.append("FL (Position): ");
            sb.append(frontLeftPos);
            sb.append('\n');
            sb.append("FR (Position): ");
            sb.append(frontRightPos);
            sb.append('\n');
            sb.append("BL (Position): ");
            sb.append(backLeftPos);
            sb.append('\n');
            sb.append("BR (Position): ");
            sb.append(backRightPos);
            sb.append("\n\n");
        }
        if(printVelocities){
            sb.append("FL (Velocity): ");
            sb.append(frontLeftVelocity);
            sb.append('\n');
            sb.append("FR (Velocity): ");
            sb.append(frontRightVelocity);
            sb.append('\n');
            sb.append("BL (Velocity): ");
            sb.append(backLeftVelocity);
            sb.append('\n');
            sb.append("BR (Velocity): ");
            sb.append(backRightVelocity);
            sb.append("\n\n");
        }
        return sb.toString();
    }

    public String toString(){
        return filteredToString(true, true, true);
    }
}
