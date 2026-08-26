public class Avion extends Vehiculo {
    private byte numMotores;
    private int altitudMaxima;
    private String aerolinea;
    private int numPasajeros;

    public Avion(String marca, String modelo, int anio, double velocidadMax, byte numMotores,
                 int altitudMaxima, String aerolinea, int numPasajeros) {
        super(marca, modelo, anio, velocidadMax);
        this.numMotores = numMotores;
        this.altitudMaxima = altitudMaxima;
        this.aerolinea = aerolinea;
        this.numPasajeros = numPasajeros;
    }

    public byte getNumMotores() {
        return numMotores;
    }

    public int getAltitudMaxima() {
        return altitudMaxima;
    }

    public String getAerolinea() {
        return aerolinea;
    }

    public int getNumPasajeros() {
        return numPasajeros;
    }

    public void setNumMotores(byte numMotores) {
        if (numMotores > 0 && numMotores <= 8) {
            this.numMotores = numMotores;
        }
    }

    public void setAltitudMaxima(int altitudMaxima) {
        if (altitudMaxima > 0) {
            this.altitudMaxima = altitudMaxima;
        }
    }

    public void setAerolinea(String aerolinea) {
        this.aerolinea = aerolinea;
    }

    public void setNumPasajeros(int numPasajeros) {
        if (numPasajeros > 0) {
            this.numPasajeros = numPasajeros;
        }
    }

    @Override
    public String toString() {
        return "Avion{" +
                "marca='" + getMarca() + '\'' +
                ", modelo='" + getModelo() + '\'' +
                ", anio=" + getAnio() +
                ", velocidadMax=" + getVelocidadMax() +
                ", numMotores=" + numMotores +
                ", altitudMaxima=" + altitudMaxima +
                ", aerolinea='" + aerolinea + '\'' +
                ", numPasajeros=" + numPasajeros +
                '}';
    }
}