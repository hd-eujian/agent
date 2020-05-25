package com.eujian.agent;

import java.lang.instrument.Instrumentation;

public class PreMainTraceAgent {
    public static void agentmain (String agentArgs, Instrumentation inst) throws Exception {
        System.out.println("agent begin agentArgs="+agentArgs);
        MyClassFileTransformer myClassFileTransformer;
        if("1".equals(agentArgs)){
            myClassFileTransformer = new MyClassFileTransformer(true);
//            boolean b = inst.removeTransformer(new MyClassFileTransformer());
//            System.out.println("remove b="+b);
            inst.addTransformer(myClassFileTransformer,true);
        }else {
            myClassFileTransformer = new MyClassFileTransformer();
            inst.addTransformer(myClassFileTransformer,true);
        }
        System.out.println("agent end");

        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class clazz : allLoadedClasses){
            if(clazz.getName().contains("com.eujian.arthaslearn.controller.MyService")){
                inst.retransformClasses(clazz);
                System.out.println("重新加载"+clazz);
            }
        }
    }
}
