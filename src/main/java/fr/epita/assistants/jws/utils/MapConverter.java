package fr.epita.assistants.jws.utils;

import java.util.ArrayList;
import java.util.List;

public class MapConverter {

    //private String map;

    public String rleToFull(String s) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < s.length(); i += 1) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                for (int j = 0; j < Character.getNumericValue(c); j++)
                    res.append(s.charAt(i + 1));
            }
        }
        return res.toString();
    }

    public String fullToRle(String s) {
        if (s.isEmpty())
            return s;

        StringBuilder res = new StringBuilder();
        char prev = '\0';
        int count = 1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != prev || count == 9) {
                if (prev != '\0') {
                    res.append(String.valueOf(count));
                    res.append(prev);
                }
                count = 1;
                prev = c;
            }
            else
                count++;
        }
        res.append(String.valueOf(count));
        res.append(prev);
        return res.toString();
        /*
        StringBuilder res = new StringBuilder();
        int k = 0;
        for (int i = 0; i < s.length(); i = k) {
            char c = s.charAt(i);
            int subIndex = 0;
            while (subIndex < 9 && (i + subIndex) < s.length() && s.charAt(i + subIndex) == c) {
                subIndex++;
            }
            res.append(String.valueOf(subIndex + 1));
            res.append(c);
            k = subIndex + 1;
        }
        return res.toString();*/
    }

    public List<String> toFullMap (List<String> map) {
        List<String> res = new ArrayList<>();
        for (String s: map) {
            res.add(rleToFull(s));
        }
        return res;
    }

    public List<String> toRle (List<String> map) {
        List<String> res = new ArrayList<>();
        for (String s: map) {
            res.add(fullToRle(s));
        }

        return res;
    }
}
