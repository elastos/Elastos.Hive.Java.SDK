package org.elastos.hive.util;

import org.elastos.hive.utils.LogUtil;

import java.util.concurrent.CompletableFuture;

public class TestUtils {
    public static void waitFinish(CompletableFuture future){
        while(!future.isDone()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
