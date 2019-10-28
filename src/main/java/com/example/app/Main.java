package com.example.app;

import com.example.app.service.impl.LogServiceImpl;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/18/19, MarkHuang,new
 * </ul>
 * @since 10/18/19
 */
public class Main {
    private static String logStructure = "%time [%th] [%pname] [%pId] [%aId] [%idNo] [%dob] | %x | %y | %z [%status] %content";

    public static void main(String[] args) throws Exception {
        MainArgs mainArgs = MainArgsParser.parseMainArgs(args);
//        LogServiceImpl.getInstance(mainArgs).run();
        //todo group by
        System.out.println("Success.");
    }

}

