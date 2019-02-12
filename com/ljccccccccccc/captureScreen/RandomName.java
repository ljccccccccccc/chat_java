package com.ljccccccccccc.captureScreen;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ljccccccccccc
 * 文件的重命名，随机产生
 *
 */
public class RandomName {
    public static void main(String[] args) {
        Date dt= new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat();
        String fileName= sdf.format(dt);
        System.out.println(fileName);
    }

}
