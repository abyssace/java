public class Main {

    public static void main(String[] args) {

        GestionGremio gremio = new GestionGremio();

        // ═══════════════════════════════════════════════
        // BLOQUE 1 — Gestión de roster con ArrayList
        // ═══════════════════════════════════════════════
        System.out.println("\n########## BLOQUE 1: ROSTER ##########");

        gremio.agregarMiembro(new Druida("Sylva", 10, 300, 100, "Lobo"));
        gremio.agregarMiembro(new Nigromante("Malachar", 8, "Almas perdidas", 12, 250));
        gremio.agregarMiembro(new Arquero("Legolas", 6, 150, "Arco élfico", 20, 95, 200));
        gremio.agregarMiembro(new Guerrero("Thorin", 9, 400, 80, "Hacha de guerra"));

        gremio.mostrarRoster();

        gremio.eliminarMiembro("Malachar");
        gremio.mostrarRoster();

        Personaje encontrado = gremio.buscarPorNombre("Legolas");
        if (encontrado != null) {
            System.out.println("Encontrado: " + encontrado.getNombre());
        }

        // ═══════════════════════════════════════════════
        // BLOQUE 2 — Cola de espera con LinkedList
        // ═══════════════════════════════════════════════
        System.out.println("\n########## BLOQUE 2: COLA ##########");

        gremio.encolarSolicitante("Gandalf");
        gremio.encolarSolicitante("Aragorn");
        gremio.encolarSolicitante("Gimli");
        gremio.mostrarCola();

        gremio.atenderSiguiente();   // atiende a Gandalf (FIFO)
        gremio.mostrarCola();

        // ═══════════════════════════════════════════════
        // BLOQUE 3 — Inventario con HashMap
        // ═══════════════════════════════════════════════
        System.out.println("\n########## BLOQUE 3: INVENTARIO ##########");

        gremio.agregarItem("Poción de vida", 5);
        gremio.agregarItem("Flecha élfica", 30);
        gremio.agregarItem("Poción de vida", 3);   // suma → 8

        gremio.mostrarInventario();

        gremio.usarItem("Poción de vida");
        gremio.usarItem("Pergamino de fuego");     // no existe
        gremio.mostrarInventario();

        // ═══════════════════════════════════════════════
        // BLOQUE 4 — Habilidades únicas con HashSet
        // ═══════════════════════════════════════════════
        System.out.println("\n########## BLOQUE 4: HABILIDADES ##########");

        gremio.registrarHabilidad("Curación");
        gremio.registrarHabilidad("Magia oscura");
        gremio.registrarHabilidad("Curación");     // duplicado — no se agrega

        gremio.mostrarHabilidades();

        System.out.println("¿Tiene Tiro con arco? " + gremio.tieneHabilidad("Tiro con arco"));
        System.out.println("¿Tiene Curación? "     + gremio.tieneHabilidad("Curación"));

        // ═══════════════════════════════════════════════
        // BLOQUE 5 — Resumen del gremio
        // ═══════════════════════════════════════════════
        System.out.println("\n########## BLOQUE 5: RESUMEN ##########");

        gremio.mostrarRoster();
        gremio.mostrarCola();
        gremio.mostrarInventario();
        gremio.mostrarHabilidades();
    }
}