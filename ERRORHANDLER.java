import java.util.ArrayList;
import java.util.Arrays;
public  class ERRORHANDLER extends Exception {
    MEMORY mem;
    ERRORHANDLER()
    {
    }
    public ERRORHANDLER(MEMORY obj) {
        this.mem = obj;
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
    public ERRORHANDLER(String JobId,String message, MEMORY mem) {
        try {
            mem.getObject(JobId,"DUMP", String.valueOf(0), new ArrayList<>(Arrays.asList(String.valueOf ( mem.size))));
        } catch (Exception e) {
        }
    }
    public ERRORHANDLER(String message) {
        try {
            // mem.getObject("DUMP", String.valueOf(0), new ArrayList<>(Arrays.asList("64")));
        } catch (Exception e) {
        } finally {
            try {
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
}
