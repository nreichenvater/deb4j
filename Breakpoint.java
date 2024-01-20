package jku.win.systemsoftware.simpledebugger;

public class Breakpoint {
	
    private final String className;
    private final int lineNumber;

    public Breakpoint(String className, int lineNumber) {
        this.className = className;
        this.lineNumber = lineNumber;
    }

    public String getClassName() {
        return className;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Breakpoint other = (Breakpoint) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (lineNumber != other.lineNumber)
            return false;
        return true;
    }
}
