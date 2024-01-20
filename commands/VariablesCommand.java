package jku.win.systemsoftware.simpledebugger.commands;

import java.util.Optional;
import java.util.stream.Collectors;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

import jku.win.systemsoftware.simpledebugger.Debugger;

public class VariablesCommand implements Command {

	@Override
	public void execute(Debugger debugger) {
		Optional<ThreadReference> tRefOpt = debugger.getVM().allThreads().stream().filter(ref -> ref.name().equals("main")).findAny();

        if (tRefOpt.isPresent()) {
            try {
                Optional<StackFrame> sFrameOpt = tRefOpt.get().frames().stream().findFirst();
                if (sFrameOpt.isPresent()) {
                    StackFrame frame = sFrameOpt.get();
                    try {
                        frame.visibleVariables().stream().map(var -> getStringOfVariable(var.name(), var.typeName(), frame.getValue(var))).forEach(System.out::println);
                    } catch (AbsentInformationException e) {
                        System.out.print("missing source information/no visible variables");
                    }
                } else {
                    System.out.println("no active stackframe found");
                }
            } catch (IncompatibleThreadStateException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("no thread reference found for thread main");
        }
	}
	
	private String getStringOfVariable(String name, String type, Value value) {
		StringBuilder varStr = new StringBuilder();
		varStr.append(name);
		varStr.append(" (");
		varStr.append(type);
		varStr.append(") = ");
		varStr.append(getValueRepresentation(value));
		return varStr.toString();
	}

	private String getValueRepresentation(Value value) {
		StringBuilder varRepStr = new StringBuilder();
		if(value == null) {
			varRepStr.append("null");
		} else if(value instanceof PrimitiveValue || value instanceof StringReference) {
			varRepStr.append(value);
		} else if(value instanceof ArrayReference) {
			ArrayReference arrRef = (ArrayReference) value;
			varRepStr.append(arrRef.getValues().stream().map(val -> val.toString()).collect(Collectors.joining(", ")));
		} else if(value instanceof ObjectReference) {
			varRepStr.append("{\n");
			ObjectReference objRef = (ObjectReference) value;
			varRepStr.append(objRef.getValues(objRef.referenceType().visibleFields()).entrySet().stream()
					.map(e -> getStringOfVariable(e.getKey().name(), e.getKey().typeName(), e.getValue())).collect(Collectors.joining("\n")));
			varRepStr.append("\n}");
		}
		return varRepStr.toString();
	}
}
