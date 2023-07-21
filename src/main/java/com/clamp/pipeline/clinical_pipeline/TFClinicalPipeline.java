package com.clamp.pipeline.clinical_pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

import com.melax.service.pipeline.DefalutPipeline;
import com.melax.service.pipeline.IPipeline;
import com.melax.typesystem.ClinicalNote;
import com.melax.typesystem.NamedEntity;
import com.melax.typesystem.UmlsConcept;

import edu.uth.clamp.config.ConfigUtil;
import edu.uth.clamp.config.ConfigurationException;
import edu.uth.clamp.io.DocumentIOException;
import edu.uth.clamp.nlp.ast.NegExAssertion;
import edu.uth.clamp.nlp.ast.NegExAssertionOptions;
import edu.uth.clamp.nlp.core.ClampSentDetector;
import edu.uth.clamp.nlp.core.ClampTokenizer;
import edu.uth.clamp.nlp.core.DictBasedSectionHeaderIdf;
import edu.uth.clamp.nlp.core.OpenNLPPosTagger;
import edu.uth.clamp.nlp.core.PosTaggerUIMA;
import edu.uth.clamp.nlp.core.SectionHeaderIdfUIMA;
import edu.uth.clamp.nlp.core.SentDetectorUIMA;
import edu.uth.clamp.nlp.core.TokenizerUIMA;
import edu.uth.clamp.nlp.dict.CascadingDictionaryMatcher;
import edu.uth.clamp.nlp.dict.CascadingDictionaryUIMA;
import edu.uth.clamp.nlp.encoding.RxNormEncoderUIMA;
import edu.uth.clamp.nlp.rule.RutaScript;
import edu.uth.clamp.nlp.structure.ClampNameEntity;
import edu.uth.clamp.nlp.structure.ClampRelation;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.structure.Document;
import edu.uth.clamp.nlp.uima.RutaScriptUIMA;
import edu.uth.clamp.nlp.uima.UmlsEncoderUIMA;
import edu.uth.clamp.nlp.uima.NegExAssertionUIMA;
import ner.CharLSTMNERUIMA;
import relation.CharLSTMREUIMA;

public class TFClinicalPipeline extends DefalutPipeline {

    public TFClinicalPipeline() {
        processors = new ArrayList<>();
    }

    @Override
    public IPipeline init(Properties conf) throws IOException, DocumentIOException, ConfigurationException {
        //build dictionary components
        // Create an InputStream for the file
        String pipelineJarPath = conf.getProperty("service.pipeline.clinical.pipelinejarpath");
        processors = ConfigUtil.importPipelineFromJar( new File(pipelineJarPath));
        for(DocProcessor proc:processors) {
//        	System.out.println(proc.getName());
			if( proc instanceof UmlsEncoderUIMA ) {
				((UmlsEncoderUIMA)proc).setIndexDir( new File( conf.getProperty("service.umlsindex") ) );
			}else if(proc instanceof RxNormEncoderUIMA) {
				((RxNormEncoderUIMA)proc).setIndex(  conf.getProperty("service.rxnormindex") ) ;

			}
        }

        return this;
    }

    @Override
    public String getJsonFromDocument(Document doc) {
        ClinicalNote note = new ClinicalNote(doc.getFileContent());
        for (ClampNameEntity cne : doc.getNameEntity()) {
            NamedEntity entity = note.createEntity(cne.getBegin(), cne.getEnd(), cne.getSemanticTag());
            if (cne.getUmlsCui() != null && !cne.getUmlsCui().isEmpty()) {
                UmlsConcept c = new UmlsConcept();
                c.setCode(cne.getUmlsCui());
                c.setTui("");
                c.setPreferredText(cne.getUmlsCuiDesc());
                entity.addConcept(c);
            }
        }

        for (ClampRelation rel : doc.getRelations()) {
            NamedEntity fromEnt = note.createEntity(rel.getEntFrom().getBegin(), rel.getEntFrom().getEnd(), rel.getEntFrom().getSemanticTag());
            NamedEntity toEnt = note.createEntity(rel.getEntTo().getBegin(), rel.getEntTo().getEnd(), rel.getEntTo().getSemanticTag());
            note.createRelation(fromEnt, toEnt, rel.getSemanticTag());
        }

        return note.toString();
    }
}
