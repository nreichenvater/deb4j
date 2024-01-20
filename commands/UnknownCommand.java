package jku.win.systemsoftware.simpledebugger.commands;

import jku.win.systemsoftware.simpledebugger.Debugger;

public class UnknownCommand implements Command {

	@Override
	public void execute(Debugger debugger) {
		System.out.println("Unknown Command...");
	}

}
