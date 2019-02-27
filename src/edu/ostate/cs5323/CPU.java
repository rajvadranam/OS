package edu.ostate.cs5323;

import java.io.*;
import java.util.*;

public class CPU extends ERRORHANDLER{


    public REGISTERS reg;
    public Map<String,String> rset;
    public Map<String,String> rname;
    public TRACE traceObj;
    public  Map<String,TRACEUNIT> thisTrace;
    public String GetTrace;
    public long PC=0000000000000000;
    public String IR;
    public  int CLOCK=0;






    public CPU(MEMORY memobj, LOADER.Unit trace) throws ERRORHANDLER {
        reg= new REGISTERS();
        GetTrace = trace.getbinary();
        traceObj= new TRACE();
        rset= reg.getRegisters();
        rname=reg.getRegisternames();
        //Initialize Instructionset and loop the CPU
        if(!CPU_I(memobj,traceObj))
        {
            throw new ERRORHANDLER("CPU not completed the user job");
        }

    }

    public boolean CPU_I(MEMORY mem,TRACE traceobj) throws ERRORHANDLER
    {
        boolean CPUStat=false;
        thisTrace=traceObj.getAllTrace();

        long startTime = System.nanoTime();

       try {
           while (true) {
               Map<String, MEMORY.MemoryState> contents = mem.getMemory();
               List<String> thisKeyset = new ArrayList<>(contents.keySet());
               int j = 0;

               for (int i = 0; i <= contents.keySet().size(); i++) {
                   //first four digits from memory
                   String Firsthalf = contents.get(thisKeyset.get(j)).getValue();
                   String secondhalf = contents.get(thisKeyset.get(j + 1)).getValue();
                   String Fullinstruction = Firsthalf + secondhalf;
                   j++;

                   String opcode = Fullinstruction.substring(0, 4);
                   Map<String, String> instructionAdd = SplitInstruction(Fullinstruction);


                   switch (opcode) {

                       //Arthematic




                       case "0000": // Adds the operation with register rd = rs + rt

                           //trace logic
                           String Cl_1=String.valueOf(PC);
                           String INS_1="add "+rname.get(instructionAdd.get("RD"))+","+rname.get(instructionAdd.get("RS"))+","+rname.get(instructionAdd.get("RT"));
                           List<String> R_1= new ArrayList<>();
                           R_1.add(rname.get(instructionAdd.get("RD")));
                           R_1.add(rname.get(instructionAdd.get("RS")));
                           R_1.add(rname.get(instructionAdd.get("RT")));
                           String EA=" ";
                           List<String> R_before_1= new ArrayList<>();
                           R_before_1.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_1.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           R_before_1.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RT")),2)));
                           String EA_exe=" ";


                           //CPU Logic

                           int value1 = Integer.parseInt(rset.get(instructionAdd.get("RS")), 2) +
                                   Integer.parseInt(rset.get(instructionAdd.get("RT")), 2);
                           rset.put(instructionAdd.get("RD"), new LOADER.Unit().DecimalToBinary(value1, Fullinstruction.length()).toString());


                           //trace logic
                           List<String> R_after_1= new ArrayList<>();
                           R_after_1.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_1.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           R_after_1.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RT")),2)));
                           String EA_2=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_1,INS_1,R_1,EA,R_before_1,EA_exe,R_after_1,EA_2));
                           CLOCK+=1;
                           PC+=Long.parseLong(new LOADER.Unit().DecimalToBinary(2, Fullinstruction.length()).toString());

                          break;








                       case "0001": // Add Immediate  operation with register rd = rs + immediate6

                           //trace logic
                           String Cl_2=String.valueOf(PC);
                           String INS_2="add "+rname.get(instructionAdd.get("RD"))+","+rname.get(instructionAdd.get("RS"))+", immediate6";
                           List<String> R_2= new ArrayList<>();
                           R_2.add(rname.get(instructionAdd.get("RD")));
                           R_2.add(rname.get(instructionAdd.get("RS")));
                           String EA_2_1=" ";
                           List<String> R_before_2= new ArrayList<>();
                           R_before_2.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_2.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_exe_2=" ";

                            //CPU Logic
                           String twos = new LOADER.Unit().doTwos(instructionAdd.get("RT") + instructionAdd.get("EX"));
                           int vlaue = Integer.parseInt(rset.get(instructionAdd.get("RS")), 2) +
                                   Integer.parseInt(twos, 2);
                           rset.put(instructionAdd.get("RD"), new LOADER.Unit().DecimalToBinary(vlaue, Fullinstruction.length()).toString());

                           //trace logic
                           List<String> R_after_2= new ArrayList<>();
                           R_after_2.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_2.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_2_2=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_2,INS_2,R_2,EA_2_1,R_before_2,EA_exe_2,R_after_2,EA_2_2));
                           CLOCK+=1;
                           PC+=Long.parseLong(new LOADER.Unit().DecimalToBinary(2, Fullinstruction.length()).toString());
                           break;






                       case "0010": // sub  operation with register rd = rs - rt



                           //trace logic
                           String Cl_3=String.valueOf(PC);
                           String INS_3="sub "+rname.get(instructionAdd.get("RD"))+","+rname.get(instructionAdd.get("RS"))+","+rname.get(instructionAdd.get("RT"));
                           List<String> R_3= new ArrayList<>();
                           R_3.add(rname.get(instructionAdd.get("RD")));
                           R_3.add(rname.get(instructionAdd.get("RS")));
                           R_3.add(rname.get(instructionAdd.get("RT")));
                           String EA_1_3=" ";
                           List<String> R_before_3= new ArrayList<>();
                           R_before_3.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_3.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           R_before_3.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RT")),2)));
                           String EA_exe_3=" ";

                           //CPU Logic
                           int value3 = Integer.parseInt(rset.get(instructionAdd.get("RS")), 2) -
                                   Integer.parseInt(rset.get(instructionAdd.get("RT")), 2);
                           rset.put(instructionAdd.get("RD"), new LOADER.Unit().DecimalToBinary(value3, Fullinstruction.length()).toString());

                           //trace logic
                           List<String> R_after_3= new ArrayList<>();
                           R_after_3.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_3.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           R_after_3.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RT")),2)));
                           String EA_2_3=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_3,INS_3,R_3,EA_1_3,R_before_3,EA_exe_3,R_after_3,EA_2_3));
                           CLOCK+=1;
                           PC+=Long.parseLong(new LOADER.Unit().DecimalToBinary(2, Fullinstruction.length()).toString());
                           break;





                       case "0011": // subi  operation with register rd = rs - immediate6

                           //trace logic
                           String Cl_4=String.valueOf(PC);
                           String INS_4="sub "+rname.get(instructionAdd.get("RD"))+","+rname.get(instructionAdd.get("RS"))+", immediate6";
                           List<String> R_4= new ArrayList<>();
                           R_4.add(rname.get(instructionAdd.get("RD")));
                           R_4.add(rname.get(instructionAdd.get("RS")));
                           String EA_4_1=" ";
                           List<String> R_before_4= new ArrayList<>();
                           R_before_4.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_4.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_exe_4=" ";

                           //CPU Logic
                           String GETTWOS = new LOADER.Unit().doTwos(instructionAdd.get("RT") + instructionAdd.get("EX"));
                           int vlaue4 = Integer.parseInt(rset.get(instructionAdd.get("RS")), 2) -
                                   Integer.parseInt(GETTWOS, 2);
                           rset.put(instructionAdd.get("RD"), new LOADER.Unit().DecimalToBinary(vlaue4, Fullinstruction.length()).toString());


                           //trace logic
                           List<String> R_after_4= new ArrayList<>();
                           R_after_4.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_4.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_4_2=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_4,INS_4,R_4,EA_4_1,R_before_4,EA_exe_4,R_after_4,EA_4_2));
                           CLOCK+=1;
                           PC+=Long.parseLong(new LOADER.Unit().DecimalToBinary(2, Fullinstruction.length()).toString());
                           break;

                       //Data Movement
                       case "1010": // moves rd <-- rs,ttt=0
                           //question ttt??
                           //trace logic
                           String Cl_5=String.valueOf(PC);
                           String INS_5="mov "+rname.get(instructionAdd.get("RD"))+","+rname.get(instructionAdd.get("RS"))+", ttt=0";
                           List<String> R_5= new ArrayList<>();
                           R_5.add(rname.get(instructionAdd.get("RD")));
                           R_5.add(rname.get(instructionAdd.get("RS")));
                           String EA_5_1=" ";
                           List<String> R_before_5= new ArrayList<>();
                           R_before_5.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_5.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_exe_5=" ";

                           //CPU Logic

                           rset.put(instructionAdd.get("RD"), rset.get(instructionAdd.get("RS")));

                           //trace logic
                           List<String> R_after_5= new ArrayList<>();
                           R_after_5.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_5.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_5_2=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_5,INS_5,R_5,EA_5_1,R_before_5,EA_exe_5,R_after_5,EA_5_2));
                           CLOCK+=1;
                           PC+=Long.parseLong(new LOADER.Unit().DecimalToBinary(2, Fullinstruction.length()).toString());
                           break;


                       case "1011": // Moves data  rd =immed6 ,rs=000
                           //question rs =???

                           //trace logic
                           String Cl_6=String.valueOf(PC);
                           String INS_6="movi "+rname.get(instructionAdd.get("RD"))+","+"immediate6 and RS=000";
                           List<String> R_6= new ArrayList<>();
                           R_6.add(rname.get(instructionAdd.get("RD")));
                           R_6.add(rname.get(instructionAdd.get("RS")));
                           String EA_6_1=" ";
                           List<String> R_before_6= new ArrayList<>();
                           R_before_6.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_6.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_exe_6=" ";

                           //CPU Logic
                           //setting rs=000
                           rset.put(instructionAdd.get("RS"),new LOADER.Unit().shortBinarytoBinary("000", Fullinstruction.length()).toString());

                           String GETTWOS2 = new LOADER.Unit().doTwos(instructionAdd.get("RT") + instructionAdd.get("EX"));
                           rset.put(instructionAdd.get("RD"), new LOADER.Unit().shortBinarytoBinary(GETTWOS2, Fullinstruction.length()).toString());

                           //trace logic
                           List<String> R_after_6= new ArrayList<>();
                           R_after_6.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_6.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_6_2=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_6,INS_6,R_6,EA_6_1,R_before_6,EA_exe_6,R_after_6,EA_6_2));
                           CLOCK+=11;
                           PC+=Long.parseLong(new LOADER.Unit().DecimalToBinary(2, Fullinstruction.length()).toString());
                           break;

                       case "1000": // load data  rd = MEM[reg[rs] + immediate]

                           //trace logic
                           String Cl_7=String.valueOf(PC);
                           String INS_7="load "+rname.get(instructionAdd.get("RD"))+","+"immediate6( "+rname.get(instructionAdd.get("RS"))+")";
                           List<String> R_7= new ArrayList<>();
                           R_7.add(rname.get(instructionAdd.get("RD")));
                           R_7.add(rname.get(instructionAdd.get("RS")));

                           List<String> R_before_7= new ArrayList<>();
                           R_before_7.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_7.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_exe_7="0";

                           //CPU Logic
                           String immediatepart = new LOADER.Unit().shortBinarytoBinary(instructionAdd.get("RT") + instructionAdd.get("EX"), Fullinstruction.length()).toString();
                           String EA_7_1=String.valueOf(Integer.parseInt(immediatepart, 2));
                           String rscontents = rset.get(instructionAdd.get("RS"));
                           int Displacement = Integer.parseInt(rscontents, 2) +
                                   Integer.parseInt(immediatepart, 2);
//                           String Displcaementbinary = new LOADER.Unit().DecimalToBinary(Displacement, Fullinstruction.length()).toString();
//
//                           String GETTWOSa = new LOADER.Unit().doTwos(Displcaementbinary);
                           //getting memory contents
                           MEMORY.MemoryState valueaaa = contents.get(String.valueOf(Displacement));
                           rset.put(instructionAdd.get("RD"), new LOADER.Unit().shortBinarytoBinary(valueaaa.getValue(), Fullinstruction.length()).toString());


                           //trace logic
                           List<String> R_after_7= new ArrayList<>();
                           R_after_7.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_7.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_7_2=String.valueOf(Displacement);;

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_7,INS_7,R_7,EA_7_1,R_before_7,EA_exe_7,R_after_7,EA_7_2));
                           CLOCK+=11;
                           PC+=Long.parseLong(new LOADER.Unit().DecimalToBinary(2, Fullinstruction.length()).toString());
                           break;


                       case "1001": // store data  MEM[reg[rd] + immediate] =rs

                           //trace logic
                           String Cl_8=String.valueOf(PC);
                           String INS_8="store "+"immediate6( "+rname.get(instructionAdd.get("RD"))+") ,"+rname.get(instructionAdd.get("RS"));
                           List<String> R_8= new ArrayList<>();
                           R_8.add(rname.get(instructionAdd.get("RD")));
                           R_8.add(rname.get(instructionAdd.get("RS")));

                           List<String> R_before_8= new ArrayList<>();
                           R_before_8.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_8.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_exe_8="0";

                           //CPU Logic

                           String immediate6 = new LOADER.Unit().shortBinarytoBinary(instructionAdd.get("RT") + instructionAdd.get("EX"), Fullinstruction.length()).toString();
                           String rdcontents = rset.get(instructionAdd.get("RD"));
                           String EA_8_1=String.valueOf(Integer.parseInt(immediate6, 2));
                           int DisplacementR = Integer.parseInt(rdcontents, 2) +
                                   Integer.parseInt(immediate6, 2);
                           //String Displcaementbinaryr= new LOADER.Unit().DecimalToBinary(DisplacementR,Fullinstruction.length()).toString();


                           //Setting memory contents
                           int firstsave = rset.get(instructionAdd.get("RS")).length() / 2;
                           int secondsave = rset.get(instructionAdd.get("RS")).length();
                           //memory lock need to implement
                           if(!contents.get(String.valueOf(DisplacementR)).getLock()) {
                               contents.put(String.valueOf(DisplacementR), new MEMORY.MemoryState(rset.get(instructionAdd.get("RS")).substring(0, firstsave)));
                           }
                        DisplacementR++;
                           if(!contents.get(String.valueOf(DisplacementR)).getLock()) {

                               contents.put(String.valueOf(DisplacementR),new MEMORY.MemoryState(rset.get(instructionAdd.get("RS")).substring(firstsave, secondsave)));
                           }


                           //trace logic
                           List<String> R_after_8= new ArrayList<>();
                           R_after_8.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_8.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_8_2=String.valueOf(DisplacementR);

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_8,INS_8,R_8,EA_8_1,R_before_8,EA_exe_8,R_after_8,EA_8_2));
                           CLOCK+=11;
                           PC+=Long.parseLong(new LOADER.Unit().DecimalToBinary(2, Fullinstruction.length()).toString());
                           break;




                       //Data manipulation and Conditional
                       case "1101": //  rd = (rs ==rt)


                           //trace logic
                           String Cl_9=String.valueOf(PC);
                           String INS_9="seq "+rname.get(instructionAdd.get("RD"))+", "+ rname.get(instructionAdd.get("RS"))+", "+rset.get(instructionAdd.get("RT"));
                           List<String> R_9= new ArrayList<>();
                           R_9.add(rname.get(instructionAdd.get("RD")));
                           R_9.add(rname.get(instructionAdd.get("RS")));
                           R_9.add(rname.get(instructionAdd.get("RT")));
                           String EA_9=" ";
                           List<String> R_before_9= new ArrayList<>();
                           R_before_9.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_9.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           R_before_9.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RT")),2)));

                           String EA_exe_9=" ";


                           //CPU Logic
                           if (rset.get(instructionAdd.get("RS")).equals(rset.get(instructionAdd.get("RT")))) {
                               rset.put(instructionAdd.get("RD"), new LOADER.Unit().shortBinarytoBinary("1", Fullinstruction.length()).toString());
                           }

                           //trace logic
                           List<String> R_after_9= new ArrayList<>();
                           R_after_9.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_9.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           R_after_9.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RT")),2)));
                           String EA_9_2=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_9,INS_9,R_9,EA_9,R_before_9,EA_exe_9,R_after_9,EA_9_2));
                           CLOCK+=1;
                           PC+=Long.parseLong(new LOADER.Unit().DecimalToBinary(2, Fullinstruction.length()).toString());

                           break;



                       case "1110": //  rd = (rs>rt)

                           //trace logic
                           String Cl_10=String.valueOf(PC);
                           String INS_10="sgt "+rname.get(instructionAdd.get("RD"))+", "+ rname.get(instructionAdd.get("RS"))+", "+rset.get(instructionAdd.get("RT"));
                           List<String> R_10= new ArrayList<>();
                           R_10.add(rname.get(instructionAdd.get("RD")));
                           R_10.add(rname.get(instructionAdd.get("RS")));
                           R_10.add(rname.get(instructionAdd.get("RT")));
                           String EA_10=" ";
                           List<String> R_before_10= new ArrayList<>();
                           R_before_10.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_10.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           R_before_10.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RT")),2)));

                           String EA_exe_10=" ";


                           //CPU Logic
                           if (Long.parseLong(rset.get(instructionAdd.get("RS"))) > Long.parseLong(rset.get(instructionAdd.get("RT")))) {
                               rset.put(instructionAdd.get("RD"), new LOADER.Unit().shortBinarytoBinary("1", Fullinstruction.length()).toString());
                           }


                           //trace logic
                           List<String> R_after_10= new ArrayList<>();
                           R_after_10.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_10.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           R_after_10.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RT")),2)));
                           String EA_10_2=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_10,INS_10,R_10,EA_10,R_before_10,EA_exe_10,R_after_10,EA_10_2));
                           CLOCK+=1;
                           PC+=Long.parseLong(new LOADER.Unit().DecimalToBinary(2, Fullinstruction.length()).toString());




                           break;





                       case "1111": //  rd = (rs!=rt)

                           //trace logic
                           String Cl_11=String.valueOf(PC);
                           String INS_11="sne "+rname.get(instructionAdd.get("RD"))+", "+ rname.get(instructionAdd.get("RS"))+", "+rset.get(instructionAdd.get("RT"));
                           List<String> R_11= new ArrayList<>();
                           R_11.add(rname.get(instructionAdd.get("RD")));
                           R_11.add(rname.get(instructionAdd.get("RS")));
                           R_11.add(rname.get(instructionAdd.get("RT")));
                           String EA_11=" ";
                           List<String> R_before_11= new ArrayList<>();
                           R_before_11.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_11.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           R_before_11.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RT")),2)));

                           String EA_exe_11=" ";

                           //CPU Logic
                           if (!rset.get(instructionAdd.get("RS")).equals(rset.get(instructionAdd.get("RT")))) {
                               rset.put(instructionAdd.get("RD"), new LOADER.Unit().shortBinarytoBinary("1", Fullinstruction.length()).toString());
                           }


                           //trace logic
                           List<String> R_after_11= new ArrayList<>();
                           R_after_11.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_11.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           R_after_11.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RT")),2)));
                           String EA_11_2=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_11,INS_11,R_11,EA_11,R_before_11,EA_exe_11,R_after_11,EA_11_2));
                           CLOCK+=1;
                           PC+=Long.parseLong(new LOADER.Unit().DecimalToBinary(2, Fullinstruction.length()).toString());

                           break;


                       //flow of control
                       case "0111": //  /  branch on equal to zero

                           //trace logic
                           String Cl_12=String.valueOf(PC);
                           String INS_12="beqz "+rname.get(instructionAdd.get("RD"))+", "+ rname.get(instructionAdd.get("RS"))+","+rname.get(instructionAdd.get("RT"))+rname.get(instructionAdd.get("EX"));
                           List<String> R_12= new ArrayList<>();
                           R_12.add(rname.get(instructionAdd.get("RD")));
                           R_12.add(rname.get(instructionAdd.get("RS")));

                           String EA_12=" ";
                           List<String> R_before_12= new ArrayList<>();
                           R_before_12.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_12.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));

                           String EA_exe_12=" ";



                            //CPU Logic
                           if (Long.parseLong(rset.get(instructionAdd.get("RS"))) == 0) {

                               int valuetoPCw = Integer.parseInt(String.valueOf(PC), 2) +
                                       Integer.parseInt(new LOADER.Unit().shortBinarytoBinary(instructionAdd.get("RT") + instructionAdd.get("EX"), Fullinstruction.length()).toString(), 2);
                               PC += Long.parseLong(new LOADER.Unit().DecimalToBinary(valuetoPCw, Fullinstruction.length()).toString());
                           }

                           //trace logic
                           List<String> R_after_12= new ArrayList<>();
                           R_after_12.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_12.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_12_2=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_12,INS_12,R_12,EA_12,R_before_12,EA_exe_12,R_after_12,EA_12_2));
                           CLOCK+=1;



                          break;

                       case "1100": //  branch on not equal to zero
                           //trace logic
                           String Cl_13=String.valueOf(PC);
                           String INS_13="bnez "+rname.get(instructionAdd.get("RD"))+", "+ rname.get(instructionAdd.get("RS"))+", immediate6 ("+rname.get(instructionAdd.get("RT"))+rname.get(instructionAdd.get("EX"))+")";
                           List<String> R_13= new ArrayList<>();
                           R_13.add(rname.get(instructionAdd.get("RD")));
                           R_13.add(rname.get(instructionAdd.get("RS")));

                           String EA_13=" ";
                           List<String> R_before_13= new ArrayList<>();
                           R_before_13.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_before_13.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));

                           String EA_exe_13=" ";



                           //CPU Logic
                           if (Long.parseLong(rset.get(instructionAdd.get("RS"))) != 0) {

                               int valuetoPC = Integer.parseInt(String.valueOf(PC), 2) +
                                       Integer.parseInt(new LOADER.Unit().shortBinarytoBinary(instructionAdd.get("RT") + instructionAdd.get("EX"), Fullinstruction.length()).toString(), 2);
                               PC +=  Long.parseLong(new LOADER.Unit().DecimalToBinary(valuetoPC, Fullinstruction.length()).toString());
                           }

                           //trace logic
                           List<String> R_after_13= new ArrayList<>();
                           R_after_13.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));
                           R_after_13.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RS")),2)));
                           String EA_13_2=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_13,INS_13,R_13,EA_13,R_before_13,EA_exe_13,R_after_13,EA_13_2));
                           CLOCK+=1;

                          break;
                       case "0100": // Trap

                           //trace logic
                           String Cl_14=String.valueOf(PC);
                           String INS_14="trap "+" immediate12";
                           List<String> R_14= new ArrayList<>();
                           R_14.add(rname.get(instructionAdd.get("RD")));

                           String EA_14=" ";
                           List<String> R_before_14= new ArrayList<>();
                           R_before_14.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));




                           //CPU Logic
                           String immediate12 = Fullinstruction.substring(4, Fullinstruction.length()).toString();
                           String EA_exe_14=String.valueOf(Integer.parseInt(immediate12, 2));
                           int trapValue = Integer.parseInt(immediate12, 2);
                           if (trapValue == 0) {
                               //halt
                               System.out.println("halting.....");
                               for (int ia = 0; ia <= 100; ia++) {
                                   System.out.print(".");
                               }
                           } else if (trapValue == 1) {
                               //asuming r1 is r0
                               String ContentsofR1 = rset.get("000");

                               String hexvaluetoSTout = new LOADER.Unit().BinaryToHex(ContentsofR1.toString()).toString();
                               //printing hex values to stdout
                               System.out.println(hexvaluetoSTout);

                           } else if (trapValue == 2) {

                               List<String> s = new ArrayList<>();
                               BufferedReader br = null;
                               try {
                                   br = new BufferedReader(new InputStreamReader(System.in));
                                   StringTokenizer st = new StringTokenizer(br.readLine());
                                   while (st != null && st.hasMoreElements()) {
                                       s.add(st.nextToken());
                                   }
                                   //asuming r1 is r0
                                   String BinarytoInsert = new LOADER.Unit().DecimalToBinary(Integer.parseInt(s.toString()), Fullinstruction.length()).toString();
                                   rset.put("000", BinarytoInsert);

                               } catch (IOException e) {

                               }

                           }

                           //trace logic
                           List<String> R_after_14= new ArrayList<>();
                           R_after_14.add(String.valueOf(Integer.parseInt(rset.get(instructionAdd.get("RD")),2)));

                           String EA_14_2=" ";

                           thisTrace.put(String.valueOf(CLOCK),new TRACEUNIT(Cl_14,INS_14,R_14,EA_14,R_before_14,EA_exe_14,R_after_14,EA_14_2));
                           CLOCK+=1;

                          break;


                       //resource lock

                       case "0101": // lock

                           String immediate12l = Fullinstruction.substring(4, Fullinstruction.length()).toString();
                           int loclocation = Integer.parseInt(immediate12l, 2);
                           contents.get(String.valueOf(loclocation)).setLock(true);

                          break;
                       case "0110": // unlock
                           String immediate12ul = Fullinstruction.substring(4, Fullinstruction.length()).toString();
                           int unloclocation = Integer.parseInt(immediate12ul, 2);
                           contents.get(String.valueOf(unloclocation)).setLock(false);


                          break;



                   }
               }
               long endtime = System.nanoTime();
               System.out.println((endtime - startTime) / 1000000 + " in ms");

           }
       }
       catch (ERRORHANDLER e)
       {
           CPUStat=false;
           e.printStackTrace();
       }
       finally {
           if( Integer.parseInt(GetTrace)==1)
           {
               try {
                   traceobj.WriteToFile(traceobj.getAllTrace());
               }
               catch (Exception e)
               {

               }

           }
       }
       return CPUStat;
    }








    public  Map<String,String> SplitInstruction(String fullInstruction) throws ERRORHANDLER
    {
        Map<String,String> ac = new LinkedHashMap<>();
        ac.put("RD",fullInstruction.substring(4,7));
        ac.put("RS",fullInstruction.substring(7,10));
        ac.put("RT",fullInstruction.substring(10,13));
        ac.put("EX",fullInstruction.substring(13,16));
        return ac;
    }


























































































    public  class REGISTERS
    {
        Map<String,String> PC;
        Map<String,String> IR;
        Map<String,String> registers;
        Map<String,String> registerName;
        public REGISTERS() throws ERRORHANDLER
        {
            String bit="0000000000000000";
            registers=new HashMap<>();
            registerName=new HashMap<>();
            PC=new HashMap<>();
            IR=new HashMap<>();

            for(int i=0;i<8;i++)
            {
                String value= new LOADER.Unit(String.valueOf(i)).getShortBinary();
                registers.put(value,bit);
                registerName.put(value,"r"+String.valueOf(Integer.parseInt(value,2)));
            }



            PC.put("PC",bit);
            IR.put("IR",bit);
        }

        public Map<String,String> getRegisters(){return registers;};
        public Map<String,String> getRegisternames(){return registerName;};


        public boolean isRegisterEmpty(String key)
        {
            boolean status = false;
            String bit="0000000000000000";
            try {
                if (registers.get(key) == bit) {
                    status = true;
                } else {
                    status = false;
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
         return status;
        }

        public String getRegisterValue(String Key) throws ERRORHANDLER
        {
            return new LOADER.Unit(registers.get(Key)).getTwos();
        }

        public String getPCValue() throws ERRORHANDLER
        {
            return new LOADER.Unit(PC.get("PC")).getTwos();
        }

        public String getIRValue() throws ERRORHANDLER
        {
            return new LOADER.Unit(IR.get("IR")).getTwos();
        }

        public boolean setRegisterValue(String Key,String value) throws ERRORHANDLER
        {
            String val = new LOADER.Unit(value).getTwos();
            registers.put(Key,value);
            return true;
        }


    }




    public class TRACE
    {
        Map<String,TRACEUNIT> tr;


        public TRACE()
        {
            tr=new LinkedHashMap<>();
        }

        public TRACE(String key,String PC,String INSTRUCTION,List<String> REGISTERs,
                     String EffectiveAddress,List<String> ValuesinRegister_before,
                     String EffectiveAddress_exe,List<String> ValuesinRegister_after,
                     String EffectiveExecution_EA)
        {

            tr.put(key,new TRACEUNIT(PC,INSTRUCTION,REGISTERs,EffectiveAddress,ValuesinRegister_before,EffectiveAddress_exe
            ,ValuesinRegister_after,EffectiveExecution_EA));

        }
        public TRACEUNIT getTraceitem(String key)
        {
            return tr.get(key);
        }

        public Map<String,TRACEUNIT> getAllTrace()
        {
            return tr;
        }

        public boolean WriteToFile(Map<String,TRACEUNIT> aaa) throws IOException {
            boolean status=false;

            File fout = new File("Trace.txt");
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(String.format("%-10s %-15s %-8s %-8s %-10s %-8s %-10s %-8s\n",
                    "PC","INSTRUCTION","R","EA","(R)-Before","(EA)-Execution",
                    "(R)-after","(EA)-Execution"));
            bw.newLine();
            int i=0;
            for (Map.Entry<String,TRACEUNIT> aae : aaa.entrySet()) {
                if(i%10!=0) {
                    bw.write(aae.getValue().toString());
                    bw.newLine();
                }
                else
                {
                    bw.newLine();
                }
            }

            bw.close();
            status=true;
            return status;
        }








        }
    public class TRACEUNIT{
            String PC;
            String INS;
            List<String> reg;
            String EA;
            List<String> regvalue_before;
            String EA_exe;
            List<String> registerVlaue_After;
            String EA2;
            String combinedString="";
                public TRACEUNIT(String PC,String INSTRUCTION,List<String> REGISTERs,
                String EffectiveAddress,List<String> ValuesinRegister_before,
                String EffectiveAddress_exe,List<String> ValuesinRegister_after,
                String EffectiveExecution_EA)
            {
                regvalue_before=new LinkedList<>();
                reg=new LinkedList<>();
                this.PC=PC;
                this.INS=INSTRUCTION;
                this.reg=REGISTERs;
                this.EA=EffectiveAddress;
                this.regvalue_before=ValuesinRegister_before;
                this.EA_exe=EffectiveAddress_exe;
                this.registerVlaue_After=ValuesinRegister_after;
                this.EA2=EffectiveExecution_EA;
                this.combinedString=String.format("%-10s %-15s %-8s %-8s %-10s %-8s %-10s %-8s\n",
                             PC,INSTRUCTION,getinLine(REGISTERs),EffectiveAddress,getinLine(ValuesinRegister_before),EffectiveAddress_exe,
                                getinLine(ValuesinRegister_after),EffectiveExecution_EA);


            }



            public String getinLine(List<String> a)
            {
                String newLinedString = "";
                for (String aa : a)
                {
                    newLinedString += a+"\n";
                }


                return newLinedString;
            }

        @Override
        public String toString() {
            return String.format(combinedString);
        }


    }
}
