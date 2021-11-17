import com.sailsim.app.NavInterfaceClient;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Math.*;
public class sim
{
	private static NavInterfaceClient API = new NavInterfaceClient();
	private static ArrayList<obstacle> Obstacles;
	
	private static float OBX;
	private static float OBY;
	private static float radius;
	private static float windHeading;
	private static float windStr;
	private static ArrayList<Point> OPEN;
	private static ArrayList<Point> CLOSED;
	private static Point GOAL;
	private static ArrayList<Point> Path1;
	private static Point b;
	private static Point nextOnPath;
	private static ArrayList<Point> GRAPH;
	public static vector wind;
	public static vector boat = new vector( 0, 1 );
	public static vector6 impo;
	
	//connects to api
	public static boolean Connect()
	{
		boolean connected = false;
		String host = "sim.sailsim.org";
		int port = 20170;
		String username = "Knotty_Buoys";
		String password = "isogonal";
		try
		{
			connected = API.connect(host,port,username,password);
			if(connected==true)
				System.out.println("connected");
			else
				System.out.println("not connected");
		}
		catch (IOException e)
		{
			System.err.println("IOException");
		}
		return connected;
	}
		
	// creates an obstacle object
	public static obstacle OBstacle(Integer ID) throws IOException
	{
		API.send("obstacle " + String.valueOf(ID));
		String obinfo = API.receive()[1];
		System.out.println(obinfo);
		OBX = Float.parseFloat(obinfo.substring(obinfo.indexOf(",")+1,obinfo.indexOf(",",obinfo.indexOf(",")+1)));
		OBY = Float.parseFloat(obinfo.substring(obinfo.indexOf(",",obinfo.indexOf(",")+1)+1,obinfo.indexOf(",",obinfo.indexOf(",",obinfo.indexOf(",")+1)+1)));
		radius = Float.parseFloat(obinfo.substring(obinfo.indexOf(",",obinfo.indexOf(",",obinfo.indexOf(",")+1)+1)+1));
		Point XY = new Point(OBX,OBY);
		obstacle o = new obstacle(ID,radius, XY);
		return o;
	}
		
	//attributs the cost to a point
	public static float computeCost(float vector,Point p)
	{
		float distance = (float)Math.sqrt(Math.pow((p.getX() - GOAL.getX()),2) + Math.pow((p.getY()-GOAL.getY()),2));
		float cost = distance - vector;
		return cost;
	}

	//finds lowest cost in OPEN
	public static Point lowestCost()
	{
		int min = 0;
		for(int i=1;i<OPEN.size();i++)
		{
			if(OPEN.get(i).getCost() < OPEN.get(min).getCost())
				min = i;
		}
		return OPEN.get(min);
	}

	//finds total cost of a path
	public static float totalCost(ArrayList<Point> path)
	{
		float tcost;
		for(int i=0;i<path.size();i++)
		{
			tcost+=path.get(i.getCost());
		}
		return tcost;
	}

	public static void windMaker() throws IOException
	{
		api.send( "windHeading" );
		float windD = ( 450 - Float.parseFloat( api.receive()[1] ) ) % 360;
		api.send( "windStrength" );
		float windS = Float.parseFloat( api.receive()[1] );
		wind = new Vector( windD, windS );
	}
	
	//creates a path
	public static ArrayList<Point> createPath()
	{
		go=true;
		while(go==true)
		{
			//adds new points to OPEN and removes points already in CLOSED
			OPEN.add(new Point(nextOnPath.getX()+10,nextOnPath.getY()+10));
			OPEN.add(new Point(nextOnPath.getX(),nextOnPath.getY()+10));
			OPEN.add(new Point(nextOnPath.getX()+10,nextOnPath.getY()));
			OPEN.add(new Point(nextOnPath.getX()-10,nextOnPath.getY()-10));
			OPEN.add(new Point(nextOnPath.getX()-10,nextOnPath.getY()+10));
			OPEN.add(new Point(nextOnPath.getX()+10,nextOnPath.getY()-10));
			OPEN.add(new Point(nextOnPath.getX()-10,nextOnPath.getY()));
			OPEN.add(new Point(nextOnPath.getX(),nextOnPath.getY()-10));
			for(int i = OPEN.size();i>=0;i--)
			{
				for(int j=0;j<CLOSED.size();j++)
				{
					if(OPEN.get(i).equals(CLOSED.get(j)))
						OPEN.remove(i);
				}
			}	
				
				//prints points in OPEN
			for(int i = 0;i<OPEN.size();i++)
				OPEN.get(i).print();
				
				//computes cost for all points in OPEN
			for(int i=0;i<OPEN.size();i++)
			{
				OPEN.get(i).changeCost(computeCost(/*vector*/ ,OPEN.get(i)));
			}
			
				//adds point with lowest cost to the path
			lc = lowestCost();
			Path1.add(lc);
			
				//moves points in OPEN to CLOSED and removes points from OPEN
			for(int i=OPEN.size();i>=0;i--)
			{
				CLOSED.add(OPEN.get(i));
				OPEN.remove(i);
			}
			
			nextOnPath.changeX(lc.getX());
			nextOnPath.changeY(lc.getY());
				//end cond.
			if(lc.equals(GOAL))
				go = false;
		}
		
		return Path1;
	}
	
	public static void main(String[] args) throws IOException
	{
		boolean run = Connect();
		windMaker();
		impo = new vector6( wind, boat );
			//creates arraylist of obstacles
		Obstacles = new ArrayList<obstacle>(); 
		API.send("obstacleCount ");
		Integer g = Integer.valueOf(API.receive()[1]);
		for(Integer i=0;i<g;i++)
		{
			Obstacles.add(OBstacle(i));
		}
			
		//prints Obstacles
		for(int i=0;i<Obstacles.size();i++)
		{
			System.out.println(Obstacles.get(i).getID());
			System.out.println(Obstacles.get(i).getRadius());
			Obstacles.get(i).getPoint().print();
		}
			
		//sets goal position
		API.send("goalPosition ");
		String cords = API.receive()[1];
		float xcord = Float.valueOf(cords.substring(0,cords.indexOf(" ")));
		float ycord = Float.valueOf(cords.substring(cords.indexOf(" ")));
		Point GOAL = new Point(xcord,ycord);
		GOAL.print();
		System.out.println();
		
		//finds starting boat position
		API.send("boatPosition ");
		String cor = API.receive()[1];
		float BX = Float.valueOf(cor.substring(0,cor.indexOf(" ")));
		float BY = Float.valueOf(cor.substring(cor.indexOf(" ")));
		Point b = new Point(BX,BY);
		b.print();
		System.out.println();
			
		//initializes arraylists
		OPEN = new ArrayList<Point>();
		CLOSED = new ArrayList<Point>();
		System.out.println("NO GO");
		
		//adds points on graph of obstacles to CLOSED list
		for(int i=2;i<Obstacles.size();i++)
		{
			GRAPH = Obstacles.get(i).Graph();
			System.out.println(Obstacles.get(i).getID());
			for(int j =0;j<GRAPH.size();j++)
			{
				CLOSED.add(GRAPH.get(j));
				GRAPH.get(j).print();
			}
			for(int f=GRAPH.size()-1;f>=0;f--)
			{
				GRAPH.remove(f);
			}
		}
		
		nextOnPath =  new Point(b.getX(),b.getY());

		for(int i =0;i<Path.size();i++)
		{
			executeTurn(Path1.get(i).getX(),Path1.get(i).getY());
		}

		API.disconnect();
	}
}