package edu.uth.clamp.support.processor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.uth.clamp.nlp.structure.ClampNameEntity;
import edu.uth.clamp.nlp.structure.ClampToken;
import edu.uth.clamp.nlp.structure.XmiUtil;
import edu.uth.clamp.nlp.uima.DocProcessor;

public class StopwordsProcessor extends DocProcessor {
	Set<String> stopwords;
	public StopwordsProcessor() {
		stopwords = new HashSet<String>( Arrays.asList( "is", "are", "in", "on", "of" ) );
	}
	@Override
	public void process( JCas jcas ) throws AnalysisEngineProcessException {
		for( ClampToken token : XmiUtil.selectToken( jcas, 0, jcas.getDocumentText().length() ) ) {
			if( stopwords.contains( token.textStr() ) ) {
				ClampNameEntity cne = new ClampNameEntity( jcas, token.getBegin(), token.getEnd(), "stopwords" );
				cne.addToIndexes();
			}
		}
	}
}
