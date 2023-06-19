package com.mankalagameapi.model;

public class Store extends Pit {

    public Store(Integer id, Player owner) {
        super(id, 0, owner);
    }

    @Override
    public boolean isSowableForPlayer(Player player) {
        return getOwner().equals(player);
    }
}
