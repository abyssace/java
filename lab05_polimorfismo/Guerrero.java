public class Guerrero extends Personaje{

    private int fuerza;
    private String armadura;

    public Guerrero(String nombre, int nivel, int puntosVida, int fuerza, String armadura) {
        super(nombre, nivel, puntosVida, true);
        this.fuerza = fuerza;
        this.armadura = armadura;
    }

    public int getFuerza(){
        return fuerza;
    }

    public String getArmadura(){
        return armadura;
    }

    
public void golpear() {
    System.out.println(getNombre() + " da un golpe con " + fuerza + " de fuerza");
}


public void golpear(int intensidad) {
    int danio = fuerza * intensidad;
    System.out.println(getNombre() + " da un golpe con intensidad " + intensidad + " causando " + danio + " de daño");
}


public void golpear(String arma) {
    System.out.println(getNombre() + " golpea con " + arma + " usando su armadura de " + armadura);
}

    @Override
    public void atacar(){
        super.atacar();
        System.out.println(getNombre() + " golpea con su espada causando " + fuerza + " de daño");
    }

    @Override
    public void defender(){
        System.out.println(getNombre() + " bloquea con su armadura de " + armadura);
    }

    @Override
    public int calcularDanio() {
        return fuerza*getNivel();
    }

    @Override
    public String toString() {
        return super.toString() + " Guerrero{" +
            "fuerza=" + fuerza +
            ", armadura='" + armadura + '\'' +
            '}';
    }
}