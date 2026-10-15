public class Main {
 
    // Si en tu máquina el "sin sync" da 5000, sube este valor (ej. 100000)
    private static final int HILOS = 5;
    private static final int INCREMENTOS = 1000;
 
    public static void main(String[] args) {
 
        System.out.println("=== Gran Torneo del Gremio ===\n");
 
        // ---------------------------------------------------------
        // BLOQUE 1 — Torneo con 3 combates simultáneos
        // ---------------------------------------------------------
        TablaResultados tabla = new TablaResultados();
 
        Personaje s1 = new Druida("Silvanus", 10, 300, 100, "Oso");
        Personaje m1 = new Nigromante("Malachar", 8, "Maldicion", 120, 80);
        Personaje l1 = new Arquero("Legolas", 6, 200, "Arco", 10, 95, 85);
        Personaje t1 = new Guerrero("Thorin", 9, 400, 80, "Hacha");
        Personaje b1 = new Druida("Brego", 7, 250, 80, "Lobo");
        Personaje k1 = new Nigromante("Kael", 5, "Maldicion", 90, 70);
 
        CombateConcurrente c1 = new CombateConcurrente("Campo-1", s1, m1, tabla);
        CombateConcurrente c2 = new CombateConcurrente("Campo-2", l1, t1, tabla);
        CombateConcurrente c3 = new CombateConcurrente("Campo-3", b1, k1, tabla);
 
        Thread h1 = new Thread(c1);
        Thread h2 = new Thread(c2);
        Thread h3 = new Thread(c3);
 
        h1.start();
        h2.start();
        h3.start();
 
        // ---------------------------------------------------------
        // BLOQUE 2 — Monitor del torneo (hilo independiente)
        // ---------------------------------------------------------
        MonitorTorneo monitor = new MonitorTorneo("Torneo Principal", 3);
        monitor.start();
 
        // ---------------------------------------------------------
        // BLOQUE 3 — Esperar a que todos terminen con join()
        // ---------------------------------------------------------
        try {
            h1.join();       // espera a que Campo-1 termine
            h2.join();       // espera a que Campo-2 termine
            h3.join();       // espera a que Campo-3 termine
            monitor.join();  // espera al monitor
        } catch (InterruptedException e) {
            System.out.println("Espera interrumpida.");
            Thread.currentThread().interrupt();
        }
 
        System.out.println("\nTodos los combates han terminado.");
        tabla.mostrarResultados();
 
        // ---------------------------------------------------------
        // BLOQUE 4 — Demostración de race condition
        // ---------------------------------------------------------
        System.out.println("\n=== Demostración de race condition ===");
 
        ContadorCompartido contador = new ContadorCompartido();
        int esperado = HILOS * INCREMENTOS;
        Thread[] hilos = new Thread[HILOS];
 
        try {
            // Sin synchronized — resultado incorrecto
            for (int i = 0; i < HILOS; i++) {
                hilos[i] = new Thread(() -> {
                    for (int j = 0; j < INCREMENTOS; j++) contador.incrementar();
                });
                hilos[i].start();
            }
            for (Thread t : hilos) t.join();
            System.out.println("Sin sync  — esperado: " + esperado +
                               " | real: " + contador.getContador());
 
            // Con synchronized — resultado correcto
            contador.resetear();
            for (int i = 0; i < HILOS; i++) {
                hilos[i] = new Thread(() -> {
                    for (int j = 0; j < INCREMENTOS; j++) contador.incrementarSeguro();
                });
                hilos[i].start();
            }
            for (Thread t : hilos) t.join();
            System.out.println("Con sync  — esperado: " + esperado +
                               " | real: " + contador.getContador());
 
        } catch (InterruptedException e) {
            System.out.println("Demostración interrumpida.");
            Thread.currentThread().interrupt();
        }
    }
}