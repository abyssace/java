public class Nigromante extends Personaje implements Hechicero {
    private String maldicion;
    private int nivelOscuridad;
    private int almasAbsorbidas;
    private int mana;

    public Nigromante(String nombre, int nivel, String maldicion, int nivelOscuridad, int mana) {
        super(nombre, nivel, 80);
        this.maldicion = maldicion;
        this.nivelOscuridad = nivelOscuridad;
        this.almasAbsorbidas = 0;
        this.mana = mana;
    }

    @Override
    public void atacar() throws RpgException {
        if (!isEstaVivo()) {
            throw new PersonajeDerrotadoException(getNombre());
        }
        if (mana < 15) {
            throw new RecursoInsuficienteException("mana", mana);
        }

        System.out.println(getNombre() + " ha drenado vida");
        almasAbsorbidas += 5;
        mana -= 15;
    }

    @Override
    public int calcularDanio() {
        return (nivel * 12) + almasAbsorbidas;
    }

    @Override
    public void defender() {
        System.out.println(getNombre() + " invoca sombras para protegerse.");
    }

    @Override
    public void lanzarHechizo() {
        System.out.println(getNombre() + " lanza un hechizo de " + maldicion +
                " con nivel de oscuridad " + nivelOscuridad);
    }

    @Override
    public int getMana() {
        return mana;
    }
}
