package jku.win.systemsoftware.simpledebugger.commands;

import com.sun.jdi.request.EventRequestManager;

import jku.win.systemsoftware.simpledebugger.Breakpoint;
import jku.win.systemsoftware.simpledebugger.Debugger;

public class ListBreakpointsCommand implements Command {

	@Override
	public void execute(Debugger debugger) {
		EventRequestManager erm = debugger.getVM().eventRequestManager();
		erm.breakpointRequests().stream().map(bpr -> bpr.location()).forEach(loc -> {
            System.out.println("breakpoint set at " + loc.declaringType().name() + ":" + loc.lineNumber());
        });
		for(Breakpoint bp : debugger.getDeferredBreakpoints()) {
			System.out.println("deferred breakpoint set at " + bp.getClassName() + ":" + bp.getLineNumber());
		}
	}

}
