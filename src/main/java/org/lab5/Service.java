package org.lab5;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Service {
    private ArrayDeque<Request> queue = new ArrayDeque<>();
    private final int channelCount = 6;
    private Map<Integer, Request> channelOccupancy = new HashMap<>(); // номер канала, текущая заявка там

    public void addToQueue(Request request, double after) {
        boolean queueAndServiceEmpty = isQueueAndServiceEmpty();
        System.out.println("-----------------------------------------------------------");
        if (after != 0) {
            System.out.printf("Прошло %4.2f минут\n", after);
            System.out.println("-------------------------");
            if (!queueAndServiceEmpty) {
                service(after);
                System.out.println("-------------------------");
            }
            printQueue();
            System.out.println("-------------------------");
        }
        System.out.printf("+ Пришел %s. Время обслуживания: %4.2f. Время отказа: %4.2f\n",
                request.getName(),
                request.getServiceTime(),
                request.getFailureTime());

        if (queueAndServiceEmpty) {
            channelOccupancy.put(1, request);
        } else {
            queue.add(request);
        }
    }

    private Request selectRequestFromQueue() {
        return null;
    }

    private int selectChannelNumber() {
        int channelNumber = 1;
        Request request = channelOccupancy.get(channelNumber);
        if (request == null) {
            return channelNumber;
        }
        double minChannelReleaseTime = request.getServiceTime();
        for (Map.Entry<Integer, Request> entry : channelOccupancy.entrySet()) {
            if (entry.getValue() == null) {
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
            System.out.println("-------------------------");
            System.out.println("Прошло " + intervals + " минут");
            System.out.println("-------------------------");
            service(intervals);
            System.out.println("-------------------------");
            minutes += intervals;
        }
        return minutes;
    }

    private Request getRequestByMinFailureTime() {
        double min = 10E8;
        Request minFailureRequest = null;
        for (Request request : queue) {
            if (request.getFailureTime() < min) {
                minFailureRequest = request;
                min = request.getFailureTime();
            }
        }
        return minFailureRequest;
    }

    private void subtractFromFailureTime(double minutes) {
        for (Request request : queue) {
            request.setFailureTime(request.getFailureTime() - minutes);
        }
    }

    private void removeFailureRequests() {
        for (Request request : queue) {
            if (request.getFailureTime() < 0) {
                System.out.println("+++ " + request.getName() + " ушел без обслуживания");
                queue.remove(request);
            }
        }
    }

    private void service(double minutes) {
        subtractFromFailureTime(minutes);
        Set<Map.Entry<Integer, Request>> entries = channelOccupancy.entrySet();
        for (Map.Entry<Integer, Request> entry : entries) {
            System.out.println("+ Канал №" + entry.getKey() + " :");

            double leftServiceTime;
            double leftPassedTime = minutes;
            do {
                Request value = entry.getValue();
                if (value == null) {
                    //value = searchNextRequest(entry.getKey());
                    value = getRequestByMinFailureTime();
                    if (value == null) {
                        System.out.println("++ Свободен");
                        break; // простой
                    } else {
                        if (value.getFailureTime() + leftPassedTime < 0) {
                            if (entry.getKey().equals(entries.size())) {
                                System.out.println("++ " + value.getName() + " ушел без обслуживания");
                                queue.remove(value);
                            }
                            break;
                        } else {
                            entry.setValue(value);
                            queue.remove(value);
                        }
                    }
                }

                leftServiceTime = value.getServiceTime() - leftPassedTime;

                if (leftServiceTime > 0) {
                    value.setServiceTime(leftServiceTime);
                    System.out.printf("++ %s Обслуживание не завершено (Осталось %4.3f минут)\n", value.getName(), leftServiceTime);
                    break;
                } else {
                    leftPassedTime -= value.getServiceTime();
                    if (value.getName() != null) {
                        System.out.println("++ " + value.getName() + " обслужен ");
                        value.setServiceTime(0);
                        entry.setValue(null);
                    }

                }

            } while (leftPassedTime > 0);
        }
        removeFailureRequests();
    }


    /*private Request searchNextRequest(int channelNumber) {
        for (Request request : queue) {
            if (request.getChannelNumber() == channelNumber) {
                return request;
            }
        }
        return null;
    }*/

    public void printQueue() {
        if (queue.isEmpty()) {
            System.out.println("Очередь пуста");
        } else {
            for (Request request : queue) {
                System.out.printf("+ %s. Время обслуживания: %4.2f. Время отказа: %4.2f\n",
                        request.getName(),
                        request.getServiceTime(),
                        request.getFailureTime());
            }
        }
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
