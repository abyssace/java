public class Main {
    public static void main(String[] args) {
        System.out.println("=== RPG  Expansión: Nuevas Clases ===\n");


        Druida sylva = new Druida("Sylva", 7, 180, 120, "lobo");
        Nigromante malachar = new Nigromante("Malachar", 6, "Maldición de decadencia", 22);
        Bardo finnian = new Bardo("Finnian", 5, "laúd", "melodía de sanación");

        System.out.println("-- Ataques y daño --");
        sylva.atacar();
        malachar.atacar();
        finnian.atacar();

        System.out.println("\n-- Solo los Hechiceros lanzan hechizos --");
        sylva.lanzarHechizo();
        malachar.lanzarHechizo();

        System.out.println("\n-- Solo los Sanadores curan --");
        malachar.recibirDanio(300);


        System.out.println("\n-- Estado final --");
        System.out.println("Nombre: " + sylva.getNombre() + "    | Nivel: " + sylva.getNivel() + " | Vida: " + sylva.getPuntosVida() + " | Vivo: " + (sylva.isEstaVivo() ? "Sí" : "No"));
        System.out.println("Nombre: " + malachar.getNombre() + " | Nivel: " + malachar.getNivel() + " | Vida: " + malachar.getPuntosVida() + " | Vivo: " + (malachar.isEstaVivo() ? "Sí" : "No"));
        System.out.println("Nombre: " + finnian.getNombre() + "  | Nivel: " + finnian.getNivel() + " | Vida: " + finnian.getPuntosVida() + " | Vivo: " + (finnian.isEstaVivo() ? "Sí" : "No"));
    }
}