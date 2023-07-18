package edu.uth.clamp.support.processor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.uth.clamp.nlp.structure.*;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

public class StopwordsProcessor extends DocProcessor {
	Set<String> stopwords;
	public StopwordsProcessor() {
		stopwords = new HashSet<String>( Arrays.asList( "is", "are", "in", "on", "of" ) );
	}

	@Override
	public int process(Document document) throws AnalysisEngineProcessException {
		for( ClampToken token : document.getTokens() ) {
			if( stopwords.contains( token.textStr() ) ) {
				ClampNameEntity cne = new ClampNameEntity(document.getJCas(), token.getBegin(), token.getEnd(), "stopwords" );
				cne.addToIndexes();
			}
		}
		return 0;
	}

	public void process( JCas jcas ) throws AnalysisEngineProcessException {
		for( ClampToken token : XmiUtil.selectToken( jcas, 0, jcas.getDocumentText().length() ) ) {
			if( stopwords.contains( token.textStr() ) ) {
				ClampNameEntity cne = new ClampNameEntity( jcas, token.getBegin(), token.getEnd(), "stopwords" );
				cne.addToIndexes();
			}
		}
	}
}
