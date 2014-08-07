package ru.kapahgaiii.client;

import org.kohsuke.args4j.Option;

import java.io.Serializable;

public class CmdArguments implements Serializable {

    @Option(name = "-rCount", usage = "Reader threads count")
    private int rCount = 0;

    @Option(name = "-wCount", usage = "Writer threads count")
    private int wCount = 0;

    @Option(name = "-idList", usage = "Id's range")
    private String idListString = "";
    private int[] idList = null;

    public int getrCount() {
        return rCount;
    }

    public int getwCount() {
        return wCount;
    }

    public int[] getIdList() throws NumberFormatException {
        if(idList==null){
            idList = Instruments.stringToArray(idListString);
        }
        return idList;
    }
}