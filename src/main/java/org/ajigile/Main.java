package org.ajigile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static final Pattern commaPtrn = Pattern.compile("\\s*,\\s*");

    public static void main(String[] args) throws Exception{

        if(args.length < 3){
            String message = "\nThis application requires 3 arguments";
            message += "\n1st argument: startDate (format with double quotes: \"DD/MM/YYYY HH24:mm:ss\")";
            message += "\n2nd argument: endDate (format with double quotes: \"DD/MM/YYYY HH24:mm:ss\")";
            message += "\n3rd argument: merchant (format with double quotes: \"merchant name\")";
            message += "\n\nExample: \njava -jar test-hoolah.jar \"20/08/2018 12:00:00\" \"20/08/2018 13:00:00\" \"Kwik-E-Mart\"\n";
            System.out.println(message);
            System.exit(1);
        }

        Date startDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(args[0]);
        Date endDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(args[1]);
        String merchant = args[2];


        Configuration config = new Configuration();
        config.loadConfiguration();
        //System.out.println(config.toString());


        Map<String, String> reversalTransactionMap = new HashMap<>();
        Map<String, List<String>> dataMap = new HashMap<>();

        Path dataPath = Paths.get(config.getProperty(Constants.DATA_FILE_PATH));
        if(Files.notExists(dataPath)) {
            System.out.println("Data file is not available.");
            System.exit(1);
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_PATTERN);

        Predicate<List<String>> logicFilter = data -> {
            Date dataDate = null;
            try {
                dataDate = sdf.parse(data.get(Constants.DATE));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(dataDate != null && (dataDate.after(startDate) &&
                    (dataDate.before(endDate)) || (dataDate.equals(startDate) || dataDate.equals(endDate))) ) {
                return true;
            }

            // invalid date format will be ignored
            return false;
        };

        AtomicInteger rowIdx = new AtomicInteger(0);
        Files.lines(dataPath)
                .map(l-> Arrays.asList(commaPtrn.split(l)))
                .filter(d->rowIdx.getAndAdd(1)>0) // filter header
                .filter(d->d.get(Constants.MERCHANT).equals(merchant))
                .peek(d->{
                    if(d.get(Constants.TYPE).equalsIgnoreCase("REVERSAL")){
                        reversalTransactionMap.put(d.get(Constants.ID), d.get(Constants.RELATED_TRX));
                    }
                })
                .filter(logicFilter)
                .forEach(d->dataMap.put(d.get(Constants.ID), d));

        //dataMap.keySet().forEach(k->System.out.println(dataMap.get(k).toString()));

        // remove transction that part of REVERSAL transaction
        List<String> excludeTrxIds = dataMap.keySet().stream().filter(k->reversalTransactionMap.containsValue(k)).collect(Collectors.toList());
        excludeTrxIds.forEach(k->dataMap.remove(k));

        DoubleSummaryStatistics stats =
                dataMap.values().stream()
                        .filter(d->!reversalTransactionMap.containsValue(d.get(Constants.ID)))
                        .mapToDouble(d->Double.parseDouble(d.get(Constants.AMOUNT)))
                        .summaryStatistics();

        // print out report
        System.out.println("\n");
        System.out.println(String.format("Number of transactions = %d", stats.getCount()));
        System.out.println(String.format("Average Transaction Value = %s", new DecimalFormat("#0.00").format(stats.getAverage())));



        try(BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv"));) {
            bw.write("num_of_trx, average"); bw.newLine();
            bw.write(String.format("%d, %s", stats.getCount(), new DecimalFormat("#0.00").format(stats.getAverage())));
        }

    }
}
