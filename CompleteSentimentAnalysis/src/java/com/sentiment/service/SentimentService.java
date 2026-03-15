package com.sentiment.service;

import java.util.*;

public class SentimentService {
    
    private Map<String, Double> lexicon = new HashMap<>();
    private boolean loaded = false;
    
    public SentimentService() {
        // Initialize with default lexicon
        initDefaultLexicon();
    }
    
    private void initDefaultLexicon() {
        String[] positive = {"good:0.8", "great:0.9", "excellent:1.0", "amazing:0.9", "love:0.8",
            "happy:0.7", "wonderful:0.9", "perfect:1.0", "fantastic:0.9", "awesome:0.9",
            "best:1.0", "nice:0.6", "pleasant:0.7", "enjoy:0.6", "recommend:0.7"};
        
        String[] negative = {"bad:-0.8", "terrible:-1.0", "awful:-1.0", "hate:-0.9", "worst:-1.0",
            "poor:-0.7", "disappointed:-0.6", "horrible:-1.0", "sucks:-0.9", "waste:-0.8",
            "useless:-0.8", "annoying:-0.5", "frustrating:-0.6", "problem:-0.4", "issue:-0.3"};
        
        String[] neutral = {"okay:0.1", "fine:0.2", "normal:0.0", "average:0.0", "regular:0.0"};
        
        for (String s : positive) {
            String[] parts = s.split(":");
            lexicon.put(parts[0], Double.parseDouble(parts[1]));
        }
        for (String s : negative) {
            String[] parts = s.split(":");
            lexicon.put(parts[0], Double.parseDouble(parts[1]));
        }
        for (String s : neutral) {
            String[] parts = s.split(":");
            lexicon.put(parts[0], Double.parseDouble(parts[1]));
        }
        
        loaded = true;
    }
    
    public Map<String, Object> analyze(String text) {
        Map<String, Object> result = new HashMap<>();
        
        String[] words = text.toLowerCase().split("\\s+");
        double totalScore = 0;
        int matchedWords = 0;
        List<String> foundWords = new ArrayList<>();
        
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", "");
            if (lexicon.containsKey(word)) {
                double score = lexicon.get(word);
                totalScore += score;
                matchedWords++;
                foundWords.add(word + "(" + score + ")");
            }
        }
        
        double avgScore = matchedWords > 0 ? totalScore / matchedWords : 0;
        String sentiment;
        
        if (avgScore > 0.1) {
            sentiment = "positive";
        } else if (avgScore < -0.1) {
            sentiment = "negative";
        } else {
            sentiment = "neutral";
        }
        
        double confidence = matchedWords > 0 ?
            Math.min(0.5 + (Math.abs(avgScore) * 0.5), 0.95) : 0.5;
        
        result.put("sentiment", sentiment);
        result.put("score", avgScore);
        result.put("confidence", confidence);
        result.put("matchedWords", matchedWords);
        result.put("foundWords", foundWords);
        
        return result;
    }
    
    public boolean loadLexicon(String filePath) {
        // Implementation for loading custom lexicon files
        return true;
    }
    
    public int getLexiconSize() {
        return lexicon.size();
    }
    
    public boolean isLoaded() {
        return loaded;
    }
}