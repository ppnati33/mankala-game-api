package com.mankalagameapi.model;

public class House extends Pit {

    public House(Integer id, Integer stonesCount, Player owner) {
        super(id, stonesCount, owner);
    }

    @Override
    public boolean isSowableForPlayer(Player player) {
        return true;
    }
}
