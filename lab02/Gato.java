public class Gato extends Animal {
    private String color;
    private boolean esInterior;

    public Gato(String nombre, int edad, double peso, String color, boolean esInterior) {
        super(nombre, edad, peso);
        this.color = color;
        this.esInterior = esInterior;
    }

    public void maullar() {
        System.out.println(getNombre() + " dice: miau");
    }

    public void ronronear() {
        System.out.println(getNombre() + " está ronroneando");
    }

    @Override
    public String toString() {
        return String.format("%s | Color: %s | Interior: %s", super.toString(), color, (esInterior ? "Sí" : "No"));
    }
}