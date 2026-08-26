public class Main {
    public static void main(String[] args) {
        
        Guerrero thorin = new Guerrero("Thorin", 5, 200, 85, "Cota de Malla");
        Mago gandalf = new Mago("Gandalf", 8, 200, 120, "Escuela de Fuego", 10, "Varita de Roble");
        Arquero legolas = new Arquero("Legolas", 6, 120, "Arco Largo", 20, 95, 50);

        System.out.println("=== Batalla RPG ===\n");

        System.out.println("-- Ronda 1: Ataques --");
        thorin.atacar();
        System.out.println();
        gandalf.atacar();
        System.out.println();
        legolas.atacar();

        System.out.println("\n-- Ronda 2: Defensas --");
        thorin.defender();
        gandalf.defender();
        legolas.defender();

        System.out.println("\n-- Daño recibido --");
        thorin.recibirdanio(60);
        gandalf.recibirdanio(200);

        System.out.println("\n-- Estado final --");
        thorin.mostrarEstado();
        gandalf.mostrarEstado();
        legolas.mostrarEstado();
    }
}