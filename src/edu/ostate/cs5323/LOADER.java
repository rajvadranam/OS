package edu.ostate.cs5323;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class LOADER extends ERRORHANDLER{

    public Map<String, List<Unit>> loadmap;
    public List<Unit> loaderunits;
    private String filename;
    public Unit TraceSwitch;
    public LOADER(String UserprogFileName,MEMORY obj) throws ERRORHANDLER {
        loadmap = new LinkedHashMap<>();
        loaderunits = new LinkedList<>();
        filename=UserprogFileName;
        setToMemory(obj);

    }

    public Map<String, List<Unit>> getLoadmap()
    {
        return loadmap;
    }

    public List<Unit>  getLoadunits()
    {
        return loaderunits;
    }

    public Map<String, MEMORY.MemoryState> setToMemory(MEMORY obj) throws ERRORHANDLER
    {
        GetLoaderContents(filename,loaderunits,loadmap);
        Map<String, MEMORY.MemoryState> memory = obj.getMemory();
        int j=0;
        int i=0;
        for(Unit u : loaderunits)
        {

                if(j!=0 && j!=loaderunits.size()-1) {
//                if(Integer.parseInt(loaderunits.get(j).decimal)< obj.freememory)
//                {

                    int len = u.getbinary().length();
                    memory.put(String.valueOf(i), new MEMORY.MemoryState(u.getbinary().substring(0, (len / 2))));
                    i++;
                    memory.put(String.valueOf(i), new MEMORY.MemoryState(u.getbinary().substring((len / 2), len)));
                    i++;
                    j++;
                }
                else
                {
                    if(j==loaderunits.size()-1)
                    {
                        TraceSwitch = u;
                    }
                    j++;
                }
//
//                }

            }



        return obj.getMemory();
    }

     public Unit getTraceSwitch() throws ERRORHANDLER
     {
         return TraceSwitch;
     }

    //reusables
    /*
     * FunctionName: GetLoaderContents
     * Description: Should be used to get loader file contents
     * parameters: takes string <filename>
     * outs: hashmap with PC as identifier
     * lastChanged: 2/12/2019
     *
     * */
    public void  GetLoaderContents(String filename, List<Unit> loaderunits, Map<String,List<Unit>> loadmap) throws ERRORHANDLER{
        int i = 0;

         try {
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(filename));
             String firstvalue ="";
             List<LOADER.Unit> thisline = loaderunits;
            while((line=br.readLine())!=null){



//                String[] columns = line.split(" ");
//                if (columns.length == 3) {
                    i++;
                    if(line.trim().length()==3) {
                        thisline.add(new LOADER.Unit(line.trim()));
                        if(firstvalue=="") {
                            firstvalue = line.trim();
                        }
                    }
                    else {
                        List<String> programCode = SplitInterval(line.trim(), 4);
                        for (String s : programCode) {
                            thisline.add(new LOADER.Unit(s));
                        }

                    }
                    if(i==5) {
                        loadmap.put(String.valueOf((i-i)+1), thisline);
                        firstvalue="";
                        thisline = new ArrayList<>();
                    }
//                }

            }
        }
        catch (Exception e) {
            System.out.println("Error Occured in reading or opening the loader file"+e.getMessage());
            System.exit(-1);
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

    public List<String> SplitInterval(String IN,int splicharlength) throws ERRORHANDLER
    {
        int totalcharlenght = IN.length();
        List<String> result = new ArrayList<>();
        int org = splicharlength;
        for (int i=0;i<(totalcharlenght/org);i++)
        {
            result.add(IN.substring(i*org,splicharlength));
            splicharlength+=org;
        }

        return result;
    }

    public static class Unit
    {
        private String binary;
        private String sbinary;
        private String Hex;
        private String decimal;
        private String ones;
        private String twos;

        public Unit(String hex) throws ERRORHANDLER
        {
            this.Hex =hex;
            this.binary = HexToBinary(hex,16).toString();
            this.decimal= HexToDecimal(hex).toString();
            this.ones=doOnes(binary);
            this.twos=doTwos(binary);
            this.sbinary = HexToBinary(hex,3).toString();
        }

        public Unit() {

        }





        /*
         * FunctionName: Transform
         * Description: Should be used to transform Hex to binary and binary to Hex
         * parameters:
         * returns:
         * lastChanged:
         *
         * */

        public Object HexToBinary(Object s,int numberofbits) throws ERRORHANDLER
        {
            String Binary="0";
            if(isHEX(s.toString()))
            {
                long num = Long.parseLong(s.toString(), 16);
                Binary=Long.toBinaryString(num);
                int length=Binary.length();
                if(length != numberofbits)
                {
                   // String vale = String.format((Locale) null,"%%0%dd",numberofbits);
//                    if(isNumber(Integer.toBinaryString(num))) {
                        Binary = String.format((Locale) null, "%0"+(numberofbits)+"d",Long.parseLong(Long.toBinaryString(num)));
//                    }
//                    else
//                    {
//                        System.out.println(Integer.toBinaryString(num));
//                    }

                }

            }

            return Binary;
        }

        /*
         * FunctionName: Transform to Binary
         * Description: Should be used to transform Binary to hex and binary to Hex
         * parameters:
         * returns:
         * lastChanged:
         *
         * */

        public Object BinaryToHex(Object s) throws ERRORHANDLER
        {

            int decimal = Integer.parseInt(s.toString(),2);
            String hexStr = Integer.toString(decimal,16);

            return hexStr;
        }

        public Object DecimalToBinary(int s,int length) throws ERRORHANDLER
        {

            String binary ="";
            binary = String.format((Locale) null, "%0"+(length)+"d",Long.parseLong(Long.toBinaryString(s)));


            return binary;
        }

        public Object shortBinarytoBinary(String s,int length) throws ERRORHANDLER
        {

            String binary ="";
            try {
                binary = String.format((Locale) null, "%0" + (length) + "d", Long.parseLong(s));
            }
            catch (Exception e)
            {
                throw new ERRORHANDLER("Connot change to number "+e.getMessage());
            }
            return binary;
        }

        public boolean isNumber(String number) throws ERRORHANDLER
        {
            boolean status = false;
            try
            {
                int aa=Integer.parseInt(number);
                status =true;
            }
            catch (NumberFormatException ex)
            {
               status=false;
            }
            return  status;
        }
        public Object HexToDecimal(Object s) throws ERRORHANDLER
        {
            Object Decimal=0;
            if(isHEX(s.toString()))
            {
                Decimal= (Integer.parseInt(s.toString(), 16));

            }
            return Decimal;
        }

        public String doOnes(String binvalue) throws ERRORHANDLER {

            StringBuffer buffer = new StringBuffer();
            for(int f=0;f<binvalue.length();f++) {

                char loc = binvalue.charAt(f)=='1' ? '0' : '1';
                buffer.append(loc);
            }
            return buffer.toString();
        }

        public String doTwos(String binvalue) throws ERRORHANDLER {

            String a1 = doOnes(binvalue);
            StringBuffer buffer=new StringBuffer(a1);
            int f=0;
            int addbit=1;
            for(f =binvalue.length()-1;f>=0;f--) {
                int lastbit=Integer.parseInt(a1.charAt(f)+"" );
                int getremm= (lastbit+addbit) % 2;
                addbit = (addbit +lastbit) /2;
                buffer.replace(f,f+1,getremm+"");
                if (lastbit==0)
                    break;
            }
            return buffer.toString();
        }
        public String getShortBinary(){return sbinary;}
        public String getbinary(){return binary;}
        public String getDecimal(){return decimal;}
        public String getOnes(){return ones;}
        public String getTwos(){return twos;}

        private boolean isHEX (String hexvalue) {
            try {
                Long.parseLong(hexvalue, 16);
                return true;
            }
            catch (NumberFormatException ex) {
                return false;
            }
        }

        @Override
        public String toString() {
            return super.toString();
        }



    }
}
