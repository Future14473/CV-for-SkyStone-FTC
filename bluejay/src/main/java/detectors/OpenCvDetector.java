package detectors;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

import detectors.FoundationPipeline.Foundation;
import detectors.FoundationPipeline.Pipeline;
import detectors.FoundationPipeline.SkyStone;
import detectors.FoundationPipeline.Stone;

public class OpenCvDetector extends StartStoppable {

	//Originally in RobotControllerActivity, but caused the camera shutter to make weird noises, so now it lives here
	static {
		//DynamicOpenCvNativeLibLoader.loadNativeLibOnStartRobot();
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	//This is a reference to the camera
	private OpenCvCamera phoneCam;

	//OpMode
	com.qualcomm.robotcore.eventloop.opmode.OpMode OpMode;

	public OpenCvDetector (com.qualcomm.robotcore.eventloop.opmode.OpMode opMode){
		this(opMode, true);
	}

	public  OpenCvDetector (com.qualcomm.robotcore.eventloop.opmode.OpMode opmode, boolean showVideo) {
		OpMode = opmode;

		//init EOCV
		int cameraMonitorViewId = OpMode.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", OpMode.hardwareMap.appContext.getPackageName());
		if(showVideo) phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
		else phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK);
		Pipeline.doFoundations = false;
		Pipeline.doStones = false;
		Pipeline.doSkyStones = true;

		phoneCam.setPipeline(new OpenCvPipeline() {
			@Override
			public Mat processFrame(Mat input) {
				Log.d("ROBOT","RUN_________________");
				return Pipeline.process(input);
			}
		});

		phoneCam.openCameraDevice();
	}

	@Override
	public void loop() {
		//will be called repeatedly when detector is active

	}

	//will be called when detector is activated
	@Override
	public void begin() {
		Log.d("ROBOT","BEGIN_________________");
		phoneCam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
	}

	//will be called when detector is ended
	@Override
	public void end() {
		phoneCam.stopStreaming();
		phoneCam.closeCameraDevice();
	}

	/*
	 * hold the phone sideways w/ camera on right
	 * x: 0 at the top, increases as you go down
	 * y: 0 at the right, increases as you go left
	 */

	public Foundation[] getFoundations() {
		if (!activated) throw new IllegalStateException("Not activated");

		return Pipeline.foundations.toArray(new Foundation[0]);
	}

	public Stone[] getStones() {
		if (!activated) throw new IllegalStateException("Not activated");

		return Pipeline.stones.toArray(new Stone[0]);
	}

	public SkyStone[] getSkyStones() {
		if (!activated) throw new IllegalStateException("Not activated");

		return Pipeline.skyStones.toArray(new SkyStone[0]);
	}
}
