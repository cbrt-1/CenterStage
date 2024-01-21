package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
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
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvPipeline;

@TeleOp

public class DetectBlue extends OpMode {
    // TODO: Rewrite into standalone class
    public WebcamName webcamName;
    OpenCvCamera camera;
    BluePropPipeline pipeline;
    @Override
    public void init() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);
        pipeline = new BluePropPipeline(telemetry);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(640,480);
            }
            @Override
            public void onError(int errorCode)
            {
                telemetry.addLine("CAMERA ERROR");
            }
        });
        camera.setPipeline(pipeline);

    }


    @Override
    public void init_loop() {

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
        camera.closeCameraDevice();
    }
}




 class BluePropPipeline extends OpenCvPipeline {
	/* 	Camera Notes:
		---------------
	 	Ratio: 640x480
	 	Resolution: 720p
	 	FPS: 30
	 */

    // Tuning
    final boolean TUNING = false;
    Mat greyScale = new Mat();

    // Constants
    static final Scalar BLUE = new Scalar(0, 0, 255);
    static final Scalar GREEN = new Scalar(0, 255, 0);

    // Matrices
    Mat HSV = new Mat();
    Mat hue = new Mat();
    Mat threshold = new Mat();

    // Regions
    public Point TOP_LEFT = new Point(50,50);
    public Point BOTTOM_RIGHT = new Point(150, 150);
    Mat leftRegion = new Mat();

    // Threshold
    public int LOWER = 80;
    public int UPPER = 120;




    @Override
    public void init(Mat firstFrame) {
        inputToHSV(firstFrame);

        resizeRegions();
    }


    @Override
    public Mat processFrame(Mat inputMat) {
        inputToHSV(inputMat);

        if(TUNING){
            resizeRegions();
        }
        draw(threshold);
        telemetry.addData("hue",Core.mean(leftRegion));
        telemetry.update();

        //return inputMat;
        return threshold;
    }

    private void resizeRegions(){
        leftRegion = HSV.submat(new Rect(TOP_LEFT, BOTTOM_RIGHT));
    }

    private void inputToHSV(Mat input){
        Imgproc.cvtColor(input, HSV, Imgproc.COLOR_RGB2HSV);
        Core.extractChannel(HSV, hue, 0);
        Imgproc.blur(hue, hue, new Size(20, 20));
        Core.inRange(hue, new Scalar(LOWER, 100, 100), new Scalar(UPPER, 255, 255), threshold);

        //Imgproc.cvtColor(threshold, greyScale, Imgproc.COLOR_HSV2GRAY);
    }

    private void draw(Mat input){
        Imgproc.rectangle(
                input, // Buffer to draw on
                TOP_LEFT,
                BOTTOM_RIGHT,
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines
    }






    Telemetry telemetry;

    public BluePropPipeline(Telemetry telemetry) {
        this.telemetry = telemetry;
    }


}