import java.io.IOException;

public class obstacle_gui
{
    private Integer ID;
    private float radius;
    private point pt;

    //creates an obstacle with Ordered Pair, Radius and ID
    public obstacle(Integer ID,float radius,point pt) throws IOException
    {
        this.ID = ID;
        this.radius = radius;
        this.pt = pt;

    }

    public point getPoint() 
    {
        return pt;
    }

    public float getRadius() { return radius; }

    public float getID() {
        return ID;
    }
}

