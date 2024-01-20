package jku.win.systemsoftware.simpledebugger.commands;

import jku.win.systemsoftware.simpledebugger.Debugger;

public interface Command {
	public void execute(Debugger debugger);
}
