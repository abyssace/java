public class PersonajeNuloException extends RuntimeException {

    public PersonajeNuloException(String metodo){
        super(" Se paso un personaje nulo al metodo " + metodo + " Esto es un error de programacion ");
    }
    
}
