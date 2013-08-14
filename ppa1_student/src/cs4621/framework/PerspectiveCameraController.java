package cs4621.framework;

import java.awt.event.MouseEvent;

public class PerspectiveCameraController extends CameraController {
	private PerspectiveCamera perspectiveCamera;
	
	public PerspectiveCameraController(PerspectiveCamera camera, GLSceneDrawer drawer) {
		super(camera, drawer);
		this.perspectiveCamera = camera;
	}
		
	protected void processMouseDragged(MouseEvent e) {
		if (mode == TRANSLATE_MODE) {	
			perspectiveCamera.convertMotion(mouseDelta, worldMotion);
			perspectiveCamera.translate(worldMotion);
		} else if (mode == ZOOM_MODE) {
			perspectiveCamera.zoom(mouseDelta.y);
		} else if (mode == ROTATE_MODE) {
			perspectiveCamera.orbit(mouseDelta);
		}		
	}	
}
