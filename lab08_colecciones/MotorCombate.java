import java.util.ArrayList;

public class MotorCombate {

    private ArrayList<String> bitacora;

    public MotorCombate() {
        bitacora = new ArrayList<>();
    }

    public void ejecutarTurno(Personaje atacante, Personaje defensor) {

        if (atacante == null) throw new PersonajeNuloException("ejecutarTurno(atacante)");
        if (defensor == null) throw new PersonajeNuloException("ejecutarTurno(defensor)");

        System.out.println("\n--- Turno: " + atacante.getNombre() +
                           " vs " + defensor.getNombre() + " ---");
        try {
            atacante.atacar();
            int danio = atacante.calcularDanio();
            defensor.recibirDanio(danio);
            bitacora.add("OK | " + atacante.getNombre() +
                         " atacó a " + defensor.getNombre() +
                         " (daño: " + danio + ")");

        } catch (PersonajeDerrotadoException e) {
            bitacora.add("DERROTA | " + e.getNombrePersonaje() +
                         " no pudo actuar");
            System.out.println(e.getMessage());

        } catch (RecursoInsuficienteException e) {
            bitacora.add("SIN RECURSO | " + atacante.getNombre() +
                         " no pudo atacar");
            System.out.println(e.getMessage());

        } catch (RpgException e) {
            bitacora.add("ERROR RPG | " + e.getMessage());
            System.out.println(" Error de juego: " + e.getMessage());

        } finally {
            // Siempre se ejecuta, haya o no excepción
            System.out.println("[Bitácora] Turno registrado.");
        }
    }

    public void mostrarBitacora() {
        System.out.println("\n=== Bitácora de Combate ===");
        for (int i = 0; i < bitacora.size(); i++) {
            System.out.println((i + 1) + ". " + bitacora.get(i));
        }
    }
}