package org.lab5;

public class Main {
    public static void main(String[] args) {
        Service service = new Service();
        Randomizer randomizer = new Randomizer();
        for (int i = 0; i < 100; i++) {
            double normalY;
            if (i == 0) {
                normalY = 0;
            } else {
                normalY = randomizer.getNormalY(Randomizer.INTERVAL_M, Randomizer.INTERVAL_CKO);
            }
            service.addToQueue(new Request(
                            randomizer.getExpY(Randomizer.SERVICE_INTERVAL_M),
                            randomizer.getExpY(Randomizer.FAILURE_INTERVAL_M),
                            "Клиент№" + (i + 1)),
                    normalY);
        }

        service.printQueue();
        double v = service.serviceRemaining();
        System.out.printf("Обслужены остальные клиенты. Потребовалось %4.2f минут\n", v);

        System.out.printf("Среднее время простоя %4.2f\n", service.getAverageServiceWaitTime());
        System.out.printf("Среднее время ожидания %4.2f\n", service.getAverageRequestWaitTime());

    }
}
