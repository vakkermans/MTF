package net.qmat.mtf.controllers;

import net.qmat.mtf.Main;

public class KeyController {

	// first dimension is key, second is [pressedP, changedP]
	private boolean[][] keys = new boolean[526][2];	
	
	public boolean mouseLeft = false;
	public boolean mouseRight = false;
	public boolean mouseMiddle = false;

	private boolean hadInputP = false;
	
	public KeyController() {
		initialiseKeys();
	}
	
	public boolean hadInput() {
		return hadInputP;
	}
	
	private void initialiseKeys() {
		for(int i=0; i<keys.length; i++) {
			keys[i] = new boolean[] {false, false};
		}
	}
	
	public void resetChangeFlags() {
		for(int i=0; i<keys.length; i++) {
			keys[i][1] = false;
		}
	}
	
	public boolean keyPressedP(int keyCode) {
		return keys.length > keyCode && keys[keyCode][0];
	}
	
	public boolean keyChangedP(int keyCode) {
		return keys.length > keyCode && keys[keyCode][1];
	}
	
	public boolean keyPressedAndChangedP(int keyCode) {
		return keys.length > keyCode && keys[keyCode][0] && keys[keyCode][1];
	}

	public void keyPressed(int keyCode) {
		if(keyCode >= keys.length) {
			System.err.println("Received an out of bounds keycode.");
			return;
		}
		// if the key was already marked as pressed, set the changed bit to false
		keys[keyCode][1] = keys[keyCode][0] ? false : true; 
		// save new key state
		keys[keyCode][0] = true;
		hadInputP = true;
	}

	public void keyReleased(int keyCode) {
		if(keyCode >= keys.length) {
			System.err.println("Received an out of bounds keycode.");
			return;
		}
		// if the key was already marked as not pressed, set the changed bit to false
		keys[keyCode][1] = !keys[keyCode][0] ? false : true; 
		// save new key state
		keys[keyCode][0] = false;
		hadInputP = true;
	}
	
	public void mousePressed(int mouseButton) {
		if(mouseButton == Main.LEFT)
			mouseLeft = true;
		else if(mouseButton == Main.RIGHT)
			mouseRight = true;
		else if(mouseButton == Main.CENTER)
			mouseMiddle = true;
		hadInputP = true;
	}	
	
	public void mouseReleased(int mouseButton) {
		if(mouseButton == Main.LEFT)
			mouseLeft = false;
		else if(mouseButton == Main.RIGHT)
			mouseRight = false;
		else if(mouseButton == Main.CENTER)
			mouseMiddle = false;
		hadInputP = true;
	}
	
}
