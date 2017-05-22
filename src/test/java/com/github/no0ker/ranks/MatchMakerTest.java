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
    private static final Gson GSON = new Gson();

    @Test
    public void test01() throws InterruptedException {
        MatchMakerObserverImpl matchMakerObserver = new MatchMakerObserverImpl();
        MatchMakerImpl.getInstance()
                .setObserver(matchMakerObserver)
                .addUser("A", (byte) 3)
                .addUser("B", (byte) 3)
                .addUser("C", (byte) 3)
                .addUser("D", (byte) 3)
                .addUser("E", (byte) 3)
                .addUser("F", (byte) 3)
                .addUser("G", (byte) 3)
                .addUser("H", (byte) 3)
        ;

        while (matchMakerObserver.getAllTeams().size() < 1) {
        }
        Iterator<Team> iterator = matchMakerObserver.getAllTeams().iterator();
        assertEquals(GSON.toJson(new String[]{"A","B","C","D","E","F","G","H"}), new Gson().toJson(iterator.next().getUserIds()));
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test02() throws InterruptedException {
        MatchMakerObserverImpl matchMakerObserver = new MatchMakerObserverImpl();
        MatchMakerImpl.getInstance()
                .setObserver(matchMakerObserver)
                .addUser("A", (byte) 3)
                .addUser("B", (byte) 3)
                .addUser("C", (byte) 3)
                .addUser("D", (byte) 3)
                .addUser("E", (byte) 3)
                .addUser("F", (byte) 3)
                .addUser("G", (byte) 3)
                .addUser("H", (byte) 3)
                .addUser("A1", (byte) 4)
                .addUser("B1", (byte) 4)
                .addUser("C1", (byte) 4)
                .addUser("D1", (byte) 4)
                .addUser("E1", (byte) 4)
                .addUser("F1", (byte) 4)
                .addUser("G1", (byte) 4)
                .addUser("H1", (byte) 4)
        ;

        while (matchMakerObserver.getAllTeams().size() < 2) {
        }
        Iterator<Team> iterator = matchMakerObserver.getAllTeams().iterator();
        assertEquals(GSON.toJson(new String[]{"A","B","C","D","E","F","G","H"}), new Gson().toJson(iterator.next().getUserIds()));
        assertEquals(GSON.toJson(new String[]{"A1","B1","C1","D1","E1","F1","G1","H1"}), new Gson().toJson(iterator.next().getUserIds()));
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test03() throws InterruptedException {
        MatchMakerObserverImpl matchMakerObserver = new MatchMakerObserverImpl();
        MatchMakerImpl.getInstance()
                .setObserver(matchMakerObserver)
                .addUser("A", (byte) 3)
                .addUser("B", (byte) 3)
                .addUser("C", (byte) 3)
                .addUser("D", (byte) 3)
                .addUser("E", (byte) 10)
                .addUser("F", (byte) 10)
                .addUser("G", (byte) 10)
                .addUser("H", (byte) 10)
        ;

        while (matchMakerObserver.getAllTeams().size() < 1) {
        }
        Iterator<Team> iterator = matchMakerObserver.getAllTeams().iterator();
        assertEquals(GSON.toJson(new String[]{"A","B","C","D","E","F","G","H"}), new Gson().toJson(iterator.next().getUserIds()));
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test04() throws InterruptedException {
        MatchMakerObserverImpl matchMakerObserver = new MatchMakerObserverImpl();
        MatchMakerImpl.getInstance()
                .setObserver(matchMakerObserver)
                .addUser("A", (byte) 1)
                .addUser("B", (byte) 2)
                .addUser("C", (byte) 3)
                .addUser("D", (byte) 4)
                .addUser("E", (byte) 5)
                .addUser("F", (byte) 6)
                .addUser("G", (byte) 7)
                .addUser("H", (byte) 8)
                .addUser("Z", (byte) 29)
        ;

        while (matchMakerObserver.getAllTeams().size() < 1) {
        }
        Iterator<Team> iterator = matchMakerObserver.getAllTeams().iterator();
        assertEquals(GSON.toJson(new String[]{"A","B","C","D","E","F","G","H"}), new Gson().toJson(iterator.next().getUserIds()));
        assertFalse(iterator.hasNext());
    }
}