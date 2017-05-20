package com.github.no0ker.ranks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MathMakerImpl implements MathMakerInterface {
    private static volatile MathMakerImpl instance;
    private static final Byte COUNT_OF_RANKS = 31;
    private static final Byte INTERVAL_OF_RANK_DELTA_INCREASING = 3;
    private static final Byte SIZE_OF_TEAM = 2;
    private static final Logger LOG = LogManager.getLogger(MathMakerImpl.class);

    private volatile List<List<String>> avialabilityList;
    private volatile Map<String, User> users;

    private MathMakerImpl() {
        users = new ConcurrentHashMap<>();
        avialabilityList = new ArrayList<>(COUNT_OF_RANKS);
        for (int i = 0; i < COUNT_OF_RANKS; i++) {
            avialabilityList.add(i, new LinkedList<String>());
        }
        new AvialableListReloader(users, avialabilityList).start();
    }

    public static MathMakerInterface getInstance() {
        MathMakerImpl localInstance = instance;
        if (localInstance == null) {
            synchronized (MathMakerImpl.class) {
                localInstance = instance;
                if (localInstance == null) {
                    localInstance = instance = new MathMakerImpl();
                }
            }
        }
        return localInstance;
    }

    @Override
    public MathMakerInterface addUser(String userId, Byte rank) {
        return addUser(userId, rank, TimeHelper.getCurrentSeconds());
    }


    @Override
    public MathMakerInterface addUser(String userId, Byte rank, Long enterTime) {
        User newUser = new User(userId, rank, (byte) 0, enterTime);
        users.put(userId, newUser);
        avialabilityList.get(newUser.getRank()).add(newUser.getUserId());
        LOG.debug(TimeHelper.getCurrentSeconds() + "s. - " + avialabilityList);
        return this;
    }

    private class User {
        private String userId;
        private Byte rank;
        private Byte rankDelta;
        private Long enterTime;

        public User(String userId, Byte rank, Byte rankDelta, Long enterTime) {
            this.userId = userId;
            this.rank = rank;
            this.rankDelta = rankDelta;
            this.enterTime = enterTime;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Byte getRank() {
            return rank;
        }

        public void setRank(Byte rank) {
            this.rank = rank;
        }

        public Byte getRankDelta() {
            return rankDelta;
        }

        public void setRankDelta(Byte rankDelta) {
            this.rankDelta = rankDelta;
        }

        public Long getEnterTime() {
            return enterTime;
        }

        public void setEnterTime(Long enterTime) {
            this.enterTime = enterTime;
        }
    }

    private class AvialableListReloader extends Thread {
        private final Logger LOG = LogManager.getLogger(AvialableListReloader.class);

        private Map<String, User> users;
        private List<List<String>> avialabilityList;

        public AvialableListReloader(Map<String, User> users, List<List<String>> avialabilityList) {
            this.users = users;
            this.avialabilityList = avialabilityList;
        }

        @Override
        public void run() {
            while (true) {
                refillAvialableList();
                while (findAndRemove()) {
                    refillAvialableList();
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                incrementRankDeltas();
            }
        }

        private boolean findAndRemove() {
            boolean needToReRun = false;
            Set<String> currentTeam = new HashSet<>(SIZE_OF_TEAM);
            for (List<String> nodeList : avialabilityList) {
                if (nodeList.size() >= MathMakerImpl.SIZE_OF_TEAM) {
                    needToReRun = true;
                    for (String userId : nodeList) {
                        currentTeam.add(userId);
                    }
                }
            }
            LOG.debug(currentTeam);
            for (String userId : currentTeam) {
                users.remove(userId);
            }
            return needToReRun;
        }

        private void refillAvialableList() {
            for (List<String> nodeList : avialabilityList) {
                nodeList.clear();
            }
            for (User user : users.values()) {
                int begin = user.getRank() - user.getRankDelta();
                begin = begin < 0 ? 0 : begin;
                int end = user.getRank() + user.getRankDelta() + 1;
                end = end > MathMakerImpl.COUNT_OF_RANKS ? MathMakerImpl.COUNT_OF_RANKS : end;
                for (int i = begin; i < end; i++) {
                    avialabilityList.get(i).add(user.getUserId());
                }
            }
            LOG.debug(TimeHelper.getCurrentSeconds() + "s. - " + avialabilityList);
        }

        private void incrementRankDeltas() {
            for (User user : users.values()) {
                if (needToIncreaseRankDelta(user)) {
                    user.setRankDelta((byte) (user.getRankDelta() + 1));
                }
            }
        }

        private boolean needToIncreaseRankDelta(User user) {
            if ((TimeHelper.getCurrentSeconds() - user.getEnterTime()) % INTERVAL_OF_RANK_DELTA_INCREASING == 0) {
                return true;
            } else {
                return false;
            }
        }
    }
}
