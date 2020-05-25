package com.eujian.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MyClassFileTransformer implements ClassFileTransformer {

    private boolean isReLoad = false;

    public MyClassFileTransformer() {
    }

    public MyClassFileTransformer(boolean isReLoad) {
        this.isReLoad = isReLoad;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (!className.equals("com/eujian/arthaslearn/controller/MyService")) {
            return null;
        }
        try {

            System.out.println("进入 isReLoad:"+isReLoad);
            System.out.println("进入 className:"+className);
            System.out.println("进入 loader:"+loader);
            System.out.println("进入 classBeingRedefined:"+classBeingRedefined);
            CtClass cl = null;
            ClassPool classPool = ClassPool.getDefault();
            cl = classPool.getCtClass("com.eujian.arthaslearn.controller.MyService");

            System.out.println("cl.isFrozen()+"+cl.isFrozen());
            if(isReLoad){
                //重新加载本地class文件
                cl.defrost();
                return readStream(ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class"), true);
            }
            CtMethod method = cl.getDeclaredMethod("send");

            //插入你想要的代码
            method.insertBefore("System.out.println(\"send-begin\");");
            method.insertAfter("System.out.println(\"send-end\");");

            byte[] transformed = cl.toBytecode();

            cl.detach();
            return transformed;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private static byte[] readStream(InputStream inputStream, boolean close) throws IOException {
        if(inputStream == null) {
            throw new IOException("Class not found");
        } else {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] data = new byte[4096];

                int bytesRead;
                while((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                    outputStream.write(data, 0, bytesRead);
                }

                outputStream.flush();
                byte[] var5 = outputStream.toByteArray();
                return var5;
            } finally {
                if(close) {
                    inputStream.close();
                }

            }
        }
    }
}
