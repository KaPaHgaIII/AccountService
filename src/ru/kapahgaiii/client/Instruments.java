package ru.kapahgaiii.client;

import java.util.Set;
import java.util.TreeSet;

public class Instruments {

    /*
    * метод принимает строку вида "1-3,5,7,9-15,8" + возможно всякие
    * проверяет на неправильные символы и в случае чего бросает NumberFormatException
    * удаляет все лишние запятые и дефисы с конца строки
    * удаляет незаконченные диапазоны 1,2-,6
    * добавляет в конец запятую как символ окончания строки
    *
    * создаёт сет чисел
    * посимвольно анализирует строку:
    * формирует первое число start (start*10+новая цифра)
    * если встречает запятую, то записывает полученное число в сет и обнуляет всё
    * если встречает дефис, ставит флаг и начинает набирать второе число
    * когда во время набора числа встретил запятую, записал циклом for все числа в сет и сбросил все флаги
    *
    * флаг shouldBeInt нужен чтобы после запятой или дефиса искать только цифры
    *
    * после того, как кончилась строка сет преобразуется в массив, который и есть результат нашего метода
    * */

    static int[] stringToArray(String s) throws NumberFormatException {
        if (s.matches(".*[^0-9,-]+.*")) {
            throw new NumberFormatException();
        }

        s = s.replaceAll("[,-]*$", "");
        s = s.replaceAll("-,", ",");
        s += ",";

        Set<Integer> set = new TreeSet<Integer>();

        Integer start = 0, end = 0;
        Boolean range = false;
        Boolean shouldBeInt = true;
        for (int i = 0; i < s.length(); i++) {
            char a = s.charAt(i);
            if (shouldBeInt && (a == '-' || a == ',')) {
                continue;
            }
            if (range) {
                if (a == ',') {
                    for (int j = start; j <= end; j++) {
                        set.add(j);
                    }
                    start = 0;
                    end = 0;
                    range = false;
                    shouldBeInt = true;
                } else {
                    shouldBeInt = false;
                    end = end * 10 + Character.getNumericValue(a);
                }
            } else {
                if (a == ',') {
                    set.add(start);
                    start = 0;
                    shouldBeInt = true;
                } else if (a == '-') {
                    range = true;
                    shouldBeInt = true;
                } else {
                    shouldBeInt = false;
                    start = start * 10 + Character.getNumericValue(a);
                }
            }
        }

        int[] result = new int[set.size()];
        int i = 0;
        for (Integer a : set) {
            result[i] = a;
            i++;
        }

        return result;
    }
}
