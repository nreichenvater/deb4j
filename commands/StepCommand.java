package jku.win.systemsoftware.simpledebugger.commands;

import java.util.Optional;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

import jku.win.systemsoftware.simpledebugger.Debugger;

public class StepCommand implements Command {
	
	private int stepRequestType;
	
	public StepCommand(int stepRequestType) {
		this.stepRequestType = stepRequestType;
	}

	@Override
	public void execute(Debugger debugger) {
		Optional<ThreadReference> tROpt = debugger.getVM().allThreads().stream().filter(ref -> ref.name().equals("main")).findAny();
		if(tROpt.isPresent()) {
			ThreadReference reference = tROpt.get();
			EventRequestManager erm = debugger.getVM().eventRequestManager();
			StepRequest request = null;
			if(stepRequestType == StepRequest.STEP_INTO) {
				request = erm.createStepRequest(reference, StepRequest.STEP_LINE, StepRequest.STEP_INTO);
				request.addCountFilter(1);
	            request.enable();
			} else if(stepRequestType == StepRequest.STEP_OVER) {
				request = erm.createStepRequest(reference, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
				request.addCountFilter(1);
	            request.enable();
			}
			if(request != null) {
				debugger.resume();
			}
		} else {
			System.out.println("no thread reference found for thread main");
		}
	}

}
