package org.apache.spark.sql;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Created by taofu on 2018/6/10.
 */
public class ParseErrorListener extends BaseErrorListener {
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {
        Origin position = new Origin(line, charPositionInLine);
        throw new ParseException(msg, position, position);
    }
}
