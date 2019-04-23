package expiaz.tabata.utils;

import java.io.Serializable;
import java.util.Iterator;

import expiaz.tabata.orm.entity.Part;
import expiaz.tabata.orm.entity.Training;

public class PartIterator implements Iterator<Part>, Serializable {

    private Part cursor;

    private int time;

    public PartIterator(Training training){
        this.time = training.getTime();

        Part last = null;
        Part current;

        for(int i = 0; i < training.getTabataReps(); i++){
            current = new Part(Part.TABATA, "prepare", training.getTabataPrepare());

            // première itération
            if(last == null) {
                last = current;
                this.cursor = current;
                this.cursor.setNext(this.cursor);
            } else {
                // on link les items entre eux
                last.setNext(current);
                last = current;
            }

            for(int j = 0; j < training.getCycleReps(); j++){
                current = new Part(Part.WORK, "work", training.getCycleWork());
                last.setNext(current);
                last = current;
                current = new Part(Part.REST, "rest", training.getCycleRest());
                last.setNext(current);
                last = current;
            }
        }

    }

    public int getTotalTime() {
        return this.time;
    }

    @Override
    public boolean hasNext() {
        return this.cursor.getNext() != null;
    }

    @Override
    public Part next() {
        return this.cursor = this.cursor.getNext();
    }

    public Part current(){
        return this.cursor;
    }
}
