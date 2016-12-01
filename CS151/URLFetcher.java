package CS151;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class URLFetcher extends Application {

	static ProgressBar pb;
	private long startTime;
	private long stopTime;
	static private int numOfURL;
	private static ObservableList<URLStrings> data;
	private ArrayList<WebWorker> workers;
	private CountDownLatch cdl;
	static String path;
	Button single;
	Button stop;
	Button concurrent;
	Button launcher;
	MyLabel3 time;
	MyLabel completed;
	MyLabel2 running;


	public void start(Stage stage) {
		data = FXCollections.observableArrayList();
		workers = new ArrayList<WebWorker>();
		readData(path);
		numOfURL = data.size();

		single = new Button("Single Thread Fetch");
		single.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				workers.clear();
				time.update(null, "0");
				startTime = System.nanoTime();
				single.setDisable(true);
				concurrent.setDisable(true);
				launcher.setDisable(true);
				startLauncher(1);
			}
		});

		single.setMinWidth(150);

		// Takes number of threads to use in the concurrent fetch
		TextField thread = new TextField("# of threads to add:");
		thread.setMaxWidth(150);

		// starts the concurrent fetch
		concurrent = new Button("Concurrent Fetch");
		concurrent.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				workers.clear();
				time.update(null, "0");
				startTime = System.nanoTime();
				single.setDisable(true);
				concurrent.setDisable(true);
				launcher.setDisable(true);
				startLauncher(Integer.parseInt(thread.getText()));
			}});
		concurrent.setMinWidth(150);


		stop = new Button("Stop");
		stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				for(WebWorker w : workers)
						w.interrupt();

				single.setDisable(false);
				concurrent.setDisable(false);
				launcher.setDisable(false);
				
			}

		});
		stop.setMinWidth(150);
		
		launcher = new Button("Launcher");
		launcher.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				workers.clear();
				time.update(null, "0");
				startTime = System.nanoTime();
				single.setDisable(true);
				concurrent.setDisable(true);
				launcher.setDisable(true);
				
				startLauncher(numOfURL);
				
			}

		});
		launcher.setMinWidth(150);
		Scene scene = new Scene(new VBox(), 840, 540);

		stage.setTitle("Table View Sample");

		final VBox left = new VBox();
		left.setAlignment(Pos.CENTER);
		left.setSpacing(5);
		left.setPadding(new Insets(20, 0, 0, 20));
		left.getChildren().addAll(single, thread, concurrent, stop, launcher);



		completed = new MyLabel("Downloads completed: ", numOfURL);
		running = new MyLabel2("Threads Running: ");
		time = new MyLabel3("Time elapsed: ");
		pb = new ProgressBar(0);
		pb.setMinWidth(800);

		final VBox leftBottom = new VBox();
		leftBottom.setAlignment(Pos.TOP_LEFT);
		leftBottom.setSpacing(5);
		leftBottom.setPadding(new Insets(20, 0, 0, 20));
		leftBottom.getChildren().addAll(completed, running, time, pb);       

		TableView<URLStrings> tv = new TableView();
		TableColumn URLCol = new TableColumn("URL");
		URLCol.setMinWidth(200);
		URLCol.setCellValueFactory(
				new PropertyValueFactory<URLStrings, String>("URL"));

		// Second column
		TableColumn statusCol = new TableColumn("Status");
		statusCol.setMinWidth(200);
		statusCol.setCellValueFactory(
				new PropertyValueFactory<URLStrings, String>("status"));

		tv.getColumns().addAll(URLCol, statusCol);

		tv.setItems(data);
		tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		final VBox right = new VBox();
		right.setAlignment(Pos.CENTER);
		right.setSpacing(5);
		right.setPadding(new Insets(20, 0, 0, 20));
		right.getChildren().addAll(tv);

		GridPane gp = new GridPane();
		ColumnConstraints column = new ColumnConstraints(400);
		gp.getColumnConstraints().add(column);
		gp.addColumn(0, left);
		gp.addColumn(0, leftBottom);
		gp.addColumn(1, right);
		gp.setVisible(true);

		((VBox) scene.getRoot()).getChildren().addAll(gp);
		stage.setScene(scene);
		stage.show();

	}

	void startLauncher (int numPermits) {
		Semaphore sema = new Semaphore(numPermits);
		cdl = new CountDownLatch(numOfURL);
		pb.setProgress(0.0);

		new Thread(() -> {
			running.update(null, 1);
			for (int rows = 0; rows < numOfURL; rows++) {
				WebWorker worker = new WebWorker(data.get(rows), rows, this, sema, cdl, pb, numOfURL, completed, running);
				workers.add(worker);
				worker.start();
			}
			try {
				cdl.await();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			stopTime = System.nanoTime() - startTime;
			String elapsed = TimeConverter.convertTimeToString(stopTime);
			time.update(null, elapsed);
			single.setDisable(false);
			concurrent.setDisable(false);
			launcher.setDisable(false);


		}).start();





	}

	private void readData(String path) {
		FileReader fr;
		try {
			fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			String url;

			while ((url = br.readLine()) != null) {
				data.add(new URLStrings(url, ""));	
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		path = args[0];
		launch(args);
	}

	public static class URLStrings implements Observer {
		StringProperty URL;
		StringProperty status;

		URLStrings(String URL, String status) {
			this.URL = new SimpleStringProperty(URL);
			this.status = new SimpleStringProperty(status);
		}

		public synchronized String getURL() {
			return URL.get();
		}

		public String getStatus() {
			return status.get();
		}

		@Override
		public void update(Observable arg0, Object arg1) {

		}

		public synchronized void update(int row, String url, String arg1) {
			Platform.runLater(() -> {
				data.set(row, new URLStrings(url, arg1));
				double value = pb.getProgress();
				pb.setProgress(value + (0.5/numOfURL));
			});
			

		}
	}


}
