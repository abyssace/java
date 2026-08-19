public class Canario extends Animal {
    private String colorPlumaje;
    private boolean cantaEnJaula;

    public Canario(String nombre, int edad, double peso, String colorPlumaje, boolean cantaEnJaula) {
        super(nombre, edad, peso);
        this.colorPlumaje = colorPlumaje;
        this.cantaEnJaula = cantaEnJaula;
    }

    public void cantar() {
        System.out.println(getNombre() + " está cantando: ♪ pi pi pi ♪");
    }

    public void volar() {
        System.out.println(getNombre() + " está volando por la jaula.");
    }

    @Override
    public String toString() {
        return String.format("%s | Plumaje: %s | Canta en jaula: %s", super.toString(), colorPlumaje, (cantaEnJaula ? "Sí" : "No"));
    }
}