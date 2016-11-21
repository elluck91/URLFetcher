package CS151;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class Bank {
	static ArrayList<Account> accounts;
	static LinkedBlockingQueue<Transaction> bq;
	static List<Worker> workers;
	private static CountDownLatch cdl;
	
	private static class Worker extends Thread {
		
		@Override
		public void run() {
			boolean notEmpty = true;
			try {
				while (notEmpty) {
					Transaction tr = bq.take();
					if (tr.getAccountFrom() == -1) {
						cdl.countDown();
						notEmpty = false;
					}
					else {
						accounts.get(tr.getAccountFrom()).withdraw(tr.getAmount());
						accounts.get(tr.getAccountTo()).deposit(tr.getAmount());
					}
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		try {
			accounts = new ArrayList<Account>();
			workers = new ArrayList<Worker>();
			bq = new LinkedBlockingQueue<Transaction>();
			cdl = new CountDownLatch(Integer.parseInt(args[1]));
			

			for (int i = 0; i < 20; i++) {
				accounts.add(new Account(i));
			}
			
			for (int i = 0; i < Integer.parseInt(args[1]); i++) {
				workers.add(new Worker());
			}
			
			for (int i = 0; i < Integer.parseInt(args[1]); i++)
				workers.get(i).start();

			FileReader fr;

			if (args.length != 0) {
				fr = new FileReader(args[0]);
			} else
				throw new FileNotFoundException();

			BufferedReader br = new BufferedReader(fr);
			String transaction;
			
			while ((transaction = br.readLine()) != null) {
				String[] tempTransaction = transaction.split(" ");


				bq.put(
						new Transaction(Integer.parseInt(tempTransaction[0]),
						Integer.parseInt(tempTransaction[1]),
						Integer.parseInt(tempTransaction[2])
						));
			}

			for (int i = 0; i < Integer.parseInt(args[1]); i++) {
				bq.put(new Transaction(-1,0,0));
			}

			
			
			cdl.await();
			
			for (int i = 0; i < accounts.size(); i++) {
				System.out.println(accounts.get(i).toString());
			}
			
			br.close();

		} catch (FileNotFoundException e) {
			System.out.println("The file could not be found or input has not been provided.");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
