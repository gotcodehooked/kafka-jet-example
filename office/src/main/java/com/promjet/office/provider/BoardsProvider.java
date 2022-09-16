package com.promjet.office.provider;

import com.promjet.common.bean.Board;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@Getter
@Component
public class BoardsProvider {

    private final List<Board> boards = new ArrayList<>();
    private final Lock lock = new ReentrantLock(true);

    private Optional<Board> getBoard(String boardName){
        return boards.stream().filter(board -> board.getName().equals(boardName)).findFirst();
    }

    private void addBoard(Board board){


        try {
            lock.lock();
            Optional<Board> optionalBoard = getBoard(board.getName());

            if (optionalBoard.isPresent()) {
                int index = boards.indexOf(optionalBoard.get());
                boards.set(index, board);
            } else {
                boards.add(board);
            }
        }finally {
            lock.unlock();
        }
    }
}
