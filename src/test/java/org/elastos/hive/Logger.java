package org.elastos.hive;

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
		System.out.println("\033[1;32m" + "=====>" + tag + "  start" + "\033[0m");
	}

	public static void controllerEnd(String tag) {
		System.out.println("\033[1;32m" + "=====>" + tag + "  end" + "\033[0m");
	}
}
