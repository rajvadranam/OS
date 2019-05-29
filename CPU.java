import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CPU extends ERRORHANDLER {

//    //map structure to hold data
    public Map<String, TRACEUNIT> thisTrace;
//    //map structure to hold output data
    public Map<String, OUT> thisout;
    //trace switch
  public String TraceSwitchStat;
    //CPU clock variable
    public int CLOCK;
    //get loader keys
//    public List<String> loadKeys;
    public String Message;
    //property to get Clock value
    public int GetClock() {
        return this.CLOCK;
    }
    //property to set Clock value
    public void SetClock(int value) {
        this.CLOCK = this.CLOCK + value;
    }
    //property to get Message String
    public String getMessage() {
        return this.Message;
    }
    //getting a randomized value
    public String getRandomNumber() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(0, 100 + 1));
    }
    //getting the jobID
    public String getJobID(String loadId) {
        return loadId + "_" + getRandomNumber();
    }

    public CPU()
    {
        this.thisout=new LinkedHashMap<> (  );
    }


    /*
     * FunctionName: CPU_I
     * Description: RUNs the CPU tasks
     * parameters: memory object ,Trace obj
     * returns: status boolean
     * lastChanged: 03/01/2019
     *
     * */
    public CPU_State CPU_I(PROCESS pcb,LOADER load,int burst) throws ERRORHANDLER {
        //setting CPU stat false
        boolean blockednow=false;
        this.CLOCK =pcb.getCPUClock ();
        CPU_State CPUState = new CPU_State ();
        MEMORY mem= pcb.thisPCB.thismemory;
        //getting this Jobid
        TraceSwitchStat = load.getTraceSwitch(pcb.thisPCB.jobid).get ( "trace" ).toString ();
        if(new LOADER.Unit (  ).Binary2sToDecimal (  pcb.thisPCB.reg.getPCValue ())==0) {
            pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( load.getProgramIndex ( pcb.thisPCB.jobid ), 16 ).toString () );
        }
        String thisJobID = pcb.thisPCB.jobid;
        //getting traceObject stucture
        thisTrace = pcb.thisPCB.thisTrace.getAllTrace();
        //setting message to normal
        String Message = "Normal";
        //setting macycles allowed
        Long MAXCYCLE = 10000L;
        boolean completed=false;
        //getting the Clock value
        int inbuiltcounter = 0;
        try {
            //getting PC value to work with memory
            int j = Math.toIntExact(new LOADER.Unit().BinaryToDecimal(  pcb.thisPCB.reg.getPCValue()));
            pcb.thisPCB.reg.setPCValue(new LOADER.Unit().DecimalToBinary(j, 16).toString());
            //getting memory has anything
            int totalsize = pcb.thisPCB.thismemory.memoryModule.size ();
            //loop until memory and max cycle is in bound
            while (totalsize != 0 && burst>=1 && inbuiltcounter <= MAXCYCLE.intValue() && !pcb.IsFinished () && GetClock ()<=10000) {
                Long aa = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 );
                int k = aa.intValue ();
                List<String> Full_INS = new ArrayList<> ();
                //reading the value from memory
                mem.getObject ( pcb.getPID (),"READ", String.valueOf ( k ), Full_INS );
                String Fullinstruction = Full_INS.toArray ()[0].toString ();

                //System.out.println (pcb);

                //checking if the read input is not xxxx

                        --burst;
                        //extracting OPcode
                        pcb.setCPUClock ( 1 );
                        pcb.setBurstTime ( burst );
                        String opcode = Fullinstruction.substring ( 0, 4 );
                        //get map to split instruction into registers
                        Map<String, String> instructionAdd = SplitInstruction ( Fullinstruction );
                        switch (opcode) {
                            //Arthematic
                            case "0000": // Adds the operation with register rd = rs + rt
                                //trace logic
                                //getting the PC value
                                String Cl_1 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                //getting the instruction
                                String INS_1 = "add " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) + "," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) + "," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RT" ) );
                                List<String> R_1 = new ArrayList<> ();
                                //getting the registers involved
                                R_1.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                R_1.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                R_1.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RT" ) ) );
                                String EA = " ";
                                List<String> R_before_1 = new ArrayList<> ();
                                //getting value of registers before execution
                                R_before_1.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_before_1.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                R_before_1.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) ) ) );
                                String EA_exe = " ";
                                //CPU Logic
                                long startTime1 = System.nanoTime ();
                                int value1 = 0;
                                //checking the value to preserve the sign
                               // if (new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) > new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) )) {
                                    value1 = new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) +
                                            new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) );
                               //
                                //checking if registers value exceeds the bound
                                if (value1 < 32767 && value1 > -32767) {
                                    //setting the value into RD
                                    pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), new LOADER.Unit ().DecimalToBinary ( value1, Fullinstruction.length () ).toString () );
                                } else {
                                    String erroMessage = " Register Allocation Too Large";
                                    Message = "Abnormal " + erroMessage;
                                    throw new ERRORHANDLER (pcb.getPID (), Message, mem );
                                }
                                long endtime1 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_1 = new ArrayList<> ();
                                //getting the register values after the execution
                                R_after_1.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_after_1.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                R_after_1.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) ) ) );
                                String EA_2 = " ";
                                SetClock ( 1 );
                                inbuiltcounter++;
                                //trace values settings
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_1, INS_1, R_1, EA, R_before_1, EA_exe, R_after_1, EA_2 ) );
                                Long pcv = (Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2);
                                //setting the PC value
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv.intValue (), Fullinstruction.length () ).toString () );
                                //setting OUT values for this case
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endtime1 - startTime1 ), "0", String.valueOf ( GetClock ()  ), String.valueOf ( 0 ) ) ) ) );
                                break;
                            case "0001": // Add Immediate  operation with register rd = rs + immediate6
                                //trace logic
                                //getting the PC value
                                String Cl_2 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                //getting the instruction
                                String INS_2 = "addi " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) + "," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) + "," + new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) );
                                List<String> R_2 = new ArrayList<> ();
                                //getting the register names
                                R_2.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                R_2.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                String EA_2_1 = " ";
                                List<String> R_before_2 = new ArrayList<> ();
                                //getting register values
                                R_before_2.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_before_2.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                String EA_exe_2 = " ";
                                //CPU Logic
                                long startTime2 = System.nanoTime ();
                                int vlaue = 0;
                                //getting immed6
                                String twos = instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" );
                                //preserving the sign
                                //if (new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) > new LOADER.Unit ().Binary2sToDecimal ( twos )) {
                                    vlaue = new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) +
                                            new LOADER.Unit ().Binary2sToDecimal ( twos );
//                                } else {
//                                    vlaue = new LOADER.Unit ().Binary2sToDecimal ( twos ) +
//                                            new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) );
//                                }
//                            vlaue = new LOADER.Unit().BinaryToDecimal(pcb.thisPCB.reg.registers.get(instructionAdd.get("RS"))) +
//                                   new LOADER.Unit().BinaryToDecimal(twos);
                                if (vlaue < 32767 && vlaue > -32767) {
                                    //setting the value
                                    pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), new LOADER.Unit ().DecimalToBinary ( vlaue, Fullinstruction.length () ).toString () );
                                } else {
                                    String erroMessage = " Register Allocation Too Large";
                                    Message = "Abnormal " + erroMessage;
                                    throw new ERRORHANDLER (pcb.getPID (),Message, mem );
                                }
                                long endtime2 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_2 = new ArrayList<> ();
                                //getting the register values
                                R_after_2.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_after_2.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                String EA_2_2 = " ";
                                SetClock ( 1 );
                                inbuiltcounter++;
                                //setting the trace values
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_2, INS_2, R_2, EA_2_1, R_before_2, EA_exe_2, R_after_2, EA_2_2 ) );
                                Long pcv2 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                //setting PC value
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv2.intValue (), Fullinstruction.length () ).toString () );
                                //setting OUT values
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endtime2 - startTime2 ), "0", String.valueOf ( GetClock ()  ), String.valueOf ( 0 ) ) ) ) );
                                break;
                            case "0010": // sub  operation with register rd = rs - rt
                                //trace logic
                                String Cl_3 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                //getting the instruction
                                String INS_3 = "sub " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) + "," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) + "," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RT" ) );
                                List<String> R_3 = new ArrayList<> ();
                                //getting the register names
                                R_3.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                R_3.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                R_3.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RT" ) ) );
                                String EA_1_3 = " ";
                                List<String> R_before_3 = new ArrayList<> ();
                                //getting the register values
                                R_before_3.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_before_3.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                R_before_3.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) ) ) );
                                String EA_exe_3 = " ";
                                //CPU Logic
                                long startTime3 = System.nanoTime ();
                                int value3 = 0;
                                //preserving the sign
//                            if (new LOADER.Unit().Binary2sToDecimal(pcb.thisPCB.reg.registers.get(instructionAdd.get("RS"))) > new LOADER.Unit().Binary2sToDecimal(pcb.thisPCB.reg.registers.get(instructionAdd.get("RT")))) {
                                value3 = new LOADER.Unit ().Binary2sToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) -
                                        new LOADER.Unit ().Binary2sToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) );
//                            } else {
//                                value3 = new LOADER.Unit().Binary2sToDecimal (pcb.thisPCB.reg.registers.get(instructionAdd.get("RT"))) -
//                                        new LOADER.Unit().Binary2sToDecimal(pcb.thisPCB.reg.registers.get(instructionAdd.get("RS")));
//                            }
//                            value3 = new LOADER.Unit().BinaryToDecimal(pcb.thisPCB.reg.registers.get(instructionAdd.get("RS"))) -
//                                   new LOADER.Unit().BinaryToDecimal(pcb.thisPCB.reg.registers.get(instructionAdd.get("RT")));
                                if (value3 < 32767 && value3 > -32767) {
                                    //setiing the values
                                    pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), new LOADER.Unit ().DecimalToBinary ( value3, Fullinstruction.length () ).toString () );
                                } else {
                                    String erroMessage = " Register Allocation Too Larg";
                                    Message = "Abnormal " + erroMessage;
                                    throw new ERRORHANDLER (pcb.getPID (),Message, mem );
                                }
                                long endtime3 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_3 = new ArrayList<> ();
                                //getting the register values
                                R_after_3.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_after_3.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                R_after_3.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) ) ) );
                                String EA_2_3 = " ";
                                SetClock ( 1 );
                                inbuiltcounter++;
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_3, INS_3, R_3, EA_1_3, R_before_3, EA_exe_3, R_after_3, EA_2_3 ) );
                                Long pcv3 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                //setting the pc value
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv3.intValue (), Fullinstruction.length () ).toString () );
                                //setting OUT values
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endtime3 - startTime3 ), "0", String.valueOf ( GetClock ()  ), String.valueOf ( 0 ) ) ) ) );
                                break;
                            case "0011": // subi  operation with register rd = rs - immediate6
                                //trace logic
                                String Cl_4 = String.valueOf ( new LOADER.Unit ().Binary2sToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                //getting the istructions
                                String INS_4 = "subi " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) + "," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) + "," + new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) );
                                List<String> R_4 = new ArrayList<> ();
                                //getting the register names
                                R_4.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                R_4.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                String EA_4_1 = " ";
                                List<String> R_before_4 = new ArrayList<> ();
                                //getting the register values
                                R_before_4.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_before_4.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                String EA_exe_4 = " ";
                                //CPU Logic
                                long startTime4 = System.nanoTime ();
                                //getting immed6
                                String GETTWOS = instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" );
                                int vlaue4 = 0;
                                //String twos = new LOADER.Unit().doTwos(instructionAdd.get("RT") + instructionAdd.get("EX")).toString();
                                //preserving sign
//                            if (new LOADER.Unit().Binary2sToDecimal(pcb.thisPCB.reg.registers.get(instructionAdd.get("RS"))) > new LOADER.Unit().Binary2sToDecimal(GETTWOS)) {
                                vlaue4 = new LOADER.Unit ().Binary2sToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) -
                                        new LOADER.Unit ().Binary2sToDecimal ( GETTWOS );
//                            } else {
//                                vlaue4 = new LOADER.Unit().Binary2sToDecimal(GETTWOS) -
//                                        new LOADER.Unit().BinaryToDecimal(pcb.thisPCB.reg.registers.get(instructionAdd.get("RS")));
//                            }
//                           int vlaue4 = new LOADER.Unit().BinaryToDecimal(pcb.thisPCB.reg.registers.get(instructionAdd.get("RS"))) -
//
//                                   new LOADER.Unit().BinaryToDecimal(GETTWOS);
                                if (vlaue4 < 32767 && vlaue4 > -32767) {
                                    //setting RD value
                                    pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), new LOADER.Unit ().DecimalToBinary ( vlaue4, Fullinstruction.length () ).toString () );
                                } else {
                                    String erroMessage = " Register Allocation Too Large";
                                    Message = "Abnormal " + erroMessage;
                                    throw new ERRORHANDLER (pcb.getPID (),Message, mem );
                                }
                                long endTime4 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_4 = new ArrayList<> ();
                                //getting register values
                                R_after_4.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_after_4.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                String EA_4_2 = " ";
                                SetClock ( 1 );
                                inbuiltcounter++;
                                //getting trace values
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_4, INS_4, R_4, EA_4_1, R_before_4, EA_exe_4, R_after_4, EA_4_2 ) );
                                Long pcv4 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                //setting PC value
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv4.intValue (), Fullinstruction.length () ).toString () );
                                //setting OUT
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime4 - startTime4 ), "0", String.valueOf ( GetClock ()  ), String.valueOf ( 0 ) ) ) ) );
                                break;
                            //Data Movement
                            case "1010": // moves rd <-- rs,ttt=0
                                //question ttt??
                                //trace logic
                                String Cl_5 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                //getting instructions
                                String INS_5 = "mov " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) + "," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) );
                                if (!instructionAdd.get ( "RT" ).equals ( "000" )) {
                                    String erroMessage = " Illegal INSTRUCTION";
                                    Message = "Abnormal " + erroMessage;
                                    throw new ERRORHANDLER (pcb.getPID (),Message, mem );
                                }
                                List<String> R_5 = new ArrayList<> ();
                                R_5.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                R_5.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                String EA_5_1 = " ";
                                List<String> R_before_5 = new ArrayList<> ();
                                R_before_5.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_before_5.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                String EA_exe_5 = " ";
                                //CPU Logic
                                long startTime5 = System.nanoTime ();
                                pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) );
                                long endTime5 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_5 = new ArrayList<> ();
                                R_after_5.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_after_5.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                String EA_5_2 = " ";
                                SetClock ( 1 );
                                inbuiltcounter++;
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_5, INS_5, R_5, EA_5_1, R_before_5, EA_exe_5, R_after_5, EA_5_2 ) );
                                Long pcv5 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv5.intValue (), Fullinstruction.length () ).toString () );
                                //setting OUT
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime5 - startTime5 ), "0", String.valueOf ( GetClock ()  ), String.valueOf ( 0 ) ) ) ) );
                                break;
                            case "1011": // Moves data  rd =immed6 ,rs=000
                                //question rs =???
                                //trace logic
                                String Cl_6 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                String INS_6 = "movi " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) + "," + new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) );
                                List<String> R_6 = new ArrayList<> ();
                                if (!instructionAdd.get ( "RS" ).equals ( "000" )) {
                                    String Erro = "Illegal INS immed-val too large";
                                    Message = "Abnormal " + Erro;
                                    throw new ERRORHANDLER ( pcb.getPID (),Erro, mem );
                                }
                                R_6.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                // R_6.add(pcb.thisPCB.reg.registerName.get(instructionAdd.get("RS")));
                                String EA_6_1 = " ";
                                 List<String> R_before_6 = new ArrayList<> ();
                                R_before_6.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                //R_before_6.add(String.valueOf(new LOADER.Unit().BinaryToDecimal(pcb.thisPCB.reg.registers.get(instructionAdd.get("RS")), 2)));
                                String EA_exe_6 = " ";
                                //CPU Logic
                                long startTime6 = System.nanoTime ();
                                int GETTWOS2 = new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) );
                                pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), new LOADER.Unit ().DecimalToBinary ( GETTWOS2, Fullinstruction.length () ).toString () );
                                long endTime6 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_6 = new ArrayList<> ();
                                R_after_6.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                // R_after_6.add(String.valueOf(new LOADER.Unit().BinaryToDecimal(pcb.thisPCB.reg.registers.get(instructionAdd.get("RS")), 2)));
                                String EA_6_2 = " ";
                                SetClock ( 1 );
                                inbuiltcounter++;
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_6, INS_6, R_6, EA_6_1, R_before_6, EA_exe_6, R_after_6, EA_6_2 ) );
                                Long pcv6 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv6.intValue (), Fullinstruction.length () ).toString () );
                                //setting OUT
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime6 - startTime6 ), "0", String.valueOf ( GetClock ()  ), String.valueOf ( 0 ) ) ) ) );
                                break;
                            case "1000": // load data  rd = MEM[reg[rs] + immediate]
                                //trace logic
                                String Cl_7 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                String INS_7 = "load " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) + "," + new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) ) + "(" + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) + ")";
                                List<String> R_7 = new ArrayList<> ();
                                R_7.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                R_7.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                List<String> R_before_7 = new ArrayList<> ();
                                R_before_7.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_before_7.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                String initallocation = String.valueOf ( new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) ) );
                                String EA_7_1 = initallocation;
                                //CPU Logic
                                long startTime7 = System.nanoTime ();
                                String rscontents = pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) );
                                int Displacement = new LOADER.Unit ().BinaryToDecimal ( rscontents ) +
                                        new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) );
                                //getting memory contents
                                long startTimememoryRead1 = System.nanoTime ();
                                long endTimememoryRead2 = System.nanoTime ();
                                List<String> valueaaa2a = new ArrayList<> ();
                                if (Integer.parseInt ( initallocation ) % 2 == 0) {
                                    if (Integer.parseInt ( initallocation ) < mem.getMemory ().size () && mem.getMemory ().get ( String.valueOf ( initallocation ) ).getMemoryTpe ().equals ( "DATA" )) {
                                        if (!mem.getMemory ().get ( String.valueOf ( initallocation ) ).getLock ()) {
                                            mem.getObject ( pcb.getPID (),"READ", initallocation, valueaaa2a );
                                        } else {
//                                        String Erro = "memorySpace is LOCKED";
//                                        Message = "Abnormal " + Erro;
//                                        throw new ERRORHANDLER(Erro);
                                            valueaaa2a.add ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) );
                                        }
                                    } else {
                                        String Erro = "MEMORY RANGE FAULT";
                                        Message = "Abnormal " + Erro;
                                        throw new ERRORHANDLER ( Erro );
                                    }
                                } else {
                                    String Erro = "Illegal memorySpace Access";
                                    Message = "Abnormal " + Erro;
                                    throw new ERRORHANDLER ( Erro );
                                }
                                String EA_exe_7 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( valueaaa2a.toArray ()[0].toString () ) );
                                long endTime7 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_7 = new ArrayList<> ();

                                List<String> valueaaa2 = new ArrayList<> ();
                                if (Displacement % 2 == 0) {
                                    if (Displacement < mem.getMemory ().size () && mem.getMemory ().get ( String.valueOf ( Displacement ) ).getMemoryTpe ().equals ( "DATA" )) {
                                        if (!mem.getMemory ().get ( String.valueOf ( Displacement ) ).getLock ()) {
                                            mem.getObject ( pcb.getPID (),"READ", String.valueOf ( Displacement ), valueaaa2 );
                                        } else {
//                                        String Erro = "memorySpace is LOCKED";
//                                        Message = "Abnormal " + Erro;
//                                        throw new ERRORHANDLER(Erro);
                                            //since it is locked mantaining the same value in rd
                                            valueaaa2.add ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) );
                                        }
                                    } else {
                                        String Erro = "MEMORY RANGE FAULT";
                                        Message = "Abnormal " + Erro;
                                        throw new ERRORHANDLER ( Erro );
                                    }
                                } else {
                                    String Erro = "Illigal memorySpace Access";
                                    Message = "Abnormal " + Erro;
                                    throw new ERRORHANDLER ( Erro );
                                }
                                String EA_7_2 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( valueaaa2.toArray ()[0].toString () ) );
                                inbuiltcounter++;
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_7, INS_7, R_7, EA_7_1, R_before_7, EA_exe_7, R_after_7, EA_7_2 ) );
                                Long pcv7 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), valueaaa2.toArray ()[0].toString () );
                                R_after_7.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_after_7.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv7.intValue (), Fullinstruction.length () ).toString () );
                                //setting OUT
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime7 - startTime7 ), "10", String.valueOf ( GetClock ()  ), String.valueOf ( 10 ) ) ) ) );
                                break;
                            case "1001": // store data  MEM[reg[rd] + immediate] =rs
                                //trace logic
                                String Cl_8 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                String INS_8 = "store " + new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) ) + "(" + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) + ")," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) );
                                List<String> R_8 = new ArrayList<> ();
                                R_8.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                R_8.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                List<String> R_before_8 = new ArrayList<> ();
                                R_before_8.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_before_8.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                //CPU Logic
                                long startTime8 = System.nanoTime ();
                                String immediate6 = String.valueOf ( new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) ) );
                                String rdcontents = pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) );
                                int DisplacementR = new LOADER.Unit ().BinaryToDecimal ( rdcontents ) +
                                        Integer.parseInt ( immediate6 );
                                List<String> valueaaa1c = new ArrayList<> ();
                                try {
                                    if (DisplacementR % 2 == 0) {
                                        if (DisplacementR < mem.getMemory ().size () || !mem.getMemory ().get ( String.valueOf ( DisplacementR ) ).getMemoryTpe ().equals ( "INS" )) {
                                            if (!mem.getMemory ().get ( String.valueOf ( DisplacementR ) ).getLock ()) {
                                                mem.getObject ( pcb.getPID (),"READ", String.valueOf ( DisplacementR ), valueaaa1c );
                                            } else {
//                                            String Erro = "MEMORY Location is locked";
//                                            Message = "Abnormal " + Erro;
//                                            throw new ERRORHANDLER(Erro);
                                            }
                                        } else {
                                            String Erro = "MEMORY RANGE FAULT";
                                            Message = "Abnormal " + Erro;
                                            throw new ERRORHANDLER ( Erro );
                                        }
                                    } else {
                                        String Erro = "Illigal memory Space Access";
                                        Message = "Abnormal " + Erro;
                                        throw new ERRORHANDLER ( Erro );
                                    }
                                } catch (Exception e) {
                                }
                                // mem.getObject("READ",String.valueOf(DisplacementR),valueaaa1c);
                                String EA_8_1 = immediate6;
                                List<String> valueaaa1a = new ArrayList<> ();
                                //memory lock need to implement
                                long startMemoryread8 = System.nanoTime ();
                                if (!mem.getMemory ().get ( String.valueOf ( DisplacementR ) ).getLock ()) {
                                    if (DisplacementR < mem.getMemory ().size () || mem.getMemory ().get ( String.valueOf ( DisplacementR ) ).getMemoryTpe ().equals ( "DATA" )) {
                                        int valuetocheck = new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) );
//                                    if (valuetocheck < (int) Math.pow(2, 8) && valuetocheck > -(int) Math.pow(2, 8)) {
                                        mem.getObject ( pcb.getPID (),"WRIT", String.valueOf ( DisplacementR ), new ArrayList<> ( Arrays.asList ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                        mem.getMemory ().get ( String.valueOf ( DisplacementR ) ).setMemoryTpe ( "DATA" );
                                        mem.getMemory ().get ( String.valueOf ( DisplacementR + 1 ) ).setMemoryTpe ( "DATA" );
//                                    } else {
//                                        String erroMessage = " Alloc Too Large";
//                                        Message = "Abnormal " + erroMessage;
//                                        throw new ERRORHANDLER(Message);
//                                    }
                                    } else {
                                        String Erro = "MEMORY RANGE FAULT";
                                        Message = "Abnormal " + Erro;
                                        throw new ERRORHANDLER ( Erro );
                                    }
                                    if (mem.getMemory ().get ( EA_8_1 ).getMemoryTpe ().equals ( "DATA" )) {
                                        mem.getObject ( pcb.getPID (),"READ", EA_8_1, valueaaa1a );
                                    } else {
                                        String Erro = "MEMORY RANGE FAULT";
                                        Message = "Abnormal " + Erro;
                                        throw new ERRORHANDLER ( Erro );
                                    }
                                } else {
//                                String ER = "Memory Location Is locked";
//                                Message = "Abnormal " + ER;
//                                throw new ERRORHANDLER(ER);
                                }
                                long endMemoryread8 = System.nanoTime ();
                                String EA_exe_8 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( valueaaa1a.toArray ()[0].toString () ) );
                                long endTime8 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_8 = new ArrayList<> ();
                                R_after_8.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_after_8.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                if (DisplacementR < mem.getMemory ().size () || mem.getMemory ().get ( String.valueOf ( DisplacementR ) ).getMemoryTpe ().equals ( "DATA" )) {
                                    mem.getObject ( pcb.getPID (),"READ", String.valueOf ( DisplacementR ), valueaaa1a );
                                } else {
                                    String e2 = "MEMORY RANGE FAULT";
                                    Message = "Abnormal " + e2;
                                    throw new ERRORHANDLER ( e2 );
                                }
                                String EA_8_2 = String.valueOf ( new LOADER.Unit ().Binary2sToDecimal ( valueaaa1a.toArray ()[0].toString () ) );
                                inbuiltcounter++;
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_8, INS_8, R_8, EA_8_1, R_before_8, EA_exe_8, R_after_8, EA_8_2 ) );
                                Long pcv8 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv8.intValue (), Fullinstruction.length () ).toString () );
                                //setting OUT
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime8 - startTime8 ), "10", String.valueOf ( GetClock () ), String.valueOf ( 10 ) ) ) ) );
                                break;
                            //Data manipulation and Conditional
                            case "1101": //  rd = (rs ==rt)
                                //trace logic
                                String Cl_9 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                String INS_9 = "seq " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) + "," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) + "," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RT" ) );
                                List<String> R_9 = new ArrayList<> ();
                                R_9.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                R_9.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                R_9.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RT" ) ) );
                                String EA_9 = " ";
                                List<String> R_before_9 = new ArrayList<> ();
                                R_before_9.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_before_9.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                R_before_9.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) ) ) );
                                String EA_exe_9 = " ";
                                //CPU Logic
                                long startTime9 = System.nanoTime ();
                                if (pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ).equals ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) )) {
                                    pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), new LOADER.Unit ().shortBinarytoBinary ( "1", Fullinstruction.length () ).toString () );
                                } else {
                                    pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), new LOADER.Unit ().shortBinarytoBinary ( "0", Fullinstruction.length () ).toString () );
                                }
                                long endTime9 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_9 = new ArrayList<> ();
                                R_after_9.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_after_9.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                R_after_9.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) ) ) );
                                String EA_9_2 = " ";
                                SetClock ( 1 );
                                inbuiltcounter++;
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_9, INS_9, R_9, EA_9, R_before_9, EA_exe_9, R_after_9, EA_9_2 ) );
                                Long pcv9 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv9.intValue (), Fullinstruction.length () ).toString () );
                                //setting OUT
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime9 - startTime9 ), "0", String.valueOf ( GetClock ()  ), String.valueOf ( 0 ) ) ) ) );
                                break;
                            case "1110": //  rd = (rs>rt)
                                //trace logic
                                String Cl_10 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                String INS_10 = "sgt " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) + "," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) + "," + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RT" ) );
                                List<String> R_10 = new ArrayList<> ();
                                R_10.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                R_10.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                R_10.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RT" ) ) );
                                String EA_10 = " ";
                                List<String> R_before_10 = new ArrayList<> ();
                                R_before_10.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_before_10.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                R_before_10.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) ) ) );
                                String EA_exe_10 = " ";
                                //CPU Logic
                                long startTime10 = System.nanoTime ();
                                Long aaa = Long.parseLong ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) );
                                if (new LOADER.Unit ().Binary2sToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) > new LOADER.Unit ().Binary2sToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) )) {
                                    pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), new LOADER.Unit ().shortBinarytoBinary ( "1", Fullinstruction.length () ).toString () );
                                } else {
                                    pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), new LOADER.Unit ().shortBinarytoBinary ( "0", Fullinstruction.length () ).toString () );
                                }
                                long endTime10 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_10 = new ArrayList<> ();
                                R_after_10.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_after_10.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                R_after_10.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) ) ) );
                                String EA_10_2 = " ";
                                SetClock ( 1 );
                                inbuiltcounter++;
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_10, INS_10, R_10, EA_10, R_before_10, EA_exe_10, R_after_10, EA_10_2 ) );
                                Long pcv10 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv10.intValue (), Fullinstruction.length () ).toString () );
                                //setting OUT
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime10 - startTime10 ), "0", String.valueOf ( GetClock ()  ), String.valueOf ( 0 ) ) ) ) );
                                break;
                            case "1111": //  rd = (rs!=rt)
                                //trace logic
                                String Cl_11 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                String INS_11 = "sne " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) + ", " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) + ", " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RT" ) );
                                List<String> R_11 = new ArrayList<> ();
                                R_11.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                R_11.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                R_11.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RT" ) ) );
                                String EA_11 = " ";
                                List<String> R_before_11 = new ArrayList<> ();
                                R_before_11.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_before_11.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                R_before_11.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) ) ) );
                                String EA_exe_11 = " ";
                                //CPU Logic
                                long startTime11 = System.nanoTime ();
                                if (!pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ).equals ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) )) {
                                    pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), new LOADER.Unit ().shortBinarytoBinary ( "1", Fullinstruction.length () ).toString () );
                                } else {
                                    pcb.thisPCB.reg.registers.put ( instructionAdd.get ( "RD" ), new LOADER.Unit ().shortBinarytoBinary ( "0", Fullinstruction.length () ).toString () );
                                }
                                long endTime11 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_11 = new ArrayList<> ();
                                R_after_11.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_after_11.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                R_after_11.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RT" ) ) ) ) );
                                String EA_11_2 = " ";
                                SetClock ( 1 );
                                inbuiltcounter++;
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_11, INS_11, R_11, EA_11, R_before_11, EA_exe_11, R_after_11, EA_11_2 ) );
                                Long pcv11 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv11.intValue (), Fullinstruction.length () ).toString () );
                                //setting OUT
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime11 - startTime11 ), "0", String.valueOf ( GetClock () ), String.valueOf ( 0 ) ) ) ) );
                                break;
                            //flow of control
                            case "0111": //  /  branch on equal to zero
                                //trace logic
                                String Cl_12 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                String INS_12 = "beqz (" + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) + ")," + new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) );
                                List<String> R_12 = new ArrayList<> ();
                                if (!instructionAdd.get ( "RD" ).equals ( "000" )) {
                                    String erroMessage = " Illegal Instruction";
                                    Message = "Abnormal " + erroMessage;
                                    throw new ERRORHANDLER (pcb.getPID (),Message, mem );
                                }
                                R_12.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RD" ) ) );
                                R_12.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                String EA_12 = " ";
                                List<String> R_before_12 = new ArrayList<> ();
                                R_before_12.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_before_12.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                String EA_exe_12 = " ";
                                //CPU Logic
                                long startTime12 = System.nanoTime ();
                                if (Long.parseLong ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) == 0) {
                                    int valuetoPCw = new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 +
                                            Integer.parseInt ( String.valueOf ( new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) ) ) );
                                    if (valuetoPCw % 2 == 0) {
                                        pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( valuetoPCw, Fullinstruction.length () ).toString () );
                                    } else {
                                        String Erro = "Illigal PC UPDATE";
                                        Message = "Abnormal " + Erro;
                                        throw new ERRORHANDLER (pcb.getPID (), Erro, mem );
                                    }
                                } else {
                                    Long Cvalue = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                    pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( Cvalue.intValue (), Fullinstruction.length () ).toString () );
                                }
                                long endTime12 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_12 = new ArrayList<> ();
                                R_after_12.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RD" ) ) ) ) );
                                R_after_12.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                String EA_12_2 = " ";
                                SetClock ( 1 );
                                inbuiltcounter++;
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_12, INS_12, R_12, EA_12, R_before_12, EA_exe_12, R_after_12, EA_12_2 ) );
                                //setting OUT
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime12 - startTime12 ), "0", String.valueOf ( GetClock ()  ), String.valueOf ( 0 ) ) ) ) );
                                break;
                            case "1100": //  branch on not equal to zero
                                //trace logic
                                String Cl_13 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                int value = new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) );
                                String INS_13 = "bnez " + pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) + "," + value;
                                if (!instructionAdd.get ( "RD" ).equals ( "000" )) {
                                    String erroMessage = " Illegal Instruction";
                                    Message = "Abnormal " + erroMessage;
                                    throw new ERRORHANDLER (pcb.getPID (),Message, mem );
                                }
                                List<String> R_13 = new ArrayList<> ();
                                R_13.add ( pcb.thisPCB.reg.registerName.get ( instructionAdd.get ( "RS" ) ) );
                                String EA_13 = " ";
                                List<String> R_before_13 = new ArrayList<> ();
                                R_before_13.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                String EA_exe_13 = " ";
                                //CPU Logic
                                long startTime13 = System.nanoTime ();
                                if (Long.parseLong ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) != 0) {
                                    int valuetoPC = new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 +
                                            Integer.parseInt ( String.valueOf ( new LOADER.Unit ().Binary2sToDecimal ( instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) ) ) );
                                    if (valuetoPC % 2 == 0) {
                                        pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( valuetoPC, Fullinstruction.length () ).toString () );
                                    } else {
                                        String Erro = "Illigal PC UPDATE";
                                        Message = "Abnormal " + Erro;
                                        throw new ERRORHANDLER (pcb.getPID (), Erro, mem );
                                    }
                                } else {
                                    Long Cvalue1 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                    pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( Cvalue1.intValue (), Fullinstruction.length () ).toString () );
                                }
                                long endTime13 = System.nanoTime ();
                                //trace logic
                                List<String> R_after_13 = new ArrayList<> ();
                                R_after_13.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( instructionAdd.get ( "RS" ) ) ) ) );
                                String EA_13_2 = " ";
                                SetClock ( 1 );
                                inbuiltcounter++;
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_13, INS_13, R_13, EA_13, R_before_13, EA_exe_13, R_after_13, EA_13_2 ) );
                                //setting OUT
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime13 - startTime13 ), "0", String.valueOf ( GetClock ()  ), String.valueOf ( 0 ) ) ) ) );
                                break;
                            case "0100": // Trap
                                //trace logic
                                String Cl_14 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                String Cl_14_trap0 = String.valueOf ( "");
                                String INS_14 = "";
                                List<String> R_14 = new ArrayList<> ();
                                String EA_14 = " ";
                                List<String> R_before_14 = new ArrayList<> ();
                                List<String> R_after_14 = new ArrayList<> ();
                                //CPU Logic
                                String immediate12 = Fullinstruction.substring ( 4 );
                                String EA_exe_14 = "";
                                int trapValue = new LOADER.Unit ().BinaryToDecimal ( immediate12 );
                                if (trapValue == 0) {
                                    long startTime14 = System.nanoTime ();
                                    //halt
                                    INS_14 = "trap " + new LOADER.Unit ().BinaryToDecimal ( instructionAdd.get ( "RS" ) + instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) );
                                    R_14.add ( "" );
                                    R_before_14.add ( "" );
                                    R_after_14.add ( "" );
//                               for (int ia = 0; ia <= 100; ia++) {
//                                   System.out.print(".");
//                               }
                                    Long pcv22 = (Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2);
                                    pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv22.intValue (), Fullinstruction.length () ).toString () );
                                    long endTime14 = System.nanoTime ();
                                    SetClock ( 1 );
                                    inbuiltcounter++;
                                    //setting OUT
                                    thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime14 - startTime14 ), "0", String.valueOf ( GetClock ()  ), String.valueOf ( 0 ) ) ) ) );
                                    String EA_14_2 = " ";
                                    thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_14_trap0, INS_14, R_14, EA_14, R_before_14, EA_exe_14, R_after_14, EA_14_2 ) );
                                    totalsize = 0;
                                    try {
                                        if (TraceSwitchStat.equals ( "1" )) {
                                            SYSTEM.PROGRAM.WriteToTraceFile ( pcb.getPID (),pcb.thisPCB.thisTrace.getAllTrace (), mem );
                                        }
                                        //write out
                                        SYSTEM.PROGRAM.WriteToOutputFile ( new ArrayList<> ( thisout.values () ), mem );
                                    } catch (Exception e) {
                                        //throw new ERRORHANDLER ( "CPUEx-in halt trap" + e.getMessage (), mem );
                                    }
                                    completed=SYSTEM.PROGRAM.Halt ();

                                } else if (trapValue == 1) {
                                    //asuming r1 is r0
                                    long startTime15 = System.nanoTime ();
                                    R_14.add ( pcb.thisPCB.reg.registerName.get ( "001" ) );
                                    R_before_14.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( "001" ) ) ) );
                                    String ContentsofR1 = pcb.thisPCB.reg.registers.get ( "001" );
                                    INS_14 = "trap " + new LOADER.Unit ().BinaryToDecimal ( instructionAdd.get ( "RS" ) + instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) );
                                    int vaaa = new LOADER.Unit ().Binary2sToDecimal ( ContentsofR1 );
                                    String hexvaluetoSTout = new LOADER.Unit ().BinaryToHex ( new LOADER.Unit ().DecimalToBinary ( vaaa, Fullinstruction.length () ) ).toString ();
                                    //printing hex values to stdout
                                    long startTime15_1 = System.nanoTime ();
                                    pcb.setRemainingQuantum ( pcb.getBurstTime () - GetClock () );
                                    pcb.setIOBlock ( true, 10 );
                                    SYSTEM.PROGRAM.WriteToOut ( "J"+pcb.getPID ()+" Out-> "+ hexvaluetoSTout );
                                    long endTime15_1 = System.nanoTime ();
                                    Long pcv1trap1 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                    pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv1trap1.intValue (), Fullinstruction.length () ).toString () );
                                    R_after_14.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( "001" ) ) ) );
                                    SetClock ( 1 );
                                    inbuiltcounter++;
                                    long endTime15 = System.nanoTime ();
                                    thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime15 - startTime15 ), String.valueOf ( endTime15_1 - startTime15_1 ), String.valueOf ( GetClock ()  ), String.valueOf ( 10 ) ) ) ) );
                                    String EA_14_2 = " ";
                                    thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_14, INS_14, R_14, EA_14, R_before_14, EA_exe_14, R_after_14, EA_14_2 ) );
                                    pcb.setIOBlock ( true, 10 );
                                    pcb.setRemainingQuantum ( pcb.getBurstTime () - GetClock () );
                                    blockednow=true;
                                    if (pcb.IsBlocked ()) {
                                        pcb.thisSchedule.setToBlockQ ( pcb );

                                        break;
                                    }
                                } else if (trapValue == 2) {
                                    long startTime16 = System.nanoTime ();
                                    R_14.add ( pcb.thisPCB.reg.registerName.get ( "001" ) );
                                    R_before_14.add ( String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.registers.get ( "001" ) ) ) );
                                    String vaa = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( instructionAdd.get ( "RS" ) + instructionAdd.get ( "RT" ) + instructionAdd.get ( "EX" ) ) );
                                    INS_14 = "trap " + vaa;
                                    String sa = "";
                                    sa = SYSTEM.PROGRAM.ReadInput ( "GiveInput to J"+pcb.getPID ()+":" );
                                    try {
                                        if (!new LOADER.Unit ().isNumber ( sa )) {
                                            Message = "Abnormal-USER Input ERROR";
                                            throw new ERRORHANDLER (pcb.getPID (),Message, mem );
                                        }
                                    } catch (Exception e) {
                                    }
                                    Long takenvalue = Long.parseLong ( sa.trim () );
                                    String BinarytoInsert = "";
                                    if (takenvalue.intValue () > 32767 || takenvalue.intValue () < -32767) {
                                        String aEa = " Register Allocation Too Large exception ";
                                        Message = "Abnormal " + aEa;
                                        throw new ERRORHANDLER ( aEa, mem );
                                    }
                                    BinarytoInsert = new LOADER.Unit ().DecimalToBinary ( takenvalue.intValue (), Fullinstruction.length () ).toString ();
                                    pcb.thisPCB.reg.registers.put ( "001", BinarytoInsert );
                                    Long pcv1trap2 = Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2;
                                    pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv1trap2.intValue (), Fullinstruction.length () ).toString () );
                                    R_after_14.add ( String.valueOf ( new LOADER.Unit ().Binary2sToDecimal ( pcb.thisPCB.reg.registers.get ( "001" ) ) ) );
                                    SetClock ( 1 );
                                    inbuiltcounter++;
                                    String EA_14_2 = " ";
                                    thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_14, INS_14, R_14, EA_14, R_before_14, EA_exe_14, R_after_14, EA_14_2 ) );
                                    long endTime16 = System.nanoTime ();
                                    thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endTime16 - startTime16 ), "0", String.valueOf ( GetClock () ), String.valueOf ( 10 ) ) ) ) );
                                    pcb.setIOBlock ( true, 10 );
                                    pcb.setRemainingQuantum ( burst - pcb.getCPUClock () );
                                    blockednow=true;
                                    if (pcb.IsBlocked ()) {
                                        if(pcb.thisSchedule.LookatBlockedProcess () !=null )
                                        {
                                            if(!pcb.thisSchedule.LookatBlockedProcess ().equals ( pcb   )) {
                                                pcb.thisSchedule.setToBlockQ ( pcb );
                                            }
                                        }
                                        else {
                                            pcb.thisSchedule.setToBlockQ ( pcb );
                                        }
                                        break;
                                    }
                                } else {
                                    String err = " - Illegal Instruction";
                                    Message = "Abnormal" + err;
                                    thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( 0 ), "0", String.valueOf (GetClock ()  ), String.valueOf ( 10 ) ) ) ) );
                                    throw new ERRORHANDLER ( err, mem );
                                }
                                //trace logic
                                // SetClock(1);

                                break;
                            //resource lock
                            case "0101": // lock
                                String Cl_19 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                String INS_19 = "";
                                List<String> R_19 = new ArrayList<> ();
                                List<String> R_before_19 = new ArrayList<> ();
                                List<String> R_after_19 = new ArrayList<> ();
                                String immediate12l = Fullinstruction.substring ( 4 );
                                String loclocation = String.valueOf ( new LOADER.Unit ().Binary2sToDecimal ( immediate12l ) );
                                INS_19 = "lock " + loclocation;
                                String EA_19 = loclocation;
                                long starttime19 = System.nanoTime ();
                                String EA_exe_19 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( mem.getMemory ().get ( String.valueOf ( loclocation ) ).getValue () ) );
                                if (Integer.parseInt ( loclocation ) % 2 == 0) {
                                    mem.getMemory ().get ( String.valueOf ( loclocation ) ).setLock ( true );
                                    mem.getMemory ().get ( String.valueOf ( Integer.valueOf ( loclocation ) + 1 ) ).setLock ( true );
                                } else {
                                    String Erro = "Illigal memory Space Access";
                                    Message = "Abnormal " + Erro;
                                    throw new ERRORHANDLER (pcb.getPID (), Erro, mem );
                                }
                                R_19.add ( " " );
                                R_before_19.add ( String.valueOf ( " " ) );
                                R_after_19.add ( String.valueOf ( " " ) );
                                Long pcv2a2 = (Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2);
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv2a2.intValue (), Fullinstruction.length () ).toString () );
                                long endtime19 = System.nanoTime ();
                                SetClock ( 11 );
                                String EA_19_9 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( mem.getMemory ().get ( String.valueOf ( loclocation ) ).getValue () ) );
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_19, INS_19, R_19, EA_19, R_before_19, EA_exe_19, R_after_19, EA_19_9 ) );
                                inbuiltcounter++;
                                thisout.put ( thisJobID + "~" + GetClock (), new OUT ( thisJobID, Message, new LOADER.Unit ().DecimalToBinary ( GetClock (), 6 ).toString (), new ArrayList<> ( Arrays.asList ( String.valueOf ( endtime19 - starttime19 ), "0", String.valueOf ( GetClock () ), String.valueOf ( 0 ) ) ) ) );

                                break;
                            case "0110": // unlock
                                String Cl_20 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( pcb.thisPCB.reg.getPCValue () ) + 2 );
                                String INS_20 = "";
                                List<String> R_20 = new ArrayList<> ();
                                List<String> R_before_20 = new ArrayList<> ();
                                List<String> R_after_20 = new ArrayList<> ();
                                R_20.add ( " " );
                                R_before_20.add ( "" );
                                R_after_20.add ( "" );
                                String immediate12ul = Fullinstruction.substring ( 4 );
                                String loclocationb = String.valueOf ( new LOADER.Unit ().Binary2sToDecimal ( immediate12ul ) );
                                INS_20 = "unlock " + loclocationb;
                                String EA_20 = loclocationb;
                                long starttime20 = System.nanoTime ();
                                String EA_exe_20 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( mem.getMemory ().get ( String.valueOf ( loclocationb ) ).getValue () ) );
                                if (Integer.parseInt ( loclocationb ) % 2 == 0) {
                                    mem.getMemory ().get ( loclocationb ).setLock ( false );
                                    mem.getMemory ().get ( String.valueOf ( Integer.parseInt ( loclocationb ) + 1 ) ).setLock ( false );
                                } else {
                                    String Erro = "Illigal memory Space Access";
                                    Message = "Abnormal " + Erro;
                                    throw new ERRORHANDLER (pcb.getPID (), Erro, mem );
                                }
                                Long pcv22aa = (Long.parseLong ( pcb.thisPCB.reg.getPCValue (), 2 ) + 2);
                                pcb.thisPCB.reg.setPCValue ( new LOADER.Unit ().DecimalToBinary ( pcv22aa.intValue (), Fullinstruction.length () ).toString () );
                                SetClock ( 11 );
                                String EA_20_9 = String.valueOf ( new LOADER.Unit ().BinaryToDecimal ( mem.getMemory ().get ( String.valueOf ( loclocationb ) ).getValue () ) );
                                thisTrace.put ( String.valueOf ( GetClock () ), new TRACEUNIT ( Cl_20, INS_20, R_20, EA_20, R_before_20, EA_exe_20, R_after_20, EA_20_9 ) );
                                inbuiltcounter++;
                                break;
                            default:
                                String Erro = "ILLegal INSTRUCTION";
                                Message = "Abnormal " + Erro;
                                throw new ERRORHANDLER (pcb.getPID (), Erro, mem );
                        }


                        if (inbuiltcounter >= MAXCYCLE.intValue () || GetClock ()>1500) {
                            String Erro = "INFINITE LOOP";
                            Message = "Abnormal " + Erro;
                            throw new ERRORHANDLER ( pcb.getPID (),Erro, mem );
                        }

                if (blockednow) {
                    pcb.setIsFinished ( false );
                    pcb.setIsActive ( false );
                    break;
                }

                if (completed) {
                    pcb.setIsFinished ( true );
                    pcb.setIsBlocked ( false );
                    CPUState.setVTU ( String.valueOf ( inbuiltcounter));
                    CPUState.setStatus ( true );
                    pcb.thisPCB.thisOUt=thisout;
                    SYSTEM.NormalTerminations+=1;
                }
            }


            TraceAndOut ( pcb, mem );
            CPUState.setVTU ( String.valueOf ( inbuiltcounter ));
        } catch (Exception e) {
            Message += "   ";
            thisout.put(thisJobID + "~" + GetClock(), new OUT(thisJobID, Message, new LOADER.Unit().DecimalToBinary(GetClock(), 6).toString(), new ArrayList<>(Arrays.asList( String.valueOf ( 0 ), String.valueOf ( 0 ), String.valueOf ( GetClock()),String.valueOf ( pcb.getIOtime () )))));
            CPUState.setVTU ( String.valueOf ( GetClock() ));
            CPUState.setStatus (true);
            pcb.setIsActive ( false );
            pcb.setIsFinished ( true );
            pcb.setIsBlocked ( false );
            pcb.SetisTerminated ( true );
            //stats region
            SYSTEM.AbnormalTerminations+=1;
            SYSTEM.AbnormalTimeLost+=pcb.getCPUClock ();


            pcb.thisPCB.thisOUt=thisout;
            if (TraceSwitchStat.equals ( "1" )) {
                SYSTEM.PROGRAM.WriteToTraceFile ( pcb.getPID (),pcb.thisPCB.thisTrace.getAllTrace (), mem );
            }
            //write out
            SYSTEM.PROGRAM.WriteToOutputFile ( new ArrayList<> ( thisout.values () ), mem );
        }
        return CPUState;
    }
    private void TraceAndOut(PROCESS pcb,  MEMORY mem) throws ERRORHANDLER {
        try {
            if (pcb.IsFinished () && !pcb.IsBlocked () && !pcb.IsActive ()) {
                if (TraceSwitchStat.equals ( "1" )) {
                    SYSTEM.PROGRAM.WriteToTraceFile ( pcb.getPID (),pcb.thisPCB.thisTrace.getAllTrace (), mem );
                }
                //write out
                SYSTEM.PROGRAM.WriteToOutputFile ( new ArrayList<> ( thisout.values () ), mem );
            }
            } catch(Exception e){
                throw new ERRORHANDLER ( "CPUEx-" + e.getMessage (), mem );
            }

    }

    /*
     * FunctionName: SplitInstruction
     * Description: split the given instruction into 4 parts asper specification
     * parameters:  takes in full isntruction 16bit
     * returns: return hashmap
     * lastChanged: 03/01/2019
     *
     * */
    public Map<String, String> SplitInstruction(String fullInstruction) {
        Map<String, String> ac = new LinkedHashMap<>();
        ac.put("RD", fullInstruction.substring(4, 7));
        ac.put("RS", fullInstruction.substring(7, 10));
        ac.put("RT", fullInstruction.substring(10, 13));
        ac.put("EX", fullInstruction.substring(13, 16));
        return ac;
    }

    public static class OUT {
        String jobid;
        String TermMessage;
        String ClockinHex;
        List<TimeUnit> RunTime;
        int Decimal;

        public OUT() {
        }

        /*
         * FunctionName: OUT constructor
         * Description: get the contents to the register
         * parameters:  takes in JOb,message,clock,timestamps
         * returns: OUT
         * lastChanged: 03/01/2019
         *
         * */
        public OUT(String JobID, String Message, String ClockinBinary, List<String> timestamp) throws ERRORHANDLER {
            RunTime = new LinkedList<>();
            jobid = JobID;
            TermMessage = Message;
            ClockinHex = new LOADER.Unit().BinaryToHex(ClockinBinary).toString();
            Decimal = new LOADER.Unit(ClockinHex).getDecimal();
            RunTime.add(new TimeUnit(Long.parseLong(timestamp.toArray()[0].toString()),
                    Long.parseLong(timestamp.toArray()[1].toString()),
                    Long.parseLong(timestamp.toArray()[2].toString()),
                    Long.parseLong(timestamp.toArray()[3].toString())));
        }

        @Override
        public String toString() {
            return String.format("%-2s %-50s %-2s %-10s", this.jobid, this.TermMessage, this.ClockinHex, this.RunTime);
        }
    }

    public static class TimeUnit {
        public Long ExeTime;
        public Long IOTime;
        public Long total;
        public Long ExeDec;
        public Long IOdec;
        public Long TotalDec;

        /*
         * FunctionName: TimeUnit Constructor
         * Description: TIME UNIT splitter
         * parameters:  takes in list of TimeUnits objects
         * returns: class
         * lastChanged: 03/01/2019
         *
         * */
        public TimeUnit(Long Exetime, Long IoTime, Long ExeDec, Long iOdec) {
            this.ExeTime = Exetime;
            this.IOTime = IoTime;
            this.total = Exetime + IoTime;
            this.ExeDec = ExeDec;
            this.IOdec = iOdec;
            this.TotalDec = ExeDec + iOdec;
        }

        public String getTotal() {
            return String.valueOf(total);
        }

        public String getTotalDec() {
            return String.valueOf(TotalDec);
        }

        public TimeUnit getTimeUnit() {
            return this;
        }

        public String getSeparate() {
            return "ETime:" + ExeTime + ",I/O:" + IOTime + "ns";
        }

        public String getSeparateDec() {
            return "ETime:" + ExeDec + ", I/O:" + IOdec + "VTUs";
        }

        @Override
        public String toString() {
            String rr = String.format(getSeparateDec() + " total: " + getTotalDec());
            // String.format(getSeparate()+" total: "+getTotal()+" ns");
            return rr;
        }
    }

    public class REGISTERS {
        Map<String, String> PC;
        Map<String, String> IR;
        Map<String, String> registers;
        Map<String, String> registerName;

        public REGISTERS() throws ERRORHANDLER {
            String bit = "0000000000000000";
            registers = new HashMap<>();
            registerName = new HashMap<>();
            PC = new HashMap<>();
            IR = new HashMap<>();
            for (int i = 0; i < 8; i++) {
                String value = new LOADER.Unit().DecimalToBinary(i, 3).toString();
                registers.put(value, bit);
                registerName.put(value, "r" + new LOADER.Unit().BinaryToDecimal(value));
            }
            PC.put("PC", bit);
            IR.put("IR", bit);
        }

        /*
         * FunctionName: getRegisters
         * Description: get initialized register content
         * parameters:  NA
         * returns: return hashmap
         * lastChanged: 03/01/2019
         *
         * */
        public Map<String, String> getRegisters() {
            return registers;
        }

        /*
         * FunctionName: getRegisterNames
         * Description: get initialized register Names (lookahead table for register names)
         * parameters:  NA
         * returns: return hashmap
         * lastChanged: 03/01/2019
         *
         * */
        public Map<String, String> getRegisterNames() {
            return registerName;
        }

        /*
         * FunctionName: isRegisterEmpty
         * Description: Check if registerValue is Zero
         * parameters:  NA
         * returns: return hashmap
         * lastChanged: 03/01/2019
         *
         * */
        public boolean isRegisterEmpty(String key) throws ERRORHANDLER {
            boolean status = false;
            String bit = "0000000000000000";
            try {
                status = registers.get(key) == bit;
            } catch (Exception e) {
                throw new ERRORHANDLER("TypeEx-" + e.getMessage(), mem);
            }
            return status;
        }

        /*
         * FunctionName: getRegisterValue
         * Description: get the contents of the register
         * parameters:  takes in key
         * returns: string in twos complement
         * lastChanged: 03/01/2019
         *
         * */
        public String getRegisterValue(String Key) throws ERRORHANDLER {
            return new LOADER.Unit(registers.get(Key)).getbinary();
        }

        /*
         * FunctionName: getPCValue
         * Description: get the contents of the PC
         * parameters:  NA
         * returns: string
         * lastChanged: 03/01/2019
         *
         * */
        public String getPCValue() {
            return PC.get("PC");
        }

        /*
         * FunctionName: SetPCValue
         * Description: Set the contents of the PC
         * parameters:  NA
         * returns: string
         * lastChanged: 03/01/2019
         *
         * */
        public void setPCValue(String value) {
            PC.put("PC", value);
        }

        /*
         * FunctionName: getIRValue
         * Description: get the contents of the IR
         * parameters:  NA
         * returns: string
         * lastChanged: 03/01/2019
         *
         * */
        public String getIRValue() {
            return IR.get("IR");
        }

        /*
         * FunctionName: getPCValue
         * Description: get the contents of the PC
         * parameters:  NA
         * returns: string
         * lastChanged: 03/01/2019
         *
         * */
        public void setIRValue(String value) {
            IR.put("IR", value);
        }

        /*
         * FunctionName: setRegisterValue
         * Description: get the contents to the register
         * parameters:  takes in key and vlaue strings
         * returns: string
         * lastChanged: 03/01/2019
         *
         * */
        public boolean setRegisterValue(String Key, String value) throws ERRORHANDLER {
            String val = new LOADER.Unit(value).getbinary();
            registers.put(Key, value);
            return true;
        }
    }

    public class TRACE {
        Map<String, TRACEUNIT> tr;

        public TRACE() {
            tr = new LinkedHashMap<>();
        }

        /*
         * FunctionName: TRACE Constructor
         * Description: TRACE OBject constructor
         * parameters:  takes in list of TRACE objects
         * returns: class
         * lastChanged: 03/01/2019
         *
         * */

        public TRACE(String key, String PC, String INSTRUCTION, List<String> REGISTERs,
                     String EffectiveAddress, List<String> ValuesinRegister_before,
                     String EffectiveAddress_exe, List<String> ValuesinRegister_after,
                     String EffectiveExecution_EA) {
            tr.put(key, new TRACEUNIT(PC, INSTRUCTION, REGISTERs, EffectiveAddress, ValuesinRegister_before, EffectiveAddress_exe
                    , ValuesinRegister_after, EffectiveExecution_EA));
        }

        /*
         * FunctionName: getTraceitem
         * Description: get the contents of the trace at some clock number
         * parameters:  takes in key
         * returns: TRACEUNIT
         * lastChanged: 03/01/2019
         *
         * */
        public TRACEUNIT getTraceitem(String key) throws ERRORHANDLER {
            TRACEUNIT a = null;
            if (tr.containsKey(key)) {
                a = tr.get(key);
            } else {
                throw new ERRORHANDLER("CODEex- error in processing the trace at the time " + key, mem);
            }
            return a;
        }

        public Map<String, TRACEUNIT> getAllTrace() {
            return tr;
        }
    }

    public class CPU_State
    {
        boolean status;
        String elapsed_VTU;

        public CPU_State()
        {
            this.status=getStatus();
            this.elapsed_VTU=getVTU();
        }

        public void setStatus(boolean status)
        {
            this.status=status;
        }
        public boolean getStatus()
        {
            return this.status;
        }
        public void setVTU(String VTU)
        {
            this.elapsed_VTU=VTU;

        }
        public String getVTU()
        {
            return this.elapsed_VTU;
        }

    }
    public class TRACEUNIT {
        String PC;
        String INS;
        List<String> reg;
        String EA;
        List<String> regvalue_before;
        String EA_exe;
        List<String> registerVlaue_After;
        String EA2;
        String combinedString = "";

        public TRACEUNIT(String PC, String INSTRUCTION, List<String> REGISTERs,
                         String EffectiveAddress, List<String> ValuesinRegister_before,
                         String EffectiveAddress_exe, List<String> ValuesinRegister_after,
                         String EffectiveExecution_EA) {
            regvalue_before = new LinkedList<>();
            reg = new LinkedList<>();
            this.PC = PC;
            this.INS = INSTRUCTION;
            this.reg = REGISTERs;
            this.EA = EffectiveAddress;
            this.regvalue_before = ValuesinRegister_before;
            this.EA_exe = EffectiveAddress_exe;
            this.registerVlaue_After = ValuesinRegister_after;
            this.EA2 = EffectiveExecution_EA;
        }

        public String getinLine(List<String> a, String spacing) {
            String newLinedString = "";
            for (String aa : a) {
                newLinedString += String.format(spacing, aa + "\n");
            }
            return newLinedString;
        }

        @Override
        public String toString() {
            String s = "";
            for (int i = 0; i < this.reg.size(); i++) {
                if (i == 0) {
                    s += String.format("%-3s %-14s %-3s %-3s %-12s %-12s %-10s %-10s\n", PC, this.INS, this.reg.get(i), this.EA, this.regvalue_before.get(i), this.EA_exe,
                            this.registerVlaue_After.get(i), this.EA2);
                } else {
                    s += String.format("%-3s %-14s %-3s %-3s %-12s %-12s %-10s %-10s\n", " ", " ", this.reg.get(i), " ", this.regvalue_before.get(i), " ",
                            this.registerVlaue_After.get(i), " ");
                }
            }
            return s;
        }
    }
}
