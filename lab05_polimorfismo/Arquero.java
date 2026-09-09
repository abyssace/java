public class Arquero extends Personaje{
    private String tipoArco;
    private int flechasDisponibles;
    private int precision;
    private int alcance;

    public Arquero(String nombre, int nivel, int puntosVida, String tipoArco, int flechasDisponibles, int precision, int alcance){
        super(nombre, nivel, puntosVida, true);
        this.tipoArco = tipoArco;
        this.flechasDisponibles = flechasDisponibles;
        this.precision = precision;
        this.alcance = alcance;
    }

    public String getTipoArco(){
        return tipoArco;
    }

    public int getFlechasDisponibles(){
        return flechasDisponibles;
    }

    public int getPrecision(){
        return precision;
    }

    public int getAlcance(){
        return alcance;
    }
    
public void disparar() {
    System.out.println(getNombre() + " dispara su " + tipoArco + " a " + alcance + " metros");
}


public void disparar(String objetivo) {
    System.out.println(getNombre() + " dispara a " + objetivo + " con precisión del " + precision + "%");
    flechasDisponibles--;
}


public void disparar(int cantidad) {
    if (flechasDisponibles >= cantidad) {
        System.out.println(getNombre() + " dispara " + cantidad + " flechas con " + tipoArco);
        flechasDisponibles -= cantidad;
    } else {
        System.out.println(getNombre() + " no tiene suficientes flechas. Solo tiene " + flechasDisponibles);
    }
}

    @Override
    public void atacar(){
        super.atacar();
        if(flechasDisponibles > 0){
            System.out.println(getNombre() + " ha disparado una flecha con precision del " + precision + "%");
            flechasDisponibles = flechasDisponibles - 1;
            System.out.println("Flechas restantes: " + flechasDisponibles);
        } else {
            System.out.println(getNombre() + " no puede disparar mas flechas");
        }
    }

    @Override
    public int calcularDanio() {
        return (precision *getNivel()) + flechasDisponibles;
    }

    @Override
    public void defender(){
        System.out.println(getNombre() + " esquiva el ataque");
    }

    @Override
    public String toString(){
        return super.toString() + " Arquero{" +
                "tipoArco='" + tipoArco + '\'' +
                ", flechasDisponibles=" + flechasDisponibles +
                ", precision=" + precision +
                ", alcance=" + alcance +
                '}';
    }
}