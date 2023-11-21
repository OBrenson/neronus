package com.boi.neronus;

import java.util.List;
import java.util.Map;

public class ModelConfig {

    private int perceptronSize;

    public int getPerceptronSize() {
        return perceptronSize;
    }

    public void setPerceptronSize(int perceptronSize) {
        this.perceptronSize = perceptronSize;
    }

    private int fuzzySize;

    public int getFuzzySize() {
        return fuzzySize;
    }

    public void setFuzzySize(int fuzzySize) {
        this.fuzzySize = fuzzySize;
    }

    private double[][] data;

    private int testSize;

    private int trainSize;

    private double border;

    private double difBorder;

    private double alpha;

    private double nu;

    private int epochs;

    private  Map<String, Double> map;

    private List<String> pam;

    private double normA;

    private double normB;

    public double getNormA() {
        return normA;
    }

    public void setNormA(double normA) {
        this.normA = normA;
    }

    public double getNormB() {
        return normB;
    }

    public void setNormB(double normB) {
        this.normB = normB;
    }

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public int getTestSize() {
        return testSize;
    }

    public void setTestSize(int testSize) {
        this.testSize = testSize;
    }

    public int getTrainSize() {
        return trainSize;
    }

    public void setTrainSize(int trainSize) {
        this.trainSize = trainSize;
    }

    public double getBorder() {
        return border;
    }

    public void setBorder(double border) {
        this.border = border;
    }

    public double getDifBorder() {
        return difBorder;
    }

    public void setDifBorder(double difBorder) {
        this.difBorder = difBorder;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getNu() {
        return nu;
    }

    public void setNu(double nu) {
        this.nu = nu;
    }

    public Map<String, Double> getMap() {
        return map;
    }

    public void setMap(Map<String, Double> map) {
        this.map = map;
    }

    public List<String> getPam() {
        return pam;
    }

    public void setPam(List<String> pam) {
        this.pam = pam;
    }

    public int getEpochs() {
        return epochs;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }
}
