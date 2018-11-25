package org.lab5;

import java.util.*;

public class Service {
    private List<Request> queue = new ArrayList<>();
    private final int channelCount = 6;
    private Map<Integer, Request> channelOccupancy = new HashMap<>(); // номер канала, текущая заявка там
    private double currentServiceWaitTime = 0;
    private int totalRequestCount = 0;
    private double currentRequestWaitTime = 0;

    public void addToQueue(Request request, double after) {
        totalRequestCount++;
        boolean queueAndServiceEmpty = isQueueAndServiceEmpty();
        System.out.println("-----------------------------------------------------------");
        if (after != 0) {
            System.out.printf("Прошло %4.2f минут\n", after);
            System.out.println("-------------------------");
            if (!queueAndServiceEmpty) {
                service(after);
                System.out.println("-------------------------");
                addWaitTimeToRequestsInQueue(after);
            }
            printQueue();
            System.out.println("-------------------------");
        }
        System.out.printf("+ Пришел %s. Время обслуживания: %4.2f. Время отказа: %4.2f\n",
                request.getName(),
                request.getServiceTime(),
                request.getLastFailureTime());

        if (queueAndServiceEmpty) {
            channelOccupancy.put(1, request);
        } else {
            queue.add(request);
        }
    }

    public double getAverageRequestWaitTime() {
        return currentRequestWaitTime / totalRequestCount;
    }

    private void addWaitTimeToRequestsInQueue(double time) {
        queue.forEach(request -> request.addCurrentWaitTime(time));
    }

    private double getMinServiceTime() {
        double min = 1e10;
        for (Map.Entry<Integer, Request> entry : channelOccupancy.entrySet()) {
            Request value = entry.getValue();
            if (value != null && value.getServiceTime() < min) {
                min = value.getServiceTime();
            }
        }
        return min;
    }

    public double serviceRemaining() {
        double minutes = 0.0;
        while (!isQueueAndServiceEmpty()) {
            double minServiceTime = getMinServiceTime();
            System.out.println("-------------------------");
            System.out.printf("Прошло %4.2f минут\n", minServiceTime);
            System.out.println("-------------------------");
            service(minServiceTime);
            System.out.println("-------------------------");
            minutes += minServiceTime;
        }
        return minutes;
    }

    private Request getRequestByMinFailureTime() {
        double min = 10E8;
        Request minFailureRequest = null;
        for (Request request : queue) {
            if (request.getLastFailureTime() < min) {
                minFailureRequest = request;
                min = request.getLastFailureTime();
            }
        }
        return minFailureRequest;
    }

    private void subtractFromFailureTime(double minutes) {
        for (Request request : queue) {
            request.setLastFailureTime(request.getLastFailureTime() - minutes);
        }
    }

    private void removeFailureRequests() {
        List<Request> deleteRequest = new ArrayList<>();
        queue.stream().filter(request -> request.getLastFailureTime() < 0).forEach(deleteRequest::add);
        if (deleteRequest.size() != 0) {
            System.out.println("-------------------------");
            deleteRequest.forEach(request -> {
                        System.out.printf("+++ %s ушел без обслуживания. Прождал %4.2f\n", request.getName(), request.getFailureTime());
                currentRequestWaitTime += request.getFailureTime();
                        queue.remove(request);
                    }
            );
        }
    }

    public double getAverageServiceWaitTime() {
        return currentServiceWaitTime / channelCount / totalRequestCount;
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
                    value = getRequestByMinFailureTime();
                    if (value == null) {
                        System.out.println("++ Свободен");
                        currentServiceWaitTime += leftPassedTime;
                        break; // простой
                    } else {
                        if (value.getLastFailureTime() + leftPassedTime < 0) {
                            if (entry.getKey().equals(entries.size())) {
                                value.addCurrentWaitTime(minutes - leftPassedTime);
                                System.out.printf("++ %s ушел без обслуживания. Прождал %4.2f\n", value.getName(), value.getCurrentWaitTime());
                                currentRequestWaitTime += value.getCurrentWaitTime();
                                queue.remove(value);
                            }
                            break;
                        } else {
                            value.addCurrentWaitTime(minutes - leftPassedTime);
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
                        System.out.println("++ " + value.getName() + " обслужен");
                        currentRequestWaitTime += value.getCurrentWaitTime();
                        value.setServiceTime(0);
                        entry.setValue(null);
                    }

                }

            } while (leftPassedTime > 0);
        }
        removeFailureRequests();
    }


    public void printQueue() {
        if (queue.isEmpty()) {
            System.out.println("Очередь пуста");
        } else {
            System.out.println("Очередь");
            for (Request request : queue) {
                System.out.printf("+ %s. Время обслуживания: %4.2f. Оставшееся время отказа: %4.2f\n",
                        request.getName(),
                        request.getServiceTime(),
                        request.getLastFailureTime());
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
