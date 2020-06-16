public final class NoAccessToCurrentFolderException extends Exception {
    NoAccessToCurrentFolderException(){
        super("There's no access to current folder");
    }
}