package org.lab5;

public class Main {
    public static void main(String[] args) {
        Service service = new Service();
        Randomizer randomizer = new Randomizer();
        for (int i = 0; i < 10; i++) {
            service.addToQueue(new Request(randomizer.getExpY(Randomizer.SERVICE_INTERVAL_M), "Клиент№" + (i + 1)));
        }



    }
}
