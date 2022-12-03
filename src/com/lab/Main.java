package com.lab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class Main {

    //loadCycles is for the amount of cycles needed to load/store
    //multCycles is for the amount of cycles needed to mult/div
    //addCycles is for the amount of cycles needed to add/sub
    private static int cycles = 1, multCycles, divCycles, addCycles, loadCycles, multTableSize, addTableSize, loadTableSize, storeTableSize;
    //inst is for the instructions after being decoded
    //multTable is for the execution table for mult/div
    //addTable is for the execution table for add/sub
    //loadTable is for the execution table for load
    //storeTable is for the execution table for store
    private static String[][] inst, mulTable, addTable, loadTable, storeTable, instTable;
    //regR is for registers R
    private static int[] regR = new int[32];
    //regF is for registers F
    private static float[] regF = new float[32], mem = new float[100];
    //the boolean stop is an indication that something is wrong and execution should stop
    private static boolean stop = false, end = false, allInst = false;

    public static void startSimulation() {
        ArrayList<String[]> waitTable = new ArrayList<String[]>();
        waitTable.add(new String[]{"instruction number", "op", "start"});

        ArrayList<String[]> regWaitTable = new ArrayList<String[]>();

        while (!end && !stop) {

            //handles the part where it checks if an instruction should execute
            for (int i = 1; i < mulTable.length; i++) {
                if (mulTable[i][3] != null && mulTable[i][4] != null) {
                    if (mulTable[i][7] != null && instTable[Integer.parseInt(mulTable[i][7])][2] == null) {
                        instTable[Integer.parseInt(mulTable[i][7])][2] = "" + cycles;
                        if (mulTable[i][1] != null && mulTable[i][1].equals("MUL.D")) {
                            instTable[Integer.parseInt(mulTable[i][7])][3] = "" + (cycles + multCycles - 1);
                            int write = cycles + multCycles;
                            ArrayList<Integer> writeTimes = new ArrayList<>();
                            for(int y = 1 ; y < instTable.length ; y++){
                                if(instTable[y][4] != null ){
                                    writeTimes.add(Integer.parseInt(instTable[y][4]));
                                }
                            }
                            Collections.sort(writeTimes);
                            for(int y = 0 ; y < writeTimes.size() ; y++){
                                if(writeTimes.get(y) == write){
                                    write++;
                                }
                            }
                            instTable[Integer.parseInt(mulTable[i][7])][4] = "" + (write);
                        } else {
                            instTable[Integer.parseInt(mulTable[i][7])][3] = "" + (cycles + divCycles - 1);
                            int write = cycles + divCycles;
                            ArrayList<Integer> writeTimes = new ArrayList<>();
                            for(int y = 1 ; y < instTable.length ; y++){
                                if(instTable[y][4] != null ){
                                    writeTimes.add(Integer.parseInt(instTable[y][4]));
                                }
                            }
                            Collections.sort(writeTimes);
                            for(int y = 0 ; y < writeTimes.size() ; y++){
                                if(writeTimes.get(y) == write){
                                    write++;
                                }
                            }
                            instTable[Integer.parseInt(mulTable[i][7])][4] = "" + (write);
                        }

                    }
                }
            }

            for (int i = 1; i < addTable.length; i++) {
                if (addTable[i][3] != null && addTable[i][4] != null) {
                    if (addTable[i][7] != null && instTable[Integer.parseInt(addTable[i][7])][2] == null) {
                        int write = cycles + addCycles;
                        ArrayList<Integer> writeTimes = new ArrayList<>();
                        for(int y = 1 ; y < instTable.length ; y++){
                            if(instTable[y][4] != null ){
                                writeTimes.add(Integer.parseInt(instTable[y][4]));
                            }
                        }
                        Collections.sort(writeTimes);
                        for(int y = 0 ; y < writeTimes.size() ; y++){
                            if(writeTimes.get(y) == write){
                                write++;
                            }
                        }
                        instTable[Integer.parseInt(addTable[i][7])][2] = "" + cycles;
                        instTable[Integer.parseInt(addTable[i][7])][3] = "" + (cycles + addCycles - 1);
                        instTable[Integer.parseInt(addTable[i][7])][4] = "" + (write);
                    }
                }
            }

            for (int i = 1; i < storeTable.length; i++) {
                if (storeTable[i][1] != null && (!storeTable[i][1].substring(0, 1).equals("A") && !storeTable[i][1].substring(0, 1).equals("M") && !storeTable[i][1].substring(0, 1).equals("L"))) {
                    if (instTable[Integer.parseInt(storeTable[i][3])][2] == null) {
                        int write = cycles + loadCycles;
                        ArrayList<Integer> writeTimes = new ArrayList<>();
                        for(int y = 1 ; y < instTable.length ; y++){
                            if(instTable[y][4] != null ){
                                writeTimes.add(Integer.parseInt(instTable[y][4]));
                            }
                        }
                        Collections.sort(writeTimes);
                        for(int y = 0 ; y < writeTimes.size() ; y++){
                            if(writeTimes.get(y) == write){
                                write++;
                            }
                        }
                        instTable[Integer.parseInt(storeTable[i][3])][2] = "" + cycles;
                        instTable[Integer.parseInt(storeTable[i][3])][3] = "" + (cycles + loadCycles - 1);
                        instTable[Integer.parseInt(storeTable[i][3])][4] = "" + (write);
                    }
                }
            }

            for (int i = 1; i < loadTable.length; i++) {
                if (loadTable[i][1] != null && (loadTable[i][1].substring(0, 1).equals("R") || loadTable[i][1].substring(0, 1).equals("F"))) {
                    if (instTable[Integer.parseInt(loadTable[i][3])][2] == null) {
                        int write = cycles + loadCycles;
                        ArrayList<Integer> writeTimes = new ArrayList<>();
                        for(int y = 1 ; y < instTable.length ; y++){
                            if(instTable[y][4] != null ){
                                writeTimes.add(Integer.parseInt(instTable[y][4]));
                            }
                        }
                        Collections.sort(writeTimes);
                        for(int y = 0 ; y < writeTimes.size() ; y++){
                            if(writeTimes.get(y) == write){
                                write++;
                            }
                        }
                        instTable[Integer.parseInt(loadTable[i][3])][2] = "" + cycles;
                        instTable[Integer.parseInt(loadTable[i][3])][3] = "" + (cycles + loadCycles - 1);
                        instTable[Integer.parseInt(loadTable[i][3])][4] = "" + (write);
                    }
                }
            }

            //handles the part where it checks if something should write
            boolean wrote = false;
            for (int i = 1; i < mulTable.length && !wrote; i++) {
                if (mulTable[i][0] != null && mulTable[i][0].equals("1")) {
                    if (mulTable[i][7] != null && instTable[Integer.parseInt(mulTable[i][7])][4] != null
                            && Integer.parseInt(instTable[Integer.parseInt(mulTable[i][7])][4]) <= cycles) {
                            wrote = true;
                            if(Integer.parseInt(instTable[Integer.parseInt(mulTable[i][7])][4]) < cycles)
                                instTable[Integer.parseInt(mulTable[i][7])][4] = "" + cycles;
                        if (mulTable[i][2].substring(0, 1).equals("R")) {
                            for (int j = 0; j < regWaitTable.size(); j++) {
                                if (regWaitTable.get(j)[0].equals(mulTable[i][2]) && regWaitTable.get(j)[1].equals("M" + i)) {
                                    float val1;
                                    float val2;
                                    if (mulTable[i][7] != null && inst[Integer.parseInt(mulTable[i][7])][2].substring(0, 1).equals("R")) {
                                        val1 = regR[Integer.parseInt(inst[Integer.parseInt(mulTable[i][7])][2].substring(1))];
                                    } else {
                                        val1 = regF[Integer.parseInt(inst[Integer.parseInt(mulTable[i][7])][2].substring(1))];
                                    }
                                    if (mulTable[i][7] != null && inst[Integer.parseInt(mulTable[i][7])][3].substring(0, 1).equals("R")) {
                                        val2 = regR[Integer.parseInt(inst[Integer.parseInt(mulTable[i][7])][3].substring(1))];
                                    } else {
                                        val2 = regF[Integer.parseInt(inst[Integer.parseInt(mulTable[i][7])][3].substring(1))];
                                    }
                                    if (mulTable[i][1] != null && mulTable[i][1].equals("MUL.D"))
                                        regR[Integer.parseInt(mulTable[i][2].substring(1))] = (int) (val1 * val2);
                                    else {
                                        regR[Integer.parseInt(mulTable[i][2].substring(1))] = (int) (val1 / val2);
                                    }
                                    mulTable[i][0] = "0";
                                    for (int k = 1; k < mulTable.length; k++) {
                                        if (mulTable[k][5] != null && mulTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][3] = "" + regR[Integer.parseInt(mulTable[i][2].substring(1))];
                                            mulTable[k][5] = null;
                                        }
                                        if (mulTable[k][6] != null && mulTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][4] = "" + regR[Integer.parseInt(mulTable[i][2].substring(1))];
                                            mulTable[k][6] = null;
                                        }
                                    }
                                    for (int k = 1; k < addTable.length; k++) {
                                        if (addTable[k][5] != null && addTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][3] = "" + regR[Integer.parseInt(mulTable[i][2].substring(1))];
                                            addTable[k][5] = null;
                                        }
                                        if (addTable[k][6] != null && addTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][4] = "" + regR[Integer.parseInt(mulTable[i][2].substring(1))];
                                            addTable[k][6] = null;
                                        }
                                    }
                                    for (int k = 1; k < storeTable.length; k++) {
                                        if (storeTable[k][1] != null && storeTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            storeTable[k][1] = "" + regR[Integer.parseInt(mulTable[i][2].substring(1))];
                                        }
                                    }
                                    for (int k = 1; k < loadTable.length; k++) {
                                        if (loadTable[k][1] != null && loadTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            loadTable[k][1] = addTable[i][2];
                                        }
                                    }
                                    regWaitTable.remove(j);
                                }
                            }
                        } else {
                            for (int j = 0; j < regWaitTable.size(); j++) {
                                if (regWaitTable.get(j)[0].equals(mulTable[i][2]) && regWaitTable.get(j)[1].equals("M" + i)) {
                                    float val1;
                                    float val2;
                                    if (inst[Integer.parseInt(mulTable[i][7]) - 1][2].substring(0, 1).equals("R")) {
                                        val1 = regR[Integer.parseInt(inst[Integer.parseInt(mulTable[i][7]) - 1][2].substring(1))];
                                    } else {
                                        val1 = regF[Integer.parseInt(inst[Integer.parseInt(mulTable[i][7]) - 1][2].substring(1))];
                                    }
                                    if (inst[Integer.parseInt(mulTable[i][7]) - 1][3].substring(0, 1).equals("R")) {
                                        val2 = regR[Integer.parseInt(inst[Integer.parseInt(mulTable[i][7]) - 1][3].substring(1))];
                                    } else {
                                        val2 = regF[Integer.parseInt(inst[Integer.parseInt(mulTable[i][7]) - 1][3].substring(1))];
                                    }
                                    if (mulTable[i][1].equals("MUL.D"))
                                        regF[Integer.parseInt(mulTable[i][2].substring(1))] = (val1 * val2);
                                    else {
                                        regF[Integer.parseInt(mulTable[i][2].substring(1))] = (val1 / val2);
                                    }
                                    mulTable[i][0] = "0";
                                    for (int k = 1; k < mulTable.length; k++) {
                                        if (mulTable[k][5] != null && mulTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][3] = "" + regF[Integer.parseInt(mulTable[i][2].substring(1))];
                                            mulTable[k][5] = null;
                                        }
                                        if (mulTable[k][6] != null && mulTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][4] = "" + regF[Integer.parseInt(mulTable[i][2].substring(1))];
                                            mulTable[k][6] = null;
                                        }
                                    }
                                    for (int k = 1; k < addTable.length; k++) {
                                        if (addTable[k][5] != null && addTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][3] = "" + regF[Integer.parseInt(mulTable[i][2].substring(1))];
                                            addTable[k][5] = null;
                                        }
                                        if (addTable[k][6] != null && addTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][4] = "" + regF[Integer.parseInt(mulTable[i][2].substring(1))];
                                            addTable[k][6] = null;
                                        }
                                    }
                                    for (int k = 1; k < storeTable.length; k++) {
                                        if (storeTable[k][1] != null && storeTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            storeTable[k][1] = "" + regF[Integer.parseInt(mulTable[i][2].substring(1))];
                                        }
                                    }
                                    for (int k = 1; k < loadTable.length; k++) {
                                        if (loadTable[k][1] != null && loadTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            loadTable[k][1] = addTable[i][2];
                                            ;
                                        }
                                    }
                                    regWaitTable.remove(j);
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 1; i < addTable.length && !wrote; i++) {
                if (addTable[i][0] != null && addTable[i][0].equals("1")) {
                    if (addTable[i][7] != null && instTable[Integer.parseInt(addTable[i][7])][4] != null && Integer.parseInt(instTable[Integer.parseInt(addTable[i][7])][4]) <= cycles) {
                        wrote = true;
                        //if(Integer.parseInt(instTable[Integer.parseInt(addTable[i][7])][4]) < cycles)
                        //    instTable[Integer.parseInt(addTable[i][7])][4] = "" + cycles;
                        if (addTable[i][2].substring(0, 1).equals("R")) {
                            for (int j = 0; j < regWaitTable.size(); j++) {
                                if (regWaitTable.get(j)[0].equals(addTable[i][2]) && regWaitTable.get(j)[1].equals("A" + i)) {

                                    float val1;
                                    float val2;
                                    if (inst[Integer.parseInt(addTable[i][7]) - 1][2].substring(0, 1).equals("R")) {
                                        val1 = regR[Integer.parseInt(inst[Integer.parseInt(addTable[i][7]) - 1][2].substring(1))];
                                    } else {
                                        val1 = regF[Integer.parseInt(inst[Integer.parseInt(addTable[i][7]) - 1][2].substring(1))];
                                    }
                                    if (inst[Integer.parseInt(addTable[i][7]) - 1][3].substring(0, 1).equals("R")) {
                                        val2 = regR[Integer.parseInt(inst[Integer.parseInt(addTable[i][7]) - 1][3].substring(1))];
                                    } else {
                                        val2 = regF[Integer.parseInt(inst[Integer.parseInt(addTable[i][7]) - 1][3].substring(1))];
                                    }
                                    if (addTable[i][1].equals("ADD.D"))
                                        regR[Integer.parseInt(addTable[i][2].substring(1))] = (int) (val1 + val2);
                                    else {
                                        regR[Integer.parseInt(addTable[i][2].substring(1))] = (int) (val1 - val2);
                                    }
                                    addTable[i][0] = "0";
                                    for (int k = 1; k < mulTable.length; k++) {
                                        if (mulTable[k][5] != null && mulTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][3] = "" + regR[Integer.parseInt(addTable[i][2].substring(1))];
                                            mulTable[k][5] = null;
                                        }
                                        if (mulTable[k][6] != null && mulTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][4] = "" + regR[Integer.parseInt(addTable[i][2].substring(1))];
                                            mulTable[k][6] = null;
                                        }
                                    }
                                    for (int k = 1; k < addTable.length; k++) {
                                        if (addTable[k][5] != null && addTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][3] = "" + regR[Integer.parseInt(addTable[i][2].substring(1))];
                                            addTable[k][5] = null;

                                        }
                                        if (addTable[k][6] != null && addTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][4] = "" + regR[Integer.parseInt(addTable[i][2].substring(1))];
                                            addTable[k][6] = null;
                                        }
                                    }
                                    for (int k = 1; k < storeTable.length; k++) {
                                        if (storeTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            storeTable[k][1] = "" + regR[Integer.parseInt(addTable[i][2].substring(1))];
                                        }
                                    }
                                    for (int k = 1; k < loadTable.length; k++) {
                                        if (loadTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            loadTable[k][1] = addTable[i][2];
                                        }
                                    }
                                    regWaitTable.remove(j);
                                }
                            }
                        } else {
                            for (int j = 0; j < regWaitTable.size(); j++) {
                                if (regWaitTable.get(j)[0].equals(addTable[i][2]) && regWaitTable.get(j)[1].equals("A" + i)) {
                                    float val1;
                                    float val2;
                                    if (addTable[i][7] != null && inst[Integer.parseInt(addTable[i][7]) - 1][2] != null && inst[Integer.parseInt(addTable[i][7]) - 1][2].substring(0, 1).equals("R")) {
                                        val1 = regR[Integer.parseInt(inst[Integer.parseInt(addTable[i][7]) - 1][2].substring(1))];
                                    } else {
                                        val1 = regF[Integer.parseInt(inst[Integer.parseInt(addTable[i][7]) - 1][2].substring(1))];
                                    }
                                    if (addTable[i][7] != null && inst[Integer.parseInt(addTable[i][7]) - 1][3] != null && inst[Integer.parseInt(addTable[i][7]) - 1][3].substring(0, 1).equals("R")) {
                                        val2 = regR[Integer.parseInt(inst[Integer.parseInt(addTable[i][7]) - 1][3].substring(1))];
                                    } else {
                                        val2 = regF[Integer.parseInt(inst[Integer.parseInt(addTable[i][7]) - 1][3].substring(1))];
                                    }
                                    if (addTable[i][1].equals("ADD.D"))
                                        regF[Integer.parseInt(addTable[i][2].substring(1))] = (val1 + val2);
                                    else {
                                        regF[Integer.parseInt(addTable[i][2].substring(1))] = (val1 - val2);
                                    }

                                    addTable[i][0] = "0";
                                    for (int k = 1; k < mulTable.length; k++) {
                                        if (mulTable[k][5] != null && mulTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][3] = "" + regF[Integer.parseInt(addTable[i][2].substring(1))];
                                            mulTable[k][5] = null;
                                        }
                                        if (mulTable[k][6] != null && mulTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][4] = "" + regF[Integer.parseInt(addTable[i][2].substring(1))];
                                            mulTable[k][6] = null;
                                        }
                                    }
                                    for (int k = 1; k < addTable.length; k++) {
                                        if (addTable[k][5] != null && addTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][3] = "" + regF[Integer.parseInt(addTable[i][2].substring(1))];
                                        }
                                        if (addTable[k][6] != null && addTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][4] = "" + regF[Integer.parseInt(addTable[i][2].substring(1))];
                                        }
                                    }
                                    for (int k = 1; k < storeTable.length; k++) {
                                        if (storeTable[k][1] != null && storeTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            storeTable[k][1] = "" + regF[Integer.parseInt(addTable[i][2].substring(1))];
                                        }
                                    }
                                    for (int k = 1; k < loadTable.length; k++) {
                                        if (loadTable[k][1] != null && loadTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            loadTable[k][1] = addTable[i][2];
                                        }
                                    }
                                    regWaitTable.remove(j);
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 1; i < loadTable.length && !wrote; i++) {
                if (loadTable[i][0] != null && loadTable[i][0].equals("1")) {
                    if (loadTable[i][3] != null && instTable[Integer.parseInt(loadTable[i][3])][4] != null && Integer.parseInt(instTable[Integer.parseInt(loadTable[i][3])][4]) <= cycles) {
                        wrote = true;
                        //if(Integer.parseInt(instTable[Integer.parseInt(loadTable[i][3])][4]) < cycles)
                            //instTable[Integer.parseInt(loadTable[i][3])][4] = "" + cycles;
                        String[] temp = loadTable[i][2].split("\\+");
                        if (loadTable[i][1] != null && loadTable[i][1].substring(0, 1).equals("R")) {

                            for (int j = 0; j < regWaitTable.size(); j++) {
                                if (regWaitTable.get(j)[0].equals(loadTable[i][1]) && regWaitTable.get(j)[1].equals("L" + i)) {
                                    int val1;
                                    int val2;

                                    val1 = Integer.parseInt(temp[0]);
                                    val2 = Integer.parseInt(temp[1].substring(1));
                                    if ((mem[val1 + regR[val2]]) > 99 || (mem[val1 + regR[val2]]) < 0) {
                                        System.out.println("address out of bounds. L.D failed. instruction " + Integer.parseInt(loadTable[i][3]) + ".");
                                        stop = true;
                                    } else {
                                        regR[Integer.parseInt(temp[1].substring(1))] = (int) (mem[val1 + regR[val2]]);
                                    }
                                    for (int k = 1; k < mulTable.length; k++) {
                                        if (mulTable[k][5] != null && mulTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][3] = "" + regR[Integer.parseInt(temp[1].substring(1))];
                                            mulTable[k][5] = null;
                                        }
                                        if (mulTable[k][6] != null && mulTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][4] = "" + regR[Integer.parseInt(temp[1].substring(1))];
                                            mulTable[k][6] = null;
                                        }
                                    }
                                    for (int k = 1; k < addTable.length; k++) {
                                        if (addTable[k][5] != null && addTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][3] = "" + regR[Integer.parseInt(temp[1].substring(1))];
                                            addTable[k][5] = null;

                                        }
                                        if (addTable[k][6] != null && addTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][4] = "" + regR[Integer.parseInt(temp[1].substring(1))];
                                            addTable[k][6] = null;
                                        }
                                    }
                                    for (int k = 1; k < storeTable.length; k++) {
                                        if (storeTable[k][1] != null && storeTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            storeTable[k][1] = "" + regR[Integer.parseInt(temp[1].substring(1))];
                                        }
                                    }
                                    for (int k = 1; k < loadTable.length; k++) {
                                        if (loadTable[k][1] != null && loadTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            loadTable[k][1] = loadTable[i][1];
                                        }
                                    }
                                    loadTable[i][0] = "0";
                                    regWaitTable.remove(j);
                                }
                            }
                        } else {
                            for (int j = 0; j < regWaitTable.size(); j++) {
                                if (regWaitTable.get(j)[0].equals(loadTable[i][1]) && regWaitTable.get(j)[1].equals("L" + i)) {
                                    int val1;
                                    int val2;
                                    //String[] temp = loadTable[i][2].split("\\+");
                                    val1 = Integer.parseInt(temp[0]);
                                    val2 = Integer.parseInt(temp[1].substring(1));
                                    if ((mem[val1 + regR[val2]]) > 99 || (mem[val1 + regR[val2]]) < 0) {
                                        stop = true;
                                        System.out.println("address out of bounds. L.D failed. instruction " + Integer.parseInt(loadTable[i][3]) + ".");
                                    } else {
                                        regF[Integer.parseInt(temp[1].substring(1))] = (mem[val1 + regR[val2]]);
                                    }
                                    for (int k = 1; k < mulTable.length; k++) {
                                        if (mulTable[k][5] != null && mulTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][3] = "" + regF[Integer.parseInt(temp[1].substring(1))];
                                            mulTable[k][5] = null;
                                        }
                                        if (mulTable[k][6] != null && mulTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            mulTable[k][4] = "" + regF[Integer.parseInt(temp[1].substring(1))];
                                            mulTable[k][6] = null;
                                        }
                                    }
                                    for (int k = 1; k < addTable.length; k++) {
                                        if (addTable[k][5] != null && addTable[k][5].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][3] = "" + regF[Integer.parseInt(temp[1].substring(1))];
                                            addTable[k][5] = null;

                                        }
                                        if (addTable[k][6] != null && addTable[k][6].equals(regWaitTable.get(j)[1])) {
                                            addTable[k][4] = "" + regF[Integer.parseInt(temp[1].substring(1))];
                                            addTable[k][6] = null;
                                        }
                                    }
                                    for (int k = 1; k < storeTable.length; k++) {
                                        if (storeTable[k][1] != null && storeTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            storeTable[k][1] = "" + regF[Integer.parseInt(temp[1].substring(1))];
                                        }
                                    }
                                    for (int k = 1; k < loadTable.length; k++) {
                                        if (loadTable[k][1] != null && loadTable[k][1].equals(regWaitTable.get(j)[1])) {
                                            loadTable[k][1] = loadTable[i][1];
                                        }
                                    }
                                    loadTable[i][0] = "0";
                                    regWaitTable.remove(j);
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 1; i < storeTable.length && !wrote; i++) {
                if (storeTable[i][0] != null && storeTable[i][0].equals("1")) {
                    if (storeTable[i][3] != null && instTable[Integer.parseInt(storeTable[i][3])][4] != null && Integer.parseInt(instTable[Integer.parseInt(storeTable[i][3])][4]) <= cycles) {
                        wrote = true;
                        if(Integer.parseInt(instTable[Integer.parseInt(loadTable[i][3])][4]) < cycles)
                            instTable[Integer.parseInt(loadTable[i][3])][4] = "" + cycles;
                        if (storeTable[i][1].substring(0, 1).equals("R")) {
                            int val1;
                            int val2;
                            String[] temp = storeTable[i][2].split("\\+");
                            val1 = Integer.parseInt(temp[0]);
                            val2 = Integer.parseInt(temp[1].substring(1));
                            if ((mem[val1 + regR[val2]]) > 99 || (mem[val1 + regR[val2]]) < 0) {
                                System.out.println("address out of bounds. S.D failed. instruction " + Integer.parseInt(storeTable[i][3]) + ".");
                                stop = true;
                            } else {
                                (mem[val1 + regR[val2]]) = regR[Integer.parseInt(storeTable[i][1].substring(1))];
                            }
                            storeTable[i][0] = "0";
                        }

                    } else {
                        int val1;
                        int val2;
                        String[] temp = storeTable[i][2].split("\\+");
                        val1 = Integer.parseInt(temp[0]);
                        val2 = Integer.parseInt(temp[1].substring(1));
                        if ((mem[val1 + regR[val2]]) > 99 || (mem[val1 + regR[val2]]) < 0) {
                            System.out.println("address out of bounds. S.D failed. instruction " + Integer.parseInt(storeTable[i][3]) + ".");
                            stop = true;
                        } else {
                            if (storeTable[i][1] != null)
                                (mem[val1 + regR[val2]]) = Float.parseFloat(storeTable[i][1]);
                        }
                        storeTable[i][0] = "0";
                    }
                }
            }

            boolean instIssued = false;
            //handles the part where an instruction wants to execute but there is no space in the respective table
            for (int i = 1; i < waitTable.size() && !instIssued; i++) {
                String[] temp = waitTable.get(i);
                if (Integer.parseInt(temp[2]) >= cycles) {
                    if (temp[1].equals("MUL.D") || temp[1].equals("DIV.D")) {
                        for (int j = 1; j < mulTable.length; j++) {
                            if (mulTable[j][0] == null || mulTable[j][0].equals("0")) {
                                mulTable[j][7] = temp[0];
                                mulTable[j][1] = temp[1];
                                mulTable[j][2] = inst[Integer.parseInt(temp[0]) - 1][1];
                                mulTable[j][0] = "1";
                                String[] temp2 = inst[Integer.parseInt(temp[0]) - 1];
                                boolean qj = false, qk = false;
                                regWaitTable.add(new String[]{temp2[1], "M" + j});
                                for (int k = 0; k < regWaitTable.size() && (!qj || !qk); k++) {
                                    if (regWaitTable.get(k)[0].equals(temp2[2])) {
                                        qj = true;
                                        mulTable[j][5] = regWaitTable.get(k)[1];
                                    }
                                    if (regWaitTable.get(k)[0].equals(temp2[3])) {
                                        qk = true;
                                        mulTable[j][6] = regWaitTable.get(k)[1];
                                    }
                                }
                                if (!qj) {
                                    if (temp2[2].substring(0, 1).equals("R"))
                                        mulTable[j][3] = "" + regR[Integer.parseInt(temp2[2].substring(1))];
                                    else {
                                        mulTable[j][3] = "" + regF[Integer.parseInt(temp2[2].substring(1))];
                                    }
                                }
                                if (!qk) {
                                    if (temp2[3].substring(0, 1).equals("R"))
                                        mulTable[j][4] = "" + regR[Integer.parseInt(temp2[3].substring(1))];
                                    else {
                                        mulTable[j][4] = "" + regF[Integer.parseInt(temp2[3].substring(1))];
                                    }
                                }
                                instIssued = true;
                                waitTable.remove(i);
                            }
                        }
                    } else {
                        if (temp[1].equals("ADD.D") || temp[1].equals("SUB.D")) {
                            for (int j = 1; j < addTable.length; j++) {
                                if (addTable[j][0] == null || addTable[j][0].equals("0")) {
                                    addTable[j][7] = temp[0];
                                    addTable[j][1] = temp[1];
                                    addTable[j][2] = inst[Integer.parseInt(temp[0]) - 1][1];
                                    addTable[j][0] = "1";
                                    String[] temp2 = inst[Integer.parseInt(temp[0]) - 1];
                                    boolean qj = false, qk = false;
                                    regWaitTable.add(new String[]{temp2[1], "A" + j});
                                    for (int k = 0; k < regWaitTable.size() && (!qj || !qk); k++) {
                                        if (regWaitTable.get(k)[0].equals(temp2[2])) {
                                            qj = true;
                                            addTable[j][5] = regWaitTable.get(k)[1];
                                            addTable[j][3] = null;
                                        }
                                        if (regWaitTable.get(k)[0].equals(temp2[3])) {
                                            qk = true;
                                            addTable[j][6] = regWaitTable.get(k)[1];
                                            addTable[j][4] = null;
                                        }
                                    }
                                    if (!qj) {
                                        if (temp2[2].substring(0, 1).equals("R"))
                                            addTable[j][3] = "" + regR[Integer.parseInt(temp2[2].substring(1))];
                                        else {
                                            addTable[j][3] = "" + regF[Integer.parseInt(temp2[2].substring(1))];
                                        }
                                    }
                                    if (!qk) {
                                        if (temp2[3].substring(0, 1).equals("R"))
                                            addTable[j][4] = "" + regR[Integer.parseInt(temp2[3].substring(1))];
                                        else {
                                            addTable[j][4] = "" + regF[Integer.parseInt(temp2[3].substring(1))];
                                        }
                                    }
                                    instIssued = true;
                                    waitTable.remove(i);
                                }
                            }
                        } else {
                            if (temp[1].equals("L.D")) {
                                for (int j = 1; j < loadTable.length; j++) {
                                    if (loadTable[j][0] == null || loadTable[j][0].equals("0")) {
                                        loadTable[j][0] = "1";
                                        String[] temp2 = inst[Integer.parseInt(temp[0]) - 1];
                                        loadTable[j][1] = temp2[1];
                                        loadTable[j][2] = temp2[2];
                                        loadTable[j][3] = temp[0];
                                        regWaitTable.add(new String[]{temp2[1], "L" + j});
                                        instIssued = true;
                                        waitTable.remove(i);
                                    }
                                }
                            } else {
                                for (int j = 1; j < storeTable.length; j++) {
                                    if (storeTable[j][0] == null || storeTable[j][0].equals("0")) {
                                        storeTable[j][0] = "1";
                                        String[] temp2 = inst[Integer.parseInt(temp[0]) - 1];
                                        storeTable[j][1] = temp2[1];
                                        storeTable[j][2] = temp2[2];
                                        storeTable[j][3] = temp[0];
                                        instIssued = true;
                                        waitTable.remove(i);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //if no waiting inst execute it checks if there is an inst that can be issued
            for (int i = 1; i < instTable.length && !instIssued; i++) {
                if (instTable[i][1] == null || instTable[i][1].equals("")) {
                    String[] instTemp = inst[Integer.parseInt(instTable[i][0]) - 1];
                    if (i == instTable.length - 1 && !instTemp[0].equals("L.D") && !instTemp[0].equals("S.D"))
                        allInst = true;
                    switch (instTemp[0]) {
                        case ("MUL.D"):
                            boolean foundSpace = false;
                            int freeSpaceInd = 0;
                            for (int j = 1; j < mulTable.length && !foundSpace; j++) {
                                if (mulTable[j][0] == null || mulTable[j][0].equals("0")) {
                                    mulTable[j][0] = "1";
                                    mulTable[j][1] = "MUL.D";
                                    mulTable[j][2] = instTemp[1];
                                    mulTable[j][7] = instTable[i][0];
                                    regWaitTable.add(new String[]{instTemp[1], "M" + j});
                                    foundSpace = true;
                                    freeSpaceInd = j;
                                    instIssued = true;
                                }
                            }
                            boolean qk = false, qj = false;
                            for (int j = 0; j < regWaitTable.size() && (!qk || !qj) && foundSpace; j++) {
                                if (regWaitTable.get(j)[0].equals(instTemp[2])) {
                                    mulTable[freeSpaceInd][5] = regWaitTable.get(j)[1];
                                    qj = true;
                                }
                                if (regWaitTable.get(j)[0].equals(instTemp[3])) {
                                    mulTable[freeSpaceInd][6] = regWaitTable.get(j)[1];
                                    qk = true;
                                }
                            }
                            if (!qk && foundSpace) {
                                if (instTemp[3].substring(0, 1).equals("R"))
                                    mulTable[freeSpaceInd][4] = "" + regR[Integer.parseInt(instTemp[3].substring(1))];
                                else {
                                    mulTable[freeSpaceInd][4] = "" + regF[Integer.parseInt(instTemp[3].substring(1))];
                                }
                            }
                            if (!qj && foundSpace) {
                                if (instTemp[2].substring(0, 1).equals("R"))
                                    mulTable[freeSpaceInd][3] = "" + regR[Integer.parseInt(instTemp[3].substring(1))];
                                else {
                                    mulTable[freeSpaceInd][3] = "" + regF[Integer.parseInt(instTemp[3].substring(1))];
                                }
                            }
                            if (foundSpace) instTable[i][1] = "" + cycles;
                            if (!qk && !qj && foundSpace) {
                                int write = cycles + multCycles + 1;
                                ArrayList<Integer> writeTimes = new ArrayList<>();
                                for(int y = 1 ; y < instTable.length ; y++){
                                    if(instTable[y][4] != null ){
                                        writeTimes.add(Integer.parseInt(instTable[y][4]));
                                    }
                                }
                                Collections.sort(writeTimes);
                                for(int y = 0 ; y < writeTimes.size() ; y++){
                                    if(writeTimes.get(y) == write){
                                        write++;
                                    }
                                }
                                instTable[i][2] = "" + (cycles + 1);
                                instTable[i][3] = "" + (cycles + multCycles);
                                instTable[i][4] = "" + (write);
                            }
                            if (!foundSpace) {
                                if (instTemp[0].equals("MUL.D") || instTemp[0].equals("DIV.D")) {
                                    int[] ends = new int[mulTable.length - 1];
                                    for (int k = 0 ; k < ends.length ; k++)
                                        ends[k]=-1;
                                    for (int k = 1; k < mulTable.length; k++) {
                                        if (mulTable[k][7] != null && instTable[Integer.parseInt(mulTable[k][7])][4] != null && Integer.parseInt(instTable[Integer.parseInt(mulTable[k][7])][4])!=0)
                                            ends[k - 1] = Integer.parseInt(instTable[Integer.parseInt(mulTable[k][7])][4]);
                                    }
                                    int firstAvailStart = -1;
                                    for (int k = 1; k < waitTable.size(); k++) {
                                        if (waitTable.get(k)[1].equals("MUL.D") || waitTable.get(k)[1].equals("DIV.D")) {
                                            for (int l = 0; l < ends.length; l++) {
                                                if (ends[l] == Integer.parseInt(waitTable.get(k)[2])) {
                                                    ends[l] = -1;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    for (int l = 0; l < ends.length; l++) {
                                        if (ends[l] != -1 && firstAvailStart != -1 && ends[l] < firstAvailStart) {
                                            firstAvailStart = ends[l];
                                        } else {
                                            if (firstAvailStart == -1)
                                                firstAvailStart = ends[l];
                                        }
                                    }
                                    if (firstAvailStart == -1) {
                                        int[] schedule = new int[mulTable.length - 1];
                                        for (int k = schedule.length-1; k >= 0; k--) {
                                            schedule[k] = -(k+1);
                                        }
                                        for (int k = 1; k < waitTable.size(); k++) {
                                            if (waitTable.get(k)[1].equals("MUL.D") || waitTable.get(k)[1].equals("DIV.D")) {
                                                for (int r = schedule.length - 1; r >= 0; r--) {
                                                    if (r != 0 && schedule[r] < schedule[r - 1] && Integer.parseInt(waitTable.get(k)[2]) >= schedule[r]) {
                                                        schedule[r] = Integer.parseInt(waitTable.get(k)[2]);
                                                        break;
                                                    } else {
                                                        if (r == 0) schedule[r] = Integer.parseInt(waitTable.get(k)[2]);
                                                    }
                                                }
                                            }
                                        }
                                        for (int k = 0; k < schedule.length; k++) {
                                            if (k == schedule.length - 1 ) {
                                                for(int p = 0 ; p < schedule.length ; p++)
                                                    if(schedule[p] > 0){
                                                        firstAvailStart = schedule[p] + multCycles + 1;
                                                        break;
                                                    }
                                            } else {
                                                if (schedule[k] < schedule[k + 1] && schedule[k] >0) {
                                                    firstAvailStart = schedule[k+1] + multCycles + 1;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    waitTable.add(new String[]{"" + i, instTemp[0], "" + firstAvailStart});
                                    instTable[i][1] = "" + (firstAvailStart+1);
                                }
                            }
                            break;
                        case ("DIV.D"):
                            foundSpace = false;
                            freeSpaceInd = 0;
                            for (int j = 1; j < mulTable.length && !foundSpace; j++) {
                                if (mulTable[j][0] == null || mulTable[j][0].equals("0")) {
                                    mulTable[j][0] = "1";
                                    mulTable[j][1] = "DIV.D";
                                    mulTable[j][2] = instTemp[1];
                                    mulTable[j][7] = instTable[i][0];
                                    regWaitTable.add(new String[]{instTemp[1], "M" + j});
                                    foundSpace = true;
                                    freeSpaceInd = j;
                                    instIssued = true;
                                }
                            }
                            qk = false;
                            qj = false;
                            for (int j = 0; j < regWaitTable.size() && (!qk || !qj) && foundSpace; j++) {
                                if (regWaitTable.get(j)[0].equals(instTemp[2])) {
                                    mulTable[freeSpaceInd][5] = regWaitTable.get(j)[1];
                                    qj = true;
                                }
                                if (regWaitTable.get(j)[0].equals(instTemp[3])) {
                                    mulTable[freeSpaceInd][6] = regWaitTable.get(j)[1];
                                    qk = true;
                                }
                            }
                            if (!qk && foundSpace) {
                                if (instTemp[3].substring(0, 1).equals("R"))
                                    mulTable[freeSpaceInd][4] = "" + regR[Integer.parseInt(instTemp[3].substring(1))];
                                else {
                                    mulTable[freeSpaceInd][4] = "" + regF[Integer.parseInt(instTemp[3].substring(1))];
                                }
                            }
                            if (!qj && foundSpace) {
                                if (instTemp[2].substring(0, 1).equals("R"))
                                    mulTable[freeSpaceInd][3] = "" + regR[Integer.parseInt(instTemp[3].substring(1))];
                                else {
                                    mulTable[freeSpaceInd][3] = "" + regF[Integer.parseInt(instTemp[3].substring(1))];
                                }
                            }
                            if (foundSpace) instTable[i][1] = "" + cycles;
                            if (!qk && !qj && foundSpace) {
                                int write = cycles + divCycles + 1;
                                ArrayList<Integer> writeTimes = new ArrayList<>();
                                for(int y = 1 ; y < instTable.length ; y++){
                                    if(instTable[y][4] != null ){
                                        writeTimes.add(Integer.parseInt(instTable[y][4]));
                                    }
                                }
                                Collections.sort(writeTimes);
                                for(int y = 0 ; y < writeTimes.size() ; y++){
                                    if(writeTimes.get(y) == write){
                                        write++;
                                    }
                                }
                                instTable[i][2] = "" + (cycles + 1);
                                instTable[i][3] = "" + (cycles + divCycles);
                                instTable[i][4] = "" + (write);
                            }
                            if (!foundSpace) {
                                if (instTemp[0].equals("MUL.D") || instTemp[0].equals("DIV.D")) {
                                    int[] ends = new int[mulTable.length - 1];
                                    for (int k = 0 ; k < ends.length ; k++)
                                        ends[k]=-1;
                                    for (int k = 1; k < mulTable.length; k++) {
                                        if (mulTable[k][7] != null && instTable[Integer.parseInt(mulTable[k][7])][4] != null && Integer.parseInt(instTable[Integer.parseInt(mulTable[k][7])][4])!=0)
                                            ends[k - 1] = Integer.parseInt(instTable[Integer.parseInt(mulTable[k][7])][4]);
                                    }
                                    int firstAvailStart = -1;
                                    for (int k = 1; k < waitTable.size(); k++) {
                                        if (waitTable.get(k)[1].equals("MUL.D") || waitTable.get(k)[1].equals("DIV.D")) {
                                            for (int l = 0; l < ends.length; l++) {
                                                if (ends[l] == Integer.parseInt(waitTable.get(k)[2])) {
                                                    ends[l] = -1;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    for (int l = 0; l < ends.length; l++) {
                                        if (ends[l] != -1 && firstAvailStart != -1 && ends[l] < firstAvailStart) {
                                            firstAvailStart = ends[l];
                                        } else {
                                            if (firstAvailStart == -1)
                                                firstAvailStart = ends[l];
                                        }
                                    }
                                    if (firstAvailStart == -1) {
                                        int[] schedule = new int[mulTable.length - 1];
                                        for (int k = schedule.length-1; k >= 0; k--) {
                                            schedule[k] = -(k+1);
                                        }
                                        for (int k = 1; k < waitTable.size(); k++) {
                                            if (waitTable.get(k)[1].equals("MUL.D") || waitTable.get(k)[1].equals("DIV.D")) {
                                                for (int r = schedule.length - 1; r >= 0; r--) {
                                                    if (r != 0 && schedule[r] < schedule[r - 1] && Integer.parseInt(waitTable.get(k)[2]) >= schedule[r]) {
                                                        schedule[r] = Integer.parseInt(waitTable.get(k)[2]);
                                                        break;
                                                    } else {
                                                        if (r == 0) schedule[r] = Integer.parseInt(waitTable.get(k)[2]);
                                                    }
                                                }
                                            }
                                        }
                                        for (int k = 0; k < schedule.length; k++) {
                                            if (k == schedule.length - 1 ) {
                                                for(int p = 0 ; p < schedule.length ; p++)
                                                    if(schedule[p] > 0){
                                                        firstAvailStart = schedule[p] + divCycles + 1;
                                                        break;
                                                    }
                                            } else {
                                                if (schedule[k] < schedule[k + 1] && schedule[k] >0) {
                                                    firstAvailStart = schedule[k+1] + divCycles + 1;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    waitTable.add(new String[]{"" + i, instTemp[0], "" + firstAvailStart});
                                    instTable[i][1] = "" + (firstAvailStart+1);
                                }
                            }
                            break;
                        case ("SUB.D"):
                        case ("ADD.D"):
                            foundSpace = false;
                            freeSpaceInd = 0;
                            for (int j = 1; j < addTable.length && !foundSpace; j++) {
                                if (addTable[j][0] == null || addTable[j][0].equals("0")) {
                                    addTable[j][0] = "1";
                                    addTable[j][1] = instTemp[0];
                                    addTable[j][2] = instTemp[1];
                                    addTable[j][7] = instTable[i][0];
                                    regWaitTable.add(new String[]{instTemp[1], "A" + j});
                                    foundSpace = true;
                                    freeSpaceInd = j;
                                    instIssued = true;
                                }
                            }
                            qk = false;
                            qj = false;
                            for (int j = 0; j < regWaitTable.size() && (!qk || !qj) && foundSpace; j++) {
                                if (regWaitTable.get(j)[0].equals(instTemp[2])) {
                                    addTable[freeSpaceInd][5] = regWaitTable.get(j)[1];
                                    qj = true;
                                }
                                if (regWaitTable.get(j)[0].equals(instTemp[3])) {
                                    addTable[freeSpaceInd][6] = regWaitTable.get(j)[1];
                                    qk = true;
                                }
                            }
                            if (!qk && foundSpace) {
                                if (instTemp[3].substring(0, 1).equals("R"))
                                    addTable[freeSpaceInd][4] = "" + regR[Integer.parseInt(instTemp[3].substring(1))];
                                else {
                                    addTable[freeSpaceInd][4] = "" + regF[Integer.parseInt(instTemp[3].substring(1))];
                                }
                            }
                            if (!qj && foundSpace) {
                                if (instTemp[2].substring(0, 1).equals("R"))
                                    addTable[freeSpaceInd][3] = "" + regR[Integer.parseInt(instTemp[3].substring(1))];
                                else {
                                    addTable[freeSpaceInd][3] = "" + regF[Integer.parseInt(instTemp[3].substring(1))];
                                }
                            }
                            if (foundSpace) instTable[i][1] = "" + cycles;
                            if (!qk && !qj && foundSpace) {
                                int write = cycles + addCycles + 1;
                                ArrayList<Integer> writeTimes = new ArrayList<>();
                                for(int y = 1 ; y < instTable.length ; y++){
                                    if(instTable[y][4] != null ){
                                        writeTimes.add(Integer.parseInt(instTable[y][4]));
                                    }
                                }
                                Collections.sort(writeTimes);
                                for(int y = 0 ; y < writeTimes.size() ; y++){
                                    if(writeTimes.get(y) == write){
                                        write++;
                                    }
                                }
                                instTable[i][2] = "" + (cycles + 1);
                                instTable[i][3] = "" + (cycles + addCycles);
                                instTable[i][4] = "" + (write);
                            }
                            if (!foundSpace) {
                                if (instTemp[0].equals("ADD.D") || instTemp[0].equals("SUB.D")) {
                                    int[] ends = new int[addTable.length - 1];
                                    for (int k = 0 ; k < ends.length ; k++)
                                        ends[k]=-1;
                                    for (int k = 1; k < addTable.length; k++) {
                                        if (addTable[k][7] != null && instTable[Integer.parseInt(addTable[k][7])][4] != null && Integer.parseInt(instTable[Integer.parseInt(addTable[k][7])][4])!=0)
                                            ends[k - 1] = Integer.parseInt(instTable[Integer.parseInt(addTable[k][7])][4]);
                                    }
                                    int firstAvailStart = -1;
                                    for (int k = 1; k < waitTable.size(); k++) {
                                        if (waitTable.get(k)[1].equals("ADD.D") || waitTable.get(k)[1].equals("SUB.D")) {
                                            for (int l = 0; l < ends.length; l++) {
                                                if (ends[l] == Integer.parseInt(waitTable.get(k)[2])) {
                                                    ends[l] = -1;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    for (int l = 0; l < ends.length; l++) {
                                        if (ends[l] != -1 && firstAvailStart != -1 && ends[l] < firstAvailStart) {
                                            firstAvailStart = ends[l];
                                        } else {
                                            if (firstAvailStart == -1)
                                                firstAvailStart = ends[l];
                                        }
                                    }
                                    if (firstAvailStart == -1) {
                                        int[] schedule = new int[addTable.length - 1];
                                        for (int k = schedule.length-1; k >= 0; k--) {
                                            schedule[k] = -(k+1);
                                        }
                                        for (int k = 1; k < waitTable.size(); k++) {
                                            if (waitTable.get(k)[1].equals("ADD.D") || waitTable.get(k)[1].equals("SUB.D")) {
                                                for (int r = schedule.length - 1; r >= 0; r--) {
                                                    if (r != 0 && schedule[r] < schedule[r - 1] && Integer.parseInt(waitTable.get(k)[2]) >= schedule[r]) {
                                                        schedule[r] = Integer.parseInt(waitTable.get(k)[2]);
                                                        break;
                                                    } else {
                                                        if (r == 0) schedule[r] = Integer.parseInt(waitTable.get(k)[2]);
                                                    }
                                                }
                                            }
                                        }
                                        for (int k = 0; k < schedule.length; k++) {
                                            if (k == schedule.length - 1 ) {
                                                for(int p = 0 ; p < schedule.length ; p++)
                                                    if(schedule[p] > 0){
                                                        firstAvailStart = schedule[p] + addCycles + 1;
                                                        break;
                                                    }
                                            } else {
                                                if (schedule[k] < schedule[k + 1] && schedule[k] >0) {
                                                    firstAvailStart = schedule[k+1] + addCycles + 1;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    waitTable.add(new String[]{"" + i, instTemp[0], "" + (firstAvailStart+1)});
                                    instTable[i][1] = "" + (firstAvailStart+1);
                                }
                            }
                            break;
                        case ("S.D"):
                            foundSpace = false;
                            freeSpaceInd = 0;
                            for (int j = 1; j < storeTable.length && !foundSpace; j++) {
                                if (storeTable[j][0] == null || storeTable[j][0].equals("0")) {
                                    storeTable[j][0] = "1";
                                    storeTable[j][1] = instTemp[1];
                                    storeTable[j][2] = instTemp[2];
                                    storeTable[j][3] = instTable[i][0];
                                    foundSpace = true;
                                    freeSpaceInd = j;
                                    instIssued = true;
                                }
                            }
                            qj = false;
                            for (int j = 0; j < regWaitTable.size() && (!qj) && foundSpace; j++) {
                                if (regWaitTable.get(j)[0].equals(instTemp[1])) {
                                    storeTable[freeSpaceInd][1] = regWaitTable.get(j)[1];
                                    qj = true;
                                }
                            }
                            if (!qj && foundSpace) {
                                if (instTemp[2].substring(0, 1).equals("R"))
                                    storeTable[freeSpaceInd][1] = "" + regR[Integer.parseInt(instTemp[1].substring(1))];
                                else {
                                    storeTable[freeSpaceInd][1] = "" + regF[Integer.parseInt(instTemp[1].substring(1))];
                                }
                            }
                            if (foundSpace) instTable[i][1] = "" + cycles;
                            if (!qj && foundSpace) {
                                int write = cycles + loadCycles + 1;
                                ArrayList<Integer> writeTimes = new ArrayList<>();
                                for(int y = 1 ; y < instTable.length ; y++){
                                    if(instTable[y][4] != null ){
                                        writeTimes.add(Integer.parseInt(instTable[y][4]));
                                    }
                                }
                                Collections.sort(writeTimes);
                                for(int y = 0 ; y < writeTimes.size() ; y++){
                                    if(writeTimes.get(y) == write){
                                        write++;
                                    }
                                }
                                instTable[i][2] = "" + (cycles + 1);
                                instTable[i][3] = "" + (cycles + loadCycles);
                                instTable[i][4] = "" + (write);
                            }
                            if (!foundSpace) {
                                int[] ends = new int[storeTable.length - 1];
                                for (int k = 0 ; k < ends.length ; k++)
                                    ends[k]=-1;
                                for (int k = 1; k < storeTable.length; k++) {
                                    if (storeTable[k][3] != null && instTable[Integer.parseInt(storeTable[k][3])][4] != null && Integer.parseInt(instTable[Integer.parseInt(storeTable[k][3])][4])!=0)
                                        ends[k - 1] = Integer.parseInt(instTable[Integer.parseInt(storeTable[k][3])][4]);
                                }
                                int firstAvailStart = -1;
                                for (int k = 1; k < waitTable.size(); k++) {
                                    if (waitTable.get(k)[1].equals("S.D")) {
                                        for (int l = 0; l < ends.length; l++) {
                                            if (ends[l] == Integer.parseInt(waitTable.get(k)[2])) {
                                                ends[l] = -1;
                                                break;
                                            }
                                        }
                                    }
                                }
                                for (int l = 0; l < ends.length; l++) {
                                    if (ends[l] != -1 && firstAvailStart != -1 && ends[l] < firstAvailStart) {
                                        firstAvailStart = ends[l];
                                    } else {
                                        if (firstAvailStart == -1)
                                            firstAvailStart = ends[l];
                                    }
                                }
                                if (firstAvailStart == -1) {
                                    int[] schedule = new int[storeTable.length - 1];
                                    for (int k = schedule.length-1; k >= 0; k--) {
                                        schedule[k] = -(k+1);
                                    }
                                    for (int k = 1; k < waitTable.size(); k++) {
                                        if (waitTable.get(k)[1].equals("S.D")) {
                                            for (int r = schedule.length - 1; r >= 0; r--) {
                                                if (r != 0 && schedule[r] < schedule[r - 1] && Integer.parseInt(waitTable.get(k)[2]) >= schedule[r]) {
                                                    schedule[r] = Integer.parseInt(waitTable.get(k)[2]);
                                                    break;
                                                } else {
                                                    if (r == 0) schedule[r] = Integer.parseInt(waitTable.get(k)[2]);
                                                }
                                            }
                                        }
                                    }
                                    for (int k = 0; k < schedule.length; k++) {
                                        if (k == schedule.length - 1 ) {
                                            for(int p = 0 ; p < schedule.length ; p++)
                                                if(schedule[p] > 0){
                                                    firstAvailStart = schedule[p] + loadCycles + 1;
                                                    break;
                                                }
                                        } else {
                                            if (schedule[k] < schedule[k + 1] && schedule[k] >0) {
                                                firstAvailStart = schedule[k+1] + loadCycles + 1;
                                                break;
                                            }
                                        }
                                    }
                                }
                                waitTable.add(new String[]{"" + i, instTemp[0], "" + (firstAvailStart+1)});
                                instTable[i][1] = "" + (firstAvailStart+1);
                            }
                            break;
                        case ("L.D"):
                            foundSpace = false;
                            freeSpaceInd = 0;
                            String dependency = "";
                            for (int j = 1; j < loadTable.length && !foundSpace; j++) {
                                if (loadTable[j][0] == null || loadTable[j][0].equals("0")) {
                                    loadTable[j][0] = "1";
                                    loadTable[j][1] = instTemp[1];
                                    loadTable[j][2] = instTemp[2];
                                    loadTable[j][3] = instTable[i][0];
                                    foundSpace = true;
                                    freeSpaceInd = j;
                                    instIssued = true;
                                    dependency = "L" + j;
                                    regWaitTable.add(new String[]{instTemp[1], "L" + j});
                                }
                            }
                            qj = false;
                            for (int j = 0; j < regWaitTable.size() && (!qj) && foundSpace; j++) {
                                if (regWaitTable.get(j)[0].equals(instTemp[1]) && !regWaitTable.get(j)[1].equals(dependency)) {
                                    loadTable[freeSpaceInd][1] = regWaitTable.get(j)[1];
                                    qj = true;
                                }
                            }
                            if (!qj && foundSpace) {
                                if (instTemp[2].substring(0, 1).equals("R"))
                                    loadTable[freeSpaceInd][1] = instTemp[1];
                                else {
                                    loadTable[freeSpaceInd][1] = instTemp[1];
                                }
                            }
                            if (foundSpace) instTable[i][1] = "" + cycles;
                            if (!qj && foundSpace) {
                                int write = cycles + loadCycles + 1;
                                ArrayList<Integer> writeTimes = new ArrayList<>();
                                for(int y = 1 ; y < instTable.length ; y++){
                                    if(instTable[y][4] != null ){
                                        writeTimes.add(Integer.parseInt(instTable[y][4]));
                                    }
                                }
                                Collections.sort(writeTimes);
                                for(int y = 0 ; y < writeTimes.size() ; y++){
                                    if(writeTimes.get(y) == write){
                                        write++;
                                    }
                                }
                                instTable[i][2] = "" + (cycles + 1);
                                instTable[i][3] = "" + (cycles + loadCycles);
                                instTable[i][4] = "" + (write);
                            }
                            if (!foundSpace) {
                                int[] ends = new int[loadTable.length - 1];
                                for (int k = 0 ; k < ends.length ; k++)
                                    ends[k]=-1;
                                for (int k = 1; k < loadTable.length; k++) {
                                    if (loadTable[k][3] != null && instTable[Integer.parseInt(loadTable[k][3])][4] != null && Integer.parseInt(instTable[Integer.parseInt(loadTable[k][3])][4])!=0)
                                        ends[k - 1] = Integer.parseInt(instTable[Integer.parseInt(loadTable[k][3])][4]);
                                }
                                int firstAvailStart = -1;
                                for (int k = 1; k < waitTable.size(); k++) {
                                    if (waitTable.get(k)[1].equals("L.D")) {
                                        for (int l = 0; l < ends.length; l++) {
                                            if (ends[l] == Integer.parseInt(waitTable.get(k)[2])) {
                                                ends[l] = -1;
                                                break;
                                            }
                                        }
                                    }
                                }
                                for (int l = 0; l < ends.length; l++) {
                                    if (ends[l] != -1 && firstAvailStart != -1 && ends[l] < firstAvailStart) {
                                        firstAvailStart = ends[l];
                                    } else {
                                        if (firstAvailStart == -1)
                                            firstAvailStart = ends[l];
                                    }
                                }
                                if (firstAvailStart == -1) {
                                    int[] schedule = new int[loadTable.length - 1];
                                    for (int k = schedule.length-1; k >= 0; k--) {
                                        schedule[k] = -(k+1);
                                    }
                                    for (int k = 1; k < waitTable.size(); k++) {
                                        if (waitTable.get(k)[1].equals("L.D")) {
                                            for (int r = schedule.length - 1; r >= 0; r--) {
                                                if (r != 0 && schedule[r] < schedule[r - 1] && Integer.parseInt(waitTable.get(k)[2]) >= schedule[r]) {
                                                    schedule[r] = Integer.parseInt(waitTable.get(k)[2]);
                                                    break;
                                                } else {
                                                    if (r == 0) schedule[r] = Integer.parseInt(waitTable.get(k)[2]);
                                                }
                                            }
                                        }
                                    }
                                    for (int k = 0; k < schedule.length; k++) {
                                        if (k == schedule.length - 1 ) {
                                            for(int p = 0 ; p < schedule.length ; p++)
                                                if(schedule[p] > 0){
                                                    firstAvailStart = schedule[p] + loadCycles + 1;
                                                    break;
                                                }
                                        } else {
                                            if (schedule[k] < schedule[k + 1] && schedule[k] >0) {
                                                firstAvailStart = schedule[k+1] + loadCycles + 1;
                                                break;
                                            }
                                        }
                                    }
                                }
                                waitTable.add(new String[]{"" + i, instTemp[0], "" + (firstAvailStart+1)});
                                instTable[i][1] = "" + (firstAvailStart+1);
                            }
                            break;
                    }
                }
            }
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println("CYCLE: " + cycles);
            System.out.println("----------------------------------------");
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println("Multiplication/Div Instruction Table");
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            for (int i = 0; i < mulTable.length; i++) {
                for (int j = 0; j < 8; j++) {
                    if (j != 7)
                        System.out.print(mulTable[i][j] + " - ");
                    else {
                        System.out.print(mulTable[i][j]);
                    }
                }
                System.out.println();
                System.out.println("----------------------------------------");
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            System.out.println("----------------------------------------");
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println("Add/Sub Instruction Table");
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            for (int i = 0; i < addTable.length; i++) {
                for (int j = 0; j < 8; j++) {
                    if (j != 7)
                        System.out.print(addTable[i][j] + " - ");
                    else {
                        System.out.print(addTable[i][j]);
                    }
                }
                System.out.println();
                System.out.println("----------------------------------------");
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            System.out.println("----------------------------------------");
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println("Store Instruction Table");
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            for (int i = 0; i < storeTable.length; i++) {
                for (int j = 0; j < 4; j++) {
                    if (j != 3)
                        System.out.print(storeTable[i][j] + " - ");
                    else {
                        System.out.print(storeTable[i][j]);
                    }
                }
                System.out.println();
                System.out.println("----------------------------------------");
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            System.out.println("----------------------------------------");
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println("load Instruction Table");
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            for (int i = 0; i < loadTable.length; i++) {
                for (int j = 0; j < 4; j++) {
                    if (j != 3)
                        System.out.print(loadTable[i][j] + " - ");
                    else {
                        System.out.print(loadTable[i][j]);
                    }
                }
                System.out.println();
                System.out.println("----------------------------------------");
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            System.out.println("----------------------------------------");
            System.out.println();
//            System.out.println("----------------------------------------");
//            System.out.println("R- Registers");
//            System.out.println("++++++++++++++++++++++++++++++++++++++++");
//            for (int i = 0; i < 32; i++) {
//                System.out.println("R" + i + ":" + regR[i]);
//            }
//            System.out.println("++++++++++++++++++++++++++++++++++++++++");
//            System.out.println("----------------------------------------");
//            System.out.println();
//            System.out.println("----------------------------------------");
//            System.out.println("F- Registers");
//            System.out.println("++++++++++++++++++++++++++++++++++++++++");
//            for (int i = 0; i < 32; i++) {
//                System.out.println("F" + i + ":" + regF[i]);
//            }
//            System.out.println("++++++++++++++++++++++++++++++++++++++++");
//            System.out.println("----------------------------------------");
//            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println("Registers awaiting update");
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            for (int i = 0; i < regWaitTable.size(); i++) {
                System.out.println(regWaitTable.get(i)[0] + "-" + regWaitTable.get(i)[1]);
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            System.out.println("----------------------------------------");
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println("Instruction Table");
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            for (int i = 0; i < instTable.length; i++) {
                System.out.println(instTable[i][0] + " - " + instTable[i][1] + " - " + instTable[i][2] + " - " + instTable[i][3] + " - " + instTable[i][4]);
                System.out.println("----------------------------------------");

            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            System.out.println("----------------------------------------");
            System.out.println();
            if (regWaitTable.isEmpty() && waitTable.size() == 1) {
                boolean flag = false;
                for (int k = 1; k < loadTable.length; k++) {
                    if (loadTable[k][0] != null && !loadTable[k][0].equals("0")) {
                        flag = true;
                    }
                }
                for (int k = 1; k < storeTable.length; k++) {
                    if (storeTable[k][0] != null && !storeTable[k][0].equals("0")) {
                        flag = true;
                    }
                }
                if (!flag) allInst = true;
            }
            if (regWaitTable.isEmpty() && waitTable.size() == 1 && allInst) {
                end = true;
            }
            cycles++;
        }
    }


    //divides all instruction into type, input and output registers / addresses
    //and check their validity
    public static void decodeInst(String[] instIn) {
        inst = new String[instIn.length][];
        for (int i = 0; i < instIn.length; i++) {
            inst[i] = instIn[i].split(" ");
        }
        for (int i = 0; i < instIn.length && !stop; i++) {
            boolean loadStore = false;
            if (inst[i].length == 3 || inst[i].length == 4) {
                String temp = inst[i][0];
                switch (temp) {
                    case ("MUL.D"):
                        break;
                    case ("DIV.D"):
                        break;
                    case ("ADD.D"):
                        break;
                    case ("SUB.D"):
                        break;
                    case ("S.D"):
                    case ("L.D"):
                        loadStore = true;
                        break;
                    default:
                        stop = true;
                        System.out.println("Problem in Instruction " + (i + 1) + " (incorrect Instruction type).");
                        break;
                }
                if (!stop) {
                    if (loadStore) {
                        if (inst[i].length == 3) {
                            if (inst[i][1].substring(0, 1).equals("R") || inst[i][1].substring(0, 1).equals("F")) {
                                try {
                                    int temp2 = Integer.parseInt(inst[i][1].substring(1));
                                    if (temp2 > 32 || temp2 < 1) {
                                        stop = true;
                                        System.out.println("Register out of bounds.");
                                    }
                                } catch (Exception e) {
                                    stop = true;
                                    System.out.println("Register Number is not a number.");
                                }
                                if (inst[i][2].split("\\+").length == 2) {
                                    String[] temp1 = inst[i][2].split("\\+");
                                    try {
                                        int n = Integer.parseInt(temp1[0]);
                                    } catch (Exception e) {
                                        stop = true;
                                        System.out.println("Address is not a number.");
                                    }
                                    try {
                                        if (temp1[1].substring(0, 1).equals("R")) {
                                            int n2 = Integer.parseInt(temp1[1].substring(1));
                                            if (n2 > 32 || n2 < 1) {
                                                stop = true;
                                                System.out.println("Register out of bounds.");
                                            }
                                        } else {
                                            stop = true;
                                            System.out.println("NOT a valid register type.");
                                        }
                                    } catch (Exception e) {
                                        stop = true;
                                        System.out.println("Register Number is not a number.");
                                    }
                                }
                            } else {
                                stop = true;
                                System.out.println("Problem in Instruction " + (i + 1) + " (incorrect Register name).");
                            }
                        } else {
                            stop = true;
                            System.out.println("Problem in Instruction " + (i + 1) + " (incorrect number of input).");
                        }
                    }
                } else {
                    if (inst[i].length == 4) {
                        for (int j = 1; j < 4; j++) {
                            if (inst[i][j].substring(0, 1).equals("R") || inst[i][j].substring(0, 1).equals("F")) {
                                try {
                                    int temp2 = Integer.parseInt(inst[i][j].substring(1));
                                    if (temp2 > 32 || temp2 < 1) {
                                        stop = true;
                                        System.out.println("Register out of bounds.");
                                    }
                                } catch (Exception e) {
                                    stop = true;
                                    System.out.println("Register Number is not a number.");
                                }
                            }
                        }
                    } else {
                        stop = true;
                        System.out.println("Problem in Instruction " + (i + 1) + " (incorrect number of input).");
                    }
                }
            }

        }
    }

    //takes instructions from user using console
    public static void takeInst() {
        boolean endInput = false;
        ArrayList<String> inputInst = new ArrayList<String>();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter either MUL.D or ADD.D or SUB.D or DIV.D or L.D or S.D");
        System.out.println("Enter Registers as R'number' or F'number'  ex. R14 , F12");
        System.out.println("Between the type and registers add spaces");
        System.out.println("Ex. ADD.D R11 R12 R13");
        System.out.println("InCase of Load/Store Enter address as number+R'number'  ex. 300+R14");
        while (!endInput) {
            System.out.println("Enter Instruction or End Input(just put 'END' all uppercase)...");
            String input = sc.nextLine();
            if (input.equals("END")) {
                endInput = true;
            } else {
                inputInst.add(input);
            }
        }
        String[] temp = new String[inputInst.size()];
        temp = inputInst.toArray(temp);
        decodeInst(temp);
    }

    public static void initTables() {

        //first array in every table holds the names of each column

        mulTable = new String[multTableSize + 1][8];
        mulTable[0][0] = "busy";
        mulTable[0][1] = "op";
        mulTable[0][2] = "output Register";
        mulTable[0][3] = "vj";
        mulTable[0][4] = "vk";
        mulTable[0][5] = "qj";
        mulTable[0][6] = "qk";
        mulTable[0][7] = "inst no";

        addTable = new String[addTableSize + 1][8];
        addTable[0][0] = "busy";
        addTable[0][1] = "op";
        addTable[0][2] = "output Register";
        addTable[0][3] = "vj";
        addTable[0][4] = "vk";
        addTable[0][5] = "qj";
        addTable[0][6] = "qk";
        addTable[0][7] = "inst no";

        loadTable = new String[loadTableSize + 1][4];
        loadTable[0][0] = "busy";
        loadTable[0][1] = "output Register";
        loadTable[0][2] = "address";
        loadTable[0][3] = "inst no";

        storeTable = new String[storeTableSize + 1][4];
        storeTable[0][0] = "busy";
        storeTable[0][1] = "input Register";
        storeTable[0][2] = "address";
        storeTable[0][3] = "inst no";

        instTable = new String[inst.length + 1][5];
        instTable[0][0] = "number/id";
        instTable[0][1] = "issued";
        instTable[0][2] = "start";
        instTable[0][3] = "end";
        instTable[0][4] = "write";

        for (int i = 1; i < inst.length + 1; i++) instTable[i][0] = "" + (i);
        for (int i = 0; i < 32; i++) {
            regR[i] = 0;
            regF[i] = 0;
        }
        for (int i = 0; i < 100; i++) mem[i] = 0;
    }

    public static void main(String[] args) {
//        divCycles = 7;
//        multCycles = 6; //no. of cycles needed to execute mult/div
//        addCycles = 4;  //no. of cycles needed to execute add/sub
//        loadCycles = 1; //no. of cycles needed to execute load/store
//
//        multTableSize = 2;
//        addTableSize = 3;
//        loadTableSize = 2;
//        storeTableSize = 2;

        divCycles = 40;
        multCycles = 6; //no. of cycles needed to execute mult/div
        addCycles = 4;  //no. of cycles needed to execute add/sub
        loadCycles = 2; //no. of cycles needed to execute load/store

        multTableSize = 2;
        addTableSize = 3;
        loadTableSize = 3;
        storeTableSize = 3;

        String[] instIn = new String[]{"MUL.D F3 F1 F2", "MUL.D F5 F3 F4", "ADD.D F7 F2 F6", "ADD.D F5 F5 F5", "MUL.D F11 F7 F10", "ADD.D F5 F5 F11"};
        //String[] instIn = new String[]{"L.D F6 32+R2", "L.D F2 44+R3", "MUL.D F0 F2 F4", "SUB.D F8 F2 F6", "DIV.D F10 F0 F6", "ADD.D F6 F8 F2"};
        decodeInst(instIn);
        //takeInst();
        initTables();
        if (!stop)
            startSimulation();
        else {
            System.out.println("an error occurred. execution aborted.");
        }
    }
}
