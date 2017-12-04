package me.vanlin.classloader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

public class MyClassLoader extends ClassLoader {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // TODO Auto-generated method stub
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        MyClassLoader myClassLoader = new MyClassLoader(classLoader, "d:\\class\\");

        Class helloWorld = myClassLoader.loadClass("me.vanlin.HelloWorld");
        Object obj = helloWorld.newInstance();

        Method method = helloWorld.getMethod("sayHelloWorld");

        method.invoke(obj);
    }

    private final ClassLoader parent = null; // parent classloader
    private final String path;

    public MyClassLoader(final ClassLoader parent, final String path) {
        super(parent);
        this.path = path;
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        synchronized (name) {
            Class<?> clazz = findLoadedClass(name);

            if (Objects.isNull(clazz)) {
                ClassLoader parent = getParent().getParent();
                try {
                    System.out.println("try to use ExtClassLoader to load class : " + name);
                    clazz = parent.loadClass(name);// 如果没有双亲委派模型，，那么 自定义加载的类无法找到 系统 类 比如  java.lang.System  java.lang.Thread这些
                } catch (ClassNotFoundException e) {
                    System.out.println("ExtClassLoader.loadClass :" + name + " Failed");
                }
                if (Objects.isNull(clazz)) {
                    System.out.println("try to MyClassLoader load class : " + name);
                    clazz = findClass(name);

                    if (Objects.isNull(clazz)) {
                        System.out.println("MyClassLoader.loadClass :" + name + " Failed");
                    } else {
                        System.out.println("MyClassLoader.loadClass :" + name + " Successful");
                    }
                }
            }

            return clazz;
        }
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        System.out.println("try findClass " + name);

        String classPath = name.replace(".", "\\") + ".class";
        String classFile = path + classPath;

        byte[] data = getClassFileBytes(classFile);

        if (Objects.isNull(data)) {
            throw new ClassNotFoundException("MyClassLoader  class not found!");
        }

        Class clazz = defineClass(name, data, 0, data.length);

        if (Objects.isNull(clazz)) {
            System.out.println("MyClassLoader.findClass() ERR ");
            throw new ClassFormatError();
        }

        return clazz;
    }

    private byte[] getClassFileBytes(final String classFile) {
        try(FileInputStream fis = new FileInputStream(classFile);
            FileChannel fc = fis.getChannel();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            WritableByteChannel wbc = Channels.newChannel(baos);
        ) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            while (true) {
                int i = fc.read(buffer);
                if (i == 0 || i == -1) {
                    break;
                }
                buffer.flip();
                wbc.write(buffer);
                buffer.clear();
            }

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
