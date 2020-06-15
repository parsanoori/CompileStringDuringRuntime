import javax.tools.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

public class CompileToFile {
    /**
     * Compiles a String as data to compile and saves to a file on Disk (Lower Speed due to writing to disk)
     *
     * @param name name of the class under question
     * @param code the code of the class under question passed as a parameter
     * @return a Class which is the result expected
     * @throws Exception because there may be not access to the files in running directory
     */
    static Class<?> compile(String name, String code) throws Exception {
        StandardJavaFileManager fileManager = null;
        try {
            File sourceFile = new File(name.replace('.', '/') + ".java"); // creates a new file object as a source file
            sourceFile.getParentFile().mkdirs(); // creates the directories needed for the file to get placed in one of them
            Files.writeString(sourceFile.toPath(), code); // writes the string data of the code under question to the file mentioned above

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler(); // Gets the system compiler for our use
            fileManager = compiler.getStandardFileManager(null, null, null); // gets the standard file manager via compiler achieved above
            Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjects(sourceFile); // The iterable object passed to the compiler so it can read the inputs
            compiler.getTask(null, fileManager, null, null, null, compilationUnit).call(); // gets the compilation task from the compiler and calls it

            // gets a new instance of URLClassLoader
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new URL("file:" + System.getProperty("user.dir") + '/')});

            // returns the class we want via calling the loadClass method of the classLoader achieved above
            return classLoader.loadClass(name);
        } finally {
            assert fileManager != null;
            fileManager.close();
        }
    }
}
