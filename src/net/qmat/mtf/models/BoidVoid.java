package net.qmat.mtf.models;
import java.util.ArrayList;

import processing.core.PImage;

import net.qmat.mtf.Main;
import codeanticode.gsvideo.*;

import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;

public class BoidVoid extends BaseLayer {

	private GLGraphicsOffScreen offscreen;
	private Flock flock;
	private FlockCell flockCell;
	private FlockBod flockBod;
	private int lastAdded = 0;

	PImage background;
	GLTexture b;
	GLTexture c;

	float y = 0;
	float scroll;

	private int[] colour = new int[]{ 0xFF5B419A, 0xFF1979BE, 0xFF43A0D8, 0xFF1BAC4B, 0xFFFBE108, 0xFFF8981D, 0xFFA0A09F, 0xFFED2024, 0xFFEE4993};

	public BoidVoid(GLGraphicsOffScreen vp, CenterMask mask) {
		super(vp, mask);
		offscreen = new GLGraphicsOffScreen(Main.p, Main.screenWidth, Main.screenHeight);
		scroll = Main.screenWidth;

		background = p.loadImage("mtf_stripes.gif");

		flock = new Flock();
		flockCell = new FlockCell();
		flockBod = new FlockBod();

		for (int i = 0; i < 30; i++) {
			flock.addBoid(new Boid(new Vector3D(0,0),2.0f,0.05f));  //width-200,height/1.5
		}
		// Add an initial set of boids2 (circles) into the system
		for (int j = 0; j < 12; j++) {    
			flockCell.addBoid2(new Boid2(new Vector3Dcell(Main.screenWidth/4, Main.screenHeight-100),0.5f,0.1f));
		}

		// Add an initial set of boids3 into the system
		for (int k = 0; k < 5; k++) {
			flockBod.addBoid3(new Boid3(new Vector3Dbod(Main.screenWidth/0.2f, Main.screenHeight/0.2f),0.3f, 0.01f));
		}
	}

	public void draw() {
		if(p.mousePressed && p.millis() - lastAdded > 500) {
			lastAdded = p.millis();
			addBoid();
		}

		offscreen.beginDraw();

		//offscreen.background(0);
		offscreen.image(background, scroll, 0); 

		scroll = scroll - 1; //change for speed of scroll
		if (scroll < background.width - Main.screenWidth) { //width of image - width of screen
			offscreen.image(background, scroll+background.width, 0);
			if (scroll < -background.width) {
				scroll = scroll+background.width;
			}
		} 
		
		flock.run();
		flockCell.run();
		flockBod.run();

		offscreen.endDraw();
		canvas = offscreen.getTexture();

		maskAndDraw();
	}


	class Flock {
		ArrayList boids; // An arraylist for all the boids

		Flock() {
			boids = new ArrayList(); // Initialize the arraylist

		}

		void run() {
			for (int i = 0; i < boids.size(); i++) {
				Boid b = (Boid) boids.get(i);  
				b.run(boids);  // Passing the entire list of boids to each boid individually
			}
		}

		void addBoid(Boid b) {
			boids.add(b);
		}

	}


	class Boid {

		Vector3D loc;
		Vector3D vel;
		Vector3D acc;
		float r;
		float maxforce;    // Maximum steering force
		float maxspeed;    // Maximum speed

		int c;
		int c2;

		Boid(Vector3D l, float ms, float mf) {
			acc = new Vector3D(0,0);
			vel = new Vector3D(p.random(-1,1), p.random(-1,1));
			loc = l.copy();
			r = 5.0f;    // size of boids
			maxspeed = ms;
			maxforce = mf;

			c=colour[(int)p.random(colour.length)];
			c2=colour[(int)p.random(colour.length)];
		}

		void run(ArrayList boids) {
			flock(boids);
			update();
			borders();
			render();
		}

		// We accumulate a new acceleration each time based on three rules
		void flock(ArrayList boids) {
			Vector3D sep = separate(boids);   // Separation
			Vector3D ali = align(boids);      // Alignment
			Vector3D coh = cohesion(boids);   // Cohesion
			// Arbitrarily weight these forces
			sep.mult(10.0f);  // higher the number more they separate
			ali.mult(0.05f);  // highre the number the more likely to align with each other
			coh.mult(2.0f);  // higher the number the tighter they clump
			// Add the force vectors to acceleration
			acc.add(sep);
			acc.add(ali);
			acc.add(coh);
		}

		// Method to update location
		void update() {
			// Update velocity
			vel.add(acc);
			// Limit speed
			vel.limit(maxspeed);
			loc.add(vel);
			// Reset accelertion to 0 each cycle
			acc.setXYZ(0,0,0);
		}

		void seek(Vector3D target) {
			acc.add(steer(target,false));
		}

		void arrive(Vector3D target) {
			acc.add(steer(target,true));
		}

		// A method that calculates a steering vector towards a target
		// Takes a second argument, if true, it slows down as it approaches the target
		Vector3D steer(Vector3D target, boolean slowdown) {
			Vector3D steer;  // The steering vector
			Vector3D desired = target.sub(target,loc);  // A vector pointing from the location to the target
			float d = desired.magnitude(); // Distance from the target is the magnitude of the vector
			// If the distance is greater than 0, calc steering (otherwise return zero vector)
			if (d > 0) {
				// Normalize desired
				desired.normalize();
				// Two options for desired vector magnitude (1 -- based on distance, 2 -- maxspeed)
				if ((slowdown) && (d < 100.0f)) desired.mult(maxspeed*(d/100.0f)); // This damping is somewhat arbitrary
				else desired.mult(maxspeed);
				// Steering = Desired minus Velocity
				steer = target.sub(desired,vel);
				steer.limit(maxforce);  // Limit to maximum steering force
			} else {
				steer = new Vector3D(0,0);
			}
			return steer;
		}

		void render() {
			// Draw a triangle rotated in the direction of velocity
			float theta = vel.heading2D() + Main.radians(90);

			offscreen.fill(c);  // fill colour of boids
			offscreen.stroke(c2);  // colour of the boids  
			offscreen.strokeWeight(2);
			offscreen.pushMatrix();
			offscreen.translate(loc.x,loc.y);
			offscreen.rotate(theta); 
			offscreen.beginShape();
			offscreen.vertex(0, -r);
			offscreen.vertex(-r, r);
			offscreen.vertex(r, r);
			offscreen.vertex(r, -r);
			offscreen.endShape(Main.CLOSE);
			offscreen.popMatrix();
		}

		// Wraparound
		void borders() {
			if (loc.x < -r) loc.x = Main.screenWidth+r;
			if (loc.y < -r) loc.y = Main.screenHeight+r;
			if (loc.x > Main.screenWidth+r) loc.x = -r;
			if (loc.y > Main.screenHeight+r) loc.y = -r;
		}

		// Separation
		// Method checks for nearby boids and steers away
		Vector3D separate (ArrayList boids) {
			float desiredseparation = 25.0f;
			Vector3D sum = new Vector3D(0,0,0);
			int count = 0;
			// For every boid in the system, check if it's too close
			for (int i = 0 ; i < boids.size(); i++) {
				Boid other = (Boid) boids.get(i);
				float d = loc.distance(loc,other.loc);
				// If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
				if ((d > 0) && (d < desiredseparation)) {
					// Calculate vector pointing away from neighbor
					Vector3D diff = loc.sub(loc,other.loc);
					diff.normalize();
					diff.div(d);        // Weight by distance
					sum.add(diff);
					count++;            // Keep track of how many
				}
			}
			// Average -- divide by how many
			if (count > 0) {
				sum.div((float)count);
			}
			return sum;
		}

		// Alignment
		// For every nearby boid in the system, calculate the average velocity
		Vector3D align (ArrayList boids) {
			float neighbordist = 50.0f;
			Vector3D sum = new Vector3D(0,0,0);
			int count = 0;
			for (int i = 0 ; i < boids.size(); i++) {
				Boid other = (Boid) boids.get(i);
				float d = loc.distance(loc,other.loc);
				if ((d > 0) && (d < neighbordist)) {
					sum.add(other.vel);
					count++;
				}
			}
			if (count > 0) {
				sum.div((float)count);
				sum.limit(maxforce);
			}
			return sum;
		}

		// Cohesion
		// For the average location (i.e. center) of all nearby boids, calculate steering vector towards that location
		Vector3D cohesion (ArrayList boids) {
			float neighbordist = 50.0f;
			Vector3D sum = new Vector3D(0,0,0);   // Start with empty vector to accumulate all locations
			int count = 0;
			for (int i = 0 ; i < boids.size(); i++) {
				Boid other = (Boid) boids.get(i);
				float d = loc.distance(loc,other.loc);
				if ((d > 0) && (d < neighbordist)) {
					sum.add(other.loc); // Add location
					count++;
				}
			}
			if (count > 0) {
				sum.div((float)count);
				return steer(sum,false);  // Steer towards the location
			}
			return sum;
		}
	}

	class FlockCell {
		ArrayList boids2; // An arraylist for all the boids2

		FlockCell() {
			boids2 = new ArrayList(); // Initialize the arraylist
		}

		void run() {
			for (int i = 0; i < boids2.size(); i++) {
				Boid2 b = (Boid2) boids2.get(i);  
				b.run(boids2);  // Passing the entire list of boids2 to each boid individually
			}
		}

		void addBoid2(Boid2 b) {
			boids2.add(b);
		}

	}


	class Boid2 {

		Vector3Dcell loc;
		Vector3Dcell vel;
		Vector3Dcell acc;
		float r;
		float maxforce;    // Maximum steering force
		float maxspeed;    // Maximum speed

		int c;
		int c2;

		Boid2(Vector3Dcell l, float ms, float mf) {
			acc = new Vector3Dcell(0,0);
			vel = new Vector3Dcell(p.random(-1,1), p.random(-1,1));
			loc = l.copy();
			r = 10.0f;    // size of boids2 NOTE: size must be changed in render() --> draw_target(size, rings);   
			maxspeed = ms;
			maxforce = mf;

			c=colour[(int)p.random(colour.length)];
			c2=colour[(int)p.random(colour.length)];


		}

		void run(ArrayList boids2) {
			flockCell(boids2);
			update();
			borders();
			render();
		}

		// We accumulate a new acceleration each time based on three rules
		void flockCell(ArrayList boids2) {
			Vector3Dcell sep = separate(boids2);   // Separation
			Vector3Dcell ali = align(boids2);      // Alignment
			Vector3Dcell coh = cohesion(boids2);   // Cohesion
			// Arbitrarily weight these forces
			sep.mult(0.20f);  // higher the number more they separate
			ali.mult(0.01f);  // higher the number the more likely to align with each other
			coh.mult(0.03f);  // higher the number the tighter they clump
			// Add the force vectors to acceleration
			acc.add(sep);
			acc.add(ali);
			acc.add(coh);
		}

		// Method to update location
		void update() {
			// Update velocity
			vel.add(acc);
			// Limit speed
			vel.limit(maxspeed);
			loc.add(vel);
			// Reset accelertion to 0 each cycle
			acc.setXYZ(0,0,0);
		}

		void seek(Vector3Dcell target) {
			acc.add(steer(target,false));
		}

		void arrive(Vector3Dcell target) {
			acc.add(steer(target,true));
		}

		// A method that calculates a steering vector towards a target
		// Takes a second argument, if true, it slows down as it approaches the target
		Vector3Dcell steer(Vector3Dcell target, boolean slowdown) {
			Vector3Dcell steer;  // The steering vector
			Vector3Dcell desired = target.sub(target,loc);  // A vector pointing from the location to the target
			float d = desired.magnitude(); // Distance from the target is the magnitude of the vector
			// If the distance is greater than 0, calc steering (otherwise return zero vector)
			if (d > 0) {
				// Normalize desired
				desired.normalize();
				// Two options for desired vector magnitude (1 -- based on distance, 2 -- maxspeed)
				if ((slowdown) && (d < 100.0f)) desired.mult(maxspeed*(d/100.0f)); // This damping is somewhat arbitrary
				else desired.mult(maxspeed);
				// Steering = Desired minus Velocity
				steer = target.sub(desired,vel);
				steer.limit(maxforce);  // Limit to maximum steering force
			} else {
				steer = new Vector3Dcell(0,0);
			}
			return steer;
		}

		void render() {
			// Draw a triangle rotated in the direction of velocity
			float theta = vel.heading2D() + p.radians(90);
			offscreen.stroke(c);  // stroke colour of the boids2  
			offscreen.strokeWeight(2);
			offscreen.pushMatrix();
			offscreen.translate(loc.x,loc.y);
			offscreen.rotate(theta); 
			//   ellipse(r,r, 4*r, 4*r);
			//       ellipse(loc.x,loc.y, 2*r, 3*r);
			draw_target(40, 3);   

			offscreen.popMatrix();
		}


		void draw_target(int size, int num) 
		{
			float grayvalues = 255/num;
			float steps = size/num;
			for(int i=0; i<num; i++) {
				offscreen.fill(i*grayvalues, 50, 50);
				offscreen.ellipse(r, r, size-i*steps, size-i*steps);
			}
		}


		// Wraparound
		void borders() {
			if (loc.x < -r) loc.x = Main.screenWidth+r;
			if (loc.x > Main.screenWidth+r) loc.x = -r;
			if (loc.y < -r) loc.y = (Main.screenHeight+r)+50;   // add some to allow cells to sit off-screen
			if (loc.y > Main.screenHeight+r) loc.y = -r;
		}

		// Separation
		// Method checks for nearby boids2 and steers away
		Vector3Dcell separate (ArrayList boids2) {
			float desiredseparation = 25.0f;
			Vector3Dcell sum = new Vector3Dcell(0,0,0);
			int count = 0;
			// For every boid in the system, check if it's too close
			for (int i = 0 ; i < boids2.size(); i++) {
				Boid2 other = (Boid2) boids2.get(i);
				float d = loc.distance(loc,other.loc);
				// If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
				if ((d > 0) && (d < desiredseparation)) {
					// Calculate vector pointing away from neighbor
					Vector3Dcell diff = loc.sub(loc,other.loc);
					diff.normalize();
					diff.div(d);        // Weight by distance
					sum.add(diff);
					count++;            // Keep track of how many
				}
			}
			// Average -- divide by how many
			if (count > 0) {
				sum.div((float)count);
			}
			return sum;
		}

		// Alignment
		// For every nearby boid in the system, calculate the average velocity
		Vector3Dcell align (ArrayList boids2) {
			float neighbordist = 50.0f;
			Vector3Dcell sum = new Vector3Dcell(0,0,0);
			int count = 0;
			for (int i = 0 ; i < boids2.size(); i++) {
				Boid2 other = (Boid2) boids2.get(i);
				float d = loc.distance(loc,other.loc);
				if ((d > 0) && (d < neighbordist)) {
					sum.add(other.vel);
					count++;
				}
			}
			if (count > 0) {
				sum.div((float)count);
				sum.limit(maxforce);
			}
			return sum;
		}

		// Cohesion
		// For the average location (i.e. center) of all nearby boids2, calculate steering vector towards that location
		Vector3Dcell cohesion (ArrayList boids2) {
			float neighbordist = 50.0f;
			Vector3Dcell sum = new Vector3Dcell(0,0,0);   // Start with empty vector to accumulate all locations
			int count = 0;
			for (int i = 0 ; i < boids2.size(); i++) {
				Boid2 other = (Boid2) boids2.get(i);
				float d = loc.distance(loc,other.loc);
				if ((d > 0) && (d < neighbordist)) {
					sum.add(other.loc); // Add location
					count++;
				}
			}
			if (count > 0) {
				sum.div((float)count);
				return steer(sum,false);  // Steer towards the location
			}
			return sum;
		}
	}

	// Simple Vector3Dcell Class 

	static class Vector3Dcell {
		float x;
		float y;
		float z;

		Vector3Dcell(float x_, float y_, float z_) {
			x = x_; y = y_; z = z_;
		}

		Vector3Dcell(float x_, float y_) {
			x = x_; y = y_; z = 0f;
		}

		Vector3Dcell() {
			x = 0f; y = 0f; z = 0f;
		}

		void setX(float x_) {
			x = x_;
		}

		void setY(float y_) {
			y = y_;
		}

		void setZ(float z_) {
			z = z_;
		}

		void setXY(float x_, float y_) {
			x = x_;
			y = y_;
		}

		void setXYZ(float x_, float y_, float z_) {
			x = x_;
			y = y_;
			z = z_;
		}

		void setXYZ(Vector3Dcell v) {
			x = v.x;
			y = v.y;
			z = v.z;
		}

		float magnitude() {
			return (float) Math.sqrt(x*x + y*y + z*z);
		}

		Vector3Dcell copy() {
			return new Vector3Dcell(x,y,z);
		}

		Vector3Dcell copy(Vector3Dcell v) {
			return new Vector3Dcell(v.x, v.y,v.z);
		}

		void add(Vector3Dcell v) {
			x += v.x;
			y += v.y;
			z += v.z;
		}

		void sub(Vector3Dcell v) {
			x -= v.x;
			y -= v.y;
			z -= v.z;
		}

		void mult(float n) {
			x *= n;
			y *= n;
			z *= n;
		}

		void div(float n) {
			x /= n;
			y /= n;
			z /= n;
		}

		void normalize() {
			float m = magnitude();
			if (m > 0) {
				div(m);
			}
		}

		void limit(float max) {
			if (magnitude() > max) {
				normalize();
				mult(max);
			}
		}

		float heading2D() {
			float angle = (float) Math.atan2(-y, x);
			return -1*angle;
		}

		Vector3Dcell add(Vector3Dcell v1, Vector3Dcell v2) {
			Vector3Dcell v = new Vector3Dcell(v1.x + v2.x,v1.y + v2.y, v1.z + v2.z);
			return v;
		}

		Vector3Dcell sub(Vector3Dcell v1, Vector3Dcell v2) {
			Vector3Dcell v = new Vector3Dcell(v1.x - v2.x,v1.y - v2.y,v1.z - v2.z);
			return v;
		}

		Vector3Dcell div(Vector3Dcell v1, float n) {
			Vector3Dcell v = new Vector3Dcell(v1.x/n,v1.y/n,v1.z/n);
			return v;
		}

		Vector3Dcell mult(Vector3Dcell v1, float n) {
			Vector3Dcell v = new Vector3Dcell(v1.x*n,v1.y*n,v1.z*n);
			return v;
		}

		float distance (Vector3Dcell v1, Vector3Dcell v2) {
			float dx = v1.x - v2.x;
			float dy = v1.y - v2.y;
			float dz = v1.z - v2.z;
			return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
		}

	}

	// Simple Vector3D Class 

	static class Vector3D {
		float x;
		float y;
		float z;

		Vector3D(float x_, float y_, float z_) {
			x = x_; y = y_; z = z_;
		}

		Vector3D(float x_, float y_) {
			x = x_; y = y_; z = 0f;
		}

		Vector3D() {
			x = 0f; y = 0f; z = 0f;
		}

		void setX(float x_) {
			x = x_;
		}

		void setY(float y_) {
			y = y_;
		}

		void setZ(float z_) {
			z = z_;
		}

		void setXY(float x_, float y_) {
			x = x_;
			y = y_;
		}

		void setXYZ(float x_, float y_, float z_) {
			x = x_;
			y = y_;
			z = z_;
		}

		void setXYZ(Vector3D v) {
			x = v.x;
			y = v.y;
			z = v.z;
		}

		float magnitude() {
			return (float) Math.sqrt(x*x + y*y + z*z);
		}

		Vector3D copy() {
			return new Vector3D(x,y,z);
		}

		Vector3D copy(Vector3D v) {
			return new Vector3D(v.x, v.y,v.z);
		}

		void add(Vector3D v) {
			x += v.x;
			y += v.y;
			z += v.z;
		}

		void sub(Vector3D v) {
			x -= v.x;
			y -= v.y;
			z -= v.z;
		}

		void mult(float n) {
			x *= n;
			y *= n;
			z *= n;
		}

		void div(float n) {
			x /= n;
			y /= n;
			z /= n;
		}

		void normalize() {
			float m = magnitude();
			if (m > 0) {
				div(m);
			}
		}

		void limit(float max) {
			if (magnitude() > max) {
				normalize();
				mult(max);
			}
		}

		float heading2D() {
			float angle = (float) Math.atan2(-y, x);
			return -1*angle;
		}

		Vector3D add(Vector3D v1, Vector3D v2) {
			Vector3D v = new Vector3D(v1.x + v2.x,v1.y + v2.y, v1.z + v2.z);
			return v;
		}

		Vector3D sub(Vector3D v1, Vector3D v2) {
			Vector3D v = new Vector3D(v1.x - v2.x,v1.y - v2.y,v1.z - v2.z);
			return v;
		}

		Vector3D div(Vector3D v1, float n) {
			Vector3D v = new Vector3D(v1.x/n,v1.y/n,v1.z/n);
			return v;
		}

		Vector3D mult(Vector3D v1, float n) {
			Vector3D v = new Vector3D(v1.x*n,v1.y*n,v1.z*n);
			return v;
		}

		float distance (Vector3D v1, Vector3D v2) {
			float dx = v1.x - v2.x;
			float dy = v1.y - v2.y;
			float dz = v1.z - v2.z;
			return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
		}

	}

	class FlockBod {
		ArrayList boids3; // An arraylist for all the boids3

		FlockBod() {
			boids3 = new ArrayList(); // Initialize the arraylist
		}

		void run() {
			for (int i = 0; i < boids3.size(); i++) {
				Boid3 b = (Boid3) boids3.get(i);  
				b.run(boids3);  // Passing the entire list of boids3 to each boid individually
			}
		}

		void addBoid3(Boid3 b) {
			boids3.add(b);
		}

	}


	class Boid3 {

		Vector3Dbod loc;
		Vector3Dbod vel;
		Vector3Dbod acc;
		float r;
		float maxforce;    // Maximum steering force
		float maxspeed;    // Maximum speed
		int c;
		int c2;


		Boid3(Vector3Dbod l, float ms, float mf) {
			acc = new Vector3Dbod(0,0);
			vel = new Vector3Dbod(p.random(-1,1), p.random(-1,1));
			loc = l.copy();
			r = 30.0f;    // size of boids3
			maxspeed = ms;
			maxforce = mf;

			c=colour[(int)p.random(colour.length)];
			c2=colour[(int)p.random(colour.length)];
		}

		void run(ArrayList boids3) {
			flockBod(boids3);
			update();
			borders();
			render();
		}

		// We accumulate a new acceleration each time based on three rules
		void flockBod(ArrayList boids3) {
			Vector3Dbod sep = separate(boids3);   // Separation
			Vector3Dbod ali = align(boids3);      // Alignment
			Vector3Dbod coh = cohesion(boids3);   // Cohesion
			// Arbitrarily weight these forces
			sep.mult(0.1f);  // higher the number more they separate
			ali.mult(10.05f);  // higher the number the more likely to align with each other
			coh.mult(0.01f);  // higher the number the tighter they clump
			// Add the force vectors to acceleration
			acc.add(sep);
			acc.add(ali);
			acc.add(coh);
		}

		// Method to update location
		void update() {
			// Update velocity
			vel.add(acc);
			// Limit speed
			vel.limit(maxspeed);
			loc.add(vel);
			// Reset accelertion to 0 each cycle
			acc.setXYZ(0,0,0);
		}

		void seek(Vector3Dbod target) {
			acc.add(steer(target,false));
		}

		void arrive(Vector3Dbod target) {
			acc.add(steer(target,true));
		}

		// A method that calculates a steering vector towards a target
		// Takes a second argument, if true, it slows down as it approaches the target
		Vector3Dbod steer(Vector3Dbod target, boolean slowdown) {
			Vector3Dbod steer;  // The steering vector
			Vector3Dbod desired = target.sub(target,loc);  // A vector pointing from the location to the target
			float d = desired.magnitude(); // Distance from the target is the magnitude of the vector
			// If the distance is greater than 0, calc steering (otherwise return zero vector)
			if (d > 0) {
				// Normalize desired
				desired.normalize();
				// Two options for desired vector magnitude (1 -- based on distance, 2 -- maxspeed)
				if ((slowdown) && (d < 100.0f)) desired.mult(maxspeed*(d/100.0f)); // This damping is somewhat arbitrary
				else desired.mult(maxspeed);
				// Steering = Desired minus Velocity
				steer = target.sub(desired,vel);
				steer.limit(maxforce);  // Limit to maximum steering force
			} else {
				steer = new Vector3Dbod(0,0);
			}
			return steer;
		}

		void render() {
			// Draw a triangle rotated in the direction of velocity
			float theta = vel.heading2D() + Main.radians(90);

			offscreen.fill(c);  // fill colour of boids3
			offscreen.stroke(c);

			offscreen.strokeWeight(16);
			offscreen.pushMatrix();
			offscreen.translate(loc.x,loc.y);
			offscreen.rotate(theta); 
			offscreen.beginShape();
			offscreen.vertex(0, -r);
			offscreen.vertex(-r+20, r-10);
			offscreen.vertex(r, r);
			offscreen.vertex(r+3, -r+3);
			offscreen.endShape(Main.CLOSE);

			offscreen.fill(0);
			offscreen.noStroke();
			offscreen.ellipse(-r+40,r-30,30,30);

			offscreen.stroke(255);
			offscreen.strokeWeight(2);
			offscreen.line(-r+40,r-20,r-19,-r+20);
			//line(-r+30,r-35,r-0,-r+15);


			//
			offscreen.pushMatrix();
			offscreen.rotate(Main.PI/2);
			offscreen.translate(-10,-10);
			offscreen.line(-r+40,r-20,r-19,-r+20);
			//		      
			offscreen.popMatrix();
			//		        

			/*    beginShape(TRIANGLE);
		    vertex(0, -r*2);
		    vertex(-r, r*2);
		    vertex(r, r*2);
		    endShape();
			 */   
			offscreen.popMatrix();
		}

		// Wraparound
		void borders() {
			if (loc.x < -r) loc.x = Main.screenWidth+r;
			if (loc.y < -r) loc.y = Main.screenHeight+r;
			if (loc.x > Main.screenWidth+r) loc.x = -r;
			if (loc.y > Main.screenHeight+r) loc.y = -r;
		}

		// Separation
		// Method checks for nearby boids3 and steers away
		Vector3Dbod separate (ArrayList boids3) {
			float desiredseparation = 25.0f;
			Vector3Dbod sum = new Vector3Dbod(0,0,0);
			int count = 0;
			// For every boid in the system, check if it's too close
			for (int i = 0 ; i < boids3.size(); i++) {
				Boid3 other = (Boid3) boids3.get(i);
				float d = loc.distance(loc,other.loc);
				// If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
				if ((d > 0) && (d < desiredseparation)) {
					// Calculate vector pointing away from neighbor
					Vector3Dbod diff = loc.sub(loc,other.loc);
					diff.normalize();
					diff.div(d);        // Weight by distance
					sum.add(diff);
					count++;            // Keep track of how many
				}
			}
			// Average -- divide by how many
			if (count > 0) {
				sum.div((float)count);
			}
			return sum;
		}

		// Alignment
		// For every nearby boid in the system, calculate the average velocity
		Vector3Dbod align (ArrayList boids3) {
			float neighbordist = 50.0f;
			Vector3Dbod sum = new Vector3Dbod(0,0,0);
			int count = 0;
			for (int i = 0 ; i < boids3.size(); i++) {
				Boid3 other = (Boid3) boids3.get(i);
				float d = loc.distance(loc,other.loc);
				if ((d > 0) && (d < neighbordist)) {
					sum.add(other.vel);
					count++;
				}
			}
			if (count > 0) {
				sum.div((float)count);
				sum.limit(maxforce);
			}
			return sum;
		}

		// Cohesion
		// For the average location (i.e. center) of all nearby boids3, calculate steering vector towards that location
		Vector3Dbod cohesion (ArrayList boids3) {
			float neighbordist = 50.0f;
			Vector3Dbod sum = new Vector3Dbod(0,0,0);   // Start with empty vector to accumulate all locations
			int count = 0;
			for (int i = 0 ; i < boids3.size(); i++) {
				Boid3 other = (Boid3) boids3.get(i);
				float d = loc.distance(loc,other.loc);
				if ((d > 0) && (d < neighbordist)) {
					sum.add(other.loc); // Add location
					count++;
				}
			}
			if (count > 0) {
				sum.div((float)count);
				return steer(sum,false);  // Steer towards the location
			}
			return sum;
		}
	}

	// Simple Vector3Dbod Class 

	static class Vector3Dbod {
		float x;
		float y;
		float z;

		Vector3Dbod(float x_, float y_, float z_) {
			x = x_; y = y_; z = z_;
		}

		Vector3Dbod(float x_, float y_) {
			x = x_; y = y_; z = 0f;
		}

		Vector3Dbod() {
			x = 0f; y = 0f; z = 0f;
		}

		void setX(float x_) {
			x = x_;
		}

		void setY(float y_) {
			y = y_;
		}

		void setZ(float z_) {
			z = z_;
		}

		void setXY(float x_, float y_) {
			x = x_;
			y = y_;
		}

		void setXYZ(float x_, float y_, float z_) {
			x = x_;
			y = y_;
			z = z_;
		}

		void setXYZ(Vector3Dbod v) {
			x = v.x;
			y = v.y;
			z = v.z;
		}

		float magnitude() {
			return (float) Math.sqrt(x*x + y*y + z*z);
		}

		Vector3Dbod copy() {
			return new Vector3Dbod(x,y,z);
		}

		Vector3Dbod copy(Vector3Dbod v) {
			return new Vector3Dbod(v.x, v.y,v.z);
		}

		void add(Vector3Dbod v) {
			x += v.x;
			y += v.y;
			z += v.z;
		}

		void sub(Vector3Dbod v) {
			x -= v.x;
			y -= v.y;
			z -= v.z;
		}

		void mult(float n) {
			x *= n;
			y *= n;
			z *= n;
		}

		void div(float n) {
			x /= n;
			y /= n;
			z /= n;
		}

		void normalize() {
			float m = magnitude();
			if (m > 0) {
				div(m);
			}
		}

		void limit(float max) {
			if (magnitude() > max) {
				normalize();
				mult(max);
			}
		}

		float heading2D() {
			float angle = (float) Math.atan2(-y, x);
			return -1*angle;
		}

		Vector3Dbod add(Vector3Dbod v1, Vector3Dbod v2) {
			Vector3Dbod v = new Vector3Dbod(v1.x + v2.x,v1.y + v2.y, v1.z + v2.z);
			return v;
		}

		Vector3Dbod sub(Vector3Dbod v1, Vector3Dbod v2) {
			Vector3Dbod v = new Vector3Dbod(v1.x - v2.x,v1.y - v2.y,v1.z - v2.z);
			return v;
		}

		Vector3Dbod div(Vector3Dbod v1, float n) {
			Vector3Dbod v = new Vector3Dbod(v1.x/n,v1.y/n,v1.z/n);
			return v;
		}

		Vector3Dbod mult(Vector3Dbod v1, float n) {
			Vector3Dbod v = new Vector3Dbod(v1.x*n,v1.y*n,v1.z*n);
			return v;
		}

		float distance (Vector3Dbod v1, Vector3Dbod v2) {
			float dx = v1.x - v2.x;
			float dy = v1.y - v2.y;
			float dz = v1.z - v2.z;
			return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
		}

	}
	
	public void addBoid() {
		flockBod.addBoid3(new Boid3(new Vector3Dbod(p.mouseX, p.mouseY), 0.3f,0.01f));
	}
}
