package edu.ostate.cs5323;

import java.util.LinkedList;
import java.util.List;

public  class ERRORHANDLER extends Exception {



    public ERRORHANDLER()
    {
        super();

    }

    public ERRORHANDLER(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ERRORHANDLER(Throwable cause) {

        super(cause);

    }
    public ERRORHANDLER(String message, Throwable cause) {
        super(message, cause);
    }

    public ERRORHANDLER(String message) {
        super(message);
    }

}
