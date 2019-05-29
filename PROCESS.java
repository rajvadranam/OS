import java.util.*;

public class PROCESS {
    public boolean isswitch = false;
    public int EndTime = 0;
    public int totalExecuted = 0;
    public Map<String, Queue> ThisContext;
    public PCB thisPCB;
    public SCHEDULER thisSchedule;
    private String Process_ID;
    private int remainingQuantum;
    private boolean isActive = false;
    private boolean isBlocked = false;
    private boolean isFinished = false;
    private boolean isTerminated = false;
    private double arrivalTime;
    private double startTime = 0.0;
    private double finishTime = 0.0;
    private int burstTime = 0;
    private int IOTime = 0;
    private int programSize = 0;
    private int CPUClock = 0;
    private int instructions = 0;
    private int priority = 0;
    private int timesExecuted = 0;

    PROCESS(String JobID, int burstTime, int priority, int arrivalTime, int size, MEMORY obj) throws ERRORHANDLER {
        this.Process_ID = JobID;
        this.burstTime = burstTime;
        this.totalExecuted = 0;
        this.timesExecuted = 0;
        this.remainingQuantum = 0;
        this.isBlocked = false;
        this.IOTime = 0;
        this.CPUClock = 0;
        this.priority = priority;
        this.programSize = size;
        this.isswitch = false;
        this.instructions = 0;
        this.isActive = false;
        this.isTerminated = false;
        this.isBlocked = false;
        this.thisPCB = new PCB ( String.valueOf ( arrivalTime ), String.valueOf ( priority ), JobID, obj );
        this.thisSchedule = null;
        this.ThisContext = new LinkedHashMap<> ();

    }
    public int getTimesExecuted() {
        return this.timesExecuted;
    }
    public void setTimesExecuted(int value) {
        this.timesExecuted += value;
    }
    public MEMORY Destroy(PROCESS p, MEMORY obj) {
        List<String> myMemIndecies = new LinkedList<> ();
        try {
            for (Map.Entry<String, MEMORY.MemoryState> s :
                    obj.memoryModule.entrySet ()) {
                if (s.getValue ().getJobId ().equals ( p.getPID () )) {
                    myMemIndecies.add ( s.getKey () );
                }
            }
        } catch (Exception e) {
        }
        for (String g : myMemIndecies) {
            obj.memoryModule.put ( g, new MEMORY.MemoryState ( "XXXXXXXX" ) );
        }
        return obj;
    }
    public int getCPUClock() {
        return this.CPUClock;
    }
    public void setCPUClock(int value) {
        this.CPUClock += value;
    }
    public void setIOTime(int value) {
        this.IOTime += value;
    }
    public int getIOtime() {
        return this.IOTime;
    }


    public void setIOBlock(boolean Value, int time) {
        setIOTime ( time );
        this.isBlocked = Value;
    }

    public int getRemainingQuantum() {
        return this.remainingQuantum;
    }
    public void setRemainingQuantum(int Value) {
        this.remainingQuantum = Value;
    }
    public boolean IsActive() {
        return this.isActive;
    }

    public void SetisTerminated(boolean value) {
        this.isActive = value;
    }

    public boolean IsSwitch() {
        return this.isswitch;
    }
    public void SwitchState(Boolean val) {
        this.isswitch = val;
    }

    @Override
    public String toString() {
        return this != null ? " PID: " + Process_ID +
                " BURST: " + burstTime +
                " ExeCount: " + timesExecuted +
                " Priority: " + priority +
                "  Arrtime: " + arrivalTime +
                " StartTime: " + startTime +
                " finishTime: " + finishTime + "\n" : "NULL";
    }

    public String getPID() {
        return Process_ID;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean IsFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public boolean IsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean isblocked) {
        this.isBlocked = isblocked;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(double finishTime) {
        this.finishTime = finishTime;
    }

    public int getBurstTime() {
        return this.burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public Map<String, String> GetCompactionAddress(MEMORY Before, String Jobid) {
        return Before.getCompactedThisJobAddress ( Jobid, Before );
    }

    public class PCB {
        public Map<String, String> thismemorymap;
        public Map<String, String> thismemorymapAfterCompaction;
        public CPU.REGISTERS reg;
        public Map<String, String> regNames;
        public Map<String, CPU.OUT> thisOUt;
        String thispriority;
        MEMORY thismemory;
        String jobid;
        String arr_time;
        CPU.TRACE thisTrace;

        private Map<String, String> PC;
        private Map<String, String> IR;
        private Map<String, String> registers;
        private Map<String, String> registerName;

        public PCB(String arr_time, String thispriority, String jobid, MEMORY wholeMem) throws ERRORHANDLER {
            this.arr_time = arr_time;
            this.thispriority = thispriority;
            this.reg = new CPU ().new REGISTERS ();
            this.jobid = jobid;
            //register variable compartment
            String bit = "0000000000000000";
            registers = new HashMap<> ();
            registerName = new HashMap<> ();
            PC = new HashMap<> ();
            IR = new HashMap<> ();
            for (int i = 0; i < 8; i++) {
                String value = new LOADER.Unit ().DecimalToBinary ( i, 3 ).toString ();
                registers.put ( value, bit );
                registerName.put ( value, "r" + new LOADER.Unit ().BinaryToDecimal ( value ) );
            }
            PC.put ( "PC", bit );
            IR.put ( "IR", bit );
            this.reg.registers = registers;
            this.reg.registerName = registerName;
            this.regNames = registerName;
            //memory values
            this.thismemory = wholeMem.getThisJobsMemory ( jobid, wholeMem );
            this.thismemorymap = wholeMem.getThisJobAddress ( jobid, wholeMem );
            this.thisTrace = new CPU ().new TRACE ();
            this.thismemorymapAfterCompaction = wholeMem.getCompactedThisJobAddress ( jobid, wholeMem );
            this.thisOUt = null;
            //
        }

        @Override
        public String toString() {
            return String.valueOf ( this.thispriority );
        }
    }
}