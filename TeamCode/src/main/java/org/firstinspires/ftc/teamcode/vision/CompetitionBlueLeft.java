package org.firstinspires.ftc.teamcode.vision;

import android.graphics.Canvas;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.*;
import org.opencv.core.Mat;
import org.firstinspires.ftc.robotcore.external.hardware.camera.*;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.openftc.easyopencv.OpenCvPipeline;
import org.firstinspires.ftc.robotcore.external.Telemetry;


public class CompetitionBlueLeft implements VisionProcessor {
    public volatile int position = 0;
    private final boolean TUNING = true;


    public Rect leftRect = new Rect(232,266,42,74);
    public Rect middleRect = new Rect(540,244,74,53);


    private Mat left = new Mat();
    private Mat middle = new Mat();
    private Mat right = new Mat();

    private Mat blue = new Mat();

    int avg1;
    int avg2;
    int avg3;

    private final Scalar BLUE = new Scalar(0, 0, 255);
    private final Scalar GREEN = new Scalar(0, 255, 0);
    @Override
    public void init(int width, int height, CameraCalibration calibration) {


    }
    int max = 0;
    @Override
    public Object processFrame(Mat inputMat, long ms) {
        Imgproc.cvtColor(inputMat, blue, Imgproc.COLOR_RGB2HSV);
        if(TUNING) resizeRegions();
        avg1 = (int) Core.mean(left).val[1];
        avg2 = (int) Core.mean(middle).val[1];
        max = Math.max(avg1, avg2);
        if(max == avg1) position = 1;
        if(max == avg2) position = 2;
        if(max < 50) position = 3;
        render(inputMat);
        //telemetry.addData("position", position);
        //telemetry.update();
        return inputMat;
    }
    public void onDrawFrame(Canvas canvas, int width, int height, float canvasPx, float pxDensity, Object userContext){

    }

    public void resizeRegions(){
        left = blue.submat(leftRect);
        middle = blue.submat(middleRect);

    }

    public void extractBlue(Mat input){
        Core.extractChannel(input, blue, 1);
        Imgproc.blur(blue, blue, new Size(3, 3));
    }

    public void render(Mat input){
        Imgproc.rectangle(
                input, // Buffer to draw on
                leftRect.tl(), // First point which defines the rectangle
                leftRect.br(), // Second point which defines the rectangle
                (position == 1) ? GREEN : BLUE, // The color the rectangle is drawn in
                (position == 1) ? -1 : 2); // Thickness of the rectangle lines
        Imgproc.rectangle(
                input, // Buffer to draw on
                middleRect.tl(), // First point which defines the rectangle
                middleRect.br(), // Second point which defines the rectangle
                (position == 2) ? GREEN : BLUE, // The color the rectangle is drawn in
                (position == 2) ? -1 : 2); // Thickness of the rectangle lines
    }


    Telemetry telemetry;

    public CompetitionBlueLeft(Telemetry telemetry) {
        this.telemetry = telemetry;
    }


}
