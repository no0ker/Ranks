package com.github.no0ker.ranks;

import java.time.Instant;

public interface MathMakerInterface {
    MathMakerInterface addUser(String userId, Byte rank);
    MathMakerInterface addUser(String userId, Byte rank, Long enterTime);
}
