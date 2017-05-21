package com.github.no0ker.ranks;

import java.util.Set;
import java.util.TreeSet;

public class Team {
    private Long startTime;
    private Set<String> userIds;

    public Team(Set<String> userIds) {
        this.userIds = new TreeSet<String>(userIds);
        startTime = TimeHelper.getCurrentSeconds();
    }

    public Long getStartTime() {
        return startTime;
    }

    public Set<String> getUserIds() {
        return userIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Team team = (Team) o;

        if (startTime != null ? !startTime.equals(team.startTime) : team.startTime != null) return false;
        return userIds != null ? userIds.equals(team.userIds) : team.userIds == null;

    }

    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (userIds != null ? userIds.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Team{" +
                "startTime=" + startTime +
                ", userIds=" + userIds +
                '}';
    }
}