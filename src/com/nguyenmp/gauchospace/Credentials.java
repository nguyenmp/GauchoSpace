package com.nguyenmp.gauchospace;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;


public class Credentials {
	private static String mUsername = null;
	private static String mPassword = null;

	public static String Username() throws IOException {
		// If we already loaded a username before, return that!
		if (mUsername != null) return mUsername;

		// Otherwise, read in the username for the first time
		else {
			FileReader fReader = new FileReader("credentials.txt");
			BufferedReader reader = new BufferedReader(fReader);
			mUsername = reader.readLine();
			mPassword = reader.readLine();

			return mUsername;
		}
	}
}
