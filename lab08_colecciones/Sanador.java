public interface Sanador {
    void curarAliado(Personaje aliado) throws RpgException;
    int getPoderCuracion();
}