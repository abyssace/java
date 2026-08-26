public class Barco extends Vehiculo {

    private String tipoCasco;
    private int tonelajeMax;
    private int numTripulantes;
    private double eslora;

    public Barco(String marca, String modelo, int anio, double velocidadMax,
                 String tipoCasco, int tonelajeMax, int numTripulantes, double eslora) {
        super(marca, modelo, anio, velocidadMax);
        this.tipoCasco = tipoCasco;
        this.tonelajeMax = tonelajeMax;
        this.numTripulantes = numTripulantes;
        this.eslora = eslora;
    }

    public String getTipoCasco() {
        return tipoCasco;
    }

    public int getTonelajeMax() {
        return tonelajeMax;
    }

    public int getNumTripulantes() {
        return numTripulantes;
    }

    public double getEslora() {
        return eslora;
    }

    public void setTipoCasco(String tipoCasco) {
        this.tipoCasco = tipoCasco;
    }

    public void setTonelajeMax(int tonelajeMax) {
        if (tonelajeMax > 0) {
            this.tonelajeMax = tonelajeMax;
        }
    }

    public void setNumTripulantes(int numTripulantes) {
        if (numTripulantes > 0) {
            this.numTripulantes = numTripulantes;
        }
    }

    public void setEslora(double eslora) {
        if (eslora > 0) {
            this.eslora = eslora;
        }
    }

    @Override
    public String toString() {
        return "Barco{" +
                "marca='" + getMarca() + '\'' +
                ", modelo='" + getModelo() + '\'' +
                ", anio=" + getAnio() +
                ", velocidadMax=" + getVelocidadMax() +
                ", tipoCasco='" + tipoCasco + '\'' +
                ", tonelajeMax=" + tonelajeMax +
                ", numTripulantes=" + numTripulantes +
                ", eslora=" + eslora +
                '}';
    }
}