# WikiPathways Nanopublications

Project to convert content of WPRDF into nanopublications (http://nanopub.org/).

## Running

The code contains a set of standalone programs which can be used to generate
a particular type of nanopublication. For example, to create nanopublications for
interactions, you can use a call like this:

    java -DOPSWPRDF=wprdfFiles/ -DSUBSETPREFIX=wp1 -Xms2G -Xmx8G -server \
        -cp target/wikipathways.nanopubs-1-SNAPSHOT.jar \
        nl.unimaas.bigcat.wikipathways.nanopubs.InteractionPubs

The OPSWPRDF parameter is to point the Turtle files for the pathways and the
SUBSETPREFIX parameter is to indicate which pathways should be converted. For
example, to make publications for all pathways, use SUSETPREFIX=wp.
