public class Nigromante extends Personaje implements Hechicero {
    private String maldicion;
    private int nivelOscuridad;
    private int almasAbsorbidas;

    public Nigromante(String nombre, int nivel, String maldicion, int nivelOscuridad){
        super(nombre, nivel, 80);
        this.maldicion = maldicion;
        this.nivelOscuridad = nivelOscuridad;
    }

    @Override 
    public void atacar(){
        System.out.println(getNombre()+" ha drenado vida");
    }
    @Override 
    public int calcularDanio(){
        return (nivel * 12) +almasAbsorbidas;
    }
    @Override 
    public void lanzarHechizo(){
        System.out.println(getNombre() + " lanza un hechizo de " + maldicion + " con nivel de oscuridad " + nivelOscuridad);
    }

    @Override 
    public int getMana(){
        return nivelOscuridad * 10;
    }

}
