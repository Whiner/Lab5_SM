package org.lab5;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class Service {
    private ArrayDeque<Request> queue = new ArrayDeque<>();
    private final int channelCount = 6;
    private Randomizer randomizer = new Randomizer();
    private Map<Integer, Request> channelOccupancy = new HashMap<>(); // номер канала, освободится через

    public void addToQueue(Request request) {
        System.out.println("-------------------------");
        boolean queueAndServiceEmpty = isQueueAndServiceEmpty();
        double passedTime = 0;
        if (!queueAndServiceEmpty) {
            passedTime = randomizer.getNormalY(Randomizer.INTERVAL_M, Randomizer.INTERVAL_CKO);
            System.out.printf("Прошло %4.2f минут\n", passedTime);
            System.out.println("-------------------------");
            service(passedTime);
            System.out.println("-------------------------");
        }
        System.out.println("Размер очереди = " + queue.size());
        System.out.println("-------------------------");
        System.out.println("Пришел " + request.getName());
        int channelNumber = selectChannelNumber();
        request.setChannelNumber(channelNumber);
        System.out.println("Выбран " + channelNumber + "-й канал");
        request.setWaitTime(randomizer.getExpY(Randomizer.WAIT_INTERVAL_M));

        if(queueAndServiceEmpty){
            channelOccupancy.put(request.getChannelNumber(), request);
        } else {
            queue.add(request);
        }
        System.out.println("-------------------------");
    }

    private int selectChannelNumber() {
        int channelNumber = 1;
        Request request = channelOccupancy.get(channelNumber);
        if(request == null) {
            return channelNumber;
        }
        double minChannelReleaseTime = request.getServiceTime();
        for (Map.Entry<Integer, Request> entry : channelOccupancy.entrySet()) {
            if(entry.getValue() == null) {
                return entry.getKey();
            }
            if (minChannelReleaseTime > entry.getValue().getServiceTime()) {
                channelNumber = entry.getKey();
                minChannelReleaseTime = entry.getValue().getServiceTime();
            }
        }
        return channelNumber;
    }

    public double serviceRemaining() {
        double minutes = 0.0;
        double intervals = 10;
        while (!isQueueAndServiceEmpty()) {
            service(intervals);
            minutes += intervals;
        }
        return minutes;
    }


    private void service(double minutes) {
        for (Map.Entry<Integer, Request> entry : channelOccupancy.entrySet()) {
            System.out.println("+ Канал №" + entry.getKey() + " :");

            double leftServiceTime;
            double leftWaitTime;
            double leftPassedTime = minutes;
            do {
                Request value = entry.getValue();
                /*if(value.getName() == null){
                            System.out.println("++ Пусто");
                        }*/
                if(value == null) {
                    value = searchNextRequest(entry.getKey());
                    if (value == null) {
                        System.out.println("++ Свободно");
                        break; // простой
                    } else {
                        entry.setValue(value);
                        queue.remove(value);
                    }
                }

                leftWaitTime = value.getWaitTime() - leftPassedTime;


                if (leftWaitTime > 0) {
                    value.setWaitTime(leftWaitTime);     //не хватило времени
                    System.out.printf("++ Ожидание (Осталось %4.3f минут)\n", leftWaitTime);
                    break;
                } else { //хватило
                    leftPassedTime -= value.getWaitTime();
                    value.setWaitTime(0);

                    leftServiceTime = value.getServiceTime() - leftPassedTime;

                    if (leftServiceTime > 0) {
                        value.setServiceTime(leftServiceTime);
                        System.out.printf("++ Обслуживание не завершено (Осталось %4.3f минут)\n", leftServiceTime);
                        break;
                    } else {
                        leftPassedTime -= value.getServiceTime();
                        if(value.getName() != null) {
                            System.out.println("++ " + value.getName() + " обслужен ");
                            value.setServiceTime(0);
                            entry.setValue(null);
                        }

                    }
                }
            } while (leftPassedTime > 0);
        }
    }


    private Request searchNextRequest(int channelNumber) {
        for (Request request : queue) {
            if (request.getChannelNumber() == channelNumber) {
                return request;
            }
        }
        return null;
    }

    private boolean isQueueAndServiceEmpty() {
        for (Request request : channelOccupancy.values()) {
            if (request != null && request.getServiceTime() > 0.0) {
                return false;
            }
        }
        return queue.isEmpty();
    }

    public Service() {
        for (int i = 0; i < channelCount; i++) {
            channelOccupancy.put(i + 1, null);
        }
    }
}
