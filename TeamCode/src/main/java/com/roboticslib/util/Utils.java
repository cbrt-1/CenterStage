package com.roboticslib.util;

import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

public final class Utils {

    public static void setServoRange(Servo servo, double lower, double upper){
        ServoImplEx s = (ServoImplEx) servo;
        s.setPwmRange(new PwmControl.PwmRange(lower,upper));
    }

    public static void maxServoRange(Servo servo){
        setServoRange(servo,500, 2500);
    }
}
