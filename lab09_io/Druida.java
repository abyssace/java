public class Druida extends Personaje implements Hechicero, Sanador {

    private int mana;
    private int poderCuracion;
    private String formaAnimal;

    public Druida(String nombre, int nivel, int mana, int poderCuracion, String formaAnimal) {
        super(nombre, nivel, 100);
        this.mana = mana;
        this.poderCuracion = poderCuracion;
        this.formaAnimal = formaAnimal;
    }

    public String getFormaAnimal() {
        return formaAnimal;
    }

    @Override
    public int calcularDanio() {
        return nivel * 10;
    }

    @Override
    public void atacar() throws RpgException {
        if (!isEstaVivo()) {
            throw new PersonajeDerrotadoException(getNombre());
        }
        if (mana <= 0) {
            throw new RecursoInsuficienteException("mana", mana);
        }

        System.out.println(getNombre() + " ataca con su forma de " + getFormaAnimal() +
                " causando " + calcularDanio() + " de daño.");
        mana -= 10;
    }

    @Override
    public void defender() {
        System.out.println(getNombre() + " se transforma en " + formaAnimal + " para defenderse.");
    }

    @Override
    public void lanzarHechizo() {
        System.out.println(getNombre() + " lanza un hechizo.");
    }

    @Override
    public int getMana() {
        return mana;
    }

    @Override
    public void curarAliado(Personaje aliado) throws RpgException {
        if (aliado == null) {
            throw new PersonajeNuloException("curarAliado");
        }
        if (!isEstaVivo()) {
            throw new PersonajeDerrotadoException(getNombre());
        }
        if (mana <= 0) {
            throw new RecursoInsuficienteException("mana", mana);
        }
        if (!aliado.isEstaVivo()) {
            throw new PersonajeDerrotadoException(aliado.getNombre());
        }

        System.out.println(getNombre() + " cura a " + aliado.getNombre() +
                " con " + poderCuracion + " de poder de curación.");
        aliado.curar(poderCuracion);
        mana -= 10;
    }

    @Override
    public int getPoderCuracion() {
        return poderCuracion;
    }
}