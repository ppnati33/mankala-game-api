package com.mankalagameapi.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "game")
public class GameProperties {
    private Integer pitStones;
}
