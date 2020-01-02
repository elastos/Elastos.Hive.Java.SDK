package org.elastos.hive.util;

import org.elastos.hive.utils.LogUtil;

import java.util.concurrent.CompletableFuture;

public class TestUtils {
    private static boolean waitFinishFlag ;
    public static void waitFinish(CompletableFuture future){
        while(!future.isDone()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void waitFinish(){
        waitFinishFlag = true ;
        while(waitFinishFlag){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void changeFlag(){
        waitFinishFlag = !waitFinishFlag ;
    }
}
