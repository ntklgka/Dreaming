package wrapper;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

/**
 * @author Andrei
 * Used for managing the display window
 */
public class DisplayManager {
	
	//initalize display variables
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS_CAP = 60;
	private static final String TITLE = "Dreaming";
	
	//used for movement
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay() {
		//used for specifying the version of OpenGL we're going to use. In this case it's 3.2.
		ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT)); //here we determine the width and height
			Display.create(new PixelFormat().withSamples(8).withDepthBits(24), attribs); //and then create the display with Anti-aliasing (MSAA)
			Display.setTitle(TITLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		GL11.glViewport(0, 0, WIDTH, HEIGHT); //tells OpenGL where to render the scene
		lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay() {
		Display.sync(FPS_CAP); //synchronises the scene to run at a steady fps
		Display.update();
		long currentFrameTime = getCurrentTime(); //gets the current frame time in ms
		delta = (currentFrameTime - lastFrameTime)/1000f; //how long the last frame took to render
		lastFrameTime = currentFrameTime; //make it ready for the next frame calculation
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	public static void closeDisplay() {
		Display.destroy();
	}
	
	
	private static long getCurrentTime() {
		//Sys.getTime() returns the time in ticks which we need to
		//divide by number ticks per second which is given by Sys.getTimerResolution()
		//we multiply it by 1000 to get the time in miliseconds
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}

}
