package com.electro.electro_cart.models;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Builder
public class Specification implements Serializable {
    private String cpu;
    private String gpu;
    private String display;
    private String memory;
    private String ram;
    private String os;
    private String battery;
    private String material;
    private String dimensions;
    private String weight;
    @Singular
    private List<String> ports;
    @Singular private List<String> features;

    public Specification() {
    }

    public Specification(String cpu, String gpu, String display, String memory, String ram, String os, String battery, String material, String dimensions, String weight, List<String> ports, List<String> features) {
        this.cpu = cpu;
        this.gpu = gpu;
        this.display = display;
        this.memory = memory;
        this.ram = ram;
        this.os = os;
        this.battery = battery;
        this.material = material;
        this.dimensions = dimensions;
        this.weight = weight;
        this.ports = ports;
        this.features = features;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getGpu() {
        return gpu;
    }

    public void setGpu(String gpu) {
        this.gpu = gpu;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    @Override
    public String toString() {
        return "Specification{" +
                "cpu='" + cpu + '\'' +
                ", gpu='" + gpu + '\'' +
                ", display='" + display + '\'' +
                ", memory='" + memory + '\'' +
                ", ram='" + ram + '\'' +
                ", os='" + os + '\'' +
                ", battery='" + battery + '\'' +
                ", material='" + material + '\'' +
                ", dimensions='" + dimensions + '\'' +
                ", weight='" + weight + '\'' +
                ", ports=" + ports +
                ", features=" + features +
                '}';
    }
}
