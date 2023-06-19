package com.mankalagameapi.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.mankalagameapi.model.Player.PLAYER_1;
import static org.assertj.core.api.Assertions.assertThat;

public class HouseTest {

    @ParameterizedTest
    @EnumSource(Player.class)
    public void givenAPitIsAHouseWhenIsSowableForPlayerThenAlwaysTrue(Player player) {
        // given
        House house = new House(1, 1, PLAYER_1);

        // when
        // then
        assertThat(house.isSowableForPlayer(player)).isTrue();
    }

    @Test
    public void givenAHouseWhenStonesCountIsZeroThenIsEmpty() {
        // given
        House house = new House(1, 0, PLAYER_1);

        // when
        // then
        assertThat(house.isEmpty()).isTrue();
    }

    @Test
    public void givenAHouseWhenClearThenStonesCountIsZero() {
        // given
        House house = new House(1, 0, PLAYER_1);

        // when
        house.clear();

        // then
        assertThat(house.getStonesCount()).isEqualTo(0);
    }

    @Test
    public void givenAHouseWhenAddStonesThenStonesCountIsIncreased() {
        // given
        House house = new House(1, 10, PLAYER_1);
        int stonesToAdd = 5;

        // when
        house.addStones(stonesToAdd);

        // then
        assertThat(house.getStonesCount()).isEqualTo(15);
    }
}
