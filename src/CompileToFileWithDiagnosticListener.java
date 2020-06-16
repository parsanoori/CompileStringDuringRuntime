import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner14;
import javax.tools.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

/**
 * Compiles a string and saves it to a .class file same as CompileToFile but the difference is that
 * this class's compile method gets a DiagnosticCollector object in order to diagnose the code under question
 */
public class CompileToFileWithDiagnosticListener {
    /**
     * compiles the given string
     * @param name name of the class going to get compiled
     * @param code the code going to get compiled
     * @param diagnostics the collector that collects errors that occur in the code and diagnostics of the file manager
     * @return the class under question
     * @throws NoAccessToCurrentFolderException in case that there's no access to current folder
     */
    public static Class<?> compile(String name,String code,DiagnosticCollector<JavaFileObject> diagnostics) throws NoAccessToCurrentFolderException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)){

            String sourceFilePathWithoutExtension = name.replace('.', '/');
            File sourceFile = new File(sourceFilePathWithoutExtension + ".java");
            sourceFile.getParentFile().mkdirs();
            Files.writeString(sourceFile.toPath(), code);

            Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjects(sourceFile);

            compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnit).call(); // diagnostics is passed in to it so diagnoses get into it

            if (!(new File(sourceFilePathWithoutExtension + ".class")).exists())
                return null;

            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new URL("file:" + System.getProperty("user.dir") + '/')});
            return classLoader.loadClass(name);
        } catch (Exception exception){
            throw new NoAccessToCurrentFolderException();
        }
    }


}
