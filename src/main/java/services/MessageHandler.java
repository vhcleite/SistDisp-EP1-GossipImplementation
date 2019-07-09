package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.Peer;

public class MessageHandler {

    private Gson gson;

    public MessageHandler() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder.setLenient().create();
    }

    public Peer parseString(String jsonString) {
        try {
//            System.out.println("String to Peer: " + jsonString);
            Peer peer = gson.fromJson(jsonString, Peer.class);
            return peer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String stringfyPeer(Peer peer) {
        try {
            String jsonString = gson.toJson(peer);
//            System.out.println("Peer to String: " + jsonString);
            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
