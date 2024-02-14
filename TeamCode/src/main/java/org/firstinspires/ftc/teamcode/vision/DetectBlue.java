package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.vision.VisionPortal;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

@TeleOp

public class DetectBlue extends OpMode {
    public WebcamName webcamName;
    CompetitionBlueLeft pipeline;
    public VisionPortal portal;
    @Override
    public void init() {
        pipeline = new CompetitionBlueLeft(telemetry);
        portal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"),pipeline);

    }


    @Override
    public void init_loop() {
        telemetry.addData("Data", pipeline.position);
    }
    @Override
    public void start() {

    }

    @Override
    public void loop() {
        //telemetry.addData("data", pipeline.getAnalysis());
        //telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        portal.close();
    }
}
