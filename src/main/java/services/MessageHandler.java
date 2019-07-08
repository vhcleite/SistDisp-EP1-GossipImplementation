package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Peer;

public class MessageHandler {

    private Gson gson;

    public MessageHandler() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder.create();
    }

    public Peer parseString(String jsonString){
        return gson.fromJson(jsonString, Peer.class);
    }

    public String stringfyPeer(Peer peer){
        return gson.toJson(peer);
    }
}
