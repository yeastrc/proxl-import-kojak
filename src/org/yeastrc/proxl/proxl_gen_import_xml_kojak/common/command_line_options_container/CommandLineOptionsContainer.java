package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.command_line_options_container;

/**
 * Holds command line options entered so they are accessible in the importer
 *
 */
public class CommandLineOptionsContainer {

	private static boolean forceDropKojakDuplicateRecordsOptOnCommandLine;

	public static boolean isForceDropKojakDuplicateRecordsOptOnCommandLine() {
		return forceDropKojakDuplicateRecordsOptOnCommandLine;
	}

	public static void setForceDropKojakDuplicateRecordsOptOnCommandLine(
			boolean forceDropKojakDuplicateRecordsOptOnCommandLine) {
		CommandLineOptionsContainer.forceDropKojakDuplicateRecordsOptOnCommandLine = forceDropKojakDuplicateRecordsOptOnCommandLine;
	}
}
