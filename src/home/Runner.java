package home;

import java.util.Arrays;

public class Runner {

	public static void main(String[] args)
	{
		SettingsScraper scraper = new SettingsScraper(args[0]);
		Boolean[] make = new Boolean[2];
		Arrays.fill(make, true);
		for (int i = 1; i < args.length; i++)
		{
			make[Integer.parseInt(args[i].substring(1))] = false;
		}
		scraper.login();
		if (make[0])
			scraper.openSecurity();
		if (make[1])
			scraper.openApps();
	}
}
