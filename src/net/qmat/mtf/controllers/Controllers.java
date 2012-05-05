/*
 * The Controllers class is a singleton that allows the right controllers to be
 * found throughout the application.
 */

package net.qmat.mtf.controllers;

public class Controllers {

	private static Controllers instance = null;
	
	private OscController oscController;
	
	protected Controllers() {
		oscController = new OscController();
		oscController.start();
	}
	
	public static void update() {
		Controllers controllers = getInstance();
		//controllers.orbController.update();
	}
	
    public static Controllers getInstance() {
        /* N.B. I'm not doing any checking here because it'll force everyone 
         * to deal with Exception stuff throughout Eclipse. Just be sure to 
         * call init() in Main's setup().
	    if(instance == null) {
            throw new Exception("The Models singleton hasn't been initialized yet.");
        }
        */
    	return instance;
    }
    
    public static void init() {
    	if(instance == null) {
    		instance = new Controllers();
    	}
    }
    
    /*
     * Getter methods for the controllers.
     */	
	public static OscController getOscController() {
		return getInstance().oscController;
	}

}
