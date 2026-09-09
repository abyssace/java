public class Main {
    public static void main(String[] args) {
        System.out.println("=== RPG — Demostración de Polimorfismo ===\n");

        // 3a — Variable de tipo padre apunta a objeto hijo
        Personaje p1 = new Guerrero("Thorin", 5, 200, 85, "Cota de Malla");
        Personaje p2 = new Mago("Gandalf", 8, 120, 150, "Hogwarts", 10, "Varita de Roble");
        Personaje p3 = new Arquero("Legolas", 6, 150, "Arco Largo", 30, 95, 75);

        System.out.println("-- calcularDanio() por tipo --");
        System.out.println(p1.getNombre() + "  (Guerrero) daño: " + p1.calcularDanio() + "    ← fuerza(" + ((Guerrero)p1).getFuerza() + ") * nivel(" + p1.getNivel() + ")");
        System.out.println(p2.getNombre() + " (Mago)     daño: " + p2.calcularDanio() + "   ← mana(" + ((Mago)p2).getMana() + ")  * nivel(" + p2.getNivel() + ")");
        System.out.println(p3.getNombre() + " (Arquero)  daño: " + p3.calcularDanio() + "   ← precision(" + ((Arquero)p3).getPrecision() + ") * flechas(" + ((Arquero)p3).getFlechasDisponibles() + ")");
        System.out.println();

        // 3b — Arreglo polimórfico
        Personaje[] equipo = {p1, p2, p3};
        
        System.out.println("-- Arreglo polimórfico --");
        for (Personaje p : equipo) {
            System.out.print(" [ " + p.getNombre() + "]  ");
            if (p instanceof Guerrero) {
                System.out.println(" ataca con un golpe básico. ");
                System.out.println("  " + p.getNombre() + " golpea con su espada causando " + ((Guerrero)p).getFuerza() + " de danio");
            } else if (p instanceof Mago) {
                System.out.println(" ataca con un golpe básico. " );
                System.out.println("¡" + p.getNombre() + " lanza una bola de fuego causando " + ((Mago)p).getMana() + " de danio magico");
            } else if (p instanceof Arquero) {
                System.out.println(" ataca con un golpe basico. ");
                System.out.println(p.getNombre() + " dispara una flecha. Flechas restantes: " + (((Arquero)p).getFlechasDisponibles() - 1));
            }
        }
        System.out.println();

        // 3c — Usar GestorBatalla con las tres sobrecargas
        GestorBatalla gestor = new GestorBatalla();
        
        System.out.println("-- GestorBatalla --");
        System.out.println("[BATALLA] " + p1.getNombre() + " ataca solo <-- danio: " + p1.calcularDanio());
        gestor.ejecutarAtaque(p1);
        
        System.out.println("[BATALLA] " + p2.getNombre() + " ataca a " + p3.getNombre() + " <-- danio: " + p2.calcularDanio());
        gestor.ejecutarAtaque(p2, p3);
        
        System.out.println(" [BATALLA] Equipo completo ataca --> " + equipo.length + " personajes ");
        gestor.ejecutarAtaque(equipo);
        System.out.println();

        // Mostrar historial
        System.out.println("-- Historial --");
        gestor.mostrarHistorial();
        System.out.println();

        // 3d — Identificar tipo real con instanceof
        System.out.println("-- instanceof --");
        for (Personaje p : equipo) {
            if (p instanceof Guerrero) {
                System.out.println(p.getNombre() + " es un Guerrero.");
            } else if (p instanceof Mago) {
                System.out.println(p.getNombre() + " es un Mago.");
            } else if (p instanceof Arquero) {
                System.out.println(p.getNombre() + " es un Arquero.");
            }
        }
    }
}