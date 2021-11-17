public class convertAbsolute
{
	//This class is designed to see if absolute sail angles converted will
	// be the same thing as converting boat heading, then converting sailangle,
	// adding them together and then converting themm will be the same thing
	
	public static void main( String args[] )
	{
		int[] boatHeadingArray = new int[]{ 0, 45, 90, 135, 180, 225, 270, 315, 360 };
		int[] sailAngleArray = new int[]{ -90, -45, 0, 45, 90 };		

		System.out.println( "Boat headings conversions: " );
		for( int bh : boatHeadingArray )
			System.out.println( bh + " => " + ( ( 450 - bh ) % 360 ) );
		System.out.println();
	
		System.out.println( "Sail angle conversions: " );
		for( int sa : sailAngleArray )
			System.out.println( sa + " => " + ( ( 450 - sa ) % 360 ) );
		System.out.println();
	
		System.out.println( "Absolute sail angles calculated pre conversion: " );
		for( int bh : boatHeadingArray )
		{
			for( int sa : sailAngleArray )
				System.out.print( "BH: " + bh + " SA: " + sa + " Abs: " + ( bh + sa ) + " " );
			System.out.println();
		}
		System.out.println();
		
		System.out.println( "Converted absolute sail angles, BH & SA are still in compass" );
		for( int bh : boatHeadingArray )
		{
			for( int sa : sailAngleArray )
				System.out.print( "BH: " + bh + " SA: " + sa + " Abs: " + ( ( 450 -( bh + sa ) ) % 360 ) + " " );
			System.out.println();
		}

		System.out.println( "Converted absolute sail angles, BH & SA converted, then added" );
		for( int bh : boatHeadingArray )
		{
			for( int sa : sailAngleArray )
				System.out.print( "BH: " + bh + "(compass) SA: " + sa + "(compass) Abs: " + 
						( ( ( 450 -( bh  ) ) % 360 ) + ( ( 450 - ( sa ) ) % 360 ) + " " ) );
			System.out.println();
		}
	}
}