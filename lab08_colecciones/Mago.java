public class Mago extends Personaje{
    private int mana;
    private String escuelaMagia;
    private int nivelMagia;
    private String varita;

    public Mago(String nombre, int nivel, int puntosVida, int mana, String escuelaMagia, int nivelMagia, String varita) {
        super(nombre, nivel, puntosVida);
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
    
public void lanzarHechizo() {
    System.out.println(getNombre() + " lanza un hechizo básico de " + escuelaMagia);
}


public void lanzarHechizo(int nivelHechizo) {
    System.out.println(getNombre() + " lanza un hechizo de nivel " + nivelHechizo + " con " + mana + " de mana");
}


public void lanzarHechizo(String nombreHechizo) {
    System.out.println(getNombre() + " lanza '" + nombreHechizo + "' con su " + varita);
}

    @Override
    public void atacar(){
        System.out.println(getNombre() + " ha usado un hechizo");
    }

    @Override
    public void defender(){
        System.out.println(getNombre() + " ha usado un escudo magico");
    }

    @Override
    public int calcularDanio() {
        return mana * getNivel();
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