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
     * @throws NoAccessToCurrentFolderException because there may be not access to the files in running directory
     */
    static Class<?> compile(String name, String code) throws NoAccessToCurrentFolderException, CompileErrorHappenedException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler(); // Gets the system compiler for our use
        // gets the standard file manager via compiler achieved above
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            String sourceFilePathWithoutExtension = name.replace('.', '/');
            File sourceFile = new File(sourceFilePathWithoutExtension + ".java"); // creates a new file object as a source file
            sourceFile.getParentFile().mkdirs(); // creates the directories needed for the file to get placed in one of them
            Files.writeString(sourceFile.toPath(), code); // writes the string data of the code under question to the file mentioned above

            Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjects(sourceFile); // The iterable object passed to the compiler so it can read the inputs

            // neither diagnostic listener nor out are passed to the get task method since we want these placed in the stdout
            compiler.getTask(null, fileManager, null, null, null, compilationUnit).call(); // gets the compilation task from the compiler and calls it

            // gets a new instance of URLClassLoader
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new URL("file:" + System.getProperty("user.dir") + '/')});

            // returns the class we want via calling the loadClass method of the classLoader achieved above
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException exception){
            throw new CompileErrorHappenedException();
        } catch (Exception exception){
            throw new NoAccessToCurrentFolderException();
        }
    }
}
