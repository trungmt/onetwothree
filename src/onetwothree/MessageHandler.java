/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onetwothree;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.StringMap;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import static onetwothree.ConstantValue.CLIENT_CONNECT;
import static onetwothree.ConstantValue.DEFAULT_TO;

/**
 *
 * @author BITVN12
 */
public class MessageHandler {

    @SerializedName("header")
    private String header;
    @SerializedName("content")
    private StringMap content;
    @SerializedName("from")
    private String from;
    @SerializedName("to")
    private String to;

    public MessageHandler(String jsonMessage) {
        Gson gson = new Gson();
        MessageHandler mess = gson.fromJson(jsonMessage, MessageHandler.class);

        this.header = mess.getHeader();
        this.content = mess.getContent();
        this.from = mess.getFrom();
        this.to = mess.getTo();

    }

    public MessageHandler(String header, StringMap content, String from, String to) {
        this.header = header;
        this.content = content;
        this.from = from;
        this.to = to;
    }

    public MessageHandler(String header, StringMap content, String from) {
        this.header = header;
        this.content = content;
        this.from = from;
        this.to = DEFAULT_TO;
    }

    public boolean isMessage() {
        if (this.header == null || this.content == null || this.from == null) {
            return false;
        }
        if (this.to == null) {
            this.to = DEFAULT_TO;
        }
        return true;
    }

    public boolean isMessage(MessageHandler messageHandler) {
        if (messageHandler.header == null || messageHandler.content == null || messageHandler.from == null) {
            return false;
        }
        if (messageHandler.to == null) {
            messageHandler.to = DEFAULT_TO;
        }
        return true;
    }

    public String toJSON() throws Exception {
        if (!isMessage()) {
            throw new Exception("Message is init wrong way!");
        }
        Gson gson = new Gson();
        return gson.toJson(new MessageHandler(this.header, this.content, this.from, this.to));
    }

    public String toJSON(MessageHandler messageHandler) throws Exception {
        if (!isMessage(messageHandler)) {
            throw new Exception("Message is init wrong way!");
        }
        Gson gson = new Gson();
        return gson.toJson(messageHandler);
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the content
     */
    public StringMap getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(StringMap content) {
        this.content = content;
    }

    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(String to) {
        this.to = to;
    }

    public MessageHandler handler() throws Exception {
        if (this.header.equals(CLIENT_CONNECT)) {
            return serverLoginHandler();
        }
        return invalid();

    }

    private MessageHandler serverLoginHandler() {
        try {
            String username = (String) content.get("username");
            String password = (String) content.get("password");
            System.out.println("u " + username + " p " + password);
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/onetwothree?useSSL=false", "root", "root");
            PreparedStatement stmt = conn.prepareStatement(
                    "select * from users WHERE username = ? AND password = ? AND status = 0");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rset = stmt.executeQuery();

            
            MessageHandler response;
            StringMap<String> responseContent = new StringMap<>();
            if (rset.next()) {
                responseContent.put("response", "OK");
                
                PreparedStatement getUserList = conn.prepareStatement(
                    "SELECT username, status.name as status "
                  + "FROM users "
                  + "INNER JOIN status ON users.status = status.id "
                  + "WHERE status > 0");
                ResultSet userList = getUserList.executeQuery();

                while (userList.next()) {
                    String name = userList.getString("username");
                    if(name.equals(username)){
                        continue;
                    }
                    String status = userList.getString("status");
                    
                    responseContent.put(name, status);
                }
                userList.close();
                getUserList.close();
                
                response = new MessageHandler("HELLO", responseContent, "SERVER", username);
                System.out.println(response.toJSON());
                return response;
            } else {
                responseContent.put("response", "ERROR");
                responseContent.put("response_content", "Tài khoản hoặc mật khẩu không đúng.");
                response = new MessageHandler("HELLO", responseContent, "SERVER", username);
                return response;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private MessageHandler invalid() throws Exception {
        MessageHandler response;
        StringMap<String> responseContent = new StringMap<>();
        responseContent.put("response", "Sai cú pháp");
        response = new MessageHandler("ERROR", responseContent, "SERVER", "");
        
        return response;
    }

}

class AuthMessage {

    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;

    public AuthMessage(StringMap content) {
//        Gson gson = new Gson();
//        AuthMessage mess = gson.fromJson(jsonMessage, AuthMessage.class);

        this.username = (String) content.get("username");
        this.password = (String) content.get("password");
    }

    public AuthMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean isMessage() {
        return !(this.username == null || this.password == null);
    }

    public boolean isMessage(AuthMessage authMessageHandler) {
        return !(authMessageHandler.getUsername() == null || authMessageHandler.getPassword() == null);
    }

    public String toJSON() throws Exception {
        if (!isMessage()) {
            throw new Exception("Message is init wrong way!");
        }
        Gson gson = new Gson();
        return gson.toJson(new AuthMessage(this.username, this.password));
    }

    public String toJSON(AuthMessage authMessageHandler) throws Exception {
        if (!isMessage(authMessageHandler)) {
            throw new Exception("Message is init wrong way!");
        }
        Gson gson = new Gson();
        return gson.toJson(authMessageHandler);
    }

    /**
     * @return the header
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param header the header to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the header
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param header the header to set
     */
    public void setPassWord(String password) {
        this.password = password;
    }

    // TODO: make a class and let AuthMessage extends it, content will be object of that class 
    // then content = new AuthMessage
}
