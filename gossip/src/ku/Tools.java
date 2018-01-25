package ku;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Tools {
    private static long seed = 48;


    static int getRandomNumberInRange(int min, int max) {

        if (min > max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random(seed ++);
        int num = r.nextInt((max - min) + 1) + min;
        return num;
    }

    /**
     *
     * @param start inclusive
     * @param end inclusive
     * @param num how many to return
     * @return return random numbers
     */
    static List<Integer> shuffleNum(int start, int end, int num){
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = start; i <= end; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list.subList(0, num);
    }


}
