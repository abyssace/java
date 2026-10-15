import java.util.ArrayList;
import java.util.Iterator;

public class GestionGremio {

    private ArrayList<Personaje> roster;
    private java.util.LinkedList<String> colaTurnos;
    private java.util.HashMap<String, Integer> inventario;
    private java.util.HashSet<String> habilidades;

    public GestionGremio() {
        roster      = new ArrayList<>();
        colaTurnos  = new java.util.LinkedList<>();
        inventario  = new java.util.HashMap<>();
        habilidades = new java.util.HashSet<>();
    }

    // ──────────────────────────────────────────
    // SECCION 1 — ArrayList: roster de personajes
    // ──────────────────────────────────────────

    public void agregarMiembro(Personaje p) {
        roster.add(p);
        System.out.println("[Gremio] " + p.getNombre() + " se unió al gremio.");
    }

    public void eliminarMiembro(String nombre) {
        Iterator<Personaje> it = roster.iterator();
        while (it.hasNext()) {
            Personaje p = it.next();
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                it.remove();   // forma segura de eliminar durante iteración
                System.out.println("[Gremio] " + nombre + " abandonó el gremio.");
                return;
            }
        }
        System.out.println("[Gremio] No se encontró: " + nombre);
    }

    public Personaje buscarPorNombre(String nombre) {
        for (Personaje p : roster) {       // for-each
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null;
    }

    public void mostrarRoster() {
        System.out.println("\n=== Roster del Gremio (" + roster.size() + " miembros) ===");
        for (int i = 0; i < roster.size(); i++) {
            Personaje p = roster.get(i);
            System.out.println((i + 1) + ". " + p.getNombre() +
                               " | Nivel: " + p.getNivel() +
                               " | Vida: " + p.getPuntosVida());
        }
    }

    public void encolarSolicitante(String nombre){
        colaTurnos.addLast(nombre);
        System.out.println("[Cola] " + nombre +
            " en posición " + colaTurnos.size());
    }
    public String atenderSiguiente() {
    if (colaTurnos.isEmpty()) {
        System.out.println("[Cola] No hay solicitantes en espera.");
        return null;
    }
    String atendido = colaTurnos.removeFirst();   // saca del frente
    System.out.println("[Cola] Atendiendo a: " + atendido);
    return atendido;
}
    public void mostrarCola() {
    System.out.println("\n=== Cola de Espera (" + colaTurnos.size() + ") ===");
    int pos = 1;
    for (String nombre : colaTurnos) {    // for-each sobre LinkedList
        System.out.println(pos++ + ". " + nombre);
    }
}

    public void agregarItem(String item, int cantidad){
        int cantidadActual=inventario.getOrDefault(item,0);
        inventario.put(item, cantidadActual+cantidad);
            System.out.println("[Inventario] " + item + " → " + inventario.get(item) + " unidades");
}
    public void usarItem(String item) {
    if (!inventario.containsKey(item)) {
        System.out.println("[Inventario] No tienes: " + item);
        return;
    }

    int cantidadActual = inventario.get(item);

    if (cantidadActual <= 0) {
        System.out.println("[Inventario] No te quedan unidades de: " + item);
        return;
    }

    int nuevaCantidad = cantidadActual - 1;

    if (nuevaCantidad == 0) {
        inventario.remove(item);
        System.out.println("[Inventario] " + item + " agotado, eliminado del inventario.");
    } else {
        inventario.put(item, nuevaCantidad);
        System.out.println("[Inventario] Usaste " + item + ". Quedan: " + nuevaCantidad);
    }
}
    public void mostrarInventario() {
    System.out.println("\n=== Inventario (" + inventario.size() + " items) ===");
    for (java.util.Map.Entry<String, Integer> entry : inventario.entrySet()) {
        System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }

    public void registrarHabilidad(String habilidad) {
    boolean eraNueva = habilidades.add(habilidad);
    if (eraNueva) {
        System.out.println("[Habilidades] Registrada: " + habilidad);
    } else {
        System.out.println("[Habilidades] Ya tenías: " + habilidad);
        }
    }
    public boolean tieneHabilidad(String habilidad) {
    return habilidades.contains(habilidad);
    }
    public void mostrarHabilidades() {
    System.out.println("\n=== Habilidades (" + habilidades.size() + ") ===");
    for (String h : habilidades) {
        System.out.println("- " + h);
        }
    }

    
}

    
