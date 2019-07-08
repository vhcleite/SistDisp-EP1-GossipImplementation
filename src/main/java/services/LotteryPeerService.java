package services;

import java.util.Random;

public class LotteryPeerService {


    /*
    * Retorna o índice de um peer a ser selecionado numa lista aleatóriamente
    * @param numero de peers a serem sorteados
    * @return peer selecionado
    * */
    public static int chooseRandomPeerIndex(int peersNumber){
        Random random = new Random();
        int selectedPeer = random.nextInt(peersNumber);
        return selectedPeer;
    }
}
