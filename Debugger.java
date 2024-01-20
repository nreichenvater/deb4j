package jku.win.systemsoftware.simpledebugger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;

import jku.win.systemsoftware.simpledebugger.commands.SetBreakpointCommand;

import com.sun.jdi.connect.IllegalConnectorArgumentsException;

public class Debugger {

	private VirtualMachine vm;
	private boolean suspended;
	private boolean running;
	private final List<Breakpoint> deferredBreakpoints;
	private SimpleDebuggerLauncher debuggerLauncher;
	
	public List<Breakpoint> getDeferredBreakpoints() {
		return deferredBreakpoints;
	}

	public Debugger(SimpleDebuggerLauncher debuggerLauncher) {
		suspended = false;
		running = true;
		this.debuggerLauncher = debuggerLauncher;
		deferredBreakpoints = new ArrayList<>();
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void suspend() {
		suspended = true;
		vm.suspend();
		debuggerLauncher.waitForInput();
	}
	
	public void resume() {
		suspended = false;
		vm.resume();
	}
	
	public void exit() {
        	suspended = false;
        	vm.exit(0);
        }

	public boolean isRunning() {
		return running;
	}
	
	public VirtualMachine getVM() {
		return vm;
	}
	
	public void handleEvents() {
		while(running) {
			try {
				EventSet events = vm.eventQueue().remove();
				for(Event e : events) {
					processEvent(e);
				}
				events.resume();
			} catch (InterruptedException e) {
				e.printStackTrace();
				vm.exit(0);
                running = false;
			}
		}
	}
	
	public void processEvent(Event e) {
		if (e instanceof VMStartEvent) {
            System.out.println("VM started...");
            suspend();
        } else if(e instanceof VMDisconnectEvent) {
        	System.out.println("VM disconnected...");
        	running = false;
        } else if(e instanceof VMDeathEvent) {
        	System.out.println("VM died...");
        	running = false;
        } else if (e instanceof ClassPrepareEvent) {
            ClassPrepareEvent cpe = (ClassPrepareEvent) e;
            ReferenceType type = cpe.referenceType();
            List<Breakpoint> bpForClass = deferredBreakpoints.stream().filter(bp -> bp.getClassName().equals(type.name())).collect(Collectors.toList());
            deferredBreakpoints.removeAll(bpForClass);
            for(Breakpoint bp : bpForClass) {
            	new SetBreakpointCommand(bp).execute(this);
            }
        } else if (e instanceof BreakpointEvent) {
            BreakpointEvent bpe = (BreakpointEvent) e;
            System.out.println("hit breakpoint at " + bpe.location().declaringType().name() + ":" + bpe.location().lineNumber());
            suspend();
        } else if (e instanceof StepEvent) {
            StepEvent event = (StepEvent)e;
            System.out.println("stepped to " + event.location().declaringType().name() + ":" + event.location().lineNumber());
            vm.eventRequestManager().deleteEventRequest(event.request());
            suspend();
        }
	}
	
	public void launch(String main) {
		LaunchingConnector connector = Bootstrap.virtualMachineManager().defaultConnector();
        	Map<String, Argument> arguments = connector.defaultArguments();
        	arguments.get("options").setValue(" -cp D:\\");
        	arguments.get("main").setValue("Debuggee");
        try {
        	vm = connector.launch(arguments);
        	Process proc = vm.process();
        	new Redirection(proc.getErrorStream(), System.err).start();
        	new Redirection(proc.getInputStream(), System.out).start();
			handleEvents();
		} catch (IOException | IllegalConnectorArgumentsException | VMStartException e) {
			e.printStackTrace();
		}
	}
	
}
