import java.util.*;
//serves as the scheduler bare bones structure
public class SCHEDULER {
//list of process
    public List<PROCESS> readyQ;
//total wuantum
    public float quantum;
//processhigh q priority q
    public PriorityQueue<PROCESS> ProcessQueueH;
    //processlow q
    public PriorityQueue<PROCESS> ProcessQueueL;
    //blocked q
    public List<PROCESS> blockedQ;
    public Map<String, PROCESS> processMap;
    protected PROCESS RunProcess;
    private int currProc;
    private double curTimeQuantum;

    SCHEDULER(int q) {
        ProcessQueueH = new PriorityQueue<PROCESS> ( new Comparator<PROCESS> () {
             //comparator logic to assign the place of the pcb based on remainder time quantum
            @Override
            public int compare(PROCESS o1, PROCESS o2) {
                return o1.getRemainingQuantum () <= o2.getRemainingQuantum () ? 1 : -1;
            }
        } );
        ProcessQueueL = new PriorityQueue<PROCESS> ( new Comparator<PROCESS> () {
            //comparator logic to assign the place of the pcb based on remainder time quantum
            @Override
            public int compare(PROCESS o1, PROCESS o2) {
                return o1.getRemainingQuantum () <= o2.getRemainingQuantum () ? 1 : -1;
            }
        } );
        //initialized
        readyQ = new LinkedList<> ();
        blockedQ = new LinkedList<> ();
        quantum = q;
        curTimeQuantum = 0.0;
        //decides  the readyqueue element position
        currProc = 0;
        //never used
        this.processMap = new LinkedHashMap<> ();
    }
//creats a process and adds to correspondingqueues based on priority
    public void createProcess(PROCESS p) {
        if (p.getArrivalTime () == 0) {
            p.setArrivalTime ( SYSTEM.getSYSClock () );
        }
        if (!this.processMap.containsKey ( p.getPID () )) {
            this.processMap.put ( p.getPID (), p );
            try {
                if (p.getPriority () == 1) {
                    if (!isExistsinPhigh ( p )) {
                        this.ProcessQueueH.add ( p );
                    }
                } else {
                    if (!isExistsinPlow ( p )) {
                        this.ProcessQueueL.add ( p );
                    }
                }
            } catch (Exception e) {
                System.out.println ( e );
            }
        }
    }
    
    //check if process already been added in highQ
    private boolean isExistsinPhigh(PROCESS p) {
        boolean exists = false;
        if (this.ProcessQueueH.size () > 0) {
            for (PROCESS a : this.ProcessQueueH) {
                if (a.getPID ().contains ( p.getPID () )) {
                    exists = true;
                }
            }
        }
        return exists;
    }
    //check if process already been added in readyQ
    private boolean isExistsinReadyQ(PROCESS p) {
        boolean exists = false;
        if (this.readyQ.size () > 0) {
            for (PROCESS a : this.readyQ) {
                if (a.getPID ().equals ( p.getPID () )) {
                    exists = true;
                }
            }
        }
        return exists;
    }
    //check if process already been added to Blockedq
    private boolean isExistsinBlocked(PROCESS p) {
        boolean exists = false;
        if (this.blockedQ.size () > 0) {
            for (PROCESS a : this.blockedQ) {
                if (a.getPID ().contains ( p.getPID () )) {
                    exists = true;
                }
            }
        }
        return exists;
    }
    //check if process already been added in priorityLow q
    private boolean isExistsinPlow(PROCESS p) {
        boolean exists = false;
        if (this.ProcessQueueL.size () > 0) {
            for (PROCESS a : this.ProcessQueueL) {
                if (a.getPID ().contains ( p.getPID () )) {
                    exists = true;
                }
            }
        }
        return exists;
    }
    //create multiple process use above method
    public void createProcesses(LOADER load, MEMORY memObj, List<String> s) throws ERRORHANDLER {
        for (String sa : s) {
            createProcess ( new PROCESS ( load.loaderunits.get ( sa ).get ( "jobid" ).toString (), 30,
                    Integer.parseInt ( load.loaderunits.get ( sa ).get ( "priority" ).toString () ),
                    Integer.parseInt ( load.loaderunits.get ( sa ).get ( "arr" ).toString () ), Integer.parseInt ( load.loaderunits.get ( sa ).get ( "plen" ).toString () ) * 2, memObj ) );
        }
    }
    //add to block q n,nvere used
    public void setToBlockQ(PROCESS p) {
        if (!isExistsinBlocked ( p )) {
            blockedQ.add ( p );
        } else if (isExistsinBlocked ( p )) {
            blockedQ.remove ( blockedQ.indexOf ( p ) );
            blockedQ.add ( p );
        }
    }
//get the blocked porocess
    public PROCESS LookatBlockedProcess() {
        PROCESS p = null;
        try {
            p = blockedQ.get ( 0 );
        } catch (Exception e) {
        }
        return p;
    }
    

    public void setQuantum(int value) {
        this.quantum = value;
    }
    //delete PCB from scheduler
    public boolean DeletefromAllQueues(PROCESS p) {
        return (ProcessQueueH.remove ( p ) || ProcessQueueL.remove ( p ) || blockedQ.remove ( p ) || readyQ.remove ( p ));
    }
    //delete from everywhere but from readyQ
    public boolean DeleteProcess(PROCESS p) {
        return (ProcessQueueH.remove ( p ) || ProcessQueueL.remove ( p ) || blockedQ.remove ( p ));
    }
//logic to search throw next pcb to execute
    public PROCESS NextProcess(int clock) {
        //addign to the ready q
        while (ProcessQueueH.size () > 0 || ProcessQueueL.size () > 0 || blockedQ.size () > 0) {
            if (ProcessQueueH.size () > 0) {
                //handle if is blocked preceed on to next one
                PROCESS pp = ProcessQueueH.poll ();
                if (!isExistsinReadyQ ( pp )) {
                    readyQ.add ( pp );
                }
            }
            if (blockedQ.size () > 0) {
                PROCESS pB = blockedQ.get ( 0 );
                blockedQ.remove ( 0 );
                if (!isExistsinReadyQ ( pB )) {
                    readyQ.add ( pB );
                }
            } else if (ProcessQueueL.size () > 0) {
                PROCESS pp2 = ProcessQueueL.poll ();
                if (!isExistsinReadyQ ( pp2 )) {
                    readyQ.add ( pp2 );
                }
            }
        }
        //if quantum is zero make process NULL
        LogicLookThrougPRocess ();
        return RunProcess;
    }
    private void LogicLookThrougPRocess() {
        if (quantum <= 0) {
            RunProcess = null;
        } else if (readyQ.size () > 0) {
            if (RunProcess == null) {
                RunProcess = readyQ.get ( currProc );
                curTimeQuantum = 0;
            } else if ((quantum - RunProcess.getBurstTime () < 0.1) && !RunProcess.IsFinished ()) {
                currProc = (currProc + 1) % readyQ.size ();
                RunProcess = readyQ.get ( currProc );
                curTimeQuantum = 0;
            } else if (RunProcess.IsFinished ()) {
                if (currProc == readyQ.size ()) {
                    currProc--;
                }
                RunProcess = readyQ.get ( currProc );
                curTimeQuantum = 0.1;
            } else if (RunProcess.IsBlocked ()) {
                if (readyQ.get ( currProc ).getIOtime () == 0) {
                    readyQ.get ( currProc ).setIOBlock ( false, 0 );
                    currProc = 0;
                } else {
                    RunProcess.setIOBlock ( false, 0 );
                    if (readyQ.size () > 1) {
                        currProc++;
                    } else {
                        currProc = 0;
                    }
                }
                RunProcess = readyQ.get ( currProc );
            } else if (RunProcess.IsSwitch ()) {
                if (readyQ.get ( currProc ).getBurstTime () == 0) {
                    if (quantum > 30) {
                        readyQ.get ( currProc ).setBurstTime ( 30 );
                        readyQ.get ( currProc ).SwitchState ( false );
                        readyQ.get ( currProc ).setRemainingQuantum ( 0 );
                    }
                }
                RunProcess = readyQ.get ( currProc );
            }
            curTimeQuantum += 1;
            currProc = 0;
        } else {
            RunProcess = readyQ.get ( currProc );
        }
        if (RunProcess.getStartTime () == 0) {
            RunProcess.setStartTime ( SYSTEM.getSYSClock () );
        }
    }

    public boolean isEmpty() {
        return !ProcessQueueH.isEmpty () || !ProcessQueueL.isEmpty () || !blockedQ.isEmpty () || !readyQ.isEmpty ();
    }

}

