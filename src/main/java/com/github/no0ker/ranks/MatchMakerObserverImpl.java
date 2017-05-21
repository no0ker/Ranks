package com.github.no0ker.ranks;

import java.util.*;

public class MatchMakerObserverImpl implements MatchMakerObserverInterface {
    private volatile Set<Team> allTeams;

    public MatchMakerObserverImpl() {
        allTeams = new TreeSet<>(new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {
                if (!o1.getStartTime().equals(o2.getStartTime())) {
                    return o1.getStartTime().compareTo(o2.getStartTime());
                } else {
                    int o1hashCode = o1.getUserIds().hashCode();
                    int o2hashCode = o2.getUserIds().hashCode();
                    return o1hashCode < o2hashCode ? -1 : (o1hashCode == o2hashCode ? 0 : 1);
                }
            }
        });
    }

    @Override
    public void addTeam(Team team) {
        allTeams.add(team);
    }

    @Override
    public Set<Team> getAllTeams() {
        return this.allTeams;
    }

}
