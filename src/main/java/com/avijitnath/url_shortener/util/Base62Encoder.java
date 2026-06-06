package com.avijitnath.url_shortener.util;

public class Base62Encoder {

    public static String encode(Long id){
        long rem;
        StringBuilder shortCode = new StringBuilder();
        while (id > 0){
            rem = id % 62;
            id = id / 62;
            if (rem <= 25){
                shortCode.insert(0, (char) (rem + 97));
            } else if (rem <=51) {
                shortCode.insert(0, (char) (rem + 39));
            }else{
                shortCode.insert(0, (char) (rem - 4));
            }
        }

        return shortCode.toString();
    }

}
