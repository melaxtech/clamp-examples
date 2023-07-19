package edu.uth.clamp.support.processor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.uth.clamp.config.NLPProcessorConf;
import edu.uth.clamp.config.ProcessorOptions;
import edu.uth.clamp.io.DocumentIOException;
import edu.uth.clamp.util.ClampConstants;

public class StopwordsProcessorConf extends NLPProcessorConf {
	// component info;
	public static final String compName = ClampConstants.UserDefinedComp;
	public String procName = "dict based match stopwords by list";
	public static final String description = "match stopwords by a pre-defined list";
	public static final Set<String> requiredComp = new HashSet<String>(
			Arrays.asList( ClampConstants.SentDetector
					, ClampConstants.Tokenizer )
				);

	public Object create() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public ProcessorOptions getOptions() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object createDocProc() throws IOException, DocumentIOException {
		return new StopwordsProcessor();
	}
	

}
