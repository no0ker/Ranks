package com.github.no0ker.ranks;

import org.junit.Test;

public class MatchMakerTest {
    @Test
    public void test01() throws InterruptedException {
        MathMakerImpl.getInstance()
                .addUser("A", (byte) 3)
                .addUser("B", (byte) 15)
        ;

        Thread.sleep(30000L);
    }
}