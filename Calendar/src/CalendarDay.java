/**
 * EECS 448 Project 1: Calendar
 * CalendarDay.java
 * Authors: Rebekah Manweiler, Shawn Parkes, Stephen Fulton
 * Date: 2016-09-17
 */

import java.io.*;
/**
 * <h2>CalendarDay Week</h2>
 * <p>Contains an array of events and a String "dayOfWeek"
 */
public class CalendarDay
{
	private int date = 0;
	private String month;
	private String dayOfWeek;
	public String[] dayEvents;
	private String monthFilePath;
	
	/**
	 * <h3>Calendar Day Constructor</h3><p>
	 * Creates a Calendar Day object based on the given date and month that stores its day of the week and events for the day
	 * @param d - int: date of month (1,2...31)
	 * @param m - String: name of CalendarMonth that CalendarDay belongs to
	 * @see CalendarMonth
	 */
	public CalendarDay(int d, String m)
	{
		date = d;
		month = m;
		dayEvents = new String[0];
		monthFilePath = "MonthFiles/" + m + ".txt";
	}

	/**
	 * Sets the dayOfWeek variable in the CalendarDay object.
	 * <p>Returns nothing.
	 * @param dow - String: Day of week (Sunday...Saturday)
	 */
	public void setDayOfWeek(String dow)
	{
		dayOfWeek = dow;
	}
	
	/**
	 * Returns the date as an integer from the CalendarDay object.
	 * @return the date of the given CalendarDay
	 */
	public int getDate()
	{
		return date;
	}
	
	public String getFilePath()
	{
		return monthFilePath;
	}
	
	/**
	 * Returns the month as a string from the CalendarDay object.
	 * @return the month that the given CalendarDay belongs to.
	 */
	public String getMonth()
	{
		return month;
	}
	
	/**
	 * Returns the day of the week (Sunday...Saturday) from the CalendarDay object.
	 * @return day of week as string of the given CalendarDay
	 */
	public String getDayOfWeek()
	{
		return dayOfWeek;
	}
	
	/**
	 * Returns the events of the given day as a string to be displayed as a list.
	 * @return A string of all events concatenated together.
	 */
	public String getEvents()
	{
		String temp = "";
		for (int i = 0; i < dayEvents.length; i++)
		{
			temp += (i+1) + ". " + dayEvents[i] + "\n";
		}
		return temp;
	}
	
	/**
	 * Adds a new event to the CalendarDay's array of events and writes the event to specific CalendarDay's month file. <p>Event  can be any String the user wants to put.
	 * @param event - Any String the user wants stored to the specific CalendarDay
	 */
	public void addEvent(String event)
	{
		//if the passed in event is empty, then it does nothing
		event = event.trim();
		if(event.equals(""))
		{
			return;
		}
		try
		{
			//opens this day's month text file and adds the event, using it's date as a code at the beginning of the string.
			BufferedWriter bw = new BufferedWriter(new FileWriter(monthFilePath, true));
			bw.newLine();
			bw.write(date + " " + event);
			bw.close();
			
		}catch(IOException e){}
		addEventToArray(event);
	}

	/**
	 * Adds the event to the CalendarDay's array to be used for display and record purposes.
	 * @param event - The given string from addEvent that is to be added to the day
	 * @see addEvent
	 */
	public void addEventToArray(String event)
	{
		//Simulates a "dynamic" array, adjusts length to allow space for new event
		String[] temp = new String[dayEvents.length + 1];
		for(int i = 0; i < dayEvents.length; i++)
		{
			temp[i] = dayEvents[i];
		}
		temp[dayEvents.length] = event;
		dayEvents = temp;	
	}
	
	/**
	 * Removes event from the CalendarDay's events array and from its corresponding month file.
	 * <p>
	 * Function always ends if user requests to remove an event that is not within the list of events, either 0 or a value greater than the known number of events.
	 * @param event - Number from list of events user wants to remove.
	 */
	public void removeEvent(int event)
	{
		//if user miraculously passes in an event that is out of bounds of the day's event array, this function will do nothing.
		if(event <= 0 || event > dayEvents.length)
		{
			return;
		}else{event = event - 1;}
		
		//Java File I/O does not allow you to selectively remove lines without leaving white spaces
		//As such, a new temporary file is opened, everything that is not on the current day is copied over
		//Then everything from the day's array (sans the event being removed) is stored into the file
		//And then the original file is deleted and the temporary file is renamed to the original file
		try{
			File monthPath = new File(monthFilePath);
			File tempFile = new File("MonthFiles/temp.txt");
			BufferedReader br = new BufferedReader(new FileReader(monthPath));
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
			
			//By removing the event from the array first, when the array is written to the file it will be gone
			removeEventFromArray(event);
			String x = br.readLine();
			while(x != null)
			{
				//if the read in string is empty, it just skips and moves on.
				if(x.equals("") == false)
				{
					//Takes a substring of the first two characters of a line, which are the code for determining the date the event belongs to.
					String trimmedX = x.substring(0, 2);
					trimmedX = trimmedX.trim();
					if(Integer.parseInt(trimmedX) != date)
					{
						bw.write(x);
						bw.newLine();
					}
				}
				x = br.readLine();
			}
			for(int i = 0; i < dayEvents.length; i++)
			{
				bw.write(date + " " + dayEvents[i]);
				bw.newLine();
			}
			br.close();
			bw.close();
			
			//set the temp file to be the new month file for that month
			monthPath.delete();
			tempFile.renameTo(new File(monthFilePath));
		}catch(IOException e){
			System.out.println(e);
		}
	}
	/**
	 * Removes the event at the given index from the CalendarDay's event array.
	 * @param event - index of the event to be removed from array. Will always be valid.
	 */
	public void removeEventFromArray(int event)
	{
		//Simulates a "dynamic" array, removes spaces that no longer stores events
		String[] temp = new String[dayEvents.length - 1];
		int tempCount = 0;
		for (int i = 0; i < dayEvents.length; i++)
		{
			if(i == event){}
			else
			{
				temp[tempCount] = dayEvents[i];
				tempCount++;
			}
		}
		dayEvents = temp;
	}
	
	/**
	 * Loads in events from the day's month file into its event array
	 * <p>Prints out an error if the file stops existing or encounters an error for any reason.
	 */
	public void loadDayEvents()
	{
		try
		{
			//sets event array to 0 since there are no events in it and java is simulating a dynamic array.
			dayEvents = new String[0];
			BufferedReader br = new BufferedReader(new FileReader(monthFilePath));
			String temp = br.readLine();
			while(temp != null)
			{
				if(temp.equals("") == false)
				{
					//gets code from line
					String temp2 = temp.substring(0, 2);
					temp2 = temp2.trim();
					try{
						//if code matches, then take the line, remove the code from it and add it to the day's event array
						if(Integer.parseInt(temp2) == date)
						{
							temp = temp.substring(2);
							temp = temp.trim();
							addEventToArray(temp);
						}	
					}catch(Exception e){}
				}
				temp = br.readLine();
			}
			br.close();
		}catch(IOException e)
		{System.out.println(e);}
	}
	
	/**
	 *  Gets the number of events in the day's event array
	 * @return int - day's event array length
	 */
	public int getEventCount()
	{
		return dayEvents.length;
	}
}
