package jku.win.systemsoftware.simpledebugger.commands;

import java.util.Optional;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;

import jku.win.systemsoftware.simpledebugger.Debugger;

public class StackTraceCommand implements Command {

	@Override
	public void execute(Debugger debugger) {
		 Optional<ThreadReference> tROpt = debugger.getVM().allThreads().stream().filter(ref -> ref.name().equals("main")).findAny();
	        if (tROpt.isPresent()) {
	            try {
	            	tROpt.get().frames().stream().map(StackFrame::location).forEach(loc -> {
	                    System.out.print(loc.declaringType().name() + "." + loc.method().name());
	                    try {
	                        System.out.print("(" + loc.sourceName() + ":" + loc.lineNumber() + ")");
	                    } catch (AbsentInformationException e) {
	                        System.out.print("no source information found");
	                    }
	                    System.out.println("");
	                });
	            } catch (IncompatibleThreadStateException e) {
	                throw new RuntimeException(e);
	            }
	        } else {
	            System.out.println("no reference found for thread main");
	        }
	}

}
