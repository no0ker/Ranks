package com.github.no0ker.ranks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MatchMakerImpl implements MatchMakerInterface {
    private static volatile MatchMakerImpl instance;
    private static final Byte COUNT_OF_RANKS = 30;
    private static final Byte INTERVAL_OF_RANK_DELTA_INCREASING = 5;
    private static final Byte SIZE_OF_TEAM = 8;
    private static final Logger LOG = LogManager.getLogger(MatchMakerImpl.class);
    private static final ReentrantReadWriteLock dataLock = new ReentrantReadWriteLock();

    private volatile List<List<String>> avialabilityList;
    private volatile Map<String, User> users;

    private MatchMakerObserverInterface matchMakerObserver;

    private MatchMakerImpl() {
        try {
            dataLock.writeLock().lock();
            users = new HashMap<>();
            avialabilityList = new ArrayList<>(COUNT_OF_RANKS);
            for (int i = 0; i < COUNT_OF_RANKS; i++) {
                avialabilityList.add(i, new LinkedList<String>());
            }
        } finally {
            dataLock.writeLock().unlock();
        }
        new AvialableListReloader(users, avialabilityList, dataLock).start();

    }

    public static MatchMakerInterface getInstance() {
        MatchMakerImpl localInstance = instance;
        if (localInstance == null) {
            synchronized (MatchMakerImpl.class) {
                localInstance = instance;
                if (localInstance == null) {
                    localInstance = instance = new MatchMakerImpl();
                }
            }
        }
        return localInstance;
    }

    @Override
    public MatchMakerInterface addUser(String userId, Byte rank) {
        return addUser(userId, rank, TimeHelper.getCurrentSeconds());
    }


    @Override
    public MatchMakerInterface addUser(String userId, Byte rank, Long enterTime) {
        User newUser = new User(userId, rank, (byte) 0, enterTime);
        try {
            dataLock.writeLock().lock();
            users.put(userId, newUser);
            avialabilityList.get(newUser.getRank()).add(newUser.getUserId());
            LOG.debug(TimeHelper.getCurrentSeconds() + "s. - " + avialabilityList);
        } finally {
            dataLock.writeLock().unlock();
        }
        return this;
    }

    @Override
    public MatchMakerInterface setObserver(MatchMakerObserverInterface matchMakerObserver) {
        this.matchMakerObserver = matchMakerObserver;
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

        private ReentrantReadWriteLock dataLock;
        private Map<String, User> users;
        private List<List<String>> avialabilityList;

        public AvialableListReloader(Map<String, User> users, List<List<String>> avialabilityList, ReentrantReadWriteLock dataLock) {
            this.users = users;
            this.avialabilityList = avialabilityList;
            this.dataLock = dataLock;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    dataLock.writeLock().lock();
                    refillAvialableList();
                    while (findAndRemove()) {
                        refillAvialableList();
                    }
                } finally {
                    dataLock.writeLock().unlock();
                }

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    LOG.error("there is an InterruptedException", e);
                    break;
                }

                try {
                    dataLock.writeLock().lock();
                    incrementRankDeltas();
                } finally {
                    dataLock.writeLock().unlock();
                }
            }
        }

        private boolean findAndRemove() {
            boolean needToReRun = false;
            Set<String> currentTeam = new HashSet<>(SIZE_OF_TEAM);
            for (List<String> nodeList : avialabilityList) {
                if (nodeList.size() >= MatchMakerImpl.SIZE_OF_TEAM) {
                    needToReRun = true;
                    for (String userId : nodeList) {
                        currentTeam.add(userId);
                    }
                    break;
                }
            }
            if (!currentTeam.isEmpty()) {
                Team newTeam = new Team(currentTeam);
                LOG.debug("add new team: " + newTeam);
                matchMakerObserver.addTeam(newTeam);
                for (String userId : currentTeam) {
                    users.remove(userId);
                }
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
                end = end > MatchMakerImpl.COUNT_OF_RANKS ? MatchMakerImpl.COUNT_OF_RANKS : end;
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
