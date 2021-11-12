package eu.xenit.contentcloud.scribe.generator.source.lombok;

public class LombokSupport implements LombokUsage, LombokFeatures {

    private boolean getters;
    private boolean setters;

    @Override
    public LombokUsage none() {
        this.getters = false;
        this.setters = false;
        return this;
    }

    @Override
    public LombokUsage getters() {
        this.getters = true;
        return this;
    }

    @Override
    public LombokUsage setters() {
        this.setters = true;
        return this;
    }

    @Override
    public boolean hasGetter() {
        return this.getters;
    }

    @Override
    public boolean hasSetter() {
        return this.setters;
    }
}
