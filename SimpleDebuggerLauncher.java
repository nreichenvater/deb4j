package jku.win.systemsoftware.simpledebugger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.jdi.request.StepRequest;

import jku.win.systemsoftware.simpledebugger.commands.DeleteBreakpointCommand;
import jku.win.systemsoftware.simpledebugger.commands.ExitCommand;
import jku.win.systemsoftware.simpledebugger.commands.Command;
import jku.win.systemsoftware.simpledebugger.commands.CommandsCommand;
import jku.win.systemsoftware.simpledebugger.commands.ListBreakpointsCommand;
import jku.win.systemsoftware.simpledebugger.commands.RunCommand;
import jku.win.systemsoftware.simpledebugger.commands.SetBreakpointCommand;
import jku.win.systemsoftware.simpledebugger.commands.StackTraceCommand;
import jku.win.systemsoftware.simpledebugger.commands.StepCommand;
import jku.win.systemsoftware.simpledebugger.commands.UnknownCommand;
import jku.win.systemsoftware.simpledebugger.commands.VariablesCommand;

public class SimpleDebuggerLauncher {
	
	private final BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
	
	private Debugger debugger;
	
	public SimpleDebuggerLauncher(String[] args) {
		String main = Stream.of(args).collect(Collectors.joining(" "));
		debugger = new Debugger(this);
		debugger.launch(main);
	}
	
	public void waitForInput() {
		while(debugger.isSuspended()) {
			try {
				System.out.println("\nPlease type in a command or \"commands\" for all possible commands");
                Command c = parseInput();
                c.execute(debugger);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
		}
	}
	
	private Command parseInput() throws IOException {
		String input = inputReader.readLine();
		String[] arguments = input.split(" ");
		String com = arguments[0];
		switch(com) {
		case "exit":
			return new ExitCommand();
		case "commands":
			return new CommandsCommand();
		case "set-breakpoint":
			return new SetBreakpointCommand(new Breakpoint(arguments[1], Integer.parseInt(arguments[2])));
		case "delete-breakpoint":
			return new DeleteBreakpointCommand(new Breakpoint(arguments[1], Integer.parseInt(arguments[2])));
		case "list-breakpoints":
			return new ListBreakpointsCommand();
		case "run":
			return new RunCommand();
		case "step-over":
			return new StepCommand(StepRequest.STEP_OVER);
		case "step-into":
			return new StepCommand(StepRequest.STEP_INTO);
		case "variables":
			return new VariablesCommand();
		case "stacktrace":
			return new StackTraceCommand();
		default:
			return new UnknownCommand();
		}
	}
	
    public static void main(String[] args) {
        new SimpleDebuggerLauncher(args);
    }
}
