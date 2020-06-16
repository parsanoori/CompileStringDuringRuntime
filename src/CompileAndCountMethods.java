import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner14;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Set;

public class  CompileAndCountMethods {
    /**
     * compiles a string class to a file and counts the number of methods in the class
     * @param name name of the class
     * @param code the code under question
     * @return the ReturnType which contains a class and an integer which is the number of methods
     * @throws NoAccessToCurrentFolderException if the folder is not accessible
     * @throws CompileErrorHappenedException if a compile error happens
     */
    public static ReturnType compile(String name, String code) throws NoAccessToCurrentFolderException, CompileErrorHappenedException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {

            String sourceFilePathWithoutExtension = name.replace('.', '/');
            File sourceFile = new File(sourceFilePathWithoutExtension + ".java");

            sourceFile.getParentFile().mkdirs();
            Files.writeString(sourceFile.toPath(), code);

            Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjects(sourceFile);

            CountMethodsScanner scanner = new CountMethodsScanner();

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnit);

            task.setProcessors(Collections.singleton(new CountElementsProcessor(scanner))); // sets the

            task.call();

            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new URL("file:" + System.getProperty("user.dir") + '/')});

            return new ReturnType(classLoader.loadClass(name),scanner.getNumberOfMethods());
        }  catch (ClassNotFoundException exception){
            throw new CompileErrorHappenedException();
        } catch (Exception exception){
            throw new NoAccessToCurrentFolderException();
        }
    }

    /**
     * Our custom scanner for counting methods
     */
    public static class CountMethodsScanner extends ElementScanner14< Void, Void > {
        private int numberOfMethods;

        @Override
        public Void visitExecutable(final ExecutableElement executable, final Void p ) { // if the scanner is visiting executable (method we increment)
            ++numberOfMethods;
            return super.visitExecutable( executable, p );
        }

        public int getNumberOfMethods() {
            return numberOfMethods;
        }
    }

    /**
     * Annotation processor for calling out scanner
     */
    @SupportedSourceVersion( SourceVersion.RELEASE_14 )
    @SupportedAnnotationTypes( "*" )
    private static class CountElementsProcessor extends AbstractProcessor { // custom annotation processor made for calling scanner
        private final CountMethodsScanner scanner;

        public CountElementsProcessor( CountMethodsScanner scanner ) {
            this.scanner = scanner;
        }

        @Override
        public boolean process( final Set< ? extends TypeElement > types,
                                final RoundEnvironment environment ) {

            if( !environment.processingOver() ) { // while processing is not over
                for( final Element element: environment.getRootElements() ) { // for each element in the root elements of our round environment
                    scanner.scan( element ); // we call the scanner to scan that element
                }
            }
            return true;
        }
    }

    /**
     * our own made return type
     */
    public static class ReturnType{
        public final Class<?> outputClass;
        public final Integer methodsCount;

        public ReturnType(Class<?> outputClass, Integer methodsCount) {
            this.outputClass = outputClass;
            this.methodsCount = methodsCount;
        }
    }
}
