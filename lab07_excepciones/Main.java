public class Main {
    public static void main(String[] args) {
        Druida druida = new Druida("Sylva", 7, 180, 120, "lobo");
        Druida druida2 = new Druida("Lorien", 8, 200, 190, "aguila");
        Nigromante nigromante = new Nigromante("Morgana", 8, "maldicion de la noche", 5, 100);
        MotorCombate motor = new MotorCombate();

        System.out.println("Escenario 1");
        motor.ejecutarTurno(druida, nigromante);

        System.out.println("Escenario 2");
        try {
            druida.recibirDanio(9999);
        } catch (AccionInvalidaException e) {
            System.out.println("Error: " + e.getMessage());
        }
        motor.ejecutarTurno(druida, nigromante);

        System.out.println("Escenario 3");
        Arquero sinFlechas = new Arquero("Legolas", 10, 100, "arco largo", 0, 80, 50);
        motor.ejecutarTurno(sinFlechas, nigromante);

        try {
            druida2.curarAliado(nigromante);
        } catch (Exception e) {
            System.out.println("Error al curar aliado: " + e.getMessage());
        }

        try {
            nigromante.recibirDanio(-50);
        } catch (AccionInvalidaException e) {
            System.out.println("Error: " + e.getMessage() + " en la acción: " + e.getAccion());
        } finally {
            System.out.println("El bloque finally siempre se ejecuta.");
        }

        motor.mostrarBitacora();
    }
}