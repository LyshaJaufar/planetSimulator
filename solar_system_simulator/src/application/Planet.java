package application;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Sphere;

public class Planet extends Sphere {
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;	
	
	private double  AU = 149.6e6 * 1000;	// approx distance from the Earth to the sun in metres
	private double G = 6.67428e-11;			// Gravitational constant to find the force of attraction between objects
	private double SCALE = 250 / AU;		// 1AU = 100 pixels
	private int TIMESTEP = 3600*24;			// 1 day
	
    Group group = new Group();
	public Sphere planet;
	
	public double xCoord, yCoord;
	public double radius;
	public Color colour;
	public double mass;
	
	public double orbit[];
	public boolean isSun;
	public double distanceToSun;
	
	public double xVel, yVel;
		
	public Planet(int xCoord, int yCoord, int radius, Color colour, double mass) {
		this.xCoord = xCoord;		// in metres
		this.yCoord = yCoord;		// in metres
		this.radius = radius;
		this.colour = colour;
		this.mass = mass;
		
		this.isSun = false;
		this.distanceToSun = 0;
		
		this.xVel = 0;				// in metres
		this.yVel = 0;				// in metres
		
		this.planet = new Sphere(this.radius);
	}

	public void draw() {
		xCoord = this.xCoord * this.SCALE + WIDTH / 2;
		yCoord = this.yCoord * this.SCALE + HEIGHT / 2;
		
	    this.planet.setTranslateX(xCoord);
	    this.planet.setTranslateY(yCoord);

	    
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(this.colour);
		this.planet.setMaterial(material);
		
	    group.getChildren().add(this.planet);
	}
}


