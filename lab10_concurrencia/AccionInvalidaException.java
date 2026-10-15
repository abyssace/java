public class AccionInvalidaException extends RpgException {
    private final String accion;

    public AccionInvalidaException(String accion, String razon) {
        super("Acción inválida '" + accion + "': " + razon);
        this.accion = accion;
    }

    public String getAccion() {
        return accion;
    }
}
