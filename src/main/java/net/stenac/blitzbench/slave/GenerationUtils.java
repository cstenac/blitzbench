package net.stenac.blitzbench.slave;

import java.util.Random;

public class GenerationUtils {
    Random r = new Random();
    
    public String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int x = r.nextInt(125-48);
            sb.append((char)(48+x));
        }
        return sb.toString();
    }
}
