import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import com.sailsim.app.NavInterfaceClient;
import java.lang.Math;
import java.util.concurrent.TimeUnit;
import java.lang.InterruptedException;

public class driver_gui extends JFrame {

    private JPanel contentPane;
    private static ArrayList<point_gui> pts;
    private static ArrayList<obstacle_gui> obs;
    private static NavInterfaceClient api = new NavInterfaceClient();
	public static Vector wind;
	public static Vector boat = new Vector( 0, 1 );
	public static vector6 impo;
	
	public static void windMaker() throws IOException
    {
		api.send( "windHeading" );
		float windD = ( 450 - Float.parseFloat( api.receive()[1] ) ) % 360;
		api.send( "windStrength" );
		float windS = Float.parseFloat( api.receive()[1] );
		wind = new Vector( windD, windS );
	}
	
	public static float pointTo( float x, float y ) throws IOException
	{
		api.send( "boatPosition" );
		String boatXY = api.receive()[1];
		System.out.println( boatXY );

		float boatX = Float.parseFloat( boatXY.substring( 0, 10 ) );
		float boatY = Float.parseFloat( boatXY.substring( 10 ) );

		float theta = ( 180 / (float) Math.PI ) * (float) Math.atan2( y-boatY, x-boatX );
		theta = ( 450 - theta ) % 360;
		impo.setBoat( theta );
		api.send( "boatHeading " + theta );
		api.receive();
		
		return ( 450 - theta ) % 360;
	}
	
	public static void executeTurn( float nextX, float nextY ) throws IOException
	{
		pointTo( nextX , nextY );

		api.send( "sailAngle " + impo.getSail() );
		api.receive();
	}
	

    public static double roundDecimal(double value) {
        return (double) Math.round (value * ((int) Math.pow (10, 1))) / ((int) Math.pow (10, 1));
    }

    public double distBtwnTwoPts (double x1, double x2, double y1, double y2) {
        return roundDecimal (Math.sqrt ((Math.pow ((x2-x1), 2)) + (Math.pow ((y2-y1), 2))));
    }

    public double totalDist (ArrayList<point> list) {

        double totalDistance = 0.0;
        for (int i = 0; list.size () - 1 > i; i++) {
            totalDistance += distBtwnTwoPts (list.get(i).getX (),
                    list.get(i+1).getX (),
                    list.get(i).getY (),
                    list.get(i+1).getY ());
        }
        return roundDecimal(totalDistance);
    }

    //takes the receive statement and parses thru to get relevant data about the obstacle
    public static obstacle Obstacle(Integer ID) throws IOException
    {
        api.send("obstacle " + String.valueOf(ID));
        String obInfo = api.receive()[1];

        float obstacleX = Float.parseFloat(obInfo.substring(obInfo.indexOf(",")+1,obInfo.indexOf(",",obInfo.indexOf(",")+1)));
        float obstacleY = Float.parseFloat(obInfo.substring(obInfo.indexOf(",",obInfo.indexOf(",")+1)+1,obInfo.indexOf(",",obInfo.indexOf(",",obInfo.indexOf(",")+1)+1)));
        float radius = Float.parseFloat(obInfo.substring(obInfo.indexOf(",",obInfo.indexOf(",",obInfo.indexOf(",")+1)+1)+1));
        point pt = new point((double) obstacleX, (double) obstacleY);

        obstacle newObs = new obstacle(ID,radius, pt);
        
        return newObs;
    }

    public void drawDatLine(point first, point second){
        Graphics g = getGraphics();
        g.setColor(Color.black);
        g.drawLine((int)first.getX (),(int)first.getY (),(int)second.getX (),(int)second.getY ());
    }

    public driver() {

        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setBounds (100,100,1000,750);
        contentPane = new JPanel ();
        setContentPane (contentPane);
        contentPane.setLayout (null);

        //background panel
        JPanel panel = new JPanel ();
        panel.setBounds (0, 0, 1000, 750);
        panel.setBackground (Color.blue);
        contentPane.add (panel);

        //X Y label
        JLabel label = new JLabel ();
        label.setFont (new Font ("Tahoma", Font.BOLD, 20));
        panel.add (label);

        //distance label
        JLabel label1 = new JLabel ();
        label1.setFont (new Font ("Tahoma", Font.BOLD, 20));
        panel.add (label1);

        //detects mouse clicks and gets coordinates/runs distance method/draws lines
        panel.addMouseListener(new MouseAdapter() {

            int count = -1;

            public void mouseClicked(MouseEvent e){

                count++;
                label.setText("X = "+ e.getX()+" ; Y = "+(750 - e.getY() ) + " ");
                pts.add(new point (e.getX(), (e.getY())));
                label1.setText("Distance: " + totalDist(pts));
                System.out.print(e.getX()*2 + " " + (750 - e.getY())*2 + " ");
                if (count >= 1) {
                    drawDatLine (pts.get(count-1),pts.get (count));
                }
            }

        });
    }

    //each accessor for each obstacle object and draws a shape based on parameters on the jpanel
    public void paint(Graphics g) {
        g.drawRect (0,0,1000,750);
        g.setColor (Color.blue);
        g.fillRect (0,0,1000,750);

        for (int i = 0; i < obs.size(); i++) {
            if (obs.get(i).getID() == 1) {
                    int x = ((int) obs.get(i).getPoint ().getX () / 2) - ((int) obs.get (i).getRadius ()/2);
                    int y = 750 - ((int) obs.get(i).getPoint ().getY () / 2) - ((int) obs.get (i).getRadius ()/2);
                    g.drawOval (x, y, (int) obs.get (i).getRadius (), (int) obs.get (i).getRadius ());
                    g.setColor (Color.red);
                    g.fillOval (x, y, (int) obs.get (i).getRadius (), (int) obs.get (i).getRadius ());
            }
                else {
                    int x = ((int) obs.get(i).getPoint ().getX () / 2) - ((int) obs.get (i).getRadius ()/2);
                    int y = 750 - ((int) obs.get(i).getPoint ().getY () / 2) - ((int) obs.get (i).getRadius ()/2);
                    g.drawOval (x, y, (int) obs.get (i).getRadius (), (int) obs.get (i).getRadius ());
                    g.setColor (Color.orange);
                    g.fillOval (x, y, (int) obs.get (i).getRadius (), (int) obs.get (i).getRadius ());
                }

        }
    }

    public static void main(String args[]) throws IOException {

        pts = new ArrayList<point>();
        obs = new ArrayList<obstacle> ();
        api.connect("sim.sailsim.org", 20170, "Knotty_Buoys", "isogonal");
        windMaker();
        impo = new vector6( wind, boat );
            
        //goes thru each object id and pulls out the relevant info then constructs an obstacle object that can be used to draw shapes on the jpanel
        //starts at goal not shore (not applicable)
        api.send("obstacleCount");
        String num = api.receive()[1];
        int numOfObstacles = Integer.parseInt (num);
    
        for (int i = 1; i < numOfObstacles; i++) {
            obs.add(Obstacle((i)));
        }
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    driver frame = new driver();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}