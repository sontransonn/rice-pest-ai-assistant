package com.example.frontend.model;
import java.util.List;

public class Prediction {
    public String label;
    public float confidence;
    public List<Integer> bbox;
}