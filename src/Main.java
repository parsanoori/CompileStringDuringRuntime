public class Main {
    public static void main(String[] args) throws Exception {
        String helloWorldClassString =

                "package com.parsa;" +
                "public class HelloWorld {\n" +
                "   static{\n" +
                "       System.out.println(\"Class Loaded\");\n" +
                "   }\n" +
                "   public HelloWorld(){\n" +
                "       System.out.println(\"Reached Constructor\");" +
                "   }\n" +
                "   public void sayHello() {\n" +
                "       System.out.println(\"Hello World!\");\n" +
                "   }\n" +
                "}\n";


        System.out.println("Compiling and invoking sayHello via writing to file ..");
        Class<?> compiled1 = CompileToCustomJavaFile.compile("com.parsa.HelloWorld",helloWorldClassString);
        Object classClass1 = compiled1.getConstructor().newInstance();
        classClass1.getClass().getMethod("sayHello").invoke(classClass1);

        System.out.println();

        System.out.println("Compiling and invoking sayHello via a custom JavaFile ..");
        Class<?> compiled2 = CompileToCustomJavaFile.compile("com.parsa.HelloWorld",helloWorldClassString);
        Object classClass2 = compiled2.getConstructor().newInstance();
        classClass2.getClass().getMethod("sayHello").invoke(classClass2);

    }
}
