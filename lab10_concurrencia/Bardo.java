public class Bardo extends Personaje implements Sanador {

    private String instrumento;
    private String melodiasConocidas;

    public Bardo(String nombre, int nivel, String instrumento, String melodiasConocidas) {
        super(nombre, nivel, 90);
        this.instrumento = instrumento;
        this.melodiasConocidas = melodiasConocidas;
    }

    @Override
    public void atacar() throws RpgException {
        if (!isEstaVivo()) {
            throw new PersonajeDerrotadoException(getNombre());
        }
        System.out.println(getNombre() + " ataca con su " + instrumento);
    }

    @Override
    public int calcularDanio() {
        return nivel * 8;
    }

    @Override
    public void defender() {
        System.out.println(getNombre() + " toca una nota de apoyo para protegerse.");
    }

    @Override
    public void curarAliado(Personaje aliado) throws RpgException {
        if (aliado == null) {
            throw new PersonajeNuloException("curarAliado");
        }
        if (!isEstaVivo()) {
            throw new PersonajeDerrotadoException(getNombre());
        }
        if (!aliado.isEstaVivo()) {
            throw new PersonajeDerrotadoException(aliado.getNombre());
        }
        System.out.println(getNombre() + " cura a " + aliado.getNombre() +
                " con su melodía " + melodiasConocidas + " (" + getPoderCuracion() + ")");
        aliado.curar(getPoderCuracion());
    }

    @Override
    public int getPoderCuracion() {
        return nivel * 5;
    }
}
