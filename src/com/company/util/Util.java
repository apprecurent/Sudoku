package com.company.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Util {

    public static LinkedHashMap<Integer, Integer> sortByValue(Map<Integer, Integer> map) {

        // Create lists for keys and values
        List<Integer> keys = new ArrayList<>(map.keySet());
        List<Integer> values = new ArrayList<>(map.values());
        LinkedHashMap<Integer, Integer> sortedMap = new LinkedHashMap<>();

        // Sort the values
        selectionSort(values);

        // Loop through each value and key and check if the passed map with the key has the value as its value
        for (Integer value : values) {
            for (Integer key : keys) {
                if (map.get(key).equals(value)) {
                    // Prevents removal of duplicates
                    keys.remove(key);

                    // Put them into sorted map
                    sortedMap.put(key, value);
                    break;
                }
            }
        }

        return sortedMap;

    }

    public static <T> LinkedHashMap<T, List<Integer>> sortMapByValue(Map<T, List<Integer>> map) {
        List<T> keys = new ArrayList<>(map.keySet());
        List<List<Integer>> values = new ArrayList<>(map.values());
        LinkedHashMap<T, List<Integer>> sortedMap = new LinkedHashMap<>();

        // Sort the values
        selectionSortList(values);

        // Loop through each value and key and check if the passed map with the key has the value as its value
        for (List<Integer> value : values) {
            for (T key : keys) {
                if (map.get(key).equals(value)) {
                    // Prevents removal of duplicates
                    keys.remove(key);

                    // Put them into sorted map
                    sortedMap.put(key, value);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public static void selectionSort(List<Integer> list) {

        for (int i = 0; i < list.size(); i++) {
            int pos = i;
            for (int j = i; j < list.size(); j++) {
                if (list.get(j) < list.get(pos)) pos = j;
            }

            int min = list.get(pos);
            list.set(pos, list.get(i));
            list.set(i, min);

        }

    }

    private static void selectionSortList(List<List<Integer>> list) {
        for (int i = 0; i < list.size(); i++) {
            int pos = i;
            for (int j = i; j < list.size(); j++) {
                if (list.get(j).size() < list.get(pos).size()) pos = j;
            }
            List<Integer> min = list.get(pos);
            list.set(pos, list.get(i));
            list.set(i, min);
        }
    }
}
