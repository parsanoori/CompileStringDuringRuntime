import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CompileToCustomJavaFile {

    /**
     * Compiles a String given to it via JavaCompiler and returns the 'Class' object of the
     * class as the output.
     * @param name which is the name of class going get compiled
     * @param code the code of the class under question
     * @return Class
     */
    static public Class<?> compile(String name,String code){

        ArrayList<JavaSourceFromString> inputFiles = new ArrayList<>(); // An array containing JavaSourceFrom String which is used by compiler to read what's going to be compiled.
        Map<String,JavaFileObjectForOutput> output = new HashMap<>(); // A Mao containing outputs of JavaCompiler and their outputs

        inputFiles.add(new JavaSourceFromString(name,code));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler(); // used to get the system compiler
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null,null,null); // the standard system compiler

        JavaFileManager fileManager = new ForwardingJavaFileManager<>(stdFileManager) { // our own file manager used to store the outputs in Map mentioned above
            // the below method is called by compiler as a mean of giving the output compiled class to us
            @Override
            public JavaFileObjectForOutput getJavaFileForOutput(Location location, String className, JavaFileObjectForOutput.Kind kind, FileObject sibling) {
                JavaFileObjectForOutput result = new JavaFileObjectForOutput(className, kind);
                output.put(className, result);
                return result;
            }
        };

        // getting a task from compiler and calling it. Our file manager is passed to provide us the functionalities mentioned above
        compiler.getTask(null,fileManager,null,null,null,inputFiles).call();

        // Creating an anonymous ClassLoader and calling our own defined findClass method to get the class that is the result we want
        Class<?> result = new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) {
                return defineClass(name,output.get(name).getBytes(),0,output.get(name).getBytes().length);
            }
        }.findClass(name);

        return result;
    }
    /**
     * A file object used to represent source coming from a string.
     */
    private static final class JavaSourceFromString extends SimpleJavaFileObject {
        /**
         * The source code of this "file".
         */
        final String code;

        /**
         * Constructs a new JavaSourceFromString.
         * @param name the name of the compilation unit represented by this file object
         * @param code the source code for the compilation unit represented by this file object
         */
        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    /**
     * Same as JavaSourceFromString but for the output file
     */
    static final class JavaFileObjectForOutput extends SimpleJavaFileObject {
        /**
         * Containing the output Class data (.class file data in regular compiling) on a ByteArrayOutputStream
         */
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        JavaFileObjectForOutput(String name, JavaFileObjectForOutput.Kind kind) {
            super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
        }

        /**
         * used for defining class and accessing compiled data of the class
         * @return byte[] for the usage mentioned in the method description
         */
        byte[] getBytes() {
            return os.toByteArray();
        }

        /**
         * called by the JavaCompiler to fill the "os" which is the main compiled data of the class we want
         * @return the "os" for filling it by the compiler
         */
        @Override
        public OutputStream openOutputStream() {
            return os;
        }
    }
}
