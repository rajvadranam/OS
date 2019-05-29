import java.io.*;
import java.sql.Timestamp;
import java.util.*;

public class MEMORY extends ERRORHANDLER {
    //variable for currentinstruction
    public String currentInstruction;
    //variable to get free memory space
    public int freememory;
    public int size;
    public Map<String, MemoryState> compactedmem;
    //memory module map stucture
    public Map<String, MemoryState> memoryModule;
    public boolean TotalCompact=false;
    /*
     * FunctionName: Memory Constructor
     * Description: Should be called before using memory
     * parameters: takes in size
     * returns: NA
     * lastChanged: 03/01/2019
     *
     * */
    public MEMORY(int size) {
        memoryModule = new LinkedHashMap<> ();
        compactedmem = new LinkedHashMap<> ();
        String Value = "XXXXXXXX";
        for (int i = 0; i < size; i++) {
            memoryModule.put ( String.valueOf ( i ), new MemoryState ( Value ) );
        }
        //getting free memory size
        freememory = getFreeMemorySize ( memoryModule );
        this.size = size;
        this.TotalCompact=false;
    }


    public void setNewMap( Map<String, MemoryState> value)
    {
        this.memoryModule=value;
    }
    /*
     * FunctionName: getMemory
     * Description: Should be called get the memory
     * parameters:
     * returns: hashmap of memory units
     * lastChanged: 03/01/2019
     *
     * */
    public Map<String, MemoryState> getMemory() {
        return this.memoryModule;
    }

    public Map<String, MemoryState> getCompactedMemory() {
        return this.compactedmem;
    }
    //get free memory space
    public int getFreeMemorySize(Map<String, MemoryState> memoryModule) {
        String Value = "XXXXXXXX";
        int i = 0;
        for (Map.Entry<String, MemoryState> v : memoryModule.entrySet ()) {
            if (v.getValue ().getValue ().equals ( Value )) {
                i++;
            }
        }
        return i;
    }
    //get occupied space
    public int getOccupiedMemory() {
        String Value = "XXXXXXXX";
        int i = 0;
        for (Map.Entry<String, MemoryState> v : memoryModule.entrySet ()) {
            MemoryState aa = v.getValue ();
            if (!Value.equals ( aa.getValue () )) {
                i++;
            }
        }
        return i;
    }

    public List<String> BestFit(MEMORY obj, Map<String, Map<String, LOADER.Unit>> loadunits, List<String> Jobs, Set<PROCESS> Done) {
        Set<String> bestFit = new HashSet<> ();
        Map<String, MEMORY.MemoryState> memory=null;
        try {
            if (Jobs.size () >= 2) {
                //get partitions
                if(obj.TotalCompact) {
                    memory = obj.getCompactedMemory ();
                }
                else
                {
                    memory = obj.getMemory ();
                }
                int freememory = obj.getFreeMemorySize ( memory );
                List<Pair<String, Integer>> prgmlenMap = new LinkedList<> ();
                for (String k : Jobs) {
                    if (!obj.IsinMemory ( k )) {
                        prgmlenMap.add ( new Pair<> ( k, loadunits.get ( k ).get ( "plen" ).getDecimal () * 2 ) );
                    }
                }
                if (prgmlenMap.size () == 1) {
                    bestFit.add ( prgmlenMap.get ( 0 ).first.toString () );
                }
                List<Integer> lenghts = new LinkedList<> ();
                List<String> DoneProces = new LinkedList<> ();
                for (PROCESS ss : Done) {
                    DoneProces.add ( ss.getPID () );
                }
                List<List<Integer>> mylists = new LinkedList<> ();
                for (Pair s : prgmlenMap) {
                    lenghts.add ( Integer.parseInt ( s.second.toString () ) );
                }
                Object[] n = lenghts.toArray ();
                Integer[] a = Arrays.copyOf ( n, n.length, Integer[].class );
                for (int i = a.length - 1; i > 0; i--) {
                    giveCombinations ( a, a.length, i, freememory, mylists );
                    if (mylists.size () == 0) {
                        continue;
                    } else {
                        break;
                    }
                }
                //best case first position
                List<Integer> listremainders = new LinkedList<> ();
                for (int i = 0; i < mylists.size (); i++) {
                    int value = freememory - mylists.get ( i ).stream ().mapToInt ( Integer::intValue ).sum ();
                    listremainders.add ( value );
                }
                int minIndex = listremainders.indexOf ( Collections.min ( listremainders ) );
                Set<Pair> aas = new HashSet<> ();
                for (Pair s : prgmlenMap) {
                    for (Integer aa : mylists.get ( minIndex )) {
                        if (aa == Integer.parseInt ( s.second.toString () ) && !obj.IsinMemory ( s.first.toString () ) && !DoneProces.contains ( s.first.toString () )) {
                            aas.add ( new Pair<> ( s.first.toString (), aa ) );
                        }
                    }
                }
                //best fit partition values
                for (Pair ss : aas) {
                    bestFit.add ( ss.first.toString () );
                }
            } else if (Jobs.size () == 1) {
                bestFit.add ( Jobs.get ( 0 ) );
            }
        } catch (Exception e) {
        }
        return new ArrayList<> ( bestFit );
    }
    private void giveCombinations(Integer arr[], int size, int split, int lookingsum, List<List<Integer>> mylist) {
        int com[] = new int[split];
        getCombination ( arr, com, 0, size - 1, 0, split, lookingsum, mylist );
    }
    private void getCombination(Integer arr[], int con[], int start,
                                int end, int index, int split, int lookingsum, List<List<Integer>> mylist) {
        if (index == split) {
            int val = 0;
            for (int j = 0; j < split; j++)
                val += con[j];
            if (val <= lookingsum) {
                mylist.add ( new LinkedList<Integer> () {{
                    for (int i : con) add ( i );
                }} );
            }
            return;
        }
        for (int i = start; i <= end && end - i + 1 >= split - index; i++) {
            con[index] = arr[i];
            getCombination ( arr, con, i + 1, end, index + 1, split, lookingsum, mylist );
        }
    }

    private int sumitup(int a[]) {
        int var = 0;
        for (int i = 0; i < a.length; i++) {
            var += a[i];
        }
        return var;
    }

    public int getNextAvailableIndex() {
        String Value = "XXXXXXXX";
        int i = 0;
        for (Map.Entry<String, MemoryState> v : memoryModule.entrySet ()) {
            MemoryState aa = v.getValue ();
            if (Value.equals ( aa.getValue () )) {
                break;
            } else {
                i++;
            }
        }
        return i;
    }

    public List<String> getAvailableHoles() {
        String Value = "XXXXXXXX";
        List<String> ss = new LinkedList<> ();
        int i = 0;
        for (Map.Entry<String, MemoryState> v : memoryModule.entrySet ()) {
            MemoryState aa = v.getValue ();
            if (Value.equals ( aa.getValue () )) {
                ss.add ( v.getKey () );
                continue;
            } else {
                i++;
            }
        }
        return ss;
    }

    public List<String> getActualData() {
        String Value = "XXXXXXXX";
        List<String> ss = new LinkedList<> ();
        int i = 0;
        for (Map.Entry<String, MemoryState> v : memoryModule.entrySet ()) {
            MemoryState aa = v.getValue ();
            if (!Value.equals ( aa.getValue () )) {
                ss.add ( v.getKey () );
                continue;
            } else {
                i++;
            }
        }
        return ss;
    }
//logic to compact to club irregular holes in memory to get best fit
    public Boolean Compact(MEMORY obj) {
        Boolean TotalCompact = false;
        String Value = "XXXXXXXX";
        Map<String, String> holes = new LinkedHashMap<> ();
        List<String> sa = getAvailableHoles ();
        List<String> data = getActualData ();
        int i = 0;
        boolean status = false;
        if(sa.size ()<obj.memoryModule.size ()) {

            for (String s : data) {
                if (!s.equals ( String.valueOf ( i ) )) {
                    i++;
                    status = true;
                }
            }
        }
        else
        {
            status=false;
        }
        List<String> availableinMem = new LinkedList<> ();
        if (status) {
            Map<String, MemoryState> mycopy = obj.memoryModule;
            Map<String, MemoryState> Newmycopy = new LinkedHashMap<> ();
            int k = 0;
            for (Map.Entry<String, MemoryState> saa : mycopy.entrySet ()) {
                if (!saa.getValue ().getJobId ().equals ( "$" )) {
                    availableinMem.add ( saa.getValue ().getJobId () );
                    MemoryState l = saa.getValue ();
                    l.SetCompacted ( true );
                    Newmycopy.put ( String.valueOf ( k ), l );
                    k++;
                }
            }
            if (Newmycopy.size () < obj.memoryModule.size ()) {
                int last = Newmycopy.keySet ().size ();
                int startid = obj.memoryModule.size () - last;
                compactedmem = Newmycopy;
                for (int j = last; j < obj.memoryModule.size (); j++) {
                    MemoryState saaas = new MemoryState ( Value );
                    saaas.SetCompacted ( true );
                    compactedmem.put ( String.valueOf ( j ), saaas );
                    this.TotalCompact = true;
                }
                if(this.TotalCompact)
                {
                    obj.setNewMap(compactedmem);

                }
            }
        }
        return this.TotalCompact;
    }

    public boolean IsinMemory(String jobid) {
        boolean status = false;
        int i = 0;
        for (Map.Entry<String, MemoryState> v : memoryModule.entrySet ()) {
            MemoryState aa = v.getValue ();
            if (jobid.equals ( aa.getJobId () )) {
                status = true;
                break;
            }
        }
        return status;
    }
    //main caller method to read/write/dump
    public MEMORY getObject(String JobID, String State, String index, List<String> Full_INS) throws ERRORHANDLER {
        switch (State.toLowerCase ()) {
            case "read":
                String firstvalue = memoryModule.get ( String.valueOf ( index ) ).getValue ();
                String second = memoryModule.get ( String.valueOf ( Integer.parseInt ( index ) + 1 ) ).getValue ();
                currentInstruction = new MemoryState ( firstvalue + second ).getValue ();
                Full_INS.add ( currentInstruction );
                break;
            case "writ":
                memoryModule.put ( String.valueOf ( index ), new MemoryState ( Full_INS.toArray ()[0].toString ().substring ( 0, (Full_INS.toArray ()[0].toString ().length () / 2) ) ) );
                memoryModule.put ( String.valueOf ( Integer.parseInt ( index ) + 1 ), new MemoryState ( Full_INS.toArray ()[0].toString ().substring ( (Full_INS.toArray ()[0].toString ().length () / 2) ) ) );
                break;
            case "dump":
                DUMP ( JobID, Integer.parseInt ( Full_INS.toArray ()[0].toString () ) );
                break;
        }
        return this;
    }

    /*
     * FunctionName: DUMP
     * Description: Should be called when ever memory Dump is needed
     * parameters: takes in size
     * returns: NA
     * lastChanged: 03/01/2019
     *
     * */
    public boolean DUMP(String JobId, int size) throws ERRORHANDLER {
        boolean status = false;
        String ts = SYSTEM.PROGRAM.santizeFilename ( "DUMP_" + JobId + "_" + new Timestamp ( new Date ().getTime () ).toString () + ".txt" );
        File fout = new File ( ts );
        try {
            fout.createNewFile ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
        List<MemoryState> values = new ArrayList<> ( memoryModule.values () );
        try {
            FileOutputStream fos = new FileOutputStream ( fout, false );
            BufferedWriter bw = new BufferedWriter ( new OutputStreamWriter ( fos ) );
            for (int i = 0; i < values.size (); i++) {
                if (i < values.size ()) {
                    List<String> alist = new LinkedList<> ();
                    for (int k = i; k <= (i + 1) * 7; k++) {
                        if (k < values.size ()) {
                            if (new LOADER.Unit ().isNumber ( values.get ( k ).getValue () )) {
                                alist.add ( new LOADER.Unit ().isNumber ( values.get ( k ).getValue () ) ? new LOADER.Unit ().BinaryToHex ( values.get ( k ).getValue () ).toString () : values.get ( k ).getValue () );
                            } else {
                                break;
                            }
                        }
                    }
                    i = i + 7;
                    bw.write ( String.format ( "%-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s\n", (0 >= 0) && (0 < alist.toArray ().length) ? alist.toArray ()[0] : "",
                            (1 >= 0) && (1 < alist.toArray ().length) ? alist.toArray ()[1] : ""
                            , (2 >= 0) && (2 < alist.toArray ().length) ? alist.toArray ()[2] : ""
                            , (3 >= 0) && (3 < alist.toArray ().length) ? alist.toArray ()[3] : ""
                            , (4 >= 0) && (4 < alist.toArray ().length) ? alist.toArray ()[4] : ""
                            , (5 >= 0) && (5 < alist.toArray ().length) ? alist.toArray ()[5] : ""
                            , (6 >= 0) && (6 < alist.toArray ().length) ? alist.toArray ()[6] : ""
                            , (7 >= 0) && (7 < alist.toArray ().length) ? alist.toArray ()[7] : "" ) );
                }
            }
            bw.close ();
            status = true;
            fos.close ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return status;
    }

    public MEMORY getmemoryObj() {
        return this;
    }

    public MEMORY getThisJobsMemory(String jobId, MEMORY mem) {
        MEMORY dd = null;
        Map<String, MemoryState> thispart = new LinkedHashMap<> ();
        try {
            int k = 0;
            for (MemoryState s : mem.memoryModule.values ()) {
                if (s.getJobId ().equals ( jobId )) {
                    thispart.put ( String.valueOf ( k ), s );
                    k++;
                }
            }
            dd = new MEMORY ( thispart.size () );
            dd.memoryModule = thispart;
        } catch (Exception e) {
            dd = new MEMORY ( thispart.size () );
            dd.memoryModule = thispart;
        }
        return dd;
    }
    public Map<String, String> getCompactedThisJobAddress(String jobId, MEMORY mem) {
        Map<String, String> thispart = new LinkedHashMap<> ();
        try {
            int k = 0;
            for (Map.Entry<String, MemoryState> s : mem.compactedmem.entrySet ()) {
                if (s.getValue ().getJobId ().equals ( jobId )) {
                    thispart.put ( String.valueOf ( k ), s.getKey () );
                    k++;
                }
            }
        } catch (Exception e) {
        }
        return thispart;
    }

    public Map<String, String> getThisJobAddress(String jobId, MEMORY mem) {
        Map<String, String> thispart = new LinkedHashMap<> ();
        try {
            int k = 0;
            for (Map.Entry<String, MemoryState> s : mem.memoryModule.entrySet ()) {
                if (s.getValue ().getJobId ().equals ( jobId )) {
                    thispart.put ( String.valueOf ( k ), s.getKey () );
                    k++;
                }
            }
        } catch (Exception e) {
        }
        return thispart;
    }
    /*
     *
     * Description: Sub class to implement lock functionality
     *
     * lastChanged: 03/01/2019
     *
     * */
    public static class MemoryState {
        boolean Lock;
        String Value;
        String MemoryType;
        String Job_ID;
        boolean Hasdata;
        boolean isCompacted;

        /*
         * FunctionName: constructor
         * parameters: takes in Value as String
         * lastChanged: 03/01/2019
         *
         * */
        public MemoryState(String value) {
            this.Lock = false;
            this.Value = value;
            this.MemoryType = "$";
            this.Job_ID = "$";
            this.Hasdata = false;
            this.isCompacted = false;
        }

        /*
         * FunctionName: getValue
         * Description: gets current string value of the memory
         * parameters: NA
         * returns: string
         * lastChanged: 03/01/2019
         *
         * */
        public String getValue() {
            return Value;
        }

        /*
         * FunctionName: getlock
         * Description: get the lock for current memeory entity
         * parameters: NA
         * returns: boolean status
         * lastChanged: 03/01/2019
         *
         * */
        public String getMemoryTpe() {
            return MemoryType;
        }
        /*
         * FunctionName: setLock
         * Description: sets the lock for current memeory entity
         * parameters: takes in boolean status for lock or unlock
         * returns: NA
         * lastChanged: 03/01/2019
         *
         * */
        public void setMemoryTpe(String memtype) {
            this.MemoryType = memtype;
        }
        public String getJobId() {
            return Job_ID;
        }
        public void setJobId(String JobId) {
            this.Job_ID = JobId;
        }
        /*
         * FunctionName: getlock
         * Description: get the lock for current memeory entity
         * parameters: NA
         * returns: boolean status
         * lastChanged: 03/01/2019
         *
         * */
        public boolean getLock() {
            return Lock;
        }
        /*
         * FunctionName: setLock
         * Description: sets the lock for current memeory entity
         * parameters: takes in boolean status for lock or unlock
         * returns: NA
         * lastChanged: 03/01/2019
         *
         * */
        public void setLock(boolean lockstatus) {
            this.Lock = lockstatus;
        }
        /*
         * FunctionName: getDirty
         * Description: get the state for current memeory entity
         * parameters: NA
         * returns: boolean status
         * lastChanged: 03/01/2019
         *
         * */
        public boolean getDirtyStat() {
            return this.Hasdata;
        }


        /*
         * FunctionName: setJobID
         * Description: sets the lock for current memeory entity
         * parameters: takes in boolean status for lock or unlock
         * returns: NA
         * lastChanged: 03/01/2019
         *
         * */
        /*
         * FunctionName: setDirty
         * Description: sets the state for current memeory entity
         * parameters: takes in boolean status for lock or unlock
         * returns: NA
         * lastChanged: 03/01/2019
         *
         * */
        public void setDirty(boolean lockstatus) {
            this.Hasdata = lockstatus;
        }
        /*
         * FunctionName: getlock
         * Description: get the lock for current memeory entity
         * parameters: NA
         * returns: boolean status
         * lastChanged: 03/01/2019
         *
         * */
        public boolean getCompactedState() {
            return isCompacted;
        }
        /*
         * FunctionName: setLock
         * Description: sets the lock for current memeory entity
         * parameters: takes in boolean status for lock or unlock
         * returns: NA
         * lastChanged: 03/01/2019
         *
         * */
        public void SetCompacted(boolean lockstatus) {
            this.isCompacted = lockstatus;
        }
        /*
         * FunctionName: toString
         * Description: custom override to toString to display value and lock of the memory space
         * parameters: NA
         * returns: String
         * lastChanged: 03/01/2019
         *
         * */
        @Override
        public String toString() {
            String aalue = "";
            try {
                aalue = new LOADER.Unit ().BinaryToHex ( Value ).toString ();
            } catch (Exception e) {
            }
            return String.format ( aalue + "--> Lock:{" + Lock + "} Type:{" + MemoryType + "} Job:{ " + getJobId () + " }" );
        }
    }

    static class Pair<U, V> {
        public final U first;    // first field of a Pair
        public final V second;    // second field of a Pair

        // Constructs a new Pair with specified values
        private Pair(U first, V second) {
            this.first = first;
            this.second = second;
        }
        // Factory method for creating a Typed Pair immutable instance
        public static <U, V> Pair<U, V> C(U a, V b) {
            // calls private constructor
            return new Pair<> ( a, b );
        }
        @Override
        // Checks specified object is "equal to" current object or not
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass () != o.getClass ())
                return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            // call equals() method of the underlying objects
            if (!first.equals ( pair.first ))
                return false;
            return second.equals ( pair.second );
        }
        @Override
        // Computes hash code for an object to support hash tables
        public int hashCode() {
            // use hash codes of the underlying objects
            return 31 * first.hashCode () + second.hashCode ();
        }
        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }
}
