package CS151;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import CS151.URLFetcher.URLStrings;
import javafx.scene.control.ProgressBar;

public class WebWorker extends Thread{
	/*
  This is the core web/download i/o code...*/
	URLStrings urlString;
	URLFetcher frame;
	Semaphore sema;
	CountDownLatch cdl;
	int row;
	ProgressBar pb;
	int progressLength;
	MyLabel completed;
	MyLabel2 running;

	public WebWorker(URLStrings url, int row, URLFetcher webFrame, Semaphore sema, 
			CountDownLatch cdl, ProgressBar pb, int size, MyLabel completed, MyLabel2 running) {
		this.cdl = cdl;
		urlString = url;
		frame = webFrame;
		this.row = row;
		this.sema = sema;
		this.pb = pb;
		this.completed = completed;
		this.running = running;
	}

	public void run() {

		System.out.println("Fetching...." + urlString);
		InputStream input = null;
		StringBuilder contents = null;
		try {
			sema.acquire();
			Thread.sleep(100);

			changeOne();

			URL url = new URL(urlString.getURL());
			URLConnection connection = url.openConnection();

			// Set connect() to throw an IOException
			// if connection does not succeed in this many msecs.
			connection.setConnectTimeout(5000);

			connection.connect();
			input = connection.getInputStream();

			BufferedReader reader  = new BufferedReader(new InputStreamReader(input));

			char[] array = new char[1000];
			int len;
			contents = new StringBuilder(1000);
			while ((len = reader.read(array, 0, array.length)) > 0) {
				contents.append(array, 0, len);
				Thread.sleep(100);

				if (isInterrupted())
					updateThree();
			}



			System.out.println(contents.toString());
			changeTwo();
			decrementCdl();
			sema.release();


		}
		// Otherwise control jumps to a catch...
		catch(MalformedURLException ignored) {
			System.out.println("Exception: " + ignored.toString());
		}
		catch(IOException ignored) {
			System.out.println("Exception: " + ignored.toString());
		} catch (InterruptedException e) {
			updateThree();
		}

		// "finally" clause, to close the input stream
		// in any case
		finally {
			try{
				if (input != null) input.close();
			}
			catch(IOException ignored) {}
		}
	}

	private synchronized void changeOne() {
		urlString.update(row, urlString.getURL(), "Starting Download");
		running.update(null, 1);

	}

	private synchronized void changeTwo() {
		urlString.update(row, urlString.getURL(), "Download complete.");
		running.update(null, -1);
		completed.update(null, null);

	}

	private synchronized void updateThree() {
		urlString.update(row, urlString.getURL(), "Stopped Download");
		pb.setProgress(0.0);
		running.update(null, 0);

	}

	private synchronized void decrementCdl() {
		cdl.countDown();
	}


}
