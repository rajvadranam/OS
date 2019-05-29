import java.io.*;
import java.sql.Timestamp;
import java.util.*;

public class SYSTEM {
    //global values for lowpriority and high priority quantums
    public static int lowpriority = 100;
    public static int highpriority = 300;
    //set of completed process
    public static Set<PROCESS> DoneJobs;
    //set of progress processes
    public static Set<PROCESS> ProgressJobs;
    //the round rodin high and low declarations
    public static SCHEDULER RRh = null;
    public static SCHEDULER RRl = null;
    public static String k = "";
    //progress file writer
    public static BufferedWriter SYSWRITE;
    //variable for abnormal terminations count
    public static int AbnormalTerminations = 0;
    //variable for normal terminations for stats file
    public static int NormalTerminations = 0;
    public static int HighDegree = 0;
    public static int timesHigh = 0;
    public static int lowDegree = 0;
    public static int timesLow = 0;
    public static int SYSIdle = 0;
    public static int AbnormalTimeLost = 0;
    private static int SYSClock = 0;
    @SuppressWarnings("ThrowableNotThrown")
    //main method the SYSTEM main
    public static void main(String[] args) throws ERRORHANDLER {
        //cleaning files
        //delete all previously generated files by this system
        Reusable.deleteContentsofFile ();
        //initialize the buffered writer
        SYSWRITE = SYSTEM.PROGRAM.GetProgressFileObj ();
        //writing timestamp on top opf the file
        Object Init = "----------" + new Timestamp ( new Date ().getTime () ).toString () + "-------------";
        SYSTEM.PROGRAM.WriteToProgress ( Init, SYSWRITE );
        //getting user defined path
        String CurrentDirectory = getUserInput ( args );
        //initializing memory of 256
        MEMORY memObj = new MEMORY ( 256 );
        Object MemInit = "--> at(" + SYSClock + ") ** memory initialized  ";
        SYSTEM.PROGRAM.WriteToProgress ( MemInit, SYSWRITE );
        //loading the first job to the system to test the arrival time
        LOADER loadObj = new LOADER ( new File ( CurrentDirectory ).getAbsolutePath (), memObj );
        Object Load = "--> at(" + SYSClock + ") ** loader initialized  ";
        SYSTEM.PROGRAM.WriteToProgress ( Load, SYSWRITE );
        //logic
        boolean firsttime = true;
        boolean completed = false;
        List<List<String>> memoryWork = new LinkedList<> ();
        List<String> setvalue = new LinkedList<> ();
        List<String> leftout = new LinkedList<> ();
        List<String> jobs = new LinkedList<> ( loadObj.loaderunits.keySet () );
        ListIterator jobiterator = jobs.listIterator ();
        ListIterator setvalueiterator = null;
        DoneJobs = new HashSet<> ();
        ProgressJobs = new HashSet<> ();
        try {
            while (!completed) {
                Object ss;
                try {
                    ss = jobiterator.next ();
                } catch (Exception e) {
                    ss = jobs.get ( jobs.size () - 1 );
                }
                k = ss.toString ();
                //get all jobs that are less than system clock
                List<String> thisload = new LinkedList<> ();
                if (firsttime) {
                    if (getSYSClock () <= loadObj.loaderunits.get ( k ).get ( "arr" ).getDecimal ()) {
                        while (getSYSClock () <= loadObj.loaderunits.get ( k ).get ( "arr" ).getDecimal ()) {
                            setSYSClock ( 1 );
                            SYSIdle += 1;
                        }
                        firsttime = false;
                    }
                    Object firstjob = "--> at(" + SYSClock + ") ** a job is ready for to load into system  ";
                    SYSTEM.PROGRAM.WriteToProgress ( firstjob, SYSWRITE );
                    loadObj.setToMemoryV2 ( memObj, k );
                    Object loaded = "--> at(" + SYSClock + ") ** a job loaded to memory ";
                    SYSTEM.PROGRAM.WriteToProgress ( loaded, SYSWRITE );
                    if (loadObj.loaderunits.get ( k ).get ( "priority" ).getDecimal () == 1) {
                        //do the high priority job
                        RRh = getScheduler_HighPriority ( memObj, loadObj, RRh, k );
                    } else {
                        ////do the low priority job
                        RRl = getScheduler_LowPriority ( memObj, loadObj, RRl, k );
                    }
                }
                //multiple job scenarios
                else {
                    //load the job ids of the jobs that are less than the system time
                    thisload = GetJobsForCurrentSYStime ( loadObj, jobs, DoneJobs, ProgressJobs );
                    int ka = 0;
                    //mechanism to inscrement if no jobs are avaialable yet
                    List<Integer> arrCopy = loadObj.Arraivals;
                    while (thisload.size () == 0) {
                        setSYSClock ( 1 );
                        ka++;
                        thisload = GetJobsForCurrentSYStime ( loadObj, jobs, DoneJobs, ProgressJobs );
                        if (ka == loadObj.Arraivals.get ( 0 )) {
                            //waited for max change nothing happens
                            break;
                        }
                    }
                    //set all the loaded jobs into the memory
                    memoryWork = loadObj.setToMemorymultyV2 ( memObj, thisload, DoneJobs );
                    //those values set to memeory
                    setvalue = memoryWork.get ( 0 );
                    //those were not set
                    leftout = memoryWork.get ( 1 );
                    SCHEDULER obj = (RRh != null) ? RRh : RRl;
                    //obj.createProcesses ( loadObj, memObj, setvalue );
                    if (RRh == null) {
                        //init
                        RRh = getSchedulerRR ( RRh, highpriority, 300 );
                    } else if (RRl == null) {
                        //init
                        RRl = getSchedulerRR ( RRl, lowpriority, 100 );
                    }
                    if (RRh != null) {
                        //create process for all the jobs thata are matching eith system time requirement
                        RRh.createProcesses ( loadObj, memObj, setvalue );
                        if (SYSTEM.HighDegree < RRh.ProcessQueueH.size () + RRh.readyQ.size ()) {
                            SYSTEM.HighDegree = RRh.ProcessQueueH.size () + RRh.readyQ.size ();
                            SYSTEM.timesHigh += 1;
                            Object Degree = "--> at(" + SYSClock + ") ** HiGh Degree of MultiProgramming  { " + SYSTEM.HighDegree + " }";
                            SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                        }
                        if (RRl.ProcessQueueL.size () == 0 && RRl.readyQ.size () == 0) {
                            SYSTEM.lowpriority = 0;
                            if (RRh.ProcessQueueH.size () > 0 && SYSTEM.highpriority <= 0) {
                                SYSTEM.highpriority = 300;
                                RRh.setQuantum ( highpriority );
                            }
                        }
                    }
                    if (RRl != null) {
                        //same foe low priority values to
                        RRl.createProcesses ( loadObj, memObj, setvalue );
                        if (SYSTEM.lowDegree > RRl.ProcessQueueL.size () + RRl.readyQ.size ()) {
                            SYSTEM.lowDegree = RRl.ProcessQueueL.size () + RRl.readyQ.size ();
                            SYSTEM.timesLow += 1;
                        }
                        if (RRh.ProcessQueueH.size () == 0 && RRh.readyQ.size () == 0) {
                            SYSTEM.highpriority = 0;
                            if (RRl.ProcessQueueL.size () > 0 && SYSTEM.lowpriority <= 0) {
                                SYSTEM.lowpriority = 100;
                                RRl.setQuantum ( lowpriority );
                            }
                        }
                    }
                    if (setvalue.size () == 1) {
                        k = setvalue.get ( 0 );
                        if (jobs.contains ( k )) {
                            //executable mode
                            LogicExecute ( memObj, loadObj, RRh, RRl, k, jobs, thisload, setvalue, leftout );
                            //if all jobs are done
                            if (DoneJobs.size () == jobs.size ()) {
                                completed = true;
                                SYSTEM.highpriority = 0;
                                SYSTEM.lowpriority = 0;
                                break;
                            }
                        } else {
                            //meaning it is either completed or setvalue doesnot yeild avalid job
                            break;
                        }
                    } else if (setvalue.size () > 1) { //if set values are more than one ata  time
                        for (int i = 0; i < setvalue.size (); i++) {
                            while (obj.isEmpty () && SYSTEM.highpriority >= 0 && SYSTEM.lowpriority >= 0) {
                                k = setvalue.get ( i );
                                LogicExecute ( memObj, loadObj, RRh, RRl, k, jobs, thisload, setvalue, leftout );
                                if (DoneJobs.size () == jobs.size ()) {
                                    completed = true;
                                    SYSTEM.highpriority = 0;
                                    SYSTEM.lowpriority = 0;
                                    break;
                                }

                                if (SYSTEM.highpriority <= 0 && SYSTEM.lowpriority <= 0) {
                                    if (RRh != null) {
                                        if (RRh.ProcessQueueH.size () > 0) {
                                            SYSTEM.highpriority = 300;
                                        }
                                    }
                                    if (RRl != null) {
                                        if (RRl.ProcessQueueL.size () > 0) {
                                            SYSTEM.lowpriority = 100;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (setvalue.size () == 0) { //if no setvalue tey to do which are in queues
                        //everything you need is available in memory
                        while (SYSTEM.highpriority > 0 || SYSTEM.lowpriority > 0) {
                            LogicExecute ( memObj, loadObj, RRh, RRl, k, jobs, thisload, setvalue, leftout );
                            if (DoneJobs.size () == jobs.size ()) {
                                completed = true;
                                SYSTEM.highpriority = 0;
                                SYSTEM.lowpriority = 0;
                                break;
                            }
                            if (SYSTEM.highpriority == 300 && SYSTEM.lowpriority == 100) {
                                break;
                            }
                        }
                        if (SYSTEM.highpriority <= 0 && SYSTEM.lowpriority <= 0) {
                            if (RRh != null) {
                                if (RRh.ProcessQueueH.size () > 0) {
                                    SYSTEM.highpriority = 300;
                                }
                            }
                            if (RRl != null) {
                                if (RRl.ProcessQueueL.size () > 0) {
                                    SYSTEM.lowpriority = 100;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            SYSTEM.PROGRAM.WriteToOUTFile ( DoneJobs, memObj );
            SYSTEM.PROGRAM.WriteToStatistic ( DoneJobs, memObj );
            SYSTEM.PROGRAM.CloseProgress ( SYSWRITE );
        }
    }

    //implemets the queueing switching logic of different scenarios with compacted memory space as needed
    private static int LogicExecute(MEMORY memObj, LOADER loadObj, SCHEDULER RRh, SCHEDULER RRl, String k, List<String> jobs, List<String> thisload, List<String> setvalue, List<String> leftOut) {
        int i = 0;
        if (SYSTEM.highpriority > 0) { //high priority segment
            try {
                //meanins started with a low job
                SYSTEM.RRh = getSchedulerRR ( SYSTEM.RRh, highpriority, 300 );
                if (memObj.IsinMemory ( k )) {
                    SYSTEM.RRh.createProcess ( new PROCESS ( loadObj.loaderunits.get ( k ).get ( "jobid" ).toString (), 30,
                            Integer.parseInt ( loadObj.loaderunits.get ( k ).get ( "priority" ).toString () ),
                            Integer.parseInt ( loadObj.loaderunits.get ( k ).get ( "arr" ).toString () ),
                            loadObj.loaderunits.get ( k ).get ( "plen" ).getDecimal () * 2, memObj ) );
                    if (RRh != null) {
                        Object printq = "--> at(" + SYSClock + ") ** Current Queues  ";
                        SYSTEM.PROGRAM.WriteToProgress ( printq, SYSWRITE );
                        SYSTEM.PROGRAM.WriteToProgress ( RRh.ProcessQueueH, SYSWRITE );
                        SYSTEM.PROGRAM.WriteToProgress ( RRh.readyQ, SYSWRITE );
                        SYSTEM.PROGRAM.WriteToProgress ( RRh.blockedQ, SYSWRITE );
                        if (SYSTEM.HighDegree < RRh.ProcessQueueH.size () + RRh.readyQ.size ()) {
                            SYSTEM.HighDegree = RRh.ProcessQueueH.size () + RRh.readyQ.size ();
                            SYSTEM.timesHigh += 1;
                            Object Degree = "--> at(" + SYSClock + ") ** HiGh Degree of MultiProgramming  { " + SYSTEM.HighDegree + " }";
                            SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                        }
                    }
                }
                RRh = SYSTEM.RRh;
                RRh.ProcessQueueL.clear ();
                RRl.ProcessQueueH.clear ();
                while (RRh.isEmpty ()) {
                    PROCESS pcb = null;
                    if (IsJobInDoneJobs ( k )) {
                        pcb = RRh.NextProcess ( getSYSClock () );
                        pcb.SwitchState ( true );
                        Object currentprocess = "--> at(" + SYSClock + ") ** Current Process {" + pcb + "}";
                        SYSTEM.PROGRAM.WriteToProgress ( currentprocess, SYSWRITE );
                        //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                    } else {
                        pcb = RRh.NextProcess ( getSYSClock () );
                        Object currentprocess = "--> at(" + SYSClock + ") ** Current Process {" + pcb + "}";
                        SYSTEM.PROGRAM.WriteToProgress ( currentprocess, SYSWRITE );
                        //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                    }
                    if (pcb == null) {
                        break;
                    }
                    if (pcb.getStartTime () == 0) {
                        pcb.setStartTime ( getSYSClock () );
                    }
                    pcb.setIsActive ( true );
                    k = pcb.getPID ();
                    i = setvalue.indexOf ( k );
                    pcb.thisSchedule = RRh;
                    // pcb.thisPCB.thismemory=memObj.getThisJobsMemory ( k,memObj );
                    CPU proc = new CPU ();
                    if (highpriority > 30) {
                        if (pcb.thisPCB.thismemory.size == 0 || pcb.thisPCB.thismemory == null || memObj.TotalCompact) {
                            pcb.thisPCB.thismemory = memObj.getThisJobsMemory ( pcb.getPID (), memObj );
                            if (memObj.TotalCompact) {
                                pcb.thisPCB.thismemorymapAfterCompaction = pcb.GetCompactionAddress ( memObj, pcb.getPID () );
                            }
                        }
                        CPU.CPU_State s = proc.CPU_I ( pcb, loadObj, 30 );
                        if (s.getStatus ()) {
                            pcb.setFinishTime ( getSYSClock () + Integer.parseInt ( s.getVTU () ) );
                            pcb.setIsFinished ( true );
                        }
                        setSYSClock ( Integer.parseInt ( s.getVTU () ) );
                        pcb.setTimesExecuted ( 1 );
                        highpriority -= Integer.parseInt ( s.getVTU () );
                        RRh.setQuantum ( highpriority );
                    } else {
                        if (RRl.readyQ.size () == 0 && RRl.ProcessQueueL.size () == 0) {
                            highpriority = 300;
                            RRh.setQuantum ( highpriority );
                        } else if (highpriority < 30 && RRl.ProcessQueueL.size () > 1) {
                            highpriority = 0;
                            RRh.setQuantum ( 0 );
                            break;
                        }
                    }
                    if (highpriority <= 0) {
                        if (RRl.readyQ.size () > 0) {
                            SYSTEM.lowpriority = 100;
                            RRl.setQuantum ( lowpriority );
                            break;
                        } else {
                            SYSTEM.highpriority = 300;
                            RRh.setQuantum ( highpriority );
                        }
                    }
                    if (pcb.getBurstTime () == 0 || pcb.IsBlocked ()) {
                        if (pcb.getBurstTime () <= 0) {
                            pcb.SwitchState ( true );
                            Object currentprocess = "--> at(" + SYSClock + ") ** Context Switch  {" + pcb + "}";
                            SYSTEM.PROGRAM.WriteToProgress ( currentprocess, SYSWRITE );
                            //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                            break;
                            //switch to another job of same priority
                        } else if (pcb.IsBlocked ()) {
                            ProgressJobs.add ( pcb );
                            if (memObj.getFreeMemorySize ( memObj.memoryModule ) >= loadObj.loaderunits.get ( k ).get ( "plen" ).getDecimal () * 2) {
                                //give precidence to blocked before loading new one
                                if (RRh.blockedQ.size () == 1) {
                                    pcb = RRh.blockedQ.get ( 0 );
                                    pcb.setIsBlocked ( true );
                                    RRh.DeleteProcess ( pcb );
                                    RRh.createProcess ( pcb );
                                    Object blocked = "--> at(" + SYSClock + ") ** Blocked Process {" + pcb + "}";
                                    SYSTEM.PROGRAM.WriteToProgress ( blocked, SYSWRITE );
                                    //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                                    if (highpriority <= 0) {
                                        break;
                                    }
                                } else {
                                    Object blocked = "--> at(" + SYSClock + ") ** Blocked Process {" + pcb + "}";
                                    SYSTEM.PROGRAM.WriteToProgress ( blocked, SYSWRITE );
                                    //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                                    pcb.setIsBlocked ( true );
                                    RRh.DeleteProcess ( pcb );
                                    RRh.createProcess ( pcb );
                                    thisload = GetJobsForCurrentSYStime ( loadObj, jobs, DoneJobs, ProgressJobs );
                                    setvalue = loadObj.setToMemorymultyV2 ( memObj, thisload, DoneJobs ).get ( 0 );
                                    if (setvalue.size () > 0) {
                                        break;
                                    }
                                }
                            } else {
                                if (RRh.quantum > 0 && RRh.isEmpty ()) {
                                } else {
                                    //switching to earliear process
                                    pcb.setIsBlocked ( true );
                                    RRh.DeleteProcess ( pcb );
                                    RRh.createProcess ( pcb );
                                    thisload = GetJobsForCurrentSYStime ( loadObj, jobs, DoneJobs, ProgressJobs );
                                    setvalue = loadObj.setToMemorymultyV2 ( memObj, thisload, DoneJobs ).get ( 0 );
                                    if (setvalue.size () > 0) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (pcb.IsFinished ()) {
                        if (SYSTEM.HighDegree < RRh.ProcessQueueH.size () + RRh.readyQ.size ()) {
                            SYSTEM.HighDegree = RRh.ProcessQueueH.size () + RRh.readyQ.size ();
                            SYSTEM.timesHigh += 1;
                            Object Degree = "--> at(" + SYSClock + ") ** HiGh Degree of MultiProgramming  { " + SYSTEM.HighDegree + " }";
                            SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                        }
                        pcb.setIsActive ( false );
                        Object finishd = "--> at(" + SYSClock + ") ** Finished Process {" + pcb + "}";
                        SYSTEM.PROGRAM.WriteToProgress ( finishd, SYSWRITE );
                        //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                        Object TraceandOut = "--> at(" + SYSClock + ") ** Trace and Out Process {" + pcb + "}";
                        SYSTEM.PROGRAM.WriteToProgress ( TraceandOut, SYSWRITE );
                        SYSTEM.PROGRAM.WriteToProgress ( pcb.thisPCB.thisOUt, SYSWRITE );
                        Object Linebrake = "--> -----------------------------------------";
                        SYSTEM.PROGRAM.WriteToProgress ( Linebrake, SYSWRITE );
                        RRh.DeletefromAllQueues ( pcb );
                        RRh.RunProcess= null;
                        pcb.Destroy ( pcb, memObj );
                        if (memObj.Compact ( memObj )) {
                            pcb.GetCompactionAddress ( memObj, pcb.getPID () );
                        }
                        DoneJobs.add ( pcb );
                        if (pcb.EndTime == 0) {
                            pcb.EndTime = getSYSClock ();
                        }
                        loadObj.setToMemorymultyV2 ( memObj, leftOut, DoneJobs );
                        RRh.createProcesses ( loadObj, memObj, leftOut );
                        if (ProgressJobs.contains ( pcb )) {
                            ProgressJobs.remove ( pcb );
                        }
                        break;
                    }
                }
                if (!RRh.isEmpty ()) {
                    if (SYSTEM.RRl != null) {
                        if (SYSTEM.RRl.RunProcess != null || SYSTEM.RRl.readyQ.size () > 0 || SYSTEM.RRl.ProcessQueueL.size () > 0) {
                            SYSTEM.highpriority = 0;
                            SYSTEM.lowpriority = 100;
                            RRl.setQuantum ( lowpriority );
                        } else {
                            highpriority--;
                        }
                    }
                }
            } catch (Exception e) {
                //liveprocess is null
            }
        } else if (SYSTEM.lowpriority >= 0) { //low priority segment
            try {
                if (SYSTEM.lowpriority >= 0) {
                    if (SYSTEM.RRl == null) {
                        SYSTEM.RRl = getSchedulerRR ( SYSTEM.RRl, lowpriority, 100 );
                        Object loaded1 = "--> at(" + SYSClock + ") ** Switched scheduler ";
                        SYSTEM.PROGRAM.WriteToProgress ( loaded1, SYSWRITE );
                        if (memObj.IsinMemory ( k )) {
                            SYSTEM.RRl.createProcess ( new PROCESS ( loadObj.loaderunits.get ( k ).get ( "jobid" ).toString (), 30,
                                    Integer.parseInt ( loadObj.loaderunits.get ( k ).get ( "priority" ).toString () ),
                                    Integer.parseInt ( loadObj.loaderunits.get ( k ).get ( "arr" ).toString () ),
                                    loadObj.loaderunits.get ( k ).get ( "plen" ).getDecimal () * 2, memObj ) );
                            if (SYSTEM.lowDegree > RRl.ProcessQueueL.size () + RRl.readyQ.size ()) {
                                SYSTEM.lowDegree = RRl.ProcessQueueL.size () + RRl.readyQ.size ();
                                SYSTEM.timesLow += 1;
                                Object Degree = "--> at(" + SYSClock + ") ** Low Degree of MultiProgramming  { " + SYSTEM.lowDegree + " }";
                                SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                            }
                        }
                    }
                    //preprocess
                    if (SYSTEM.lowDegree > RRl.ProcessQueueL.size () + RRl.readyQ.size ()) {
                        SYSTEM.lowDegree = RRl.ProcessQueueL.size () + RRl.readyQ.size ();
                        SYSTEM.timesLow += 1;
                        Object Degree = "--> at(" + SYSClock + ") ** Low Degree of MultiProgramming  { " + SYSTEM.lowDegree + " }";
                        SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                    }
                    RRl.ProcessQueueH.clear ();
                    RRh.ProcessQueueL.clear ();
                    if (RRl != null) {
                        Object printq = "--> at(" + SYSClock + ") ** Current Queues  ";
                        SYSTEM.PROGRAM.WriteToProgress ( printq, SYSWRITE );
                        SYSTEM.PROGRAM.WriteToProgress ( RRl.ProcessQueueL, SYSWRITE );
                        SYSTEM.PROGRAM.WriteToProgress ( RRl.readyQ, SYSWRITE );
                        SYSTEM.PROGRAM.WriteToProgress ( RRl.blockedQ, SYSWRITE );
                        if (SYSTEM.lowDegree > RRl.ProcessQueueL.size () + RRl.readyQ.size ()) {
                            SYSTEM.lowDegree = RRl.ProcessQueueL.size () + RRl.readyQ.size ();
                            SYSTEM.timesLow += 1;
                            Object Degree = "--> at(" + SYSClock + ") ** Low Degree of MultiProgramming  { " + SYSTEM.lowDegree + " }";
                            SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                        }
                    }
                    while (RRl.isEmpty ()) {
                        PROCESS pcb = SYSTEM.RRl.NextProcess ( getSYSClock () );
                        Object currentprocess = "--> at(" + SYSClock + ") ** Current Process {" + pcb + "}";
                        SYSTEM.PROGRAM.WriteToProgress ( currentprocess, SYSWRITE );
                        //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                        if (pcb == null) {
                            break;
                        }
                        pcb.setIsActive ( true );
                         k = pcb.getPID ();
                        i = setvalue.indexOf ( k );

                        pcb.thisSchedule = SYSTEM.RRl;
                        if (pcb.getArrivalTime () == 0) {
                            pcb.setArrivalTime ( getSYSClock () );
                        }
                        //pcb.thisPCB.thismemory = memObj.getThisJobsMemory ( k, memObj );
                        CPU proc = new CPU ();
                        if (lowpriority > 30) {
                            if (pcb.thisPCB.thismemory.size == 0 || pcb.thisPCB.thismemory == null || memObj.TotalCompact) {
                                pcb.thisPCB.thismemory = memObj.getThisJobsMemory ( pcb.getPID (), memObj );
                                if (memObj.TotalCompact) {
                                    pcb.thisPCB.thismemorymapAfterCompaction = pcb.GetCompactionAddress ( memObj, pcb.getPID () );
                                }
                            }
                            CPU.CPU_State s = proc.CPU_I ( pcb, loadObj, 30 );
                            if (s.getStatus ()) {
                                pcb.setFinishTime ( getSYSClock () + Integer.parseInt ( s.getVTU () ) );
                                pcb.setIsFinished ( true );
                            }
                            pcb.setTimesExecuted ( 1 );
                            setSYSClock ( Integer.parseInt ( s.getVTU () ) );
                            lowpriority -= Integer.parseInt ( s.getVTU () );
                            RRl.setQuantum ( lowpriority );
                        } else {

                            if (RRh != null) {
                                if (RRh.readyQ.size () == 0 && RRh.ProcessQueueH.size () == 0) {
                                    lowpriority = 100;
                                    RRl.setQuantum ( lowpriority );
                                }
                            }
                        }
                        if (lowpriority <= 0) {
                            if (RRh.readyQ.size () > 0) {
                                SYSTEM.highpriority = 300;
                                RRh.setQuantum ( highpriority );
                                break;
                            } else {
                                SYSTEM.lowpriority = 100;
                                RRl.setQuantum ( lowpriority );
                            }
                        }
                        if (pcb.getBurstTime () == 0 || pcb.IsBlocked ()) {
                            if (pcb.getBurstTime () <= 0) {
                                pcb.SwitchState ( true );
                                if (highpriority <= 0 && RRh.readyQ.size () == 0 && RRh.ProcessQueueH.size () == 0) {
                                    //meaning only job in the system
                                    setSYSClock ( 10 );
                                    pcb.SwitchState ( false );
                                    pcb.setBurstTime ( 30 );
                                } else {
                                    Object switchstate = "--> at(" + SYSClock + ") ** Context switch on burst elapsed {" + pcb + "}";
                                    SYSTEM.PROGRAM.WriteToProgress ( switchstate, SYSWRITE );
                                    //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                                    break;
                                }
                                //switch to another job of same priority
                            } else if (pcb.IsBlocked ()) {
                                ProgressJobs.add ( pcb );
                                if (memObj.getFreeMemorySize ( memObj.memoryModule ) >= loadObj.loaderunits.get ( k ).get ( "plen" ).getDecimal () * 2) {
                                    //give precidence to blocked before loading new one
                                    if (RRl.blockedQ.size () == 1) {
                                        pcb = RRl.blockedQ.get ( 0 );
                                        pcb.setIsBlocked ( true );
                                        RRl.DeleteProcess ( pcb );
                                        RRl.createProcess ( pcb );
                                        if (lowpriority <= 0 || RRl.readyQ.size () > 1) {
                                            pcb.SwitchState ( true );
                                        }
                                    } else {
                                        if (RRl.blockedQ.size () > 1) {
                                            Object blo = "--> at(" + SYSClock + ") **  is blocked {" + pcb + "}";
                                            SYSTEM.PROGRAM.WriteToProgress ( blo, SYSWRITE );
                                            //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                                            pcb = RRl.blockedQ.get ( 0 );
                                            pcb.setIsBlocked ( true );
                                            RRl.DeleteProcess ( pcb );
                                            RRl.createProcess ( pcb );
                                            thisload = GetJobsForCurrentSYStime ( loadObj, jobs, DoneJobs, ProgressJobs );
                                            setvalue = loadObj.setToMemorymultyV2 ( memObj, thisload, DoneJobs ).get ( 0 );
                                            if (setvalue.size () > 0) {
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    if (RRl.quantum > 0 && RRl.isEmpty ()) {
                                    } else {
                                        //switching to earliear process
                                        pcb.SwitchState ( true );
                                        RRl.DeleteProcess ( pcb );
                                        RRl.createProcess ( pcb );
                                        thisload = GetJobsForCurrentSYStime ( loadObj, jobs, DoneJobs, ProgressJobs );
                                        setvalue = loadObj.setToMemorymultyV2 ( memObj, thisload, DoneJobs ).get ( 0 );
                                        if (setvalue.size () > 0) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (pcb.IsFinished ()) {
                            if (SYSTEM.lowDegree > RRl.ProcessQueueL.size () + RRl.readyQ.size ()) {
                                SYSTEM.lowDegree = RRl.ProcessQueueL.size () + RRl.readyQ.size ();
                                SYSTEM.timesLow += 1;
                                Object Degree = "--> at(" + SYSClock + ") ** Low Degree of MultiProgramming  { " + SYSTEM.lowDegree + " }";
                                SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                            }
                            Object finishd = "--> at(" + SYSClock + ") ** Finished Process {" + pcb + "}";
                            SYSTEM.PROGRAM.WriteToProgress ( finishd, SYSWRITE );
                            //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                            Object TraceandOut = "--> at(" + SYSClock + ") ** Trace and Out Process {" + pcb + "}";
                            SYSTEM.PROGRAM.WriteToProgress ( TraceandOut, SYSWRITE );
                            SYSTEM.PROGRAM.WriteToProgress ( pcb.thisPCB.thisOUt, SYSWRITE );
                            Object Linebrake = "--> -----------------------------------------";
                            SYSTEM.PROGRAM.WriteToProgress ( Linebrake, SYSWRITE );
                            RRl.DeletefromAllQueues ( pcb );
                            pcb.setIsActive ( false );
                            RRl.RunProcess = null;
                            pcb.Destroy ( pcb, memObj );
                            if (memObj.Compact ( memObj )) {
                                pcb.GetCompactionAddress ( memObj, pcb.getPID () );
                            }
                            DoneJobs.add ( pcb );
                            if (pcb.EndTime == 0) {
                                pcb.EndTime = getSYSClock ();
                            }
                            loadObj.setToMemorymultyV2 ( memObj, leftOut, DoneJobs );
                            RRl.createProcesses ( loadObj, memObj, leftOut );
                            if (ProgressJobs.contains ( pcb )) {
                                ProgressJobs.remove ( pcb );
                            }
                            if (RRl.readyQ.size () == 0 && RRl.ProcessQueueL.size () == 0) {
                                SYSTEM.lowpriority = 0;
                            }
                            break;
                        }
                    }
                    if (!RRl.isEmpty ()) {
                        if (SYSTEM.RRh != null) {
                            if (SYSTEM.RRh.RunProcess != null || SYSTEM.RRh.readyQ.size () > 0 || SYSTEM.RRh.ProcessQueueH.size () > 0) {
                                SYSTEM.highpriority = 0;
                                SYSTEM.lowpriority = 100;
                                RRh.setQuantum ( lowpriority );
                            } else {
                                RRl.setQuantum ( lowpriority );
                            }
                        }
                    }
                }
                if (lowpriority <= 0 && RRl.readyQ.size () >= 0) {
                    if (RRh.readyQ.size () == 0 && RRh.ProcessQueueH.size () == 0) {
                        RRl.setQuantum ( lowpriority );
                    } else {
                        SYSTEM.highpriority = 300;
                        RRh.setQuantum ( highpriority );
                    }
                } else if (lowpriority > 0 && (RRl.readyQ.size () == 0 && RRl.ProcessQueueL.size () == 0)) {
                    SYSTEM.highpriority = 300;
                    RRh.setQuantum ( highpriority );
                }
            } catch (Exception e) {
            }
        } else if (lowpriority < 0 || RRl.readyQ.size () > 0) {
            if (RRh.readyQ.size () == 0) {
                SYSTEM.lowpriority = 100;
                RRl.setQuantum ( lowpriority );
            } else {
                SYSTEM.highpriority = 300;
                RRh.setQuantum ( highpriority );
            }
        }
        return i;
    }
    //test to see if the given job is in done jobs
    private static boolean IsJobInDoneJobs(String k) {
        boolean alreadycompleted = false;
        for (PROCESS p : DoneJobs) {
            if (p.getPID ().equals ( k )) {
                alreadycompleted = true;
            }
        }
        return alreadycompleted;
    }
    //the logic to run the loaded job atleast once
    private static SCHEDULER getScheduler_LowPriority(MEMORY memObj, LOADER loadObj, SCHEDULER RRl, String k) throws ERRORHANDLER {
        RRl = getSchedulerRR ( RRl, lowpriority, 100 );
        Object loaded1 = "--> at(" + SYSClock + ") ** Initialized scheduler ";
        SYSTEM.PROGRAM.WriteToProgress ( loaded1, SYSWRITE );
        RRl.createProcess ( new PROCESS ( loadObj.loaderunits.get ( k ).get ( "jobid" ).toString (), 30,
                Integer.parseInt ( loadObj.loaderunits.get ( k ).get ( "priority" ).toString () ),
                Integer.parseInt ( loadObj.loaderunits.get ( k ).get ( "arr" ).toString () ),
                loadObj.loaderunits.get ( k ).get ( "plen" ).getDecimal () * 2, memObj ) );
        try {
            if (RRl != null) {
                Object printq = "--> at(" + SYSClock + ") ** Current Queues  ";
                SYSTEM.PROGRAM.WriteToProgress ( printq, SYSWRITE );
                SYSTEM.PROGRAM.WriteToProgress ( RRl.ProcessQueueL, SYSWRITE );
                SYSTEM.PROGRAM.WriteToProgress ( RRl.readyQ, SYSWRITE );
                SYSTEM.PROGRAM.WriteToProgress ( RRl.blockedQ, SYSWRITE );
                if (SYSTEM.lowDegree > RRl.ProcessQueueL.size () + RRl.readyQ.size ()) {
                    SYSTEM.lowDegree = RRl.ProcessQueueL.size () + RRl.readyQ.size ();
                    SYSTEM.timesLow += 1;
                    Object Degree = "--> at(" + SYSClock + ") ** Low Degree of MultiProgramming  { " + SYSTEM.lowDegree + " }";
                    SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                }
            }
            while (RRl.isEmpty ()) {
                PROCESS pcb = RRl.NextProcess ( getSYSClock () );
                Object currentprocess = "--> at(" + SYSClock + ") ** Current Process {" + pcb + "}";
                SYSTEM.PROGRAM.WriteToProgress ( currentprocess, SYSWRITE );
                //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                pcb.setIsActive ( true );
                k = pcb.getPID ();
                if (pcb.getArrivalTime () == 0) {
                    pcb.setArrivalTime ( getSYSClock () );
                }
                pcb.thisSchedule = RRl;
                //pcb.thisPCB.thismemory=memObj.getThisJobsMemory ( k,memObj );
                CPU proc = new CPU ();
                if (lowpriority > 30) {
                    CPU.CPU_State s = proc.CPU_I ( pcb, loadObj, 30 );
                    if (s.getStatus ()) {
                        if (pcb.getStartTime () == 0) {
                            pcb.setStartTime ( getSYSClock () );
                        }
                        pcb.setFinishTime ( getSYSClock () + Integer.parseInt ( s.getVTU () ) );
                        pcb.setIsFinished ( true );
                    }
                    setSYSClock ( Integer.parseInt ( s.getVTU () ) );
                    pcb.setTimesExecuted ( 1 );
                    lowpriority -= Integer.parseInt ( s.getVTU () );
                    RRl.setQuantum ( lowpriority );
                } else {
                    if (RRh == null) {
                        lowpriority = 100;
                        RRl.setQuantum ( lowpriority );
                    }
                }
                if (pcb.getBurstTime () == 0 || pcb.IsBlocked ()) {
                    if (pcb.getBurstTime () == 0) {
                        if (RRh != null) {
                            if (RRh.ProcessQueueH.size () > 0 || RRh.readyQ.size () > 0) {
                                break;
                            }
                        } else if (lowpriority <= 0) {
                            if (RRh == null) {
                                lowpriority = 100;
                                RRl.setQuantum ( lowpriority );
                            }
                        }
                        //switch to another job of same priority
                    } else if (pcb.IsBlocked ()) {
                        ProgressJobs.add ( pcb );
                        if (memObj.getFreeMemorySize ( memObj.memoryModule ) >= loadObj.loaderunits.get ( String.valueOf ( Integer.parseInt ( k ) + 1 ) ).get ( "plen" ).getDecimal () * 2) {
                            //increase 10 VTU to set the job into one of the priority queues
                            //break;
                        } else {
                            setSYSClock ( 10 );
                            pcb = RRl.blockedQ.get ( 0 );
                            RRl.DeleteProcess ( pcb );
                            RRl.createProcess ( pcb );
                        }
                    }
                } else if (pcb.IsFinished ()) {
                    if (SYSTEM.lowDegree > RRl.ProcessQueueL.size () + RRl.readyQ.size ()) {
                        SYSTEM.lowDegree = RRl.ProcessQueueL.size () + RRl.readyQ.size ();
                        SYSTEM.timesLow += 1;
                        Object Degree = "--> at(" + SYSClock + ") ** Low Degree of MultiProgramming  { " + SYSTEM.lowDegree + " }";
                        SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                    }
                    Object Degree = "--> at(" + SYSClock + ") ** Low Degree of MultiProgramming  { " + SYSTEM.lowDegree + " }";
                    SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                    Object finishd = "--> at(" + SYSClock + ") ** Finished Process {" + pcb + "}";
                    SYSTEM.PROGRAM.WriteToProgress ( finishd, SYSWRITE );
                    //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                    Object TraceandOut = "--> at(" + SYSClock + ") ** Trace and Out Process {" + pcb + "}";
                    SYSTEM.PROGRAM.WriteToProgress ( TraceandOut, SYSWRITE );
                    SYSTEM.PROGRAM.WriteToProgress ( pcb.thisPCB.thisOUt, SYSWRITE );
                    Object memOryDump = "--> at(" + SYSClock + ") ** MEMORY DUMP for  {" + pcb + "}";
                    SYSTEM.PROGRAM.WriteToProgress ( pcb.thisPCB.thismemory.memoryModule, SYSWRITE );
                    Object Linebrake = "--> -----------------------------------------";
                    SYSTEM.PROGRAM.WriteToProgress ( Linebrake, SYSWRITE );
                    RRl.DeletefromAllQueues ( pcb );
                    pcb.setIsActive ( false );
                    RRl.RunProcess = null;
                    pcb.Destroy ( pcb, memObj );
                    if (memObj.Compact ( memObj )) {
                        pcb.GetCompactionAddress ( memObj, pcb.getPID () );
                    }
                    if (pcb.EndTime == 0) {
                        pcb.EndTime = getSYSClock ();
                    }
                    DoneJobs.add ( pcb );
                    if (ProgressJobs.contains ( pcb )) {
                        ProgressJobs.remove ( pcb );
                    }
                    break;
                }
            }
        } catch (Exception e) {
        }
        return RRl;
    }
    //logic to run the high priority one atleast one time
    private static SCHEDULER getScheduler_HighPriority(MEMORY memObj, LOADER loadObj, SCHEDULER RRh, String k) throws ERRORHANDLER {
        RRh = getSchedulerRR ( RRh, highpriority, 300 );
        Object loaded1 = "--> at(" + SYSClock + ") ** Initialized scheduler ";
        SYSTEM.PROGRAM.WriteToProgress ( loaded1, SYSWRITE );
        RRh.createProcess ( new PROCESS ( loadObj.loaderunits.get ( k ).get ( "jobid" ).toString (), 30,
                Integer.parseInt ( loadObj.loaderunits.get ( k ).get ( "priority" ).toString () ),
                Integer.parseInt ( loadObj.loaderunits.get ( k ).get ( "arr" ).toString () ),
                loadObj.loaderunits.get ( k ).get ( "plen" ).getDecimal () * 2, memObj ) );
        Object jobcreated = "--> at(" + SYSClock + ") ** Created a Job ";
        SYSTEM.PROGRAM.WriteToProgress ( jobcreated, SYSWRITE );
        try {
            if (RRh != null) {
                Object printq = "--> at(" + SYSClock + ") ** Current Queues  ";
                SYSTEM.PROGRAM.WriteToProgress ( printq, SYSWRITE );
                SYSTEM.PROGRAM.WriteToProgress ( RRh.ProcessQueueH, SYSWRITE );
                SYSTEM.PROGRAM.WriteToProgress ( RRh.readyQ, SYSWRITE );
                SYSTEM.PROGRAM.WriteToProgress ( RRh.blockedQ, SYSWRITE );
                if (SYSTEM.HighDegree < RRh.ProcessQueueH.size () + RRh.readyQ.size ()) {
                    SYSTEM.HighDegree = RRh.ProcessQueueH.size () + RRh.readyQ.size ();
                    SYSTEM.timesHigh += 1;
                    Object Degree = "--> at(" + SYSClock + ") ** HiGh Degree of MultiProgramming  { " + SYSTEM.HighDegree + " }";
                    SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                }
            }
            while (RRh.isEmpty ()) {
                if (SYSTEM.HighDegree < RRh.ProcessQueueH.size () + RRh.readyQ.size ()) {
                    SYSTEM.HighDegree = RRh.ProcessQueueH.size () + RRh.readyQ.size ();
                    SYSTEM.timesHigh += 1;
                }
                PROCESS pcb = RRh.NextProcess ( getSYSClock () );
                Object currentprocess = "--> at(" + SYSClock + ") ** Current Process {" + pcb + "}";
                SYSTEM.PROGRAM.WriteToProgress ( currentprocess, SYSWRITE );
                ////SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                pcb.setIsActive ( true );
                k = pcb.getPID ();
                if (pcb.getArrivalTime () == 0) {
                    pcb.setArrivalTime ( getSYSClock () );
                }
                pcb.thisSchedule = RRh;
                //pcb.thisPCB.thismemory=memObj.getThisJobsMemory ( k,memObj );
                CPU proc = new CPU ();
                CPU.CPU_State s = proc.CPU_I ( pcb, loadObj, 30 );
                if (s.getStatus ()) {
                    if (pcb.getStartTime () == 0) {
                        pcb.setStartTime ( getSYSClock () );
                    }
                    pcb.setFinishTime ( getSYSClock () + Integer.parseInt ( s.getVTU () ) );
                    pcb.setIsFinished ( true );
                }
                setSYSClock ( Integer.parseInt ( s.getVTU () ) );
                pcb.setTimesExecuted ( 1 );
                highpriority -= Integer.parseInt ( s.getVTU () );
                RRh = getSchedulerRR ( RRh, highpriority, 300 );
                RRh.setQuantum ( highpriority );
                if (pcb.getBurstTime () == 0 || pcb.IsBlocked ()) {
                    if (pcb.getBurstTime () == 0) {
                        break;
                        //switch to another job of same priority
                    } else if (pcb.IsBlocked ()) {
                        ProgressJobs.add ( pcb );
                        if (memObj.getFreeMemorySize ( memObj.memoryModule ) >= loadObj.loaderunits.get ( String.valueOf ( Integer.parseInt ( k ) + 1 ) ).get ( "plen" ).getDecimal () * 2) {
                            //increase 10 VTU to set the job into one of the priority queues
                            //first job continue with execution
                        } else {
                            Object blocked = "--> at(" + SYSClock + ") ** Blocked Process {" + pcb + "}";
                            SYSTEM.PROGRAM.WriteToProgress ( blocked, SYSWRITE );
                            //SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                            setSYSClock ( 10 );
                            pcb = RRh.blockedQ.get ( 0 );
                            RRh.DeleteProcess ( pcb );
                            RRh.createProcess ( pcb );
                        }
                    }
                } else if (pcb.IsFinished ()) {
                    if (SYSTEM.HighDegree < RRh.ProcessQueueH.size () + RRh.readyQ.size ()) {
                        SYSTEM.HighDegree = RRh.ProcessQueueH.size () + RRh.readyQ.size ();
                        SYSTEM.timesHigh += 1;
                        Object Degree = "--> at(" + SYSClock + ") ** HiGh Degree of MultiProgramming  { " + SYSTEM.HighDegree + " }";
                        SYSTEM.PROGRAM.WriteToProgress ( Degree, SYSWRITE );
                    }
                    Object finishd = "--> at(" + SYSClock + ") ** Finished Process {" + pcb + "}";
                    SYSTEM.PROGRAM.WriteToProgress ( finishd, SYSWRITE );
                    ////SYSTEM.PROGRAM.WriteToProgress ( pcb, SYSWRITE );
                    Object TraceandOut = "--> at(" + SYSClock + ") ** Trace and Out Process {" + pcb + "}";
                    SYSTEM.PROGRAM.WriteToProgress ( TraceandOut, SYSWRITE );
                    SYSTEM.PROGRAM.WriteToProgress ( pcb.thisPCB.thisOUt, SYSWRITE );
                    Object Linebrake = "--> -----------------------------------------";
                    SYSTEM.PROGRAM.WriteToProgress ( Linebrake, SYSWRITE );
                    RRh.DeletefromAllQueues ( pcb );
                    pcb.setIsActive ( false );
                    RRh.RunProcess = null;
                    pcb.Destroy ( pcb, memObj );
                    if (memObj.Compact ( memObj )) {
                        pcb.GetCompactionAddress ( memObj, pcb.getPID () );
                    }
                    DoneJobs.add ( pcb );
                    if (pcb.EndTime == 0) {
                        pcb.EndTime = getSYSClock ();
                    }
                    if (ProgressJobs.contains ( pcb )) {
                        ProgressJobs.remove ( pcb );
                    }
                    break;
                }
            }
        } catch (Exception e) {
            try {
                SYSWRITE.close ();
            } catch (Exception ae) {
            }
        }
        return RRh;
    }
    //if either of the low or high have became zero this serves as provisioning perameter
    private static SCHEDULER getSchedulerRR(SCHEDULER RRh, int highpriority, int i) {
        if (RRh != null) {
            if (highpriority <= 0) {
                //re-provisioning
                if (i == 300) {
                    SYSTEM.highpriority = i;
                } else {
                    SYSTEM.lowpriority = i;
                }
                RRh.setQuantum ( i );
            } else {
            }
        } else {
            RRh = new SCHEDULER ( highpriority );
        }
        return RRh;
    }
    //get job ids from loader for curtrent systime
    private static List<String> GetJobsForCurrentSYStime(LOADER loadObj, List<String> jobs, Set<PROCESS> ku, Set<PROCESS> j) {
        List<String> thisload1 = new LinkedList<> ( jobs );
        List<String> thisload = new LinkedList<> ();
        for (String s : jobs) {
            if (getSYSClock () >= loadObj.loaderunits.get ( s ).get ( "arr" ).getDecimal ()) {
                thisload.add ( String.valueOf ( loadObj.loaderunits.get ( s ).get ( "jobid" ) ) );
            }
        }
        for (PROCESS ll : ku) {
            thisload.remove ( ll.getPID () );
        }
        for (PROCESS ll : j) {
            thisload.remove ( ll.getPID () );
        }
        return thisload;
    }
    //property to get sysy clock
    public static int getSYSClock() {
        return SYSClock;
    }
    //property to set sysytem clock
    public static void setSYSClock(int value) {
        SYSClock += value;
    }
    private static String getUserInput(String[] arg) {
        String CurrentDirectory = "";
        try {
            if (arg.equals ( "" )) {
                CurrentDirectory = "." + File.separator;
            } else {
                CurrentDirectory = arg[0];
            }
        } catch (Exception e) {
            //Assuming user didnot provide any info or an invalid entry,so always check in current directory
            CurrentDirectory = "." + File.separator;
        }
        return CurrentDirectory;
    }

    //REusable methods that can be used in SYSTEM
    public static class Reusable {
        /*
         * FunctionName: getProgramFiles
         * Description: Should be called when ever user system is starting to read loader files names "program" in current directory (a prereq) as needed
         * parameters: File object
         * returns: List of filenames
         * lastChanged: 03/15/2019
         *
         * */
        public static List<String> getProgramFiles(File folder) {
            List<String> myfiles = new LinkedList<> ();
            //reading all program files in directory and subdirectory
            for (final File fileEntry : folder.listFiles ()) {
                if (fileEntry.isDirectory ()) {
                    getProgramFiles ( fileEntry );
                } else {
                    if (fileEntry.getName ().contains ( ".txt" ))
                        myfiles.add ( fileEntry.getName () );
                }
            }
            return myfiles;
        }

        /*
         * FunctionName: deleteContentsofFile
         * Description: Should be called when ever user system is starting as a prereq as needed
         * parameters: NA
         * returns: boolean
         * lastChanged: 03/15/2019
         *
         * */
        public static boolean deleteContentsofFile() {
            try {
                File fout1 = new File ( "." + File.separator + "OutPut.txt" );
//                File fout2 = new File("." + File.separator + "Trace.txt");
                //    File fout3 = new File("." + File.separator + "DUMP.txt");
                for (File f : new ArrayList<File> ( Arrays.asList ( fout1 ) ))
                    if (f.exists ()) {
                        RandomAccessFile raf = new RandomAccessFile ( f, "rw" );
                        raf.setLength ( 0 );
                        raf.close ();
                        f.delete ();
                    }
                for (File file : new File ( "." + File.separator ).listFiles ()) {
                    if (file.getName ().contains ( "Trace" ) || file.getName ().contains ( "DUMP" ) || file.getName ().contains ( "OutPut" ) || file.getName ().contains ( "Progress" ) || file.getName ().contains ( "OUT_Phase" )) {
                        file.delete ();
                    } else {
                        //nothing
                    }
                }
            } catch (Exception e) {
                System.out.println ( "Error Deleting the old output files" );
            }
            return true;
        }
        //if already header in file?
        public static boolean containTraceHeaders(File fout) {
            boolean status = false;
            try {
                BufferedReader br = new BufferedReader ( new InputStreamReader ( new FileInputStream ( fout ) ) );
                String Line = "";
                while ((Line = br.readLine ()) != null) {
                    if (Line.contains ( "PC" )) {
                        status = true;
                        break;
                    } else {
                        status = false;
                    }
                }
            } catch (Exception e) {
            }
            return status;
        }

        /*
         * FunctionName: containOutputHeaders
         * Description: get the contents of OutPut file and check headers
         * parameters:  takes in FIle object
         * returns: boolean
         * lastChanged: 03/01/2019
         *
         * */
        public static boolean containOutputHeaders(File fout) {
            boolean status = false;
            try {
                BufferedReader br = new BufferedReader ( new InputStreamReader ( new FileInputStream ( fout ) ) );
                String Line = "";
                while ((Line = br.readLine ()) != null) {
                    if (Line.contains ( "J" )) {
                        status = true;
                        break;
                    } else {
                        status = false;
                    }
                }
            } catch (Exception e) {
            }
            return status;
        }
    }

    public static class PROGRAM {
        /*
         * FunctionName: ReadInput
         * Description: Should be called when ever user inputs is needed
         * parameters: NA
         * returns: String
         * lastChanged: 03/15/2019
         *
         * */
        public static String ReadInput(String jobID) {
            String sa = "";
            BufferedReader br = null;
            try {
                System.out.println ( jobID );
                br = new BufferedReader ( new InputStreamReader ( System.in ) );
                StringTokenizer st = new StringTokenizer ( br.readLine () );
                while (st != null && st.hasMoreElements ()) {
                    sa += st.nextToken ();
                }
            } catch (IOException e) {
                e.printStackTrace ();
            }
            return sa;
        }

        /*
         * FunctionName: HALt
         * Description: Should be called when ever system halt needed
         * parameters: NA
         * returns: boolean
         * lastChanged: 03/15/2019
         *
         * */
        public static boolean Halt() {
            //System.exit(0);
            return true;
        }

        /*
         * FunctionName: WriteToOut
         * Description: Should be called when ever stdout is needed
         * parameters: NA
         * returns: boolean
         * lastChanged: 03/15/2019
         *
         * */
        public static boolean WriteToOut(String outVar) {
            System.out.println ( outVar );
            return true;
        }

        /*
        writing the Trace file

         */
        public static boolean WriteToTraceFile(String JobiD, Map<String, CPU.TRACEUNIT> aaa, MEMORY mem) throws ERRORHANDLER {
            boolean status = false;
            String ts = SYSTEM.PROGRAM.santizeFilename ( "Trace_" + JobiD + "_" + new Timestamp ( new Date ().getTime () ).toString () + ".txt" );
            File fout = new File ( ts );
            try {
                fout.createNewFile ();
            } catch (IOException e) {
                e.printStackTrace ();
            }
            try {
                FileOutputStream fos = new FileOutputStream ( fout, true );
                BufferedWriter bw = new BufferedWriter ( new OutputStreamWriter ( fos ) );
                if (!Reusable.containTraceHeaders ( fout )) {
                    bw.write ( String.format ( "%-3s %-14s %-3s %-3s %-12s %-12s %-10s %-10s\n",
                            "PC", "INSTRUCTION", "R", "EA", "(R)Before", "(EA)Execution",
                            "(R)after", "(EA)Execution" ) );
                    bw.write ( String.format ( "%-3s %-14s %-3s %-3s %-12s %-12s %-10s %-10s\n",
                            "--", "-----------", "-", "--", "---------", "-----------",
                            "---------", "------------" ) );
                } else {
                    bw.write ( "\n" );
                    bw.write ( "--> Next Job in line \n" );
                    bw.write ( "\n" );
                    bw.write ( "\n" );
                }
                int i = 0;
                for (Map.Entry<String, CPU.TRACEUNIT> aae : aaa.entrySet ()) {
                    if (i % 10 != 0 || i == 0) {
                        bw.write ( aae.getValue ().toString () );
                        i++;
                    } else {
                        bw.newLine ();
                        bw.write ( aae.getValue ().toString () );
                        i++;
                    }
                }
                bw.close ();
                status = true;
            } catch (Exception e) {
                throw new ERRORHANDLER ( "SYSTEMTrace-" + e.getMessage (), mem );
            }
            return status;
        }

        public static String santizeFilename(String original) {
            original = original.replaceAll ( "[^a-zA-Z0-9\\.\\-]", "_" );
            return original;
        }

        /*
         * FunctionName: WriteToFile
         * Description: Write to a file in append mode
         * parameters:  takes in list of OUT objects
         * returns: boolean
         * lastChanged: 03/01/2019
         *
         * */
        public static boolean WriteToOutputFile(List<CPU.OUT> aaa, MEMORY mem) throws ERRORHANDLER {
            boolean status = false;
            String FileName = "OUT_Phase2.txt";
            File fout = new File ( FileName );
            try {
//               has headers?
                FileOutputStream fos = new FileOutputStream ( fout, true );
                BufferedWriter bw = new BufferedWriter ( new OutputStreamWriter ( fos ) );
                if (!Reusable.containOutputHeaders ( fout )) {
                    bw.write ( String.format ( "%-2s %-50s %-2s  %-12s\n",
                            "J", "Mess", "Clock(HEX)", "Time" ) );
                    bw.write ( String.format ( "%-2s %-50s %-2s  %-12s \n",
                            "--", "----", "----------", "------------" ) );
                } else {
                    bw.write ( "\n" );
                }
                int i = 0;
                String JobID = "";
                String Message = "";
                int Clockvalue = 0;
                Long totalExe = 0L;
                Long totalIO = 0L;
                int grandtotal = 0;
                Long ClockExeinDEc = 0L;
                Long ClockIO = 0L;
                for (CPU.OUT aae : aaa) {
                    JobID = aae.jobid;
                    Message = aae.TermMessage;
                    Clockvalue = aae.Decimal;
                    for (CPU.TimeUnit a : aae.RunTime) {
                        totalExe += a.getTimeUnit ().ExeTime;
                        totalIO += a.getTimeUnit ().IOTime;
                        grandtotal += totalExe + totalIO;
                        ClockExeinDEc += a.getTimeUnit ().ExeDec;
                        ClockIO += a.getTimeUnit ().IOdec;
                    }
                }
                String Binary = new LOADER.Unit ().DecimalToBinary ( Clockvalue, 16 ).toString ();
                String ClockHex = new LOADER.Unit ().BinaryToHex ( Binary ).toString ();
                bw.write ( String.format ( "%-2s %-50s %-2s %-12s \n",
                        JobID, Message, ClockHex, new CPU.TimeUnit ( totalExe, totalIO, ClockExeinDEc, ClockIO ).toString () ) );
                bw.close ();
                status = true;
            } catch (Exception e) {
                //throw new ERRORHANDLER("SYSTEMTrace-" + e.getMessage(), mem);
            }
            return status;
        }


        /*
         * FunctionName: ProgressFileObject
         * Description: Write to a file in using theis object
         * parameters:  void
         * returns: buffered writer
         * lastChanged: 05/01/2019
         *
         * */

        public static BufferedWriter GetProgressFileObj() {
            boolean status = false;
            String ts = SYSTEM.PROGRAM.santizeFilename ( "Progress_" + new Timestamp ( new Date ().getTime () ).toString () + ".txt" );
            File fout = new File ( ts );
            try {
                fout.createNewFile ();
            } catch (IOException e) {
                e.printStackTrace ();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream ( fout, true );
            } catch (FileNotFoundException e) {
                e.printStackTrace ();
            }
            BufferedWriter bw = new BufferedWriter ( new OutputStreamWriter ( fos ) );
            return bw;
        }
        //write to file using the object
        public static Boolean WriteToProgress(Object Data, BufferedWriter bw) {
            Boolean s = false;
            try {
                if (bw != null) {
//                   if(Data.getClass ().getName ().equals ( "list" ))
//                   {
//                       bw.write ( String.format("\n",Data));
//                   }
                    bw.write ( Data.toString () + "\n" );
                    s = true;
                }
            } catch (Exception e) {
            }
            return s;
        }
        //close the file when done
        public static Boolean CloseProgress(BufferedWriter bw) {
            Boolean s = false;
            try {
                if (bw != null) {
                    bw.flush ();
                    bw.close ();
                    s = true;
                }
            } catch (Exception e) {
            }
            return s;
        }
        //writing to outfile new
        public static boolean WriteToOUTFile(Set<PROCESS> aaa, MEMORY mem) throws ERRORHANDLER {
            boolean status = false;
            String FileName = "OUT_Phase2.txt";
            File fout = new File ( FileName );
            try {
//               has headers?
                FileOutputStream fos = new FileOutputStream ( fout, true );
                BufferedWriter bw = new BufferedWriter ( new OutputStreamWriter ( fos ) );
                if (!Reusable.containOutputHeaders ( fout )) {
                    bw.write ( "OUT\n" );
                    bw.write ( String.format ( "-----------------------------------------------------------------------\n" ) );
                } else {
                    bw.write ( "\n" );
                }
                for (PROCESS aa : aaa) {
                    bw.write ( "A) Job -> " + aa.getPID () + ".\n" );
                    bw.write ( "B) Memory. \n" );
                    bw.write ( "OLD memory " + aa.thisPCB.thismemorymap + "\n" );
                    bw.write ( "Compacted " + aa.thisPCB.thismemorymapAfterCompaction + "\n" );
                    bw.write ( "C) Job Initiation. (decimal) \n" );
                    bw.write ( "      ->Arrival Time:" + aa.getArrivalTime () + "\n" );
                    bw.write ( "D & E) SYSTEM.(decimal) \n" );
                    bw.write ( "      ->Start in SYSTEM:" + aa.getStartTime () + "\n" );
                    bw.write ( "      ->Left out of SYSTEM:" + aa.EndTime + "\n" );
                    bw.write ( "F) Execution time.(decimal) \n" );
                    bw.write ( "      ->Exec Time:" + aa.getCPUClock () + "\n" );
                    bw.write ( "G) IO time.(decimal) \n" );
                    bw.write ( "      ->IO Time:" + aa.getIOtime () + "\n" );
                    ;
                    bw.write ( "H) Priority.(decimal) \n" );
                    bw.write ( "      ->Priority: " + aa.getPriority () + "\n" );
                    bw.write ( "I)Warning/Errors. \n" );
                    List<CPU.OUT> outs = new ArrayList<> ( aa.thisPCB.thisOUt.values () );
                    bw.write ( outs.get ( outs.size () - 1 ).toString () + "\n" );
                    bw.write ( String.format ( "-----------------------------------------------------------------------\n" ) );
                }
                bw.close ();
                status = true;
            } catch (Exception e) {
                //throw new ERRORHANDLER("SYSTEMOUT-" + e.getMessage(), mem);
            }
            return status;
        }
        //writing to a stat file takes in completed set of PCBs and mem object
        public static boolean WriteToStatistic(Set<PROCESS> aaa, MEMORY mem) throws ERRORHANDLER {
            boolean status = false;
            String FileName = "Statistics.txt";
            //runtime
            int meanjobruntime = 0;
            int meanIO = 0;
            int meanSys = 0;
            int meanMulti = 0;
            int meanDiff = 0;
            try {
                int totalruntime = 0;
                int totalIO = 0;
                int totalSYS = 0;
                int numberprocess = 0;
                int totalDiff = 0;
                for (PROCESS aa : aaa) {
                    totalruntime += aa.getCPUClock ();
                    totalIO += aa.getIOtime ();
                    totalSYS += (aa.EndTime - aa.getStartTime ());
                    totalDiff += (aa.getStartTime () - aa.getArrivalTime ());
                    numberprocess++;
                }
                meanjobruntime = totalruntime / numberprocess;
                meanIO = totalIO / numberprocess;
                meanSys = totalSYS / numberprocess;
                meanMulti = (SYSTEM.HighDegree + SYSTEM.lowDegree / timesHigh + timesLow);
                meanDiff = (totalDiff / numberprocess);
            } catch (Exception e) {
            }
            File fout = new File ( FileName );
            try {
//               has headers?
                FileOutputStream fos = new FileOutputStream ( fout );
                BufferedWriter bw = new BufferedWriter ( new OutputStreamWriter ( fos ) );
                bw.write ( "STATISTIC\n" );
                bw.write ( String.format ( "-----------------------------------------------------------------------\n" ) );
                bw.write ( "\n" );
                bw.write ( "A) Current Clock -> " + SYSTEM.SYSClock + ".\n" );
                bw.write ( "B) Mean Job Run time (decimal). \n" );
                bw.write ( "      -> { " + meanjobruntime + " }\n" );
                bw.write ( "C) Mean Job IO time. (decimal) \n" );
                bw.write ( "      -> { " + meanIO + " } \n" );
                bw.write ( "D) Mean Time in SYSTEM.(decimal) \n" );
                bw.write ( "      -> { " + meanSys + " }\n" );
                bw.write ( "E) CPU Idle Time.(decimal) \n" );
                bw.write ( "      -> { " + SYSTEM.SYSIdle + " }\n" );
                bw.write ( "F) Total Time Lost Abnormal terminated Jobs (decimal) \n" );
                bw.write ( "      -> { " + SYSTEM.AbnormalTimeLost + " }\n" );
                bw.write ( "G) Number of jobs terminated Abnormally (decimal) \n" );
                bw.write ( "      -> { " + SYSTEM.AbnormalTerminations + " }\n" );
                bw.write ( "H) Number of jobs terminated normally (decimal) \n" );
                bw.write ( "      -> { " + SYSTEM.NormalTerminations + " }\n" );
                bw.write ( "I) Highest Degree of multiprogramming. \n" );
                bw.write ( "      -> { " + SYSTEM.HighDegree + " }\n" );
                bw.write ( "J) Lowest Degree of multiprogramming. \n" );
                bw.write ( "      -> { " + SYSTEM.lowDegree + " }\n" );
                bw.write ( "k) Mean Degree of multiprogramming. \n" );
                bw.write ( "      -> { " + meanMulti + " }\n" );
                bw.write ( "L) Mean difference b?W arrival time and start time. \n" );
                bw.write ( "      -> { " + meanDiff + " }\n" );
                bw.write ( String.format ( "-----------------------------------------------------------------------\n" ) );
                bw.close ();
                status = true;
            } catch (Exception e) {
                //throw new ERRORHANDLER("SYSTEMOUT-" + e.getMessage(), mem);
            }
            return status;
        }
    }
}
