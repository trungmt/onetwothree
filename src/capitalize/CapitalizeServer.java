/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package capitalize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BITVN12
 */
public class CapitalizeServer {
	public static void main(String[] args) throws IOException {
		int clientNumber = 0;
		
		/**
		 * Start a server by give it a socket listener at port 9898
		 * while server is running it waits for client's connection request
		 * Then a thread to handle new client is created
		 */
		ServerSocket listener = new ServerSocket(9898);
		System.out.println("The capitalization server is running");
		try {
			while(true){
				new Capitalizer(listener.accept(), clientNumber++).start();
			}
		} finally {
			listener.close();
		}
	}

	/**
	 * Handle one client - server connection
	 * First it sends to client a welcoming message
	 * then each time client send a text, this class make all text capitalize
	 * and send back text to client
	 */
	private static class Capitalizer extends Thread {

		private final Socket socket;
		private final int clientNumber;
		
		public Capitalizer(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New connection with client#" + clientNumber + " at " + socket);
		}
		
		public void run() {
			try {
				BufferedReader in = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				PrintWriter out   = new PrintWriter(socket.getOutputStream(), true);
				
				out.println("Hello, you are client #" + clientNumber + ".");
				out.println("Enter a line with only a period to quit \n");
				
				while(true) {
					String input = in.readLine();
					if(input == null || input.equals('.')) {
						break;
					}
					out.println(input.toUpperCase());
				}
				
			} catch (IOException ex) {
				Logger.getLogger(CapitalizeServer.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				try {
					socket.close();
				} catch (IOException ex) {
					Logger.getLogger(CapitalizeServer.class.getName()).log(Level.WARNING, "cant close socket");
					Logger.getLogger(CapitalizeServer.class.getName()).log(Level.WARNING, null, ex);
				}
				Logger.getLogger(CapitalizeServer.class.getName()).log(Level.SEVERE, "Connect with client #{0} closed", clientNumber);
			}
		}
	}
}
