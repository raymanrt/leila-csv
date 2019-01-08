package com.github.raymanrt.leila.csv;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;


public class CsvFormatter {

    private String[] columns;
    private CSVPrinter printer = null;

    private Map<String, Function<String, Object>> parsers = ImmutableMap.of(

            "javatimestamp", timestamp -> Instant.ofEpochMilli(Long.parseLong(timestamp))
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    );

    private Map<String, Function<String, Object>> columnToParser;

    public CsvFormatter(String[] args) {

        String columnsFromArgs = args[0];
        columns = split(columnsFromArgs, ",");

        columnToParser = new HashMap<>();
        for(int i = 0; i < columns.length; i++) {
            if(columns[i].contains(":")) {
                String column = substringBefore(columns[i], ":");
                String parser = substringAfter(columns[i], ":");

                columns[i] = column;

                if(parsers.containsKey(parser)) {
                    columnToParser.put(column, parsers.get(parser));
                }
            }
        }

        System.out.println(":: [leila-csv] requested columns: " + columns);

        try {
            printer = new CSVPrinter(System.out, CSVFormat.EXCEL.withHeader(columns));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String format(final Document doc) {

        if(printer == null) {
            return "";
        }

        try {
            Object[] cells = new Object[columns.length];

            for(int i = 0 ; i < columns.length; i ++) {
                String column = columns[i];
                String[] values = doc.getValues(column);

                cells[i] = parseMultivalue(column, values);
            }

            printer.printRecord(cells);
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return "";

    }

    private Object parseMultivalue(String column, String[] values) {

        try {
            if(values == null || values.length == 0) {
                return "";
            }

            if(values.length == 1) {
                return parse(column, values[0]);
            }

            Object[] parsedValues = new Object[values.length];

            for(int i = 0; i < values.length; i ++) {
                parsedValues[i] = parse(column, values[i]);
            }

            return StringUtils.join(parsedValues, ";");
        } catch(Exception ex) {
            ex.printStackTrace();

            System.out.println(":: problems parsing :: " + StringUtils.join(values, ", "));

            return "";
        }
    }

    private Object parse(String column, String value) {

//        System.out.println(":: debug:: parsing value:: " + value);

        if(columnToParser.containsKey(column)) {
            return columnToParser.get(column).apply(value);
        }

        if(StringUtils.isEmpty(value)) {
            return "";
        }

        Boolean parsedBoolean = parseBoolean(value);
        if(parsedBoolean != null) {

//            System.out.println(":: debug:: parsed boolean:: " + parsedBoolean);
            return parsedBoolean;
        }

        Number parsedNumber = parseNumber(value);
        if(parsedNumber != null) {
//            System.out.println(":: debug:: parsed number:: " + parsedNumber);
            return parsedNumber;
        }

        return value;
    }

    private Boolean parseBoolean(String value) {

        if(value == null) {
            return null;
        }

        if(value.toLowerCase().equals("true")) {
            return true;
        }

        if(value.toLowerCase().equals("false")) {
            return false;
        }

        return null;
    }

    private Number parseNumber(String value) {
        try {
            if(NumberUtils.isNumber(value)) {
                return NumberUtils.createNumber(value);
            }
        } catch(NumberFormatException ex) {
            return null;
        }

        return null;
    }
}