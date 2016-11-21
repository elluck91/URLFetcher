package CS151;

import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class MyLabel2 extends Label implements Observer{
	
	int threadsRunning;
	String myMsg;
	
	MyLabel2(String msg) {
		super(msg);
		threadsRunning = 0;
		myMsg = msg;
		changeText();
		
	}
	
	private void changeText() {
		Platform.runLater(() -> {
			this.setText(myMsg + threadsRunning);
		});
		
	}

	private void changeToZero() {
		Platform.runLater(() -> {
			this.setText(myMsg + 0);
		});
	}
	
	@Override
	public synchronized void update(Observable arg0, Object arg1) {
		if ((int)arg1 == 0)
			changeToZero();
		else {
			threadsRunning += (int) arg1;
			changeText();
		}
		
	}



}