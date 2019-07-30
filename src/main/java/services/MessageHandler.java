package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.Message;
import model.MessageType;
import model.Peer;
import model.Query;

public class MessageHandler {

    private Gson gson;

    public MessageHandler() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder.setLenient().create();
    }


    public Message parseMessage(String jsonString) {
        try {
            Message message = gson.fromJson(jsonString, Message.class);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Peer parsePeerMessage(String jsonString) {
        try {
            Peer peer = gson.fromJson(jsonString, Peer.class);
            return peer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Query parseQueryMessage(String jsonString) {
        try {
            Query query = gson.fromJson(jsonString, Query.class);
            return query;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String stringfy(Object object) {
        try {
            String jsonString = gson.toJson(object);
//            System.out.println("Peer to String: " + jsonString);
            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getValidJsonString(String message) {
        int index = 0;
        while (message.charAt(index) != '\0') {
            index++;
        }
        return message.substring(0, index);
    }
}
