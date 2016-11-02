/* Copyright (C) 2015-2016  Egon Willighagen <egon.willighagen@gmail.com>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nanopub.MalformedNanopubException;
import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.nanopub.NanopubUtils;
import org.nanopub.trusty.MakeTrustyNanopub;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;

import net.trustyuri.TrustyUriException;

public class NanoPubs {

	private static final URI pavCreatedBy = new URIImpl("http://purl.org/pav/createdBy");

	protected static void createNanoPublications(String topic)
			throws Exception, RepositoryException, MalformedNanopubException, TrustyUriException, RDFHandlerException {
		SailRepository data = OPSWPRDFFiles.loadData();
		OPSWPRDFFiles.createNanopublications(data, "constructs/" + topic + ".insert");
		SailRepositoryConnection conn = data.getConnection();
		RepositoryResult<Resource> result = conn.getContextIDs();
		StringBuffer buffer = new StringBuffer();
		while (result.hasNext()) {
			Resource graph = result.next();
			RepositoryResult<Statement> r = conn.getStatements(null, RDF.TYPE, Nanopub.NANOPUB_TYPE_URI, false, graph);
			if (!r.hasNext()) continue;
			Resource nanopubId = r.next().getSubject();
			if (!(nanopubId instanceof URI)) {
				continue;
			}
			List<String> prefixes = new ArrayList<String>();
			prefixes.add("has-source");
			prefixes.add("wp");
			prefixes.add("xsd");
			prefixes.add("dcterms");
			prefixes.add("np");
			prefixes.add("pav");
			Map<String,String> namespaces = new HashMap<String, String>();
			namespaces.put("has-source", "http://semanticscience.org/resource/SIO_000253");
			namespaces.put("wp", "http://vocabularies.wikipathways.org/wp#");
			namespaces.put("xsd", "http://www.w3.org/2001/XMLSchema#");
			namespaces.put("dcterms", "http://purl.org/dc/terms/");
			namespaces.put("np", "http://www.nanopub.org/nschema#");
			namespaces.put("pav", "http://purl.org/pav/");
			Nanopub nanopub = new NanopubImpl(data, (URI)nanopubId, prefixes, namespaces);
			conn.add(
				new StatementImpl(nanopub.getPubinfoUri(), pavCreatedBy, 
					new URIImpl("https://jenkins.bigcat.unimaas.nl/job/WikiPathways%20Nanopublications/")
				),
				nanopub.getPubinfoUri()
			);
			nanopub = MakeTrustyNanopub.transform(new NanopubImpl(data, (URI)nanopubId, prefixes, namespaces));
			buffer.append(NanopubUtils.writeToString(nanopub, RDFFormat.TRIG)).append("\n\n");
		}
		conn.close();
		ResourceHelper.saveToFile(topic + ".trig", buffer.toString());
	}
}
