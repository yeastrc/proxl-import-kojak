package org.yeastrc.proxl.xml.kojak.reader;

import org.yeastrc.proxl.xml.kojak.objects.KojakCrosslinker;
import org.yeastrc.proxl.xml.kojak.objects.LinkableEnd;
import org.yeastrc.proxl.xml.kojak.objects.LinkableEnds;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * A class for reading the values from a Kojak conf file. Once a value is
 * queried, it will read the file once and store the values for future
 * reference.
 * 
 * @author mriffle
 *
 */
public class KojakConfReader {

	public KojakConfReader( File kojakConfFile ) throws IOException {
		this.configFile = kojakConfFile;
		this.readFileIntoCache();
	}

	public Map<String, BigDecimal> getStaticMods() throws Exception {

		if( this.staticMods == null ) {
			Map staticMods = new HashMap<>();

			for (String line : this.configFileLines) {

				if (line.startsWith("fixed_modification")) {
					String[] fields = line.split("\\s+");
					if (fields.length != 4) {
						throw new Exception("Did not get three fields on static mod line. Got: " + line);
					}

					staticMods.put(fields[2], new BigDecimal(fields[3]));
				}
			}
			this.staticMods = Collections.unmodifiableMap( staticMods );
		}

		return this.staticMods;
	}

	public Collection<KojakCrosslinker> getCrosslinkers() throws Exception {

		if( this.crossLinkers == null ) {
			Collection<KojakCrosslinker> crosslinkers = new HashSet<>();

			for (String line : this.configFileLines) {

				if (line.startsWith("cross_link")) {
					String[] fields = line.split("\\s+");

					if (fields.length != 6) {
						throw new Exception("Did not get 6 fields for cross-linker def. Got: " + line);
					}
					crosslinkers.add(getCrosslinkerForFields(fields));
				}
			}

			this.crossLinkers = Collections.unmodifiableCollection( crosslinkers );
		}

		return this.crossLinkers;
	}

	private KojakCrosslinker getCrosslinkerForFields( String[] fields ) {

		LinkableEnd linkableEnd1 = getLinkableEnd( fields[ 2 ] );
		LinkableEnd linkableEnd2 = getLinkableEnd( fields[ 3 ] );

		LinkableEnds linkableEnds = new LinkableEnds( linkableEnd1, linkableEnd2 );
		BigDecimal linkerModMass = new BigDecimal( fields[ 5 ] );
		String linkerName = fields[ 6 ];

		return new KojakCrosslinker( linkerName, linkerModMass, linkableEnds );
	}

	private LinkableEnd getLinkableEnd( String kojakLinkerEndDefinition ) {
		boolean nterm = false;
		boolean cterm = false;
		Collection<String> residues = new HashSet<>();

		for (int i = 0; i < kojakLinkerEndDefinition.length(); i++){
			String r = String.valueOf( kojakLinkerEndDefinition.charAt(i) );

			if( r.equals( "n" ) )
				nterm = true;

			else if( r.equals( "c" ) )
				cterm = true;

			else
				residues.add( r );
		}

		return new LinkableEnd( Collections.unmodifiableCollection( residues ), nterm, cterm );
	}


	private void readFileIntoCache() throws IOException {

		this.configFileLines = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(this.configFile))) {

			String line = br.readLine();
			while( line != null ) {

				line = line.replaceAll( "#.*", "" );	// strip out comments
				this.configFileLines.add( line );

				line = br.readLine();
			}
		}
	}

	private File configFile;
	private List<String> configFileLines;
	private Map<String, BigDecimal> staticMods;
	private Collection<KojakCrosslinker> crossLinkers;
}
