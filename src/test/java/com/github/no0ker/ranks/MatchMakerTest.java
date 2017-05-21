package com.github.no0ker.ranks;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MatchMakerTest {
    private static final Logger LOG = LogManager.getLogger(MatchMakerTest.class);

    @Test
    public void test01() throws InterruptedException {
        MatchMakerObserverImpl matchMakerObserver = new MatchMakerObserverImpl();
        MatchMakerImpl.getInstance()
                .setObserver(matchMakerObserver)
                .addUser("A", (byte) 3)
                .addUser("B", (byte) 10)
//                .addUser("C", (byte) 10)
//                .addUser("D", (byte) 10)
        ;

        while (matchMakerObserver.getAllTeams().size() < 2) {}
        Iterator<Team> iterator = matchMakerObserver.getAllTeams().iterator();
        assertEquals("[\"A\",\"B\"]", new Gson().toJson(iterator.next().getUserIds()));
        assertEquals("[\"C\",\"D\"]", new Gson().toJson(iterator.next().getUserIds()));
        assertFalse(iterator.hasNext());
    }
}