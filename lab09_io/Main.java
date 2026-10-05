import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        try {
            // ═══════════════════════════════════════════════
            // BLOQUE 1 — Crear personajes y guardar roster
            // ═══════════════════════════════════════════════
            System.out.println("\n########## BLOQUE 1: GUARDAR ROSTER ##########");

            PersistenciaGremio persistencia = new PersistenciaGremio();

            ArrayList<Personaje> roster = new ArrayList<>();
            roster.add(new Druida("Sylva", 10, 300, 100, "Lobo"));
            roster.add(new Arquero("Legolas", 6, 150, "Arco élfico", 20, 95, 200));
            roster.add(new Guerrero("Thorin", 9, 400, 80, "Hacha de guerra"));

            persistencia.guardarRoster(roster);

            // ═══════════════════════════════════════════════
            // BLOQUE 2 — Cargar roster desde archivo
            // ═══════════════════════════════════════════════
            System.out.println("\n########## BLOQUE 2: CARGAR ROSTER ##########");

            ArrayList<String> lineasRoster = persistencia.cargarRoster();
            System.out.println("\n=== Roster cargado desde archivo ===");
            for (String linea : lineasRoster) {
                String[] partes = linea.split(",");
                System.out.println("Nombre: " + partes[0] +
                                   " | Nivel: " + partes[1] +
                                   " | Vida: "  + partes[2]);
            }

            // ═══════════════════════════════════════════════
            // BLOQUE 3 — Guardar y cargar inventario
            // ═══════════════════════════════════════════════
            System.out.println("\n########## BLOQUE 3: INVENTARIO ##########");

            HashMap<String, Integer> inventario = new HashMap<>();
            inventario.put("Poción de vida", 8);
            inventario.put("Flecha élfica", 30);
            inventario.put("Pergamino de fuego", 3);

            persistencia.guardarInventario(inventario);

            HashMap<String, Integer> inventarioCargado = persistencia.cargarInventario();

            System.out.println("\n=== Inventario cargado desde archivo ===");
            for (Map.Entry<String, Integer> e : inventarioCargado.entrySet()) {
                System.out.println(e.getKey() + " → " + e.getValue());
            }

            // ═══════════════════════════════════════════════
            // BLOQUE 4 — Escribir entradas en la bitácora
            // ═══════════════════════════════════════════════
            System.out.println("\n########## BLOQUE 4: BITÁCORA ##########");

            persistencia.agregarEntradaBitacora("Sylva atacó a Malachar (daño: 240)");
            persistencia.agregarEntradaBitacora("Legolas sin flechas — no pudo atacar");
            persistencia.agregarEntradaBitacora("Thorin venció a Dragón de Hielo");

            persistencia.mostrarBitacora();

            // ═══════════════════════════════════════════════
            // BLOQUE 5 — Verificar los archivos creados
            // ═══════════════════════════════════════════════
            System.out.println("\n########## BLOQUE 5: ARCHIVOS ##########");

            File carpeta = new File("datos_gremio");
            System.out.println("\n=== Archivos en datos_gremio/ ===");
            File[] archivos = carpeta.listFiles();
            if (archivos != null) {
                for (File f : archivos) {
                    System.out.println(f.getName() + " (" + f.length() + " bytes)");
                }
            } else {
                System.out.println("La carpeta no existe o está vacía.");
            }

        } catch (IOException e) {
            System.out.println("Error de archivo: " + e.getMessage());
        }
    }
}