package com.mankalagameapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.lang.String.format;

@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class Pit {

    private static final int EMPTY_PIT_STONES_COUNT = 0;

    private Integer id;
    private Integer stonesCount;
    private Player owner;

    public abstract boolean isSowableForPlayer(Player player);

    public boolean isEmpty() {
        return this.stonesCount == EMPTY_PIT_STONES_COUNT;
    }

    public void clear() {
        this.stonesCount = EMPTY_PIT_STONES_COUNT;
    }

    public void addStones(int stonesCount) {
        this.stonesCount += stonesCount;
    }

    @Override
    public String toString() {
        return format("[%s:%s]",id, stonesCount);
    }
}
