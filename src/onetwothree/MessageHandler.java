/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onetwothree;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import static onetwothree.ConstantValue.DEFAULT_TO;



/**
 *
 * @author BITVN12
 */
public class MessageHandler {
	@SerializedName("header")
	private String header;
	@SerializedName("content")
	private String content;
	@SerializedName("from")
	private String from;
	@SerializedName("to")
	private String to;
	
	public MessageHandler(String jsonMessage){
		Gson gson = new Gson();
		MessageHandler mess = gson.fromJson(jsonMessage, MessageHandler.class);

		this.header  = mess.getHeader();
		this.content = mess.getContent();
		this.from    = mess.getFrom();
		this.to      = mess.getTo();
		
		
	}
	
	public MessageHandler(String header, String content, String from, String to){
		this.header  = header;
		this.content = content;
		this.from    = from;
		this.to      = to;
	}
	
	public MessageHandler(String header, String content, String from){
		this.header  = header;
		this.content = content;
		this.from    = from;
		this.to      = DEFAULT_TO;
	}
	
	public boolean isMessage(){
		if(this.header == null || this.content == null || this.from == null){
			return false;
		}
		if (this.to == null){
			this.to = DEFAULT_TO;
		}
		return true;
	}
	public boolean isMessage(MessageHandler messageHandler){
		if(messageHandler.header == null || messageHandler.content == null || messageHandler.from == null){
			return false;
		}
		if (messageHandler.to == null){
			messageHandler.to = DEFAULT_TO;
		}
		return true;
	}
	
	public String toJSON() throws Exception{
		if(!isMessage()){
			throw new Exception("Message is init wrong way!");
		}
		Gson gson = new Gson();
		return gson.toJson(new MessageHandler(this.header, this.content, this.from, this.to));
	}
	
	public String toJSON(MessageHandler messageHandler) throws Exception{
		if(!isMessage(messageHandler)){
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
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
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
	
	
	
}
