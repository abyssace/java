public class RecursoInsuficienteException extends RpgException {

    public RecursoInsuficienteException(String recurso, int disponible){
        super("No hay suficiente " + recurso + ". Disponible: " + disponible);
    }
    
}
