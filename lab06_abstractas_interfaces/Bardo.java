public class Bardo extends Personaje implements Sanador {

    private String instrumento;
    private String melodiasConocidas;

    public Bardo(String nombre, int nivel, String instrumento, String melodiasConocidas){
        super(nombre, nivel, 90);
        this.instrumento = instrumento;
        this.melodiasConocidas = melodiasConocidas;
    }
    
    @Override 
    public void atacar(){
        System.out.println(getNombre() + " ataca con su " + instrumento);
    }

    @Override
    public int calcularDanio(){
        return nivel * 8;
    }
    @Override 
    public void curarAliado(Personaje aliado){
        System.out.println(getNombre() + " cura a " + aliado.getNombre() + " con su melodía " + melodiasConocidas);
    }
    @Override 
    public int getPoderCuracion(){
        return nivel * 5;
    }
}
