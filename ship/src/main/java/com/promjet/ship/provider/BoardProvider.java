package com.promjet.ship.provider;


import com.promjet.common.bean.Board;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Getter
@ConfigurationProperties(prefix = "application")
public class BoardProvider {
    private final List<Board> boards = new ArrayList<>();
}
