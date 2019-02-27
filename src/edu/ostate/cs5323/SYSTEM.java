package edu.ostate.cs5323;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class SYSTEM  {


    public static void main(String[] args) throws ERRORHANDLER {


        //initialize registers and CPU

        MEMORY memObj = new MEMORY(1024);
        LOADER loadObj = new LOADER("program.txt",memObj);
        CPU proc = new CPU(memObj,loadObj.getTraceSwitch());


        //load the user program

        System.out.println();
    }








}


