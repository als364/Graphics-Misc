package cs4621.framework;


import java.awt.event.MouseEvent;

public class OrthographicCameraController extends CameraController {
	protected OrthographicCamera orthographicCamera;
	
	public OrthographicCameraController(OrthographicCamera camera, GLSceneDrawer drawer) {
		super(camera, drawer);
		orthographicCamera = camera;
	}
		
	protected void processMouseDragged(MouseEvent e)
	{
		if (mode == TRANSLATE_MODE) {	
			orthographicCamera.convertMotion(mouseDelta, worldMotion);
			orthographicCamera.translate(worldMotion);
		} else if (mode == ZOOM_MODE) {
			orthographicCamera.zoom(mouseDelta.y);
		} 
	}
	
	public OrthographicCamera getOrthographicCamera()
	{
		return orthographicCamera;
	}
}
