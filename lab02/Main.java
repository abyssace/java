public class Main {
    public static void main(String[] args) {
        System.out.println("=== Clínica Veterinaria ===\n");

        Perro perro = new Perro("Max", 3, 12.5, "Labrador", true);
        System.out.println("-- Perro --");
        System.out.println(perro.toString());
        perro.comer();
        perro.ladrar();
        perro.buscarPelota();

        System.out.println();

        Gato gato = new Gato("Misi", 2, 3.8, "Gris", true);
        System.out.println("-- Gato --");
        System.out.println(gato.toString());
        gato.dormir();
        gato.maullar();
        gato.ronronear();

        System.out.println();

        Canario canario = new Canario("Pico", 1, 0.03, "Amarillo", true);
        System.out.println("-- Canario --");
        System.out.println(canario.toString());
        canario.comer();
        canario.cantar();
        canario.volar();
    }
}