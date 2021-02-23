package edu.uth.clamp.support;

import edu.uth.clamp.config.ConfigUtil;
import edu.uth.clamp.config.ConfigurationException;
import edu.uth.clamp.io.DocumentIOException;
import edu.uth.clamp.nlp.structure.ClampNameEntity;
import edu.uth.clamp.nlp.structure.Document;
import edu.uth.clamp.nlp.uima.DocProcessor;
import org.apache.uima.UIMAException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DemoEntityRecognizer {

	public static void main(String[] argv) throws DocumentIOException, ConfigurationException, IOException, UIMAException {
		// loading pipeline from jar file;
		List<DocProcessor> pipeline = ConfigUtil.importPipelineFromJar(new File("pipeline/demo-pipeline.jar"));

		System.out.println("In this pipeline " + pipeline.size() + " components:");
		// listing all components;
		for (DocProcessor proc : pipeline) {
			System.out.println("\t-" + proc.getDesc());
		}
		File inputDir = new File("data/input/mtsamples/");
		String outputDir = "data/output/xmi/";
		for (File file : inputDir.listFiles()) {
			System.out.println("Processing file: " + file.getName());
			// accepting raw text files as an input
			if (!file.isFile()
					|| file.getName().startsWith(".")
					|| !file.getName().endsWith(".txt")) {
				continue;
			}
			Document doc = new Document(file);
			// processing document by loaded pipeline;
			for ( DocProcessor proc : pipeline ) {
				proc.process( doc );
			}
			// listing all entities recognized by demo pipeline;
			List<ClampNameEntity> entities = doc.getNameEntity();
			for (ClampNameEntity ent : entities) {
				System.out.printf("Entity begin character number: %s, End character: %s, Semantic tag: %s, " +
						"Entity text: %s%n", ent.getBegin(), ent.getEnd(), ent.getSemanticTag(), ent.textStr());
			}
			// saving processed document in xmi format;
			doc.save(outputDir + File.separator + file.getName().replace(".txt", ".xmi"));
		}
	}
}
