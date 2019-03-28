package peer.core.folder;

public class Fragment {
    private String fileName;
    private long sizeFile;
    private long pointer;
    private int fragment;

    public Fragment(String fileName, long sizeFile, long pointer, int fragment) {
        this.fileName = fileName;
        this.sizeFile = sizeFile;
        this.pointer = pointer;
        this.fragment = fragment;
    }

    public String getFileName() {
        return fileName;
    }

    public long getSizeFile() {
        return sizeFile;
    }

    public long getPointer() {
        return pointer;
    }

    public int getFragment() {
        return fragment;
    }

    @Override
    public String toString() {
        return fileName + " " + sizeFile + " " + pointer + " " + fragment;
    }
}
