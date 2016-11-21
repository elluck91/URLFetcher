package CS151;

import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class MyLabel extends Label implements Observer{
	int completed;
	int outOf;
	String myMsg;
	
	MyLabel(String msg, int urls) {
		super(msg);
		outOf = urls;
		completed = 0;
		myMsg = msg;
		this.changeText();
	}

	private void changeText() {
		Platform.runLater(() -> {
			this.setText(myMsg + completed + "/" + outOf);
		});
		
	}
	
	@Override
	public synchronized void update(Observable arg0, Object arg1) {
		completed++;
		changeText();
	}

}
