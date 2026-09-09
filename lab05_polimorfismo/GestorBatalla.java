import java.util.ArrayList;

public class GestorBatalla {
    // Version 1: un solo atacante
    public void ejecutarAtaque(Personaje atacante){
        historial.add(atacante.getNombre() + " ha atacado ");
        atacante.atacar();
    }
    // Version 2: atacante vs defensor
    public void ejecutarAtaque(Personaje atacante, Personaje defensor){
        historial.add(atacante.getNombre() + " ha atacado a " + defensor.getNombre());
        atacante.atacar();
        defensor.recibirdanio(atacante.calcularDanio());
        defensor.defender();
    }
    // Version 3: todo un equipo ataca 
    public void ejecutarAtaque(Personaje[] equipo){
        historial.add(" El equipo esta atacando ");
        for(Personaje p: equipo){
            historial.add(p.getNombre() + " ha causado " + p.calcularDanio() + " de danio ");
            p.atacar();
        }
    }

   private ArrayList<String> historial;

   public GestorBatalla() {
       historial = new ArrayList<>();
   }

      public void mostrarHistorial() {
        for (int i = 0; i < historial.size(); i++) {
            System.out.println((i + 1) + ". " + historial.get(i));
        }
    }
 
    public void limpiarHistorial() {
        historial.clear();
    }

}
