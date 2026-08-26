public class Automovil extends Vehiculo {

    private int numPuertas;
    private boolean esElectrico;

    public Automovil(String marca, String modelo, int anio, double velocidadMax, int numPuertas, boolean esElectrico) {
        super(marca, modelo, anio, velocidadMax);
        this.numPuertas = numPuertas;
        this.esElectrico = esElectrico;
    }

    public int getNumPuertas() {
        return numPuertas;
    }

    public boolean isEsElectrico() {
        return esElectrico;
    }

    public void setNumPuertas(int numPuertas) {
        if (numPuertas >= 2 && numPuertas <= 6) {
            this.numPuertas = numPuertas;
        }
    }

    public void setEsElectrico(boolean esElectrico) {
        this.esElectrico = esElectrico;
    }

    @Override
    public String toString() {
        return "Automovil{" +
                "marca='" + getMarca() + '\'' +
                ", modelo='" + getModelo() + '\'' +
                ", anio=" + getAnio() +
                ", velocidadMax=" + getVelocidadMax() +
                ", numPuertas=" + numPuertas +
                ", esElectrico=" + esElectrico +
                '}';
    }
}