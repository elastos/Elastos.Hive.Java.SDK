package org.elastos.hive;

import org.elastos.hive.scripting.Executable;

public class Logger {

	public static void hive() {
		System.out.println("\033[1;35m" + "#################################### Elastos Hive #################################" + "\033[0m");
		System.out.println("\033[1;35m" + "##                 **     **   **   **             **   ************             ##   " + "\033[0m");
		System.out.println("\033[1;35m" + "##                 **     **   **    **           **    **                       ##   " + "\033[0m");
		System.out.println("\033[1;35m" + "##                 **     **   **     **         **     **                       ##    " + "\033[0m");
		System.out.println("\033[1;35m" + "##                 **     **   **      **       **      **                       ##     " + "\033[0m");
		System.out.println("\033[1;35m" + "##                 ** *** **   **       **     **       ** *********             ##      " + "\033[0m");
		System.out.println("\033[1;35m" + "##                 **     **   **        **   **        **                       ##       " + "\033[0m");
		System.out.println("\033[1;35m" + "##                 **     **   **         ** **         **                       ##        " + "\033[0m");
		System.out.println("\033[1;35m" + "##                 **     **   **          **           *************            ##        " + "\033[0m");
		System.out.println("\033[1;35m" + "###################################################################################" + "\033[0m");
	}

	public static void controllerStart(String tag) {
		information("=====>" + tag + "  start");
	}

	public static void controllerEnd(String tag) {
		information("=====>" + tag + "  end");
	}

	public static void error(String error) {
		System.out.println("\033[7;31m" + error + "\033[0m");
	}

	public static void exception(String exception) {
		System.out.println("\033[0;31m" + exception + "\033[0m");
	}

	public static void exception(Exception exception) {
		System.out.println("\033[0;31m" + exception.getLocalizedMessage() + "\033[0m");
	}

	public static void information(String information) {
		System.out.println("\033[0;32m" + information + "\033[0m");
	}

	public static void debug(String message) {
		System.out.println("\033[0;36m" + message + "\033[0m");
	}
}
