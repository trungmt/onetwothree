/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onetwothree.client;

import com.google.gson.internal.StringMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import onetwothreeMisc.ConstantValue;
import onetwothreeMisc.MessageHandler;
import static onetwothreeMisc.ConstantValue.*;

/**
 *
 * @author TJTran
 */
public class OneTwoThreeClient extends javax.swing.JFrame {

    private static String username;
    private static String password;
    private BufferedReader in;
    private PrintWriter out;
    protected Socket socket;
    private static DefaultListModel<String> listModel = new DefaultListModel<>();
    private static OneTwoThreeClient client;
    private static Thread thread;
    private static Map<String, Integer> clients
            = new HashMap<>();
    private static String currentEnemy;
    private static int currentStatus;
    private static ClientGameBoard gameBoard;
    private static String currentChoice = null;
    /**
     * Creates new form OneTwoThreeClient
     */
    public OneTwoThreeClient() {
        initComponents();
    }
    
    public void setCurrentChoice(String choice){
        this.currentChoice = choice;
    }
    public void connectToServer() throws IOException {
        String serverAddress = JOptionPane.showInputDialog(this,
                "Enter IP Address of the Server:",
                "Welcome to the Capitalization Program",
                JOptionPane.QUESTION_MESSAGE);

        socket = new Socket(serverAddress, 8901);
        System.out.println(socket.toString());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }
    
    public void handleInputStream(){
        try {
            while(true){
                String response;
                response = in.readLine();
                System.out.println(response);
                //            System.out.println(response);
                MessageHandler messResponse = new MessageHandler(response);
                StringMap<String> responseContent = messResponse.getContent();
                if (messResponse.isMessage()
                        && messResponse.getHeader().equals(ConstantValue.CLIENT_CONNECT)) {
                    //Login Error
                    lbError.setText(responseContent.get("response_content"));

                }
                if (messResponse.isMessage()
                        && messResponse.getHeader().equals(ConstantValue.SERVER_WELCOME)) {
                    Set<Map.Entry<String, String>> userList = responseContent.entrySet();
                    for (Map.Entry<String, String> item : userList) {
                        if (!item.getKey().equals("response")) {
                            listModel.addElement(item.getKey() + "(" + item.getValue() + ")");
                        }

                    }
                    client.jLayeredPane1.setVisible(false);
                    client.jLayeredPane2.setVisible(true);
                }
                if (messResponse.isMessage()
                        && messResponse.getHeader().equals(ConstantValue.SERVER_NEWCOMER)) {
                    Set<Map.Entry<String, String>> userList = responseContent.entrySet();
                    for (Map.Entry<String, String> item : userList) {
                        System.out.println(username);
                        System.out.println(item.getKey());
                        if (!item.getKey().equals("response") && !item.getKey().equals(username)) {
                            listModel.addElement(item.getKey() + "(" + item.getValue() + ")");
                        }

                    }
                }

                if (messResponse.isMessage()
                        && messResponse.getHeader().equals(ConstantValue.SERVER_NEWOUTER)) {
                    Set<Map.Entry<String, String>> userList = responseContent.entrySet();
                    listModel.removeAllElements();
                    for (Map.Entry<String, String> item : userList) {
                        System.out.println(item.getKey());
                        System.out.println(username);
                        System.out.println(item.getKey().equals(username));
                        if (!item.getKey().equals("response") && !item.getKey().equals(username)) {
                            listModel.addElement(item.getKey() + "(" + item.getValue() + ")");
                        }
                        
                    }
                }
                
                if (messResponse.isMessage()
                        && messResponse.getHeader().equals(ConstantValue.SERVER_SIGNUP_SUCCESS)) {
                    Set<Map.Entry<String, String>> userList = responseContent.entrySet();
                    for (Map.Entry<String, String> item : userList) {
                        lbError.setText(responseContent.get("response_content"));
                        client.labelSignup.setVisible(false);
                        client.btnSignUp.setVisible(false);
                        client.btnBack.setVisible(false);
                        client.textRetypePassword.setVisible(false);

                        client.btnLogin.setVisible(true);
                        client.btnOpenSignup.setVisible(true);
                    }
                }
                
                if (messResponse.isMessage()
                        && messResponse.getHeader().equals(ConstantValue.SERVER_CONNECT_WAR_SUCCESS)) {
                    Set<Map.Entry<String, String>> userList = responseContent.entrySet();
                    for (Map.Entry<String, String> item : userList) {
                        lbError.setText(responseContent.get("response_content"));
                        client.labelSignup.setVisible(false);
                        client.btnSignUp.setVisible(false);
                        client.btnBack.setVisible(false);
                        client.textRetypePassword.setVisible(false);

                        client.btnLogin.setVisible(true);
                        client.btnOpenSignup.setVisible(true);
                    }
                }
                
                if (messResponse.isMessage()
                        && messResponse.getHeader().equals(ConstantValue.PEER_CONNECT_WAR)) {
                    String otherUsername = (String)messResponse.getContent().get("otherUsername");
                    String mess = "User with name " + otherUsername + " wants to play with you. Accept?";
                    String title = "Game play confirm";
                    int reply = JOptionPane.showConfirmDialog(null, mess, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        StringMap<String> content = new StringMap<>();
                        content.put("response_message", "Accept");
                        MessageHandler messAccept = new MessageHandler(PEER_CONNECT_WAR_SUCCESS, content, username, otherUsername);
                        out.println(messAccept.toJSON());
                        gameBoard = new ClientGameBoard(this);
                        gameBoard.setTitle("Playing with " + otherUsername);
                        gameBoard.setVisible(true);
                    }
                    else {
                        StringMap<String> content = new StringMap<>();
                        content.put("response_message", "Decline");
                        MessageHandler messAccept = new MessageHandler(PEER_CONNECT_WAR_FAILED, content, username, otherUsername);
                    }
                }
                
                if (messResponse.isMessage()
                        && messResponse.getHeader().equals(ConstantValue.PEER_CONNECT_WAR_SUCCESS)) {
                    currentEnemy = (String)messResponse.getFrom();
                    gameBoard = new ClientGameBoard(this);
                    gameBoard.setTitle("Playing with " + currentEnemy);
                    gameBoard.setVisible(true);
                }
                
                if (messResponse.isMessage()
                        && messResponse.getHeader().equals(ConstantValue.PEER_GAME_SHOW_CHOICE)) {
                    StringMap<String> choice = new StringMap<>();
                    
                    if(currentChoice != null){
                        choice.put("choice", currentChoice);
                        MessageHandler choiceMessage = new MessageHandler(PEER_GAME_SHOW_CHOICE, choice, username, "SERVER");
                        out.println(choiceMessage.toJSON());
                        System.out.println(choiceMessage.toJSON());
                    } 
                    
                }
                
                if (messResponse.isMessage()
                        && messResponse.getHeader().equals(ConstantValue.PEER_GAME_COUNT_DOWN)) {
                    String countdown = (String)messResponse.getContent().get("count_down");
                    gameBoard.setCountDown(countdown);
                }
                
                if (messResponse.isMessage()
                        && messResponse.getHeader().equals(ConstantValue.PEER_GAME_SHOW_RESULT)) {
                    String result = (String)messResponse.getContent().get("result");
                    if(result.equals(username)){
                        gameBoard.setWinner("Bạn");
                    } else {
                        gameBoard.setWinner(result);
                    }
                }
                
            }
            
        } catch (IOException ex) {
            Logger.getLogger(OneTwoThreeClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(OneTwoThreeClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String passwordCrypt(String password){
        StringBuffer passwordCrypt = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            
            byte byteData[] = md.digest();
            
            //convert the byte to hex format method 1
            
            for (int i = 0; i < byteData.length; i++) {
                passwordCrypt.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            
        } catch (NoSuchAlgorithmException ex) {
        }
        return passwordCrypt.toString();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        textUsername = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        textPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        lbError = new javax.swing.JLabel();
        btnSignUp = new javax.swing.JButton();
        labelSignup = new javax.swing.JLabel();
        textRetypePassword = new javax.swing.JPasswordField();
        btnOpenSignup = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        btnLogOut = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listUser = new javax.swing.JList<>();

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(300, 622));
        setResizable(false);

        textUsername.setName("textUsername"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setLabelFor(textUsername);
        jLabel1.setText("Username:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setLabelFor(textPassword);
        jLabel2.setText("Password:");

        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        btnSignUp.setText("Sign Up!");
        btnSignUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSignUpActionPerformed(evt);
            }
        });

        labelSignup.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labelSignup.setLabelFor(textPassword);
        labelSignup.setText("Retype Password:");

        btnOpenSignup.setText("Sign Up");
        btnOpenSignup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenSignupActionPerformed(evt);
            }
        });

        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        jLayeredPane1.setLayer(textUsername, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(textPassword, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(btnLogin, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lbError, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(btnSignUp, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(labelSignup, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(textRetypePassword, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(btnOpenSignup, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(btnBack, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelSignup)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(textUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                            .addComponent(textRetypePassword)
                            .addComponent(textPassword)
                            .addComponent(btnOpenSignup, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSignUp, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap(112, Short.MAX_VALUE)
                .addComponent(lbError, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnOpenSignup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelSignup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textRetypePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSignUp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogin)
                .addGap(149, 149, 149))
        );

        btnLogOut.setText("Đăng xuất");
        btnLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogOutActionPerformed(evt);
            }
        });

        listUser.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách người chơi"));
        listUser.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        listUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listUserMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(listUser);

        jLayeredPane2.setLayer(btnLogOut, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLogOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                .addContainerGap())
        );
        jLayeredPane2Layout.setVerticalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLogOut, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLayeredPane2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLayeredPane2, javax.swing.GroupLayout.Alignment.TRAILING))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        client.lbError.setText("");
        try {
            username = textUsername.getText();
            password = passwordCrypt( new String(textPassword.getPassword()) );
            StringMap<String> content = new StringMap<>();
            content.put("username", username);
            content.put("password", password);
            MessageHandler messLogin = new MessageHandler(CLIENT_CONNECT, content, username, "SERVER");

            out.println(messLogin.toJSON());

        } catch (IOException ex) {
            Logger.getLogger(OneTwoThreeClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(OneTwoThreeClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnSignUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignUpActionPerformed
        try {
            username = textUsername.getText();
            password = passwordCrypt( new String(textPassword.getPassword()) );
            String retypePassword = passwordCrypt( new String(textRetypePassword.getPassword()) );
            
            if(!password.equals(retypePassword)) {
                lbError.setText("Mật khẩu và Mật khẩu (nhập lại) không trùng khớp.") ;
            } else {
                StringMap<String> content = new StringMap<>();
                content.put("username", username);
                content.put("password", password);
                MessageHandler messSignUp = new MessageHandler(CLIENT_SIGNUP, content, username, "SERVER");

                out.println(messSignUp.toJSON());
            }
        } catch (IOException ex) {
            Logger.getLogger(OneTwoThreeClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(OneTwoThreeClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSignUpActionPerformed

    private void btnLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogOutActionPerformed
        try {
            username = textUsername.getText();
            StringMap<String> content = new StringMap<>();
            content.put("username", username);
            content.put("password", password);
            MessageHandler messLogin = new MessageHandler("BYE", content, username, "SERVER");

            out.println(messLogin.toJSON());
            socket.close();

            client.listModel.removeAllElements();
            client.jLayeredPane1.setVisible(true);
            client.jLayeredPane2.setVisible(false);
            
        } catch (IOException ex) {
            Logger.getLogger(OneTwoThreeClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(OneTwoThreeClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLogOutActionPerformed

    private void btnOpenSignupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenSignupActionPerformed
        // TODO add your handling code here:
        client.labelSignup.setVisible(true);
        client.btnSignUp.setVisible(true);
        client.btnBack.setVisible(true);
        client.textRetypePassword.setVisible(true);
        
        client.btnLogin.setVisible(false);
        client.btnOpenSignup.setVisible(false);
        
        client.lbError.setText("");
    }//GEN-LAST:event_btnOpenSignupActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        client.labelSignup.setVisible(false);
        client.btnSignUp.setVisible(false);
        client.btnBack.setVisible(false);
        client.textRetypePassword.setVisible(false);
        
        client.btnLogin.setVisible(true);
        client.btnOpenSignup.setVisible(true);
    }//GEN-LAST:event_btnBackActionPerformed

    private void listUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listUserMouseClicked
        // TODO add your handling code here:
        JList list = (JList)evt.getSource();
        if (evt.getClickCount() == 2) {

            // Double-click detected
            //int index = list.locationToIndex(evt.getPoint());
            String enemy = listUser.getSelectedValue();
            if(enemy.contains("(online)")){
                try {
                    enemy=enemy.replaceAll("\\(online\\)", "");
                    StringMap<String> content = new StringMap<>();
                    content.put("username", enemy);
                    MessageHandler messWar = new MessageHandler(CLIENT_CONNECT_WAR, content, username, "SERVER");

                    out.println(messWar.toJSON());

                }  catch (Exception ex) {
                    Logger.getLogger(OneTwoThreeClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (enemy.contains("(in-game)")){
                JOptionPane.showMessageDialog(null, "Người chơi hiện đang trong trận.");
            }
        } 
    }//GEN-LAST:event_listUserMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(OneTwoThreeClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OneTwoThreeClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OneTwoThreeClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OneTwoThreeClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                client = new OneTwoThreeClient();
                client.setVisible(true);
                client.jLayeredPane2.setVisible(false);
                client.jLayeredPane1.setVisible(true);
                client.labelSignup.setVisible(false);
                client.btnSignUp.setVisible(false);
                client.btnBack.setVisible(false);
                client.textRetypePassword.setVisible(false);
                
                client.listUser.setModel(listModel);
                try {
                    client.connectToServer();
                    
                    thread = new Thread() {
                        public void run(){
                            client.handleInputStream();
                        }
                    };
                    thread.start();
                    
                } catch (IOException ex) {
                    Logger.getLogger(OneTwoThreeClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    public void sendMessage(MessageHandler message) {
        if (message.isMessage()) {
            try {
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                output.println(message.toJSON());
            } catch (Exception ex) {
                
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnLogOut;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnOpenSignup;
    private javax.swing.JButton btnSignUp;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelSignup;
    private javax.swing.JLabel lbError;
    private javax.swing.JList<String> listUser;
    private javax.swing.JPasswordField textPassword;
    private javax.swing.JPasswordField textRetypePassword;
    private javax.swing.JTextField textUsername;
    // End of variables declaration//GEN-END:variables
}
