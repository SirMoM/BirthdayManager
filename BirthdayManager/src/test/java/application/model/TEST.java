package application.model;

import java.io.File;
import java.io.IOException;

public class TEST{
	public static void main(final String[] args) throws IOException{
		final File output = new File("output.txt");
		output.createNewFile();

		PersonManager.getInstance().setSaveFile(output);
	}

}
