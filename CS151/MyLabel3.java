package CS151;

import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class MyLabel3 extends Label implements Observer{
	
	String myMsg;
	
	MyLabel3(String msg) {
		super(msg);
		myMsg = msg;
	}
	
	private void changeText(String elapsedTime) {
		Platform.runLater(() -> {
			this.setText(myMsg + elapsedTime);
		});
		
	}
	
	@Override
	public synchronized void update(Observable arg0, Object arg1) {

		changeText((String) arg1);
	}

}
