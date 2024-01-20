package jku.win.systemsoftware.simpledebugger.commands;

import jku.win.systemsoftware.simpledebugger.Debugger;

public class ExitCommand implements Command {

	@Override
	public void execute(Debugger debugger) {
		debugger.exit();
	}

}
