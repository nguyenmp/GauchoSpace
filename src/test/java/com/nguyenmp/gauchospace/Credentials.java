package com.nguyenmp.gauchospace;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;


public class Credentials {
	private static String mUsername = null;
	private static String mPassword = null;

	private static void init() throws IOException {
		// the username and password should be stored as the first
		// and second line in credentials
        FileReader fReader;
        try {
            fReader = new FileReader("credentials");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("credentials file has not been defined.  Please see the README.md for help.");
        }
		BufferedReader reader = new BufferedReader(fReader);
		mUsername = reader.readLine();
		mPassword = reader.readLine();

        if (mUsername == null || mPassword == null)
            throw new IllegalStateException("Username or password have not been defined in the credentials file.");
	}

	public static String Username() throws IOException {
		// If we already loaded a username before, return that!
		// Otherwise, read a new one
		if (mUsername == null) init();

		return mUsername;
	}

	public static String Password() throws IOException {
		// If we already loaded a password before, return that!
		// Otherwise, read a new one
		if (mPassword == null) init();

		return mPassword;
	}
}
