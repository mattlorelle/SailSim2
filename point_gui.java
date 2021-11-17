public class point_gui
{
	private float x;
	private float y;
	private float cost;
	
	public point_gui(float X,float Y)
	{
		x=X;
		y=Y;
		cost = 0;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	public float getCost()
	{
		return cost;
	}
	public void changeX(float nx)
	{
		x=nx;
	}
	public void changeY(float ny)
	{
		y=ny;
	}
	public void changeCost(float c)
	{
		cost = c;
	}
	public void print()
	{
		System.out.println("("+x+","+y+")");
	}
	public boolean equals(Point p)
	{
		if(this.x == x && this.y ==y)
			return true;
		else
			return false;
	}
	
}