package org.apache.spark.sql;

/**
 * Created by taofu on 2018/6/10.
 */
public class Origin {

    private int line;

    private int startPosition;

    Origin(int line, int startPosition) {
        this.line = line;
        this.startPosition = startPosition;
    }

    int getLine() {
        return line;
    }

    int getStartPosition() {
        return startPosition;
    }
}
