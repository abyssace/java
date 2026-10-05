public abstract class Personaje implements Combatiente {

    protected String nombre;
    protected int nivel;
    protected int puntosVida;
    protected boolean estaVivo;

    public Personaje(String nombre, int nivel, int puntosVida) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new PersonajeNuloException("Personaje(String, int, int)");
        }
        this.nombre = nombre;
        this.nivel = nivel;
        this.puntosVida = puntosVida;
        this.estaVivo = puntosVida > 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getNivel() {
        return nivel;
    }

    public int getPuntosVida() {
        return puntosVida;
    }

    public boolean isEstaVivo() {
        return estaVivo;
    }

    public void recibirDanio(int danio) throws AccionInvalidaException {
        if (danio < 0) {
            throw new AccionInvalidaException("recibirDanio",
                    "El daño no puede ser negativo: " + danio);
        }

        puntosVida -= danio;
        if (puntosVida <= 0) {
            puntosVida = 0;
            estaVivo = false;
        }

        System.out.println(nombre + " recibe " + danio +
                " de daño. Vida: " + puntosVida);
        if (!estaVivo) {
            System.out.println(nombre + " ha sido derrotado.");
        }
    }

    public void curar(int cantidad) throws AccionInvalidaException {
        if (cantidad < 0) {
            throw new AccionInvalidaException("curar",
                    "La curación no puede ser negativa: " + cantidad);
        }
        puntosVida += cantidad;
        estaVivo = true;
        System.out.println(nombre + " recibe " + cantidad + " de curación. Vida: " + puntosVida);
    }

    public void recibirCuracion(int cantidad) throws AccionInvalidaException {
        curar(cantidad);
    }

    @Override
    public abstract void atacar() throws RpgException;

    public abstract int calcularDanio();

    @Override
    public abstract void defender();

    @Override
    public String toString() {
        String vivo = estaVivo ? "Sí" : "No";
        return "Nombre: " + nombre + " | Nivel: " + nivel +
                " | Vida: " + puntosVida + " | Vivo: " + vivo;
    }
}
