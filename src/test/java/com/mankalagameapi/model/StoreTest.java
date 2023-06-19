package com.mankalagameapi.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.mankalagameapi.model.Player.PLAYER_1;
import static org.assertj.core.api.Assertions.assertThat;

public class StoreTest {

    @Test
    public void givenAPitIsAStoreWhenIsSowableForPlayerThenTrueForOwner() {
        // given
        Store store = new Store(1, PLAYER_1);

        // when
        // then
        assertThat(store.isSowableForPlayer(PLAYER_1)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = Player.class, names = {"PLAYER_1"}, mode = EnumSource.Mode.EXCLUDE)
    public void givenAPitIsAStoreWhenIsSowableForPlayerThenFalseForNonOwner(Player player) {
        // given
        Store store = new Store(1, PLAYER_1);

        // when
        // then
        assertThat(store.isSowableForPlayer(player)).isFalse();
    }

    @Test
    public void givenAStoreWhenStonesCountIsZeroThenIsEmpty() {
        // given
        Store store = new Store(1, PLAYER_1);

        // when
        // then
        assertThat(store.isEmpty()).isTrue();
    }

    @Test
    public void givenAStoreWhenClearThenStonesCountIsZero() {
        // given
        Store store = new Store(1, PLAYER_1);

        // when
        store.clear();

        // then
        assertThat(store.getStonesCount()).isEqualTo(0);
    }

    @Test
    public void givenAStoreWhenAddStonesThenStonesCountIsIncreased() {
        // given
        Store store = new Store(1, PLAYER_1);
        store.setStonesCount(3);
        int stonesToAdd = 5;

        // when
        store.addStones(stonesToAdd);

        // then
        assertThat(store.getStonesCount()).isEqualTo(8);
    }
}
