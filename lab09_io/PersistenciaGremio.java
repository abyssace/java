import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PersistenciaGremio {

    private static final String CARPETA = "datos_gremio";

    public PersistenciaGremio() {
        crearCarpetaSiNoExiste();
    }

    // Crea la carpeta si todavía no existe
    private void crearCarpetaSiNoExiste() {
        File carpeta = new File(CARPETA);
        if (!carpeta.exists()) {
            carpeta.mkdir();
            System.out.println("[IO] Carpeta '" + CARPETA + "' creada.");
        }
    }

    // ──────────────────────────────────────────────────────
    // GUARDAR ROSTER
    // Formato de linea:  Sylva,10,300
    //                    nombre,nivel,puntosVida
    // ──────────────────────────────────────────────────────
    public void guardarRoster(ArrayList<Personaje> roster) throws IOException {

        File archivo = new File(CARPETA + "/roster.txt");

        BufferedWriter writer = new BufferedWriter(new FileWriter(archivo));

        for (Personaje p : roster) {
            writer.write(p.getNombre() + "," +
                         p.getNivel()  + "," +
                         p.getPuntosVida());
            writer.newLine();   // salto de línea independiente del SO
        }

        writer.close();
        System.out.println("[IO] Roster guardado: " +
                           roster.size() + " personajes.");
    }

    // ──────────────────────────────────────────────────────
    // CARGAR ROSTER — retorna lista de líneas sin parsear
    // (el Main decide qué hacer con cada línea)
    // ──────────────────────────────────────────────────────
    public ArrayList<String> cargarRoster() throws IOException {

        File archivo = new File(CARPETA + "/roster.txt");
        ArrayList<String> lineas = new ArrayList<>();

        if (!archivo.exists()) {
            System.out.println("[IO] roster.txt no encontrado.");
            return lineas;
        }

        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        String linea;

        while ((linea = reader.readLine()) != null) {
            if (!linea.trim().isEmpty()) {
                lineas.add(linea);
            }
        }

        reader.close();
        System.out.println("[IO] Roster cargado: " +
                           lineas.size() + " registros.");
        return lineas;
    }
    // ──────────────────────────────────────────────────────
// AGREGAR ENTRADA A BITÁCORA (modo append — no borra lo anterior)
// ──────────────────────────────────────────────────────
public void agregarEntradaBitacora(String entrada) throws IOException {

    File archivo = new File(CARPETA + "/bitacora.txt");

    // true = modo append → agrega al final del archivo
    BufferedWriter writer = new BufferedWriter(
                                new FileWriter(archivo, true));
    writer.write(entrada);
    writer.newLine();
    writer.close();
}

// ──────────────────────────────────────────────────────
// LEER Y MOSTRAR BITÁCORA COMPLETA
// ──────────────────────────────────────────────────────
public void mostrarBitacora() throws IOException {

    File archivo = new File(CARPETA + "/bitacora.txt");

    if (!archivo.exists()) {
        System.out.println("[IO] La bitácora está vacía.");
        return;
    }

    System.out.println("\n=== Bitácora de Batallas ===");
    BufferedReader reader = new BufferedReader(new FileReader(archivo));
    String linea;
    int numero = 1;

    while ((linea = reader.readLine()) != null) {
        System.out.println(numero++ + ". " + linea);
    }

    reader.close();
    }

public void guardarInventario(HashMap<String, Integer> inventario) throws IOException {
    File archivo = new File(CARPETA + "/inventario.txt");

    
    BufferedWriter writer = new BufferedWriter(new FileWriter(archivo));

    int contador = 0;
    for (Map.Entry<String, Integer> entry : inventario.entrySet()) {
        writer.write(entry.getKey() + "," + entry.getValue());
        writer.newLine();
        contador++;
    }

    writer.close();
    System.out.println("[IO] Inventario guardado: " + contador + " items.");
    }
public HashMap<String, Integer> cargarInventario() throws IOException {
    HashMap<String, Integer> inventario = new HashMap<>();
    File archivo = new File(CARPETA + "/inventario.txt");

    // Si el archivo no existe, retorna un HashMap vacío
    if (!archivo.exists()) {
        System.out.println("[IO] inventario.txt no encontrado. Devolviendo inventario vacío.");
        return inventario;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            if (linea.trim().isEmpty()) continue;

            String[] partes = linea.split(",");
            if (partes.length == 2) {
                String item = partes[0];
                int cantidad = Integer.parseInt(partes[1].trim());
                inventario.put(item, cantidad);
            }
        }
    }

    System.out.println("[IO] Inventario cargado: " + inventario.size() + " items.");
    return inventario;
    }
}