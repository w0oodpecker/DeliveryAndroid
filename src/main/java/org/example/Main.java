package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    private static final int STRINGLENGHT = 100; //Длина строки
    private static final int ROUNTAMOUNT = 1000; //Количество маршрутов
    private static final char LETTER = 'R'; //Искомая буква

    public static void main(String[] args) throws InterruptedException {

        List<Thread> threads = new ArrayList<>();
        //Запускаем процессы
        for (int i = 0; i < ROUNTAMOUNT; i++) {
            synchronized (threads) {
                threads.add(new Thread(() -> countFreq()));
                threads.get(i).start();
                threads.notifyAll();
            }
        }

        for (Thread thread : threads) { //Зависаем, ждём когда поток объект которого лежит в thread завершится
            thread.join();
        }

        Map<Integer, Integer> sortedSizeToFreq = sortDescending(); //Сортируем мапу по убыванию
        display(sortedSizeToFreq); //Вывод на консоль результата
    }

    public static void display(Map<Integer, Integer> sortedSizeToFreq) {
        Iterator<Map.Entry<Integer, Integer>> itr = sortedSizeToFreq.entrySet().iterator(); //Создаем итератор
        boolean count = false;
        while (itr.hasNext()) {
            Map.Entry<Integer, Integer> entry = itr.next();
            // get key
            Integer key = entry.getKey();
            // get value
            Integer value = entry.getValue();

            if (!count) {
                System.out.println("Самое частое количество повторений: " + key + " (встретилось " + value + " раз)");
                System.out.println("Другие размеры:");
                count = true;
            } else {
                System.out.println("- " + key + " (" + value + ") раз");
            }
        }

    }


    //Сортировка мапы по убыванию
    public static Map<Integer, Integer> sortDescending() {
        Map<Integer, Integer> sortedSizeToFreq = sizeToFreq.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new));
        return sortedSizeToFreq;
    }


    //Подсчет повторений и заполнение мапы
    public static void countFreq() {
        String string;
        string = generateRoute("RLRFR", STRINGLENGHT);
        int amountOfLetter = 0;

        for (int index = 0; index < string.length(); index++) {
            if (string.charAt(index) == LETTER) {
                amountOfLetter++;
            }
        }

        if (amountOfLetter > 0) {
            synchronized (sizeToFreq) {
                if (!sizeToFreq.containsKey(amountOfLetter)) {
                    sizeToFreq.put(amountOfLetter, 1);
                } else {
                    sizeToFreq.put(amountOfLetter, sizeToFreq.get(amountOfLetter).intValue() + 1);
                }
                sizeToFreq.notifyAll();
            }
        }
    }


    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

}