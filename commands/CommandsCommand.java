package jku.win.systemsoftware.simpledebugger.commands;

import jku.win.systemsoftware.simpledebugger.Debugger;

public class CommandsCommand implements Command {

	@Override
	public void execute(Debugger debugger) {
		System.out.println("\nPossible Commands:");
		System.out.println("\"list-breakpoints\" to list all set breakpoints");
		System.out.println("\"set-breakpoint\" to set a breakpoint (first argument: Name of Class, second argument: line number)");
		System.out.println("\"delete-breakpoint\" to delete a breakpoint (first argument: Name of Class, second argument: line number)");
		System.out.println("\"run\" to let the virtual machine run to the next breakpoint.");
		System.out.println("\"step-over\" to perform a step-over request.");
		System.out.println("\"step-into\" to perform a step-into request.");
		System.out.println("\"variables\" to list all visible fields of the current stackframe.");
		System.out.println("\"stacktrace\" to print all active methods.");
		System.out.println("\"exit\" to quit.");
	}

}
