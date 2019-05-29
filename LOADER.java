import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@SuppressWarnings("unchecked")
public class LOADER extends ERRORHANDLER {
    public final Map<String, Map<String, Unit>> firstjob;
    public Map<String, List<Unit>> loadmap;
    public Map<String, Map<String, Unit>> loaderunits;
    public Map<String, Map<String, Unit>> TraceSwitch;
    public Map<String, Integer> firstInstructionIndex;
    public Queue<Map<String, Unit>> waitQ;
    public List<Integer> Arraivals;
    private String filename;

    /*
     * FunctionName: Loader constructor
     * parameters: program file name <Assuming that file will be in same directory as in main class does>, Memory Object<Already Initialized
     * returns: NA
     * lastChanged: 03/01/2019
     *
     * */
    public LOADER(String UserprogFileName, MEMORY obj) throws ERRORHANDLER {
        loadmap = new LinkedHashMap<> ();
        TraceSwitch = new LinkedHashMap<> ();
        loaderunits = new LinkedHashMap<> ();
        firstjob = new HashMap<> ();
        filename = UserprogFileName;
        waitQ = new LinkedList<> ();
        firstInstructionIndex = new LinkedHashMap<> ();
        Arraivals = new LinkedList<> ();
        GetLoaderContentsV2 ( filename, loaderunits, loadmap, firstInstructionIndex );
        Collections.sort ( Arraivals );
    }
    public static int LineCount(File file) throws IOException {
        try (Stream<String> lines = Files.lines ( file.toPath () )) {
            return (int) lines.count ();
        }
    }
    public static Map<String, List<String>> GetProgramsSeperate(File file) throws IOException {
        Map<String, List<String>> Programs = new LinkedHashMap<> ();
        try (Stream<String> lines = Files.lines ( file.toPath () )) {
            List<String> alllines = lines.collect ( Collectors.toList () );
            int size = alllines.size ();
            List<Integer> filelist = new LinkedList<> ();
            List<String> programlist = new LinkedList<> ();
            List<String> programlistCopy = new LinkedList<> ();
            int nprog = 0;
            int k = 0;
            int sub = 1;
            String largestring = "";
            for (String ll : alllines) {
                largestring += ll;
                if (!ll.toLowerCase ().contains ( "job" ) && !ll.toLowerCase ().contains ( "end" )) {
                    if (ll.split ( " " ).length == 2) {
                        nprog++;
                        filelist.add ( sub );
                        sub = 1;
                    } else {
                        sub++;
                    }
                }
            }
            for (String ll : alllines) {
                if (!ll.toLowerCase ().contains ( "job" ) && !ll.toLowerCase ().contains ( "end" )) {
                    programlist.add ( ll );
                }
            }
            programlistCopy = (List<String>) ((LinkedList<String>) programlist).clone ();
            int h = 1;
            List<String> thislist = new LinkedList<> ();
            for (int s : filelist) {
                for (int i = 0; i <= s - 1; i++) {
                    String ss = programlist.get ( 0 );
                    programlist.remove ( 0 );
                    thislist.add ( ss );
                    if (i == s - 1) {
                        Programs.put ( String.valueOf ( h ), thislist );
                        thislist = new ArrayList<> ();
                        h++;
                        break;
                    }
                }
            }
            //ERROR HANDLER for LOADER
            int i = 1;
            boolean status = false;
            for (String s : programlistCopy) {
                if (s.split ( " " ).length == 4) {
                    if (new Unit ( s.split ( " " )[0] ).getDecimal () <= i) {
                        i++;
                        status = true;
                    } else {
                        status = false;
                    }
                }
            }
            if (!status) {
                List<CPU.OUT> out = new ArrayList<> ();
                String Message = "Abnormal - JOB { " + i + " } CODE MISSING ERROR";
                out.add ( new CPU.OUT ( "XX1", Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                SYSTEM.PROGRAM.WriteToOutputFile ( out, new MEMORY ( 10 ) );
                SYSTEM.AbnormalTerminations += 1;
                //throw new ERRORHANDLER(Message);
            }
            int value3 = largestring.split ( "JOB" ).length;
            int value = largestring.split ( "ENDJOB" ).length;
            int value4 = largestring.split ( "END" ).length;
            if (value3 == Programs.size ()) {
                List<CPU.OUT> out = new ArrayList<> ();
                String Message = "Abnormal - MISSING JOB TAG ERROR";
                out.add ( new CPU.OUT ( "XX1", Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                SYSTEM.PROGRAM.WriteToOutputFile ( out, new MEMORY ( 10 ) );
                SYSTEM.AbnormalTerminations += 1;
                //throw new ERRORHANDLER(Message);
            }
            if (Programs.size () - 1 == value) {
                List<CPU.OUT> out = new ArrayList<> ();
                String Message = "Abnormal - MISSING END TAG ERROR";
                out.add ( new CPU.OUT ( "XX1", Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                SYSTEM.PROGRAM.WriteToOutputFile ( out, new MEMORY ( 10 ) );
                SYSTEM.AbnormalTerminations += 1;
                //throw new ERRORHANDLER(Message);
            }
        } catch (Exception errorhandler) {
            errorhandler.printStackTrace ();
        }
        return Programs;
    }
    public LOADER getLoaderObject() {
        return this;
    }
    /*
     * FunctionName: getloadmap
     * Description: gets the load maps read from the programfile
     * parameters: NA
     * returns: returns a hashmap
     * lastChanged: 03/01/2019
     *
     * */
    public Map<String, List<Unit>> getLoadmap() {
        return loadmap;
    }
    /*
     * FunctionName: getloadmap
     * Description: gets the load maps read from the programfile
     * parameters: NA
     * returns: returns a hashmap
     * lastChanged: 03/01/2019
     *
     * */
    public List<String> getLoadmapKey() {
        return new ArrayList<> ( loadmap.keySet () );
    }
    /*
     * FunctionName: getLoadUnits
     * Description: get list of all load units
     * parameters: NA
     * returns: ArrayList
     * lastChanged: 03/01/2019
     *
     * */
    public Map<String, Map<String, Unit>> getLoadunits() {
        return loaderunits;
    }

    /*
     * FunctionName: setTomemory( Memory Write)
     * Description: Writes program code to Memory
     * parameters: takes in initialized memory object
     * returns: hashmap
     * lastChanged: 03/01/2019
     *
     * */
    public MEMORY setToMemoryV2(MEMORY obj, String JobID) throws ERRORHANDLER {
        try {
            //GetLoaderContentsV2(filename, loaderunits, loadmap, firstInstructionIndex);
            String K = JobID;
            Map<String, MEMORY.MemoryState> memory = obj.getMemory ();
            boolean status;
            boolean status1 = false;
            int i = obj.getNextAvailableIndex ();
            status1 = isStatus1 ( JobID, memory, status1 );
            if (!status1) {
                if (obj.getFreeMemorySize ( memory ) >= loaderunits.get ( K ).get ( "plen" ).getDecimal ()) {
                    this.firstInstructionIndex.put ( loaderunits.get ( K ).get ( "jobid" ).toString (), loaderunits.get ( K ).get ( "sa" ).getDecimal () );
                    for (Unit u : loaderunits.get ( K ).get ( "code" ).code) {
                        if (u.ins) {
                            int len = u.getbinary ().length ();
                            MEMORY.MemoryState sa = new MEMORY.MemoryState ( u.getbinary ().substring ( 0, (len / 2) ) );
                            sa.setMemoryTpe ( "INS" );
                            sa.setJobId ( loaderunits.get ( K ).get ( "jobid" ).toString () );
                            sa.setDirty ( true );
                            if (memory.containsKey ( String.valueOf ( i ) )) {
                                memory.put ( String.valueOf ( i ), sa );
                            } else {
                                status = false;
                            }
                            i++;
                            MEMORY.MemoryState sa2 = new MEMORY.MemoryState ( u.getbinary ().substring ( (len / 2), len ) );
                            sa2.setMemoryTpe ( "INS" );
                            sa2.setJobId ( loaderunits.get ( K ).get ( "jobid" ).toString () );
                            sa2.setDirty ( true );
                            if (memory.containsKey ( String.valueOf ( i ) )) {
                                memory.put ( String.valueOf ( i ), sa2 );
                            } else {
                                status = false;
                            }
                            i++;
                        } else {
                            int len = u.getbinary ().length ();
                            MEMORY.MemoryState sa = new MEMORY.MemoryState ( u.getbinary ().substring ( 0, (len / 2) ) );
                            sa.setMemoryTpe ( "DATA" );
                            sa.setJobId ( loaderunits.get ( K ).get ( "jobid" ).toString () );
                            sa.setDirty ( true );
                            if (memory.containsKey ( String.valueOf ( i ) )) {
                                memory.put ( String.valueOf ( i ), sa );
                            } else {
                                status = false;
                            }
                            i++;
                            MEMORY.MemoryState sa2 = new MEMORY.MemoryState ( u.getbinary ().substring ( (len / 2), len ) );
                            sa2.setMemoryTpe ( "DATA" );
                            sa2.setJobId ( loaderunits.get ( K ).get ( "jobid" ).toString () );
                            sa2.setDirty ( true );
                            if (memory.containsKey ( String.valueOf ( i ) )) {
                                memory.put ( String.valueOf ( i ), sa2 );
                            } else {
                                status = false;
                            }
                            i++;
                        }
                    }
                    TraceSwitch.put ( loaderunits.get ( K ).get ( "jobid" ).toString (), loaderunits.get ( K ) );
                }
            }
        } catch (Exception e) {
            // System.exit ( 0 );
        }
        return obj;
    }

    /*
     * FunctionName: setToMemoryMultiV2( Memory Write)
     * Description: Writes program code to Memory
     * parameters: takes in initialized memory object
     * returns: hashmap
     * lastChanged: 03/01/2019
     *
     * */
    public List<List<String>> setToMemorymultyV2(MEMORY obj, List<String> JobIDs, Set<PROCESS> Done) throws ERRORHANDLER {
        List<List<String>> whole = new LinkedList<> ();
        List<String> setjobs = new LinkedList<> ();
        Set<String> temp = new HashSet<> ( JobIDs );
        List<String> leftOut = new LinkedList<> ();
        try {
            //best fit
            Map<String, MEMORY.MemoryState> memory=null;
            //do compaction
            if(obj.Compact (obj))
            {
                 memory = obj.getCompactedMemory ();
            }
            else
            {
                memory= obj.getMemory ();
            }
            List<String> BestFit = obj.BestFit ( obj, loaderunits, JobIDs, Done );
            temp.removeAll ( BestFit );
            leftOut.addAll ( temp );
            for (String j : BestFit) {
                String K = j;

                boolean status;
                boolean status1 = false;


                status1 = isStatus1 ( K, memory, status1 );
                //see if compaction is required

                int i = obj.getNextAvailableIndex ();
                if (!status1) {
                    if (obj.getFreeMemorySize ( memory ) >= loaderunits.get ( K ).get ( "plen" ).getDecimal () * 2) {
                        this.firstInstructionIndex.put ( loaderunits.get ( K ).get ( "jobid" ).toString (), loaderunits.get ( K ).get ( "sa" ).getDecimal () );
                        for (Unit u : loaderunits.get ( K ).get ( "code" ).code) {
                            if (u.ins) {
                                int len = u.getbinary ().length ();
                                MEMORY.MemoryState sa = new MEMORY.MemoryState ( u.getbinary ().substring ( 0, (len / 2) ) );
                                sa.setMemoryTpe ( "INS" );
                                sa.setJobId ( loaderunits.get ( K ).get ( "jobid" ).toString () );
                                sa.setDirty ( true );
                                if (memory.containsKey ( String.valueOf ( i ) ) && !memory.get ( String.valueOf ( i ) ).getDirtyStat ()) {
                                    memory.put ( String.valueOf ( i ), sa );
                                } else {
                                    i=obj.getNextAvailableIndex ();
                                    //System.out.println ("trying to overrite the memory when it has valid data ");
                                }
                                i++;
                                MEMORY.MemoryState sa2 = new MEMORY.MemoryState ( u.getbinary ().substring ( (len / 2), len ) );
                                sa2.setMemoryTpe ( "INS" );
                                sa2.setJobId ( loaderunits.get ( K ).get ( "jobid" ).toString () );
                                sa2.setDirty ( true );
                                if (memory.containsKey ( String.valueOf ( i ) ) && !memory.get ( String.valueOf ( i ) ).getDirtyStat ()) {
                                    memory.put ( String.valueOf ( i ), sa2 );
                                } else {
                                    i=obj.getNextAvailableIndex ();
                                }
                                i++;
                            } else {
                                int len = u.getbinary ().length ();
                                MEMORY.MemoryState sa = new MEMORY.MemoryState ( u.getbinary ().substring ( 0, (len / 2) ) );
                                sa.setMemoryTpe ( "DATA" );
                                sa.setJobId ( loaderunits.get ( K ).get ( "jobid" ).toString () );
                                sa.setDirty ( true );
                                if (memory.containsKey ( String.valueOf ( i ) ) && !memory.get ( String.valueOf ( i ) ).getDirtyStat ()) {
                                    memory.put ( String.valueOf ( i ), sa );
                                } else {
                                    i=obj.getNextAvailableIndex ();
                                }
                                i++;
                                MEMORY.MemoryState sa2 = new MEMORY.MemoryState ( u.getbinary ().substring ( (len / 2), len ) );
                                sa2.setMemoryTpe ( "DATA" );
                                sa2.setJobId ( loaderunits.get ( K ).get ( "jobid" ).toString () );
                                sa2.setDirty ( true );
                                if (memory.containsKey ( String.valueOf ( i ) ) && !memory.get ( String.valueOf ( i ) ).getDirtyStat ()) {
                                    memory.put ( String.valueOf ( i ), sa2 );
                                } else {
                                    i=obj.getNextAvailableIndex ();
                                }
                                i++;
                            }
                        }
                        //System.out.println ("free memory -->"+obj.getFreeMemorySize ( memory ));
                        TraceSwitch.put ( loaderunits.get ( K ).get ( "jobid" ).toString (), loaderunits.get ( K ) );
                        setjobs.add ( loaderunits.get ( K ).get ( "jobid" ).toString () );
                    } else {
                        if (loaderunits.get ( K ).get ( "plen" ).getDecimal () * 2 > obj.size) {
                            List<CPU.OUT> out = new ArrayList<> ();
                            String Message = "Abnormal - ALLOC LARGE DATA EXCEPTION";
                            out.add ( new CPU.OUT ( "1", Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                            SYSTEM.PROGRAM.WriteToOutputFile ( out, obj );
                            //throw new ERRORHANDLER(Message, obj);
                        }
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            //5 System.exit ( 0 );
        }
        Collections.sort ( setjobs, Collections.reverseOrder () );
        whole.add ( setjobs );
        whole.add ( leftOut );
        return whole;
    }

    private boolean isStatus1(String JobID, Map<String, MEMORY.MemoryState> memory, boolean status1) {
        try {
            for (Map.Entry<String, MEMORY.MemoryState> s : memory.entrySet ()) {
                try {
                    if (s.getValue ().getJobId ().equals ( JobID )) {
                        status1 = true;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            status1 = false;
        }
        return status1;
    }
    /*
    /*
     * FunctionName: getTraceSwitch
     * Description: gets the trace switch value
     * parameters: NA
     * returns: Loader.UNIT
     * lastChanged: 03/01/2019
     *
     * */
    public Map<String, Unit> getTraceSwitch(String index) throws ERRORHANDLER {
        return TraceSwitch.get ( index );
    }
    //reusables
    /*
     * FunctionName: getProgramIndex
     * Description: gets the trace switch value
     * parameters: NA
     * returns: int
     * lastChanged: 03/01/2019
     *
     * */
    public int getProgramIndex(String Key) throws ERRORHANDLER {
        return firstInstructionIndex.get ( Key );
    }

    /*
     * FunctionName: ReadAheadLoaderFile
     * Description: Should be used to get loader file contents
     * parameters: takes string <filename>
     * outs: hashmap with PC as identifier
     * lastChanged: 2/12/2019
     *
     * */
    public List<List<String>> ReadaHeadLoaderFile(File f) {
        List<List<String>> fileInfo = new LinkedList<> ();
        try {
            BufferedReader br = new BufferedReader ( new FileReader ( f ) );
            String Line = "";
            int loaderNotifier = 1;
            int pos = 1;
            while ((Line = br.readLine ()) != null) {
                if (Line.trim ().toLowerCase ().contains ( "end" )) {
                    fileInfo.add ( new ArrayList<String> ( Arrays.asList ( String.valueOf ( loaderNotifier - 2 ) ) ) );
                    loaderNotifier = 0;
                }
                loaderNotifier++;
            }
        } catch (Exception e) {
        }
        return fileInfo;
    }

    /*
     * FunctionName: GetLoaderContents
     * Description: Should be used to get loader file contents
     * parameters: takes string <filename>
     * outs: hashmap with PC as identifier
     * lastChanged: 2/12/2019
     *
     * */
    public void GetLoaderContentsV2(String filename, Map<String, Map<String, Unit>> LoaderUnits, Map<String, List<Unit>> loadmap, Map<String, Integer> firstInstructionIndex) throws ERRORHANDLER {
        try {
            List<List<String>> loadinfo = ReadaHeadLoaderFile ( new File ( filename ) );
            BufferedReader br = new BufferedReader ( new FileReader ( filename ) );
            int i = 0;
            int j = 1;
            String SA = "";
            int index = 0;
            Map<String, List<String>> aa = GetProgramsSeperate ( new File ( filename ) );
            for (String K : aa.keySet ()) {
                int linec = aa.get ( K ).size ();
                List<Unit> thisline = new ArrayList<> ();
                List<Unit> justcode = new ArrayList<> ();
                Map<String, Unit> unitVec = new HashMap<> ();
                boolean set = false;
                for (String line : new ArrayList<> ( aa.get ( K ) )) {
                    i++;
                    int temp;
                    if (line.split ( " " ).length == 4) {
                        try {
                            if (line.split ( " " ).length == 3) {
                                List<String> errorSuspicion = new ArrayList<String> ( Arrays.asList ( line.split ( " " ) ) );
                                if (!new LOADER.Unit ().isHEX ( errorSuspicion.get ( 0 ) )) {
                                    if (errorSuspicion.get ( 0 ).length () != 3) {
                                        List<CPU.OUT> out = new ArrayList<> ();
                                        String Message = "Abnormal - MISSING JOB NUMBER ERROR";
                                        out.add ( new CPU.OUT ( "XX1", Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                                        SYSTEM.PROGRAM.WriteToOutputFile ( out, mem );
                                        SYSTEM.AbnormalTerminations += 1;
                                        //throw new ERRORHANDLER(Message);
                                    }
                                } else {
                                    List<CPU.OUT> out = new ArrayList<> ();
                                    String Message = "Abnormal - NotHex JobNumber ERROR";
                                    out.add ( new CPU.OUT ( "XX1", Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                                    SYSTEM.PROGRAM.WriteToOutputFile ( out, mem );
                                    SYSTEM.AbnormalTerminations += 1;
                                    //throw new ERRORHANDLER(Message);
                                }
                                if (new LOADER.Unit ().isHEX ( errorSuspicion.get ( 1 ) )) {
                                    if (errorSuspicion.get ( 1 ).length () != 4) {
                                        List<CPU.OUT> out = new ArrayList<> ();
                                        String Message = "Abnormal - MISSING ArrivalTime ERROR";
                                        out.add ( new CPU.OUT ( "XX1", Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                                        SYSTEM.PROGRAM.WriteToOutputFile ( out, mem );
                                        SYSTEM.AbnormalTerminations += 1;
                                        //throw new ERRORHANDLER(Message);
                                    }
                                } else {
                                    List<CPU.OUT> out = new ArrayList<> ();
                                    String Message = "Abnormal - NotHex ArrivalTime ERROR";
                                    out.add ( new CPU.OUT ( "XX1", Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                                    SYSTEM.PROGRAM.WriteToOutputFile ( out, mem );
                                    SYSTEM.AbnormalTerminations += 1;
                                    //throw new ERRORHANDLER(Message);
                                }
                                if (new LOADER.Unit ().isHEX ( errorSuspicion.get ( 2 ) )) {
                                    if (errorSuspicion.get ( 1 ).length () != 3) {
                                        List<CPU.OUT> out = new ArrayList<> ();
                                        String Message = "Abnormal - MISSING ProgramLength ERROR";
                                        out.add ( new CPU.OUT ( "XX1", Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                                        SYSTEM.PROGRAM.WriteToOutputFile ( out, mem );
                                        SYSTEM.AbnormalTerminations += 1;
                                        //throw new ERRORHANDLER(Message);
                                    }
                                } else {
                                    List<CPU.OUT> out = new ArrayList<> ();
                                    String Message = "Abnormal - NotHex ProgramLength ERROR";
                                    out.add ( new CPU.OUT ( "XX1", Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                                    SYSTEM.PROGRAM.WriteToOutputFile ( out, mem );
                                    SYSTEM.AbnormalTerminations += 1;
                                    //throw new ERRORHANDLER(Message);
                                }
                                //something is missing in loader job
                            }
                            List<String> startandtrace = new ArrayList<String> ( Arrays.asList ( line.split ( " " ) ) );
                            if (new LOADER.Unit ().isHEX ( startandtrace.get ( 0 ) )) {
                                unitVec.put ( "jobid", new LOADER.Unit ( startandtrace.get ( 0 ) ) );
                            }
                            if (new LOADER.Unit ().isHEX ( startandtrace.get ( 1 ) )) {
                                unitVec.put ( "arr", new LOADER.Unit ( startandtrace.get ( 1 ), false, false, false, true ) );
                            }
                            if (new LOADER.Unit ().isHEX ( startandtrace.get ( 2 ) )) {
                                unitVec.put ( "plen", new LOADER.Unit ( startandtrace.get ( 2 ) ) );
                            }
                            if (new LOADER.Unit ().isHEX ( startandtrace.get ( 0 ) )) {
                                unitVec.put ( "sa", new LOADER.Unit ( startandtrace.get ( 3 ) ) );
                            }
                            SA = String.valueOf ( new LOADER.Unit ().HexToDecimal ( startandtrace.get ( 3 ) ) );
                            index = Integer.parseInt ( SA );
                            this.firstInstructionIndex.put ( String.valueOf ( j ), index );
                            thisline.add ( new LOADER.Unit ( startandtrace.get ( 0 ), false, false, false, true ) );
                            thisline.add ( new LOADER.Unit ( startandtrace.get ( 1 ) ) );
                            thisline.add ( new LOADER.Unit ( startandtrace.get ( 2 ) ) );
                            thisline.add ( new LOADER.Unit ( startandtrace.get ( 3 ) ) );
                        } catch (Exception e) {
                        }
                    } else {
                        if (line.split ( " " ).length == 2) {
                            if (line.split ( " " )[0].equals ( "000" ) || line.split ( " " )[0].equals ( "001" )) {
                                unitVec.put ( "trace", new LOADER.Unit ( line.split ( " " )[0] ) );
                            } else {
                                List<CPU.OUT> out = new ArrayList<> ();
                                String Message = "Abnormal - UNRECOGNIZABLE TRACEBIT ERROR";
                                out.add ( new CPU.OUT ( unitVec.get ( "jobid" ).toString (), Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                                SYSTEM.PROGRAM.WriteToOutputFile ( out, mem );
                                unitVec.put ( "trace", new LOADER.Unit ( "001" ) );
                                SYSTEM.AbnormalTerminations+=1;
                                // throw new ERRORHANDLER(Message);
                            }
                            thisline.add ( new LOADER.Unit ( line.split ( " " )[0], false, false, true, false ) );
                            thisline.add ( new LOADER.Unit ( line.split ( " " )[1], false, true, false, false ) );
                            if (line.split ( " " )[1].equals ( "000" ) || line.split ( " " )[1].equals ( "001" )) {
                                unitVec.put ( "priority", new LOADER.Unit ( line.split ( " " )[1] ) );
                            } else {
                                List<CPU.OUT> out = new ArrayList<> ();
                                String Message = "Abnormal - UNRECOGNIZABLE Priority ERROR";
                                out.add ( new CPU.OUT ( unitVec.get ( "jobid" ).toString (), Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                                SYSTEM.PROGRAM.WriteToOutputFile ( out, mem );
                                SYSTEM.AbnormalTerminations+=1;
                                //throw new ERRORHANDLER(Message);
                                unitVec.put ( "priority", new LOADER.Unit ( "000" ) );
                            }
                        } else {
                            List<String> programCode = SplitInterval ( line.trim (), 4 );
                            temp = index / 2;
                            for (String s : programCode) {
                                if (temp > 0 && !set) {
                                    thisline.add ( new LOADER.Unit ( s, false, false ) );
                                    justcode.add ( new LOADER.Unit ( s, false, false ) );
                                    temp--;
                                } else {
                                    set = true;
                                    thisline.add ( new LOADER.Unit ( s, false, true ) );
                                    justcode.add ( new LOADER.Unit ( s, false, true ) );
                                }
                            }
                        }
                    }
                    if (i == linec) {
                        thisline.get ( index ).setStartINS ( true );
                        justcode.get ( index ).setStartINS ( true );
                        loadmap.put ( unitVec.get ( "jobid" ).toString (), thisline );
                        unitVec.put ( "code", new Unit ().setCode ( justcode, index ) );
                        if (unitVec.get ( "plen" ).getDecimal () != unitVec.get ( "code" ).code.size ()) {
                            //System.out.println ("Error in Program lenght  for "+unitVec.get("jobid").toString ()+" , Given as "+unitVec.get("plen").Hex+" bUt code is of size "+unitVec.get ( "code" ).code.size ());
                            List<CPU.OUT> out = new ArrayList<> ();
                            String Message = "Abnormal - ERROR Incorrect ProgLEN EXCEPTION";
                            out.add ( new CPU.OUT ( unitVec.get ( "jobid" ).toString (), Message, new LOADER.Unit ().DecimalToBinary ( 0, 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf ( 1 ), String.valueOf ( 0 ) ) ) ) );
                            SYSTEM.PROGRAM.WriteToOutputFile ( out, mem );
                            SYSTEM.AbnormalTerminations+=1;
                            i=0;
                            //throw new ERRORHANDLER(Message);
                            continue;
                        }
                        LoaderUnits.put ( unitVec.get ( "jobid" ).toString (), unitVec );
                        Arraivals.add ( Integer.parseInt ( unitVec.get ( "arr" ).toString () ) );
                        j++;
                        thisline = new ArrayList<> ();
                        unitVec = new HashMap<> ();
                        i = 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
            System.out.println ( "Error Occured in reading or user program loader file" );
            System.exit ( -1 );
        }
    }
    /*
     * FunctionName: SplitInterval
     * Description: Should be used to get splitted strings on a given long string
     * parameters: takes string <Ex:program instruction>
     * returns: List of Strings
     * lastChanged: 2/12/2019
     *
     * */
    public List<String> SplitInterval(String IN, int splicharlength) throws ERRORHANDLER {
        int totalcharlenght = IN.length ();
        List<String> result = new ArrayList<> ();
        int org = splicharlength;
        if (totalcharlenght % 2 == 0) {
            for (int i = 0; i < (totalcharlenght / org); i++) {
                result.add ( IN.substring ( i * org, splicharlength ) );
                splicharlength += org;
            }
        } else {

        }
        return result;
    }
    /*
     * Class to convert the HEX to binary a
     * Description: Should be used towhile using loader
     * lastChanged: 2/12/2019
     *
     * */
    public static class Unit {
        public boolean ins;
        private String binary;
        private String sbinary;
        private String Hex;
        private int decimal;
        private String ones;
        private String twos;
        private boolean StartINS;
        private boolean priority;
        private boolean trace;
        private boolean Arraival;
        private List<Unit> code = new LinkedList<> ();

        public Unit(String hex) throws ERRORHANDLER {
            this.Hex = hex;
            this.binary = HexToBinary ( hex, 16 ).toString ();
            this.decimal = HexToDecimal ( hex );
            this.ones = doOnes ( binary );
            this.twos = doTwos ( binary );
            this.sbinary = HexToBinary ( hex, 3 ).toString ();
            this.StartINS = getStartINS ();
        }
        public Unit(String hex, boolean startInsStat, boolean instype) throws ERRORHANDLER {
            this.Hex = hex;
            this.binary = HexToBinary ( hex, 16 ).toString ();
            this.decimal = HexToDecimal ( hex );
            this.ones = doOnes ( binary );
            this.twos = doTwos ( binary );
            this.sbinary = HexToBinary ( hex, 3 ).toString ();
            this.StartINS = startInsStat;
            this.ins = instype;
        }

        public Unit(String hex, boolean startInsStat, boolean priority, boolean trace, boolean Arrival) throws ERRORHANDLER {
            this.Hex = hex;
            this.binary = HexToBinary ( hex, 16 ).toString ();
            this.decimal = HexToDecimal ( hex );
            this.ones = doOnes ( binary );
            this.twos = doTwos ( binary );
            this.sbinary = HexToBinary ( hex, 3 ).toString ();
            if (startInsStat) {
                this.StartINS = startInsStat;
            }
            if (priority) {
                this.priority = true;
            }
            if (trace) {
                this.trace = true;
            }
            if (Arrival) {
                this.trace = true;
            }
        }
        public Unit() {
        }

        public void setType(boolean stat) {
            this.ins = stat;
        }

        /*
         * FunctionName: Transform hext to binary (HexToBinary)
         * Description: Should be used to transform Hex to binary
         * parameters: takes in Hexcode and number of bits to convert
         * returns: returns binaty string
         * lastChanged: 03/01/2019
         *
         * */
        public Object HexToBinary(Object s, int numberofbits) throws ERRORHANDLER {
            String Binary = "0";
            try {
                if (isHEX ( s.toString () )) {
//                    long num = Long.parseLong(s.toString(), 16);
//                    Binary = Long.toBinaryString(num);
//                    int length = Binary.length();
//                    if (length != numberofbits) {
//                        // String vale = String.format((Locale) null,"%%0%dd",numberofbits);
////                    if(isNumber(Integer.toBinaryString(num))) {
//                        Binary = String.format((Locale) null, "%0" + (numberofbits) + "d", Long.parseLong(Long.toBinaryString(num)));
////                    }
////                    else
////                    {
////                        System.out.println(Integer.toBinaryString(num));
////                    }
                    String aaaaa = new BigInteger ( s.toString (), 16 ).toString ( 2 );
                    Binary = String.format ( (Locale) null, "%0" + numberofbits + "d", Long.parseLong ( aaaaa ) );
                    //Binary=aaaaa;
                }
            } catch (Exception e) {
                throw new ERRORHANDLER ( "CODEEx-" + e.getMessage () );
            }
            return Binary;
        }

        public Unit setCode(List<Unit> s, int Index) {
            this.code.addAll ( s );
            return this;
        }
        /*
         * FunctionName: Transform  binary to hex (BinaryToHex)
         * Description: Should be used to transform Binary to hex
         * parameters:takes in (object) string
         * returns: object
         * lastChanged: 03/01/2019
         *
         * */
        public Object BinaryToHex(Object s) throws ERRORHANDLER {
            String hexStr = "";
            try {
                int decimal = Integer.parseInt ( s.toString (), 2 );
                hexStr = Integer.toString ( decimal, 16 );
            } catch (Exception e) {
                throw new ERRORHANDLER ( "CODEEx-" + e.getMessage () );
            }
            return hexStr;
        }
        /*
         * FunctionName: Transform  binary to hex (BinaryToHex)
         * Description: Should be used to transform Binary to hex
         * parameters:takes in (object) string
         * returns: object
         * lastChanged: 03/01/2019
         *
         * */
        public int BinaryToDecimal(Object s) throws ERRORHANDLER {
            int decimala = 0;
            try {
                if (s.toString ().startsWith ( "1" ) && s.toString ().length () > 3) {
                    decimala = Binary2sToDecimal ( s );
                } else {
                    decimala = (int) Long.parseLong ( s.toString (), 2 );
                }
            } catch (Exception e) {
                throw new ERRORHANDLER ( "CODEEx-" + e.getMessage () );
            }
            return decimala;
        }
        /*
         * FunctionName: Add bit wise binary
         * Description: Should be used to add Binary to binary
         * parameters:takes in (object) string , String
         * returns: object
         * lastChanged: 03/01/2019
         *
         * */
        public String AddBinary(String first, String Second) {
            String result = "";
            int s = 0;
            int i = first.length () - 1, j = Second.length () - 1;
            while (i >= 0 || j >= 0 || s == 1) {
                s += ((i >= 0) ? first.charAt ( i ) - '0' : 0);
                s += ((j >= 0) ? Second.charAt ( j ) - '0' : 0);
                result = (char) (s % 2 + '0') + result;
                s = s / 2;
                i--;
                j--;
            }
            return result;
        }
        /*
         * FunctionName: Transform  binary to hex (BinaryToHex)
         * Description: Should be used to transform Binary to hex
         * parameters:takes in (object) string
         * returns: object
         * lastChanged: 03/01/2019
         *
         * */
        public int Binary2sToDecimal(Object s) throws ERRORHANDLER {
            int Decimal = 0;
            try {
                String ones = new Unit ().doOnes ( s.toString () );
                String addition = AddBinary ( ones, "1" );
                if (s.toString ().substring ( 0, 1 ).startsWith ( "1" )) {
                    Decimal = -(new Unit ().BinaryToDecimal ( addition.substring ( 1 ) ));
                } else {
                    Decimal = (new Unit ().BinaryToDecimal ( s.toString () ));
                }
            } catch (Exception e) {
                throw new ERRORHANDLER ( "CODEEx-" + e.getMessage () );
            }
            return Decimal;
        }
        /*
         * FunctionName: Transform to Decimal to binary (DecimalToBinary)
         * Description: Should be used to transform Decimal to binary
         * parameters:takes in (object) string
         * returns: object
         * lastChanged: 03/01/2019
         *
         * */
        public Object DecimalToBinary(int s, int length) throws ERRORHANDLER {
            String binary = "";
            try {
                if (s < 0) {
                    String rep = Long.toBinaryString ( s );
                    binary = rep.substring ( rep.length () - length );
                } else {
                    binary = String.format ( (Locale) null, "%0" + (length) + "d", Long.parseLong ( Long.toBinaryString ( s ) ) );
                }
            } catch (Exception e) {
                throw new ERRORHANDLER ( "CODEEx-" + e.getMessage () );
            }
            return binary;
        }
        /*
         * FunctionName: Transform to short Binary to a full length binary (bits) (shortBinarytoBinary)
         * Description: Should be used to transform Binary to an enlarged binary
         * parameters:takes in (object) string
         * returns: object
         * lastChanged: 03/01/2019
         *
         * */
        public Object shortBinarytoBinary(String s, int length) throws ERRORHANDLER {
            String binary = "";
            try {
                binary = String.format ( (Locale) null, "%0" + (length) + "d", Long.parseLong ( s ) );
            } catch (Exception e) {
                throw new ERRORHANDLER ( "CODEEx-" + e.getMessage () );
            }
            return binary;
        }
        /*
         * FunctionName: isNumber
         * Description: Should be used to test if the given string is capable of conveting it to number
         * parameters:takes in (object) string
         * returns: boolean
         * lastChanged: 03/01/2019
         *
         * */
        public boolean isNumber(String number) throws ERRORHANDLER {
            boolean status = false;
            try {
                int aa = Integer.parseInt ( number );
                status = true;
            } catch (Exception ex) {
                status = false;
                //throw new ERRORHANDLER("CODEEx-Connot change to number "+ex.getMessage());
            }
            return status;
        }
        /*
         * FunctionName: HexToDecimal
         * Description: Should be used to  transfer the Hex code to equivalent Decimal
         * parameters:takes in (object) string
         * returns: boolean
         * lastChanged: 03/01/2019
         *
         * */
        public int HexToDecimal(Object s) throws ERRORHANDLER {
            int Decimal = 0;
            try {
                if (isHEX ( s.toString () )) {
                    Decimal = (Integer.parseInt ( s.toString (), 16 ));
                }
            } catch (Exception e) {
                throw new ERRORHANDLER ( "CODEEx-" + e.getMessage () );
            }
            return Decimal;
        }
        /*
         * FunctionName: doOnes
         * Description: convert binary to ones compliment
         * parameters:takes in (object) string
         * returns: string
         * lastChanged: 03/01/2019
         *
         * */
        public String doOnes(String binvalue) throws ERRORHANDLER {
            StringBuffer buffer = new StringBuffer ();
            try {
                for (int f = 0; f < binvalue.length (); f++) {
                    char loc = binvalue.charAt ( f ) == '1' ? '0' : '1';
                    buffer.append ( loc );
                }
            } catch (Exception e) {
                throw new ERRORHANDLER ( "CODEEx-" + e.getMessage () );
            }
            return buffer.toString ();
        }
        /*
         * FunctionName: doTwos
         * Description: convert binary to twos compliment
         * parameters:takes in (object) string
         * returns: string
         * lastChanged: 03/01/2019
         *
         * */
        public String doTwos(String binvalue) throws ERRORHANDLER {
            String a1 = doOnes ( binvalue );
            StringBuffer buffer = new StringBuffer ( a1 );
            try {
                int f = 0;
                int addbit = 1;
                for (f = binvalue.length () - 1; f >= 0; f--) {
                    int lastbit = Integer.parseInt ( a1.charAt ( f ) + "" );
                    int getremm = (lastbit + addbit) % 2;
                    addbit = (addbit + lastbit) / 2;
                    buffer.replace ( f, f + 1, getremm + "" );
                    if (lastbit == 0)
                        break;
                }
            } catch (Exception e) {
                throw new ERRORHANDLER ( "CODEEx-" + e.getMessage () );
            }
            return buffer.toString ();
        }
        /*
         * FunctionName: getShortBinary
         * Description: get the short binary string using existing method
         * parameters:NA
         * returns: string binary
         * lastChanged: 03/01/2019
         *
         * */
        public String getShortBinary() {
            return sbinary;
        }
        /*
         * FunctionName: getBinary
         * Description: get the binary string
         * parameters:NA
         * returns: string binary
         * lastChanged: 03/01/2019
         *
         * */
        public String getbinary() {
            return binary;
        }
        /*
         * FunctionName: getdecimalvalue
         * Description: get the Decimal value using existing method
         * parameters:NA
         * returns: string decimal
         * lastChanged: 03/01/2019
         *
         * */
        public int getDecimal() {
            return decimal;
        }
        /*
         * FunctionName: getOnes
         * Description: get the caliculated one's binary string using existing method
         * parameters:NA
         * returns: string binary
         * lastChanged: 03/01/2019
         *
         * */
        public String getOnes() {
            return ones;
        }
        /*
         * FunctionName: getTwos
         * Description: get the caliculated two's binary string using existing method
         * parameters:NA
         * returns: string binary
         * lastChanged: 03/01/2019
         *
         * */
        public String getTwos() {
            return twos;
        }
        /*
         * FunctionName: isHEX
         * Description: checks if given code is valid HEX
         * parameters: String HEX value
         * returns: status boolean
         * lastChanged: 03/01/2019
         *
         * */
        private boolean isHEX(String hexvalue) {
            try {
                Long.parseLong ( hexvalue, 16 );
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        @Override
        public String toString() {
            return String.format ( "" + decimal );
        }
        public boolean getStartINS() {
            return StartINS;
        }
        public void setStartINS(boolean value) {
            this.StartINS = value;
        }
    }
}
