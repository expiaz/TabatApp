package expiaz.tabata.orm.entity;

import java.io.Serializable;

/**
 * représente une partie dans le timer, les possibles sont la préparation au début de chaque tabata,
 * le travail et le repos dans un cycle
 */
public class Part implements Serializable{

    public static final int TABATA = 1;
    public static final int WORK = 2;
    public static final int REST = 3;

    /**
     * la partie suivante à celle-ci (linked list), utile pour la vue play
     */
    private Part next;

    /**
     * nom de la partie
     */
    private String name;
    /**
     * durée de la partie
     */
    private int time;
    /**
     * type de la partie
     */
    private int type;

    public Part(int type, String name, int time){
        this.type = type;
        this.name = name;
        this.time = time;
        this.next = null;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public Part getNext(){
        return this.next;
    }

    public void setNext(Part part){
        this.next = part;
    }

    public String toString(){
        return this.getName();
    }
}
