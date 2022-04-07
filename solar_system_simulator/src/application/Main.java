package application;

import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;	
	
	private double  AU = 149.6e6 * 1000;	// approx distance from the Earth to the sun in metres
	private double G = 6.67428e-11;			// Gravitational constant to find the force of attraction between objects
	private double SCALE = 250 / AU;		// 1AU = 100 pixels
	private int TIMESTEP = 3600*24;			// 1 day

	//Tracks drag starting point for x and y
	private double anchorX, anchorY;
	
	@Override
	public void start(Stage primaryStage) {
		
	    Sphere sun = createPlanet(0, 0, 30, "/textures/sun.png");
	    double sunMass = 1.98892 * 10e30;
	    double xVelocitySun = 0;
	    double yVelocitySun = 0;
	    double[] sunOrbit = new double[] {};

		Sphere earth = createPlanet(-1 * AU, 0, 16, "/textures/earth_atmos_2048.jpg");
		double earthMass = 5.9742 * 10e24;
		double earthYVel = setVelocity(29.783);
		double xVelocityEarth = 0;
		double yVelocityEarth = 0;
		double[] earthOrbit = new double[] {};
		
		Sphere mars = createPlanet(-1.524 * AU, 0, 12, "/textures/mars.jpg");
		double marsMass = 6.39 * 10e23;
		double marsYVel = setVelocity(24.077);
		double xVelocityMars = 0;
		double yVelocityMars = 0;
		double[] marsOrbit = new double[] {};

		Sphere mercury = createPlanet(0.387 * AU, 0, 8, "/textures/mercury.jpg");
		double mercuryMass = 3.30 * 10e23;
		double mercuryYVel = setVelocity(-47.4);
		double xVelocityMercury = 0;
		double yVelocityMercury = 0;
		double[] mercuryOrbit = new double[] {};

		Sphere venus = createPlanet(0.723 * AU, 0, 14, "/textures/venus.jpg");
		double venusMass = 4.8685 * 10e24;
		double venusYVel = setVelocity(-35.02);
		double xVelocityVenus = 0;
		double yVelocityVenus = 0;
		double[] venusOrbit = new double[] {};
		
		Sphere[] planets = new Sphere[]{sun, earth, mars, mercury, venus}; 
		double masses[] = new double[]{sunMass, earthMass, marsMass, mercuryMass, venusMass};
		double xVelocities[] = new double[]{xVelocitySun, xVelocityEarth, xVelocityMars, xVelocityMercury, xVelocityVenus};
		double yVelocities[] = new double[]{yVelocitySun, yVelocityEarth, yVelocityMars, yVelocityMercury, yVelocityVenus};
		
	    Group group = new Group();
	    		
	    Camera camera = new PerspectiveCamera();
	    Scene scene = new Scene(group, WIDTH, HEIGHT);
	    scene.setFill(Color.BLACK);
	    scene.setCamera(camera);
	    
	    initMouseControl(scene, camera, primaryStage, sun);
	    initMouseControl(scene, camera, primaryStage, earth);
	    initMouseControl(scene, camera, primaryStage, mars);
	    initMouseControl(scene, camera, primaryStage, mercury);
	    initMouseControl(scene, camera, primaryStage, venus);
	 
	    primaryStage.setTitle("Solar System Simulator");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	    
	    //updatePosition(planets, sun, sunMass, masses, xVelocitySun, yVelocitySun, sunOrbit);
	    Path earthPath = updatePosition(planets, earth, earthMass, masses, xVelocityEarth, yVelocityEarth, earthOrbit, (WIDTH/2)-200, (HEIGHT/2)-180, (WIDTH/2)+200);
	    Path marsPath = updatePosition(planets, mars, marsMass, masses, xVelocityMars, yVelocityMars, marsOrbit, (WIDTH/2)-100, (HEIGHT/2)-75, (WIDTH/2)+300);
	    Path mercuryPath = updatePosition(planets, mercury, mercuryMass, masses, xVelocityMercury, yVelocityMercury, mercuryOrbit, (WIDTH/2)-500, (HEIGHT/2)-300, (WIDTH/2)-90);
	    Path venusPath = updatePosition(planets, venus, venusMass, masses, xVelocityVenus, yVelocityVenus, venusOrbit, (WIDTH/2)-235, (HEIGHT/2)-250, (WIDTH/2)+150);
	    

        createPathTransition(5, earthPath, earth).play();
        createPathTransition(10, marsPath, mars).play();
        createPathTransition(1, mercuryPath, mercury).play();
        createPathTransition(3, venusPath, venus).play();     
        
        prepareAnimation(earth);
        prepareAnimation(mars);
        prepareAnimation(mercury);
        prepareAnimation(venus);
        
        group.getChildren().addAll(sun, earth, mars, mercury, venus, earthPath, marsPath, mercuryPath, venusPath);
	}

	public Sphere createPlanet(double xCoord, double yCoord, double radius, String image) {
		Sphere planet = new Sphere(radius);
		
		xCoord = (xCoord * SCALE) + WIDTH / 2;
		yCoord = (yCoord * SCALE) + HEIGHT / 2;
		
		planet.setTranslateX(xCoord);
		planet.setTranslateY(yCoord);
		
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseMap(new Image(getClass().getResourceAsStream(image)));
		planet.setRotationAxis(Rotate.Y_AXIS);
		planet.setMaterial(material);
		
		return planet;
	}
	
	private void prepareAnimation(Sphere planet) {
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				planet.rotateProperty().set(planet.getRotate() + 2.5);
			}
		};
		timer.start();
	}
	
	public double setVelocity(double x) {
		return x * 1000;
	}
	
	// calculate the force of attraction between another obj & the current obj
	public double[] attraction(Sphere planet1, Sphere planet2, double mass1, double mass2) {
		
		/* Force of attraction = G*((mass1*mass2)/r^2)
		 		-mass and G is known 
				-r: calc the distance between the 2 objs
		*/
		
		double distanceY = planet2.getTranslateY() - planet1.getTranslateY();
		double distanceX = planet2.getTranslateX() - planet1.getTranslateX();
		double distance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));		// finding r using pythagorus
		
		/* (basic trig)
			finding x and y components when F is known
				-F is known
				-calculate angle theta: can calc angle theta because we know X and Y
			
				finding fy -> sin theta = fy / f
				finding fx -> cos theta = fx / f
		 */
		
		// straight line force || force of attraction
		double force = G * mass1 * mass2 / Math.pow(distance, 2);
		double theta = Math.atan2(distanceY, distanceX);
		double forceX = Math.cos(theta) * force;
		double forceY = Math.sin(theta) * force;
		
		double[] forceXY = new double[]{ forceX, forceY };
		return forceXY;
	}
	
	public Path updatePosition(Sphere[] planets, Sphere planet, double mass1, double masses[], double xPlanetVelocity,
			double yPlanetVelocity, double[] planetOrbit, double planetRadiusX, double planetRadiusY, double planetCenterX) {
		double totalFx = 0, totalFy = 0;
		double fx, fy;
		
		// sum all the forces together from all the planets
		for (int i = 0; i < planets.length; i++) {
			// not same planet
			if (planet != planets[i]) {
				fx = attraction(planet, planets[i], mass1, masses[i])[0];
				fy = attraction(planet, planets[i], mass1, masses[i])[1];
				
				totalFx += fx;
				totalFy += fy;				
			}
		}
		
		// calculate velocity
		xPlanetVelocity += totalFx / mass1 * TIMESTEP;
		yPlanetVelocity += totalFy / mass1 * TIMESTEP;
		
		Path p = createOrbitalPath(planetCenterX, HEIGHT/2, planetRadiusX, planetRadiusY, 0);
		
		planetOrbit = append(planetOrbit, (planet.getTranslateX() + (xPlanetVelocity * TIMESTEP)));
		planetOrbit = append(planetOrbit, (planet.getTranslateY() + (yPlanetVelocity * TIMESTEP)));
		
		return p;
	}

   private Path createOrbitalPath(double centerX, double centerY,double radiusX, double radiusY, double rotate) {
      ArcTo arcTo = new ArcTo();
      arcTo.setX(centerX - radiusX + 1);
      arcTo.setY(centerY - radiusY);
      arcTo.setSweepFlag(false);
      arcTo.setLargeArcFlag(true);
      arcTo.setRadiusX(radiusX);
      arcTo.setRadiusY(radiusY);
      arcTo.setXAxisRotation(rotate);
      Path path = new Path();
      path.getElements().add(new MoveTo(centerX - radiusX, centerY - radiusY));
      path.getElements().add(arcTo);
      path.getElements().add(new ClosePath());
      path.setVisible(false);
      path.setStroke(Color.RED);

      return path;
   }

   private PathTransition createPathTransition(double second, Path path, Node node) {
      PathTransition t = new PathTransition();
      t.setDuration(Duration.seconds(second));
      t.setPath(path);
      t.setNode(node);
      t.setCycleCount(Timeline.INDEFINITE);
      return t;
   }
   
   private void initMouseControl(Scene scene, Camera camera, Stage stage, Sphere planet) {

		   scene.setOnMousePressed(event -> {
			   	//Save start points
			    anchorX = event.getSceneX();
			    anchorY = event.getSceneY();
		   });
		 
		   scene.setOnMouseDragged(event -> {
			   	camera.setTranslateY(camera.getTranslateY() + ((anchorY - event.getSceneY()) * 0.05));
				camera.setTranslateX(camera.getTranslateX() + ((anchorX - event.getSceneX()) * 0.05));
		   });
		   		   
		   stage.addEventHandler(ScrollEvent.SCROLL, event -> {
			   planet.setTranslateZ(planet.getTranslateZ() + event.getDeltaY());
		   });
	}
	
	static double[] append(double[] array, double element) {
	    final int N = array.length;
	    double[] newArray = new double[N + 1];

	    for (int i = 0; i < N; i++) {
	    	newArray[i] = array[i];
	    }
	    newArray[N] = element;
	    return newArray;
	}

	
	public static void main(String[] args) {
		launch(args);
	}
}