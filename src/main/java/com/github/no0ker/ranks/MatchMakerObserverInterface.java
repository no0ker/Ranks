package com.github.no0ker.ranks;

import java.util.Set;

public interface MatchMakerObserverInterface {
    void addTeam(Team team);
    Set<Team> getAllTeams();
}
