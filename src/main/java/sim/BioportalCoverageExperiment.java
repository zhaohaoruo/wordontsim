package sim;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by slava on 05/09/17.
 */
public class BioportalCoverageExperiment {

    private static final Logger log = Logger.getLogger(String.valueOf(BioportalCoverageExperiment.class));


    public static void main(String[] args) throws IOException {

        File ontDir = new File(args[0]);
        File analogyFile = new File(args[1]);
        File relatednessFile = new File(args[2]);
        File similarityFile = new File(args[3]);

        log.info("Loading CSV files");
        Set<String> analogyTerms = getAnalogyTerms(analogyFile);
        Set<String> relatednessTerms = getRelatednessTerms(relatednessFile);
        Set<String> similarityTerms = getSimilarityTerms(similarityFile);

        Set<String> allTerms = new HashSet<>(analogyTerms);
        allTerms.addAll(relatednessTerms);
        allTerms.addAll(similarityTerms);

        Map<String, Set<String>> termOntsMap = new HashMap<>();
        for (String term : allTerms) {
            termOntsMap.put(term, new HashSet<>());
        }

        log.info("Checking ontologies");
        int ontCount = 0;
        for (File ontFile : ontDir.listFiles()) {
            log.info("\tLoading " + ++ontCount + " : " + ontFile.getName());
            OntologyLoader loader = new OntologyLoader(ontFile, true);
            ClassFinder finder = new ClassFinder(loader.getOntology());
            int termCount = 0;
            for (String term : termOntsMap.keySet()) {
                Set<String> onts = termOntsMap.get(term);
                if (finder.contains(term)) {
                    onts.add(ontFile.getName());
                }
                if (++termCount % 100 == 0) {
                    log.info("\t\t" + termCount + " terms checked");
                }
            }
        }

        int termsCoveredCount = 0;
        for (String term : termOntsMap.keySet()) {
            Set<String> onts = termOntsMap.get(term);
            if (!onts.isEmpty()) {
                termsCoveredCount++;
            }
        }

        log.info(termsCoveredCount + " / " + allTerms.size() + " terms are found");

    }



    private static Set<String> getAnalogyTerms(File analogyFile) throws IOException {
        Set<String> terms = new HashSet<>();
        CSVReader reader = new CSVReader(new FileReader(analogyFile));
        List<String[]> rows = reader.readAll();
        for (String[] row : rows) {
            for (String value : row) {
                terms.add(value.toLowerCase());
            }
        }
        return terms;
    }

    private static Set<String> getRelatednessTerms(File relatednessFile) throws IOException {
        Set<String> terms = new HashSet<>();
        CSVReader reader = new CSVReader(new FileReader(relatednessFile));
        List<String[]> rows = reader.readAll();
        for (String[] row : rows) {
            terms.add(row[2].toLowerCase());
            terms.add(row[3].toLowerCase());
        }
        return terms;
    }

    private static Set<String> getSimilarityTerms(File similarityFile) throws IOException {
        Set<String> terms = new HashSet<>();
        CSVReader reader = new CSVReader(new FileReader(similarityFile));
        List<String[]> rows = reader.readAll();
        for (String[] row : rows) {
            terms.add(row[2].toLowerCase());
            terms.add(row[3].toLowerCase());
        }
        return terms;
    }





}
