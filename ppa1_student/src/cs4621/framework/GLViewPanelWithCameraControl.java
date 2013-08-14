package cs4621.framework;

import javax.media.opengl.GLContext;

public class GLViewPanelWithCameraControl extends GLViewPanel {
	private static final long serialVersionUID = 1L;
	
	protected CameraController cameraController;
	protected PickingController pickingController;
	
	public GLViewPanelWithCameraControl(int initialFrameRate, CameraController cameraController, GLContext sharedWith)
	{
		super(initialFrameRate, sharedWith);
		this.cameraController = cameraController;
		this.pickingController = new PickingController(cameraController);
		addGLController(pickingController);		
	}
	
	public GLViewPanelWithCameraControl(int initialFrameRate, CameraController cameraController) {
		this(initialFrameRate, cameraController, null);
	}
	
	public CameraController getCameraController() {
		return cameraController;
	}
	
	public PickingController getPickingController() {
		return pickingController;
	}
	
	public void addPickingEventListener(PickingEventListener listener)
	{
		pickingController.addPickingEventListener(listener);
	}
	
	public void removePickingEventListener(PickingEventListener listener)
	{
		pickingController.removePickingEventListener(listener);
	}
	
	public void addPrioritizedObjectId(int id)
	{
		pickingController.addPrioritizedObjectId(id);
	}
	
	public void removePrioritizedObjectId(int id)
	{
		pickingController.removePrioritizedObjectId(id);
	}
}
