/* Copyright (C) 2013-2016  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the <organization> nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.unimaas.bigcat.wikipathways.nanopubs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

public class OPSWPRDFFiles {

	private static SailRepository loadedData;
	private static boolean locked = false;
	
	public static SailRepository loadData() throws Exception {
		if (loadedData != null) return loadedData;

		while (locked) Thread.sleep(1000);

		if (loadedData != null) return loadedData;

		locked = true;
		
		String folder = "/tmp/doesntexist/";
		if (System.getProperty("OPSWPRDF") != null) {
			folder = System.getProperty("OPSWPRDF");
			folder = folder.replace(".", "/");
			folder = folder.replace("_", " ");
			folder = folder.replace("[", "(");
			folder = folder.replace("]", ")");
		}
		System.out.println("OPSWPRDF folder: " + folder);

		String subsetPrefix = "wp111";
		if (System.getProperty("SUBSETPREFIX") != null) {
			subsetPrefix = System.getProperty("SUBSETPREFIX");
		}
		System.out.println("WP subset: " + subsetPrefix);

		List<File> files = findAllFiles(folder, subsetPrefix);
		String directory = "target/UnitTest" ;
		File tbdFolder = new File(directory);
		tbdFolder.mkdir();

		MemoryStore store = new MemoryStore();
		store.initialize();
		loadedData = new SailRepository(store);
		SailRepositoryConnection conn = loadedData.getConnection();

		for (File file : files) {
			String baseURI = "http://nanopubs.wikipathways.org/";
			conn.add(file, baseURI, RDFFormat.TURTLE);
		}
		conn.close();

		return loadedData;
	}

	public static SailRepository createNanopublications(SailRepository loadedData, String updateSPARQL) throws Exception {
		SailRepositoryConnection conn = loadedData.getConnection();
		String query = ResourceHelper.resourceAsString(updateSPARQL);
		Update update = conn.prepareUpdate(QueryLanguage.SPARQL, query);
		update.execute();
		conn.close();
		return loadedData;
	}

	private static List<File> findAllFiles(String folder, String subsetPrefix) {
		List<File> files = new ArrayList<File>();

		File root = new File(folder);
		File[] list = root.listFiles();
        if (list == null) return Collections.emptyList();

        for ( File file : list ) {
            if ( file.isDirectory() ) {
            	files.addAll(findAllFiles(file.getAbsolutePath(), subsetPrefix));
            } else {
            	String name = file.getName();
            	if (name.toLowerCase().endsWith(".ttl") && name.toLowerCase().startsWith(subsetPrefix)) {
            		if (!testOrTutorial(name)) files.add(file);
            	}
            }
        }
		return files;
	}

	@SuppressWarnings("serial")
	private static final List<String> pathwaysToIgnore = new ArrayList<String>() {{
		add("WP4"); // the edit playground
		add("WP2582"); // the metabolite tests
	}};

	private static boolean testOrTutorial(String filename) {
		for (String pathway : pathwaysToIgnore) {
			if (filename.contains(pathway + "_")) return true;
		}
		return false;
	}

}
