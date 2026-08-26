public class Mago extends Personaje{
    private int mana;
    private String escuelaMagia;
    private int nivelMagia;
    private String varita;

    public Mago(String nombre, int nivel, int puntosVida, int mana, String escuelaMagia, int nivelMagia, String varita) {
        super(nombre, nivel, puntosVida, true);
        this.mana = mana;
        this.escuelaMagia = escuelaMagia;
        this.nivelMagia = nivelMagia;
        this.varita = varita;
    }

    public int getMana(){
        return mana;
    }

    public String getEscuelaMagia(){
        return escuelaMagia;
    }

    public int getNivelMagia(){
        return nivelMagia;
    }

    public String getVarita(){
        return varita;
    }

    @Override
    public void atacar(){
        super.atacar();
        System.out.println(getNombre() + " ha usado un hechizo");
    }

    @Override
    public void defender(){
        System.out.println(getNombre() + " ha usado un escudo magico");
    }

    @Override
    public String toString() {
        return super.toString() + " Mago{" +
                "mana=" + mana +
                ", escuelaMagia='" + escuelaMagia + '\'' +
                ", nivelMagia=" + nivelMagia +
                ", varita='" + varita + '\'' +
                '}';
    }
}