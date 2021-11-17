public class vector2
{
	private final vector wind;
	private vector boat;
	private vector[] sailArray = new vector[181];
	private vector[] vectorArray = new vector[181];
	
	public vector6( vector wind2, vector boat2 )
	{
		wind = new vector( wind2.getDirection(), wind2.getMagnitude() );
		boat = new vector( ( 450 - ( ( 180 / (float) Math.PI ) * boat2.getDirection() ) ) % 360 , 1 );	
	}
	
	public void setBoat( float boatHeading )
	{
		boat.setDirection( ( 450 - boatHeading ) % 360 );
		int count = 0;
		for( int i=90; i<271; i++ )
		{	sailArray[count] = new vector( boat.getDirection() + i , 1 );
			count++;	}
		for( int i=0; i<181; i++ )
			vectorArray[i] = wind.windToBoat( sailArray[i], boat );
	}
	
	public int getSail()
	{
		int index = -1;
		float max = 0;
		for( int i=0; i<181; i++ )
		{
			if( vectorArray[i].getQuadrant() == boat.getQuadrant() )
				if( vectorArray[i].getMagnitude() > max )
				{
					max = vectorArray[i].getMagnitude();
					index = i;
				}
		}
		return 90-index;
	}
}


