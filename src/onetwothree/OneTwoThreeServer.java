/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onetwothree;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import static onetwothree.ConstantValue.SERVER_NEWCOMER;
import static onetwothree.ConstantValue.SERVER_NEWOUTER;
import org.json.JSONObject;

/**
 *
 * @author BITVN12
 */
public class OneTwoThreeServer extends javax.swing.JFrame {

	private static ServerSocket listener;
        private static DefaultListModel<String> listModel = new DefaultListModel<>();
        private static Map<String, Authentication> clients 
                = new HashMap<>();
        private static int numClient = 0;
        private static OneTwoThreeServer server;
        
	/**
	 * Creates new form OneTwoThreeServer
	 */
	public OneTwoThreeServer() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        areaLog = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        listUser = new javax.swing.JList<>();
        btnStopServer = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        areaLog.setEditable(false);
        areaLog.setColumns(20);
        areaLog.setRows(5);
        jScrollPane1.setViewportView(areaLog);

        listUser.setBorder(javax.swing.BorderFactory.createTitledBorder("Online User List"));
        listUser.setFocusable(false);
        jScrollPane2.setViewportView(listUser);

        btnStopServer.setBackground(new java.awt.Color(204, 0, 0));
        btnStopServer.setForeground(new java.awt.Color(255, 255, 255));
        btnStopServer.setText("Stop Server!");
        btnStopServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopServerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnStopServer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGap(18, 18, 18)
                .addComponent(btnStopServer)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void setAreaLog(String log){
        areaLog.append(log);
    }
    
    private void btnStopServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopServerActionPerformed
		try {
			areaLog.append("Server is stopped!");
			listener.close();
		} catch (IOException ex) {
			Logger.getLogger(OneTwoThreeServer.class.getName()).log(Level.SEVERE, null, ex);
		}
    }//GEN-LAST:event_btnStopServerActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) throws IOException, Exception {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(OneTwoThreeServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(OneTwoThreeServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(OneTwoThreeServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(OneTwoThreeServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>
		
		listener = new ServerSocket(8901);
		System.out.println("Keo Bua Bao Server is Running");
		
//                JSONObject jsonObj = new JSONObject(a);
//                JSONObject content = jsonObj.getJSONObject("content");
//                String pass = content.getString("username");
//                System.out.println(pass);
		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
                            server = new OneTwoThreeServer();
                            server.setVisible(true);
                            server.listUser.setModel(listModel);
			}
		});
		
		try {
                    while(true){
                        new Authentication(listener.accept()).start();
                    }
		} finally {
                    listener.close();
		}
		
		
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaLog;
    private javax.swing.JButton btnStopServer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> listUser;
    // End of variables declaration//GEN-END:variables
    
    private static class Authentication extends Thread {
    
        private final Socket socket;
        private int numOrder;
        
        public Authentication(Socket _socket){
            this.socket = _socket;
        }
        public void run(){
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                while(true) {
                    String input = in.readLine();
                    MessageHandler message = new MessageHandler(input);
                    if(message.isMessage()){
                        MessageHandler responseMessage = message.handler();
                        if(responseMessage.isMessage() && 
                           responseMessage.getHeader().equals(ConstantValue.SERVER_LOGOUT_SUCCESS))
                        {
                            String username = responseMessage.getTo();
                            listModel.remove(numOrder);
                            server.setAreaLog("User name " + username + " has logged out.");
                            clients.remove(username);
                            socket.close();
                            announNewOuter(responseMessage.getContent());
                        }
                        out.println(responseMessage.toJSON());
                        if(responseMessage.isMessage() && 
                           responseMessage.getHeader().equals(ConstantValue.SERVER_WELCOME))
                        {
                            String username = responseMessage.getTo();
                            listModel.addElement(username + "(online)");
                            numOrder = listModel.getSize() - 1;
                            server.setAreaLog("User name " + username + " has logged in.");
                            announNewComer(username);
                            clients.put(username, this);
                        }
                        
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(OneTwoThreeServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(OneTwoThreeServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        private void announNewComer(String username){
            Iterator setClients = clients.entrySet().iterator();
            MessageHandler message;
            while (setClients.hasNext()) {
                Map.Entry pair = (Map.Entry)setClients.next();
                StringMap<String> responseContent = new StringMap<>();
                responseContent.put(username, "online");
                System.out.println(pair.getKey().toString());
                message = new MessageHandler(SERVER_NEWCOMER, responseContent, "SERVER", pair.getKey().toString());
                setClients.remove(); // avoids a ConcurrentModificationException
            }
            
        }
        
        private void announNewOuter(StringMap<String> responseContent){
            Iterator setClients = clients.entrySet().iterator();
            MessageHandler message;
            while (setClients.hasNext()) {
                Map.Entry pair = (Map.Entry)setClients.next();
                System.out.println(pair.getKey().toString());
                message = new MessageHandler(SERVER_NEWOUTER, responseContent, "SERVER", pair.getKey().toString());
                setClients.remove(); // avoids a ConcurrentModificationException
            }
            
        }
        
    }

}


