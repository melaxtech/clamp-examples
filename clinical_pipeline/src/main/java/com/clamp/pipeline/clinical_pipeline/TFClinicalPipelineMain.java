package com.clamp.pipeline.clinical_pipeline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.melax.service.util.BratFileJson;
import com.melax.service.util.BratSemJson;

import edu.uth.clamp.config.ConfigurationException;
import edu.uth.clamp.io.DocumentIOException;
import edu.uth.clamp.nlp.structure.ClampNameEntity;
import edu.uth.clamp.nlp.structure.ClampRelation;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.structure.Document;
import edu.uth.clamp.nlp.structure.XmiUtil;
import edu.uth.clamp.nlp.typesystem.ClampNameEntityUIMA;
import edu.uth.clamp.nlp.typesystem.ClampRelationUIMA;


public class TFClinicalPipelineMain {

    List<DocProcessor> processors = null;

    public TFClinicalPipelineMain() throws SecurityException, IOException, DocumentIOException, ConfigurationException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream("application_local.properties")) {
            properties.load(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TFClinicalPipeline pipeline = new TFClinicalPipeline();
        pipeline.init(properties);
        processors = pipeline.getProcessors();
    }

    public void runXMI(File xmifile) throws UIMAException, IOException, DocumentIOException {
        Document xmidoc = new Document(xmifile);
        // run pipeline on a new document;
        JCas ajcas = XmiUtil.createJCas();
        ajcas.setDocumentText(xmidoc.getFileContent());
        Document doc = new Document(ajcas);
        for (DocProcessor proc : processors) {
            proc.process(doc);
        }
    }

    public Document run(File infile) throws UIMAException, IOException, DocumentIOException {
        Document doc = new Document(infile);
        for (DocProcessor proc : processors) {
            proc.process(doc);
        }
        return doc;
    }

    public void runXMI2(File xmifile, File outfile) throws UIMAException, IOException, DocumentIOException {
        Document xmidoc = new Document(xmifile);
        // run pipeline on a new document;
        JCas ajcas = XmiUtil.createJCas();
        ajcas.setDocumentText(xmidoc.getFileContent());
        Document doc = new Document(ajcas);
        for (DocProcessor proc : processors) {
            proc.process(doc);
        }

        // copy pipeline output to the original xmi document;
        Map<Annotation, Annotation> entityMap = new HashMap<>();
        for (ClampNameEntity cne : doc.getNameEntity()) {
            ClampNameEntity cne2 = new ClampNameEntity(xmidoc.getJCas(), cne.getBegin(), cne.getEnd(), cne.getSemanticTag());
            cne2.setAssertion(cne.getAssertion());
            cne2.setAttr1(cne.getAttr1());
            cne2.setAttr2(cne.getAttr2());
            cne2.setAttr3(cne.getAttr3());
            cne2.setAttr4(cne.getAttr4());
            cne2.setUmlsCui(cne.getUmlsCui());
            cne2.setUmlsCuiDesc(cne.getUmlsCuiDesc());
            cne2.addToIndexes();
            entityMap.put(cne.getUimaEnt(), cne2.getUimaEnt());
        }
        for (ClampRelation rel : doc.getRelations()) {
            ClampRelationUIMA rel2 = new ClampRelationUIMA(xmidoc.getJCas());
            rel2.setSemanticTag(rel.getSemanticTag());
            rel2.setEntFrom((ClampNameEntityUIMA) entityMap.get(rel.getEntFrom().getUimaEnt()));
            rel2.setEntTo((ClampNameEntityUIMA) entityMap.get(rel.getEntTo().getUimaEnt()));
            rel2.addToIndexes();
        }

        // save after merging;
        xmidoc.save(outfile.getAbsolutePath());
    }

    public List<String> getSem(Document doc) {
        BratSemJson semJson = new BratSemJson();
        BratFileJson fileJson = new BratFileJson();
        fileJson.setContent(doc.getFileContent());

        Set<String> semSet = new HashSet<String>();
        for (ClampNameEntity cne : doc.getNameEntity()) {
            if (!semSet.contains(cne.getSemanticTag())) {
                semJson.addEntity(cne.getSemanticTag(), new Vector<String>(
                        Arrays.asList(cne.getSemanticTag())), HtmlColor
                        .getCorlor(semSet.size()));
                semSet.add(cne.getSemanticTag());
            }
            fileJson.addEntity(cne.getBegin(), cne.getEnd(), cne.textStr(),
                    cne.getSemanticTag());
        }

        for (ClampRelation relation : doc.getRelations()) {
            String name = relation.getSemanticTag();
            ClampNameEntity fromEnt = relation.getEntFrom();
            ClampNameEntity toEnt = relation.getEntTo();

            String fromId = fileJson.getEntity(fromEnt.getBegin(),
                    fromEnt.getEnd(), fromEnt.textStr(),
                    fromEnt.getSemanticTag());
            String toId = fileJson.getEntity(toEnt.getBegin(), toEnt.getEnd(),
                    toEnt.textStr(), toEnt.getSemanticTag());

            if (fromId == null || toId == null) {
                continue;
            }
            if (fileJson.getRelation(fromId, toId, name) == null) {
                fileJson.addRelation(fromId, toId, name);

                Vector<String> alias = new Vector<>();
                alias.add(name);
                semJson.addRelation(name, alias, fromEnt.getSemanticTag(),
                        toEnt.getSemanticTag());
            }
        }

        return Arrays.asList(fileJson.getJson(null),
                semJson.getJson(null, null));
    }

    public static void runDir(String inDir, String outDir) throws DocumentIOException, IOException, UIMAException, SecurityException, ConfigurationException {
        TFClinicalPipelineMain pipeline = new TFClinicalPipelineMain();
        for (File file : new File(inDir).listFiles()) {
            Document doc = pipeline.run(file);
            doc.save(outDir + file.getName().replace(".txt", ".xmi"));
        }
    }

    public static void main(String[] argv) throws UIMAException, IOException, DocumentIOException, SecurityException, ConfigurationException {
        File textFile = new File("/Users/linbin1973/ClampMac_1.6.6/workspace/MyPipeline/clinical/Data/Input/100006.txt");
        TFClinicalPipelineMain pipeline = new TFClinicalPipelineMain();
        Document doc = pipeline.run(textFile);
        doc.save("/Users/linbin1973/ClampMac_1.6.6/workspace/MyPipeline/clinical/Data/Output/100006.xmi");

//        runDir("/Users/garyunderwood/melax/repos/imo/input/", "/Users/garyunderwood/melax/ClampMac_1.6.6/workspace/MyPipeline/Viewer/Data/OutputImo/");
    }
}
