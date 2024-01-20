package jku.win.systemsoftware.simpledebugger.commands;

import java.util.Optional;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequestManager;

import jku.win.systemsoftware.simpledebugger.Breakpoint;
import jku.win.systemsoftware.simpledebugger.Debugger;

public class DeleteBreakpointCommand implements Command {
	
	private final Breakpoint breakpoint;
	
	public DeleteBreakpointCommand(Breakpoint breakpoint) {
		this.breakpoint = breakpoint;
	}

	@Override
	public void execute(Debugger debugger) {
		Optional<ReferenceType> type = debugger.getVM().allClasses().stream().filter(c -> c.name().equals(breakpoint.getClassName())).findAny();
		if(type.isPresent()) {
			removeBreakpoint(debugger, type.get());
		} else {
			removeDeferredBreakpoint(debugger);
		}
	}
	
	private void removeBreakpoint(Debugger debugger, ReferenceType type) {
		try {
            Optional<Location> locationOpt = type.locationsOfLine(breakpoint.getLineNumber()).stream().findFirst();
            if (locationOpt.isPresent()) {
                Location location = locationOpt.get();
                EventRequestManager erm = debugger.getVM().eventRequestManager();
                Optional<BreakpointRequest> optRequest = erm.breakpointRequests().stream().filter(req -> req.location().equals(location)).findAny();
                if (optRequest.isPresent()) {
                	erm.deleteEventRequest(optRequest.get());
                    System.out.print("removed breakpoint at " + breakpoint.getClassName() + ":" + breakpoint.getLineNumber());
                } else {
                    System.out.print("there is no breakpoint set at " + breakpoint.getClassName() + ":" + breakpoint.getLineNumber());
                }
            } else {
                System.out.println("the provided line number cannot be found in the specified type");
            }
        } catch (AbsentInformationException e) {
            System.out.println("there is no line number information for the specified type");
        }
	}
	
	private void removeDeferredBreakpoint(Debugger debugger) {
		if(debugger.getDeferredBreakpoints().contains(breakpoint)) {
			debugger.getDeferredBreakpoints().remove(breakpoint);
			System.out.print("removed deferred breakpoint at " + breakpoint.getClassName() + ":" + breakpoint.getLineNumber());
		} else {
			System.out.print("there is no deferred breakpoint set at " + breakpoint.getClassName() + ":" + breakpoint.getLineNumber());
		}
	}

}
