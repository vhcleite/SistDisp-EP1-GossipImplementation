package services;

import java.util.Random;

public class LotteryService {

    /*
     * Retorna um número aleatório
     * 
     * @param numero máximo para ser sorteado
     * 
     * @return número selecionado
     */
    public static int getRandomInt(int maximumValue) {
        Random random = new Random();
        return random.nextInt(maximumValue);
    }
}
