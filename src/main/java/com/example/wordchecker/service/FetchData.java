package com.example.wordchecker.service;

import com.example.wordchecker.config.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchData {
    private final static Logger logger = LoggerFactory.getLogger(FetchData.class);
    private static final String SQL_QUERY = "select word from nouns_morf";

    public static List<String> fetchData(Map<Integer, String> knownLetterPositions, String knownLetters, String unknownLetters) {
        List<String> nounsMorfs = new ArrayList<>();
        Statement statement = getPreparedString(knownLetterPositions, knownLetters, unknownLetters);
        logger.info("query: {}", statement.query);
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(statement.query)) {
            for (int i = 0; i < statement.values.size(); i++) {
                pst.setObject(i + 1, statement.values.get(i));
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    nounsMorfs.add(rs.getString("word"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return nounsMorfs;
    }

    private static Statement getPreparedString(Map<Integer, String> knownLetterPositions, String knownLetters, String unknownLetters) {
        List<String> results = new ArrayList<>();
        results.add(SQL_QUERY);
        if (knownLetterPositions.size() > 0 || knownLetters.length() > 0 || unknownLetters.length() > 0) {
            results.add(" where");
        }
        List<Object> values = new ArrayList<>();
        for (Map.Entry<Integer, String> value : knownLetterPositions.entrySet()) {
            results.addAll(addCondition(values.size(), " substr(word, ?, 1) = ?"));
            values.add(value.getKey() + 1);
            values.add(value.getValue());
        }
        for (char c : knownLetters.toCharArray()) {
            results.addAll(addCondition(values.size(), " instr(word, ?) <> 0"));
            values.add(c);
        }
        for (char c : unknownLetters.toCharArray()) {
            results.addAll(addCondition(values.size(), " instr(word, ?) = 0"));
            values.add(c);
        }
        return new Statement(String.join("", results), values);
    }

    private static List<String> addCondition(int size, String s) {
        if (size == 0) {
            return List.of(s);
        }
        return List.of(" and", s);
    }

    private static class Statement {
        private final String query;
        private final List<Object> values;

        public Statement(String query, List<Object> values) {
            this.query = query;
            this.values = values;
        }
    }
}
