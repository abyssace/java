public class Vehiculo {
    private String marca;
    private String modelo;
    private int anio;
    protected double velocidadMax;

    public Vehiculo(String marca, String modelo, int anio, double velocidadMax) {
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.velocidadMax = velocidadMax;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getAnio() {
        return anio;
    }

    public double getVelocidadMax() {
        return velocidadMax;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setAnio(int anio) {
        if (anio >= 1886 && anio <= 2026) {
            this.anio = anio;
        } else {
            System.out.println("Año inválido. Debe estar entre 1886 y 2026. No se aplicó el cambio.");
        }
    }

    protected void setVelocidadMax(double velocidadMax) {
        if (velocidadMax > 0) {
            this.velocidadMax = velocidadMax;
        } else {
            System.out.println("Velocidad inválida. Debe ser mayor a 0. No se aplicó el cambio.");
        }
    }

    public void describir() {
        System.out.println("Marca: " + marca);
        System.out.println("Modelo: " + modelo);
        System.out.println("Año: " + anio);
        System.out.println("Velocidad Maxima: " + velocidadMax + " km/h");
    }

    @Override
    public String toString() {
        return "Vehiculo{" +
                "marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", anio=" + anio +
                ", velocidadMax=" + velocidadMax +
                '}';
    }
}