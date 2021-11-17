import static java.lang.Math.*;

public class Vector
{
	private float[] componentArray = new float[2]; //This will contain x-y components
	private float[] vectorArray = new float[2]; //This will contain direction-magnitude
	
	//Typical constructor, parameters in direction then magnitude order
	//Then uses trigonometry to make components

	public Vector( float direction, float magnitude )
	{
		vectorArray[0] = direction;
		vectorArray[1] = magnitude;
	
		componentArray[0] = magnitude * (float) cos( ( (float) PI / 180 ) * direction ) ;
		componentArray[1] = magnitude * (float) sin( ( (float) PI / 180 ) * direction ) ;
	}

	//ACCESSORS
	public float getDirection()
	{	return vectorArray[0];	}

	public float getMagnitude()
	{	return vectorArray[1];	}

	public float getXComponent()
	{	return componentArray[0];	}

	public float getYComponent()
	{	return componentArray[1];	}

	//This method will return the quadrant of motion
	public int getQuadrant()
	{
		if( componentArray[0] > 0 && componentArray[0] > 0 )
			return 1;
		if( componentArray[0] < 0 && componentArray[0] > 0 )
			return 2;
		if( componentArray[0] < 0 && componentArray[0] < 0 )
			return 3;
		if( componentArray[0] > 0 && componentArray[0] < 0 )
			return 4;
		return 0;
	}
	
	//Projects current vector object onto b
	public float project( Vector b )
	{
		float alpha = vectorArray[0] - b.getDirection();
		return( vectorArray[1] * (float) cos( alpha * ( (float) PI / 180 ) ) );
	}

	public Vector getProject( Vector b )
	{
		return new Vector( b.getDirection(), project( b ) );
	}

	//this will be enacted on a wind vector
	public Vector windToBoat( Vector absoluteSail, Vector boat )
	{
		float wind = getDirection();
		float sailAngle = absoluteSail.getDirection();
		if( wind < sailAngle && sailAngle < wind + 180 )
			sailAngle -= 90;
		else if( wind + 180 < sailAngle && sailAngle < wind + 360 )
			sailAngle += 90;
		else if( wind == sailAngle || wind + 180 == sailAngle )
			return new Vector( wind, 0 );
		return getProject( new Vector( sailAngle, 0 ) ).getProject( boat );
	}
		
		

	public Vector fin( Vector b, Vector c )
	{
		Vector boatVector = ( windToBoat( b, c ) );
		Vector boatX = new Vector( 0, boatVector.getXComponent() );
		Vector boatY = new Vector( 90, boatVector.getYComponent() );
		Vector boatX2 = boatX.getProject( c );
		Vector boatY2 = boatY.getProject( c );
		return new Vector( c.getDirection(), boatX2.getMagnitude() + boatY2.getMagnitude() );
	}
	public String toString()
	{
		return( "[ " + getXComponent() + ", " + getYComponent() + " ]" );
	}
}






