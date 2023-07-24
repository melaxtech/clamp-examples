package edu.uth.clamp.support.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uth.clamp.nlp.structure.DocProcessor;
import org.apache.uima.UIMAException;

import edu.uth.clamp.config.ConfigurationException;
import edu.uth.clamp.config.NLPProcessorConf;
import edu.uth.clamp.io.DocumentIOException;
import edu.uth.clamp.nlp.structure.Document;

public class DictAssertionPipeline {
	
	public static void main( String[] argv ) throws IOException, DocumentIOException, ConfigurationException, UIMAException {
		String basedir = "_Absolute_Path_to_Clamp_Components/";
		DocProcessor proc1 = (DocProcessor) NLPProcessorConf.fromFile( new File( basedir + "./Sentence detector/DF_Clamp_sentence_detector/config.conf" ) ).createDocProc();
		DocProcessor proc2 = (DocProcessor) NLPProcessorConf.fromFile( new File( basedir + "./Tokenizer/DF_Clamp_tokenizer/config.conf" ) ).createDocProc();
		DocProcessor proc3 = (DocProcessor) NLPProcessorConf.fromFile( new File( basedir + "./POS tagger/DF_OpenNLP_POS_tagger/config.conf" ) ).createDocProc();
		DocProcessor proc4 = (DocProcessor) NLPProcessorConf.fromFile( new File( basedir + "./Named entity recogizer/DF_Dictionary_lookup/config.conf" ) ).createDocProc();
		DocProcessor proc5 = (DocProcessor) NLPProcessorConf.fromFile( new File( basedir + "./Assertion classifier/DF_NegEx_assertion/config.conf" ) ).createDocProc();

		List<DocProcessor> pipeline = new ArrayList<DocProcessor>();
		pipeline.add( proc1 );
		pipeline.add( proc2 );
		pipeline.add( proc3 );
		pipeline.add( proc4 );
		pipeline.add( proc5 );
		
		File indir = new File( "data/input/mtsamples/" );
		for( File file : indir.listFiles() ) {
			Document doc = new Document( file );
			for( DocProcessor proc : pipeline ) {
				proc.process( doc.getJCas() );
			}
			System.out.println( doc.getFileName() );
		}

		return;
	}

}
