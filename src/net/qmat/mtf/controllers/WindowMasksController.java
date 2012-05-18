package net.qmat.mtf.controllers;

import net.qmat.mtf.Main;
import net.qmat.mtf.models.Models;
import net.qmat.mtf.models.WindowMasksModel;
import net.qmat.mtf.utils.Settings;

public class WindowMasksController {
	
	boolean changeEqually = true;
	
	public WindowMasksController() {
	
	}
	
	public void update() {
		KeyController keyc = Controllers.getKeyController();
		
		// if no input was received, don't do all of the stuff below.
		if(!keyc.hadInput())
			return;
		
		/*
		 * Act on keystrokes 
		 */
		WindowMasksModel wmm = Models.getWindowMasksModel();
		// a - add mask
		if(keyc.keyPressedAndChangedP(65))
			wmm.addMask();
		// d - delete selected mask
		if(keyc.keyPressedAndChangedP(68))
			wmm.removeMask();
		// s - save masks to disk
		if(keyc.keyPressedAndChangedP(83))
			wmm.saveMasks();
		// h - toggle hide/show window mask borders
		if(keyc.keyPressedAndChangedP(72))
			wmm.toggleBorderHiding();
		// e - toggle resizing width and height together 
		if(keyc.keyPressedAndChangedP(69))
			changeEqually = !changeEqually;
		// arrow keys - resizing of selected mask
		// left arrow
		if(keyc.keyPressedP(37)) {
			wmm.changeWidth(-1);
			if(changeEqually)
				wmm.changeHeight(-1);
		}
		// right arrow
		if(keyc.keyPressedP(39)) {
			wmm.changeWidth(1);
			if(changeEqually)
				wmm.changeHeight(1);
		}
		// down arrow
		if(keyc.keyPressedP(40)) {
			wmm.changeHeight(-1);
			if(changeEqually)
				wmm.changeWidth(-1);
		}
		// up arrow
		if(keyc.keyPressedP(38)) {
			wmm.changeHeight(1);
			if(changeEqually)
				wmm.changeWidth(1);
		}
			
		
		/*
		 * Act on mouse movements 
		 */
		
		// Move the selected mask when the left mouse key is pressed.
		if(keyc.mouseLeft) {
			if(wmm.selectedMaskP()) {
				wmm.moveMask(Main.p.mouseX - keyc.lastMouseX, Main.p.mouseY - keyc.lastMouseY);
			}
		// Otherwise check if we should select a mask.
		} else {
			wmm.selectMask(Main.p.mouseX, Main.p.mouseY);
		}
	}
	
	
	
	
}
