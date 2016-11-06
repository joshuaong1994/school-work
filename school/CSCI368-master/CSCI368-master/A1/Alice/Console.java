import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Console {

	public static String readString(String text)
	{
		String line;
		String result = "";		
		try
		{
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader input = new BufferedReader(isr);
		
			boolean success = false;
			
			while(!success)
			{
				try
				{
					System.out.print(text);
					line = input.readLine();
					
					success = true;
					result = line;
				}
				catch(Exception ex)
				{
					System.out.println("Invalid Input!");
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Error in Console I/O: " + e.getMessage());
		}
		return result;
	}
	
	public static char readChar(String text)
	{
		String line;
		char result = 0;		
		try
		{
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader input = new BufferedReader(isr);
		
			boolean success = false;
			
			while(!success)
			{
				try
				{
					System.out.print(text);
					line = input.readLine();
					line.trim();
					
					if(line.length()>1)
						throw new Exception("Invalid Input Exception");
		
					success = true;
					result = line.charAt(0);
				}
				catch(Exception ex)
				{
					System.out.println("Invalid Input!");
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Error in Console I/O: " + e.getMessage());
		}
		return result;
	}
	
	public static int readInt(String text)
	{
		String line;
		int result = 0;		
		try
		{
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader input = new BufferedReader(isr);
		
			boolean success = false;
			
			while(!success)
			{
				try
				{
					System.out.print(text);
					line = input.readLine();
					line.trim();
					result= Integer.parseInt(line);
					success = true;
				}
				catch(Exception ex)
				{
					System.out.println("Invalid Input!");
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Error in Console I/O: " + e.getMessage());
		}
		return result;
	}
	
	public static double readDouble(String text)
	{
		String line;
		double result = 0;		
		try
		{
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader input = new BufferedReader(isr);
		
			boolean success = false;
			
			while(!success)
			{
				try
				{
					System.out.print(text);
					line = input.readLine();
					line.trim();
					result= Double.parseDouble(line);
					success = true;
				}
				catch(Exception ex)
				{
					System.out.println("Invalid Input!");
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Error in Console I/O: " + e.getMessage());
		}
		return result;
	}
	
	public static boolean readBoolean(String text)
	{
		String line;
		boolean result = false;		
		try
		{
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader input = new BufferedReader(isr);
		
			boolean success = false;
			
			while(!success)
			{
				try
				{
					System.out.print(text+"(T/F)");
					line = input.readLine();
					line.trim();
					
					if(line.length()>1)
						throw new Exception("Invalid Input Exception");
					
					char first = line.charAt(0);
					if ((first == 'T') || (first == 't'))
					{
						success = true;
						result = true;
					}
					else if ((first == 'F') || (first == 'f'))
					{
						success = true;
						result = false;
					}
					else
					{
						throw new Exception("Invalid Input Exception");
					}
				}
				catch(Exception ex)
				{
					System.out.println("Invalid Input!");
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Error in Console I/O: " + e.getMessage());
		}
		return result;
	}
	
	public static void main(String args[])
	{
		char resultC = Console.readChar("Enter A CHARACTER:");		
		System.out.println("USER INPUT CHARACTER = " + resultC);
		
		String resultS = Console.readString("Enter A STRING:");		
		System.out.println("USER INPUT STRING = " + resultS);
		
		boolean resultB = Console.readBoolean("Enter A BOOLEAN:");		
		System.out.println("USER INPUT BOOLEAN = " + resultB);
		
		int num = Console.readInt("Enter an INTEGER:");
		System.out.println("USER INPUT INTEGER = " + num);
	}
}