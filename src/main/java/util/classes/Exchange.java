package util.classes;

public class Exchange {
    int id;
    Currency baseCurrency;
    Currency targetCurrency;
    double rate;

    public Exchange(int id, Currency baseCurrency, Currency targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = (double) Math.round(rate * 100) / 100;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = (double) Math.round(rate * 100) / 100;
    }

    public void swap() {
        Currency temp = baseCurrency;
        baseCurrency = targetCurrency;
        targetCurrency = temp;
    }

    public void reverseRate() {
        rate = 1/rate;
    }
}
