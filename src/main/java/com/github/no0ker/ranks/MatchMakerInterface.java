package com.github.no0ker.ranks;

public interface MatchMakerInterface {
    MatchMakerInterface addUser(String userId, Byte rank);
    MatchMakerInterface addUser(String userId, Byte rank, Long enterTime);
    MatchMakerInterface setObserver(MatchMakerObserverInterface matchMakerObserver);
}
