public abstract class Personaje implements Combatiente {

    protected String nombre;
    protected int nivel;
    protected int puntosVida;
    protected boolean estaVivo;

    public Personaje(String nombre, int nivel, int puntosVida) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.puntosVida = puntosVida;
        this.estaVivo = true;
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

    public void recibirDanio(int danio) {
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
    public void curar(int cantidad) {
    puntosVida += cantidad;
}
    public void recibirCuracion(int cantidad) {
    puntosVida += cantidad;
    System.out.println(nombre + " recibe " + cantidad + " de curación. Vida: " + puntosVida);
}

    @Override
    public abstract void atacar();

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
