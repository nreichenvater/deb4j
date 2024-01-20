package jku.win.systemsoftware.simpledebugger.commands;

import java.util.Optional;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;

import jku.win.systemsoftware.simpledebugger.Breakpoint;
import jku.win.systemsoftware.simpledebugger.Debugger;

public class SetBreakpointCommand implements Command {
	
	private final Breakpoint breakpoint;
	
	public SetBreakpointCommand(Breakpoint breakpoint) {
		this.breakpoint = breakpoint;
	}

	@Override
	public void execute(Debugger debugger) {
		Optional<ReferenceType> type = debugger.getVM().allClasses().stream().filter(c -> c.name().equals(breakpoint.getClassName())).findAny();
		if(type.isPresent()) {
			setBreakpoint(debugger, type.get());
		} else {
			setDeferredBreakpoint(debugger);
		}
	}
	
	private void setBreakpoint(Debugger debugger, ReferenceType type) {
		try {
			Optional<Location> locationOpt = type.locationsOfLine(breakpoint.getLineNumber()).stream().findFirst();
			if (locationOpt.isPresent()) {
	            Location location = locationOpt.get();
	            EventRequestManager erm = debugger.getVM().eventRequestManager();
	            boolean bpExists = erm.breakpointRequests().stream().anyMatch(req -> req.location().equals(location));
	            if (bpExists) {
	                System.out.println("there is already a breakpoint set at " + breakpoint.getClassName() + ":" + breakpoint.getLineNumber());
	            } else {
	            	erm.createBreakpointRequest(location).enable();
	                System.out.println("breakpoint set at " + breakpoint.getClassName() + ":" + breakpoint.getLineNumber());
	            }
	        } else {
	            System.out.println("the provided line number cannot be found in the specified type");
	        }
		} catch (AbsentInformationException e) {
			System.out.println("there is no line number information for the specified type");
		}
	}
	
	private void setDeferredBreakpoint(Debugger debugger) {
		EventRequestManager erm = debugger.getVM().eventRequestManager();
        ClassPrepareRequest req = erm.createClassPrepareRequest();
        req.addClassFilter(breakpoint.getClassName());
        req.addCountFilter(1);
        req.enable();
        boolean defBpExists = debugger.getDeferredBreakpoints().contains(breakpoint);
        if (defBpExists) {
        	System.out.println("there is already a deferred breakpoint set at " + breakpoint.getClassName() + ":" + breakpoint.getLineNumber());
        } else {
        	debugger.getDeferredBreakpoints().add(breakpoint);
        	System.out.println("deferred breakpoint set at " + breakpoint.getClassName() + ":" + breakpoint.getLineNumber());
        }
	}

}
