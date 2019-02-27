package edu.ostate.cs5323;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MEMORY extends ERRORHANDLER {

    Map<String,MemoryState> memoryModule;
    public int freememory;

    public MEMORY(int size)
    {
        Map<String,MemoryState> temp = new LinkedHashMap<>();
         String Value = "XXXXXXXX";
        for(int i=0;i<size;i++)
        {
            temp.put(String.valueOf(i),new MemoryState(Value));
        }
        memoryModule=temp;
        freememory=getFreeMemorySize(memoryModule);
    }

    public Map<String,MemoryState> getMemory()
    {
        return memoryModule;
    }

    public int getFreeMemorySize(Map<String,MemoryState> memoryModule)
    {
        String Value = "XXXXXXXX";
        int i=0;
        for(Map.Entry<String,MemoryState> v : memoryModule.entrySet())
        {
              if(v.getValue().toString().equals(Value))
              {
                  i++;
              }
        }
        return i;

    }

    public boolean DUMP(int size) throws ERRORHANDLER
    {

        List<MemoryState> values= (List<MemoryState>) memoryModule.values();
        for(int i=0;i<values.size();i++) {
            if (i <= size-7) {
                System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s\n", new LOADER.Unit().BinaryToHex(values.get(i).getValue()).toString(),
                        new LOADER.Unit().BinaryToHex(values.get(i+1).getValue()).toString(),
                        new LOADER.Unit().BinaryToHex(values.get(i+2).getValue()).toString(),
                        new LOADER.Unit().BinaryToHex(values.get(i+3).getValue()).toString(),
                        new LOADER.Unit().BinaryToHex(values.get(i+4).getValue()).toString(),
                        new LOADER.Unit().BinaryToHex(values.get(i+5).getValue()).toString(),
                        new LOADER.Unit().BinaryToHex(values.get(i+6).getValue()).toString(),
                        new LOADER.Unit().BinaryToHex(values.get(i+7).getValue()).toString());

            }

        }


        return true;
    }

    public static class MemoryState
    {

        boolean Lock;
        String  Value;

        public MemoryState(String value)
        {
            Lock=false;
            this.Value=value;

        }
        public String getValue() {
            return Value;
        }
        public void setLock(boolean lockstatus) {
            this.Lock = lockstatus;
        }

        public boolean  getLock() {
            return Lock;
        }

        @Override
        public String toString() {
            return String.format(Value + "--> Lock status :" + Lock);
        }
    }


}
