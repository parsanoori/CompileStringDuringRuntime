import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

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
                "       System.out.println(\"Hello World\");\n" +
                "   }\n" +
                "}\n";


        System.out.println("Compiling and invoking sayHello via writing to file ..");
        Class<?> compiled1 = CompileToCustomJavaFile.compile("com.parsa.HelloWorld",helloWorldClassString);
        Object classClass1 = compiled1.getConstructor().newInstance();
        classClass1.getClass().getMethod("sayHello").invoke(classClass1);

        System.out.println();

        System.out.println("Compiling and invoking sayHello via a custom JavaFile ..");
        Class<?> compiled2 = CompileToFile.compile("com.parsa.HelloWorld",helloWorldClassString);
        Object classClass2 = compiled2.getConstructor().newInstance();
        classClass2.getClass().getMethod("sayHello").invoke(classClass2);

        System.out.println();

        String classSourceWithError =
                "package com.parsa;\n" +
                "public class HelloWorldWithError {\n" +
                "   public static void main(String[] args){\n" +
                "       System.out.println(\"Hello World!\")\n" +
                "   }\n" +
                "}";
        System.out.println("Compiling class with error");
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        CompileToFileWithDiagnosticListener.compile("com.parsa.HelloWorldWithError",classSourceWithError,diagnosticCollector);
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticCollector.getDiagnostics())
            System.err.format("%s, line %d in %s\n",diagnostic.getMessage(null),
                    diagnostic.getLineNumber(),diagnostic.getSource().getName());

        System.out.println();

        System.out.println("Compiling and counting methods");
        String classSourceToCountMethods =
                "package com.parsa;\n" +
                "public class ClassToCountMethods {\n" +
                "   public static void main(String[] args){\n" +
                "       System.out.println(\"Hello World!\");\n" +
                "   }\n" +
                "   public void helloJavaCup(){\n" +
                "       System.out.println(\"Hello JavaCup\");\n" +
                "   }\n" +
                "}";

        CompileAndCountMethods.ReturnType result = CompileAndCountMethods.compile("com.parsa.ClassToCountMethods",classSourceToCountMethods);
        System.out.println("Methods count : " + result.methodsCount);
        Object classClass3 = result.outputClass.getConstructor().newInstance();
        classClass3.getClass().getMethod("helloJavaCup").invoke(classClass3);


    }
}
